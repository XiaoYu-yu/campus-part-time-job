import request from '../utils/request'

export const getPublicCategories = () => request({
  url: '/public/categories',
  method: 'get'
})

export const getPublicDishes = (params) => request({
  url: '/public/dishes',
  method: 'get',
  params
})

export const getPublicDishById = (id) => request({
  url: `/public/dishes/${id}`,
  method: 'get'
})
