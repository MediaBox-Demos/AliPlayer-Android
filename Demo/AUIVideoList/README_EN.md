# **AUIVideoList**

## **1. Module Introduction**

**AUIVideoList** module is designed for **immersive short video**，It includes UI components encapsulated with the Alibaba Cloud Player SDK, providing a smooth short video streaming experience and achieving an immersive playback experience.

## **2. Module Composition**

| Module Name          | Module Type     | Implementation                         |
| -------------------- |-----------------|----------------------------------------|
| AUIVideoEpisode      | Episode mode    | Based on AliListPlayer                 |
| AUIVideoFunctionList | Functional mode | Based on AliPlayer, multiple instances |
| AUIVideoStandradList | Standard mode   | Based on AliListPlayer                 |

### **1. Short drama mode**

Episode mode uses AliListPlayer to implement short video list playback. Based on Alibaba Cloud's experience in the Short drama scenario, it integrates best practices such as local caching and intelligent preloading of the Audio-Video Terminal SDK, providing a low-code integration suite that helps integrators quickly build Short Episode apps and achieve a better audiovisual experience.

For more details, please refer to the `README.md` under the AUIVideoEpisode module.

### **2. Functional mode**

Functional mode uses multiple AliPlayer instances + preloading + pre-rendering to implement short video list playback. With local caching, it can achieve a better experience. Compared with the standard mode, the functional mode supports all functions of the player, offering more flexibility and powerful features.

For more details, please refer to the `README.md` under the AUIVideoFunctionList module.

### **3. Standard mode**

The standard mode uses AliListPlayer to implement short video list playback, achieving a better experience through built-in preloading and local caching mechanisms.

## **3. Compilation and Execution**

1. Integrate the authorized Audio-Video Terminal SDK License.

   For specific operations, refer to [Bind a license](https://www.alibabacloud.com/help/en/apsara-video-sdk/user-guide/access-to-license)。
                             
2. Copy the corresponding modules under the AUIVideoList directory (AUIVideoEpisode/AUIVideoFunctionList/AUIVideoStandardList) and the AUIVideoListCommon module to your project.

   Make sure to modify the compile version in the build.gradle files of these modules (to match your project's settings) as well as the player SDK version.

   The player SDK version is configured in AUIVideoListCommon/build.gradle (reference the externalPlayerFull in AUIVideoListCommon/build.gradle).

3. In the repositories configuration of your project's gradle file, add the Maven source for the Alibaba Cloud SDK:

   ```groovy
   maven { url "https://maven.aliyun.com/repository/releases" }
   ```
   
4. Add the reference and dependency of the desired modules.

   In your project's setting.gradle, add on demand:
   ```groovy
   // The project root directory contains an AUIVideoList folder with the AUIVideoListCommon module. If this module is directly placed under the root directory, it should be ':AUIVideoListCommon', and so on for other modules.
   include ':AUIVideoList:AUIVideoListCommon'// Required by all three modules
   include ':AUIVideoList:AUIVideoEpisode' 
   include ':AUIVideoList:AUIVideoFunctionList'
   include ':AUIVideoList:AUIVideoStandradList'
   ```

   In the build.gradle of the app module, add on demand:
   ```groovy
   // Add the AUIVideoEpisode module in the AUIVideoList folder as a compile-time dependency of the app module. Similarly, if it's placed under the root directory, just write ':AUIVideoEpisode'.
   implementation project(':AUIVideoList:AUIVideoEpisode')
   implementation project(':AUIVideoList:AUIVideoFunctionList')
   implementation project(':AUIVideoList:AUIVideoStandradList')
   ```

5. Configure page navigation to open the main interface of the corresponding module from the current page.

   ```java
   // episode mode
   Intent videoListEpisodeIntent = new Intent(this, AUIEpisodePlayerActivity.class);
   startActivity(videoListEpisodeIntent);
   
   // functional mode
   Intent videoListFunctionIntent = new Intent(this, AUIVideoFunctionListActivity.class);
   startActivity(videoListFunctionIntent);
   
   // standard mode
   Intent videoListStandardIntent = new Intent(this, AUIVideoStandardListActivity.class);
   startActivity(videoListStandardIntent);
   ```
### **Integration FAQ**
  
1. Error “Namespace not specified”

   Please check your AGP version. If it's a newer version (like 8.3.2), you need to manually add the namespace setting in each module's build.gradle file. For older AGP versions, this configuration is located in the package attribute of the module's /src/main/res/AndroidManifest.xml.
    
2. Gradle conflict during repository priority processing

   Please add the repository in setting.gradle as a priority.

## **4. User Guide**

### **Documentation**

* [Short video list player](https://www.alibabacloud.com/help/en/vod/developer-reference/short-video-list-player)

* [微短剧场景简介](https://help.aliyun.com/zh/apsara-video-sdk/use-cases/introduction-to-micro-drama-scenes)

* [ApsaraVideo Player SDK](https://www.alibabacloud.com/help/en/vod/developer-reference/apsaravideo-player-sdk/)

* [ApsaraVideo MediaBox SDK](https://www.alibabacloud.com/en/product/mediabox_sdk)

* [ApsaraVideo VOD](https://www.alibabacloud.com/en/product/apsaravideo-for-vod/)

* [视频点播控制台](https://vod.console.aliyun.com)

* [ApsaraVideo VOD](https://www.alibabacloud.com/zh/product/apsaravideo-for-vod)

### **FAQ**

If you have any questions or suggestions regarding the player SDK, feel free to join the Alibaba Cloud Player SDK developer group by searching for group number 31882553 on DingTalk.
