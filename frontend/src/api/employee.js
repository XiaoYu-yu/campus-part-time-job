import request from '../utils/request'

/**
 * 员工登录
 * @param {Object} data - 登录参数
 * @param {string} data.phone - 手机号
 * @param {string} data.password - 密码
 * @returns {Promise<{token: string, employee: Object}>}
 */
export const login = (data) => {
  return request({
    url: '/employees/login',
    method: 'post',
    data
  })
}

/**
 * 获取当前登录员工信息
 * @returns {Promise<Object>}
 */
export const getInfo = () => {
  return request({
    url: '/employees/info',
    method: 'get'
  })
}

/**
 * 退出登录
 * @returns {Promise<void>}
 */
export const logout = () => {
  return request({
    url: '/employees/logout',
    method: 'post'
  })
}

/**
 * 分页查询员工列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页条数
 * @param {string} [params.name] - 员工姓名（可选）
 * @param {string} [params.phone] - 手机号（可选）
 * @returns {Promise<{records: Array, total: number, size: number, current: number, pages: number}>}
 */
export const getEmployeeList = (params) => {
  return request({
    url: '/employees',
    method: 'get',
    params
  })
}

/**
 * 获取员工详情
 * @param {number} id - 员工ID
 * @returns {Promise<Object>}
 */
export const getEmployeeById = (id) => {
  return request({
    url: `/employees/${id}`,
    method: 'get'
  })
}

/**
 * 新增员工
 * @param {Object} data - 员工信息
 * @returns {Promise<void>}
 */
export const addEmployee = (data) => {
  return request({
    url: '/employees',
    method: 'post',
    data
  })
}

/**
 * 更新员工信息
 * @param {number} id - 员工ID
 * @param {Object} data - 员工信息
 * @returns {Promise<void>}
 */
export const updateEmployee = (id, data) => {
  return request({
    url: `/employees/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除员工
 * @param {number} id - 员工ID
 * @returns {Promise<void>}
 */
export const deleteEmployee = (id) => {
  return request({
    url: `/employees/${id}`,
    method: 'delete'
  })
}

/**
 * 修改员工状态
 * @param {number} id - 员工ID
 * @param {number} status - 状态（0-禁用，1-启用）
 * @returns {Promise<void>}
 */
export const updateEmployeeStatus = (id, status) => {
  return request({
    url: `/employees/${id}/status`,
    method: 'put',
    data: { status }
  })
}
