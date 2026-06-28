<template>
  <v-dialog
    v-model="isShown"
    max-width="900"
    content-class="elevation-0"
  >
    <v-card class="bg-white dark:bg-[#1a1a1a] rounded-2xl overflow-hidden shadow-2xl">
      <!-- Header -->
      <div class="px-8 pt-8 pb-4">
        <div class="flex items-center gap-4">
          <div
            class="w-12 h-12 rounded-xl flex items-center justify-center flex-shrink-0"
            style="background-color: rgba(var(--v-theme-primary), 0.12)"
          >
            <v-icon size="26" color="primary">ios_share</v-icon>
          </div>
          <div>
            <h2 class="text-xl font-bold tracking-tight">Export Modpack</h2>
            <p class="text-sm opacity-70 mt-1">Choose how you want to share or backup this instance.</p>
          </div>
        </div>
      </div>

      <v-card-text class="px-8 pb-8 pt-4">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          
          <!-- Export Modpack (.mrpack / .zip) -->
          <div
            data-testid="export-hub-modpack"
            class="group relative flex flex-col p-6 rounded-2xl border border-gray-300 dark:border-white/10 bg-gray-100 dark:bg-[#212121] hover:bg-gray-200 dark:hover:bg-[#2a2a2a] cursor-pointer transition-all hover:-translate-y-1 hover:shadow-lg hover:border-primary/50"
            @click="onSelectModpack"
          >
            <div class="w-12 h-12 rounded-xl bg-orange-500/20 flex items-center justify-center mb-4">
              <v-icon color="orange" size="24">inventory_2</v-icon>
            </div>
            <h3 class="text-base font-bold mb-2 group-hover:text-primary transition-colors">Modpack File</h3>
            <p class="text-sm opacity-70 mb-4 flex-grow">Export as a standard .mrpack or .zip for CurseForge and Modrinth sharing.</p>
            <div class="flex items-center text-primary text-sm font-medium opacity-0 group-hover:opacity-100 transition-opacity">
              Configure Export <v-icon size="16" class="ml-1">arrow_forward</v-icon>
            </div>
          </div>

          <!-- Export to Server -->
          <div
            data-testid="export-hub-server"
            class="group relative flex flex-col p-6 rounded-2xl border border-gray-300 dark:border-white/10 bg-gray-100 dark:bg-[#212121] hover:bg-gray-200 dark:hover:bg-[#2a2a2a] cursor-pointer transition-all hover:-translate-y-1 hover:shadow-lg hover:border-primary/50"
            @click="onSelectServer"
          >
            <div class="w-12 h-12 rounded-xl bg-blue-500/20 flex items-center justify-center mb-4">
              <v-icon color="blue" size="24">dns</v-icon>
            </div>
            <h3 class="text-base font-bold mb-2 group-hover:text-primary transition-colors">Server Export</h3>
            <p class="text-sm opacity-70 mb-4 flex-grow">Export directly to a dedicated server folder or upload via SSH.</p>
            <div class="flex items-center text-primary text-sm font-medium opacity-0 group-hover:opacity-100 transition-opacity">
              Export to Server <v-icon size="16" class="ml-1">arrow_forward</v-icon>
            </div>
          </div>

          <!-- P2P Share -->
          <div
            data-testid="export-hub-p2p"
            class="group relative flex flex-col p-6 rounded-2xl border border-gray-300 dark:border-white/10 bg-gray-100 dark:bg-[#212121] hover:bg-gray-200 dark:hover:bg-[#2a2a2a] cursor-pointer transition-all hover:-translate-y-1 hover:shadow-lg hover:border-primary/50"
            @click="onSelectP2P"
          >
            <div class="w-12 h-12 rounded-xl bg-purple-500/20 flex items-center justify-center mb-4">
              <v-icon color="purple" size="24">wifi_tethering</v-icon>
            </div>
            <h3 class="text-base font-bold mb-2 group-hover:text-primary transition-colors">Peer to Peer</h3>
            <p class="text-sm opacity-70 mb-4 flex-grow">Share directly with friends over your local network or via invite.</p>
            <div class="flex items-center text-primary text-sm font-medium opacity-0 group-hover:opacity-100 transition-opacity">
              Start Sharing <v-icon size="16" class="ml-1">arrow_forward</v-icon>
            </div>
          </div>

        </div>
      </v-card-text>
      
      <v-card-actions class="px-8 pb-8 pt-0">
        <v-spacer />
        <v-btn
          variant="text"
          class="font-medium tracking-wide capitalize"
          rounded="pill"
          size="large"
          @click="isShown = false"
        >
          Cancel
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts" setup>
import { useDialog } from '@/composables/dialog'
import { AppExportServerDialogKey } from '@/composables/instanceExport'
import { useRouter } from 'vue-router'

const { isShown } = useDialog('export-instance-hub')
const { show: showExportServer } = useDialog(AppExportServerDialogKey)
const { show: showShareDialog } = useDialog('share-instance')

const router = useRouter()

const onSelectModpack = () => {
  isShown.value = false
  router.push('/base-setting?target=modpack')
}

const onSelectServer = () => {
  isShown.value = false
  showExportServer()
}

const onSelectP2P = () => {
  isShown.value = false
  showShareDialog()
}
</script>
