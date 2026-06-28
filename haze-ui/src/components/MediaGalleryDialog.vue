<template>
  <v-dialog
    v-model="isShown"
    max-width="95vw"
    max-height="90vh"
    scrollable
  >
    <v-card class="bg-white dark:bg-[#1a1a1a] rounded-xl overflow-hidden" style="max-height: 90vh;">
      
      <!-- Top Navigation & Header -->
      <v-card-item class="pa-0">
        <div class="flex items-center justify-between bg-gray-100 dark:bg-[#212121] border-b border-gray-300 dark:border-white/10 px-4 py-2">
          
          <div class="flex items-center gap-6">
            <!-- Title -->
            <div class="flex items-center gap-2">
              <v-icon color="primary" size="20">perm_media</v-icon>
              <span class="text-sm font-bold tracking-tight">Media Center</span>
            </div>

            <!-- Tabs -->
            <div class="flex items-center gap-1 bg-gray-200 dark:bg-[#151515] p-1 rounded-lg">
              <v-btn 
                :variant="activeTab === 'screenshot' ? 'flat' : 'text'" 
                :color="activeTab === 'screenshot' ? 'primary' : undefined"
                size="small" 
                class="rounded-md capitalize tracking-tight"
                @click="activeTab = 'screenshot'"
              >
                <v-icon size="small" start>image</v-icon>
                Screenshots
                <v-badge v-if="screenshots.length > 0" :content="screenshots.length" color="white" text-color="black" inline class="ml-1"></v-badge>
              </v-btn>
              <v-btn 
                :variant="activeTab === 'video' ? 'flat' : 'text'" 
                :color="activeTab === 'video' ? 'primary' : undefined"
                size="small" 
                class="rounded-md capitalize tracking-tight"
                @click="activeTab = 'video'"
              >
                <v-icon size="small" start>movie</v-icon>
                Videos
                <v-badge v-if="videos.length > 0" :content="videos.length" color="white" text-color="black" inline class="ml-1"></v-badge>
              </v-btn>
              <v-btn 
                :variant="activeTab === 'replay' ? 'flat' : 'text'" 
                :color="activeTab === 'replay' ? 'primary' : undefined"
                size="small" 
                class="rounded-md capitalize tracking-tight"
                @click="activeTab = 'replay'"
              >
                <v-icon size="small" start>replay</v-icon>
                Replays
                <v-badge v-if="replays.length > 0" :content="replays.length" color="white" text-color="black" inline class="ml-1"></v-badge>
              </v-btn>
            </div>
          </div>

          <!-- Actions -->
          <div class="flex items-center gap-1">
            <v-btn @click="onOpenFolder" size="small" variant="text" class="capitalize tracking-tight">
              <v-icon size="small" start>folder_open</v-icon>
              Open Folder
            </v-btn>
            <v-btn icon variant="text" @click="close" size="small">
              <v-icon>close</v-icon>
            </v-btn>
          </div>

        </div>
      </v-card-item>

      <v-card-text class="p-0 overflow-y-auto" style="max-height: calc(90vh - 56px);">
        <!-- Active View -->
        <div class="p-4 h-full">

          <!-- EMPTY STATE -->
          <div v-if="activeItems.length === 0" class="flex flex-col items-center justify-center py-20 text-gray-700 dark:text-gray-500 opacity-60">
            <v-icon size="64" class="mb-4">
              {{ activeTab === 'screenshot' ? 'photo_camera' : activeTab === 'video' ? 'videocam' : 'history' }}
            </v-icon>
            <h3 class="text-lg font-medium mb-1">No {{ activeTab }}s found</h3>
            <p class="text-sm">They will appear here once you create them in-game.</p>
          </div>

          <!-- SCREENSHOTS GRID -->
          <div v-else-if="activeTab === 'screenshot'" class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
            <div
              v-for="(item, idx) in screenshots"
              :key="item.url"
              class="group relative rounded-xl overflow-hidden bg-gray-200 dark:bg-[#111] cursor-pointer transition-all hover:ring-2 ring-primary hover:shadow-2xl"
              @click="onViewImage(idx)"
            >
              <div class="aspect-video">
                <img :src="item.url" class="w-full h-full object-cover" loading="lazy" />
              </div>
              <div class="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center backdrop-blur-sm">
                <v-icon color="white">fullscreen</v-icon>
              </div>
              <v-btn
                icon
                variant="text"
                color="error"
                class="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition-opacity bg-black/50 hover:bg-black/80"
                @click.stop="onDeleteMedia(item.url)"
                size="small">
                <v-icon size="small">delete</v-icon>
              </v-btn>
              <div class="absolute bottom-0 left-0 right-0 p-2 bg-gradient-to-t from-black/80 to-transparent">
                <p class="text-[10px] text-white/80 truncate">{{ item.name }}</p>
              </div>
            </div>
          </div>

          <!-- VIDEOS GRID -->
          <div v-else-if="activeTab === 'video'" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <div
              v-for="item in videos"
              :key="item.url"
              class="group relative rounded-2xl overflow-hidden bg-gray-200 dark:bg-[#111] border border-white/5 shadow-lg"
            >
              <div class="aspect-video bg-black flex items-center justify-center relative">
                <video controls class="w-full h-full object-contain" preload="metadata">
                  <source :src="item.url" type="video/mp4">
                  Your browser does not support the video tag.
                </video>
              </div>
              <div class="p-3 bg-gray-100 dark:bg-[#151515] flex justify-between items-center">
                <div class="overflow-hidden">
                  <h4 class="text-sm font-medium truncate" :title="item.name">{{ item.name }}</h4>
                  <p class="text-xs text-gray-500">{{ formatBytes(item.size) }} • {{ new Date(item.lastModified).toLocaleDateString() }}</p>
                </div>
                <div class="flex gap-1 shrink-0">
                  <v-btn icon variant="text" size="small" @click="onShowInFolder(item.url)">
                    <v-icon size="small">folder</v-icon>
                  </v-btn>
                  <v-btn icon variant="text" color="error" size="small" @click="onDeleteMedia(item.url)">
                    <v-icon size="small">delete</v-icon>
                  </v-btn>
                </div>
              </div>
            </div>
          </div>

          <!-- REPLAYS GRID -->
          <div v-else-if="activeTab === 'replay'" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <div
              v-for="item in replays"
              :key="item.url"
              class="flex items-center gap-4 p-4 rounded-2xl bg-gray-100 dark:bg-[#1a1a1a] border border-white/10 hover:bg-gray-200 dark:hover:bg-[#252525] transition-colors"
            >
              <div class="h-12 w-12 rounded-xl bg-primary/20 flex items-center justify-center shrink-0">
                <v-icon color="primary" size="24">movie_creation</v-icon>
              </div>
              <div class="flex-grow overflow-hidden">
                <h4 class="text-sm font-bold truncate" :title="item.name">{{ item.name }}</h4>
                <p class="text-xs text-gray-500 mt-0.5">{{ formatBytes(item.size) }} • {{ new Date(item.lastModified).toLocaleString() }}</p>
              </div>
              <div class="flex gap-1 shrink-0">
                <v-btn icon variant="text" size="small" @click="onShowInFolder(item.url)">
                  <v-icon size="small">folder</v-icon>
                </v-btn>
                <v-btn icon variant="text" color="error" size="small" @click="onDeleteMedia(item.url)">
                  <v-icon size="small">delete</v-icon>
                </v-btn>
              </div>
            </div>
          </div>

        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script lang="ts" setup>
import { computed, ref } from 'vue'
import { useDialog } from '@/composables/dialog'
import { kImageDialog } from '@/composables/imageDialog'
import { useInstanceMedia } from '@/composables/media'
import { useService } from '@/composables/service'
import { injection } from '@/util/inject'
import { BaseServiceKey, InstanceMediaServiceKey } from '@haze/runtime-api'

const { isShown, parameter } = useDialog<{
  instancePath: string
}>('media-gallery')

const activeTab = ref<'screenshot' | 'video' | 'replay'>('screenshot')

const instancePath = computed(() => parameter.value?.instancePath || '')
const { media } = useInstanceMedia(instancePath)
const { showItemInDirectory } = useService(BaseServiceKey)
const { deleteMedia, showMedia } = useService(InstanceMediaServiceKey)

const imageDialog = injection(kImageDialog)

// Computed categories
const screenshots = computed(() => media.value.filter(m => m.type === 'screenshot'))
const videos = computed(() => media.value.filter(m => m.type === 'video'))
const replays = computed(() => media.value.filter(m => m.type === 'replay'))

const activeItems = computed(() => {
  if (activeTab.value === 'screenshot') return screenshots.value
  if (activeTab.value === 'video') return videos.value
  if (activeTab.value === 'replay') return replays.value
  return []
})

const onViewImage = (idx: number) => {
  const urls = screenshots.value.map(s => s.url)
  imageDialog.showAll(urls, idx)
}

const onShowInFolder = (url: string) => {
  showMedia(url)
}

const onOpenFolder = () => {
  if (instancePath.value) {
    // Open the instance root since there are multiple folders now
    showItemInDirectory(instancePath.value)
  }
}

const onDeleteMedia = async (url: string) => {
  const success = await deleteMedia(url)
  if (success) {
    // Optimistic UI update
    const index = media.value.findIndex(m => m.url === url)
    if (index > -1) {
      const newMedia = [...media.value]
      newMedia.splice(index, 1)
      media.value = newMedia
    }
  }
}

const close = () => {
  isShown.value = false
}

function formatBytes(bytes: number, decimals = 2) {
    if (!+bytes) return '0 Bytes'
    const k = 1024
    const dm = decimals < 0 ? 0 : decimals
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return `${parseFloat((bytes / Math.pow(k, i)).toFixed(dm))} ${sizes[i]}`
}
</script>

<style scoped>
.aspect-video {
  aspect-ratio: 16 / 9;
}
/* Ensure rounded videos inside containers don't leak corners */
video {
  border-radius: inherit;
}
</style>
