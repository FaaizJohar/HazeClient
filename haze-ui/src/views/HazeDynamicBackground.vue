<template>
  <div class="haze-dynamic-background">
    <div class="haze-gradient-sphere haze-sphere-1"></div>
    <div class="haze-gradient-sphere haze-sphere-2"></div>
    <div class="haze-gradient-sphere haze-sphere-3"></div>
    <div class="haze-noise-overlay"></div>
    <div class="haze-glass-blur" :style="{ backdropFilter: `blur(${blur}px)` }"></div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { injection } from '@/util/inject'
import { kTheme } from '@/composables/theme'

const { blur } = injection(kTheme)

</script>

<style scoped>
.haze-dynamic-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: #09090b; /* Deep obsidian base */
  z-index: 0;
}

.haze-gradient-sphere {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.6;
  animation: float 20s infinite ease-in-out alternate;
}

.haze-sphere-1 {
  width: 60vw;
  height: 60vw;
  background: radial-gradient(circle, rgba(120, 60, 255, 0.4) 0%, rgba(0, 0, 0, 0) 70%);
  top: -10vw;
  left: -10vw;
  animation-delay: 0s;
}

.haze-sphere-2 {
  width: 70vw;
  height: 70vw;
  background: radial-gradient(circle, rgba(40, 150, 255, 0.3) 0%, rgba(0, 0, 0, 0) 70%);
  bottom: -20vw;
  right: -10vw;
  animation-delay: -5s;
}

.haze-sphere-3 {
  width: 50vw;
  height: 50vw;
  background: radial-gradient(circle, rgba(255, 60, 120, 0.2) 0%, rgba(0, 0, 0, 0) 70%);
  top: 40%;
  left: 30%;
  animation-delay: -10s;
}

.haze-noise-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('data:image/svg+xml,%3Csvg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg"%3E%3Cfilter id="noiseFilter"%3E%3CfeTurbulence type="fractalNoise" baseFrequency="0.65" numOctaves="3" stitchTiles="stitch"/%3E%3C/filter%3E%3Crect width="100%25" height="100%25" filter="url(%23noiseFilter)"/%3E%3C/svg%3E');
  opacity: 0.04;
  mix-blend-mode: overlay;
  pointer-events: none;
}

.haze-glass-blur {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.2);
  pointer-events: none;
}

@keyframes float {
  0% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(5vw, 5vh) scale(1.1);
  }
  100% {
    transform: translate(-5vw, -5vh) scale(0.95);
  }
}
</style>
