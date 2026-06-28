# Contributing to Haze Client

First off, thank you for considering contributing to **Haze Client**! It's people like you that make the open-source community such an amazing place to learn, inspire, and create.

## 🛡️ Important Notice
As outlined in the README, if you use, fork, or copy significant portions of Haze Client's source code for your own projects, you **must** provide clear and visible credit back to this repository and its creator, **FaaizJohar**. Please do not claim this work as your own.

## 🚀 How Can I Contribute?

### Reporting Bugs
If you find a bug, please create an Issue on GitHub. Include:
- A clear descriptive title.
- Steps to reproduce the bug.
- Expected vs. actual behavior.
- Screenshots if applicable.
- Your OS and Java version.

### Suggesting Enhancements
Have an idea for a new feature? We'd love to hear it! Create an Issue tagged as an `enhancement` and describe how it would work and why it would be beneficial for Haze Client.

### Pull Requests (Code Contributions)
If you are writing code for Haze Client, please follow these steps:
1. **Fork** the repository and create your branch from `main`.
2. **Setup your environment:** Ensure you have Node.js >= 20, pnpm 10+, and JDK 21 installed.
3. Run `pnpm install --frozen-lockfile` to install dependencies.
4. If you've added code that should be tested, add tests.
5. Ensure the test suite passes (`pnpm test`).
6. Format your code (`pnpm format`) and ensure it passes the linter (`pnpm lint`).
7. Issue that pull request!

## 🧑‍💻 Architecture Overview

When contributing to the codebase, please be mindful of our architecture:
- **`haze-electron-app/`**: The Electron main process handling the backend.
- **`haze-ui/`**: The Vue 3 renderer containing the modern launcher interface.
- **`haze-in-game/`**: The core Java Fabric mod loaded inside Minecraft.
- **`haze-core-framework/`**: The Java framework for UI overlays, modules, and events.

## 🤝 Code of Conduct
By participating in this project, you are expected to uphold our [Code of Conduct](CODE_OF_CONDUCT.md).

Happy coding!
- FaaizJohar & The Haze Client Team
