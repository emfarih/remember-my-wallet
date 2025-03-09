# Remember My Wallet

A secure Android application to help users remember their wallet seed phrases through a quiz-based approach.

## Features

- **Offline-Only Mode**: The app prevents all external communication, ensuring complete privacy.
- **Network Lockdown**: Disables all external communication when the app is running, preventing any form of data leakage. The app continuously monitors and re-blocks any accidental network reconnection. If any connection is detected, a real-time alert warns the user.
- **Secure Seed Storage**: Instead of storing the actual seed phrase, the app securely stores the index of the shuffled wordlist for added security.
- **Shuffled Wordlist**: The 24-word seed phrase is mapped to a shuffled version of the BIP39 wordlist, preventing direct exposure of the original phrase.
- **Memory Quiz Game**: A game to test and reinforce the user's memory of the seed phrase by dynamically generating questions based on shuffled word indices.
- **Seed Reset Option**: Users can reset their stored seed phrase and re-enter a new one, triggering a new shuffle.
- **High Security**: Encryption and security best practices are implemented to protect stored data.

## Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/emfarih/remember-my-wallet.git
   ```
2. Open the project in Android Studio.
3. Build and run the app on an Android device or emulator.

## Usage

1. Open the app.
2. Enter and securely store your 24-word seed phrase.
3. The app will shuffle the BIP39 wordlist and store only the indices corresponding to your seed phrase.
4. Play the memory quiz game to reinforce your knowledge.
5. Reset the stored phrase anytime if needed, which will reshuffle the wordlist.

## Security Measures

- The app operates in **offline mode** to ensure no data is leaked.
- **Encryption techniques** are used to securely store shuffled word indices instead of the seed phrase itself.
- **No logs, no cloud, no backup** – all data remains on the user's device.
- **Network Lockdown**: The app **blocks all external network requests** (including HTTP, sockets, and other protocols) when in use, ensuring no data transmission to external servers.
- **No external services**: The app does not connect to any external servers or third-party services, maintaining complete privacy.

## License

MIT License

---

> **Warning:** This app is designed to help you memorize your seed phrase. Do not use it as your sole method of storage. Always keep a physical backup in a secure place.