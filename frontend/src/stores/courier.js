import { defineStore } from 'pinia'

const emptyProfile = {
  id: null,
  realName: '',
  reviewStatus: '',
  enabled: 0
}

const parseStoredProfile = () => {
  try {
    return JSON.parse(localStorage.getItem('courier_profile') || JSON.stringify(emptyProfile))
  } catch (error) {
    return { ...emptyProfile }
  }
}

export const useCourierStore = defineStore('courier', {
  state: () => ({
    token: localStorage.getItem('courier_token') || '',
    profile: parseStoredProfile()
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token)
  },
  actions: {
    login(token, profile = {}) {
      this.token = token || ''
      this.profile = {
        ...emptyProfile,
        ...(profile || {})
      }
      localStorage.setItem('courier_token', this.token)
      localStorage.setItem('courier_profile', JSON.stringify(this.profile))
    },
    setProfile(profile = {}) {
      this.profile = {
        ...emptyProfile,
        ...(profile || {})
      }
      localStorage.setItem('courier_profile', JSON.stringify(this.profile))
    },
    logout() {
      this.token = ''
      this.profile = { ...emptyProfile }
      localStorage.removeItem('courier_token')
      localStorage.removeItem('courier_profile')
    }
  }
})
