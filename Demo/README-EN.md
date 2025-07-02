Language: [中文简体](README.md) | English

# amdemos-android-player

A demo project for Apsara Video Player SDK.

## **🔐 License Configuration Instructions**

This project does not include an official license. Please complete the following steps to configure it before using all features.

### ✅ Preparations for Production Use

1. **Obtain and Integrate the License**

   Please refer to the [Bind a license](https://www.alibabacloud.com/help/en/apsara-video-sdk/user-guide/access-to-license) documentation to obtain an authorized Player SDK License and follow the instructions to complete the integration.

2. **Update License Information**

   In the `AUIPlayerAPP/src/main/AndroidManifest.xml` file, locate the following fields and replace them with your own License:

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

> **⚠️ Note**: If the license is not configured correctly, the player functionality may be limited
> or unavailable.

------

## **Short Drama Demo Source Code Announcement**

This version is the old Short Drama Scenario Demo source code released before October 2024. Alibaba Cloud officially released the latest version of the "Micro-Drama Scenario Multi-Instance Demo" in February 2025, which includes the complete source code. Compared with the old version, the new Demo features higher integration ease of use, smoother playback experience, and achieves the optimal balance in playback performance and user experience.

The old version of the Short Drama Scenario Demo source code in the current directory is no longer updated or maintained. If you need to obtain the latest "Micro-Drama Scenario Multi-Instance Demo," please purchase the Professional Player License first and submit a ticket to contact us for the Demo source code. For details, see:

[Integrate the Latest "Micro-Drama Scenario Multi-Instance Demo"](https://help.aliyun.com/zh/vod/use-cases/micro-drama-integration-ios-player-sdk?spm=a2c4g.11186623.help-menu-29932.d_3_0_0_1_1.6afd523cYfdJM7)

[Obtain the Player SDK Professional License](https://help.aliyun.com/zh/vod/developer-reference/obtain-the-player-sdk-license?spm=a2c4g.11186623.help-menu-search-29932.d_15)

------

## **🚀 Quick Start**

### **🧰 Environment Requirements**

| Tool           | Version Requirement  |
|----------------|----------------------|
| Android Studio | 4.0+                 |
| Android SDK    | API 21+              |
| JDK            | Recommended: 8 or 11 |

**⚠️ Note:** The current Gradle version **does not support JDK 17 or higher**.

If you have JDK 17, 21, 23, or later installed, please switch to JDK 8 or 11 before building the project.

> **How to set JDK 11:**
>
> In Android Studio, go to `Settings` (
> or `Preferences`) → `Build, Execution, Deployment` → `Build Tools` → `Gradle` → `Gradle JDK`, and
> select 11. (If JDK 11 is not available, please upgrade Android Studio.)

### **IDE**

* Android Studio

### **📦 Build and Run**

The current player demo project has undergone structural improvements to support both all-in-one and standalone compilation, enhancing development efficiency.

1. **Independent Compilation (Recommended)**

   * ou need to copy the basic module AndroidThirdParty from the AlivcAIODemo project to the current directory.
   * Open and run the project with Android Studio using the current directory as the target folder to achieve standalone compilation of the player module.

2. **Integrated Compilation**

   * The current project is embedded within the AlivcAIODemo project.
   * You can directly open and run AlivcAIODemo with Android Studio to achieve multi-module compilation.

3. **Environment Configuration**
   * Open Android Studio, go to Settings (or Preferences) → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK, select 11 (if 11 is not available, please upgrade Android Studio).

4. **Sync the Project**
   * Android Studio will automatically prompt to sync Gradle.
   * Click `Sync Now` and wait for the synchronization to complete.

5. **Connect Device and Run**
   * Connect an **Android physical** device via USB and ensure that Developer Mode and USB Debugging permissions are enabled.
   * Click the `Run` button (green triangle) on the toolbar.
   * Select the target device and wait for the application to install and run.

### **🧪 Verification Results**

After the application starts, it will enter the main functionality menu page. 
Click any functionality item to jump to the corresponding playback demonstration page.

### **Compilation Configuration**

1. If you are using**All-in-one Compilation**, the underlying SDK utilizes the Audio-Video Terminal SDK (AliVCSDK).
2. If you are using**Standalone Compilation**, you can configure the underlying SDK to use either the player SDK (AliyunPlayer) or the Audio-Video Terminal SDK.
   1. The `allInOne` compilation configuration in the `gradle.properties` file, determines the SDK type used.
   2. true, uses the Audio-Video Terminal SDK; false, uses the player SDK.

## **SDK Integration**

* [SDK download](https://www.alibabacloud.com/help/en/vod/developer-reference/sdk-download)
* [Quick integration](https://www.alibabacloud.com/help/en/vod/developer-reference/quick-integration-1)

**Note: If you integrate both the live streaming push SDK and the player SDK, there will be conflict issues. You can use the [Apsara Video SDK](https://www.alibabacloud.com/help/en/apsara-video-sdk/download-sdks) to avoid conflicts.**
