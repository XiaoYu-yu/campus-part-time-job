import request from '../utils/request'

const normalizePageParams = (params = {}) => {
  const normalized = { ...params }
  if (normalized.pageSize === undefined && normalized.size !== undefined) {
    normalized.pageSize = normalized.size
    delete normalized.size
  }
  return normalized
}

export const getCourierProfile = () => request({
  url: '/campus/courier/profile',
  method: 'get'
})

export const getCourierReviewStatus = () => request({
  url: '/campus/courier/review-status',
  method: 'get'
})

export const getCourierAvailableOrders = (params) => request({
  url: '/campus/courier/orders/available',
  method: 'get',
  params: normalizePageParams(params)
})

export const acceptCourierOrder = (orderId) => request({
  url: `/campus/courier/orders/${orderId}/accept`,
  method: 'post'
})

export const getCourierOrderDetail = (orderId) => request({
  url: `/campus/courier/orders/${orderId}`,
  method: 'get'
})

export const pickupCourierOrder = (orderId, data) => request({
  url: `/campus/courier/orders/${orderId}/pickup`,
  method: 'post',
  data
})

export const deliverCourierOrder = (orderId, data) => request({
  url: `/campus/courier/orders/${orderId}/deliver`,
  method: 'post',
  data
})

export const reportCourierOrderException = (orderId, data) => request({
  url: `/campus/courier/orders/${orderId}/exception-report`,
  method: 'post',
  data
})
