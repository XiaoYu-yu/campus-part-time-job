import request from '../utils/request'

export const customerLogin = (data) => request({
  url: '/users/login',
  method: 'post',
  data
})

export const getCustomerInfo = () => request({
  url: '/users/info',
  method: 'get'
})

export const customerLogout = () => request({
  url: '/users/logout',
  method: 'post'
})
