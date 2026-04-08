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
