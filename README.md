# NamiWallet

A production-quality Android crypto wallet supporting Bitcoin, Ethereum, and BNB Smart Chain.

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Android App (Kotlin)                      │
│  ┌─────────┐  ┌──────────┐  ┌────────────┐  ┌────────────┐  │
│  │   UI    │──│ViewModel │──│ Repository │──│  Network   │  │
│  │(Compose)│  │  (MVVM)  │  │            │  │(Public RPC)│  │
│  └─────────┘  └──────────┘  └─────┬──────┘  └────────────┘  │
│                                   │                          │
│  ┌────────────────────────────────┴──────────────────────┐  │
│  │                 NamiCore (Crypto Engine)               │  │
│  │         web3j (ETH/BSC) + bitcoinj (Bitcoin)          │  │
│  └───────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
```

## Features

- **Multi-chain support**: Bitcoin, Ethereum, and BNB Smart Chain
- **Self-custody**: Your keys stay on your device
- **BIP-39/BIP-44 compliant**: Industry-standard key derivation
- **Secure storage**: Encrypted with Android Keystore
- **Biometric authentication**: Optional fingerprint/face unlock
- **Pure Kotlin**: No native code required

## Prerequisites

- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34

## Building

```bash
./gradlew assembleDebug
```

## Project Structure

```
NamiWallet/
├── app/
│   ├── src/main/java/com/namiwallet/
│   │   ├── bridge/           # Crypto engine (web3j + bitcoinj)
│   │   ├── di/               # Hilt modules
│   │   ├── network/          # RPC clients
│   │   ├── repository/       # Data layer
│   │   ├── security/         # Encryption, biometrics
│   │   ├── ui/               # Compose UI
│   │   └── viewmodel/        # MVVM ViewModels
│   └── build.gradle.kts
└── README.md
```

## Security

- Seed phrases are encrypted with Android Keystore master key
- No sensitive data is logged
- Backup to cloud is disabled

## Supported Networks

| Network | Chain ID | RPC Endpoint |
|---------|----------|--------------|
| Ethereum Mainnet | 1 | https://eth.llamarpc.com |
| BNB Smart Chain | 56 | https://bsc-dataseed.binance.org |
| Bitcoin | - | Electrum protocol |

## Libraries Used

- **web3j**: Ethereum/BSC transactions and key management
- **bitcoinj**: Bitcoin transactions and key management
- **Jetpack Compose**: Modern Android UI
- **Hilt**: Dependency injection
- **DataStore**: Encrypted preferences

## License

MIT License
