<template>
  <div class="haze-library w-full h-full p-8 flex flex-col gap-6 overflow-y-auto">
    <div class="flex items-center justify-between">
      <h1 class="text-4xl font-bold">Library</h1>
      <v-btn color="primary" @click="showAddInstance"><v-icon left>add</v-icon> New Instance</v-btn>
    </div>

    <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6">
      <div 
        v-for="inst in instances" 
        :key="inst.path"
        class="haze-library-item flex flex-col p-4 rounded-xl border border-white/10 bg-black/40 hover:bg-black/60 transition cursor-pointer"
        :class="{ 'ring-2 ring-primary': selectedInstance === inst.path }"
        @click="selectInstance(inst.path)"
      >
        <v-img :src="inst.icon" class="rounded-lg mb-3 bg-white/5 aspect-square" cover />
        <span class="font-semibold truncate">{{ inst.name || 'Haze Client' }}</span>
        <span class="text-sm text-white/50">{{ inst.runtime.minecraft }}</span>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { injection } from '@/util/inject'
import { kInstances } from '@/composables/instances'
import { useDialog } from '@/composables/dialog'
import { AddInstanceDialogKey } from '@/composables/instanceTemplates'

const { instances, selectedInstance } = injection(kInstances)
const { show: showAddInstance } = useDialog(AddInstanceDialogKey)

const selectInstance = (path: string) => {
  selectedInstance.value = path
}
</script>
