import {
  defineConfig,
  presetAttributify,
  presetIcons,
  presetUno,
  transformerDirectives,
  transformerVariantGroup,
} from 'unocss'

export default defineConfig({
  shortcuts: [
  ],
  theme: {
    fontFamily: {
      sans: '"Inter", "General Sans", "Segoe UI", Roboto, Helvetica, Arial, sans-serif',
      display: '"Sora", "Inter", sans-serif',
    },
    colors: {
      primary: '#6b32ec',
    },
    animation: {
      keyframes: {
        'fade-in': '{from { opacity: 0 } to { opacity: 1 }}',
        'fade-out': '{from { opacity: 1 } to { opacity: 0 }}',
        'slide-up': '{from { transform: translateY(10px); opacity: 0 } to { transform: translateY(0); opacity: 1 }}',
        'scale-in': '{from { transform: scale(0.95); opacity: 0 } to { transform: scale(1); opacity: 1 }}',
      },
      durations: {
        'fade-in': '0.3s',
        'slide-up': '0.4s',
        'scale-in': '0.4s',
      },
      timingFns: {
        'fade-in': 'ease-out',
        'slide-up': 'cubic-bezier(0.16, 1, 0.3, 1)',
        'scale-in': 'cubic-bezier(0.16, 1, 0.3, 1)',
      },
    },
  },
  presets: [
    presetUno(),
    presetAttributify(),
    presetIcons(),
  ],
  transformers: [
    transformerDirectives(),
    transformerVariantGroup(),
  ],
})
