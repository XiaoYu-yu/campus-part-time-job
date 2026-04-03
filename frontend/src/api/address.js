import request from '../utils/request'

export const getAddressList = () => request({
  url: '/user/addresses',
  method: 'get'
})

export const addAddress = (data) => request({
  url: '/user/addresses',
  method: 'post',
  data
})

export const updateAddress = (id, data) => request({
  url: `/user/addresses/${id}`,
  method: 'put',
  data
})

export const deleteAddress = (id) => request({
  url: `/user/addresses/${id}`,
  method: 'delete'
})

export const setDefaultAddress = (id) => request({
  url: `/user/addresses/${id}/default`,
  method: 'put'
})
