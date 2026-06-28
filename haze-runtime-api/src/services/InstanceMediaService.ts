import { ServiceKey } from './Service'

export interface MediaItem {
  /**
   * The type of media
   */
  type: 'screenshot' | 'video' | 'replay'
  /**
   * The name of the file
   */
  name: string
  /**
   * The URL to load or play the media in the launcher
   */
  url: string
  /**
   * File size in bytes
   */
  size: number
  /**
   * Last modified timestamp
   */
  lastModified: number
  /**
   * Extended metadata (e.g. tags, duration, dimensions) for dynamic filtering
   */
  metadata?: {
    tags?: string[]
    durationMs?: number
    width?: number
    height?: number
  }
}

export interface InstanceMediaService {
  /**
   * Fetches all media (screenshots, videos, replays) from the instance directories
   */
  getMedia(instancePath: string): Promise<MediaItem[]>

  /**
   * Open the file or its parent folder in the OS explorer
   */
  showMedia(url: string): Promise<void>

  /**
   * Delete a media file by its URL
   */
  deleteMedia(url: string): Promise<boolean>
}

export const InstanceMediaServiceKey: ServiceKey<InstanceMediaService> = 'InstanceMediaService'
