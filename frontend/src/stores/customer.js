import { defineStore } from 'pinia'

const emptyUser = {
  id: null,
  name: '',
  phone: '',
  avatar: ''
}

const parseStoredCustomerInfo = () => {
  try {
    return JSON.parse(localStorage.getItem('customer_user_info') || JSON.stringify(emptyUser))
  } catch {
    localStorage.removeItem('customer_user_info')
    return { ...emptyUser }
  }
}

export const useCustomerStore = defineStore('customer', {
  state: () => ({
    token: localStorage.getItem('customer_token') || '',
    userInfo: parseStoredCustomerInfo()
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
      localStorage.removeItem('courier_token')
      localStorage.removeItem('courier_profile')
    }
  }
})
