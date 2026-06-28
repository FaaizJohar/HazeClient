<template>
  <Teleport to="body">
    <Transition name="haze-dialog">
      <div v-if="modelValue" class="haze-dialog-overlay" @click.self="closeOnOutside ? $emit('update:modelValue', false) : null">
        <div class="haze-dialog-content">
          <div class="haze-dialog-header">
            <h3>{{ title }}</h3>
            <button class="haze-dialog-close" @click="$emit('update:modelValue', false)">
              <v-icon>close</v-icon>
            </button>
          </div>
          <div class="haze-dialog-body">
            <slot></slot>
          </div>
          <div v-if="$slots.footer" class="haze-dialog-footer">
            <slot name="footer"></slot>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script lang="ts" setup>
defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  title: {
    type: String,
    default: ''
  },
  closeOnOutside: {
    type: Boolean,
    default: true
  }
})
defineEmits(['update:modelValue'])
</script>

<style scoped>
.haze-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(8px);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.haze-dialog-content {
  background: rgba(25, 25, 25, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  width: 100%;
  max-width: 500px;
  box-shadow: 0 24px 48px rgba(0, 0, 0, 0.4);
  color: #fff;
  display: flex;
  flex-direction: column;
}

.haze-dialog-header {
  padding: 20px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.haze-dialog-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.haze-dialog-close {
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  border-radius: 50%;
  padding: 4px;
  transition: all 0.2s ease;
}

.haze-dialog-close:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.haze-dialog-body {
  padding: 24px;
}

.haze-dialog-footer {
  padding: 16px 24px;
  background: rgba(0, 0, 0, 0.2);
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  border-bottom-left-radius: 16px;
  border-bottom-right-radius: 16px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* Transitions */
.haze-dialog-enter-active,
.haze-dialog-leave-active {
  transition: opacity 0.3s ease;
}

.haze-dialog-enter-active .haze-dialog-content,
.haze-dialog-leave-active .haze-dialog-content {
  transition: transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.haze-dialog-enter-from,
.haze-dialog-leave-to {
  opacity: 0;
}

.haze-dialog-enter-from .haze-dialog-content,
.haze-dialog-leave-to .haze-dialog-content {
  transform: scale(0.9);
}
</style>
