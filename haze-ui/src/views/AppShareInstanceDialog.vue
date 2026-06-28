<template>
  <v-dialog
    v-model="isShown"
    width="800"
    :persistent="false"
    transition="fade-transition"
    content-class="elevation-0"
  >
    <div class="flex w-full max-h-[85vh] flex-col overflow-hidden bg-white dark:bg-[#1a1a1a] rounded-2xl shadow-2xl">
      <!-- Header -->
      <div class="flex items-center px-6 pt-6 pb-4">
        <div class="flex items-center gap-3 flex-grow">
          <div
            class="w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0"
            :style="{ backgroundColor: sharing ? 'rgba(var(--v-theme-primary), 0.12)' : 'rgba(var(--v-theme-info), 0.12)' }"
          >
            <v-icon size="22" :color="sharing ? 'primary' : 'info'">
              {{ sharing ? 'share' : 'download' }}
            </v-icon>
          </div>
          <div class="text-base font-bold tracking-tight" style="color: rgba(var(--v-theme-on-surface), 0.9);">
            {{ sharing ? t('AppShareInstanceDialog.shareTitle') : t('AppShareInstanceDialog.downloadTitle') }}
          </div>
        </div>
        <v-btn
          icon="close"
          variant="text"
          size="small"
          @click="onCancelShare"
        />
      </div>

      <v-divider class="mx-6 opacity-20" />

      <!-- Content -->
      <div
        ref="scrollElement"
        class="flex-1 min-h-0 overflow-y-auto invisible-scroll px-6 pb-6 pt-4 flex flex-col gap-5"
      >
        <p class="text-sm opacity-80 mb-2">
          {{ sharing ? t('AppShareInstanceDialog.description') : t('AppShareInstanceDialog.downloadDescription', { name }) }}
          <template v-if="!sharing">
            <br>
            {{ t('AppShareInstanceDialog.alterDownloadDescription') }}
          </template>
        </p>

        <!-- Base Info -->
        <div class="surface-panel p-4">
          <div class="mb-3 flex items-center gap-2">
            <v-icon size="18" color="primary">info</v-icon>
            <span class="text-sm font-semibold opacity-80">{{ t('AppShareInstanceDialog.baseInfo') }}</span>
          </div>
          <div class="grid grid-cols-2 sm:grid-cols-3 gap-3 mb-3">
            <v-text-field
              variant="outlined"
              density="compact"
              hide-details
              :model-value="minecraft"
              label="Minecraft"
              readonly
            >
              <template #prepend-inner>
                <img :src="BuiltinImages.minecraft" width="24" class="mr-2" />
              </template>
            </v-text-field>
            <v-text-field
              v-if="forge"
              variant="outlined"
              density="compact"
              hide-details
              label="Forge"
              :model-value="forge"
              readonly
            >
              <template #prepend-inner>
                <img :src="BuiltinImages.forge" width="24" class="mr-2" />
              </template>
            </v-text-field>
            <v-text-field
              v-if="fabricLoader"
              variant="outlined"
              density="compact"
              hide-details
              label="Fabric"
              :model-value="fabricLoader"
              readonly
            >
              <template #prepend-inner>
                <img :src="BuiltinImages.fabric" width="24" class="mr-2" />
              </template>
            </v-text-field>
            <v-text-field
              v-if="quiltLoader"
              variant="outlined"
              density="compact"
              hide-details
              label="Quilt"
              :model-value="quiltLoader"
              readonly
            >
              <template #prepend-inner>
                <img :src="BuiltinImages.quilt" width="24" class="mr-2" />
              </template>
            </v-text-field>
            <v-text-field
              v-if="neoForged"
              variant="outlined"
              density="compact"
              hide-details
              label="NeoForge"
              :model-value="neoForged"
              readonly
            >
              <template #prepend-inner>
                <img :src="BuiltinImages.neoForged" width="24" class="mr-2" />
              </template>
            </v-text-field>
          </div>
          
          <div v-if="vmOptions.length > 0" class="mb-3">
            <v-text-field
              :model-value="vmOptions.join(' ')"
              readonly
              density="compact"
              variant="outlined"
              hide-details
              :label="t('instance.vmOptions')"
            />
          </div>
          <div v-if="mcOptions.length > 0">
            <v-text-field
              :model-value="mcOptions.join(' ')"
              readonly
              density="compact"
              variant="outlined"
              hide-details
              :label="t('instance.mcOptions')"
            />
          </div>
        </div>

        <!-- Files Selection -->
        <div class="surface-panel p-4">
          <div class="mb-3 flex items-center gap-2">
            <v-icon size="18" color="primary">folder_open</v-icon>
            <span class="text-sm font-semibold opacity-80">
              {{ sharing ? t('AppShareInstanceDialog.filesToShare') : t('AppShareInstanceDialog.filesToDownload') }}
            </span>
          </div>

          <v-skeleton-loader
            v-if="loading"
            class="flex flex-col gap-3 overflow-auto"
            type="list-item-avatar-two-line, list-item-avatar-two-line, list-item-avatar-two-line"
          />
          <InstanceManifestFileTree
            v-else
            v-model="selected"
            selectable
            multiple
            :scroll-element="scrollElement"
          />
        </div>
      </div>

      <v-divider class="mx-6 opacity-20" />

      <!-- Footer -->
      <div class="flex items-center px-6 py-4 gap-4">
        <v-btn
          variant="text"
          rounded="pill"
          @click="onCancelShare"
        >
          {{ sharing ? t('AppShareInstanceDialog.cancelShare') : t('shared.cancel') }}
        </v-btn>
        
        <v-spacer />
        
        <template v-if="sharing">
          <v-btn
            color="primary"
            variant="flat"
            rounded="pill"
            @click="onShareInstance"
          >
            <v-icon start size="18">share</v-icon>
            {{ t('AppShareInstanceDialog.share') }}
          </v-btn>
        </template>
        <template v-else>
          <v-btn
            variant="tonal"
            rounded="pill"
            @click="onCreateInstance"
          >
            <v-icon start size="18">add</v-icon>
            {{ t('instances.add') }}
          </v-btn>
          <v-btn
            color="primary"
            variant="flat"
            rounded="pill"
            @click="onDownloadInstance"
          >
            <v-icon start size="18">download</v-icon>
            {{ t('shared.download') }}
          </v-btn>
        </template>
      </div>
    </div>
  </v-dialog>
</template>
<script lang="ts" setup>
import { useService } from '@/composables'
import { kInstance } from '@/composables/instance'
import { provideFileNodes, useInstanceFileNodesFromLocal } from '@/composables/instanceFileNodeData'
import { AddInstanceDialogKey } from '@/composables/instanceTemplates'
import { injection } from '@/util/inject'
import { InstanceInstallServiceKey, InstanceManifest, InstanceManifestServiceKey, PeerServiceKey } from '@haze/runtime-api'
import { Ref } from 'vue'
import InstanceManifestFileTree from '../components/InstanceManifestFileTree.vue'
import { useDialog } from '../composables/dialog'
import { useNotifier } from '../composables/notifier'
import { BuiltinImages } from '../constant'

const { isShown, parameter } = useDialog('share-instance')

const { installInstanceFiles } = useService(InstanceInstallServiceKey)
const { getInstanceManifest } = useService(InstanceManifestServiceKey)
const { shareInstance } = useService(PeerServiceKey)
const { path, name } = injection(kInstance)
const { t } = useI18n()
const { subscribeTask } = useNotifier()

const sharing = computed(() => isShown.value && !parameter.value)
/**
 * The sharing user name. Only for sharing == false
 */
const currentUser = ref('')
const manifest: Ref<InstanceManifest | undefined> = shallowRef(undefined)
const selected = ref([] as string[])

provideFileNodes(useInstanceFileNodesFromLocal(computed(() => manifest.value?.files || [])))

const minecraft = computed(() => manifest.value?.runtime.minecraft)
const forge = computed(() => manifest.value?.runtime.forge)
const fabricLoader = computed(() => manifest.value?.runtime.fabricLoader)
const quiltLoader = computed(() => manifest.value?.runtime.quiltLoader)
const neoForged = computed(() => manifest.value?.runtime.neoForged)
const optifine = computed(() => manifest.value?.runtime.optifine)
const mcOptions = computed(() => manifest.value?.mcOptions || [])
const vmOptions = computed(() => manifest.value?.vmOptions || [])
const loading = ref(false)

const scrollElement = ref<HTMLElement | null>(null)

const onCancelShare = () => {
  shareInstance({ manifest: undefined, instancePath: path.value })
  isShown.value = false
}

const onShareInstance = () => {
  if (manifest.value) {
    const man = { ...manifest.value }
    const allow = new Set(selected.value)
    man.files = man.files.filter(f => allow.has(f.path))
    subscribeTask(shareInstance({ manifest: man, instancePath: path.value }), t('AppShareInstanceDialog.shareNotifyTitle'))
    isShown.value = false
  }
}

const isNoUpstreamOrSameUpstream = computed(() => {

})

const onDownloadInstance = () => {
  if (manifest.value) {
    const man = manifest.value
    let files = man.files
    const allow = new Set(selected.value)
    files = files.filter(f => allow.has(f.path))

    subscribeTask(installInstanceFiles({
      path: path.value,
      files,
      upstream: {
        type: 'peer',
        id: '',
      }
    }), t('AppShareInstanceDialog.downloadNotifyTitle', { user: currentUser.value }))

    isShown.value = false
  }
}

const { show } = useDialog(AddInstanceDialogKey)
const onCreateInstance = () => {
  if (manifest.value) {
    show({
      format: 'manifest',
      manifest: manifest.value,
    })
  }
}

watch(isShown, async (shown) => {
  if (shown) {
    windowController.focus()
    if (parameter.value) {
      manifest.value = parameter.value as any
    } else {
      loading.value = true
      manifest.value = await getInstanceManifest({ path: path.value }).finally(() => { loading.value = false })
    }
  }
})
</script>
