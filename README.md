# Concentrate 🛡️

> **Ruthless Android Deep-Work Lockdown & Proof-of-Work JEE Economy**  
> *Built with Kotlin 2.0, Jetpack Compose (Material 3), and Android System Services.*

---

## ⚡ Overview

**Concentrate** merges the distraction-free discipline of modern focus tools with the pitch-black, physics-driven aesthetic of **Blockit** and an economy built around **JEE Mains & Advanced** preparation.

### 🌟 Core Architecture

1. **The Warden (OS-Level Interceptor)**
   - Continuous background surveillance via `UsageStatsManager` and `AccessibilityService`.
   - Real-time distraction app interception without weak battery-saving workarounds.
   - **Emergency Dialer Shield**: Dynamically identifies default system dialers (`TelecomManager.defaultDialerPackage`) and emergency SOS lines to guarantee they are never blocked.
   - **Persistent Lockdown Memory**: Auto-resumes active lockdowns on device reboots (`BOOT_COMPLETED`).
   - **Notification Guillotine (Auto-DND)**: Automatically silences non-emergency notifications during active deep work.

2. **The Vault (Intense Mode)**
   - Nuclear isolation mode. Every app is blocked by default except the dialer, system settings, and your whitelisted tools.
   - **Hard 4-App Limit**: Strictly rejects selecting more than 4 approved applications.
   - **Rubber-Band Barrier**: Launches an unbreakable full-screen lock overlay if an unapproved app is opened.

3. **The Sniper (Pomodoro Mode)**
   - Targeted blacklist mode for social media and games.
   - **Silent Assassination**: Unapproved taps instantly drop the user back to the home screen without heavy overlays.
   - **Ghost Expiration**: Quietly disarms itself at `00:00`.

4. **The Market & Economy (Proof-of-Work)**
   - Points ($10\text{ P/min}$ in Vault, $5\text{ P/min}$ in Sniper) and Diamonds awarded for surviving focus sessions.
   - **Streak Multipliers**: Payout scales algebraically with consecutive days ($1.0 + 0.1 \times \text{Streak}$).
   - **Slashing Penalties**: Indiscipline or abort attempts burn $20\%$ of saved points and reset streaks.
   - **Controlled Release Passes**: Spend currency on timed micro-breaks (e.g., YouTube 15m, Instagram 10m).
   - **The Guillotine Drop**: When a pass expires, the Warden instantly terminates the app with zero grace period.
   - **Dynamic Inflation Pricing**: Pass prices dynamically inflate if the ratio of break passes to problem-solving output deteriorates.

5. **The Architect (AI Surveillance & Weakness Radar)**
   - Analyzes relapse patterns and identifies peak risk windows (e.g. 21:30 - 23:00 post-dinner slump).
   - Generates daily accountability debriefings.
   - Automatically categorizes installed apps into `HIGH_RISK_DISTRACTION`, `PRODUCTIVITY_TOOL`, and `SYSTEM_ESSENTIAL`.

6. **JEE Output Integration**
   - **3-Hour NTA Exam Simulation Slot**: 180-minute official JEE test lockdown.
   - **Subject Badges**: Tag sessions with Physics, Chemistry, Mathematics, or Mock Test.
   - **Problem Output Tracker**: Log notebook problem throughput post-session.
   - **Formula Vault**: Built-in offline formula handbook for revision during lockdown.

7. **Developer Safeguard**
   - Rapidly tap the **CONCENTRATE** header 5 times on the dashboard to open the emergency dialog and enter bypass PIN `7777` to disarm the timer during development.

---

## 🚀 Building & Running

### Option A: Android Studio
1. Clone this repository:
   ```bash
   git clone <repo-url>
   ```
2. Open the folder in **Android Studio**.
3. Allow Gradle Sync to finish (uses Gradle 8.10.2).
4. Click **Run (▶)** to install on your device or emulator.
5. In the app, navigate to **Shields** in the bottom bar to grant Usage Access, Overlay, and DND permissions.

### Option B: Command Line (Gradle)
```bash
./gradlew assembleDebug
```
The compiled APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`
