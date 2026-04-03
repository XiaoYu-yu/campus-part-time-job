import { beforeEach, describe, expect, it } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useCustomerStore } from './customer'

describe('customer store', () => {
  beforeEach(() => {
    localStorage.clear()
    setActivePinia(createPinia())
  })

  it('persists login state', () => {
    const store = useCustomerStore()
    store.login({ id: 1, name: '张三', phone: '13900139000' }, 'token-1')

    expect(store.isLoggedIn).toBe(true)
    expect(localStorage.getItem('customer_token')).toBe('token-1')
  })

  it('clears state on logout', () => {
    const store = useCustomerStore()
    store.login({ id: 1, name: '张三' }, 'token-1')
    store.logout()

    expect(store.isLoggedIn).toBe(false)
    expect(localStorage.getItem('customer_token')).toBeNull()
  })
})
