# OpTrace

A persistent, locally-stored permission audit logger for Android. 

Android's built-in Privacy Dashboard is great, but it wipes your history after 7 days and heavily focuses on just the Camera, Microphone, and Location. OpTrace leverages Shizuku (or Root) to hook directly into Android's hidden `AppOpsManager`, providing a complete, persistent timeline of exactly what your installed applications are accessing in the background.

## Features ( MVP)

* **Persistent Logging:** Stores permission access history locally in a Room database, bypassing the system's 7-day deletion limit.
* **Comprehensive Tracking:** Monitors all AppOps, including silent accesses to Contacts, Call Logs, Clipboard, and nearby devices.
* **Clean Timeline UI:** A modern, Jetpack Compose interface to filter and view access events per application or per permission.
* **Privacy First:** Fully open-source, entirely offline, and stores data only on your device.

## How it Works

Standard Android apps cannot access the `WATCH_APPOPS` permission. To bypass this sandboxing, OpTrace requires elevated privileges to run a headless monitoring service.

You can grant these privileges via two methods:
1.  **Shizuku (Recommended):** Uses wireless ADB to grant shell-level access without rooting your device.
2.  **Root (Magisk/KernelSU/APatch):** Directly launches the monitoring service as the root user.

## Tech Stack

* **Language:** Kotlin
* **UI:** Jetpack Compose (Material 3)
* **Database:** Room (SQLite)
* **IPC / Privilege Escalation:** Shizuku API

## Installation

*F-Droid and GitHub release links coming soon.*

**Requirements:**
* Android 11+ 
* [Shizuku](https://shizuku.rikka.app/) installed and running, OR a rooted device.
