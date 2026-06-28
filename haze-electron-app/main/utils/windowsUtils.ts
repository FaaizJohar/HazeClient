import ElectronLauncherApp from '@/ElectronLauncherApp'
import { Logger } from '@haze/runtime/infra'

export function getWindowsUtils(app: ElectronLauncherApp, logger: Logger) {
  try {
    // eslint-disable-next-line @typescript-eslint/no-var-requires
    const utils = require('@haze/windows-utils')
    logger.log('Success to load windows utils!')
    return utils as typeof import('@haze/windows-utils')
  } catch (e) {
    logger.warn('Fail to load windows utils!')
    return undefined
  }
}
