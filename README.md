# Camp Snap Photo Importer (Snapp)

A native Android application built with **Kotlin** and **Jetpack Compose** (Material 3) designed specifically as an automated, conflict-free photo importer for **Camp Snap cameras**.

---

## 📷 Problem & Solution

Camp Snap cameras operate as USB Mass Storage devices (FAT32) when plugged into an Android phone via USB-C. Every time the camera's internal storage is emptied or formatted, its internal file numbering sequence resets back to `IMG_0001.JPG`. Transferring files manually or via general-purpose file managers leads to catastrophic filename collisions on the phone.

**Snapp solves this with a safe one-click transfer engine:**
1. **Collision-Proof Renaming**: Extracts EXIF `TAG_DATETIME_ORIGINAL`. If valid, filenames are formatted as `CS_YYYYMMDD_HHMMSS_XXX.jpg`. If the camera clock was reset (epoch 1970/2020), it falls back to `CS_<BatchTimestamp>_<Index>.jpg`.
2. **Safe Two-Phase Transfer Engine ("Copy-Verify-Prune")**:
   - **Phase 1 (Copy & Verify)**: Streams bytes into Android's `MediaStore` under `Pictures/Camp Snap` (or custom user-selected album) with `IS_PENDING = 1`. Verifies that written byte count matches the source file length before setting `IS_PENDING = 0` to publish to the gallery.
   - **Phase 2 (Pruning)**: Only after Phase 1 completes with **zero fatal errors**, securely calls `DocumentFile.delete()` on the camera to clear the storage. Failed files are **never deleted**.
3. **USB Auto-Mount Detection**: Listens for USB Mass Storage attachments (`android.hardware.usb.action.USB_DEVICE_ATTACHED`) to automatically detect the camera and prompt the user to scan.
4. **Persistent SAF Setup**: Uses `OpenDocumentTree` with `takePersistableUriPermission` and AndroidX DataStore to prompt the user once for camera root access.

---

## 🛠 Tech Stack & Architecture

- **Platform**: Android (`minSdk = 26`, `targetSdk = 34`, `compileSdk = 34`)
- **UI**: Jetpack Compose with Material 3 & custom retro-inspired camera theme
- **Concurrency**: Kotlin Coroutines & Flow
- **Persistence**: AndroidX DataStore (Preferences)
- **File & Media APIs**: Storage Access Framework (`DocumentFile`), `ExifInterface`, `MediaStore`
- **Build System**: Gradle 8.11.1 with Android Gradle Plugin 8.7.3 & Kotlin 2.0.21

---

## 📂 Key Source Files

- [`AndroidManifest.xml`](file:///Users/pietro/Builds/Snapp/app/src/main/AndroidManifest.xml): Configures media permissions (`READ_MEDIA_IMAGES`, `READ_EXTERNAL_STORAGE`) and USB device attached intent filter.
- [`CampSnapImporter.kt`](file:///Users/pietro/Builds/Snapp/app/src/main/java/com/campsnap/importer/data/CampSnapImporter.kt): Core repository & two-phase transfer engine implementing SAF directory traversal, byte verification, and safe pruning.
- [`CampSnapRenamer.kt`](file:///Users/pietro/Builds/Snapp/app/src/main/java/com/campsnap/importer/data/CampSnapRenamer.kt): EXIF timestamp extractor and collision-proof filename generator with epoch reset detection.
- [`PreferencesManager.kt`](file:///Users/pietro/Builds/Snapp/app/src/main/java/com/campsnap/importer/data/PreferencesManager.kt): DataStore manager for persistent tree URI and custom gallery album name.
- [`UsbDeviceMonitor.kt`](file:///Users/pietro/Builds/Snapp/app/src/main/java/com/campsnap/importer/data/UsbDeviceMonitor.kt): Real-time USB hardware attachment observer using `UsbManager`.
- [`ImportViewModel.kt`](file:///Users/pietro/Builds/Snapp/app/src/main/java/com/campsnap/importer/ui/ImportViewModel.kt): ViewModel orchestrating UI state flow (`Unconfigured`, `Idle`, `Scanning`, `Importing`, `Success`, `Error`).
- [`MainActivity.kt`](file:///Users/pietro/Builds/Snapp/app/src/main/java/com/campsnap/importer/MainActivity.kt): Activity entry point handling USB intent dispatching.
- [`CampSnapScreen.kt`](file:///Users/pietro/Builds/Snapp/app/src/main/java/com/campsnap/importer/ui/CampSnapScreen.kt): Clean single-screen Jetpack Compose UI.

---

## 🚀 Building & Testing

To assemble the debug APK:
```bash
./gradlew assembleDebug
```
The output APK is generated at:
`app/build/outputs/apk/debug/app-debug.apk`

To run the unit test suite:
```bash
./gradlew testDebugUnitTest
```