# Guide d'installation de GemmaChat

## Prérequis

### Outils de développement
- **Android Studio** : Arctic Fox (2020.3.1) ou plus récent
- **JDK** : Version 11 ou supérieure
- **Android SDK** : API Level 34
- **Android NDK** : Version 25.x ou supérieure
- **CMake** : Version 3.22.1

### Matériel requis
- **RAM minimum** : 4 GB (8 GB recommandé)
- **Espace disque** : 10 GB minimum pour l'environnement de développement
- **Appareil Android** : Android 8.0 (API 26) ou supérieur avec au moins 2 GB de RAM disponible

## Étapes d'installation

### 1. Cloner le projet

```bash
git clone [URL_DU_REPO]
cd GemmaChat
```

### 2. Configurer llama.cpp

Exécutez le script de configuration :

```bash
./setup_llama_cpp.sh
```

Ce script va :
- Cloner le dépôt llama.cpp
- Le placer dans le bon répertoire
- Configurer les dépendances

### 3. Obtenir le modèle Gemma 1B

#### Option A : Télécharger un modèle pré-converti
1. Recherchez "Gemma 1B GGUF" sur Hugging Face
2. Téléchargez la version quantifiée Q4_K_M
3. Renommez le fichier en `gemma-1b-it-q4_k_m.gguf`

#### Option B : Convertir le modèle vous-même
```bash
# Installer les dépendances Python
pip install -r llama.cpp/requirements.txt

# Télécharger le modèle original
# (suivre les instructions sur le site de Google)

# Convertir au format GGUF
python llama.cpp/convert.py path/to/gemma-1b-it \
    --outtype q4_k_m \
    --outfile gemma-1b-it-q4_k_m.gguf
```

### 4. Placer le modèle

Créez le dossier des modèles et placez-y le fichier :

```bash
mkdir -p app/src/main/assets/models
cp gemma-1b-it-q4_k_m.gguf app/src/main/assets/models/
```

### 5. Configurer Android Studio

1. Ouvrez Android Studio
2. Sélectionnez "Open an existing project"
3. Naviguez vers le dossier GemmaChat
4. Attendez la synchronisation Gradle

### 6. Configurer le SDK et NDK

Dans Android Studio :
1. File → Project Structure
2. SDK Location :
   - Android SDK Location : Vérifier le chemin
   - Android NDK Location : Installer si nécessaire

### 7. Compiler le projet

#### Via Android Studio
1. Build → Make Project
2. Attendre la fin de la compilation

#### Via ligne de commande
```bash
./gradlew assembleDebug
```

### 8. Exécuter l'application

#### Sur émulateur
1. Tools → AVD Manager
2. Créer un émulateur avec au moins 4 GB de RAM
3. Run → Run 'app'

#### Sur appareil physique
1. Activer le mode développeur sur l'appareil
2. Activer le débogage USB
3. Connecter l'appareil via USB
4. Run → Run 'app'

## Résolution des problèmes

### Erreur : "NDK not configured"
```bash
# Dans local.properties, ajouter :
ndk.dir=/path/to/android-ndk
```

### Erreur : "CMake not found"
- Installer CMake via SDK Manager dans Android Studio

### Erreur : "Model file not found"
- Vérifier que le fichier GGUF est bien dans `app/src/main/assets/models/`
- Vérifier le nom exact du fichier

### Performances lentes
- Utiliser un modèle plus petit (Q4_0 au lieu de Q4_K_M)
- Réduire le contexte dans LlamaManager.kt
- Désactiver les animations dans les paramètres développeur

## Configuration avancée

### Optimisation pour votre appareil

Dans `LlamaManager.kt`, ajustez ces paramètres :

```kotlin
companion object {
    private const val MODEL_FILENAME = "gemma-1b-it-q4_k_m.gguf"
    private const val MAX_TOKENS = 512  // Réduire pour plus de vitesse
    private const val N_THREADS = 4    // Ajuster selon votre CPU
    private const val N_CTX = 2048     // Réduire pour moins de RAM
}
```

### Support GPU (expérimental)

Pour activer l'accélération GPU :

1. Dans `llama-android.cpp` :
```cpp
model_params.n_gpu_layers = 10; // Au lieu de 0
```

2. Recompiler l'application

## Vérification de l'installation

L'installation est réussie si :
- ✅ L'application se lance sans crash
- ✅ Le message de bienvenue s'affiche
- ✅ Vous pouvez envoyer un message
- ✅ L'IA répond dans un délai raisonnable

## Support

En cas de problème :
1. Vérifier les logs dans Android Studio (Logcat)
2. Consulter la documentation de llama.cpp
3. Ouvrir une issue sur le dépôt du projet