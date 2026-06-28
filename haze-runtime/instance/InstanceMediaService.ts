import {
  InstanceMediaService as IInstanceMediaService,
  InstanceMediaServiceKey,
  MediaItem,
} from "@haze/runtime-api";
import { existsSync } from "fs";
import { readdir, unlink, stat } from "fs-extra";
import { extname, join } from "path";
import { Inject, LauncherAppKey } from "~/app";
import { AbstractService, ExposeServiceKey } from "~/service";
import { LauncherApp } from "../app/LauncherApp";

const IMAGE_EXTENSIONS: readonly string[] = [".png", ".jpg", ".jpeg", ".webp", ".gif"];
const VIDEO_EXTENSIONS: readonly string[] = [".mp4", ".webm", ".mkv", ".mov", ".avi"];
const REPLAY_EXTENSIONS: readonly string[] = [".mcpr"];

@ExposeServiceKey(InstanceMediaServiceKey)
export class InstanceMediaService
  extends AbstractService
  implements IInstanceMediaService
{
  constructor(@Inject(LauncherAppKey) app: LauncherApp) {
    super(app);
  }

  private async scanDirectory(
    dirPath: string,
    extensions: readonly string[],
    type: 'screenshot' | 'video' | 'replay'
  ): Promise<MediaItem[]> {
    if (!existsSync(dirPath)) {
      return [];
    }

    const entries = await readdir(dirPath, { withFileTypes: true });
    const items: MediaItem[] = [];

    for (const entry of entries) {
      if (!entry.isFile()) continue;

      const ext = extname(entry.name).toLowerCase();
      if (!extensions.includes(ext)) continue;

      const fullPath = join(dirPath, entry.name);
      try {
        const fileStat = await stat(fullPath);
        const url = new URL("http://launcher/media");
        url.searchParams.append("path", fullPath);

        const metadata: MediaItem['metadata'] = { tags: [] }
        if (type === 'screenshot') metadata.tags?.push('Screenshot', 'Image')
        else if (type === 'video') metadata.tags?.push('Video', 'Gameplay')
        else if (type === 'replay') metadata.tags?.push('Replay', '3D')

        items.push({
          type,
          name: entry.name,
          url: url.toString(),
          size: fileStat.size,
          lastModified: fileStat.mtimeMs,
          metadata,
        });
      } catch (e) {
        this.error(e as Error);
      }
    }

    return items;
  }

  async getMedia(instancePath: string): Promise<MediaItem[]> {
    const screenshotsPath = join(instancePath, "screenshots");
    const replayVideosPath = join(instancePath, "replay_videos");
    const replaysPath = join(instancePath, "replay_recordings");
    const videosPath = join(instancePath, "videos");

    const [screenshots, replayVideos, videos, replays] = await Promise.all([
      this.scanDirectory(screenshotsPath, IMAGE_EXTENSIONS, 'screenshot'),
      this.scanDirectory(replayVideosPath, VIDEO_EXTENSIONS, 'video'),
      this.scanDirectory(videosPath, VIDEO_EXTENSIONS, 'video'),
      this.scanDirectory(replaysPath, REPLAY_EXTENSIONS, 'replay'),
    ]);

    // Combine and sort by date descending (newest first)
    const allMedia = [...screenshots, ...replayVideos, ...videos, ...replays];
    return allMedia.sort((a, b) => b.lastModified - a.lastModified);
  }

  async showMedia(url: string): Promise<void> {
    const parsed = new URL(url);
    const path = parsed.searchParams.get("path");
    if (path && existsSync(path)) {
      this.app.shell.showItemInFolder(path);
    }
  }

  async deleteMedia(url: string): Promise<boolean> {
    try {
      const parsed = new URL(url);
      const path = parsed.searchParams.get("path");
      if (path && existsSync(path)) {
        await unlink(path);
        return true;
      }
      return false;
    } catch (e) {
      const errorForLog = e instanceof Error ? e : new Error(String(e));
      this.error(errorForLog);
      return false;
    }
  }
}
