# Remember My Wallet - Documentation

## Overview
"Remember My Wallet" is a secure Android application designed to store and help users memorize their cryptocurrency seed phrases through an interactive quiz. The app ensures maximum security by preventing external communication and encrypting stored data.

## Architecture
The app follows the **MVVM (Model-View-ViewModel) architecture**, ensuring clean code, maintainability, and security.

### **Project Structure**
```
📂 remember-my-wallet
 ┣ 📂 app
 ┃ ┣ 📂 src
 ┃ ┃ ┣ 📂 main
 ┃ ┃ ┃ ┣ 📂 kotlin/limited/m/remembermywallet
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
- **Purpose**: Allows users to enter and securely store their **24-word seed phrase**.
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

- **Seed phrase storage**: Instead of storing the seed phrase directly, the app securely stores the **index of each word** from a shuffled BIP39 word list.
- **Shuffled word list**: The app generates a **deterministic shuffled word list** using a stored shuffle seed, ensuring that the seed words can be validated without storing them directly.
- **Network restrictions**: The app blocks all network communication using Android's `bindProcessToNetwork(null)` method. This ensures that no data can be transmitted or received, preventing any leaks.
- **Data privacy**: No cloud storage; everything remains on-device.
- **App Isolation**: External communication is blocked by restricting network access, and the app uses strict security measures to ensure privacy.

## Changes Related to Network Isolation

To ensure complete isolation of the app's network activities:

1. **Network Security Configuration**:
- The app is configured to block any external communication by using network security settings, ensuring no cleartext traffic or unauthorized network access.
- This is achieved through the `network_security_config.xml` file which disables cleartext traffic.

2. **Blocking All Network Traffic**:
- The app uses the `bindProcessToNetwork(null)` method to block all network communication for the app. This prevents the app from communicating over HTTP, TCP, UDP, or WebSockets, ensuring total isolation.

3. **Permissions**:
- The app uses permissions like `CHANGE_NETWORK_STATE` to manage and block network access.

4. **Testing Network Isolation**:
- A test function was implemented to confirm the app blocks all network requests, including HTTP, sockets, and other types of network connections. If the app attempts to make a network request, it will result in a permission denied error.

## Future Enhancements
- Biometric authentication for extra security.
- Make backspace in seed input back to previous field input.
- Create Paste Button on Seed Input Screen.
- More quiz difficulty levels.
- Option to export encrypted seed phrase.
- Improve UI/UX for a better user experience.
- Additional security checks before storing the seed phrase.

---
This document provides an overview of the application's structure, network isolation measures, and security mechanisms. For implementation details, refer to the codebase.