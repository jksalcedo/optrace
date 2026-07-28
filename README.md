# OpTrace

A persistent, locally-stored permission audit logger for Android. 

Android's built-in Privacy Dashboard is great, but it wipes your history after 7 days and heavily focuses on just Camera, Microphone, and Location. OpTrace leverages privileged shell access to hook directly into Android's hidden `AppOpsManager`, providing a complete, persistent timeline of exactly what your installed applications are accessing in the background.

## Features

* **Persistent Logging:** Stores permission access history locally in a Room database, bypassing the system's 7-day deletion limit.
* **Comprehensive Tracking:** Monitors all AppOps, including accesses to Contacts, Call Logs, Clipboard, Nearby Devices, Camera, Microphone, and Location.
* **Organized Timeline UI:** Modern Jetpack Compose (Material 3) interface with Date/App grouping, category filters, real-time search, and app icon resolution.
* **Background Auditing:** Periodically polls AppOps state changes in the background using Android WorkManager.
* **Privacy First:** Fully open-source, entirely offline, and stores data strictly on your device.

## How it Works

Standard Android apps cannot access elevated permission auditing APIs without root orADB-level shell privileges. OpTrace executes privileged `appops` dump commands (`cmd appops dump` / `dumpsys appops`) to capture full system snapshot diffs over time.

Privilege Methods:
1. **Root (Magisk / KernelSU / APatch / su):** Executes privileged shell queries directly on-device.
2. **Shizuku:** ADB-level privilege integration *(In Progress)*.

## Tech Stack

* **Language:** Kotlin
* **UI:** Jetpack Compose (Material 3) with Adaptive Navigation Suite
* **Database:** Room (SQLite)
* **Background Work:** WorkManager
* **Privilege Escalation:** Root Shell / Shizuku API

## Requirements

* Android 7.0+ (API 24+)
* Rooted device (Magisk, KernelSU, APatch, etc.) OR [Shizuku](https://shizuku.rikka.app/) *(planned)*.

## License

Distributed under the GNU General Public License v3.0 (GPL-3.0). See `LICENSE` for details.
