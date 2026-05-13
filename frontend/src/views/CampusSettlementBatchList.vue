<template>
  <MainLayout>
    <div class="campus-admin-page">
      <section class="page-hero">
        <div>
          <span class="eyebrow">Campus Ops</span>
          <h2>校园结算批次</h2>
          <p>按批次维度查看结算数据，支持按状态和类型筛选。</p>
        </div>
        <div class="hero-notes">
          <span>运营模块</span>
          <strong>batches</strong>
        </div>
      </section>

      <section class="filter-card">
        <div class="filter-copy">
          <span class="section-kicker">筛选条件</span>
          <h3>按打款状态与核对状态筛选批次</h3>
        </div>
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
            <el-button type="primary" class="search-btn" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </section>

      <section class="table-card">
        <div class="table-heading">
          <div>
            <span class="section-kicker">批次列表</span>
            <h3>结算批次总览</h3>
          </div>
          <el-tag type="info" effect="plain">共 {{ pagination.total }} 条</el-tag>
        </div>

        <el-table v-loading="loading" :data="records" class="campus-table" style="width: 100%;">
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
  display: flex;
  flex-direction: column;
  gap: 22px;

  .page-hero {
    position: relative;
    overflow: hidden;
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    gap: 24px;
    min-height: 170px;
    padding: 34px 40px;
    border: 1px solid rgba(15, 118, 110, 0.12);
    border-radius: 28px;
    background:
      radial-gradient(circle at 86% 18%, rgba(132, 204, 22, 0.28), transparent 28%),
      radial-gradient(circle at 16% 20%, rgba(14, 165, 233, 0.16), transparent 32%),
      linear-gradient(135deg, rgba(255, 255, 255, 0.94) 0%, rgba(236, 253, 245, 0.86) 54%, rgba(224, 242, 254, 0.86) 100%);
    box-shadow: 0 24px 60px rgba(15, 23, 42, 0.09);
    color: #0f172a;

    &::after {
      content: '';
      position: absolute;
      right: -80px;
      bottom: -110px;
      width: 270px;
      height: 270px;
      border-radius: 50%;
      border: 38px solid rgba(15, 118, 110, 0.07);
    }

    h2 {
      position: relative;
      margin: 8px 0 10px;
      font-size: 34px;
      font-weight: 900;
      letter-spacing: -0.03em;
    }

    p {
      position: relative;
      max-width: 560px;
      margin: 0;
      color: #475569;
      font-size: 15px;
      line-height: 1.8;
    }
  }

  .eyebrow,
  .section-kicker {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    color: #0f766e;
    font-size: 12px;
    font-weight: 900;
    letter-spacing: 0.14em;
    text-transform: uppercase;
  }

  .hero-notes {
    position: relative;
    z-index: 1;
    display: grid;
    gap: 4px;
    min-width: 142px;
    padding: 18px 20px;
    border: 1px solid rgba(15, 118, 110, 0.12);
    border-radius: 22px;
    background: rgba(255, 255, 255, 0.72);
    backdrop-filter: blur(14px);

    span {
      color: #64748b;
      font-size: 12px;
    }

    strong {
      color: #0f172a;
      font-size: 20px;
      letter-spacing: 0.02em;
    }
  }

  .filter-card,
  .table-card {
    border: 1px solid rgba(15, 118, 110, 0.1);
    border-radius: 24px;
    background: rgba(255, 255, 255, 0.9);
    box-shadow: 0 18px 42px rgba(15, 23, 42, 0.08);
    padding: 22px 24px;
  }

  .filter-card {
    .filter-copy {
      margin-bottom: 16px;

      h3 {
        margin: 6px 0 0;
        color: #0f172a;
        font-size: 18px;
        font-weight: 900;
      }
    }

    .filter-form {
      margin-bottom: -18px;
    }

    .search-btn {
      border: none;
      background: linear-gradient(135deg, #0f766e, #0ea5e9);
      box-shadow: 0 12px 24px rgba(14, 165, 233, 0.22);
    }
  }

  .table-card {
    padding-bottom: 16px;
  }

  .table-heading {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 16px;
    margin-bottom: 16px;

    h3 {
      margin: 6px 0 0;
      color: #0f172a;
      font-size: 20px;
      font-weight: 900;
    }
  }

  .campus-table {
    overflow: hidden;
    border: 1px solid #e2e8f0;
    border-radius: 18px;

    :deep(.el-table__header-wrapper th) {
      background: #f8fafc;
      color: #0f172a;
      font-weight: 800;
    }

    :deep(.el-table__row) {
      color: #334155;
    }

    :deep(.el-table__cell) {
      border-bottom-color: #edf2f7;
    }
  }

  .pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}

@media (max-width: 900px) {
  .campus-admin-page {
    .page-hero,
    .filter-card {
      align-items: flex-start;
      flex-direction: column;
    }
  }
}
</style>
