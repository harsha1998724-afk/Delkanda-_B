# Freshness Desk — Android APK project

This Android Studio project wraps the Café Expiry Tracker in an Android WebView and includes Android daily notification support.

## Build an APK
1. Upload this folder to a GitHub repository (or open it in Android Studio).
2. In GitHub, open **Actions** → **Build Android APK** → **Run workflow** (or push to `main`).
3. When the workflow finishes, open its run and download the **FreshnessDesk-debug-apk** artifact.
4. Extract the artifact ZIP to get `app-debug.apk`, then move it to your Android phone and install it.
5. If Android asks, allow installation from that source and grant notification permission when the app asks.

## Notes
- This is a debug APK, suitable for personal installation, not Play Store publishing.
- Stock is stored locally on the device in the app's WebView storage. Do not clear app data if you want to keep the stock list.
- Android may delay alarms due to battery restrictions. Exact-alarm access is not required by this project; reminders are scheduled with an idle-friendly alarm.
