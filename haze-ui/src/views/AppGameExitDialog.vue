<template>
  <v-dialog
    v-model="data.isShown"
  >
    <v-card class="visible-scroll flex max-h-[80vh] flex-col overflow-auto">
      <v-toolbar color="error">
        <v-toolbar-title
          class="white--text"
        >
          {{ data.isCrash ? t('launchFailed.crash') : t('launchFailed.title') }}
        </v-toolbar-title>
        <v-spacer />
        <v-toolbar-items v-if="!data.launcherError">
          <v-btn
            @click="openFolder"
           variant="text">
            {{ data.isCrash ? t('instance.openCrashReportFolder') : t('instance.openLogFolder') }}
          </v-btn>
        </v-toolbar-items>
        <v-btn
          icon
          @click="data.isShown=false"
        >
          <v-icon>close</v-icon>
        </v-btn>
      </v-toolbar>
      <v-card-text class="grid grid-cols-12 overflow-auto gap-4">
        <div class="col-span-9 flex flex-col overflow-auto mt-4">
          <div
            v-if="data.elyByWarning"
            class="mb-4 rounded bg-orange-100 dark:bg-orange-900 p-4 border-s-4 border-orange-500"
          >
            <div class="flex items-center gap-2 mb-2">
              <v-icon color="orange">
                warning
              </v-icon>
              <span class="font-bold">{{ t('launchFailed.elyByAuthlib.title') }}</span>
            </div>
            <div class="text-sm">
              {{ t('launchFailed.elyByAuthlib.description', { version: data.elyByMinecraftVersion }) }}
            </div>
            <div class="text-sm mt-2">
              {{ t('launchFailed.elyByAuthlib.suggestion') }}
            </div>
          </div>
          <!-- Crash Report Analyzer (Missing Feature 1) -->
          <div v-if="data.crashAnalysis && data.crashAnalysis.length > 0" class="mb-4 rounded bg-red-100 dark:bg-red-900 p-4 border-s-4 border-red-500">
            <div class="flex items-center gap-2 mb-2">
              <v-icon color="red">error</v-icon>
              <span class="font-bold">Detected Crash Causes</span>
            </div>
            <ul class="list-disc pl-5">
              <li v-for="analysis in data.crashAnalysis" :key="analysis">{{ analysis }}</li>
            </ul>
          </div>
          <div
            v-if="data.errorLog"
          >
            {{ data.launcherError ? t('launchFailed.failedToLaunch') : data.isCrash ? t(`launchFailed.crash`) : t(`launchFailed.description`) }}
          </div>
          <pre
            v-if="data.errorLog"
            class="overflow-auto min-h-[200px] rounded bg-[rgba(0,0,0,0.1)] p-5 hover:bg-[rgba(0,0,0,0.2)]"
          >{{ data.errorLog }}</pre>
          <div
            v-if="!data.isCrash"
          >
            {{ t(`launchFailed.latestLog`) }}
          </div>
          <pre class="overflow-auto rounded bg-[rgba(0,0,0,0.1)] p-5 hover:bg-[rgba(0,0,0,0.2)]">{{ data.log }}</pre>
        </div>
        <AppCrashAIHint
          class="col-span-3 mt-2"
          :useCNAI="useCNAI"
          :getPrompt="getPrompt"
          :getAgentPrompt="getAgentPrompt"
          @close="data.isShown = false"
        />
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script lang=ts setup>
import AppCrashAIHint from '@/components/AppCrashAIHint.vue'
import { useService } from '@/composables'
import { kEnvironment } from '@/composables/environment'
import { kInstance } from '@/composables/instance'
import { kInstanceLaunch } from '@/composables/instanceLaunch'
import { kSettingsState } from '@/composables/setting'
import { getCrashPrompt, getCrashAgentPrompt, toVirtualInstancePath } from '@/util/crashPrompt'
import { injection } from '@/util/inject'
import { BaseServiceKey, InstanceLogServiceKey, LaunchServiceKey } from '@haze/runtime-api'

const data = reactive({
  isShown: false,
  log: '',
  isCrash: false,
  isServer: false,
  launcherError: false,
  crashReportLocation: '',
  errorLog: '',
  elyByWarning: false,
  elyByMinecraftVersion: '',
  crashAnalysis: [] as string[],
})
watch(() => data.isShown, (isShown) => {
  if (!isShown) {
    data.log = ''
    data.isCrash = false
    data.isServer = false
    data.launcherError = false
    data.crashReportLocation = ''
    data.errorLog = ''
    data.elyByWarning = false
    data.elyByMinecraftVersion = ''
    data.crashAnalysis = []
  }
})
const { t } = useI18n()
const { path } = injection(kInstance)
const { getLogContent, getCrashReportContent, getServerLogContent, showLog, showServerLog } = useService(InstanceLogServiceKey)
const { on } = useService(LaunchServiceKey)
const { showItemInDirectory } = useService(BaseServiceKey)
const { error } = injection(kInstanceLaunch)

watch(error, (e) => {
  if (!e) return
  data.launcherError = true
  data.errorLog = JSON.stringify(e, null, 2)
})
async function displayLog() {
  const log = data.isServer
    ? await getServerLogContent(path.value, 'latest.log')
    : await getLogContent(path.value, 'latest.log')
  data.log = log
  data.isShown = true
}
async function displayCrash(crashReport: string | undefined) {
  const log = await getCrashReportContent(path.value, data.crashReportLocation) || crashReport || ''
  data.log = log
  data.isShown = true
}
on('minecraft-exit', (options: any) => {
  const { code, signal, crashReport, crashReportLocation, errorLog, stdLog, side, elyByAuthlibReplaced, elyByMinecraftVersion } = options
  if (!code && signal === 'SIGTERM') {
    return
  }
  
  const showSummary = JSON.parse(localStorage.getItem('gameActiveMode') || 'true')
  
  if (code !== 0 || showSummary) {
    if (code === 0) {
      // Post-session summary
      data.isShown = true
      data.isCrash = false
      data.isServer = side === 'server'
      data.errorLog = ''
      const args = options
      const durationStr = typeof args.duration === 'number' 
        ? (args.duration / 1000 / 60).toFixed(1) + ' minutes' 
        : 'unknown time'
      let msg = `Welcome back! You played for ${durationStr}.`
      if (args.averageFps || args.gcPauseCount) {
        msg += `\n\nPerformance Summary:`
        if (args.averageFps) msg += `\n- Average FPS: ${args.averageFps}`
        if (args.gcPauseCount) msg += `\n- GC Pauses: ${args.gcPauseCount}`
      }
      data.log = msg
      return
    }
    
    data.isServer = side === 'server'
    data.errorLog = `${errorLog || ''}\n${stdLog || ''}`.trim()
    
    // Check if Ely.by authlib was used
    if (elyByAuthlibReplaced) {
      data.elyByWarning = true
      data.elyByMinecraftVersion = elyByMinecraftVersion || ''
    }
    
    // Check crash analysis
    const analysis = []
    if (data.errorLog) {
      if (data.errorLog.includes('OutOfMemoryError')) analysis.push('The game ran out of memory. Try increasing the maximum RAM allocation in instance settings.')
      if (data.errorLog.includes('UnsupportedClassVersionError')) analysis.push('Incorrect Java version. Ensure you are using the correct Java version for this Minecraft version (e.g. Java 17 for MC 1.18+).')
      if (data.errorLog.includes('missing coremods') || data.errorLog.includes('java.lang.NoClassDefFoundError')) analysis.push('A required mod or library is missing. Check your mod list.')
      if (data.errorLog.includes('org.lwjgl.LWJGLException')) analysis.push('Graphics driver issue or incompatible resolution. Try updating your GPU drivers.')
    }
    data.crashAnalysis = analysis
    
    if (crashReportLocation) {
      data.crashReportLocation = crashReportLocation
      data.isCrash = true
      displayCrash(crashReport)
    } else {
      displayLog()
    }
  }
})
function openFolder() {
  if (data.isCrash) {
    showItemInDirectory(data.crashReportLocation)
  } else if (data.isServer) {
    showServerLog(path.value, 'latest.log')
  } else {
    showLog(path.value, 'latest.log')
  }
}

const env = injection(kEnvironment)
const useCNAI = computed(() => {
  return env.value?.gfw || env.value?.region === 'zh-CN'
})

const { state } = injection(kSettingsState)
function getPrompt(raw?: boolean) {
  if (raw) {
    return data.errorLog
  }
  return getCrashPrompt(useCNAI.value, data.log, data.errorLog, state.value?.locale || 'en-US')
}
function getAgentPrompt() {
  const crashPath = data.crashReportLocation ? toVirtualInstancePath(data.crashReportLocation, path.value) : undefined
  const logPath = toVirtualInstancePath(`${path.value}/${data.isServer ? 'server/logs' : 'logs'}/latest.log`, path.value)
  return getCrashAgentPrompt(data.log, data.errorLog, crashPath, logPath)
}
</script>

<style>
</style>
