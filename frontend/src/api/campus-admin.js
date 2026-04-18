import request from '../utils/request'

const normalizePageParams = (params = {}) => {
  const normalized = { ...params }
  if (normalized.pageSize === undefined && normalized.size !== undefined) {
    normalized.pageSize = normalized.size
    delete normalized.size
  }
  return normalized
}

export const getCampusSettlementPayoutBatches = (params) => request({
  url: '/campus/admin/settlements/payout-batches',
  method: 'get',
  params: normalizePageParams(params)
})

export const getCampusSettlementPayoutBatchDetail = (batchNo) => request({
  url: `/campus/admin/settlements/payout-batches/${encodeURIComponent(batchNo)}`,
  method: 'get'
})

export const getCampusSettlementBatchOperations = (batchNo, params) => request({
  url: `/campus/admin/settlements/payout-batches/${encodeURIComponent(batchNo)}/operations`,
  method: 'get',
  params: normalizePageParams(params)
})

export const getCampusAfterSaleExecutions = (params) => request({
  url: '/campus/admin/orders/after-sale-executions',
  method: 'get',
  params: normalizePageParams(params)
})

export const getCampusAfterSaleExecutionRecords = (params) => request({
  url: '/campus/admin/after-sale-execution-records',
  method: 'get',
  params: normalizePageParams(params)
})

export const getCampusAdminAfterSaleResult = (relayOrderId) => request({
  url: `/campus/admin/orders/${encodeURIComponent(relayOrderId)}/after-sale-result`,
  method: 'get'
})

export const getCampusCouriers = (params) => request({
  url: '/campus/admin/couriers',
  method: 'get',
  params: normalizePageParams(params)
})

export const getCampusCourierRecentExceptions = (courierProfileId, params) => request({
  url: `/campus/admin/couriers/${courierProfileId}/exceptions/recent`,
  method: 'get',
  params
})

export const getCampusCourierLocationReports = (courierProfileId, params) => request({
  url: `/campus/admin/couriers/${courierProfileId}/location-reports`,
  method: 'get',
  params: normalizePageParams(params)
})

export const getCampusSettlements = (params) => request({
  url: '/campus/admin/settlements',
  method: 'get',
  params: normalizePageParams(params)
})

export const getCampusSettlementReconcileSummary = (params) => request({
  url: '/campus/admin/settlements/reconcile-summary',
  method: 'get',
  params
})

export const getCampusSettlementDetail = (id) => request({
  url: `/campus/admin/settlements/${id}`,
  method: 'get'
})

export const getCampusExceptionRecords = (params) => request({
  url: '/campus/admin/exceptions',
  method: 'get',
  params: normalizePageParams(params)
})

export const getCampusExceptionDetail = (id) => request({
  url: `/campus/admin/exceptions/${id}`,
  method: 'get'
})

export const resolveCampusException = (id, data) => request({
  url: `/campus/admin/exceptions/${id}/resolve`,
  method: 'post',
  data
})
