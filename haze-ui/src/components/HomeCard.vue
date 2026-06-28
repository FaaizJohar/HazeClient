<template>
  <v-card
    class="flex h-full flex-col home-card"
    :class="{ highlighted: highlighted }"
    :style="{
      'backdrop-filter': `blur(${blurCard}px)`,
    }"
    :color="highlighted ? 'yellow darken-2' : cardColor"
    @dragover="emit('dragover', $event)"
    @drop="onDrop"
    @dragenter="dragover += 1"
    @dragleave="dragover -= 1"
    @mouseenter="mouse += 1"
    @mouseleave="mouse -= 1"
  >
    <v-progress-linear
      v-if="refreshing"
      class="absolute left-0 bottom-0 z-20 m-0 p-0"
      indeterminate
    />
    <v-card-item v-if="title" class="pt-4 px-5 pb-2">
      <v-card-title class="font-weight-bold" style="font-size: 1.15rem; letter-spacing: -0.2px;">
        <v-icon size="small" start class="mr-2 opacity-80">
          {{ icon }}
        </v-icon>
        {{ title }}
      </v-card-title>
    </v-card-item>
    <v-card-text class="flex-grow relative pb-0 px-5">
      <template v-if="refreshing && icons.length === 0">
        <v-skeleton-loader type="paragraph" />
      </template>
      <template v-else-if="slots.default">
        <slot />
      </template>
      <template v-else>
        <!-- Standard Content / Icons -->
        <template v-if="icons.length > 0">
          <span v-if="!error" class="text-content opacity-90">
            {{ text }}
          </span>
          <span v-else class="color-red">
            <v-icon color="red" size="small"> warning </v-icon>
            {{ error.message || error }}
          </span>
          <div v-if="!globalDragover" class="mt-4 flex flex-wrap gap-2">
            <v-avatar
              v-for="a of icons"
              :key="a.name"
              v-shared-tooltip="a.name"
              :color="a.color ? a.color : !a.icon ? getColor(a.name) : undefined"
              size="32px"
              class="elevation-2"
            >
              <img
                v-if="a.icon"
                :src="a.icon"
                width="32"
                v-fallback-img="BuiltinImages.unknownServer"
                draggable="false"
              />
              <span v-else class="text-caption font-weight-bold"> {{ a.name[0]?.toUpperCase() }} </span>
            </v-avatar>
          </div>
        </template>
        <!-- Beautiful Empty State -->
        <div v-else class="w-full h-full flex flex-col items-center justify-center opacity-40 mt-2">
          <v-icon size="36" class="mb-2">{{ icon }}</v-icon>
          <div class="text-caption font-weight-medium text-center px-4">{{ text }}</div>
        </div>
      </template>
    </v-card-text>
    <v-card-actions class="justify-between px-4 pb-3" v-if="button || additionButton">
      <v-btn v-if="button" ref="btnElem" :data-testid="button.testid" @click="emit('navigate')" variant="text">
        <v-icon v-if="button.icon" start>
          {{ button.icon }}
        </v-icon>
        <span :style="{ color: isOverflowed ? 'transparent' : '' }">
          {{ button.text }}
        </span>
      </v-btn>
      <v-spacer v-else />
      <v-btn
        v-if="additionButton"
        color="primary"
        :data-testid="additionButton.testid"
        @click="emit('navigate-addition')"
        variant="text"
      >
        <v-icon class="material-icons-outlined" start>
          {{ additionButton.icon || 'add' }}
        </v-icon>
        <span>
          {{ additionButton.text }}
        </span>
      </v-btn>
    </v-card-actions>
  </v-card>
</template>
<script lang="ts" setup>
import { kDropHandler } from '@/composables/dropHandler'
import { kTheme } from '@/composables/theme'
import { BuiltinImages } from '@/constant'
import { vFallbackImg } from '@/directives/fallbackImage'
import { vSharedTooltip } from '@/directives/sharedTooltip'
import { getColor } from '@/util/color'
import { injection } from '@/util/inject'
import type { ComponentPublicInstance } from 'vue'

const btnElem = ref(null as ComponentPublicInstance | null)

const isOverflowed = computed(() => {
  const el = btnElem.value?.$el
  if (!el) {
    return
  }

  const isOverflowed = el.scrollWidth > el.clientWidth
  return isOverflowed
})

defineProps<{
  icon?: string
  title?: string
  subtitle?: string
  text: string
  button?: { text: string; icon?: string; testid?: string }
  additionButton?: { text: string; icon?: string; testid?: string }
  refreshing: boolean
  error?: any
  icons: Array<{ name: string; icon?: string; color?: string }>
}>()
const emit = defineEmits([
  'navigate',
  'drop',
  'dragover',
  'dragenter',
  'dragleave',
  'navigate-addition',
])
const { cardColor, blurCard } = injection(kTheme)

const slots = useSlots()

function onDrop(event: DragEvent) {
  emit('drop', event)
  dragover.value = 0
}

const dragover = ref(0)
const { dragover: globalDragover } = injection(kDropHandler)
const mouse = ref(0)
const highlighted = computed(() => globalDragover.value && dragover.value > 0)
</script>

<style scoped>
.highlighted {
  transform: scale(1.05);
}

.text-content {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.home-card {
  /* blur behand */
  container-type: size;
  width: 100%;
  border-radius: 10px;
}

.btn {
  display: none;
}

@container (min-width: 300px) {
  .btn {
    display: block;
  }
}
</style>
