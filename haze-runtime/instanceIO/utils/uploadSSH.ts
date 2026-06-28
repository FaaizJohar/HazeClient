import type { ServerOptions } from '@haze/core'
import { ServerSSHExporter } from '@haze/instance'

/**
 * Upload instance files to SSH server with progress tracking.
 */
export async function uploadSSH(
  exporter: ServerSSHExporter,
  serverDir: string,
  options: ServerOptions,
  files: string[],
  signal?: AbortSignal,
): Promise<void> {
  signal?.addEventListener('abort', () => {
    exporter.abort()
  })

  await exporter.exportInstance(serverDir, options, files)
}
