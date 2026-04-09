/**
 * HTTP请求工具模块
 * @module request
 * @description 基于axios封装的HTTP请求工具，包含请求/响应拦截器、错误处理和认证功能
 */

import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const PUBLIC_PREFIXES = ['/public/', '/campus/public/', '/campus/courier/auth/token', '/employees/login', '/users/login']
const CUSTOMER_PREFIXES = ['/users/', '/user/', '/campus/customer/']
const COURIER_PREFIXES = ['/campus/courier/orders/', '/campus/courier/location-reports']
const COURIER_BRIDGE_PREFIXES = ['/campus/courier/profile', '/campus/courier/review-status']

const isPublicRequest = (url = '') => PUBLIC_PREFIXES.some(prefix => url.startsWith(prefix))
const isCustomerRequest = (url = '') => CUSTOMER_PREFIXES.some(prefix => url.startsWith(prefix))
const isCourierRequest = (url = '') => COURIER_PREFIXES.some(prefix => url.startsWith(prefix))
const isCourierBridgeRequest = (url = '') => COURIER_BRIDGE_PREFIXES.some(prefix => url.startsWith(prefix))

/**
 * 创建axios实例
 * @type {import('axios').AxiosInstance}
 * @description 配置基础URL、超时时间和默认请求头
 */
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

/**
 * 请求拦截器
 * @description 在请求发送前执行，处理 token 认证
 * @param {import('axios').AxiosRequestConfig} config - 请求配置对象
 * @returns {import('axios').AxiosRequestConfig} 处理后的请求配置
 */
request.interceptors.request.use(
  (config) => {
    const requestUrl = config.url || ''
    let token = ''

    if (!isPublicRequest(requestUrl)) {
      if (isCustomerRequest(requestUrl)) {
        token = localStorage.getItem('customer_token')
      } else if (isCourierBridgeRequest(requestUrl)) {
        token = localStorage.getItem('courier_token') || localStorage.getItem('customer_token')
      } else if (isCourierRequest(requestUrl)) {
        token = localStorage.getItem('courier_token')
      } else {
        token = localStorage.getItem('admin_token')
      }
    }

    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    return config
  },
  (error) => {
    // 请求发送失败时的错误处理
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 * @description 在接收到响应后执行，处理响应数据和错误状态
 * @param {import('axios').AxiosResponse} response - 响应对象
 * @returns {any} 处理后的响应数据
 */
request.interceptors.response.use(
  (response) => {
    const res = response.data
    
    // 如果返回的状态码是 200，说明请求成功，返回数据部分
    if (res.code === 200) {
      return res.data
    }
    
    // 其他状态码视为错误，显示错误消息
    ElMessage.error(res.msg || '请求失败')
    return Promise.reject(new Error(res.msg || '请求失败'))
  },
  (error) => {
    const { response } = error
    
    // 根据HTTP状态码进行不同的错误处理
    if (response) {
      const requestUrl = error.config?.url || ''
      const customerRequest = isCustomerRequest(requestUrl)
      const courierRequest = isCourierRequest(requestUrl)
      const courierBridgeRequest = isCourierBridgeRequest(requestUrl)
      switch (response.status) {
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          if (customerRequest) {
            localStorage.removeItem('customer_token')
            localStorage.removeItem('customer_user_info')
            router.push('/user/login')
          } else if (courierBridgeRequest) {
            if (localStorage.getItem('courier_token')) {
              localStorage.removeItem('courier_token')
              localStorage.removeItem('courier_profile')
              router.push('/user/campus/courier-onboarding')
            } else {
              localStorage.removeItem('customer_token')
              localStorage.removeItem('customer_user_info')
              router.push('/user/login')
            }
          } else if (courierRequest) {
            localStorage.removeItem('courier_token')
            localStorage.removeItem('courier_profile')
            router.push('/user/campus/courier-onboarding')
          } else {
            localStorage.removeItem('admin_token')
            localStorage.removeItem('admin_user_info')
            router.push('/login')
          }
          break
        case 403:
          // 权限不足
          ElMessage.error('没有权限访问该资源')
          break
        case 404:
          // 资源不存在
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          // 服务器内部错误
          ElMessage.error('服务器内部错误')
          break
        default:
          // 其他错误
          ElMessage.error(response.data?.msg || '网络错误')
      }
    } else {
      // 网络连接失败
      ElMessage.error('网络连接失败，请检查网络')
    }
    
    return Promise.reject(error)
  }
)

export default request
