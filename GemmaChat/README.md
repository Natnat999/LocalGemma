# GemmaChat - Assistant IA Local pour Android

## Description

GemmaChat est une application Android native qui intègre le modèle Gemma 1B pour fournir un assistant IA conversationnel fonctionnant entièrement hors ligne sur l'appareil. L'application offre une interface moderne basée sur Material Design 3 avec support des thèmes clairs et sombres.

## Fonctionnalités principales

- 🤖 **IA locale** : Exécution du modèle Gemma 1B directement sur l'appareil via llama.cpp
- 💬 **Interface de chat temps réel** : Conversation fluide avec l'assistant IA
- 🎨 **Design Material 3** : Interface moderne avec support des thèmes dynamiques (Android 12+)
- 🌐 **Multilingue** : Support du français et de l'anglais
- 📱 **Compatibilité étendue** : Fonctionne sur Android 8.0 (API 26) et versions ultérieures
- 🔒 **Confidentialité** : Aucune donnée n'est transmise en ligne
- 💾 **Historique local** : Sauvegarde des conversations avec Room
- 🔔 **Notifications** : Alertes locales pour les réponses en arrière-plan

## Architecture technique

### Technologies utilisées

- **Langage** : Kotlin
- **UI** : Jetpack Compose avec Material 3
- **Architecture** : MVVM avec ViewModel et StateFlow
- **Base de données** : Room pour la persistance locale
- **Préférences** : DataStore
- **IA** : llama.cpp via JNI pour l'exécution du modèle
- **Injection de dépendances** : Singleton pattern manuel

### Structure du projet

```
GemmaChat/
├── app/
│   ├── src/main/
│   │   ├── java/com/gemmachat/
│   │   │   ├── data/              # Modèles de données et repository
│   │   │   ├── llm/               # Interface JNI pour llama.cpp
│   │   │   ├── ui/                # Composants UI et écrans
│   │   │   ├── utils/             # Utilitaires
│   │   │   ├── viewmodels/        # ViewModels
│   │   │   └── MainActivity.kt
│   │   ├── cpp/                   # Code natif C++ pour llama.cpp
│   │   └── res/                   # Ressources (layouts, strings, etc.)
│   └── build.gradle.kts
└── README.md
```

## Installation et compilation

### Prérequis

- Android Studio Arctic Fox ou plus récent
- Android SDK 34
- NDK 25.x ou plus récent
- CMake 3.22.1

### Étapes de compilation

1. Cloner le dépôt llama.cpp dans `app/src/main/cpp/llama.cpp`
2. Placer le modèle Gemma 1B au format GGUF dans `app/src/main/assets/models/`
3. Ouvrir le projet dans Android Studio
4. Synchroniser le projet avec Gradle
5. Compiler et exécuter sur un appareil ou émulateur

### Configuration du modèle

Le modèle Gemma 1B doit être converti au format GGUF. Utilisez les outils de llama.cpp pour la conversion :

```bash
python convert.py gemma-1b-it --outtype q4_k_m --outfile gemma-1b-it-q4_k_m.gguf
```

## Optimisations

- **Quantification** : Le modèle utilise la quantification Q4_K_M pour réduire la taille et améliorer les performances
- **Threading** : Utilisation de 4 threads pour l'inférence
- **Contexte** : Limité à 2048 tokens pour optimiser la mémoire
- **Batch size** : 512 pour un équilibre entre vitesse et utilisation mémoire

## Limitations connues

- Le modèle nécessite environ 1-2 GB de RAM disponible
- Les performances peuvent varier selon l'appareil
- La première génération peut être plus lente (chargement du modèle)

## Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.

## Contributions

Les contributions sont les bienvenues ! Veuillez créer une issue pour discuter des changements majeurs avant de soumettre une pull request.