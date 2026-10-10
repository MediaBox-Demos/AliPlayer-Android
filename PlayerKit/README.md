Language: 中文简体 | [English](README-EN.md)

# **🎬 AliPlayerKit Android 指南**

[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen)](https://www.android.com/) [![GitHub](https://img.shields.io/badge/GitHub-PlayerKit--Android-blue?logo=github)](https://github.com/aliyun/PlayerKit-Android) [![website](https://img.shields.io/badge/Product-VOD-FF6A00)](https://www.aliyun.com/product/vod)

------

## **📌 简介**

**AliPlayerKit** 是阿里云面向视频业务推出的 **播放器含 UI 集成方案**，提供**低代码、可扩展的播放器 UI 组件**与**场景化解决方案**。

<p align="center">
  <img
    src="./_media/playerkit-ui-preview.png"
    alt="AliPlayerKit UI 组件预览"
    width="100%"
  />
</p>

<p align="center">
  <sub>开箱即用的播放器 UI，支持多种播放形态与交互配置</sub>
</p>

通过对播放器能力和 UI 交互的高度封装，帮助客户以极低的接入成本快速完成 App 播放能力建设，无需直接调用底层播放器 API，也无需自行实现复杂的播放器 UI。

---

## **✨ 核心特性**

- 🚀 **低代码接入** — 无需直接调用底层播放器 API，几行代码即可完成视频播放能力接入。
- 🎨 **开箱即用的 UI 组件** — 提供可配置的播放器 UI 组件，覆盖基础播放与常见交互能力。
- 📱 **场景化解决方案** — 内置中长视频、短视频、直播、播放列表等典型业务场景，快速搭建完整播放体验。
- 🔧 **高度可扩展的架构设计** — 插槽系统支持 UI 自由组合，策略系统支持业务逻辑灵活扩展。
- 🌐 **跨平台统一架构** — 各端共享相同的设计理念与接口语义，降低多端接入与维护成本。

| 平台 | 说明 | 源码 | 文档 |
|------|------|------|------|
| **Android** | 原生 Android 播放器 UI 组件与场景化解决方案 | [PlayerKit-Android](https://github.com/aliyun/PlayerKit-Android) | [在线文档](https://aliyun.github.io/PlayerKit-Android/) |
| **iOS** | 原生 iOS 播放器 UI 组件与场景化解决方案 | [PlayerKit-iOS](https://github.com/aliyun/PlayerKit-iOS) | [在线文档](https://aliyun.github.io/PlayerKit-iOS/) |
| **Flutter** | 跨平台 Flutter 播放器 Widget 封装 | [player_widget](https://github.com/aliyun/player_widget) | [在线文档](https://aliyun.github.io/player_widget/) |

------

## **🏗️ 架构概览**

在架构层级上，**AliPlayerKit 位于播放器内核之上**，通过统一的 UI 组件体系与播放场景抽象，承载不同播放业务的共性能力：

```
┌──────────────────────────────────────────────────┐
│              业务应用层（Your App）              │
├──────────────────────────────────────────────────┤
│         playerkit-scenes（场景解决方案）         │
│       中长视频 / 短视频 / 直播 / 播放列表        │
├──────────────────────────────────────────────────┤
│           playerkit（核心 UI 组件层）            │
│        AliPlayerView / Controller / Model        │
│        插槽 / 策略 / 事件 / 生命周期策略         │
├──────────────────────────────────────────────────┤
│           AliPlayer SDK（播放器内核）            │
└──────────────────────────────────────────────────┘
```

AliPlayerKit 提供两种接入层级：

| 层级 | 模块 | 说明 | 适用场景 |
|-----|------|------|---------|
| **组件层** | `playerkit` | 核心 UI 组件，提供播放器视图、控制器、数据模型 | 需要自定义 UI 或灵活控制播放行为 |
| **场景层** | `playerkit-scenes` | 完整场景解决方案，包含 UI 和业务逻辑 | 快速实现标准播放场景 |

------

## **🔗 相关链接**

- 📘 **在线文档**：[AliPlayerKit Android 文档](https://aliyun.github.io/PlayerKit-Android/)
- 💻 **GitHub 仓库**：[PlayerKit-Android](https://github.com/aliyun/PlayerKit-Android)

------

## **🚀 快速开始**

请访问 [GitHub 仓库](https://github.com/aliyun/PlayerKit-Android) 获取源码，并结合 [在线文档](https://aliyun.github.io/PlayerKit-Android/) 完成接入。

------

如需进一步帮助，请访问 [阿里云视频点播官方文档中心](https://help.aliyun.com/zh/vod) 获取更多资源支持。

-----

