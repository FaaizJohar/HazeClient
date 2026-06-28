<template>
  <div class="haze-card" :class="[{ 'haze-card-hoverable': hoverable }]">
    <div v-if="$slots.header" class="haze-card-header">
      <slot name="header"></slot>
    </div>
    <div class="haze-card-body">
      <slot></slot>
    </div>
    <div v-if="$slots.footer" class="haze-card-footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script lang="ts" setup>
defineProps({
  hoverable: {
    type: Boolean,
    default: false
  }
})
</script>

<style scoped>
.haze-card {
  background: rgba(20, 20, 20, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  backdrop-filter: blur(20px);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  color: #fff;
}

.haze-card-hoverable:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 255, 255, 0.1);
}

.haze-card-header {
  padding: 16px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  font-weight: 600;
}

.haze-card-body {
  padding: 20px;
  flex-grow: 1;
}

.haze-card-footer {
  padding: 16px 20px;
  background: rgba(0, 0, 0, 0.2);
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}
</style>
