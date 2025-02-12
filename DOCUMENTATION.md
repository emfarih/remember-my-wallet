# Remember My Wallet - Documentation

## Overview
"Remember My Wallet" is a secure Android application designed to store and help users memorize their cryptocurrency seed phrases through an interactive quiz. The app ensures maximum security by preventing external communication and encrypting stored seed phrases.

## Architecture
The app follows the **MVVM (Model-View-ViewModel) architecture**, ensuring clean code, maintainability, and security.

### **Project Structure**
```
📂 remember-my-wallet
 ┣ 📂 app
 ┃ ┣ 📂 src
 ┃ ┃ ┣ 📂 main
 ┃ ┃ ┃ ┣ 📂 kotlin/com/example/remembermywallet
 ┃ ┃ ┃ ┃ ┣ 📂 ui
 ┃ ┃ ┃ ┃ ┃ ┣ 📂 seedinput        # UI for entering the seed phrase
 ┃ ┃ ┃ ┃ ┃ ┣ 📂 quizgame         # UI for the quiz game
 ┃ ┃ ┃ ┃ ┣ 📂 navigation        # Navigation setup
 ┃ ┃ ┃ ┃ ┣ 📂 data              # Data layer (encrypted storage)
 ┃ ┃ ┃ ┃ ┣ 📂 domain            # Business logic (if needed)
 ┃ ┃ ┃ ┃ ┣ 📂 viewmodel         # ViewModels for UI logic
 ┃ ┃ ┃ ┣ 📂 res
 ┃ ┃ ┃ ┃ ┣ 📂 drawable
 ┃ ┃ ┃ ┃ ┣ 📂 values
 ┃ ┃ ┃ ┣ AndroidManifest.xml
 ┃ ┃ ┣ 📂 java (if needed)
 ┃ ┣ 📂 build.gradle
 ┗ 📂 other project files...
```

## Screens and App Flow

### 1. Seed Input Screen (First-Time Setup & Reset)
- **Purpose**: Allows users to enter and securely store their **12/24-word seed phrase**.
- **Components**:
  - Secure text fields for seed phrase input.
  - Button to submit and encrypt the seed phrase.
  - Redirection to the **Quiz Game Screen** after successful storage.
  - If a seed phrase is already stored, this screen is skipped.

### 2. Quiz Game Screen (Main Screen)
- **Purpose**: Helps users memorize their seed phrase by playing a quiz game.
- **Components**:
  - Randomly selected seed words with multiple-choice answers.
  - Feedback mechanism (correct/wrong answer indication).
  - Score tracking (optional).
  - **Floating Action Button (FAB) for Reset**: Allows users to clear the stored seed and return to the **Seed Input Screen**.

## App Flow
1. **First-time users** are directed to the **Seed Input Screen** to store their phrase.
2. After storing, the app navigates to the **Quiz Game Screen**.
3. Returning users go directly to the **Quiz Game Screen** if a seed is already stored.
4. Users can reset the stored seed using the **FAB** on the **Quiz Game Screen**, which navigates back to the **Seed Input Screen**.

## Security Considerations
- **Seed phrase storage**: Securely encrypted using Android's Keystore system.
- **Network restrictions**: App prevents external communication when in use.
- **Data privacy**: No cloud storage; everything remains on-device.

## Future Enhancements
- Biometric authentication for extra security.
- More quiz difficulty levels.
- Option to export encrypted seed phrase.

---
This document provides an overview of the application's structure and security mechanisms. For implementation details, refer to the codebase.

