import request from '../utils/request'

export const submitUserOrder = (data) => request({
  url: '/user/orders',
  method: 'post',
  data
})

export const getUserOrders = (params) => request({
  url: '/user/orders',
  method: 'get',
  params
})

export const getUserOrderById = (id) => request({
  url: `/user/orders/${id}`,
  method: 'get'
})

export const payUserOrder = (id) => request({
  url: `/user/orders/${id}/pay`,
  method: 'put'
})

export const cancelUserOrder = (id) => request({
  url: `/user/orders/${id}/cancel`,
  method: 'put'
})

export const reorderUserOrder = (id) => request({
  url: `/user/orders/${id}/reorder`,
  method: 'post'
})
