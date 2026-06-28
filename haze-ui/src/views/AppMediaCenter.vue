<template>
  <div class="media-center-view w-full h-full flex flex-col overflow-hidden bg-transparent">
    <!-- Header / Filter Bar -->
    <MediaFilterBar
      v-model="activeFilter"
      v-model:search="searchQuery"
      @refresh="loadMedia"
    />

    <!-- Main Content Area -->
    <div class="flex-1 overflow-y-auto p-4 space-y-4">
      <div v-if="loading" class="flex justify-center items-center h-64">
        <v-progress-circular indeterminate color="primary"></v-progress-circular>
      </div>

      <div v-else-if="filteredMedia.length === 0" class="flex flex-col justify-center items-center h-64 opacity-50">
        <v-icon size="64" class="mb-4">mdi-folder-open-outline</v-icon>
        <span class="text-xl">No media found</span>
        <span class="text-sm mt-2">Take some screenshots or record replays in-game!</span>
      </div>

      <transition-group 
        v-else 
        name="media-grid" 
        tag="div" 
        class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6"
      >
        <v-card
          v-for="item in filteredMedia"
          :key="item.url"
          class="media-card bg-white/5 backdrop-blur-sm border border-white/10 overflow-hidden hover:scale-105 transition-transform duration-300 cursor-pointer"
          elevation="4"
          rounded="xl"
          @click="openMedia(item)"
        >
          <!-- Media Preview -->
          <div class="h-40 w-full bg-black/40 relative flex items-center justify-center">
            <template v-if="item.type === 'screenshot'">
              <v-img :src="item.url" cover class="h-full w-full"></v-img>
            </template>
            <template v-else-if="item.type === 'video'">
              <v-icon size="48" color="white" class="opacity-50">mdi-play-circle</v-icon>
            </template>
            <template v-else>
              <v-icon size="48" color="white" class="opacity-50">mdi-movie-open</v-icon>
            </template>
            
            <!-- Type Badge -->
            <div class="absolute top-2 right-2 bg-black/60 px-2 py-1 rounded-md text-xs font-semibold backdrop-blur-md">
              <v-icon size="small" class="mr-1">
                {{ item.type === 'screenshot' ? 'mdi-image' : item.type === 'video' ? 'mdi-video' : 'mdi-movie' }}
              </v-icon>
              {{ item.type.toUpperCase() }}
            </div>
          </div>

          <!-- Media Info -->
          <v-card-text class="pt-3 pb-2 px-4 flex flex-col h-24 justify-between">
            <div class="font-semibold text-sm truncate" :title="item.name">{{ item.name }}</div>
            <div class="flex justify-between items-center mt-2">
              <span class="text-xs opacity-70">{{ formatSize(item.size) }}</span>
              <span class="text-xs opacity-70">{{ formatDate(item.lastModified) }}</span>
            </div>
            <!-- Action buttons -->
            <div class="flex justify-end mt-2 space-x-2">
              <v-btn icon="mdi-folder-open" variant="text" size="x-small" @click.stop="showInFolder(item)"></v-btn>
              <v-btn icon="mdi-delete" color="error" variant="text" size="x-small" @click.stop="deleteMediaItem(item)"></v-btn>
            </div>
          </v-card-text>
        </v-card>
      </transition-group>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useService } from '@/composables'
import { InstanceMediaServiceKey, MediaItem } from '@haze/runtime-api'
import { useService as useServiceCore } from '@/composables/service'
import { injection } from '@/util/inject'
import { kInstance } from '@/composables/instance'
import MediaFilterBar from '../components/MediaFilterBar.vue'
import { getExpectedSize } from '@/util/size'
const { getMedia, showMedia, deleteMedia } = useServiceCore(InstanceMediaServiceKey)
// Use the reactive kInstance composable to get the current instance path
const { path: instancePath } = injection(kInstance)

const mediaList = ref<MediaItem[]>([])
const loading = ref(false)
const activeFilter = ref('All')
const searchQuery = ref('')

const loadMedia = async () => {
  if (!instancePath.value) return
  loading.value = true
  try {
    mediaList.value = await getMedia(instancePath.value)
  } catch (e) {
    console.error('Failed to load media:', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadMedia()
})

const filteredMedia = computed(() => {
  let result = mediaList.value

  if (activeFilter.value !== 'All') {
    result = result.filter(item => {
      if (activeFilter.value === 'Screenshot') return item.type === 'screenshot'
      if (activeFilter.value === 'Video') return item.type === 'video'
      if (activeFilter.value === 'Replay') return item.type === 'replay'
      return true
    })
  }

  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    result = result.filter(item => item.name.toLowerCase().includes(q))
  }

  return result
})

const formatSize = (bytes: number) => getExpectedSize(bytes)
const formatDate = (ts: number) => new Date(ts).toLocaleDateString()

const openMedia = (item: MediaItem) => {
  // If it's a screenshot, maybe open a dialog. For now, open folder.
  showMedia(item.url)
}

const showInFolder = (item: MediaItem) => {
  showMedia(item.url)
}

const deleteMediaItem = async (item: MediaItem) => {
  if (confirm(`Are you sure you want to delete ${item.name}?`)) {
    const success = await deleteMedia(item.url)
    if (success) {
      mediaList.value = mediaList.value.filter(m => m.url !== item.url)
    }
  }
}
</script>

<style scoped>
.media-grid-enter-active,
.media-grid-leave-active {
  transition: all 0.5s ease;
}
.media-grid-enter-from,
.media-grid-leave-to {
  opacity: 0;
  transform: translateY(30px) scale(0.95);
}
.media-grid-leave-active {
  position: absolute;
}
</style>
