#!/bin/bash

# Script to download and setup llama.cpp for GemmaChat

echo "Setting up llama.cpp for GemmaChat..."

# Navigate to the cpp directory
cd app/src/main/cpp

# Clone llama.cpp repository
if [ ! -d "llama.cpp" ]; then
    echo "Cloning llama.cpp repository..."
    git clone https://github.com/ggml-org/llama.cpp.git
    cd llama.cpp
    # Checkout a stable version (you can change this to a specific commit/tag)
    git checkout master
    cd ..
else
    echo "llama.cpp directory already exists. Updating..."
    cd llama.cpp
    git pull
    cd ..
fi

echo "llama.cpp setup complete!"
echo ""
echo "Next steps:"
echo "1. Download or convert Gemma 1B model to GGUF format"
echo "2. Place the model file as: app/src/main/assets/models/gemma-1b-it-q4_k_m.gguf"
echo "3. Open the project in Android Studio"
echo "4. Build and run the application"