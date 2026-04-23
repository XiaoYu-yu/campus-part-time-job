<template>
  <MainLayout>
    <div class="campus-admin-page">
      <div class="page-header">
        <div>
          <h2>校园配送运营</h2>
          <p>只读演示页，联动查看配送员最近异常、低频位置记录和腾讯地图预览，不做实时刷新。</p>
        </div>
      </div>

      <el-alert
        title="该页聚焦配送员维度的异常与低频位置联动查看，只读展示后端当前最小运营模型，并补最小腾讯地图预览承接。"
        type="info"
        :closable="false"
        show-icon
        class="page-alert"
      />

      <div class="ops-layout">
        <section class="panel-card courier-panel">
          <div class="panel-header">
            <div>
              <h3>配送员列表</h3>
              <p>左侧列表调用管理员配送员分页接口，点击后联动右侧异常与位置记录。</p>
            </div>
          </div>

          <el-form label-position="top" class="panel-filters">
            <div class="filter-grid">
              <el-form-item label="姓名">
                <el-input v-model="courierFilters.realName" placeholder="按姓名筛选" clearable />
              </el-form-item>
              <el-form-item label="审核状态">
                <el-select v-model="courierFilters.reviewStatus" placeholder="全部" clearable>
                  <el-option label="待审核" value="PENDING" />
                  <el-option label="已通过" value="APPROVED" />
                  <el-option label="已驳回" value="REJECTED" />
                  <el-option label="已停用" value="DISABLED" />
                </el-select>
              </el-form-item>
              <el-form-item label="启用状态">
                <el-select v-model="courierFilters.enabled" placeholder="全部" clearable>
                  <el-option label="已启用" :value="1" />
                  <el-option label="未启用" :value="0" />
                </el-select>
              </el-form-item>
            </div>
            <div class="filter-actions">
              <el-button type="primary" @click="handleCourierSearch">查询</el-button>
              <el-button @click="handleCourierReset">重置</el-button>
            </div>
          </el-form>

          <div class="table-scroll">
            <el-table
              ref="courierTableRef"
              v-loading="courierLoading"
              :data="couriers"
              border
              highlight-current-row
              empty-text="当前筛选条件下暂无配送员数据"
              class="courier-table"
              @current-change="handleCourierSelect"
            >
              <el-table-column prop="id" label="ID" width="88" align="center" />
              <el-table-column prop="realName" label="姓名" min-width="110" />
              <el-table-column prop="phone" label="手机号" min-width="130" />
              <el-table-column label="审核状态" width="120" align="center">
                <template #default="{ row }">
                  <el-tag :type="reviewTagType(row.reviewStatus)">{{ row.reviewStatus || 'PENDING' }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="启用" width="96" align="center">
                <template #default="{ row }">
                  <el-tag :type="Number(row.enabled) === 1 ? 'success' : 'info'">
                    {{ Number(row.enabled) === 1 ? '是' : '否' }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <div class="pagination-wrapper">
            <el-pagination
              v-model:current-page="courierPagination.page"
              v-model:page-size="courierPagination.pageSize"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next"
              :total="courierPagination.total"
              @size-change="handleCourierSizeChange"
              @current-change="handleCourierPageChange"
            />
          </div>
        </section>

        <div class="detail-panels">
          <section class="panel-card selected-card">
            <template v-if="selectedCourier">
              <div class="panel-header">
                <div>
                  <h3>当前查看配送员</h3>
                  <p>默认自动选中当前页第一条配送员记录，无数据时右侧展示空态。</p>
                </div>
              </div>
              <div class="selected-summary">
                <div class="summary-item">
                  <span>配送员ID</span>
                  <strong>{{ selectedCourier.id }}</strong>
                </div>
                <div class="summary-item">
                  <span>姓名</span>
                  <strong>{{ displayText(selectedCourier.realName) }}</strong>
                </div>
                <div class="summary-item">
                  <span>手机号</span>
                  <strong>{{ displayText(selectedCourier.phone) }}</strong>
                </div>
                <div class="summary-item">
                  <span>审核状态</span>
                  <strong>{{ selectedCourier.reviewStatus || 'PENDING' }}</strong>
                </div>
                <div class="summary-item">
                  <span>启用状态</span>
                  <strong>{{ Number(selectedCourier.enabled) === 1 ? '已启用' : '未启用' }}</strong>
                </div>
              </div>
            </template>
            <el-empty v-else description="当前没有可展示的配送员数据" />
          </section>

          <section class="panel-card">
            <div class="panel-header">
              <div>
                <h3>最近异常</h3>
                <p>调用 `GET /api/campus/admin/couriers/{courierProfileId}/exceptions/recent`，只展示当前最小模型下可读到的最近异常。</p>
              </div>
            </div>

            <template v-if="selectedCourier">
              <el-table v-loading="exceptionLoading" :data="exceptions" border empty-text="当前配送员暂无异常记录">
                <el-table-column prop="relayOrderId" label="订单号" min-width="180" />
                <el-table-column label="异常类型" width="140">
                  <template #default="{ row }">
                    {{ displayText(row.exceptionType) }}
                  </template>
                </el-table-column>
                <el-table-column label="异常备注" min-width="220" show-overflow-tooltip>
                  <template #default="{ row }">
                    {{ displayText(row.exceptionRemark) }}
                  </template>
                </el-table-column>
                <el-table-column label="订单状态" width="140">
                  <template #default="{ row }">
                    {{ displayText(row.orderStatus) }}
                  </template>
                </el-table-column>
                <el-table-column label="上报时间" min-width="170">
                  <template #default="{ row }">
                    {{ formatDateTime(row.exceptionReportedAt) }}
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="!exceptionLoading && exceptions.length === 0" description="当前配送员暂无异常记录" />
            </template>
            <el-empty v-else description="请先选择配送员" />
          </section>

          <section class="panel-card">
            <div class="panel-header">
              <div>
                <h3>位置地图预览</h3>
                <p>基于当前选中的低频位置记录加载腾讯地图，只读展示当前点位，不做轨迹回放或实时调度。</p>
              </div>
            </div>

            <template v-if="selectedCourier">
              <div v-if="selectedLocationReport" class="map-preview-layout">
                <div class="map-summary">
                  <div class="summary-item">
                    <span>当前订单</span>
                    <strong>{{ displayText(selectedLocationReport.relayOrderId) }}</strong>
                  </div>
                  <div class="summary-item">
                    <span>上报时间</span>
                    <strong>{{ formatDateTime(selectedLocationReport.reportedAt) }}</strong>
                  </div>
                  <div class="summary-item">
                    <span>位置来源</span>
                    <strong>{{ displayText(selectedLocationReport.source) }}</strong>
                  </div>
                  <div class="summary-item">
                    <span>坐标</span>
                    <strong>{{ coordinateText }}</strong>
                  </div>
                </div>

                <el-alert
                  v-if="!isTencentMapReady"
                  title="当前未配置腾讯地图 key，仅展示坐标摘要。"
                  type="warning"
                  :closable="false"
                  show-icon
                  class="map-hint"
                />
                <el-alert
                  v-else-if="mapErrorMessage"
                  :title="mapErrorMessage"
                  type="error"
                  :closable="false"
                  show-icon
                  class="map-hint"
                />
                <div v-else v-loading="mapLoading" class="map-preview-shell">
                  <div ref="mapContainerRef" class="map-preview-canvas" />
                </div>

                <p class="map-note">
                  选中位置记录后会更新地图中心点。当前仍以后台返回的经纬度为准，不在前端重建轨迹或定位语义。
                </p>
              </div>
              <el-empty v-else description="当前配送员暂无可预览的位置记录" />
            </template>
            <el-empty v-else description="请先选择配送员" />
          </section>

          <section class="panel-card">
            <div class="panel-header">
              <div>
                <h3>低频位置记录</h3>
                <p>调用 `GET /api/campus/admin/couriers/{courierProfileId}/location-reports`，默认按后端返回的 `reportedAt DESC` 只读展示，点击记录可同步更新上方地图预览。</p>
              </div>
            </div>

            <template v-if="selectedCourier">
              <el-table
                ref="locationTableRef"
                v-loading="locationLoading"
                :data="locationReports"
                border
                highlight-current-row
                empty-text="当前配送员暂无位置记录"
                @current-change="handleLocationSelect"
              >
                <el-table-column prop="relayOrderId" label="订单号" min-width="180" />
                <el-table-column label="上报时间" min-width="170">
                  <template #default="{ row }">
                    {{ formatDateTime(row.reportedAt) }}
                  </template>
                </el-table-column>
                <el-table-column prop="latitude" label="纬度" width="120" />
                <el-table-column prop="longitude" label="经度" width="120" />
                <el-table-column label="来源" width="120">
                  <template #default="{ row }">
                    {{ displayText(row.source) }}
                  </template>
                </el-table-column>
                <el-table-column label="备注" min-width="180" show-overflow-tooltip>
                  <template #default="{ row }">
                    {{ displayText(row.note) }}
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="!locationLoading && locationReports.length === 0" description="当前配送员暂无位置记录" />
              <div class="pagination-wrapper">
                <el-pagination
                  v-model:current-page="locationPagination.page"
                  v-model:page-size="locationPagination.pageSize"
                  :page-sizes="[10, 20, 50, 100]"
                  layout="total, sizes, prev, pager, next"
                  :total="locationPagination.total"
                  @size-change="handleLocationSizeChange"
                  @current-change="handleLocationPageChange"
                />
              </div>
            </template>
            <el-empty v-else description="请先选择配送员" />
          </section>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import MainLayout from '../layout/MainLayout.vue'
import {
  getCampusCourierLocationReports,
  getCampusCourierRecentExceptions,
  getCampusCouriers
} from '../api/campus-admin'
import { hasTencentMapKey, loadTencentMapSdk } from '../utils/tencentMap'

const courierTableRef = ref(null)
const locationTableRef = ref(null)
const mapContainerRef = ref(null)
const courierLoading = ref(false)
const exceptionLoading = ref(false)
const locationLoading = ref(false)
const mapLoading = ref(false)
const couriers = ref([])
const exceptions = ref([])
const locationReports = ref([])
const selectedCourier = ref(null)
const selectedLocationReport = ref(null)
const mapErrorMessage = ref('')
let mapInstance = null

const courierFilters = reactive({
  realName: '',
  reviewStatus: '',
  enabled: null
})

const courierPagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const locationPagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const formatDateTime = (value) => value || '暂无'
const displayText = (value) => (value === null || value === undefined || value === '' ? '暂无' : value)
const isTencentMapReady = hasTencentMapKey()

const coordinateText = computed(() => {
  if (!selectedLocationReport.value) {
    return '暂无'
  }
  return `${displayText(selectedLocationReport.value.latitude)}, ${displayText(selectedLocationReport.value.longitude)}`
})

const reviewTagType = (status) => ({
  PENDING: 'warning',
  APPROVED: 'success',
  REJECTED: 'danger',
  DISABLED: 'info'
}[status] || 'info')

const loadCourierList = async () => {
  courierLoading.value = true
  try {
    const response = await getCampusCouriers({
      page: courierPagination.page,
      pageSize: courierPagination.pageSize,
      realName: courierFilters.realName || undefined,
      reviewStatus: courierFilters.reviewStatus || undefined,
      enabled: courierFilters.enabled ?? undefined
    })
    couriers.value = response.records || []
    courierPagination.total = response.total || 0
    await applySelectedCourier()
  } finally {
    courierLoading.value = false
  }
}

const loadRecentExceptions = async () => {
  if (!selectedCourier.value) {
    exceptions.value = []
    return
  }
  exceptionLoading.value = true
  try {
    exceptions.value = await getCampusCourierRecentExceptions(selectedCourier.value.id, { limit: 10 }) || []
  } finally {
    exceptionLoading.value = false
  }
}

const loadLocationReports = async () => {
  if (!selectedCourier.value) {
    locationReports.value = []
    locationPagination.total = 0
    selectedLocationReport.value = null
    mapErrorMessage.value = ''
    return
  }
  locationLoading.value = true
  try {
    const response = await getCampusCourierLocationReports(selectedCourier.value.id, {
      page: locationPagination.page,
      pageSize: locationPagination.pageSize
    })
    locationReports.value = response.records || []
    locationPagination.total = response.total || 0
    await applySelectedLocationReport()
  } finally {
    locationLoading.value = false
  }
}

const loadCourierRelatedPanels = async () => {
  await Promise.all([loadRecentExceptions(), loadLocationReports()])
}

const applySelectedCourier = async () => {
  if (couriers.value.length === 0) {
    selectedCourier.value = null
    exceptions.value = []
    locationReports.value = []
    locationPagination.total = 0
    selectedLocationReport.value = null
    mapErrorMessage.value = ''
    return
  }

  const matchedCourier = couriers.value.find(item => item.id === selectedCourier.value?.id)
  const nextCourier = matchedCourier || couriers.value[0]
  const changed = selectedCourier.value?.id !== nextCourier.id
  selectedCourier.value = nextCourier

  await nextTick()
  courierTableRef.value?.setCurrentRow(nextCourier)

  if (changed) {
    locationPagination.page = 1
    await loadCourierRelatedPanels()
  }
}

const handleCourierSelect = async (row) => {
  if (!row || selectedCourier.value?.id === row.id) {
    return
  }
  selectedCourier.value = row
  locationPagination.page = 1
  await loadCourierRelatedPanels()
}

const applySelectedLocationReport = async () => {
  if (locationReports.value.length === 0) {
    selectedLocationReport.value = null
    mapErrorMessage.value = ''
    return
  }

  const matchedRecord = locationReports.value.find(item => item.id === selectedLocationReport.value?.id)
  const nextRecord = matchedRecord || locationReports.value[0]
  selectedLocationReport.value = nextRecord
  mapErrorMessage.value = ''

  await nextTick()
  locationTableRef.value?.setCurrentRow(nextRecord)
  await renderMapPreview()
}

const handleLocationSelect = async (row) => {
  if (!row || selectedLocationReport.value?.id === row.id) {
    return
  }
  selectedLocationReport.value = row
  mapErrorMessage.value = ''
  await nextTick()
  await renderMapPreview()
}

const handleCourierSearch = () => {
  courierPagination.page = 1
  loadCourierList()
}

const handleCourierReset = () => {
  courierFilters.realName = ''
  courierFilters.reviewStatus = ''
  courierFilters.enabled = null
  courierPagination.page = 1
  loadCourierList()
}

const handleCourierSizeChange = (size) => {
  courierPagination.pageSize = size
  courierPagination.page = 1
  loadCourierList()
}

const handleCourierPageChange = (page) => {
  courierPagination.page = page
  loadCourierList()
}

const handleLocationSizeChange = (size) => {
  locationPagination.pageSize = size
  locationPagination.page = 1
  loadLocationReports()
}

const handleLocationPageChange = (page) => {
  locationPagination.page = page
  loadLocationReports()
}

const renderMapPreview = async () => {
  if (!isTencentMapReady || !selectedLocationReport.value || !mapContainerRef.value) {
    return
  }

  const latitude = Number(selectedLocationReport.value.latitude)
  const longitude = Number(selectedLocationReport.value.longitude)
  if (!Number.isFinite(latitude) || !Number.isFinite(longitude)) {
    mapErrorMessage.value = '当前记录缺少有效经纬度，无法加载地图预览。'
    return
  }

  mapLoading.value = true
  mapErrorMessage.value = ''
  try {
    const TMap = await loadTencentMapSdk()
    const center = new TMap.LatLng(latitude, longitude)
    mapContainerRef.value.innerHTML = ''
    mapInstance = new TMap.Map(mapContainerRef.value, {
      center,
      zoom: 16,
      pitch: 0,
      rotation: 0
    })

    if (TMap.MultiMarker) {
      try {
        new TMap.MultiMarker({
          map: mapInstance,
          geometries: [
            {
              id: `location-${selectedLocationReport.value.id || selectedLocationReport.value.relayOrderId}`,
              position: center
            }
          ]
        })
      } catch (markerError) {
        console.warn('Tencent map marker init failed:', markerError)
      }
    }
  } catch (error) {
    mapErrorMessage.value = error?.message || '腾讯地图加载失败，请检查 key 配置或当前坐标。'
  } finally {
    mapLoading.value = false
  }
}

onMounted(() => loadCourierList())
</script>

<style scoped lang="scss">
.campus-admin-page {
  padding: 20px;
}

.page-header,
.panel-card {
  background: rgba(255, 255, 255, 0.86);
  border: 1px solid rgba(15, 118, 110, 0.08);
  border-radius: 18px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 16px 40px rgba(15, 23, 42, 0.06);
  backdrop-filter: blur(18px);
}

.page-header {
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

.ops-layout {
  display: grid;
  grid-template-columns: minmax(340px, 420px) minmax(0, 1fr);
  gap: 16px;
}

.detail-panels {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;

  h3 {
    margin: 0 0 6px;
    font-size: 18px;
    color: #18181b;
  }

  p {
    margin: 0;
    color: #71717a;
    line-height: 1.5;
  }
}

.panel-filters {
  margin-bottom: 12px;
}

.filter-grid {
  display: grid;
  grid-template-columns: repeat(1, minmax(0, 1fr));
  gap: 12px;
}

.filter-actions {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.table-scroll {
  width: 100%;
  overflow-x: auto;
  overflow-y: hidden;
  padding-bottom: 4px;
  border-radius: 14px;

  &::-webkit-scrollbar {
    height: 8px;
  }

  &::-webkit-scrollbar-thumb {
    background: rgba(15, 118, 110, 0.22);
    border-radius: 999px;
  }
}

.courier-table {
  min-width: 620px;
  border-radius: 14px;

  :deep(.el-table__inner-wrapper::before) {
    display: none;
  }

  :deep(.el-table__header-wrapper th) {
    background: rgba(240, 253, 250, 0.92);
    color: #0f172a;
    font-weight: 850;
  }

  :deep(.el-table__body tr.current-row > td.el-table__cell) {
    background: rgba(15, 118, 110, 0.9);
    color: #f8fafc;
  }

  :deep(.el-table__body tr.current-row .el-tag) {
    border-color: transparent;
    background: rgba(255, 255, 255, 0.92);
  }
}

.selected-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 12px;
}

.summary-item {
  background: #f8fafc;
  border-radius: 12px;
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 8px;

  span {
    color: #71717a;
    font-size: 13px;
  }

  strong {
    color: #18181b;
  }
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.map-preview-layout {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.map-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 12px;
}

.map-preview-shell {
  overflow: hidden;
  border-radius: 16px;
  border: 1px solid #e4e7ed;
  background: #f8fafc;
  min-height: 320px;
}

.map-preview-canvas {
  width: 100%;
  min-height: 320px;
}

.map-hint {
  margin-bottom: 0;
}

.map-note {
  margin: 0;
  color: #71717a;
  line-height: 1.6;
}

@media (max-width: 1280px) {
  .ops-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .campus-admin-page {
    padding: 12px;
  }

  .page-header,
  .panel-card {
    padding: 16px;
    border-radius: 16px;
  }

  .filter-actions {
    flex-wrap: wrap;
  }

  .pagination-wrapper {
    justify-content: flex-start;
    overflow-x: auto;
  }
}
</style>
