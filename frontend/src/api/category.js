import request from '../utils/request'

/**
 * 获取所有分类列表
 * @returns {Promise<Array>}
 */
export const getCategoryList = () => {
  return request({
    url: '/categories',
    method: 'get'
  })
}

/**
 * 获取分类详情
 * @param {number} id - 分类ID
 * @returns {Promise<Object>}
 */
export const getCategoryById = (id) => {
  return request({
    url: `/categories/${id}`,
    method: 'get'
  })
}

/**
 * 新增分类
 * @param {Object} data - 分类信息
 * @param {string} data.name - 分类名称
 * @param {number} data.sort - 排序
 * @returns {Promise<void>}
 */
export const addCategory = (data) => {
  return request({
    url: '/categories',
    method: 'post',
    data
  })
}

/**
 * 更新分类
 * @param {number} id - 分类ID
 * @param {Object} data - 分类信息
 * @returns {Promise<void>}
 */
export const updateCategory = (id, data) => {
  return request({
    url: `/categories/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除分类
 * @param {number} id - 分类ID
 * @returns {Promise<void>}
 */
export const deleteCategory = (id) => {
  return request({
    url: `/categories/${id}`,
    method: 'delete'
  })
}

/**
 * 修改分类状态
 * @param {number} id - 分类ID
 * @param {number} status - 状态（0-禁用，1-启用）
 * @returns {Promise<void>}
 */
export const updateCategoryStatus = (id, status) => {
  return request({
    url: `/categories/${id}/status`,
    method: 'put',
    data: { status }
  })
}

/**
 * 修改分类排序
 * @param {number} id - 分类ID
 * @param {number} sort - 排序值
 * @returns {Promise<void>}
 */
export const updateCategorySort = (id, sort) => {
  return request({
    url: `/categories/${id}/sort`,
    method: 'put',
    data: { sort }
  })
}
