<template>
  <div class="media-filter-bar flex items-center justify-between p-4 bg-white/5 backdrop-blur-md rounded-xl border border-white/10 shadow-lg sticky top-4 z-10 mx-4">
    <div class="flex items-center space-x-2">
      <v-btn
        v-for="filter in ['All', 'Screenshot', 'Video', 'Replay']"
        :key="filter"
        :color="activeFilter === filter ? 'primary' : 'transparent'"
        :class="{'text-white': activeFilter !== filter}"
        variant="tonal"
        size="small"
        rounded="pill"
        @click="activeFilter = filter"
      >
        {{ filter }}
      </v-btn>
    </div>
    
    <div class="flex items-center space-x-4">
      <v-text-field
        v-model="searchQuery"
        prepend-inner-icon="mdi-magnify"
        label="Search media..."
        variant="solo-filled"
        density="compact"
        hide-details
        bg-color="rgba(0,0,0,0.2)"
        rounded="lg"
        class="w-64"
      ></v-text-field>
      <v-btn icon="mdi-refresh" variant="text" size="small" @click="$emit('refresh')"></v-btn>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{
  modelValue: string
  search: string
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', val: string): void
  (e: 'update:search', val: string): void
  (e: 'refresh'): void
}>()

const activeFilter = ref(props.modelValue || 'All')
const searchQuery = ref(props.search || '')

watch(activeFilter, (val) => {
  emit('update:modelValue', val)
})

watch(searchQuery, (val) => {
  emit('update:search', val)
})
</script>

<style scoped>
.media-filter-bar {
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}
</style>
