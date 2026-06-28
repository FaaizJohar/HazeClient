import { OptifineVersion } from '@haze/runtime-api'
import { InjectionKey } from '~/app'

export const kOptifineInstaller: InjectionKey<(version: OptifineVersion) => Promise<string>> = Symbol('kOptifineInstaller')
