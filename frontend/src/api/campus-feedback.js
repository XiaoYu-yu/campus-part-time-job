import request from '../utils/request'

export const submitCampusFeedback = (data) => request({
  url: '/campus/public/feedback',
  method: 'post',
  data
})
