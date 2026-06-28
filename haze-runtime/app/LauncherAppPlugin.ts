import { AppManifest } from '@haze/runtime-api'
import { LauncherApp } from './LauncherApp'

export interface LauncherAppPlugin {
  (app: LauncherApp, manifest: AppManifest): void
}
