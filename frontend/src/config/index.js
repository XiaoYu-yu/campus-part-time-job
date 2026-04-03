// 环境配置
const config = {
  // 是否使用 Mock 数据
  useMock: import.meta.env.VITE_USE_MOCK === 'true',
  
  // API 基础地址
  apiBaseUrl: import.meta.env.VITE_API_BASE_URL || '/api',
  
  // 请求超时时间（毫秒）
  requestTimeout: 10000,
  
  // 分页默认配置
  pagination: {
    defaultPageSize: 10,
    pageSizeOptions: [10, 20, 50, 100]
  }
}

export default config
