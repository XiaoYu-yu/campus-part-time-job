const rawTencentMapKey = (import.meta.env.VITE_TENCENT_MAP_KEY || '').trim()

let tencentMapLoader = null

export const hasTencentMapKey = () => Boolean(rawTencentMapKey)

export const loadTencentMapSdk = () => {
  if (!hasTencentMapKey()) {
    return Promise.reject(new Error('未配置腾讯地图 key'))
  }

  if (window.TMap?.Map) {
    return Promise.resolve(window.TMap)
  }

  if (tencentMapLoader) {
    return tencentMapLoader
  }

  tencentMapLoader = new Promise((resolve, reject) => {
    const existingScript = document.querySelector('script[data-tencent-map-sdk="true"]')
    if (existingScript) {
      existingScript.addEventListener('load', () => {
        if (window.TMap?.Map) {
          resolve(window.TMap)
        } else {
          reject(new Error('腾讯地图 SDK 已加载但 Map 对象不可用'))
        }
      }, { once: true })
      existingScript.addEventListener('error', () => reject(new Error('腾讯地图 SDK 加载失败')), { once: true })
      return
    }

    const script = document.createElement('script')
    script.src = `https://map.qq.com/api/gljs?v=1.exp&key=${encodeURIComponent(rawTencentMapKey)}`
    script.async = true
    script.defer = true
    script.dataset.tencentMapSdk = 'true'
    script.onload = () => {
      if (window.TMap?.Map) {
        resolve(window.TMap)
        return
      }
      reject(new Error('腾讯地图 SDK 已加载但 Map 对象不可用'))
    }
    script.onerror = () => reject(new Error('腾讯地图 SDK 加载失败'))
    document.head.appendChild(script)
  }).catch((error) => {
    tencentMapLoader = null
    throw error
  })

  return tencentMapLoader
}
