import { InstanceMediaServiceKey, LaunchServiceKey, MediaItem } from '@haze/runtime-api'
import { useService } from './service'
import { useRefreshable } from './refreshable'
import { Ref, shallowRef, watch, onUnmounted } from 'vue'

export function useInstanceMedia(path: Ref<string>) {
  const { getMedia } = useService(InstanceMediaServiceKey)
  const { on, removeListener } = useService(LaunchServiceKey)
  const media = shallowRef([] as MediaItem[])
  
  const { refresh, refreshing } = useRefreshable<any | undefined>(async () => {
    if (!path.value) return
    const result = await getMedia(path.value)
    media.value = result || []
  })

  watch(path, refresh, { immediate: true })
  
  on('minecraft-exit', refresh)
  onUnmounted(() => {
    removeListener('minecraft-exit', refresh)
  })

  return {
    media,
    refreshing,
    refresh
  }
}
