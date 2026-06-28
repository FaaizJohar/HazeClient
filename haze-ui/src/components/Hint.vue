<template>
  <div
    class="h-full w-full overflow-hidden"
    :class="{ 'absoluted': absolute }"
    @click="$emit('click', $event)"
  >
    <div
      class="flex h-full items-center justify-center"
    >
      <div
        class="headline text-button select-none text-center flex flex-col items-center gap-2 hint-content"
      >
        <v-icon
          :style="{ 'font-size' : `${size}px`, 'color': color }"
          style="display: block"
        >
          {{ icon }}
        </v-icon>
        {{ text }}
        <slot />
      </div>
    </div>
  </div>
</template>

<script lang=ts setup>

defineProps({
  absolute: {
    type: Boolean,
    default: false,
  },
  color: {
    type: String,
    default: '',
  },
  text: {
    type: String,
    default: '',
  },
  icon: {
    type: String,
    default: '',
  },
  size: {
    type: Number,
    default: 50,
  },
  secondary: {
    type: Boolean,
    default: false,
  },
})
</script>

<style scoped>
.absoluted {
  position: absolute;
  left: 0;
  top: 0;
}

.hint-content {
  background: rgba(var(--v-theme-surface), 0.3);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.05);
  padding: 48px;
  border-radius: 24px;
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.15);
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}
.hint-content:hover {
  transform: translateY(-2px);
  background: rgba(var(--v-theme-surface), 0.4);
  box-shadow: 0 20px 48px rgba(0, 0, 0, 0.2);
}
</style>
