# **快速开始**

下面介绍 **AUIShortVideoList** 组件的对外接口和使用方法介绍，用于实现短视频列表播放功能。在完成 AUIShortVideoList 组件的集成步骤后，您可以直接将以下代码拷贝到项目中使用。

## **1. 使用方法**

下面提供三种方法，通过不同的方式对接 AUIShortVideoList 模块，以便快速实现功能运行：

* **AUIShortVideoListActivity**

您可以将短视频列表播放 Activity 页面直接提供给外部进行跳转。以下是不同语言的实现示例：

Java 示例：

```java
// TODO: context is android context
Intent intent = new Intent(context, AUIShortVideoListActivity.class);
// TODO: videoInfoListJSON is the serialized string of List<VideoInfo>
intent.putExtra(AUIShortVideoListView.KEY_VIDEO_INFO_LIST_DATA, videoInfoListJSON);
startActivity(intent);
```

Kotlin 示例：

```kotlin
// TODO: context is android context
val intent = Intent(context, AUIShortVideoListActivity::class.java)
// TODO: videoInfoListJSON is the serialized string of List<VideoInfo>
intent.putExtra(AUIShortVideoListView.KEY_VIDEO_INFO_LIST_DATA, videoInfoListJSON)
startActivity(intent)
```

在代码中，`context` 指的是 Android 应用的上下文。要获取 `videoInfoListJSON` 数据，请参考文档中「获取数据」部分的示例代码。

* **AUIShortVideoListFragment**

您可以将短视频列表播放 Fragment 嵌入到 Activity 页面或者 Fragment 页面中进行使用，具体调用逻辑可参考以下示例：

1. 在 XML 布局文件中添加 FrameLayout，用于承载 Fragment：

```xml
<FrameLayout
    android:id="@+id/fragment_container"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

2. 在代码中初始化 Fragment 并嵌入到容器。以下是不同语言的实现示例：

Java 示例：

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // ...

    if (savedInstanceState == null) {
        AUIShortVideoListFragment fragment = new AUIShortVideoListFragment();
        Bundle bundle = new Bundle();
        // TODO: videoInfoListJSON is the serialized string of List<VideoInfo>
        bundle.putString(AUIShortVideoListView.KEY_VIDEO_INFO_LIST_DATA, videoInfoListJSON);
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
```

Kotlin 示例：

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // ...

    if (savedInstanceState == null) {
        val fragment = AUIShortVideoListFragment()
        val bundle = Bundle()
        // TODO: videoInfoListJSON is the serialized string of List<VideoInfo>
        bundle.putString(AUIShortVideoListView.KEY_VIDEO_INFO_LIST_DATA, videoInfoListJSON)
        fragment.arguments = bundle
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
```

* **AUIShortVideoListView**

您可以利用短视频列表播放 View 组件，构建一个沉浸式的列表播放页面，具体调用逻辑可参考以下示例：

1. 在 XML 布局文件中添加短视频列表播放 View 组件：

```xml
<!--  1. Add Short Video List View Component  -->
<com.alivc.player.playerkits.shortvideolist.AUIShortVideoListView
    android:id="@+id/aui_video_list_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

2. 在代码中声明短视频列表播放 View 组件，并添加 `List<VideoInfo>` 数据源。以下是不同语言的实现示例：

Java 示例：

```java
// 2. Declaration of Short Video List View
private AUIShortVideoListView mShortVideoListView;
mShortVideoListView = findViewById(R.id.aui_video_list_view);

// 3. TODO: Retrieve data and fill it into videoInfoList
List<VideoInfo> videoInfoList;

// 4. Add List<VideoInfo> type data source to Short Video List View
mShortVideoListView.addSources(videoInfoList);
// mShortVideoListView.loadSources(videoInfoList);
```

Kotlin 示例：

```kotlin
// 2. Declaration of Short Video List View
private lateinit var mShortVideoListView: AUIShortVideoListView
mShortVideoListView = findViewById(R.id.aui_video_list_view)

// 3. TODO: Retrieve data and fill it into videoInfoList
val videoInfoList: List<VideoInfo>

// 4. Add List<VideoInfo> type data source to Short Video List View
mShortVideoListView.addSources(videoInfoList)
// mShortVideoListView.loadSources(videoInfoList)
```

## **2. 获取数据**

AUIShortVideoList 组件使用的数据结构为 `List<VideoInfo>`，其中 `VideoInfo` 为存储视频信息的数据类，其数据结构如下：

| 字段     | 类型   | 释义       | 备注                                   |
| -------- | ------ | ---------- | -------------------------------------- |
| id       | int    | 视频唯一id | 用于唯一标识每一个视频                 |
| url      | String | 视频源地址 | 您可以自定义视频源格式，如 MP4/M3U8 等 |
| coverUrl | String | 视频封面图 |                                        |
| author   | String | 视频作者   |                                        |
| title    | String | 视频标题   |                                        |
| type     | String | 视频类型   | 参考 VideoType 枚举，视频源 or 广告    |

为了确保 AUIShortVideoList 组件正常运行，请通过 Bundle 传递已序列化的 `List<VideoInfo>` 字符串，以下是示例代码：

```java
intent.putExtra(AUIShortVideoListView.KEY_VIDEO_INFO_LIST_DATA, videoInfoListJSON);
```

您可以通过网络请求或数据转换这两种方式，获取最终的 `List<VideoInfo>` 数据源，并将其序列化为 JSON 字符串。

* 网络请求

以下是不同语言的实现示例：

Java 示例：

```java
AUIShortVideoListUtil.requestVideoInfoList(new AUIShortVideoListUtil.OnNetworkCallBack<List<VideoInfo>>() {
    @Override
    public void onResponse(List<VideoInfo> videoInfoList) {
        if (videoInfoList == null || videoInfoList.isEmpty()) {
            // TODO: Request video info list error!
            return;
        }
        String videoInfoListJSON = AUIShortVideoListUtil.serializeVideoInfoListToJson(videoInfoList);
        // TODO: Use videoInfoList or videoInfoListJSON...
    }
});
```

Kotlin 示例：

```kotlin
AUIShortVideoListUtil.requestVideoInfoList(object : AUIShortVideoListUtil.OnNetworkCallBack<List<VideoInfo?>?> {
    override fun onResponse(videoInfoList: List<VideoInfo?>?) {
        if (videoInfoList.isNullOrEmpty()) {
            // TODO: Request video info list error!
            return
        }
        val videoInfoListJSON = AUIShortVideoListUtil.serializeVideoInfoListToJson(videoInfoList)
        // TODO: Use videoInfoList or videoInfoListJSON...
    }
})
```

* 数据转换

Java 示例：

```java
ArrayList<VideoInfo> videoInfoList = AUIShortVideoListUtil.assembleVideoInfoList();
if (videoInfoList == null || videoInfoList.isEmpty()) {
    // TODO: Assemble video info list error!
    return;
}
String videoInfoListJSON = AUIShortVideoListUtil.serializeVideoInfoListToJson(videoInfoList);
// TODO: Use videoInfoList or videoInfoListJSON...
```

Kotlin 示例：

```kotlin
val videoInfoList = AUIShortVideoListUtil.assembleVideoInfoList()
if (videoInfoList.isNullOrEmpty()) {
    // TODO: Assemble video info list error!
    return
}
val videoInfoListJSON = AUIShortVideoListUtil.serializeVideoInfoListToJson(videoInfoList)
// TODO: Use videoInfoList or videoInfoListJSON...
```

## **3. 搭建场景**

AUIShortVideoList 组件支持低代码集成，适用于多种场景。您可以基于此组件构建短视频列表的场景化功能。请参考 AUIPlayerScenes 中的示例及文档，例如 AUIShortPlaylistTheater（短剧剧场场景化模块）和 AUIShortPlaylistFeeds（短剧 Feeds 流场景化模块）。
