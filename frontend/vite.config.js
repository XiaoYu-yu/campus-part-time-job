import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const isAndroidUserMode = mode.startsWith('android-user')
  const isAndroidParttimeMode = mode.startsWith('android-parttime')
  const isAndroidMode = isAndroidUserMode || isAndroidParttimeMode
  const appShell = env.VITE_APP_SHELL || (isAndroidUserMode ? 'user' : isAndroidParttimeMode ? 'parttime' : 'admin')
  const outDirMap = {
    user: 'dist-android-user',
    parttime: 'dist-android-parttime',
    admin: 'dist'
  }

  if (isAndroidMode && (!env.VITE_API_BASE_URL || env.VITE_API_BASE_URL === '/api')) {
    throw new Error(`Android build mode '${mode}' must set an explicit VITE_API_BASE_URL. Copy the matching frontend/.env.${mode}.example if needed.`)
  }

  return {
    plugins: [vue()],
    build: {
      outDir: outDirMap[appShell] || 'dist',
      // The current RC keeps Element Plus as a global runtime dependency.
      // 1100 kB suppresses noise from the accepted vendor baseline while
      // still surfacing larger regressions above the current footprint.
      chunkSizeWarningLimit: 1100
    },
    server: {
      port: 5173,
      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
          // rewrite: (path) => path.replace(/^\/api/, '')
        }
      }
    }
  }
})
