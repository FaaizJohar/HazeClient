import { AbstractService, ExposeServiceKey } from '~/service'
import { LauncherApp, LauncherAppKey, Inject } from '~/app'
import { exec } from 'child_process'
import { promisify } from 'util'
import * as os from 'os'

export const HardwareServiceKey = 'HardwareService'

export interface HardwareFingerprint {
  totalRAM: number
  freeRAM: number
  cpuModel: string
  cpuCores: number
  gpuModel: string
  gpuVRAM: number
  os: string
  javaVersion: string
}

export enum PerformanceProfile {
  POTATO = 'POTATO',
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  ULTRA = 'ULTRA'
}

const execAsync = promisify(exec)

@ExposeServiceKey(HardwareServiceKey)
export class HardwareService extends AbstractService {
  constructor(@Inject(LauncherAppKey) app: LauncherApp) {
    super(app)
  }

  async getHardwareFingerprint(): Promise<HardwareFingerprint> {
    const totalRAM = os.totalmem()
    const freeRAM = os.freemem()
    const cpuModel = os.cpus()[0]?.model || 'Unknown CPU'
    const cpuCores = os.cpus().length
    const osPlatform = os.platform()
    let gpuModel = 'Unknown GPU'
    let gpuVRAM = 0
    let javaVersion = 'Unknown'

    try {
      if (osPlatform === 'win32') {
        const { stdout } = await execAsync('wmic path win32_VideoController get Name,AdapterRAM /value')
        const lines = stdout.split('\n')
        for (const line of lines) {
          if (line.includes('Name=')) {
            gpuModel = line.split('=')[1].trim()
          }
          if (line.includes('AdapterRAM=')) {
            const bytes = parseInt(line.split('=')[1].trim(), 10)
            if (!isNaN(bytes)) {
              gpuVRAM = Math.floor(bytes / (1024 * 1024 * 1024))
            }
          }
        }
      } else if (osPlatform === 'darwin') {
        const { stdout } = await execAsync('system_profiler SPDisplaysDataType')
        // Simple parse for macOS
        const match = stdout.match(/Chipset Model: (.+)/)
        if (match) gpuModel = match[1].trim()
      } else if (osPlatform === 'linux') {
        const { stdout } = await execAsync('lspci -vnn | grep VGA -A 12')
        // Simple parse for Linux
        const match = stdout.match(/VGA compatible controller: (.+)/)
        if (match) gpuModel = match[1].trim()
      }
    } catch (e) {
      this.warn('Failed to detect GPU information', e)
    }

    try {
      const { stdout, stderr } = await execAsync('java -version')
      const versionOutput = stderr || stdout
      const match = versionOutput.match(/version "([^"]+)"/)
      if (match) javaVersion = match[1]
    } catch (e) {
      this.warn('Failed to detect Java version', e)
    }

    return {
      totalRAM,
      freeRAM,
      cpuModel,
      cpuCores,
      gpuModel,
      gpuVRAM,
      os: osPlatform,
      javaVersion,
    }
  }

  async getRecommendedProfile(): Promise<PerformanceProfile> {
    const fingerprint = await this.getHardwareFingerprint()
    const ramGB = fingerprint.totalRAM / (1024 * 1024 * 1024)

    if (ramGB <= 4 && fingerprint.cpuCores <= 2) return PerformanceProfile.POTATO
    if (ramGB <= 8 && fingerprint.cpuCores <= 4 && fingerprint.gpuVRAM < 2) return PerformanceProfile.LOW
    if (ramGB <= 16 && fingerprint.cpuCores <= 8 && fingerprint.gpuVRAM <= 4) return PerformanceProfile.MEDIUM
    if (ramGB < 32 && fingerprint.cpuCores < 12 && fingerprint.gpuVRAM < 8) return PerformanceProfile.HIGH
    return PerformanceProfile.ULTRA
  }
}
