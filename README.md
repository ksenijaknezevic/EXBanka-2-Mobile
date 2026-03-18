# EXBanka — Mobile Verification App

Android application for confirming bank transactions via a one-time verification code.

---

## What is this?

When a user initiates a payment on the **laptop/web browser**, they must confirm it on their **mobile phone**. This app handles that mobile side of the flow:

```
Laptop (web)                      Mobile app
─────────────────                 ─────────────────────────
1. Create payment         ──►     2. Open app, see pending tx
                                  3. Click "Approve Transaction"
                                  4. Receive 6-digit code
5. Enter code on laptop   ◄──     (code valid 5 min)
6. Payment confirmed
   (3 wrong attempts → cancelled)
```

---

## Folder structure

```
EXBanka-2-Mobile/
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/rs/raf/exbanka/mobile/
│       │   │   ├── BankaApplication.kt        # Hilt entry point
│       │   │   ├── MainActivity.kt
│       │   │   ├── data/
│       │   │   │   ├── local/SessionDataStore.kt     # JWT token storage
│       │   │   │   ├── remote/
│       │   │   │   │   ├── api/AuthApi.kt            # POST /login
│       │   │   │   │   ├── api/TransactionApi.kt     # transaction endpoints*
│       │   │   │   │   ├── dto/                      # Gson DTOs
│       │   │   │   │   └── interceptor/AuthInterceptor.kt
│       │   │   │   └── repository/
│       │   │   │       ├── AuthRepositoryImpl.kt
│       │   │   │       ├── TransactionRepositoryImpl.kt   # real API
│       │   │   │       └── MockTransactionRepositoryImpl.kt  # demo data
│       │   │   ├── di/
│       │   │   │   ├── NetworkModule.kt    # Retrofit + OkHttp
│       │   │   │   └── RepositoryModule.kt # binds mock or real repo
│       │   │   ├── domain/
│       │   │   │   ├── model/              # Transaction, VerificationCode
│       │   │   │   └── repository/         # interfaces
│       │   │   ├── ui/
│       │   │   │   ├── navigation/         # NavGraph + Screen sealed class
│       │   │   │   ├── screens/login/
│       │   │   │   ├── screens/transactions/
│       │   │   │   ├── screens/transactiondetail/
│       │   │   │   ├── screens/verificationcode/
│       │   │   │   ├── components/         # TransactionCard, LoadingView, ErrorView
│       │   │   │   └── theme/              # Material 3 colors + typography
│       │   │   └── util/NetworkResult.kt   # Loading / Success / Error
│       │   └── res/xml/network_security_config.xml
│       └── test/                           # ViewModel + repository unit tests
├── Dockerfile                              # Android APK build container
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

---

## Dependencies

| Library | Version | Purpose |
|---|---|---|
| Kotlin | 1.9.23 | Language |
| AGP | 8.4.0 | Android Gradle Plugin |
| Compose BOM | 2024.04.01 | All Compose libraries |
| Material 3 | (BOM) | UI components |
| Navigation Compose | 2.7.7 | Screen navigation |
| Lifecycle / ViewModel | 2.7.0 | State management |
| Hilt | 2.51 | Dependency injection |
| KSP | 1.9.23-1.0.20 | Annotation processing |
| Retrofit | 2.11.0 | HTTP client |
| OkHttp | 4.12.0 | HTTP transport + logging |
| DataStore Preferences | 1.1.1 | Token persistence |
| Coroutines | 1.8.0 | Async + Flow |

---

## How to open in Android Studio

1. Open Android Studio (Hedgehog 2023.1.1+).
2. **File → Open** → select the `EXBanka-2-Mobile` folder.
3. Gradle syncs automatically (downloads deps ~2–5 min on first run).

> **Note on `gradle-wrapper.jar`**: Android Studio regenerates it automatically.
> For CLI use: `gradle wrapper --gradle-version 8.6` inside `EXBanka-2-Mobile/`.

---

## How to run the app

### Android Emulator (recommended)

1. **Tools → Device Manager** → Create virtual device (Pixel 7, API 34).
2. Press **▶ Run** (`Shift+F10`).

### Physical device

1. Enable Developer Options + USB Debugging on phone.
2. Connect via USB, select device in toolbar, press **▶ Run**.

---

## Emulator connects to backend

The Android emulator loopback to the host machine:

| Client | Address to use | Reaches |
|---|---|---|
| Android Emulator | `10.0.2.2` | `localhost` on your laptop |
| Genymotion | `10.0.3.2` | `localhost` on your laptop |
| Physical device | Laptop LAN IP (e.g. `192.168.1.x`) | Laptop |

Backend user-service is exposed on **port 8082** by Docker Compose.
Default `BASE_URL` in `app/build.gradle.kts`:

```kotlin
buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8082/\"")
```

---

## How to change BASE_URL

Edit `app/build.gradle.kts` → `defaultConfig` block, then **Sync Project with Gradle Files**.

```kotlin
// Emulator (default)
buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8082/\"")

// Physical device on same WiFi
// buildConfigField("String", "BASE_URL", "\"http://192.168.1.100:8082/\"")

// Production
// buildConfigField("String", "BASE_URL", "\"https://api.exbanka.rs/\"")
```

---

## Mock mode vs real API

`USE_MOCK_API = true` (default in debug) → uses `MockTransactionRepositoryImpl`
No backend required; fake transactions with random verification codes.

To switch to the real API:
1. Set `USE_MOCK_API = false` in `build.gradle.kts`
2. In `di/RepositoryModule.kt` change:
   ```kotlin
   // from:
   abstract fun bindTransactionRepository(impl: MockTransactionRepositoryImpl): TransactionRepository
   // to:
   abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository
   ```
3. Make sure the backend transaction endpoints are running.

### Assumed transaction endpoints (not yet in backend)

| Method | Path | Description |
|---|---|---|
| `GET` | `/transactions/pending` | Pending transactions list |
| `GET` | `/transactions/{id}` | Single transaction details |
| `POST` | `/transactions/{id}/approve` | Approve → returns 6-digit code |

Approve response shape:
```json
{
  "verification_code": "847293",
  "expires_at": "2024-01-15T10:35:00Z",
  "expires_in_seconds": 300
}
```

---

## Build APK via Docker

Docker builds the APK in a reproducible environment. The app still runs on a device/emulator.

```bash
# From EXBanka-2-Infrastructure/
docker compose --profile mobile run --rm mobile-build
# APK → EXBanka-2-Mobile/output/exbanka-verification-debug.apk

# Install on running emulator or connected device
adb install EXBanka-2-Mobile/output/exbanka-verification-debug.apk
```

Or directly:
```bash
cd EXBanka-2-Mobile
docker build -t exbanka-mobile-build .
docker run --rm -v $(pwd)/output:/host-output exbanka-mobile-build
```

### Why Android apps don't run inside Docker

Android apps require the Android Runtime (ART) which only exists on Android OS.
Docker containers run on the Linux kernel — there is no ART there.
The Docker container here is a **build tool only** (like a CI server).
Running the app requires an Android emulator (QEMU-based, managed by Android Studio) or a physical device.

---

## Running unit tests

```bash
./gradlew test
# or
./gradlew testDebugUnitTest
```

Test classes:
- `LoginViewModelTest` — email/password validation, success and error states
- `MockTransactionRepositoryTest` — mock data shape, 6-digit code, approve logic
- `VerificationCodeViewModelTest` — countdown timer, expiry after 0 seconds, MM:SS format

---

## Verification code flow — detailed

```
1. User opens web app on laptop → creates a payment (PENDING)
2. User opens mobile app → sees pending transaction
3. User taps the transaction → sees: recipient, amount, purpose, date
4. User taps "Approve Transaction"
   → POST /transactions/{id}/approve
   → Backend generates a 6-digit code with 5-minute TTL
5. Mobile shows the code prominently with a live countdown timer
6. Mobile shows: "Enter this code on your laptop"
7. User types the code on the laptop:
   - Correct  → transaction APPROVED ✓
   - Wrong (attempt 1 or 2) → error on laptop, code still valid
   - Wrong (attempt 3) → transaction CANCELLED automatically
8. Code expires after 5 minutes regardless of attempts
```
