import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  build: {
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
})
