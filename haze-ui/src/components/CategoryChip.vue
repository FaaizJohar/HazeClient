<template>
  <v-chip v-if="mounted"
    label
    :size="small ? 'small' : undefined"
    :variant="outlined ? 'outlined' : undefined"
    @click="$emit('click')"
  >
    <template #prepend>
      <v-avatar
        v-if="item.icon?.startsWith('http')"
        variant="plain"
        start
      >
        <v-img
          :src="item.icon"
        />
      </v-avatar>
      <v-avatar
        v-else-if="item.icon"
        variant="plain"
        start
        v-html="item.icon"
      />
    </template>
    {{ item.text }}
  </v-chip>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
const mounted = ref(false)
onMounted(() => {
  mounted.value = true
})

export interface CategoryChipProps {
  text: string
  id: string
  icon?: string
}

defineProps<{
  item: CategoryChipProps
  small?: boolean
  outlined?: boolean
}>()

</script>
