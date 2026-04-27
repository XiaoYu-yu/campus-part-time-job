<template>
  <div class="shop-status-management">
    <div class="compat-notice">
      <el-alert type="info" :closable="false" show-icon>
        <template #title>
          该页面保留旧模块兼容能力，不作为当前校园兼职主业务入口。
        </template>
      </el-alert>
    </div>
    <h2>店铺状态兼容</h2>

    <div class="status-card">
      <div class="status-header">
        <h3>当前营业状态</h3>
        <el-switch v-model="shopStatus.isOpen" active-text="营业中" inactive-text="休息中" />
      </div>
      <div class="status-info" :class="{ closed: !shopStatus.isOpen }">
        <div>{{ shopStatus.isOpen ? '店铺当前营业中' : '店铺当前休息中' }}</div>
        <div>最后更新：{{ shopStatus.lastUpdated }}</div>
      </div>
    </div>

    <div class="hours-card">
      <h3>营业时间段设置</h3>
      <div class="table-header">
        <div>星期</div>
        <div>状态</div>
        <div>营业时间</div>
      </div>
      <div v-for="day in shopStatus.businessHours" :key="day.day" class="table-row">
        <div>{{ day.dayName }}</div>
        <div><el-switch v-model="day.isOpen" /></div>
        <div class="time-cell">
          <el-time-select v-model="day.openTime" :disabled="!day.isOpen" :picker-options="timeOptions" />
          <span>-</span>
          <el-time-select v-model="day.closeTime" :disabled="!day.isOpen" :picker-options="timeOptions" />
        </div>
      </div>
    </div>

    <div class="notice-card">
      <h3>休息公告</h3>
      <el-input v-model="shopStatus.restNotice" type="textarea" :rows="3" placeholder="请输入休息公告内容" />
      <div class="actions">
        <el-button type="primary" @click="handleSave">保存设置</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getShopStatus, updateShopStatus } from '../api/shop'

const timeOptions = {
  start: '00:00',
  step: '00:30',
  end: '23:30'
}

const shopStatus = ref({
  isOpen: true,
  restNotice: '',
  lastUpdated: '',
  businessHours: []
})

const loadShopStatus = async () => {
  shopStatus.value = await getShopStatus()
}

const handleSave = async () => {
  await updateShopStatus(shopStatus.value)
  ElMessage.success('店铺状态已保存')
  await loadShopStatus()
}

onMounted(() => loadShopStatus())
</script>

<style scoped lang="scss">
.shop-status-management {
  .compat-notice {
    margin-bottom: 16px;
  }

  padding: 20px;
}

.status-card,
.hours-card,
.notice-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 18px;
}

.status-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.status-info {
  display: flex;
  justify-content: space-between;
  background: #f0f9eb;
  border-radius: 12px;
  padding: 14px 16px;
}

.status-info.closed {
  background: #fef0f0;
}

.table-header,
.table-row {
  display: grid;
  grid-template-columns: 1fr 1fr 2fr;
  gap: 12px;
  align-items: center;
}

.table-header {
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
  font-weight: 700;
}

.table-row {
  padding: 14px 0;
  border-bottom: 1px solid #f0f0f0;
}

.time-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
