import { AbstractService, ExposeServiceKey } from '~/service'
import { LauncherApp, LauncherAppKey, Inject } from '~/app'

export const PreLaunchScanServiceKey = 'PreLaunchScanService'

export interface ScanResult {
  severity: 'crash' | 'warning' | 'tip'
  message: string
  autoFix?: () => Promise<void>
}

export interface ScanOptions {
  mods: string[]
  loader: string
  minecraftVersion: string
  javaPath: string
}

@ExposeServiceKey(PreLaunchScanServiceKey)
export class PreLaunchScanService extends AbstractService {
  constructor(@Inject(LauncherAppKey) app: LauncherApp) {
    super(app)
  }

  async scan(options: ScanOptions): Promise<ScanResult[]> {
    const results: ScanResult[] = []

    // Duplicate optimization mod conflict
    if (options.mods.some(m => m.toLowerCase().includes('optifine')) && options.mods.some(m => m.toLowerCase().includes('sodium'))) {
      results.push({
        severity: 'crash',
        message: 'OptiFine and Sodium are incompatible and will cause a crash. Please remove one of them.',
      })
    }

    // FPS Killer Mod
    if (options.mods.some(m => m.toLowerCase().includes('journeymap'))) {
      results.push({
        severity: 'warning',
        message: 'JourneyMap real-time chunk scanning can reduce FPS. Consider disabling web-map or using Xaero\'s Minimap.',
      })
    }
    
    // Missing optimization mods for Fabric 1.18+
    const isModernFabric = options.loader === 'fabric' && (options.minecraftVersion.startsWith('1.18') || options.minecraftVersion.startsWith('1.19') || options.minecraftVersion.startsWith('1.20'))
    if (isModernFabric && !options.mods.some(m => m.toLowerCase().includes('sodium'))) {
      results.push({
        severity: 'tip',
        message: 'Consider adding Sodium, Lithium, and FerriteCore for significantly better performance.',
      })
    }

    // Java Version Mismatch
    const isJava21Required = options.minecraftVersion.startsWith('1.20.5') || options.minecraftVersion.startsWith('1.21')
    if (isJava21Required && !options.javaPath.includes('21')) { // Very basic check
      results.push({
        severity: 'crash',
        message: `Minecraft ${options.minecraftVersion} requires Java 21, but a different version might be selected.`,
        autoFix: async () => {
          this.log('Auto-fixing Java version...')
          // TODO: Implementation for auto-downloading Java 21
        }
      })
    }

    return results
  }
}
