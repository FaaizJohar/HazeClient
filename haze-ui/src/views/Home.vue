<template>
  <div class="haze-home select-none flex flex-col h-full overflow-y-auto custom-scrollbar">
    <HomeCriticalError />
    
    <!-- Dynamic Hero Dashboard -->
    <HomeHero />

    <!-- Dashboard Content Area -->
    <div class="haze-dashboard-grid mx-6 mb-8">
      <!-- Quick Actions & Stats Column -->
      <div class="haze-dashboard-col-left flex flex-col gap-6">
        <HazeCard class="haze-recent-activity" hoverable>
          <template #header>
            <div class="flex items-center gap-2">
              <v-icon size="20" color="primary">history</v-icon>
              <span>Recent Activity</span>
            </div>
          </template>
          <div class="flex flex-col gap-3">
            <div class="activity-item">
              <div class="activity-icon bg-primary/20 text-primary">
                <v-icon size="16">play_arrow</v-icon>
              </div>
              <div class="activity-details">
                <span class="activity-title">Played {{ instance.name || 'Haze Client' }}</span>
                <span class="activity-time">Just now</span>
              </div>
            </div>
            <div class="activity-item">
              <div class="activity-icon bg-blue/20 text-blue">
                <v-icon size="16">extension</v-icon>
              </div>
              <div class="activity-details">
                <span class="activity-title">Updated 3 mods</span>
                <span class="activity-time">2 hours ago</span>
              </div>
            </div>
          </div>
        </HazeCard>


      </div>

      <!-- Main Content Column -->
      <div class="haze-dashboard-col-main flex flex-col gap-6">
        <!-- Media Gallery -->
        <HazeCard hoverable class="haze-gallery-card">
          <template #header>
            <div class="flex items-center justify-between w-full">
              <div class="flex items-center gap-2">
                <v-icon size="20" color="primary">photo_library</v-icon>
                <span>Media & Screenshots</span>
              </div>
              <HazeButton variant="ghost" class="text-sm px-2 py-1">View All</HazeButton>
            </div>
          </template>
          <div class="haze-gallery-placeholder bg-black/20 rounded-lg h-[200px] flex items-center justify-center border border-white/5">
             <span class="text-white/40 flex flex-col items-center gap-2">
               <v-icon size="32">image</v-icon>
               No recent screenshots
             </span>
          </div>
        </HazeCard>

        <!-- Modpack Upstream Info -->
        <HomeUpstreamCurseforge
          v-if="instance.upstream && instance.upstream.type === 'curseforge-modpack'"
          :id="instance.upstream.modId"
        />
        <HomeUpstreamModrinth
          v-else-if="instance.upstream && instance.upstream.type === 'modrinth-modpack'"
          :id="instance.upstream.projectId"
        />
        <HomeUpstreamFeedTheBeast
          v-else-if="instance.upstream && instance.upstream.type === 'ftb-modpack'"
          :id="instance.upstream.id"
        />

        <!-- Latest News -->
        <HazeCard hoverable class="haze-news-card">
          <template #header>
            <div class="flex items-center gap-2">
              <v-icon size="20" color="primary">article</v-icon>
              <span>Haze Client Updates</span>
            </div>
          </template>
          <div class="flex flex-col gap-3">
             <div class="news-item">
               <div class="news-date">v2.0.0</div>
               <div class="news-content">
                 <h4>Haze Client V2 is here!</h4>
                 <p>Experience the completely redesigned interface and ultra-low resource engine.</p>
               </div>
             </div>
          </div>
        </HazeCard>
      </div>
      
      <!-- Right Sidebar Column -->
      <div class="haze-dashboard-col-right flex flex-col gap-6">
         <!-- Favorite Instances -->
         <HazeCard hoverable class="haze-favorites-card">
          <template #header>
            <div class="flex items-center gap-2">
              <v-icon size="20" color="primary">star</v-icon>
              <span>Favorite Instances</span>
            </div>
          </template>
          <div class="flex flex-col gap-2">
             <div class="favorite-item flex items-center gap-3 p-2 rounded-lg hover:bg-white/5 transition cursor-pointer">
               <v-avatar size="32" class="bg-primary/20">
                 <v-icon size="16" color="primary">layers</v-icon>
               </v-avatar>
               <div class="flex flex-col">
                 <span class="text-sm font-semibold">Vanilla Survival</span>
                 <span class="text-xs text-white/50">1.20.4</span>
               </div>
             </div>
          </div>
        </HazeCard>
      </div>
    </div>

    <MediaGalleryDialog />
    <AppExportInstanceDialog />
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, provide, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useDialog } from '@/composables/dialog'
import { useGlobalDrop } from '@/composables/dropHandler'
import { kInstance } from '@/composables/instance'
import { kUpstream } from '@/composables/instanceUpdate'
import { kCompact } from '@/composables/scrollTop'
import { injection } from '@/util/inject'
import HomeCriticalError from './HomeCriticalError.vue'
import HomeHero from './HomeHero.vue'
import HomeUpstreamCurseforge from './HomeUpstreamCurseforge.vue'
import HomeUpstreamFeedTheBeast from './HomeUpstreamFeedTheBeast.vue'
import HomeUpstreamModrinth from './HomeUpstreamModrinth.vue'
import MediaGalleryDialog from '@/components/MediaGalleryDialog.vue'
import AppExportInstanceDialog from './AppExportInstanceDialog.vue'
import HazeCard from '@/components/haze/HazeCard.vue'
import HazeButton from '@/components/haze/HazeButton.vue'

const { instance } = injection(kInstance)
provide(
  kUpstream,
  computed(() => ({
    upstream: instance.value.upstream,
    minecraft: instance.value.runtime.minecraft,
  })),
)

const compact = injection(kCompact)
onMounted(() => {
  compact.value = false
})

const { show } = useDialog('HomeDropModpackDialog')

useGlobalDrop({
  onDrop: async (e) => {
    const files = e.files
    const file = files?.[0]
    if (file) {
      const ext = file.name.split('.').pop()
      if (ext === 'zip' || ext === 'mrpack') {
        show(file.path)
        return
      }
    }
  },
})

const scrollElement = ref(null as HTMLElement | null)
provide('scrollElement', scrollElement)
</script>

<style scoped>
.haze-home {
  background-color: transparent;
}

.haze-dashboard-grid {
  display: grid;
  grid-template-columns: 300px 1fr 300px;
  gap: 24px;
}

@media (max-width: 1400px) {
  .haze-dashboard-grid {
    grid-template-columns: 250px 1fr 250px;
  }
}

@media (max-width: 1100px) {
  .haze-dashboard-grid {
    grid-template-columns: 1fr 300px;
  }
  .haze-dashboard-col-left {
    display: none;
  }
}

@media (max-width: 800px) {
  .haze-dashboard-grid {
    grid-template-columns: 1fr;
  }
  .haze-dashboard-col-right {
    display: none;
  }
}

/* Activity Item */
.activity-item {
  display: flex;
  align-items: center;
  gap: 12px;
}
.activity-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.activity-details {
  display: flex;
  flex-direction: column;
}
.activity-title {
  font-size: 14px;
  font-weight: 500;
  color: #fff;
}
.activity-time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

/* Insight Row */
.insight-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}
.insight-row:last-child {
  border-bottom: none;
  padding-bottom: 0;
}
.insight-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
}
.insight-value {
  font-size: 14px;
  font-weight: 600;
}
.text-green {
  color: #10b981;
}

/* News Item */
.news-item {
  display: flex;
  gap: 16px;
}
.news-date {
  font-size: 12px;
  font-weight: 700;
  color: var(--primary, #10b981);
  padding: 4px 8px;
  background: rgba(16, 185, 129, 0.1);
  border-radius: 6px;
  height: fit-content;
}
.news-content h4 {
  font-size: 14px;
  font-weight: 600;
  margin: 0 0 4px 0;
}
.news-content p {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
  line-height: 1.4;
}

/* Scrollbar */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(128, 128, 128, 0.2);
  border-radius: 4px;
}
</style>
