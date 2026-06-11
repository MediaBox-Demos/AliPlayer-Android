# **AUIShortPlaylistFeeds**

## **一、场景介绍**

**AUIShortPlaylistFeeds** 模块是短剧 Feeds 流场景化模块，基于 **AUIShortVideoList** 组件实现。该模块提供 Feeds 流 TAB 页，支持 TAB 页嵌套、TAB 页上下左右滑动播放，实现了播放器实例共享。

## **二、场景集成**

### **集成准备**

在进行短剧 Feeds 流场景搭建之前，请确保已完成 **AUIShortVideoList** 组件的集成准备。

### **集成步骤**

1. 将 AUIShortPlaylistFeeds 模块拷贝到您项目工程中。
2. 检查 AUIShortVideoList 组件的依赖关系，并增加模块引用方式和依赖方式。

* 检查组件依赖

请在 AUIShortPlaylistFeeds 模块的 build.gradle 文件中检查 AUIShortVideoList 组件的依赖配置：

```groovy
// 若 AUIShortVideoList 模块位于 AUIPlayerKits 文件夹中：
implementation project(':AUIPlayerKits:AUIShortVideoList')
// 若 AUIShortVideoList 模块直接放在项目根目录：
implementation project(':AUIShortVideoList')
```

* 添加模块引用

如果您使用 Groovy DSL，请在项目根目录的 settings.gradle 文件中添加以下内容：

```groovy
// 若 AUIShortPlaylistFeeds 模块位于 AUIPlayerScenes 文件夹中：
include ':AUIPlayerScenes:AUIShortPlaylistFeeds'
// 若 AUIShortPlaylistFeeds 模块直接放在项目根目录：
include ':AUIShortPlaylistFeeds'
```

如果您使用 Kotlin DSL，请在项目根目录的 settings.gradle.kts 文件中添加以下内容：

```kotlin
// 若 AUIShortPlaylistFeeds 模块位于 AUIPlayerScenes 文件夹中：
include(":AUIPlayerScenes:AUIShortPlaylistFeeds")
// 若 AUIShortPlaylistFeeds 模块直接放在项目根目录：
include(":AUIShortPlaylistFeeds")
```

* 添加模块依赖

如果您使用 Groovy DSL，请在 app 模块的 build.gradle 文件中添加以下内容：

```groovy
// 若 AUIShortPlaylistFeeds 模块位于 AUIPlayerScenes 文件夹中：
implementation project(':AUIPlayerScenes:AUIShortPlaylistFeeds')
// 若 AUIShortPlaylistFeeds 模块直接放在项目根目录：
implementation project(':AUIShortPlaylistFeeds')
```

如果您使用 Kotlin DSL，请在 app 模块的 build.gradle.kts 文件中添加以下内容：

```kotlin
// 若 AUIShortPlaylistFeeds 模块位于 AUIPlayerScenes 文件夹中：
implementation(project(":AUIPlayerScenes:AUIShortPlaylistFeeds"))
// 若 AUIShortPlaylistFeeds 模块直接放在项目根目录：
implementation(project(":AUIShortPlaylistFeeds"))
```

4. 编译运行，确保组件已被正确集成。

## **三、快速开始**

### **使用方法**

您可以将短剧 Feeds 流 Activity 页面直接提供给外部进行跳转。以下是不同语言的实现示例：

Java 示例：

```java 
// TODO: context is android context
Intent intent = new Intent(context, AUIShortPlaylistFeedsActivity.class);
startActivity(intent);
```

Kotlin 示例：

```kotlin
// TODO: context is android context
val intent = Intent(context, AUIShortPlaylistFeedsActivity::class.java)
startActivity(intent)
```

### **获取数据**

AUIShortPlaylistFeeds 模块使用的数据结构为 `List<VideoInfo>`，其中 `VideoInfo` 为存储视频信息的数据类；如需了解更详细的内容，建议查阅 AUIShortVideoList 组件的完整文档。