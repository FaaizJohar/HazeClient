<template>
  <button
    class="haze-btn"
    :class="[`haze-btn-${variant}`, { 'haze-btn-disabled': disabled }]"
    :disabled="disabled"
    @click="$emit('click')"
  >
    <slot></slot>
  </button>
</template>

<script lang="ts" setup>
defineProps({
  variant: {
    type: String,
    default: 'primary' // 'primary', 'secondary', 'danger', 'ghost'
  },
  disabled: {
    type: Boolean,
    default: false
  }
})
defineEmits(['click'])
</script>

<style scoped>
.haze-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 8px 16px;
  border-radius: 8px;
  font-family: 'Inter', sans-serif;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  border: none;
  outline: none;
}

.haze-btn:active {
  transform: scale(0.95);
}

.haze-btn-primary {
  background: var(--primary, #10b981);
  color: #ffffff;
  box-shadow: 0 4px 14px rgba(16, 185, 129, 0.3);
}

.haze-btn-primary:hover {
  background: var(--primary-hover, #059669);
  box-shadow: 0 6px 20px rgba(16, 185, 129, 0.4);
}

.haze-btn-secondary {
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
}

.haze-btn-secondary:hover {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(255, 255, 255, 0.3);
}

.haze-btn-disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none !important;
  box-shadow: none !important;
}
</style>
