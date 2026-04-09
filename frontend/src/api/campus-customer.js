import request from '../utils/request'

export const getCampusCustomerAfterSaleResult = (orderId) => request({
  url: `/campus/customer/orders/${orderId}/after-sale-result`,
  method: 'get'
})

export const getCampusCustomerOrderDetail = (orderId) => request({
  url: `/campus/customer/orders/${orderId}`,
  method: 'get'
})

export const submitCourierOnboardingProfile = (data) => request({
  url: '/campus/customer/courier-onboarding/profile',
  method: 'post',
  data
})

export const getCourierOnboardingProfile = () => request({
  url: '/campus/customer/courier-onboarding/profile',
  method: 'get'
})

export const getCourierOnboardingReviewStatus = () => request({
  url: '/campus/customer/courier-onboarding/review-status',
  method: 'get'
})

export const getCourierTokenEligibility = () => request({
  url: '/campus/customer/courier-onboarding/token-eligibility',
  method: 'get'
})

export const applyCourierToken = (data) => request({
  url: '/campus/courier/auth/token',
  method: 'post',
  data
})
