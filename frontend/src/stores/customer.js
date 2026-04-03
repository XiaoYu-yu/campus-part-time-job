import { defineStore } from 'pinia'

const emptyUser = {
  id: null,
  name: '',
  phone: '',
  avatar: ''
}

export const useCustomerStore = defineStore('customer', {
  state: () => ({
    token: localStorage.getItem('customer_token') || '',
    userInfo: JSON.parse(localStorage.getItem('customer_user_info') || JSON.stringify(emptyUser))
  }),
  getters: {
    isLoggedIn: (state) => !!state.token
  },
  actions: {
    login(userInfo, token) {
      this.userInfo = userInfo
      this.token = token
      localStorage.setItem('customer_token', token)
      localStorage.setItem('customer_user_info', JSON.stringify(userInfo))
    },
    setUserInfo(userInfo) {
      this.userInfo = userInfo
      localStorage.setItem('customer_user_info', JSON.stringify(userInfo))
    },
    logout() {
      this.userInfo = { ...emptyUser }
      this.token = ''
      localStorage.removeItem('customer_token')
      localStorage.removeItem('customer_user_info')
    }
  }
})
