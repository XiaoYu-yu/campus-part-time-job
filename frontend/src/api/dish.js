import request from '../utils/request'

/**
 * 分页查询菜品列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页条数
 * @param {string} [params.name] - 菜品名称（可选）
 * @param {number} [params.categoryId] - 分类ID（可选）
 * @param {number} [params.status] - 状态（可选，0-禁用，1-启用）
 * @returns {Promise<{records: Array, total: number, size: number, current: number, pages: number}>}
 */
export const getDishList = (params) => {
  return request({
    url: '/dishes',
    method: 'get',
    params
  })
}

/**
 * 获取菜品详情
 * @param {number} id - 菜品ID
 * @returns {Promise<Object>}
 */
export const getDishById = (id) => {
  return request({
    url: `/dishes/${id}`,
    method: 'get'
  })
}

/**
 * 新增菜品
 * @param {Object} data - 菜品信息
 * @returns {Promise<void>}
 */
export const addDish = (data) => {
  return request({
    url: '/dishes',
    method: 'post',
    data
  })
}

/**
 * 更新菜品
 * @param {number} id - 菜品ID
 * @param {Object} data - 菜品信息
 * @returns {Promise<void>}
 */
export const updateDish = (id, data) => {
  return request({
    url: `/dishes/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除菜品
 * @param {number} id - 菜品ID
 * @returns {Promise<void>}
 */
export const deleteDish = (id) => {
  return request({
    url: `/dishes/${id}`,
    method: 'delete'
  })
}

/**
 * 修改菜品状态
 * @param {number} id - 菜品ID
 * @param {number} status - 状态（0-禁用，1-启用）
 * @returns {Promise<void>}
 */
export const updateDishStatus = (id, status) => {
  return request({
    url: `/dishes/${id}/status`,
    method: 'put',
    data: { status }
  })
}

/**
 * 批量修改菜品状态
 * @param {Array<number>} ids - 菜品ID数组
 * @param {number} status - 状态（0-禁用，1-启用）
 * @returns {Promise<void>}
 */
export const batchUpdateDishStatus = (ids, status) => {
  return request({
    url: '/dishes/status/batch',
    method: 'put',
    data: { ids, status }
  })
}
