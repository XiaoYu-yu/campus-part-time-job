import request from '../utils/request'

export const getPublicShopStatus = () => request({
  url: '/public/shop/status',
  method: 'get'
})

export const getShopStatus = () => request({
  url: '/shop/status',
  method: 'get'
})

export const updateShopStatus = (data) => request({
  url: '/shop/status',
  method: 'put',
  data
})
