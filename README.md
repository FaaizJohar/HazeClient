<p align="center">
  <img alt="Haze Launcher Logo" width="100" src="haze-electron-app/icons/dark@256x256.png">
</p>

<h2 align="center">Haze Launcher</h2>
<p align="center">
  <b>A premium Minecraft launcher by Cavrix Core Technologies</b>
</p>

<p align="center">
  <a href="https://github.com/FaaizJohar/HazeClient/releases">
    <img src="https://img.shields.io/github/v/release/FaaizJohar/HazeClient?style=flat-square&label=Latest%20Release&color=6c63ff" alt="Latest Release">
  </a>
  <a href="https://github.com/FaaizJohar/HazeClient/blob/master/LICENSE">
    <img src="https://img.shields.io/github/license/FaaizJohar/HazeClient?style=flat-square&color=6c63ff" alt="License">
  </a>
  <a href="https://github.com/FaaizJohar/HazeClient/issues">
    <img src="https://img.shields.io/github/issues/FaaizJohar/HazeClient?style=flat-square" alt="Issues">
  </a>
</p>

---

## ✨ Features

- **Modern UI** — Dark-themed premium launcher interface
- **In-Game Overlay** — Open the Haze Client menu with **Right Shift** while in-game
- **JVM Presets** — Minimal, Balanced, and Aikar's GC preset flags
- **FPS Boost** — Sodium, ImmediatelyFast, Entity Culling and MoreCulling integrated
- **Instance Management** — Create and manage multiple Minecraft instances
- **Mod Manager** — Browse and install mods from Modrinth and CurseForge
- **Auto Updater** — Keeps the launcher up to date automatically

---

## 📦 Download

Head to the [Releases page](https://github.com/FaaizJohar/HazeClient/releases) and download the latest `HazeClient-Installer.exe`.

---

## 🛠️ Building from Source

### Prerequisites

- Node.js ≥ 20
- pnpm 10+
- JDK 21 (for haze-in-game module)

### Steps

```bash
# Clone the repository
git clone https://github.com/FaaizJohar/HazeClient.git
cd haze-launcher

# Install dependencies
pnpm install --frozen-lockfile

# Build renderer
pnpm build:renderer

# Build the Electron app + installer
pnpm run --prefix=haze-electron-app build:all
```

The installer will be output to `haze-electron-app/build/output/HazeClient-Installer.exe`.

---

## 🗂️ Project Structure

| Directory | Description |
|---|---|
| `haze-electron-app/` | Electron main process & installer config |
| `haze-keystone-ui/` | Vue 3 renderer (the launcher UI) |
| `haze-runtime/` | Core launcher runtime services |
| `haze-runtime-api/` | Shared TypeScript API types |
| `haze-in-game/` | Fabric mod for in-game overlay (Java) |
| `haze-core-framework/` | Core game enhancement framework (Java) |

---

## 🎮 In-Game Controls

| Keybind | Action |
|---|---|
| **Right Shift** | Open Haze Client overlay menu |
| **Right Control** | Open HUD editor |
| **C** | Zoom |

---

## 🐛 Reporting Issues

Please use the [GitHub Issues](https://github.com/FaaizJohar/HazeClient/issues/new/choose) page to report bugs or request features.

---

## 📄 License

MIT — see [LICENSE](LICENSE) for details.
