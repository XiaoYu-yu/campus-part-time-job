import request from '../utils/request'

export const getCartList = () => request({
  url: '/user/cart',
  method: 'get'
})

export const getCartCount = () => request({
  url: '/user/cart/count',
  method: 'get'
})

export const addCartItem = (data) => request({
  url: '/user/cart',
  method: 'post',
  data
})

export const updateCartItem = (id, data) => request({
  url: `/user/cart/${id}`,
  method: 'put',
  data
})

export const deleteCartItem = (id) => request({
  url: `/user/cart/${id}`,
  method: 'delete'
})

export const clearCart = () => request({
  url: '/user/cart/clear',
  method: 'delete'
})
