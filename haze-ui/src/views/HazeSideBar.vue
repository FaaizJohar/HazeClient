<template>
  <div
    data-testid="haze-sidebar"
    role="navigation"
    class="haze-sidebar z-10 flex flex-col justify-between"
  >
    <!-- Top Section: Logo & Primary Nav -->
    <div class="sidebar__top flex flex-col items-center gap-4 pt-6">
      <!-- Haze Logo -->
      <div class="haze-logo-container mb-4">
        <v-icon size="36" color="primary">architecture</v-icon>
      </div>

      <!-- Main Navigation -->
      <nav class="flex flex-col gap-2 w-full px-3">
        <router-link
          to="/"
          class="nav-item"
          active-class="nav-item--active"
          exact
        >
          <v-icon size="24" class="nav-icon">home</v-icon>
          <span class="nav-label">Home</span>
        </router-link>

        <router-link
          to="/library"
          class="nav-item"
          active-class="nav-item--active"
        >
          <v-icon size="24" class="nav-icon">grid_view</v-icon>
          <span class="nav-label">Library</span>
        </router-link>

        <router-link
          to="/store"
          class="nav-item"
          active-class="nav-item--active"
        >
          <v-icon size="24" class="nav-icon">explore</v-icon>
          <span class="nav-label">Discover</span>
        </router-link>

        <router-link
          to="/multiplayer"
          class="nav-item"
          active-class="nav-item--active"
        >
          <v-icon size="24" class="nav-icon">hub</v-icon>
          <span class="nav-label">Servers</span>
        </router-link>
      </nav>
    </div>

    <!-- Bottom Section: User & Settings -->
    <div class="sidebar__bottom flex flex-col items-center gap-2 pb-6 px-3 w-full">
      <router-link
        to="/setting"
        class="nav-item"
        active-class="nav-item--active"
      >
        <v-icon size="24" class="nav-icon">settings</v-icon>
        <span class="nav-label">Settings</span>
      </router-link>

      <div class="sidebar__divider my-2"></div>

      <router-link
        to="/me"
        class="nav-item profile-item"
        active-class="nav-item--active"
      >
        <PlayerAvatar
          class="overflow-hidden rounded-full"
          :src="gameProfile?.textures?.SKIN?.url"
          :dimension="28"
        />
        <span class="nav-label truncate w-full text-center">{{ gameProfile?.name || 'Account' }}</span>
      </router-link>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import PlayerAvatar from '@/components/PlayerAvatar.vue'
import { kUserContext } from '@/composables/user'
import { injection } from '@/util/inject'
import { kTheme } from '@/composables/theme'

const { gameProfile } = injection(kUserContext)
const { sideBarColor } = injection(kTheme)

</script>

<style scoped>
.haze-sidebar {
  width: 90px;
  min-width: 90px;
  height: calc(100% - 24px);
  margin: 12px 0 12px 12px;
  border-radius: 24px;
  background: rgba(15, 15, 18, 0.45);
  backdrop-filter: blur(40px) saturate(200%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 16px 40px -8px rgba(0, 0, 0, 0.4);
  transition: width 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  overflow: hidden;
}

/* On hover, expand the sidebar slightly to show labels */
.haze-sidebar:hover {
  width: 220px;
  min-width: 220px;
}

.haze-logo-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(var(--v-theme-primary), 0.2), transparent);
  box-shadow: inset 0 0 0 1px rgba(var(--v-theme-primary), 0.3);
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 16px;
  height: 48px;
  padding: 0 12px;
  border-radius: 12px;
  color: rgba(255, 255, 255, 0.5);
  text-decoration: none;
  transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
  overflow: hidden;
  position: relative;
}

.nav-icon {
  flex-shrink: 0;
  transition: transform 0.2s ease, color 0.2s ease;
}

.nav-label {
  font-weight: 600;
  font-size: 15px;
  white-space: nowrap;
  opacity: 0;
  transform: translateX(-10px);
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.haze-sidebar:hover .nav-label {
  opacity: 1;
  transform: translateX(0);
}

.nav-item:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.06);
}

.nav-item--active {
  color: rgb(var(--v-theme-primary));
  background: rgba(var(--v-theme-primary), 0.1);
}
.nav-item--active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 12px;
  bottom: 12px;
  width: 4px;
  border-radius: 0 4px 4px 0;
  background: rgb(var(--v-theme-primary));
}

.profile-item {
  padding: 0 10px;
  height: 52px;
}

.sidebar__divider {
  width: 60%;
  height: 1px;
  background: rgba(255, 255, 255, 0.1);
  transition: width 0.3s ease;
}
.haze-sidebar:hover .sidebar__divider {
  width: 90%;
}
</style>
