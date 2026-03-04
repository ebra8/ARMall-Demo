# AR Mall Demo

## Overview
**AR Mall Demo** is an Android application developed using **Kotlin**, **Jetpack Compose**, and **ARCore (via Sceneview)**. The project demonstrates how Augmented Reality can be used to navigate complex indoor spaces, such as shopping malls, by recognizing store logos/entrances and displaying virtual wayfinding elements.

## Features
- **Logo Detection (Augmented Images)**: Uses ARCore to recognize physical reference images (store entrances/logos) from the camera feed based on a pre-loaded offline image database.
- **Robust Tracking**: Implements logic to differentiate between "Full Tracking" (actively looking at the image) and ARCore's cached tracking, ensuring the UI accurately reflects what the camera currently sees.
- **Asynchronous Database Loading**: Reads and processes the AR image database on background threads (`Dispatchers.IO`) during app launch. This guarantees smooth performance and prevents "Application Not Responding" (ANR) errors.
- **Modern Jetpack Compose UI**: Provides a clean, dynamic user interface that updates real-time tracking status.

## Technical Architecture
### Jetpack Compose
The UI is built exclusively using Jetpack Compose, emphasizing a reactive programming model.
- **`MainActivity.kt`**: Entry point that manages the permission flow (`PermissionsScreen`) and launches the main AR experience (`ARNavigationScreen`).
- **`ARNavigationScreen.kt`**: Contains the AR viewport using Sceneview and state-driven Compose widgets that overlay instructional text and recognized store names.

### ARCore Integration
- **`ARCoreUtils.kt`**: Handles the offline image database setup. It loads the highly optimized `mall_database.imgdb` precompiled database from the `assets/` directory. This single database seamlessly tracks 174 different store entrances without increasing app loading times or RAM usage.
- **Real-time Engine**: Sceneview wraps the ARCore session. Features automatic camera focus (`Config.FocusMode.AUTO`) and per-frame evaluations mapping physical tracking points back to Compose state updates. Strings are dynamically formatted (e.g. `pull_bear_1` -> `Pull Bear`) for clean UI display.

## Setup and Building
1. Clone this repository and open the project in **Android Studio** (Koala or newer recommended).
2. Minimum SDK: **API 26 (Android 8.0)** or higher.
3. Ensure you have **Google Play Services for AR** installed on your Android test device.
4. Click **Run > Run 'app'** to launch.
5. Grant Camera permissions.
6. Point the device camera at any of the store logos recognized by the database.

## Future Work (Navigation Expansion)
The underlying architecture is built to support the future placement of 3D navigational arrows. By storing recognized images in the `trackedImages` `Set`, the foundation is laid out seamlessly for attaching recursive `ArNode` models to the physical anchors.
