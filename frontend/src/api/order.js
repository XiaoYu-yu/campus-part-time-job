import request from '../utils/request'

/**
 * 分页查询订单列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页条数
 * @param {string} [params.orderNo] - 订单号（可选）
 * @param {string} [params.customerName] - 客户姓名（可选）
 * @param {number} [params.status] - 订单状态（可选）
 * @param {string} [params.beginTime] - 开始时间（可选）
 * @param {string} [params.endTime] - 结束时间（可选）
 * @returns {Promise<{records: Array, total: number, size: number, current: number, pages: number}>}
 */
export const getOrderList = (params) => {
  const normalizedParams = { ...params }
  if (normalizedParams.pageSize === undefined && normalizedParams.size !== undefined) {
    normalizedParams.pageSize = normalizedParams.size
    delete normalizedParams.size
  }

  return request({
    url: '/orders',
    method: 'get',
    params: normalizedParams
  })
}

/**
 * 获取订单详情
 * @param {number} id - 订单ID
 * @returns {Promise<Object>}
 */
export const getOrderById = (id) => {
  return request({
    url: `/orders/${id}`,
    method: 'get'
  })
}

/**
 * 修改订单状态
 * @param {number} id - 订单ID
 * @param {number} status - 订单状态
 * @returns {Promise<void>}
 */
export const updateOrderStatus = (id, status) => {
  return request({
    url: `/orders/${id}/status`,
    method: 'put',
    data: { status }
  })
}
