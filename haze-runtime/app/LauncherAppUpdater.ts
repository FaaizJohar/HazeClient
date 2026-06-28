import { Tracker } from '@haze/installer'
import { ReleaseInfo, DownloadUpdateTrackerEvents } from '@haze/runtime-api'
export interface DownloadUpdateOptions {
  tracker?: Tracker<DownloadUpdateTrackerEvents>
  abortSignal?: AbortSignal
}

export interface LauncherAppUpdater {
  /**
   * Check update for haze-launcher
   */
  checkUpdateTask(): Promise<ReleaseInfo>

  /**
    * Download the update to the disk. You should first call `checkUpdate`
    */
  downloadUpdate(updateInfo: ReleaseInfo, options?: DownloadUpdateOptions): Promise<void>

  /**
    * Install update and quit the app.
    */
  installUpdateAndQuit(updateInfo: ReleaseInfo): Promise<void>
}
