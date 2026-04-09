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
