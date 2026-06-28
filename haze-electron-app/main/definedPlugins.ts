import { pluginAutoUpdate } from './pluginAutoUpdate'
import { pluginIconProtocol } from './pluginIconProtocol'
import { pluginDiscreteGPULinux } from './pluginDiscreteGPULinux'
import { pluginPowerMonitor } from './pluginPowerMonitor'

import { pluginApiFallback } from '@haze/runtime/app/pluginApiFallback'
import { pluginCommonProtocol } from '@haze/runtime/app/pluginCommonProtocol'
import { pluginMediaProtocol } from '@haze/runtime/app/pluginMediaProtocol'
import { pluginCli } from '@haze/runtime/commands/pluginCli'
import { pluginCommandHost } from '@haze/runtime/commands/pluginCommandHost'
import { elyByPlugin } from '@haze/runtime/elyby/elyByPlugin'
import { pluginEncodingWorker } from '@haze/runtime/encoding/pluginEncodingWorker'
import { pluginClientToken, pluginFlights, pluginGFW, pluginImageStorage, pluginLogConsumer, pluginTasks, pluginTelemetry, pluginUncaughtError } from '@haze/runtime/infra/plugins'
import { pluginLaunchPrecheck } from '@haze/runtime/launch/pluginLaunchPrecheck'
import { pluginMarketProvider } from '@haze/runtime/market/pluginMarketProvider'
import { pluginNativeReplacer } from '@haze/runtime/nativeReplacer/pluginNativeReplacer'
import { pluginNetworkInterface } from '@haze/runtime/network/pluginNetworkInterface'
import { pluginUndiciLogger } from '@haze/runtime/network/pluginUndiciLogger'
import { pluginUserPlaytime } from '@haze/runtime/playTime/pluginUserPlaytime'
import { pluginResourceWorker } from '@haze/runtime/resource/pluginResourceWorker'
import { pluginResourcePackLink } from '@haze/runtime/resourcePack/pluginResourcePackLink'
import { pluginServicesHandler } from '@haze/runtime/service/pluginServicesHandler'
import { pluginSettings } from '@haze/runtime/settings/pluginSettings'
import { pluginSetup } from '@haze/runtime/setup/pluginSetup'
import { pluginModrinthAccess } from '@haze/runtime/user/pluginModrinthAccess'
import { pluginOfficialUserApi } from '@haze/runtime/user/pluginOfficialUserApi'
import { pluginOffineUser } from '@haze/runtime/user/pluginOfflineUser'
import { pluginUserTokenStorage } from '@haze/runtime/user/pluginUserTokenStorage'
import { pluginYggdrasilApi } from '@haze/runtime/user/pluginYggdrasilApi'
import { pluginYggdrasilHandler } from '@haze/runtime/yggdrasilServer/pluginYggdrasilHandler'

import { LauncherAppPlugin } from '~/app'
import { definedServices } from './definedServices'

export const definedPlugins: LauncherAppPlugin[] = [
  pluginCommandHost({ services: definedServices }),
  pluginCli,
  pluginAutoUpdate,
  pluginPowerMonitor,
  pluginIconProtocol,
  pluginApiFallback,
  pluginResourceWorker,
  pluginEncodingWorker,
  pluginSetup,
  pluginLaunchPrecheck,
  pluginDiscreteGPULinux,
  pluginUncaughtError,
  pluginNativeReplacer,
  elyByPlugin,
  pluginMarketProvider,
  pluginYggdrasilApi,

  pluginMediaProtocol,
  pluginResourcePackLink,
  pluginUserPlaytime,
  pluginYggdrasilHandler,
  pluginClientToken,
  pluginServicesHandler(definedServices),
  pluginTelemetry,
  pluginLogConsumer,
  pluginSettings,
  pluginGFW,
  pluginTasks,
  pluginImageStorage,
  pluginFlights,
  pluginNetworkInterface,
  pluginOfficialUserApi,
  pluginOffineUser,
  pluginUndiciLogger,
  pluginUserTokenStorage,

  pluginModrinthAccess,

  pluginCommonProtocol,
]
