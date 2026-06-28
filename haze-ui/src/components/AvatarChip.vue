<template>
  <v-chip v-if="mounted"
    label
    variant="outlined"
    :size="small ? 'small' : undefined"
    @click="emit('click')"
  >
    <template #prepend>
      <v-avatar start>
        <img
          :src="avatar"
        >
      </v-avatar>
    </template>
    <div v-if="text">
      {{ text }}
    </div>
  </v-chip>
</template>
<script lang="ts" setup>
import { ref, onMounted } from 'vue'
const mounted = ref(false)
onMounted(() => {
  mounted.value = true
})
defineProps<{
  small?: boolean
  avatar?: string
  text?: string
}>()
const emit = defineEmits(['click'])
</script>
