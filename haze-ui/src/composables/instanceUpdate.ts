import { InstanceData, InstanceFile, InstanceUpstream } from '@haze/instance'
import { CachedFTBModpackVersionManifest } from '@haze/runtime-api'
import { InjectionKey, Ref } from 'vue'
import { DialogKey } from './dialog'

export type InstanceInstallOptions = {
  type: 'upstream'
  instancePath: string
  modpack: string
  upstream: InstanceUpstream
} | {
  type: 'ftb'
  newManifest: CachedFTBModpackVersionManifest
  oldManifest: CachedFTBModpackVersionManifest
  upstream: InstanceUpstream
} | {
  type: 'updates'
  oldFiles: InstanceFile[]
  files: InstanceFile[]
  id: string
}

export const InstanceInstallDialog: DialogKey<InstanceInstallOptions> = 'instance-install'
export const UnresolvedFilesDialogKey: DialogKey<void> = 'unresolved-files'
export const BaseSettingModUpgradeDialogKey: DialogKey<{ minecraftVersion: string }> = 'base-setting-mod-upgrade'
export const kUpstream: InjectionKey<Ref<{ upstream: InstanceData['upstream']; minecraft: string }>> = Symbol('Upstream')
