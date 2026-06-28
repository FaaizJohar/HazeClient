<template>
  <v-chip v-if="mounted"
    filter
    :disabled="disabled"
    variant="outlined"
    label
    @click="disabled || $emit('click', value)"
  >
    <template #prepend>
      <v-avatar size="20" class="mr-2">
        <v-img :src="value.iconUrl" />
      </v-avatar>
    </template>
    {{ tCategory(value.name) }}
  </v-chip>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
const mounted = ref(false)
onMounted(() => {
  mounted.value = true
})
import type { ModCategory } from '@haze/curseforge'
import { useCurseforgeCategoryI18n } from '@/composables/curseforge'
defineProps<{
  disabled?: boolean
  value: ModCategory
}>()
const tCategory = useCurseforgeCategoryI18n()
defineEmits(['click'])
</script>
