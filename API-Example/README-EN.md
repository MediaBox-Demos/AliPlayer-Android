Language: [中文简体](README.md) | English

# **API-Example (Android)**

ApsaraVideo Player SDK Android Sample Project

## **📖 Project Overview**

This project is an Android sample project for the ApsaraVideo Player SDK, written in Java, designed to help developers quickly understand and integrate the core functionalities provided by the SDK.

The project adopts a **modular architecture design**, supports schema-based routing navigation, and offers excellent **extensibility** and **maintainability**.

## **✨ Feature Highlights**

### **🎯 Single-Function Demonstration Design**

- **Focus on single feature** – Each module demonstrates only one core function, making it easy to understand and verify.
- **Minimal code implementation** – Only core logic is retained, eliminating redundant code and improving example readability.
- **Unified access entry** – Supports schema-based navigation between modules, enabling decoupled module communication.

### **🔧 Technical Features**

- **Modular architecture** – Each functional module is independently encapsulated for easier management and reuse.
- **Non-invasive routing mechanism** – Implements schema navigation based on Android Intent, without requiring additional frameworks.
- **Material Design style** – Follows Material Design guidelines to ensure visual consistency.
- **Internationalization support** – Supports automatic switching between Chinese and English for multi-language environments.
- **Configuration-driven** – Supports both XML and code-based configuration methods.

## **🏗️ Project Architecture**

This project adopts a modular organizational approach, with a clear structure and easy scalability:

| Module                  | Description                          | Main Functions                                     | Entry File                  |
|-------------------------| ------------------------------------ |----------------------------------------------------|-----------------------------|
| **App**                 | Main application module              | Application entry, feature navigation, menu        | MainActivity                |
| **Common**              | Common base module                   | Constants, utility classes                         | -                           |
| **BasicPlayback**       | Single-Function demonstration module | Basic video playback                               | BasicPlaybackActivity       |
| **BasicLiveStream**     | Single-Function demonstration module | Basic livestream video playback                    | BasicLiveStreamActivity     |
| **PlaybackSurfaceView** | Single-Function demonstration module | Basic video playback using SurfaceView             | PlaybackSurfaceViewActivity |
| **PlaybackTextureView** | Single-Function demonstration module | Basic video playback using TextureView             | PlaybackTextureViewActivity |
| **Downloader**          | Single-Function demonstration module | Video download and offline playback                | DownloaderActivity          |
| **ExternalSubtitle**    | Single-Function demonstration module | External subtitle demo and switching(SubtitleView) | ExternalSubtitleActivity    |
| **ExternalSubtitle**    | Single-Function demonstration module | External subtitle demo and switching(Recommended)  | VttSubtitleSampleActivity   |
| **FloatWindow**         | Single-Function demonstration module | Floating window playback                           | FloatWindowActivity         |
| **MultiResolution**     | Single-Function demonstration module | Multi-bitrate/resolution switching                 | MultiResolutionActivity     |
| **PictureInPicture**    | Single-Function demonstration module | Picture-in-picture playback                        | PictureInPictureActivity    |
| **Preload**             | Single-Function demonstration module | Video preloading (Direct URL/VOD)                  | PreloadActivity             |
| **RtsLiveStream**       | Single-Function demonstration module | RTS ultra-low latency live streaming               | RtsLiveStreamActivity       |
| **Thumbnail**           | Single-Function demonstration module | Video thumbnail preview                            | ThumbnailActivity           |

> 📌 The functional modules will be continuously expanded according to business and demonstration needs. The table only lists some representative modules. For more functions, please follow the project's subsequent updates.

## **🔐 License Configuration Instructions**

This project does not include an official license. Please complete the following steps to configure it before using all features.

### ✅ Preparations for Production Use

1. **Obtain and Integrate the License**

   Please refer to the [Bind a license](https://www.alibabacloud.com/help/en/apsara-video-sdk/user-guide/access-to-license) documentation to obtain an authorized Player SDK License and follow the instructions to complete the integration.

2. **Update License Information**

   In the `App/src/main/AndroidManifest.xml` file, locate the following fields and replace them with your own License:

```xml
<meta-data
    android:name="com.aliyun.alivc_license.licensekey"
    android:value="YOUR_LICENSE_KEY" />
<meta-data
    android:name="com.aliyun.alivc_license.licensefile"
    android:value="YOUR_LICENSE_FILE_PATH" />
```

* license key: Enter the License key string obtained from the console.

* license file: Specify the path to the License file, e.g., assets/cert/release.crt.

3. **Rebuild and Run the Project**

After completing the configuration, please rebuild and run the project. The SDK will automatically load the license and enable full functionality.

> **⚠️ Note**: If the license is not configured correctly, the player functionality may be limited or unavailable.

## **🚀 Quick Start**

### **🧰 Environment Requirements**

| Tool           | Version Requirement  |
| -------------- | -------------------- |
| Android Studio | 4.0+                 |
| Android SDK    | API 21+              |
| JDK            | Recommended: 8 or 11 |

**⚠️ Note:** The current Gradle version **does not support JDK 17 or higher**.

 If you have JDK 17, 21, 23, or later installed, please switch to JDK 8 or 11 before building the project.

> **How to set JDK 11:**
>
> In Android Studio, go to `Settings` (or `Preferences`) → `Build, Execution, Deployment` → `Build Tools` → `Gradle` → `Gradle JDK`, and select 11. (If JDK 11 is not available, please upgrade Android Studio.)

### **📦 Build and Run**

1. **Download the Project**

   ```bash
   git clone [your-repo-url]
   cd API-Example
   ```

2. **Import the Project**

   - Open Android Studio
   - Select `File` → `Open`
   - Choose the project root directory and open it

3. **Sync the Project**

   - Android Studio will automatically prompt to sync Gradle
   - Click `Sync Now` and wait for the sync to complete

4. **Connect Device and Run**

   - Connect an **Android physical device** via USB, and ensure Developer Mode and USB Debugging are enabled

   - Click the `Run` button (green triangle) in the toolbar
   - Select the target device and wait for the app to install and launch

### **🧪 Verify Results**

After launching the app, you will enter the main menu page. Click any feature item to navigate to the corresponding playback demo page.