const fs = require('fs');

const files = [
  "D:\\haze-client\\haze-keystone-ui\\src\\components\\AppCopyChip.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\components\\AvatarChip.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\components\\CategoryChip.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\components\\CurseforgeCategoryChip.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\components\\FilterCard.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\components\\InstanceItem.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\components\\InstanceVersionShiftAlert.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\components\\MarketProjectDetail.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\components\\MarketProjectDetailVersion.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\components\\MarketTextField.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\components\\MarketTextFieldWithMenu.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\components\\ModrinthCategoryChip.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\components\\SaveMapRenderer.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\components\\StoreGallery.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\components\\StoreProjectInstallVersionDialog.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\components\\UserCardAddYggdrasilService.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\components\\VersionMenu.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\views\\AppCommandPalette.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\views\\AppLaunchServerDialog.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\views\\AppTaskDialogTaskView.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\views\\BaseSettingGlobalLabel.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\views\\BaseSettingJava.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\views\\BaseSettingJavaList.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\views\\BaseSettingLaunch.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\views\\BaseSettingServerItem.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\views\\HomeInstanceInstallDialog.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\views\\HomeLogDialog.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\views\\HomeScreenshotCard.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\views\\HomeUnresolvedFilesDialog.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\views\\HomeWorldCard.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\views\\ModIncompatibileDialog.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\views\\ModLabels.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\views\\Multiplayer.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\views\\ResourcePackItem.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\views\\SettingGeneral.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\views\\SettingGlobalUI.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\views\\SettingUpdate.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\views\\StoreEntry.vue",
  "D:\\haze-client\\haze-keystone-ui\\src\\windows\\browser\\AppCard.vue"
];

for (const file of files) {
  let content = fs.readFileSync(file, 'utf8');
  let originalContent = content;
  
  // Replace v-if
  content = content.replace(/<v-chip([^>]*?)v-if="([^"]*)"([^>]*)>/gi, (match, p1, p2, p3) => {
    if (p2.includes('mounted')) return match;
    return `<v-chip${p1}v-if="mounted && (${p2})"${p3}>`;
  });
  
  // Add v-if where it lacks it
  // Ensure we don't match <v-chip-group> or <v-chip> with v-else or v-else-if
  content = content.replace(/<v-chip(\s+(?:(?!\bv-(?:if|else|else-if)\b)[^>])*)>/gi, '<v-chip v-if="mounted"$1>');
  content = content.replace(/<v-chip\s*>/gi, '<v-chip v-if="mounted">');

  if (content !== originalContent) {
    if (!content.includes('const mounted = ref(')) {
      const scriptRegex = /<script\b[^>]*\bsetup\b[^>]*>/;
      content = content.replace(scriptRegex, (match) => {
        return `${match}\nimport { ref, onMounted } from 'vue'\nconst mounted = ref(false)\nonMounted(() => {\n  mounted.value = true\n})`;
      });
    }
    fs.writeFileSync(file, content);
    console.log(`Updated ${file}`);
  }
}
