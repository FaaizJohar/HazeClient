<template>
  <div class="haze-input-wrapper">
    <label v-if="label" class="haze-input-label">{{ label }}</label>
    <div class="haze-input-container">
      <div v-if="$slots.prefix" class="haze-input-prefix">
        <slot name="prefix"></slot>
      </div>
      <input
        class="haze-input"
        :type="type"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
        @focus="focused = true"
        @blur="focused = false"
      />
      <div v-if="$slots.suffix" class="haze-input-suffix">
        <slot name="suffix"></slot>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'

defineProps({
  modelValue: {
    type: [String, Number],
    default: ''
  },
  label: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: ''
  },
  type: {
    type: String,
    default: 'text'
  },
  disabled: {
    type: Boolean,
    default: false
  }
})
defineEmits(['update:modelValue'])

const focused = ref(false)
</script>

<style scoped>
.haze-input-wrapper {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
}

.haze-input-label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

.haze-input-container {
  display: flex;
  align-items: center;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 0 12px;
  transition: all 0.2s ease;
  overflow: hidden;
}

.haze-input-container:focus-within {
  border-color: var(--primary, #10b981);
  box-shadow: 0 0 0 2px rgba(16, 185, 129, 0.2);
}

.haze-input {
  flex-grow: 1;
  background: transparent;
  border: none;
  color: #fff;
  padding: 10px 0;
  font-family: 'Inter', sans-serif;
  font-size: 14px;
  outline: none;
  min-width: 0;
}

.haze-input::placeholder {
  color: rgba(255, 255, 255, 0.3);
}

.haze-input:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.haze-input-prefix {
  margin-right: 8px;
  color: rgba(255, 255, 255, 0.5);
  display: flex;
  align-items: center;
}

.haze-input-suffix {
  margin-left: 8px;
  color: rgba(255, 255, 255, 0.5);
  display: flex;
  align-items: center;
}
</style>
