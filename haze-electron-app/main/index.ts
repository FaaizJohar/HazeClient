// eslint-disable-next-line @typescript-eslint/no-var-requires
require('graceful-fs').gracefulify(require('fs'))

// eslint-disable-next-line import/first
import { app } from 'electron'
// eslint-disable-next-line import/first
import ElectronLauncherApp from './ElectronLauncherApp'

// Disable sandbox for AppImage to avoid chrome-sandbox permission issues
// AppImage mounts to /tmp which cannot have proper setuid permissions
if (process.env.APPIMAGE) {
  app.commandLine.appendSwitch('no-sandbox')
}

import * as os from 'os'

const ramGB = os.totalmem() / (1024 * 1024 * 1024)
const cores = os.cpus().length
const isLowEnd = ramGB <= 4 || cores <= 2
const isBrokenGPU = false // TODO: expose config flag for disableHardwareAcceleration

if (isLowEnd) {
  app.commandLine.appendSwitch('disable-features', 'UseEcoQoSForBackgroundProcess')
  app.commandLine.appendSwitch('disable-renderer-backgrounding')
  app.commandLine.appendSwitch('disable-background-timer-throttling')
}

if (isBrokenGPU) {
  app.disableHardwareAcceleration()
}

new ElectronLauncherApp().start()
