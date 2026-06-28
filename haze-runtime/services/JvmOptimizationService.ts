import { AbstractService, ExposeServiceKey } from '~/service'
import { LauncherApp, LauncherAppKey, Inject } from '~/app'
import { HardwareService, HardwareServiceKey } from './HardwareService'

export const JvmOptimizationServiceKey = 'JvmOptimizationService'

@ExposeServiceKey(JvmOptimizationServiceKey)
export class JvmOptimizationService extends AbstractService {
  constructor(
    @Inject(LauncherAppKey) app: LauncherApp,
    @Inject(HardwareServiceKey as any) private hardwareService: HardwareService
  ) {
    super(app)
  }

  async optimizeJvmArgs(): Promise<string[]> {
    const fingerprint = await this.hardwareService.getHardwareFingerprint()
    const ramGB = fingerprint.totalRAM / (1024 * 1024 * 1024)
    const cores = fingerprint.cpuCores

    let heapSize = '1536M'
    if (ramGB <= 4) heapSize = '1536M'
    else if (ramGB <= 8) heapSize = '3072M'
    else if (ramGB <= 16) heapSize = '5120M'
    else {
      // Don't exceed 50% system RAM for heap, max 8GB
      const maxHeap = Math.min(8, Math.floor(ramGB / 2))
      heapSize = `${maxHeap}G`
    }

    const args = [
      `-Xms${heapSize}`,
      `-Xmx${heapSize}`,
      '-XX:+UseStringDeduplication',
      '-XX:+OptimizeStringConcat',
      '-XX:+UseCompressedOops',
      '-XX:+UseCompressedClassPointers',
      '-Dfml.ignorePatchDiscrepancies=true',
      '-Dfml.ignoreInvalidMinecraftCertificates=true',
      '-Dsodium.checks.win32.rtss=false',
    ]

    if (fingerprint.os === 'win32') {
      args.push('-Dos.name="Windows 10"')
      args.push('-Dos.version=10.0')
    }

    if (cores >= 8 && ramGB >= 8) {
      // Shenandoah GC
      args.push(
        '-XX:+UseShenandoahGC',
        '-XX:ShenandoahGCMode=iu',
        '-XX:ShenandoahGCHeuristics=adaptive',
        '-XX:+ShenandoahUncommit',
        '-XX:ShenandoahUncommitDelay=1000'
      )
    } else if (cores >= 4 || ramGB > 4) {
      // Tuned G1GC (Aikar)
      args.push(
        '-XX:+UseG1GC',
        '-XX:+ParallelRefProcEnabled',
        '-XX:MaxGCPauseMillis=50',
        '-XX:+UnlockExperimentalVMOptions',
        '-XX:+DisableExplicitGC',
        '-XX:+AlwaysPreTouch',
        '-XX:G1NewSizePercent=30',
        '-XX:G1MaxNewSizePercent=40',
        '-XX:G1HeapRegionSize=8M',
        '-XX:G1ReservePercent=20',
        '-XX:G1HeapWastePercent=5',
        '-XX:G1MixedGCCountTarget=4',
        '-XX:InitiatingHeapOccupancyPercent=15',
        '-XX:G1MixedGCLiveThresholdPercent=90',
        '-XX:G1RSetUpdatingPauseTimePercent=5',
        '-XX:SurvivorRatio=32',
        '-XX:+PerfDisableSharedMem',
        '-XX:MaxTenuringThreshold=1'
      )
    } else {
      // Serial or Basic G1GC
      args.push('-XX:+UseG1GC')
    }

    return args
  }
}
