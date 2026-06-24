import fs from 'node:fs'
import path from 'node:path'
import process from 'node:process'
import { fileURLToPath } from 'node:url'

const scriptDir = path.dirname(fileURLToPath(import.meta.url))
const appDirectoryName = process.argv[2]
const mode = process.argv[3]
const supportedApps = new Set(['user-app', 'parttime-app'])
const supportedModes = new Set(['default', 'emulator', 'lan', 'public'])

if (!supportedApps.has(appDirectoryName) || !supportedModes.has(mode)) {
  console.error('Usage: node mobile/configure-capacitor.mjs <user-app|parttime-app> <default|emulator|lan|public>')
  process.exit(1)
}

const configPath = path.join(scriptDir, appDirectoryName, 'capacitor.config.json')
const config = JSON.parse(fs.readFileSync(configPath, 'utf8'))
const publicMode = mode === 'public'

config.server = {
  ...(config.server || {}),
  androidScheme: publicMode ? 'https' : 'http',
  cleartext: !publicMode
}

fs.writeFileSync(configPath, `${JSON.stringify(config, null, 2)}\n`, 'utf8')
console.log(
  `Configured ${appDirectoryName} for ${mode}: androidScheme=${config.server.androidScheme}, cleartext=${config.server.cleartext}`
)
