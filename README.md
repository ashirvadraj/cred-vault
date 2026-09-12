# 💳 CRED Vault - Credit Card Bill & Due Tracker (Android APK)

An open, 100% on-device, luxury CRED-styled Android application to manage credit cards, track statement generation, automatically extract bill due dates and amounts from SMS & Gmail, and trigger multi-tier reminder alerts before due dates.

---

## 📲 Direct APK Download

| File | Size | Type | Link |
| :--- | :--- | :--- | :--- |
| **CRED Vault (Signed Release)** | **27.6 MB** | Recommended | [📥 **Download `CRED-Vault-v1.0.0.apk`**](https://github.com/ashirvadraj/cred-vault/raw/main/apk/CRED-Vault-v1.0.0.apk) |
| **GitHub Release Asset** | **27.6 MB** | Release Page | [🏷️ **View Release v1.0.0**](https://github.com/ashirvadraj/cred-vault/releases/tag/v1.0.0) |

> [!TIP]
> **Installation Note**: When installing, if Google Play Protect shows a prompt, tap **"More details"** and then **"Install anyway"** (standard for personal open-source APKs).

---

## ✨ Key Features

1. **CRED-Style Obsidian & Neon UI**:
   - 3D tactile credit card carousel with metallic chip graphics, bank logos, and glowing border gradients.
   - Total Upcoming Dues hero counter with real-time balance aggregations.
   - Credit Utilization Gauge (calculates utilization percentage vs healthy 30% limit).
2. **Dual Automated Ingestion**:
   - **SMS Parser Engine**: Scans incoming & historical SMS from major Indian and global banks (HDFC, SBI, ICICI, Axis, Kotak, Amex, OneCard, IndusInd, RBL, SC, HSBC).
   - **Gmail E-Statement Sync**: Connects via OAuth2 (`gmail.readonly`) to parse billing emails & e-statements.
3. **Smart Due Date Alerts**:
   - Notifications scheduled at T-7 days, T-3 days, T-1 day, and on Due Date 9:00 AM.
   - Interactive notification buttons: **"Pay via UPI"** and **"Mark as Paid"**.
4. **One-Tap UPI Payment**:
   - Directly launches Google Pay, PhonePe, Paytm, CRED, or BHIM with pre-configured bank biller VPA and exact due amounts.
5. **100% On-Device Privacy & Biometrics**:
   - Encrypted local Room Database (zero cloud storage or data tracking).
   - Biometric fingerprint / face authentication lock.

---

## 🏗️ Building the APK

### Prerequisites:
- Android Studio / Android SDK (API 34)
- JDK 17+ (Eclipse Adoptium or OpenJDK)

### Build Debug APK:
```bash
./gradlew assembleDebug
```
The generated APK will be at:
`app/build/outputs/apk/debug/app-debug.apk`

### Build Release APK:
```bash
./gradlew assembleRelease
```

---

## 🧪 Testing Parser Engine

Run the unit test suite covering real bank SMS templates:
```bash
./gradlew test
```

---

## 📱 Granting Permissions via ADB (for Testing/Personal Sideload)

```bash
# Grant SMS read & receive permissions
adb shell pm grant com.credtracker android.permission.READ_SMS
adb shell pm grant com.credtracker android.permission.RECEIVE_SMS

# Grant Notification permission (Android 13+)
adb shell pm grant com.credtracker android.permission.POST_NOTIFICATIONS
```

---

## 📂 Project Structure

- `com.credtracker.data`: Room DB, DAOs, and Entities (`CreditCard`, `BillStatement`, `Transaction`).
- `com.credtracker.parser`: Bank regex patterns, date format normalizers, SMS & Gmail parser engines.
- `com.credtracker.receiver`: `SmsBroadcastReceiver`, `ReminderAlarmReceiver`, `BootReceiver`.
- `com.credtracker.worker`: WorkManager `BillSyncWorker` for periodic historical inbox checks.
- `com.credtracker.ui`: Jetpack Compose dark luxury theme, custom 3D card widgets, and screen navigation.
- `com.credtracker.util`: `NotificationHelper`, `UpiPaymentHelper`, `BiometricHelper`.
