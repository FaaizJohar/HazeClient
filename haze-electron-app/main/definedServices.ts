import { BaseService } from '@haze/runtime/app'
import { AuthlibInjectorService } from '@haze/runtime/authlibInjector'
import { ElyByService } from '@haze/runtime/elyby'
import { InstallService, VersionMetadataService } from '@haze/runtime/install'
import {
  InstanceLogService,
  InstanceModsService,
  InstanceOptionsService,
  InstanceSavesService,
  InstanceResourcePackService,
  InstanceScreenshotService,
  InstanceServerInfoService,
  InstanceService,
  InstanceShaderPacksService,
  InstanceThemeService,
  InstanceModsGroupService,
} from '@haze/runtime/instance'
import {
  InstanceIOService,
  InstanceInstallService,
  InstanceManifestService,
} from '@haze/runtime/instanceIO'
import { JavaService } from '@haze/runtime/java'
import { LaunchService, VersionService } from '@haze/runtime/launch'
import { ProjectMappingService } from '@haze/runtime/moddb'
import { ModMetadataService } from '@haze/runtime/moddb/ModMetadataService'
import { ModpackService } from '@haze/runtime/modpack'
import { PeerService } from '@haze/runtime/peer'
import { PresenceService } from '@haze/runtime/presence'
import { ResourcePackPreviewService } from '@haze/runtime/resourcePack'
import { ServerStatusService } from '@haze/runtime/serverStatus'
import { ThemeService } from '@haze/runtime/theme'
import { OfficialUserService, UserService, MinecraftFriendsService } from '@haze/runtime/user'

export const definedServices = [
  VersionMetadataService,
  BaseService,
  AuthlibInjectorService,
  InstallService,
  ProjectMappingService,
  InstanceIOService,
  InstanceLogService,
  ElyByService,
  InstanceModsService,
  InstanceModsGroupService,
  InstanceOptionsService,
  InstanceResourcePackService,
  InstanceSavesService,
  InstanceService,
  InstanceScreenshotService,
  InstanceShaderPacksService,
  PresenceService,
  JavaService,
  LaunchService,
  ModpackService,
  InstanceServerInfoService,
  ResourcePackPreviewService,
  InstanceManifestService,
  ServerStatusService,
  OfficialUserService,
  MinecraftFriendsService,
  UserService,
  VersionService,
  InstanceInstallService,
  ModMetadataService,
  PeerService,
  ThemeService,
  InstanceThemeService,
]
