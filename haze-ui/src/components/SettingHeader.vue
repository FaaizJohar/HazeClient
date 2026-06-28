<template>
  <div
    class="v-heading w-full select-none"
  >
    <h3 class="xtext-h5">
      <v-icon color="primary" start v-if="icon">{{ icon }}</v-icon>
      {{ title }}
    </h3>
    <v-list-subheader v-if="subtitle" class="p-0 h-[1.25rem]">
      {{ subtitle }}
    </v-list-subheader>
  </div>
</template>
<script setup lang="ts">
defineProps<{
  title: string
  icon?: string
  subtitle?: string
}>()
</script>
<style scoped>
  .v-heading {
    display: inline-block;
    top: 0;
    padding: 0 .25rem .5rem 0.1rem;
    z-index: 3;
    margin-bottom: 0.5rem;
  }
  .xtext-h5 {
    font-size: 1.5rem !important;
    display: flex;
    font-family: var(--font-display) !important;
    font-weight: 700;
    letter-spacing: normal !important;
    text-transform: none !important;
  }
</style>
