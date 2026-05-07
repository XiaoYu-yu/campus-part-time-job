<template>
  <UserLayout>
    <div class="orders-page">
      <h1 class="page-title">我的订单</h1>

      <div class="order-filter">
        <button v-for="item in filters" :key="item.value" class="filter-btn" :class="{ active: activeFilter === item.value }" @click="changeFilter(item.value)">
          {{ item.label }}
        </button>
      </div>

      <div v-if="orders.length" class="order-list">
        <div v-for="order in orders" :key="order.id" class="order-card">
          <div class="order-header">
            <span>订单号：{{ order.id }}</span>
            <strong :class="statusClass(order.status)">{{ statusLabel(order.status) }}</strong>
          </div>
          <div class="order-items">
            <div v-for="item in order.items || []" :key="item.id" class="order-item">
              <span>{{ item.name }}</span>
              <span>x{{ item.quantity }}</span>
              <span>¥{{ Number(item.total || item.price * item.quantity).toFixed(2) }}</span>
            </div>
          </div>
          <div class="order-footer">
            <div>合计：<strong>¥{{ Number(order.totalAmount).toFixed(2) }}</strong></div>
            <div class="actions">
              <button v-if="order.status === 0" @click="payOrder(order.id)">去支付</button>
              <button v-if="order.status === 0 || order.status === 1" @click="cancelOrder(order.id)">取消订单</button>
              <button v-if="order.status === 3 || order.status === 4" @click="reorder(order.id)">再来一单</button>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">还没有相关订单</div>
    </div>
  </UserLayout>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import UserLayout from '../../layout/UserLayout.vue'
import { cancelUserOrder, getUserOrders, payUserOrder, reorderUserOrder } from '../../api/customer-order'

const route = useRoute()
const activeFilter = ref(route.query.status ? Number(route.query.status) : 'all')
const orders = ref([])

const filters = [
  { label: '全部', value: 'all' },
  { label: '待支付', value: 0 },
  { label: '处理中', value: 1 },
  { label: '已配送', value: 2 },
  { label: '已完成', value: 3 },
  { label: '已取消', value: 4 }
]

const statusLabel = (status) => ({
  0: '待支付',
  1: '处理中',
  2: '已配送',
  3: '已完成',
  4: '已取消'
}[status] || '未知状态')

const statusClass = (status) => ({
  0: 'pending',
  1: 'processing',
  2: 'delivery',
  3: 'completed',
  4: 'cancelled'
}[status] || '')

const loadOrders = async () => {
  const params = {
    page: 1,
    pageSize: 20,
    status: activeFilter.value === 'all' ? undefined : activeFilter.value
  }
  const res = await getUserOrders(params)
  orders.value = res.records || []
}

const changeFilter = (value) => {
  activeFilter.value = value
  loadOrders()
}

const payOrder = async (id) => {
  await payUserOrder(id)
  ElMessage.success('订单支付成功')
  await loadOrders()
}

const cancelOrder = async (id) => {
  await cancelUserOrder(id)
  ElMessage.success('订单已取消')
  await loadOrders()
}

const reorder = async (id) => {
  await reorderUserOrder(id)
  ElMessage.success('已重新加入购物车')
}

onMounted(() => loadOrders())
</script>

<style scoped lang="scss">
.orders-page {
  padding: 16px;
}

.page-title {
  margin: 0 0 16px;
  font-size: 20px;
}

.order-filter {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  margin-bottom: 14px;
  -webkit-overflow-scrolling: touch;
}

.filter-btn,
.actions button {
  border: none;
  border-radius: 999px;
  cursor: pointer;
}

.filter-btn {
  background: #ffffff;
  padding: 8px 14px;
  font-size: 13px;
  color: #52525b;
  border: 1px solid #e4e4e7;

  &.active {
    background: #0f9f8f;
    color: #ffffff;
    border-color: #0f9f8f;
  }
}

.order-list {
  display: grid;
  gap: 12px;
}

.order-card {
  background: #ffffff;
  border-radius: 14px;
  padding: 16px;
  border: 1px solid #e4e4e7;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.order-header,
.order-item,
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-header {
  font-size: 13px;
  color: #71717a;
}

.order-items {
  margin: 14px 0;
}

.order-item {
  padding: 8px 0;
  border-bottom: 1px solid #f4f4f5;
  font-size: 14px;
}

.order-item:last-child {
  border-bottom: none;
}

.actions {
  display: flex;
  gap: 8px;
}

.actions button {
  background: rgba(15, 159, 143, 0.1);
  color: #0f9f8f;
  padding: 8px 14px;
  font-size: 13px;
  font-weight: 600;
}

.pending { color: #f59e0b; }
.processing { color: #3b82f6; }
.delivery { color: #0f9f8f; }
.completed { color: #10b981; }
.cancelled { color: #a1a1aa; }

.empty-state {
  background: #ffffff;
  border-radius: 14px;
  border: 1px solid #e4e4e7;
  padding: 40px 16px;
  text-align: center;
  color: #a1a1aa;
  font-size: 14px;
}
</style>
