# **AUIVideoFunctionList**

## **一、模块介绍**

**AUIVideoFunctionList**模块使用多个播放器实例（AliPlayer）+ 预加载（MediaLoader）+ 预渲染的方式实现短视频列表播放，结合本地缓存可以达到更好体验。

## **二、前置条件**

您已获取音视频终端SDK的播放器的License授权和License Key。获取方法，请参见[申请License](https://help.aliyun.com/zh/apsara-video-sdk/user-guide/license-authorization-and-management#13133fa053843)。

## **三、编译运行**

1. 接入已授权播放器的音视频终端SDK License。

   具体操作请参见[Android端接入License](https://help.aliyun.com/zh/apsara-video-sdk/user-guide/access-to-license#58bdccc0537vx)。
   
2. 将 AUIVideoList 目录下的 AUIVideoFunctionList 和 AUIVideoListCommon 两个模块拷贝到您项目工程中。

   请注意修改两个模块 build.gradle 文件中的编译版本（与您项目工程中设置保持一致）以及播放器SDK版本。
   
   播放器SDK版本配置在 AUIVideoListCommon/build.gradle 中修改（参考 AndroidThirdParty/config.gradle 中的 externalPlayerFull ）。

3. 在项目 gradle 文件的 repositories 配置中，引入阿里云SDK的 Maven 源：

   ```groovy
   maven { url "https://maven.aliyun.com/repository/releases" }
   ```
   
4. 增加模块引用方式和依赖方式。

   在项目的 setting.gradle 中增加:
   ```groovy
   // 项目根目录下有一个 AUIVideoList 文件夹，其中包含 AUIVideoListCommon 和AUIVideoFunctionList两个模块，其引用方式如下。
   include ':AUIVideoList:AUIVideoListCommon'
   include ':AUIVideoList:AUIVideoFunctionList' 
   // 如果此模块直接放在根目录下，则应 include ':AUIVideoListCommon' 及 ':AUIVideoFunctionList'
   ```
   
   在 app 模块的 build.gradle 中增加:
   ```groovy
   implementation project(':AUIVideoList:AUIVideoFunctionList')
   // 同上，如果此模块被放置在根目录下，直接写':AUIVideoFunctionList'即可
   ```
   
5. 配置页面跳转，在当前页面中打开对应模块的主界面。

   ```java
   Intent videoListFunctionIntent = new Intent(this, AUIVideoFunctionListActivity.class);
   startActivity(videoListFunctionIntent);
   ```
   
### **集成FAQ**

1. 错误“Namespace not specified”

   请检查您的 AGP 版本。如果为较新版本（如8.3.2），需要手动在各模块 build.gradle 中添加 namespace 设置。旧版本 AGP 此配置位于模块 /src/main/res/AndroidManifest.xml 中的 package 属性。
   
2. Gradle 在处理 repository 的优先级时出现冲突
   
     请优先在 setting.gradle 中添加 repository。

## **四、模块说明**
### **文件说明**

```html
.
└── videoefunctionlist                                     # 根目录
    ├── AUIFunctionListPlayerActivity.java                 # 多实例页面
    ├── AUIVideoFunctionListController.java                # 多实例页面控制器
    ├── AUIVideoFunctionListView.java                      # 视频列表视图
    ├── adapter                                           
    │   ├── AUIVideoFunctionListAdapter.java               # 多实例面板适配器
    │   └── AUIVideoFunctionListLayoutManager.java         # 布局管理器
    └── player                                          
        ├── AliPlayerPool.java                                      # 播放器池
        ├── AliPlayerPreload.java                          # 预加载
        └── AliyunRenderView.java                          # 视频渲染与播放
```

### **架构设计**

![aui_functionlist_architecture](./aui_functionlist_architecture.png)

### **入口页面**

* **AUIVideoFunctionListActivity**

**外部对接**：如果需要将多实例页面作为一个原子页面供外部进行跳转使用，同样是将DataProvider去除，页面跳转时同步传递List\<VideoInfo\>数据到当前页面即可。

### **数据来源**

当前Demo中的数据取自本地videolist.json。

取数据逻辑：AUIVideoListViewModel.DataProvider<List\<VideoInfo\>> dataProvider，通过onLoadData请求数据。

如果通过API请求获取数据，可以重写加载数据函数，直接传入List\<VideoInfo\>数据。

## **五、核心能力介绍**

本组件功能使用阿里云播放器SDK，通过多个播放器实例（AliPlayer）+ 预加载（MediaLoader）+ 预渲染的方式进行实现，使用了预加载、预渲染、HTTPDNS、加密播放等核心能力，在播放延迟、播放稳定性及安全性方面大幅度提升观看体验。具体介绍参考[进阶功能](https://help.aliyun.com/zh/vod/developer-reference/advanced-features)。

### **预加载**

```java
// 使用阿里云播放器SDK的 MediaLoader 组件进行加载
private void load(String url) {
    if(!mIsLive){
        mMediaLoader.load(url, GlobalSettings.DURATION);
    }
}
// 取消加载
private void cancel(String url) {
    if(!mIsLive){
        mMediaLoader.cancel(url);
    }
}
//加载下一个媒体资源
private void loadNext() {
    if (hasNext(mOldPosition.get())) {
        int next = mOldPosition.incrementAndGet();
        load(mUrlLinkedList.get(next).getUrl());
    }
}
//移动到指定位置并加载该位置之后的视频
public void moveToSerial(int position) {
    mExecutorService.execute(() -> {
        //cancel first
        int i = mOldPosition.get();
        if (i >= 0 && i < mUrlLinkedList.size()) {
            cancel(mUrlLinkedList.get(mOldPosition.get()).getUrl());
        }
        //load
        if (hasNext(position)) {
            load(mUrlLinkedList.get(position + 1).getUrl());
        }
       mOldPosition.set(position + 1);
    });
}
```

### **页面事件响应**

```java
// 选中某一视频
public void onPageSelected(int position, AUIVideoListViewHolder viewHolder) {
    this.mCurrentPosition = position;
    mIsPreloading = false;
    mAliPlayerPreload.cancel(position);
    if (viewHolder instanceof AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) {
        AliyunRenderView aliPlayer = ((AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) viewHolder).getAliPlayer();
        aliPlayer.getAliPlayer().start();
    }
}
// 页面滑动到一半
public void onPageHideHalf(int position, AUIVideoListViewHolder viewHolder) {
    if (viewHolder instanceof AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) {
        ((AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) viewHolder).getAliPlayer().pause();
        viewHolder.showPlayIcon(false);
    }
}
// 页面释放
public void onPageRelease(int position, AUIVideoListViewHolder viewHolder) {
    if (viewHolder instanceof AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) {
        ((AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) viewHolder).getAliPlayer().pause();
    }
}
```

### **多实例播放器池**

```java
//初始化AliyunRenderView实例队列
public static void init(Context context) {
    if (mDequeue.size() != TOTAL_SIZE) {
        mDequeue.clear();
        mDequeue.add(new AliyunRenderView(context));
        mDequeue.add(new AliyunRenderView(context));
        mDequeue.add(new AliyunRenderView(context));
    }
}
//获取一个AliyunRenderView实例
public static AliyunRenderView getPlayer() {
    AliyunRenderView aliyunRenderView = mDequeue.pollFirst();
    mDequeue.addLast(aliyunRenderView);
    return aliyunRenderView;
}
// 循环播放
public static void openLoopPlay(boolean openLoopPlay) {
    for (AliyunRenderView aliyunRenderView : mDequeue) {
        aliyunRenderView.openLoopPlay(openLoopPlay);
    }
}
```

### **预渲染**

```java
private void invokeSeekTo() {
    if (mHasCreateSurface && mHasPrepared) {
        mAliPlayer.seekTo(0);
        mHasCreateSurface = false;
        mHasPrepared = false;
    }
}
```
### **HTTPDNS**

HTTPDNS可以提供更快速和稳定的DNS解析服务，通过替换传统DNS解析，可以减少DNS解析时间，提高视频播放的加载速度和稳定性，从而提升用户的观看体验。

音视频终端SDK和播放器SDK从6.12.0版本开始无需手动开启HTTPDNS。

### **视频加密**

音视频终端SDK和播放器SDK从6.8.0版本开始支持MP4私有加密播放能力。

- 经私有加密的MP4格式视频，需满足以下条件，才可正常播放：
  - 经私有加密的MP4视频传给播放器播放时，业务侧（App侧）需要为视频URL追加`etavirp_nuyila=1`
  - App的License对应的uid与产生私有加密MP4的uid是一致的
- 校验加密视频是否正确，以私有加密的视频URL为例：
  - meta信息里面带有`AliyunPrivateKeyUri`的tag
  - ffplay不能直接播放

### **其它功能**

- **防录屏**

  防录屏通过监听录屏和截屏行为及时阻断播放进程，有效保护视频内容的版权，防止未经授权的盗录和传播。

## 六、用户指引

### **文档**

[播放器SDK](https://help.aliyun.com/zh/vod/developer-reference/apsaravideo-player-sdk/)

[音视频终端SDK](https://help.aliyun.com/product/261167.html)

[阿里云·视频点播](https://www.aliyun.com/product/vod)

[视频点播控制台](https://vod.console.aliyun.com)

[ApsaraVideo VOD](https://www.alibabacloud.com/zh/product/apsaravideo-for-vod)


### **FAQ**

如果您在使用播放器SDK有任何问题或建议，欢迎通过钉钉搜索群号31882553加入阿里云播放器SDK开发者群。

