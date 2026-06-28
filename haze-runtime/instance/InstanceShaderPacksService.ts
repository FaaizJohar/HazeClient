import { InstanceShaderPacksService as IInstanceShaderPacksServic, InstanceShaderPacksServiceKey } from '@haze/runtime-api'
import { Inject, LauncherAppKey } from '~/app'
import { ExposeServiceKey } from '~/service'
import { LauncherApp } from '../app/LauncherApp'
import { AbstractInstanceDomainService } from './AbstractInstanceDomainService'
import { ResourceDomain } from '@haze/resource'

@ExposeServiceKey(InstanceShaderPacksServiceKey)
export class InstanceShaderPacksService extends AbstractInstanceDomainService implements IInstanceShaderPacksServic {
  constructor(@Inject(LauncherAppKey) app: LauncherApp) {
    super(app, ResourceDomain.ShaderPacks)
  }
}
