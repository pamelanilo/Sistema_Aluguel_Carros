import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// O "proxy" faz o Vite repassar chamadas que começam com /api para o Spring Boot (porta 8080).
// Assim o navegador acha que tudo é do mesmo site e não precisamos configurar CORS.
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
})
