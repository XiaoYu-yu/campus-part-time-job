import request from '../utils/request'

/**
 * 分页查询套餐列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页条数
 * @param {string} [params.name] - 套餐名称（可选）
 * @param {number} [params.categoryId] - 分类ID（可选）
 * @param {number} [params.status] - 状态（可选，0-禁用，1-启用）
 * @returns {Promise<{records: Array, total: number, size: number, current: number, pages: number}>}
 */
export const getSetmealList = (params) => {
  return request({
    url: '/setmeals',
    method: 'get',
    params
  })
}

/**
 * 获取套餐详情
 * @param {number} id - 套餐ID
 * @returns {Promise<Object>}
 */
export const getSetmealById = (id) => {
  return request({
    url: `/setmeals/${id}`,
    method: 'get'
  })
}

/**
 * 新增套餐
 * @param {Object} data - 套餐信息
 * @returns {Promise<void>}
 */
export const addSetmeal = (data) => {
  return request({
    url: '/setmeals',
    method: 'post',
    data
  })
}

/**
 * 更新套餐
 * @param {number} id - 套餐ID
 * @param {Object} data - 套餐信息
 * @returns {Promise<void>}
 */
export const updateSetmeal = (id, data) => {
  return request({
    url: `/setmeals/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除套餐
 * @param {number} id - 套餐ID
 * @returns {Promise<void>}
 */
export const deleteSetmeal = (id) => {
  return request({
    url: `/setmeals/${id}`,
    method: 'delete'
  })
}

/**
 * 修改套餐状态
 * @param {number} id - 套餐ID
 * @param {number} status - 状态（0-禁用，1-启用）
 * @returns {Promise<void>}
 */
export const updateSetmealStatus = (id, status) => {
  return request({
    url: `/setmeals/${id}/status`,
    method: 'put',
    data: { status }
  })
}

/**
 * 批量修改套餐状态
 * @param {Array<number>} ids - 套餐ID数组
 * @param {number} status - 状态（0-禁用，1-启用）
 * @returns {Promise<void>}
 */
export const batchUpdateSetmealStatus = (ids, status) => {
  return request({
    url: '/setmeals/status/batch',
    method: 'put',
    data: { ids, status }
  })
}
