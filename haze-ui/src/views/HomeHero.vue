<template>
  <div class="home-hero relative mx-3 mt-3 mb-6 rounded-3xl overflow-hidden flex flex-col justify-end">
    <!-- Blurred Background -->
    <div class="home-hero__bg absolute inset-0 z-0">
      <img
        v-if="instance.icon"
        :src="instance.icon"
        class="w-full h-full object-cover opacity-50 filter blur-[80px] scale-110 transform"
        alt="Background"
      />
      <!-- Fallback Gradient -->
      <div v-else class="w-full h-full bg-gradient-to-br from-primary/30 to-background opacity-50" />
      <!-- Overlay Gradient -->
      <div class="absolute inset-0 bg-gradient-to-t from-background via-background/40 to-transparent" />
    </div>

    <!-- Content -->
    <div class="relative z-10 p-8 flex flex-col gap-6">
      <div class="flex items-end justify-between gap-6">
        <!-- Instance Identity -->
        <div class="flex items-center gap-6">
          <v-img
            v-if="instance.icon"
            :src="instance.icon"
            class="rounded-2xl shadow-2xl shrink-0"
            width="100"
            height="100"
            cover
          />
          <div
            v-else
            class="w-[100px] h-[100px] rounded-2xl shadow-2xl flex items-center justify-center shrink-0"
            style="background-color: rgba(var(--v-theme-primary), 0.12)"
          >
            <v-icon size="48" color="primary">apps</v-icon>
          </div>
          
          <div class="flex flex-col gap-1 min-w-0">
            <h1 class="text-4xl font-display font-extrabold leading-tight truncate text-white drop-shadow-md">
              {{ instance.name || 'Haze Client' }}
            </h1>
            <p v-if="instance.author" class="text-white/70 font-medium flex items-center gap-1.5 text-sm drop-shadow">
              <v-icon size="14" class="material-icons-outlined">person</v-icon>
              {{ instance.author }}
            </p>
          </div>
        </div>

        <!-- Call to Action -->
        <div class="flex items-center gap-4 shrink-0" @mouseenter="active = true" @mouseleave="active = false">
          <HomeLaunchButtonStatus :active="active" />
          <HomeLaunchButton />
        </div>
      </div>

      <!-- Quick Stats -->
      <div class="flex items-center gap-3 overflow-x-auto custom-scrollbar pb-1">
        <!-- Minecraft Version -->
        <div class="stat-pill" v-shared-tooltip="t('minecraftVersion.name')">
          <v-icon size="16" color="green">haze:minecraft</v-icon>
          <span>{{ instance.runtime.minecraft || 'Unknown' }}</span>
        </div>

        <!-- Mod Loader -->
        <div class="stat-pill" v-if="modLoader" v-shared-tooltip="t('modrinth.modLoaders.name')">
          <v-icon size="16" class="material-icons-outlined opacity-70">extension</v-icon>
          <span class="capitalize">{{ modLoader.name }} {{ modLoader.version }}</span>
        </div>

        <!-- Playtime -->
        <div class="stat-pill" v-shared-tooltip="t('playtime')">
          <v-icon size="16" class="material-icons-outlined opacity-70">schedule</v-icon>
          <span>{{ formattedPlaytime }}</span>
        </div>
        
        <!-- Last Played -->
        <div class="stat-pill" v-if="instance.lastPlayedDate" v-shared-tooltip="t('lastPlayed')">
          <v-icon size="16" class="material-icons-outlined opacity-70">history</v-icon>
          <span>{{ formattedLastPlayed }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, ref } from 'vue'
import { injection } from '@/util/inject'
import { kInstance } from '@/composables/instance'
import { useI18n } from 'vue-i18n'
import { vSharedTooltip } from '@/directives/sharedTooltip'
import HomeLaunchButton from './HomeLaunchButton.vue'
import HomeLaunchButtonStatus from './HomeLaunchButtonStatus.vue'
import { getHumanizeDuration } from '@/util/date'

const { instance } = injection(kInstance)
const { t } = useI18n()

const active = ref(false)

const modLoader = computed(() => {
  const r = instance.value.runtime
  if (r.forge) return { name: 'Forge', version: r.forge }
  if (r.fabricLoader) return { name: 'Fabric', version: r.fabricLoader }
  if (r.quiltLoader) return { name: 'Quilt', version: r.quiltLoader }
  if (r.neoForged) return { name: 'NeoForge', version: r.neoForged }
  return undefined
})

const formattedPlaytime = computed(() => {
  if (!instance.value.playtime) return t('playtimeNotDefined')
  return getHumanizeDuration(instance.value.playtime)
})

const formattedLastPlayed = computed(() => {
  if (!instance.value.lastPlayedDate) return ''
  const date = new Date(instance.value.lastPlayedDate)
  return date.toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
})
</script>

<style scoped>
.home-hero {
  min-height: 240px;
  background-color: rgba(var(--v-theme-surface), 0.5);
  border: 1px solid rgba(255, 255, 255, 0.05);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
}

.stat-pill {
  @apply flex items-center gap-2 px-3 py-1.5 rounded-xl text-sm font-semibold whitespace-nowrap cursor-default;
  background-color: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.05);
  transition: all 0.2s ease;
}

.stat-pill:hover {
  background-color: rgba(255, 255, 255, 0.15);
  transform: translateY(-1px);
}

.custom-scrollbar::-webkit-scrollbar {
  height: 4px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
}
</style>
