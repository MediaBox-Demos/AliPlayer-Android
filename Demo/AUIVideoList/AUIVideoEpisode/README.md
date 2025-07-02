# **AUIVideoEpisode**

## **一、模块介绍**

**AUIVideoEpisode**模块，为微短剧场景模块。提供抽屉式剧集列表，负责播放展示指定短剧剧集，获得沉浸式播放体验。

## **二、前置条件**

您已获取音视频终端SDK的播放器的License授权和License Key。获取方法，请参见[申请License](https://help.aliyun.com/zh/apsara-video-sdk/user-guide/license-authorization-and-management#13133fa053843)。

## **三、编译运行**

1. 接入已授权播放器的音视频终端SDK License。

   具体操作请参见[Android端接入License](https://help.aliyun.com/zh/apsara-video-sdk/user-guide/access-to-license#58bdccc0537vx)。
   
2. 将 AUIVideoList 目录下的 AUIVideoEpisode 和 AUIVideoListCommon 两个模块拷贝到您项目工程中。

   请注意修改两个模块 build.gradle 文件中的编译版本（与您项目工程中设置保持一致）以及播放器SDK版本。
   
   播放器SDK版本配置在 AUIVideoListCommon/build.gradle 中修改（参考 AndroidThirdParty/config.gradle 中的 externalPlayerFull ）。

3. 在项目 gradle 文件的 repositories 配置中，引入阿里云SDK的 Maven 源：

   ```groovy
   maven { url "https://maven.aliyun.com/repository/releases" }
   ```
   
4. 增加模块引用方式和依赖方式。

   在项目的 setting.gradle 中增加:
   ```groovy
   // 项目根目录下有一个 AUIVideoList 文件夹，其中包含 AUIVideoListCommon 和AUIVideoEpisode两个模块，其引用方式如下。
   include ':AUIVideoList:AUIVideoListCommon'
   include ':AUIVideoList:AUIVideoEpisode' 
   // 如果此模块直接放在根目录下，则应 include ':AUIVideoListCommon' 及 ':AUIVideoEpisode'
   ```
   
   在 app 模块的 build.gradle 中增加:
   ```groovy
   implementation project(':AUIVideoList:AUIVideoEpisode')
   // 同上，如果此模块被放置在根目录下，直接写':AUIVideoEpisode'即可
   ```

5. 配置页面跳转，在当前页面中打开对应模块的主界面。

   ```java
   Intent videoListEpisodeIntent = new Intent(this, AUIEpisodePlayerActivity.class);
   startActivity(videoListEpisodeIntent);
   ```

注：请确认您的视频源地址，如果视频源地址为模块提供的 MP4 私有加密地址，由于加密特性，集成到您项目工程中将会播放失效。请注意修改 AUIEpisodeConstants 文件下的 EPISODE_JSON_URL 的变量值，手动切换剧集地址。

### **集成FAQ**

1. 错误“Namespace not specified”

   请检查您的 AGP 版本。如果为较新版本（如8.3.2），需要手动在各模块 build.gradle 中添加 namespace 设置。旧版本 AGP 此配置位于模块 /src/main/res/AndroidManifest.xml 中的 package 属性。
   
2. Gradle 在处理 repository 的优先级时出现冲突
   
     请优先在 setting.gradle 中添加 repository。
                                                           
## **四、模块说明**

### **文件说明**

```html
.
└── videoepisode                                            # 短剧根目录
    ├── AUIEpisodePlayerActivity.java                      # 短剧页面
    ├── AUIVideoEpisodeController.java                      # 短剧页面控制器
    ├── adapter                                             # 适配器根目录
    │   ├── AUIEpisodePanelAdapter.java                     # 短剧面板适配器
    │   └── AUIVideoEpisodeAdapter.java                      # 短剧视频页面适配器
    ├── annotation                                          # 枚举类目录
    │   └── PreRenderPlayerState.java                        # 预渲染状态枚举
    ├── component                                           # 页面组件目录
    │   ├── AUIEpisodeBarComponent.java                     # 短剧页面底部bar组件
    │   ├── AUIEpisodePanelComponent.java                    # 短剧选集列表组件
    │   ├── AUIVideoDetailComponent.java                     # Feed流页面详情组件（用户名、视频详情）
    │   └── AUIVideoInteractiveComponent.java                # Feed流页面交互组件（点赞、评论、分享）
    ├── data                                                # 数据结构目录
    │   ├── AUIEpisodeConstants.java                        # 短剧常量类
    │   ├── AUIEpisodeData.java                              # 短剧剧集数据结构
    │   ├── AUIEpisodeDataEvent.java                         # 短剧剧集更新事件
    │   └── AUIEpisodeVideoInfo.java                         # 短剧单集视频数据
    ├── listener                                            # 回调与监听目录
    │   ├── OnDetailEventListener.java                      # Feed流页面详情组件回调事件
    │   ├── OnInteractiveEventListener.java                 # Feed流页面交互组件回调事件
    │   ├── OnPanelEventListener.java                       # 短剧选集列表组件回调事件
    │   └── OnSurfaceListener.java                          # 短剧视频页面Surface状态回调事件
    └── view                                                # 视图目录
        ├── AUIVideoEpisodeLayoutManager.java               # 短剧Feed流上下滑页面骨架控制器
        └── AUIVideoEpisodeListView.java                     # 短剧Feed流上下滑页面骨架
```

### **架构设计**

![aui_episode_architecture](./aui_episode_architecture.jpg)

### **入口页面**

* **AUIEpisodePlayerActivity**

**外部对接**：如果需要将短剧页面作为一个原子页面供外部进行跳转使用，只需要将AUIEpisodePlayerActivity中数据来源DataProvider去除；取而代之的是，在页面跳转时，同步传递AUIEpisodeData数据到当前页面即可。

### **数据定义**

* **短剧剧集**

AUIEpisodeData

| 字段  | 含义             |
| ----- | ---------------- |
| id    | 短剧剧集唯一ID   |
| title | 短剧剧集名称     |
| list  | 短剧剧集视频列表 |

* **单集视频**

AUIEpisodeVideoInfo

![aui_episode_json_data](./aui_episode_json_data.jpg)

继承自基类VideoInfo，新增了橙色部分的字段，主要为：点赞数、评论数，和分享数。

### **数据来源**

当前Demo中的数据为mock数据，取自网页json，参考常量`EPISODE_JSON_URL`。

取数据逻辑：AUIVideoListViewModel.DataProvider<AUIEpisodeData> dataProvider，通过onLoadData请求短剧数据。

## **五、核心能力介绍**

本组件功能通过阿里云播放器SDK的AliListPlayer进行实现，使用了本地缓存、智能预加载、智能预渲染、HTTPDNS、加密播放等核心能力，在播放延迟、播放稳定性及安全性方面大幅度提升观看体验。具体介绍参考[进阶功能](https://help.aliyun.com/zh/vod/developer-reference/advanced-features)。

### **本地缓存**

本地缓存可以提高短视频播放的加载速度和稳定性，使用户在网络不稳定或者断网的情况下依然能够流畅观看视频，提升用户的观看体验。

### **智能预加载**

智能预加载可以提前加载视频数据，使视频播放更加流畅，减少加载等待时间，提升用户的观看体验。

### **智能预渲染**

智能预渲染可以减少视频播放的启动延迟，让用户更快地看到画面，提升视频播放的加载速度和观看体验。

### **HTTPDNS**

HTTPDNS可以提供更快速和稳定的DNS解析服务，通过替换传统DNS解析，可以减少DNS解析时间，提高视频播放的加载速度和稳定性，从而提升用户的观看体验。

音视频终端SDK和播放器SDK从6.12.0版本开始无需手动开启HTTPDNS。

### **视频加密**

音视频终端SDK和播放器SDK从6.8.0版本开始支持MP4私有加密播放能力。

* 经私有加密的MP4格式视频，需满足以下条件，才可正常播放：

  * 经私有加密的MP4视频传给播放器播放时，业务侧（App侧）需要为视频URL追加```etavirp_nuyila=1```

  * App的License对应的uid与产生私有加密MP4的uid是一致的

* 校验加密视频是否正确，以私有加密的视频URL为例：

  * meta信息里面带有`AliyunPrivateKeyUri`的tag
  * ffplay不能直接播放

### **其它功能**

* **防录屏**

  防录屏通过监听录屏和截屏行为及时阻断播放进程，有效保护视频内容的版权，防止未经授权的盗录和传播。


## 六、用户指引

### **文档**

[播放器SDK](https://help.aliyun.com/zh/vod/developer-reference/apsaravideo-player-sdk/)

[音视频终端SDK](https://help.aliyun.com/product/261167.html)

[阿里云·视频点播](https://www.aliyun.com/product/vod)

[视频点播控制台](https://vod.console.aliyun.com)

[ApsaraVideo VOD](https://www.alibabacloud.com/zh/product/apsaravideo-for-vod)


### **FAQ**

[播放异常自主排查](https://help.aliyun.com/zh/vod/developer-reference/troubleshoot-playback-errors)
