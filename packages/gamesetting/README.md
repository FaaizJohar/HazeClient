# Gamesetting Module

[![npm version](https://img.shields.io/npm/v/@haze/gamesetting.svg)](https://www.npmjs.com/package/@haze/gamesetting)
[![Downloads](https://img.shields.io/npm/dm/@haze/gamesetting.svg)](https://npmjs.com/@haze/gamesetting)
[![Install size](https://packagephobia.now.sh/badge?p=@haze/gamesetting)](https://packagephobia.now.sh/result?p=@haze/gamesetting)
[![npm](https://img.shields.io/npm/l/@haze/minecraft-launcher-core.svg)](https://github.com/voxelum/minecraft-launcher-core-node/blob/master/LICENSE)
[![Build Status](https://github.com/voxelum/minecraft-launcher-core-node/workflows/Build/badge.svg)](https://github.com/Voxelum/minecraft-launcher-core-node/actions?query=workflow%3ABuild)

Provide function to parse Minecraft game settings

## Usage

### Parse GameSetting (options.txt)

Serialize/Deserialize the minecraft game setting string.

```ts
import { GameSetting } from '@haze/gamesetting'
const settingString;
const setting: GameSetting = GameSetting.parse(settingString);
const string: string = GameSetting.stringify(setting);
```
