Language: [中文简体](README.md) | English

# **🎬 AliPlayerKit Android Guide**

[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen)](https://www.android.com/) [![GitHub](https://img.shields.io/badge/GitHub-PlayerKit--Android-blue?logo=github)](https://github.com/aliyun/PlayerKit-Android) [![website](https://img.shields.io/badge/Product-VOD-FF6A00)](https://www.alibabacloud.com/en/product/apsaravideo-for-vod)

------

## **📌 Overview**

**AliPlayerKit** is Alibaba Cloud's **player UI integration solution** for video applications, offering **low-code, extensible player UI components** and **scenario-based solutions**.

<p align="center">
  <img
    src="./_media/playerkit-ui-preview.png"
    alt="AliPlayerKit UI preview"
    width="100%"
  />
</p>

<p align="center">
  <sub>Ready-to-use player UI components for multiple playback scenarios</sub>
</p>

By encapsulating player capabilities and UI interactions, it enables rapid development of app playback features with minimal integration effort — no need to call underlying player APIs directly or build complex player UIs from scratch.

---

## **✨ Core Features**

- 🚀 **Low-code Integration** — Integrate video playback capabilities with just a few lines of code, without directly calling low-level player APIs.
- 🎨 **Out-of-the-Box UI Components** — Configurable player UI components covering basic playback and common interaction capabilities.
- 📱 **Scenario-based Solutions** — Built-in typical business scenarios such as medium/long video, short video, live streaming, and playlists, enabling rapid construction of complete playback experiences.
- 🔧 **Highly Extensible Architecture** — The slot system supports flexible UI composition, while the strategy system supports flexible extension of business logic.
- 🌐 **Unified Cross-platform Architecture** — Shared design philosophy and API semantics across platforms, reducing multi-platform integration and maintenance costs.

| Platform | Description | Source | Docs |
|----------|-------------|--------|------|
| **Android** | Native Android player UI components & scenario solutions | [PlayerKit-Android](https://github.com/aliyun/PlayerKit-Android) | [Online Docs](https://aliyun.github.io/PlayerKit-Android/) |
| **iOS** | Native iOS player UI components & scenario solutions | [PlayerKit-iOS](https://github.com/aliyun/PlayerKit-iOS) | [Online Docs](https://aliyun.github.io/PlayerKit-iOS/) |
| **Flutter** | Cross-platform Flutter player widget | [player_widget](https://github.com/aliyun/player_widget) | [Online Docs](https://aliyun.github.io/player_widget/) |

------

## **🏗️ Architecture Overview**

Architecturally, **AliPlayerKit sits on top of the player core**, providing a unified UI component system and playback scenario abstraction that carries the shared capabilities of different playback businesses:

```
┌──────────────────────────────────────────────────┐
│           Application Layer (Your App)           │
├──────────────────────────────────────────────────┤
│      playerkit-scenes (Scenario Solutions)       │
│  Mid/Long Video / Short Video / Live / Playlist  │
├──────────────────────────────────────────────────┤
│       playerkit (Core UI Component Layer)        │
│        AliPlayerView / Controller / Model        │
│       Slot / Strategy / Event / Lifecycle        │
├──────────────────────────────────────────────────┤
│           AliPlayer SDK (Player Core)            │
└──────────────────────────────────────────────────┘
```

AliPlayerKit provides two integration levels:

| Level | Module | Description | Use Case |
|-------|--------|-------------|----------|
| **Component** | `playerkit` | Core UI components: player view, controller, data model | Custom UI or flexible playback control |
| **Scene** | `playerkit-scenes` | Complete scenario solutions with UI and business logic | Quickly implement standard playback scenarios |

------

## **🔗 Related Links**

- 📘 **Online Documentation**: [AliPlayerKit Android Documentation](https://aliyun.github.io/PlayerKit-Android/)
- 💻 **GitHub Repository**: [PlayerKit-Android](https://github.com/aliyun/PlayerKit-Android)

------

## **🚀 Quick Start**

Please visit the [GitHub repository](https://github.com/aliyun/PlayerKit-Android) to get the source code, and refer to the [online documentation](https://aliyun.github.io/PlayerKit-Android/) for integration.

------

For further assistance, please visit [Alibaba Cloud VOD Documentation Center](https://www.alibabacloud.com/help/en/vod/) for more resources and support.

-----
