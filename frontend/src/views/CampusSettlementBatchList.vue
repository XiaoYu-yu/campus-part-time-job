<template>
  <MainLayout>
    <div class="campus-admin-page">
      <div class="page-header">
        <div>
          <h2>校园结算批次</h2>
          <p>只读演示页，聚合展示当前 campus 结算批次与核对状态。</p>
        </div>
      </div>

      <section class="filter-card">
        <el-form :inline="true" class="filter-form">
          <el-form-item label="打款状态">
            <el-select v-model="filters.payoutStatus" placeholder="全部" clearable style="width: 180px">
              <el-option label="未打款" value="UNPAID" />
              <el-option label="已打款" value="PAID" />
              <el-option label="打款失败" value="FAILED" />
            </el-select>
          </el-form-item>
          <el-form-item label="核对状态">
            <el-select v-model="filters.payoutVerified" placeholder="全部" clearable style="width: 180px">
              <el-option label="未核对" value="0" />
              <el-option label="已核对" value="1" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </section>

      <section class="table-card">
        <el-table v-loading="loading" :data="records" border>
          <el-table-column prop="payoutBatchNo" label="批次号" min-width="220" />
          <el-table-column prop="totalCount" label="总条数" width="90" align="center" />
          <el-table-column prop="paidCount" label="已打款" width="90" align="center" />
          <el-table-column prop="failedCount" label="失败数" width="90" align="center" />
          <el-table-column prop="verifiedCount" label="已核对" width="90" align="center" />
          <el-table-column prop="unverifiedCount" label="未核对" width="90" align="center" />
          <el-table-column label="待结算金额" width="140" align="right">
            <template #default="{ row }">
              {{ formatAmount(row.totalPendingAmount) }}
            </template>
          </el-table-column>
          <el-table-column label="首次记录时间" min-width="170">
            <template #default="{ row }">
              {{ formatDateTime(row.firstRecordedAt) }}
            </template>
          </el-table-column>
          <el-table-column label="最近记录时间" min-width="170">
            <template #default="{ row }">
              {{ formatDateTime(row.lastRecordedAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="primary" link @click="goToDetail(row.payoutBatchNo)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="pagination.total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </section>
    </div>
  </MainLayout>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import MainLayout from '../layout/MainLayout.vue'
import { getCampusSettlementPayoutBatches } from '../api/campus-admin'

const router = useRouter()
const loading = ref(false)
const records = ref([])

const filters = reactive({
  payoutStatus: '',
  payoutVerified: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const formatAmount = (value) => `¥${Number(value || 0).toFixed(2)}`
const formatDateTime = (value) => value || '暂无'

const loadBatches = async () => {
  loading.value = true
  try {
    const response = await getCampusSettlementPayoutBatches({
      page: pagination.page,
      pageSize: pagination.pageSize,
      payoutStatus: filters.payoutStatus || undefined,
      payoutVerified: filters.payoutVerified || undefined
    })
    records.value = response.records || []
    pagination.total = response.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadBatches()
}

const handleReset = () => {
  filters.payoutStatus = ''
  filters.payoutVerified = ''
  pagination.page = 1
  loadBatches()
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.page = 1
  loadBatches()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadBatches()
}

const goToDetail = (batchNo) => {
  router.push(`/campus/settlement-batches/${encodeURIComponent(batchNo)}`)
}

onMounted(() => loadBatches())
</script>

<style scoped lang="scss">
.campus-admin-page {
  padding: 20px;
}

.page-header,
.filter-card,
.table-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 6px 20px rgba(15, 23, 42, 0.05);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  h2 {
    margin: 0 0 8px;
    font-size: 24px;
    color: #18181b;
  }

  p {
    margin: 0;
    color: #71717a;
  }
}

.filter-form {
  margin-bottom: -18px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
