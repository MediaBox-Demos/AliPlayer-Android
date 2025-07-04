# **Android示例工程模块化架构开发指引**

## **📌 概述**

本文档基于 **Android 示例工程**，介绍如何在现有的模块化架构中，快速添加与集成新的功能模块。该架构基于 Gradle 模块化和 Schema 路由机制，具备高度解耦特性，支持完全独立的模块开发和部署。

------

## **🔧 架构特点**

- **模块独立**：功能模块只依赖 Common 模块，无主项目耦合
- **Schema 路由**：支持通过自定义 URL Scheme 实现页面跳转与模块调用
- **极简接入**：新模块只需配置 Gradle、实现 Activity，并注册路由即可完成集成

------

## **🚀 开发流程概览**

### **Step 1：创建模块目录结构**

```bash
YourNewModule/
├── build.gradle                          # Gradle 构建配置
├── consumer-rules.pro                    # ProGuard 消费者规则
├── proguard-rules.pro                    # 混淆规则
├── src/
│   └── main/
│       ├── AndroidManifest.xml           # 模块清单文件
│       ├── java/com/aliyun/player/yournewmodule/
│       │   └── YourNewModuleActivity.java # 功能界面实现
│       └── res/
│           └── layout/
│               └── activity_your_new_module.xml # 界面布局
└── README.md                             # 模块说明文档（推荐）
```

### **Step 2：配置模块 build.gradle**

创建 `YourNewModule/build.gradle`:

```groovy
apply plugin: 'com.android.library'

android {
    compileSdkVersion 31

    defaultConfig {
        minSdkVersion 21

        consumerProguardFiles "consumer-rules.pro"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation 'androidx.appcompat:appcompat:1.2.0'
    implementation 'com.google.android.material:material:1.3.0'
    
    // 只依赖 Common 模块，确保完全独立
    implementation project(":Common")
    
    // 根据功能需要添加其他依赖
    // implementation 'your.specific.dependency:library:version'
}
```

### **Step 3：配置模块 AndroidManifest.xml**

创建 `YourNewModule/src/main/AndroidManifest.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.aliyun.player.yournewmodule">

    <!-- 根据功能需要添加权限 -->
    <!-- <uses-permission android:name="android.permission.INTERNET" /> -->

    <application>
        <activity
            android:name=".YourNewModuleActivity"
            android:exported="true"
            android:theme="@style/Theme.MaterialComponents.Light">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <data
                    android:host="advanced"
                    android:path="/yournewmodule"
                    android:scheme="demo" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

### **Step 4：注册 Schema 常量**

在 `Constants.java` 中添加：

```java
/**
 * Schema related constants
 * Schema相关常量
 */
public static final class Schema {
    // 现有常量...
    
    // Advanced features paths
    private static final String PATH_YOUR_NEW_MODULE = "/yournewmodule";
    
    // Complete schema URLs
    public static final String YOUR_NEW_MODULE = SCHEME + "://" + HOST_ADVANCED + PATH_YOUR_NEW_MODULE;
}
```

### **Step 5：本地化配置**

在 `Common/src/main/res/values/strings.xml` 中添加:

```xml
<!-- 进阶功能菜单项 -->
<string name="menu_your_new_module_title">您的新模块</string>
<string name="menu_your_new_module_desc">新模块功能描述</string>

<!-- 新模块页面 -->
<string name="your_new_module_demo_title">YourNewModule</string>
```

在 `Common/src/main/res/values-en/strings.xml` 中添加:

```xml
<!-- Advanced Feature Menu Items -->
<string name="menu_your_new_module_title">Your New Module</string>
<string name="menu_your_new_module_desc">Description of your new module functionality</string>

<!-- YourNewModule Activity Strings -->
<string name="your_new_module_demo_title">YourNewModule</string>
```

### **Step 6：实现功能界面**

创建 `YourNewModuleActivity.java`:

```java
package com.aliyun.player.yournewmodule;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author your-name
 * @date 2025/6/5
 * @brief 您的新模块功能演示
 * 
 * 本示例展示了如何实现您的新模块功能
 */
public class YourNewModuleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_new_module);

        // 设置标题和返回按钮
        setupActionBar();
        
        // 初始化您的功能
        initYourFeature();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * 设置ActionBar
     */
    private void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.your_new_module_demo_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * 初始化您的功能
     */
    private void initYourFeature() {
        // TODO: 在这里实现您的具体功能
    }
}
```

### **Step 7：创建界面布局**

创建 `activity_your_new_module.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/white">

    <!-- 播放器渲染视图 -->
    <SurfaceView
        android:id="@+id/surface_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_gravity="center" />

</FrameLayout>
```

### **Step 8：添加模块至项目并配置依赖**

1. **更新 settings.gradle**

在项目的根目录下的 `settings.gradle` 文件中添加新模块引用：

```groovy
include ':App'
include ':Common'
include ':BasicPlayback'
include ':YourNewModule'  // 添加您的新模块
```

2. **配置 App 模块依赖**

在 `App/build.gradle` 中添加对新模块的依赖：

```groovy
dependencies {
    // 现有模块依赖
    implementation project(":BasicPlayback")
    implementation project(":Common")
    
    // 添加您的新模块依赖
    implementation project(":YourNewModule")
}
```

**⚠️ 重要提醒**: 只有在 App 模块的 build.gradle 中添加了 `implementation project(":YourNewModule")` 这一行，新模块才会参与编译，否则即使在 settings.gradle 中包含了模块，也不会被编译到最终的 APK 中。

### **Step 9：更新菜单配置**

在 `MenuConfig.java` 中添加菜单项：

```java
// 在 getAdvancedMenuItems() 方法中添加
menuItems.add(new MenuItem(
    getString(R.string.menu_your_new_module_title),
    getString(R.string.menu_your_new_module_desc),
    Constants.Schema.YOUR_NEW_MODULE
));
```

### **Step 10：同步项目并测试**

```bash
# 在 Android Studio 中点击 "Sync Project with Gradle Files"
# 或者在终端执行
./gradlew clean build
```

运行 App，新模块将自动出现在进阶功能列表中。

----

## **📚 示例参考**

可参考 `BasicPlayback` 模块了解完整实现结构，包括：

- 目录结构规范
- Gradle 配置技巧
- AndroidManifest 权限与组件声明
- Activity 生命周期管理
- 多语言资源配置

---

## **🧾 总结**

通过以上步骤，您可以快速开发一个符合模块化架构的新模块，仅需：

- **配置 Gradle 模块**（约10行配置）
- **实现 Activity 界面**（标准 Android 开发）
- **添加模块依赖**（在 App 模块中引用）
- **添加菜单和路由**（约5行代码）
- **更新项目配置**（添加模块引用）

整个过程仅需少量配置，即可实现模块的快速集成与发布。
