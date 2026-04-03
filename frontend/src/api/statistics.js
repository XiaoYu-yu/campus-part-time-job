import request from '../utils/request'

/**
 * 获取仪表盘统计数据
 * @returns {Promise<Object>}
 */
export const getDashboardData = () => {
  return request({
    url: '/statistics/dashboard',
    method: 'get'
  })
}

/**
 * 获取销售趋势数据
 * @param {Object} params - 查询参数
 * @param {string} [params.beginDate] - 开始日期
 * @param {string} [params.endDate] - 结束日期
 * @returns {Promise<Object>}
 */
export const getSalesTrend = (params) => {
  return request({
    url: '/statistics/sales',
    method: 'get',
    params
  })
}

/**
 * 获取热门菜品排行
 * @param {Object} params - 查询参数
 * @param {number} [params.top] - 排行数量（默认10）
 * @returns {Promise<Array>}
 */
export const getPopularDishes = (params) => {
  return request({
    url: '/statistics/popular-dishes',
    method: 'get',
    params
  })
}

/**
 * 获取订单统计
 * @param {Object} params - 查询参数
 * @returns {Promise<Object>}
 */
export const getOrderStatistics = (params) => {
  return request({
    url: '/statistics/orders',
    method: 'get',
    params
  })
}

/**
 * 获取用户统计
 * @param {Object} params - 查询参数
 * @returns {Promise<Object>}
 */
export const getUserStatistics = (params) => {
  return request({
    url: '/statistics/users',
    method: 'get',
    params
  })
}
