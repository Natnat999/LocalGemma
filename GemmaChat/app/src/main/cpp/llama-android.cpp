#include <jni.h>
#include <string>
#include <thread>
#include <mutex>
#include <android/log.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

#include "llama.h"
#include "common.h"

#define LOG_TAG "LlamaAndroid"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static llama_model* g_model = nullptr;
static llama_context* g_ctx = nullptr;
static std::mutex g_mutex;

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_gemmachat_llm_LlamaJNI_loadModel(JNIEnv *env, jclass /* clazz */, jstring jmodelPath) {
    const char* modelPath = env->GetStringUTFChars(jmodelPath, nullptr);
    
    llama_model_params model_params = llama_model_default_params();
    model_params.n_gpu_layers = 0; // CPU only for compatibility
    
    g_model = llama_load_model_from_file(modelPath, model_params);
    env->ReleaseStringUTFChars(jmodelPath, modelPath);
    
    if (!g_model) {
        LOGE("Failed to load model");
        return 0;
    }
    
    llama_context_params ctx_params = llama_context_default_params();
    ctx_params.n_ctx = 2048;
    ctx_params.n_batch = 512;
    ctx_params.n_threads = 4;
    
    g_ctx = llama_new_context_with_model(g_model, ctx_params);
    
    if (!g_ctx) {
        LOGE("Failed to create context");
        llama_free_model(g_model);
        g_model = nullptr;
        return 0;
    }
    
    LOGI("Model loaded successfully");
    return reinterpret_cast<jlong>(g_ctx);
}

JNIEXPORT void JNICALL
Java_com_gemmachat_llm_LlamaJNI_freeModel(JNIEnv* /* env */, jclass /* clazz */) {
    std::lock_guard<std::mutex> lock(g_mutex);
    
    if (g_ctx) {
        llama_free(g_ctx);
        g_ctx = nullptr;
    }
    
    if (g_model) {
        llama_free_model(g_model);
        g_model = nullptr;
    }
}

JNIEXPORT jstring JNICALL
Java_com_gemmachat_llm_LlamaJNI_generateResponse(JNIEnv *env, jclass /* clazz */, jstring jprompt, jint maxTokens) {
    if (!g_ctx || !g_model) {
        return env->NewStringUTF("Error: Model not loaded");
    }
    
    const char* prompt = env->GetStringUTFChars(jprompt, nullptr);
    std::string response;
    
    // Tokenize input
    std::vector<llama_token> tokens_list;
    tokens_list = ::llama_tokenize(g_ctx, prompt, true);
    
    env->ReleaseStringUTFChars(jprompt, prompt);
    
    const int n_ctx = llama_n_ctx(g_ctx);
    const int n_kv_req = tokens_list.size() + maxTokens;
    
    if (n_kv_req > n_ctx) {
        return env->NewStringUTF("Error: Context length exceeded");
    }
    
    // Process tokens
    for (size_t i = 0; i < tokens_list.size(); i++) {
        if (llama_decode(g_ctx, llama_batch_get_one(&tokens_list[i], 1, i, 0))) {
            return env->NewStringUTF("Error: Failed to decode");
        }
    }
    
    // Generate response
    int n_cur = tokens_list.size();
    int n_len = maxTokens;
    
    std::vector<llama_token> candidates;
    candidates.reserve(llama_n_vocab(g_model));
    
    while (n_cur < n_len) {
        auto logits = llama_get_logits_ith(g_ctx, -1);
        
        candidates.clear();
        for (llama_token token_id = 0; token_id < llama_n_vocab(g_model); token_id++) {
            candidates.emplace_back(llama_token_data{token_id, logits[token_id], 0.0f});
        }
        
        llama_token_data_array candidates_p = { candidates.data(), candidates.size(), false };
        
        // Sample token
        const llama_token new_token_id = llama_sample_token_greedy(g_ctx, &candidates_p);
        
        // Check for end of generation
        if (new_token_id == llama_token_eos(g_model) || n_cur >= n_len) {
            break;
        }
        
        response += llama_token_to_piece(g_ctx, new_token_id);
        
        // Prepare next iteration
        if (llama_decode(g_ctx, llama_batch_get_one(&new_token_id, 1, n_cur, 0))) {
            break;
        }
        
        n_cur++;
    }
    
    return env->NewStringUTF(response.c_str());
}

JNIEXPORT void JNICALL
Java_com_gemmachat_llm_LlamaJNI_resetContext(JNIEnv* /* env */, jclass /* clazz */) {
    if (g_ctx) {
        llama_kv_cache_clear(g_ctx);
    }
}

} // extern "C"