Language: 中文简体 | [English](README-EN.md)

# amdemos-android-player

A demo project for Apsara Video Player SDK.

## **🔐 License 配置说明**

本项目未包含正式 License，请根据以下步骤完成配置以启用完整功能。

### ✅ 正式使用前配置

1. **获取并接入 License**

   请先参考 [接入 License](https://help.aliyun.com/zh/apsara-video-sdk/user-guide/access-to-license) 文档，获取已授权的播放器 SDK License，并按照指引完成接入。

2. **更新 License 信息**

   在 `AUIPlayerAPP/src/main/AndroidManifest.xml` 文件中，找到如下字段并替换为你自己的 License：

```xml
<meta-data
    android:name="com.aliyun.alivc_license.licensekey"
    android:value="YOUR_LICENSE_KEY" />
<meta-data
    android:name="com.aliyun.alivc_license.licensefile"
    android:value="YOUR_LICENSE_FILE_PATH" />
```

* License Key：填写从控制台获取到的 License 密钥字符串。
* License File：填写 License 文件名称（如 license.crt），并将该 .crt 文件添加到 AUIPlayerAPP/src/main/assets/cert/ 目录下。

3. **重新编译运行项目**

   完成配置后，请重新编译并运行项目，SDK 将自动加载 License 并启用完整功能。

> **⚠️ 注意**：若未正确配置 License，播放器功能将会受限或无法使用。

----

## **短剧Demo源码公告**

该版本为 2024年10月前发布的旧版本短剧场景Demo源码，阿里云已于2025年2月正式发布最新版「微短剧场景化多实例Demo」，并包含完整源码。新版Demo相比旧版本，集成易用性更高，播放体验更加丝滑，在播放性能和体验上达到了最佳平衡。

当前目录下的旧版短剧场景Demo源码已不再更新维护，若需获取最新版「微短剧场景化多实例Demo」，请先购买播放器专业版License，并提工单联系我们获取Demo源码，详见：

[集成最新「微短剧场景化多实例Demo」](https://help.aliyun.com/zh/vod/use-cases/micro-drama-integration-ios-player-sdk?spm=a2c4g.11186623.help-menu-29932.d_3_0_0_1_1.6afd523cYfdJM7)

[播放器SDK专业版License获取](https://help.aliyun.com/zh/vod/developer-reference/obtain-the-player-sdk-license?spm=a2c4g.11186623.help-menu-search-29932.d_15)

----

## **🚀 快速开始**

### **🧰 环境要求**

| 工具             | 版本要求        |
|----------------|-------------|
| Android Studio | 4.0+        |
| Android SDK    | API 21+     |
| JDK            | 推荐使用 8 或 11 |

**⚠️ 注意**：当前项目的 Gradle 版本**不兼容 JDK 17 及以上版本**。

如已安装 JDK 17、21、23 等较高版本，请手动切换为 JDK 8 或 11 后再进行构建。

### **IDE**

> **JDK 11 设置方法**：
>
> 打开 Android Studio，进入 `Settings`
> （或 `Preferences`）→ `Build, Execution, Deployment` → `Build Tools` → `Gradle` → `Gradle JDK`，选择
> 11（如果没有 11，请先升级 Android Studio）。

* Android Studio

### **📦 编译运行**

当前播放器Demo项目已完成工程架构改造，支持一体化编译和单独编译；实现工程解构，提升开发效率。

1. **独立编译（推荐）**

    * 您需要将`AlivcAIODemo`工程中的基础模块`AndroidThirdParty`拷贝到当前目录下
    * 以当前目录为目标文件夹，使用Android Studio打开并运行，实现仅播放器模块的独立编译。

2. **一体化编译**

    * 当前工程已内嵌到`AlivcAIODemo`工程。
    * 您可以直接使用`AlivcAIODemo`作为目标文件夹，使用Android Studio打开并运行，实现多模块编译。

3. **配置环境**

    * 打开 Android Studio，进入 Settings（或 Preferences）→ Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK，选择 11（如果没有 11，请先升级 Android Studio）。

4. **同步项目**

    * Android Studio 会自动提示同步 Gradle
    * 点击 `Sync Now` 等待同步完成

5. **连接设备并运行**

    * 使用 USB 连接 **Android 真机设备**，并确保已开启开发者模式与 USB 调试权限
    * 点击工具栏的 `Run` 按钮 (绿色三角形)
    * 选择目标设备并等待应用安装运行

### **🧪 验证结果**

应用启动后将进入主功能菜单页面，点击任意功能项即可跳转至对应的播放演示页面。

### **编译配置**

1. 如您使用**一体化编译**方式进行编译，底层SDK采用音视频终端SDK（AliVCSDK）
2. 如您使用**独立编译**方式进行编译，底层SDK可以配置使用播放器SDK（AliyunPlayer）或音视频终端SDK
    1. `gradle.properties`文件中`allInOne`编译配置，决定使用的SDK类型
    2. true，使用音视频终端SDK；false，使用播放器SDK

## **SDK集成**

* [SDK下载](https://help.aliyun.com/zh/vod/developer-reference/sdk-download)

* [快速集成](https://help.aliyun.com/zh/vod/developer-reference/quick-integration-1)

**注意：如果同时集成直播推流SDK和播放器SDK，会存在冲突问题。可以使用[音视频终端 SDK](https://help.aliyun.com/zh/apsara-video-sdk/download-sdks) 避免冲突**
