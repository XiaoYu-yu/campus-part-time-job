<!--
  订单兼容管理页面组件
  @description 旧模块兼容 — 订单管理，保留旧模块兼容能力，不作为当前校园兼职主业务入口
-->
<template>
  <div class="order-management">
    <div class="compat-notice">
      <el-alert type="info" :closable="false" show-icon>
        <template #title>
          该页面保留旧模块兼容能力，不作为当前校园兼职主业务入口。
        </template>
      </el-alert>
    </div>
    <h2>订单兼容管理</h2>
    <div class="content">
      <!-- 搜索和筛选 -->
      <div class="search-filter">
        <el-input
          v-model="searchForm.orderNo"
          placeholder="搜索订单号"
          clearable
          style="width: 200px; margin-right: 10px"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-input
          v-model="searchForm.customerName"
          placeholder="客户姓名"
          clearable
          style="width: 150px; margin-right: 10px"
          @keyup.enter="handleSearch"
        />
        <el-select
          v-model="searchForm.status"
          placeholder="订单状态"
          clearable
          style="width: 150px; margin-right: 10px"
        >
          <el-option label="待支付" :value="0" />
          <el-option label="处理中" :value="1" />
          <el-option label="已配送" :value="2" />
          <el-option label="已完成" :value="3" />
          <el-option label="已取消" :value="4" />
        </el-select>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="width: 300px; margin-right: 10px"
          value-format="YYYY-MM-DD"
        />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
      
      <!-- 订单列表 -->
      <el-table v-loading="loading" :data="orderList" style="width: 100%; margin-top: 20px" border>
        <el-table-column prop="id" label="订单号" width="120" />
        <el-table-column prop="customerName" label="客户姓名" width="120" />
        <el-table-column prop="customerPhone" label="联系电话" width="150" />
        <el-table-column prop="totalAmount" label="总金额" width="100" align="right">
          <template #default="scope">
            ¥{{ Number(scope.row.totalAmount).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="100">
          <template #default="scope">
            <el-tag
              :type="getStatusType(scope.row.status)"
              size="small"
            >
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="viewOrderDetail(scope.row.id)"
            >
              详情
            </el-button>
            <el-dropdown size="small" style="margin-left: 5px;">
              <el-button size="small">
                更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleStatusUpdate(scope.row.id, 1)">标记为处理中</el-dropdown-item>
                  <el-dropdown-item @click="handleStatusUpdate(scope.row.id, 2)">标记为已配送</el-dropdown-item>
                  <el-dropdown-item @click="handleStatusUpdate(scope.row.id, 3)">标记为已完成</el-dropdown-item>
                  <el-dropdown-item @click="handleStatusUpdate(scope.row.id, 4)">标记为已取消</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination" style="margin-top: 20px; text-align: right">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
      
      <!-- 订单详情抽屉 -->
      <el-drawer
        v-model="drawerVisible"
        title="订单详情"
        direction="rtl"
        size="50%"
      >
        <div v-if="currentOrder" class="order-detail">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="订单号">{{ currentOrder.id }}</el-descriptions-item>
            <el-descriptions-item label="客户姓名">{{ currentOrder.customerName }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ currentOrder.customerPhone }}</el-descriptions-item>
            <el-descriptions-item label="配送地址">{{ currentOrder.customerAddress }}</el-descriptions-item>
            <el-descriptions-item label="总金额">¥{{ Number(currentOrder.totalAmount).toFixed(2) }}</el-descriptions-item>
            <el-descriptions-item label="订单状态">
              <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusLabel(currentOrder.status) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatDateTime(currentOrder.createdAt) }}</el-descriptions-item>
            <el-descriptions-item label="支付时间">{{ currentOrder.paymentTime ? formatDateTime(currentOrder.paymentTime) : '未支付' }}</el-descriptions-item>
            <el-descriptions-item label="送达时间">{{ currentOrder.deliveryTime ? formatDateTime(currentOrder.deliveryTime) : '未送达' }}</el-descriptions-item>
          </el-descriptions>
          
          <h3 style="margin-top: 20px; margin-bottom: 10px">订单商品</h3>
          <el-table :data="currentOrder.items" style="width: 100%" border>
            <el-table-column prop="name" label="商品名称" />
            <el-table-column prop="quantity" label="数量" width="80" align="center" />
            <el-table-column prop="price" label="单价" width="100" align="right">
              <template #default="scope">
                ¥{{ Number(scope.row.price).toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="total" label="小计" width="100" align="right">
              <template #default="scope">
                ¥{{ Number(scope.row.total).toFixed(2) }}
              </template>
            </el-table-column>
          </el-table>
          
          <div style="margin-top: 20px; text-align: right">
            <el-button type="primary" @click="drawerVisible = false">关闭</el-button>
          </div>
        </div>
      </el-drawer>
    </div>
  </div>
</template>

<script setup>
/**
 * 订单管理页面逻辑
 * @module Order
 */
import { ref, onMounted } from 'vue'
import { Search, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getOrderList, getOrderById, updateOrderStatus } from '../api/order'

// ==================== 响应式数据 ====================

/** @type {import('vue').Ref<boolean>} 表格加载状态 */
const loading = ref(false)

/** @type {import('vue').Ref<Array>} 订单列表数据 */
const orderList = ref([])

/** @type {import('vue').Ref<Object|null>} 当前查看的订单详情 */
const currentOrder = ref(null)

/** @type {import('vue').Ref<number>} 订单总数 */
const total = ref(0)

/**
 * @typedef {Object} SearchForm
 * @property {string} orderNo - 订单号
 * @property {string} customerName - 客户姓名
 * @property {number|null} status - 订单状态码
 */

/** @type {import('vue').Ref<SearchForm>} 搜索表单数据 */
const searchForm = ref({
  orderNo: '',
  customerName: '',
  status: null
})

/** @type {import('vue').Ref<Array<string>>} 日期范围 [开始日期, 结束日期] */
const dateRange = ref([])

/** @type {import('vue').Ref<number>} 当前页码 */
const currentPage = ref(1)

/** @type {import('vue').Ref<number>} 每页显示条数 */
const pageSize = ref(10)

/** @type {import('vue').Ref<boolean>} 详情抽屉显示状态 */
const drawerVisible = ref(false)

// ==================== 方法 ====================

/**
 * 格式化日期时间
 * @param {string} dateTime - ISO格式日期字符串
 * @returns {string} 格式化后的日期时间字符串 (YYYY-MM-DD HH:mm:ss)
 */
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  }).replace(/\//g, '-')
}

/**
 * 获取订单状态对应的标签类型
 * @param {number} status - 订单状态码
 * @returns {string} Element Plus 标签类型 (success/warning/info/danger)
 */
const getStatusLabel = (status) => {
  const statusMap = {
    0: '待支付',
    1: '处理中',
    2: '已配送',
    3: '已完成',
    4: '已取消'
  }

  return statusMap[status] || '未知状态'
}

const getStatusType = (status) => {
  switch (status) {
    case 0:
      return 'warning'
    case 1:
      return 'info'
    case 2:
      return 'primary'
    case 3:
      return 'success'
    case 4:
      return 'danger'
    default:
      return ''
  }
}

/**
 * 加载订单列表
 * @description 根据当前分页、搜索条件和日期范围获取订单列表
 * @returns {Promise<void>}
 */
const loadOrderList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      orderNo: searchForm.value.orderNo || undefined,
      customerName: searchForm.value.customerName || undefined,
      status: searchForm.value.status || undefined
    }

    // 添加日期范围参数
    if (dateRange.value && dateRange.value.length === 2) {
      params.beginTime = dateRange.value[0] + ' 00:00:00'
      params.endTime = dateRange.value[1] + ' 23:59:59'
    }

    const res = await getOrderList(params)
    orderList.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    console.error('加载订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 搜索处理
 * @description 重置页码并重新加载数据
 */
const handleSearch = () => {
  currentPage.value = 1
  loadOrderList()
}

/**
 * 查看订单详情
 * @param {number} orderId - 订单ID
 * @description 获取订单详情并显示在抽屉中
 */
const viewOrderDetail = async (orderId) => {
  try {
    const res = await getOrderById(orderId)
    currentOrder.value = res
    drawerVisible.value = true
  } catch (error) {
    console.error('加载订单详情失败:', error)
  }
}

/**
 * 更新订单状态
 * @param {number} orderId - 订单ID
 * @param {number} status - 新状态码
 * @description 更新订单状态并刷新列表和详情
 */
const handleStatusUpdate = async (orderId, status) => {
  try {
    await updateOrderStatus(orderId, status)
    ElMessage.success('订单状态已更新')
    loadOrderList()
    // 如果当前正在查看详情，也刷新详情
    if (drawerVisible.value && currentOrder.value && currentOrder.value.id === orderId) {
      viewOrderDetail(orderId)
    }
  } catch (error) {
    console.error('更新订单状态失败:', error)
  }
}

/**
 * 每页条数改变
 * @param {number} size - 新的每页条数
 */
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadOrderList()
}

/**
 * 页码改变
 * @param {number} current - 新的页码
 */
const handleCurrentChange = (current) => {
  currentPage.value = current
  loadOrderList()
}

// ==================== 生命周期 ====================

/**
 * 组件挂载时初始化数据
 */
onMounted(() => {
  loadOrderList()
})
</script>

<style scoped>
.order-management {
  .compat-notice {
    margin-bottom: 16px;
  }

  h2 {
    margin-bottom: 20px;
    color: #303133;
  }
  
  .content {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .search-filter {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .order-detail {
    padding: 10px;
  }
}
</style>
