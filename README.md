<p align="center">
  <img alt="Haze Client Logo" width="120" src="haze-electron-app/icons/dark@256x256.png">
</p>

<h1 align="center">Haze Client</h1>
<p align="center">
  <b>The Ultimate Open-Source Minecraft PvP & Utility Client</b>
</p>

<p align="center">
  <a href="https://github.com/FaaizJohar/HazeClient">
    <img src="https://img.shields.io/badge/Version-0.59.1-6b32ec?style=for-the-badge" alt="Version">
  </a>
  <a href="https://github.com/FaaizJohar/HazeClient/blob/main/LICENSE">
    <img src="https://img.shields.io/github/license/FaaizJohar/HazeClient?style=for-the-badge&color=8b5cf6" alt="License">
  </a>
  <a href="https://github.com/FaaizJohar/HazeClient/issues">
    <img src="https://img.shields.io/github/issues/FaaizJohar/HazeClient?style=for-the-badge&color=4d23a9" alt="Issues">
  </a>
  <a href="https://github.com/FaaizJohar/HazeClient">
    <img src="https://img.shields.io/badge/Platform-Windows-6b32ec?style=for-the-badge&logo=windows" alt="Platform">
  </a>
</p>

<p align="center">
  <i>Built for maximum FPS, seamless mod management, and a premium UI experience.</i>
</p>

---

## 🚀 What is Haze Client?

**Haze Client** is a custom, high-performance Minecraft launcher and in-game utility client designed for players who demand the best. Whether you are looking for massive **FPS boosts**, integrated **Fabric & Forge** support, or a built-in **PvP and HUD overlay**, Haze Client delivers it all in a sleek, dark-themed interface. 

It is entirely open-source, beautifully designed, and packed with features that rival premium clients.

## ✨ Core Features

- ⚡ **Massive FPS Boosts:** Pre-integrated with Sodium, Lithium, Entity Culling, and MemoryLeakFix. Play Minecraft smoother than ever before.
- 🎨 **Premium UI/UX:** A stunning, modern Electron & Vue 3 launcher featuring a deep dark mode and responsive micro-animations.
- 🎮 **In-Game Overlay:** A beautiful in-game ClickGUI and HUD Editor. Open the menu instantly with **Right Shift**.
- 🔧 **Mod & Instance Manager:** Seamlessly browse, install, and manage mods directly from **Modrinth** and **CurseForge** within the launcher.
- 🚀 **Optimized JVM Presets:** Automatic memory allocation and advanced Garbage Collection flags (Aikar's Flags) for zero stuttering.
- 🔄 **Auto-Updates:** Never worry about manual installations. Haze Client keeps itself and your mods updated automatically.

## 📦 Installation & Download

Getting started with Haze Client is incredibly simple:

1. Head over to our [Releases Page](https://github.com/FaaizJohar/HazeClient/releases).
2. Download the latest `HazeClient-Installer.exe` (Windows).
3. Run the installer and log in with your Microsoft account.
4. Click **Launch** and enjoy the highest FPS you've ever had in Minecraft!

## 🎮 In-Game Controls

Once you launch the game, Haze Client's utility features are right at your fingertips:

| Keybind | Action | Description |
|---|---|---|
| **Right Shift** | **ClickGUI** | Opens the main Haze Client module menu. |
| **Right Control** | **HUD Editor** | Move and customize your on-screen overlays (FPS, Ping, CPS). |
| **C** | **Zoom** | OptiFine-style zoom for better visibility. |

## 🛠️ Tech Stack & Architecture

Haze Client is built using modern, bleeding-edge technologies:

- **Launcher Frontend:** Vue 3, TypeScript, Vite, Unocss
- **Launcher Backend:** Electron, Node.js
- **In-Game Client:** Java 21, Fabric API, Mixins
- **Core Architecture:** Modular event-bus system and robust injection pipeline.

### Directory Structure

| Directory | Purpose |
|---|---|
| `haze-ui/` | The Vue 3 renderer and modern launcher interface. |
| `haze-electron-app/` | The Electron main process handling local file/auth systems. |
| `haze-in-game/` | The core Java Fabric mod loaded when Minecraft starts. |
| `haze-core-framework/` | The engine handling ClickGUI, modules, and events. |
| `packages/` | Internal modules for Modrinth/CurseForge API integration. |

## 🏗️ Building from Source

Are you a developer? You can easily build Haze Client from source!

### Prerequisites
- [Node.js](https://nodejs.org/) (v20 or higher)
- [pnpm](https://pnpm.io/) (v10 or higher)
- [Java Development Kit (JDK) 21](https://adoptium.net/)

### Build Instructions
```bash
# Clone the repository
git clone https://github.com/FaaizJohar/HazeClient.git
cd HazeClient

# Install all workspace dependencies
pnpm install --frozen-lockfile

# Build the frontend Vue renderer
pnpm build:renderer

# Compile the Electron application and generate the Windows Installer
pnpm run --prefix=haze-electron-app build:all
```
Your compiled installer will be available in `haze-electron-app/build/output/`.

## 🤝 Contributing

We welcome contributions! Whether you want to add new in-game modules, improve the launcher UI, or optimize the Java framework, feel free to open a Pull Request. Please read our `CONTRIBUTING.md` before submitting.

## 📄 License & Credits

Haze Client is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for more information.

> [!IMPORTANT]  
> **Copyright & Credits:** If you use, fork, or copy significant portions of Haze Client's source code for your own projects, you **must** provide clear and visible credit back to this repository and its creator, **FaaizJohar**. Please do not claim this work as your own.

---
<p align="center">
  <i>Keywords for SEO: Minecraft Client, Best FPS Boost Client, Custom Minecraft Launcher, Open Source Minecraft Client, PvP Client, Modrinth Launcher, Electron Vue Minecraft Launcher, Haze Client.</i>
</p>
