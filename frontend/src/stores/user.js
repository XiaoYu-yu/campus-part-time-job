/**
 * 用户状态管理模块
 * @module userStore
 * @description 使用 Pinia 管理用户登录状态、用户信息和 Token
 */

import { defineStore } from 'pinia'

/**
 * 用户信息对象
 * @typedef {Object} UserInfo
 * @property {string} name - 用户姓名
 * @property {string} avatar - 用户头像URL
 * @property {string} role - 用户角色
 */

/**
 * 用户状态 Store
 * @returns {Object} Store 实例
 */
export const useUserStore = defineStore('user', {
  /**
   * 状态定义
   * @returns {Object} 状态对象
   * @property {UserInfo} userInfo - 用户信息
   * @property {string} token - 登录令牌
   * @property {boolean} isLoggedIn - 登录状态
   */
  state: () => ({
    /** @type {UserInfo} 用户信息对象 */
    userInfo: {
      name: '',
      avatar: '',
      role: ''
    },
    /** @type {string} 用户登录令牌，从 localStorage 中读取 */
    token: localStorage.getItem('admin_token') || '',
    storedUserInfo: JSON.parse(localStorage.getItem('admin_user_info') || '{"name":"","avatar":"","role":""}'),
    /** @type {boolean} 用户是否已登录 */
    isLoggedIn: !!localStorage.getItem('admin_token')
  }),
  getters: {
    currentUserInfo(state) {
      return state.userInfo.name ? state.userInfo : state.storedUserInfo
    }
  },
  
  /**
   * 操作方法
   */
  actions: {
    /**
     * 设置用户信息
     * @param {UserInfo} userInfo - 用户信息对象
     */
    setUserInfo(userInfo) {
      this.userInfo = userInfo
      this.storedUserInfo = userInfo
      localStorage.setItem('admin_user_info', JSON.stringify(userInfo))
    },
    
    /**
     * 设置登录令牌
     * @param {string} token - JWT Token
     * @description 设置token并更新登录状态，同时持久化到 localStorage
     */
    setToken(token) {
      this.token = token
      this.isLoggedIn = true
      localStorage.setItem('admin_token', token)
    },
    
    /**
     * 用户登录
     * @param {UserInfo} userInfo - 用户信息对象
     * @param {string} token - JWT Token
     * @description 同时设置用户信息和token，完成登录流程
     */
    login(userInfo, token) {
      this.userInfo = userInfo
      this.storedUserInfo = userInfo
      this.token = token
      this.isLoggedIn = true
      localStorage.setItem('admin_token', token)
      localStorage.setItem('admin_user_info', JSON.stringify(userInfo))
    },
    
    /**
     * 退出登录
     * @description 清除用户信息和token，移除 localStorage 中的token
     */
    logout() {
      this.userInfo = {
        name: '',
        avatar: '',
        role: ''
      }
      this.storedUserInfo = {
        name: '',
        avatar: '',
        role: ''
      }
      this.token = ''
      this.isLoggedIn = false
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_user_info')
    }
  }
})
