<template>
  <MainLayout>
    <div class="ops-hero">
      <div>
        <span class="eyebrow">Campus Part-time Ops</span>
        <h2>欢迎回来，{{ userStore.currentUserInfo.name || '校园运营员' }}</h2>
        <p>这里是校内兼职平台的运营总览，用于查看订单、人员、异常和演示链路状态。</p>
      </div>
      <div class="hero-badge">
        <span>试运营</span>
        <strong>本地联调 / H2</strong>
      </div>
    </div>
    
    <div class="stats-cards">
      <div
        v-for="stat in statsData"
        :key="stat.title"
        class="stat-card"
      >
        <div class="stat-icon" :style="{ backgroundColor: stat.color + '20' }">
          <i :class="stat.icon" :style="{ color: stat.color }"></i>
        </div>
        <div class="stat-content">
          <p class="stat-title">{{ stat.title }}</p>
          <h3 class="stat-value">{{ stat.value }}</h3>
        </div>
      </div>
    </div>
    
    <div class="charts-section">
      <div class="chart-card">
        <div class="section-title">
          <span>趋势观察</span>
          <small>兼容旧统计接口</small>
        </div>
        <div ref="trendChartRef" class="chart-container"></div>
      </div>
      
      <div class="chart-card">
        <div class="section-title">
          <span>服务热度</span>
          <small>示例排行</small>
        </div>
        <div ref="popularChartRef" class="chart-container"></div>
      </div>
    </div>
    
    <div class="recent-orders">
      <div class="section-title">
        <span>最近订单快照</span>
        <small>只读预览，不改变旧订单语义</small>
      </div>
      <el-table v-loading="loading" :data="recentOrders" style="width: 100%">
        <el-table-column prop="id" label="订单号" width="120" />
        <el-table-column prop="customerName" label="客户" />
        <el-table-column prop="totalAmount" label="金额" width="100">
          <template #default="scope">
            ¥{{ Number(scope.row.totalAmount).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag
              :type="getStatusType(scope.row.status)"
            >
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="日期" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
      </el-table>
    </div>
  </MainLayout>
</template>

<script setup>
import MainLayout from '../layout/MainLayout.vue'
import { useUserStore } from '../stores/user'
import { onMounted, ref, watch } from 'vue'
import { echarts } from '../utils/echarts'
import { getDashboardData } from '../api/statistics'
import { getOrderList } from '../api/order'

const userStore = useUserStore()
const loading = ref(false)

// 统计数据
const statsData = ref([
  { title: '模拟流水', value: '¥0', icon: 'el-icon-s-data', color: '#0f9f8f' },
  { title: '订单快照', value: '0', icon: 'el-icon-s-order', color: '#0284c7' },
  { title: '用户规模', value: '0', icon: 'el-icon-user', color: '#16a34a' },
  { title: '平均单值', value: '¥0', icon: 'el-icon-money', color: '#f59e0b' }
])

// 最近订单
const recentOrders = ref([])

// 图表数据
const orderTrend = ref({
  dates: [],
  sales: [],
  orders: []
})
const popularDishes = ref([])

const trendChartRef = ref(null)
const popularChartRef = ref(null)
let trendChart = null
let popularChart = null

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).replace(/\//g, '-')
}

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

// 获取状态对应的标签类型
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

// 加载仪表盘数据
const loadDashboardData = async () => {
  try {
    const res = await getDashboardData()
    if (res) {
      // 更新统计数据
      statsData.value = [
        { title: '模拟流水', value: res.totalSales || '¥0', icon: 'el-icon-s-data', color: '#0f9f8f' },
        { title: '订单快照', value: String(res.totalOrders || 0), icon: 'el-icon-s-order', color: '#0284c7' },
        { title: '用户规模', value: String(res.totalUsers || 0), icon: 'el-icon-user', color: '#16a34a' },
        { title: '平均单值', value: res.avgOrderValue || '¥0', icon: 'el-icon-money', color: '#f59e0b' }
      ]
      
      // 更新图表数据
      if (res.orderTrend) {
        orderTrend.value = res.orderTrend
      }
      if (res.popularDishes) {
        popularDishes.value = res.popularDishes
      }
    }
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
  }
}

// 加载最近订单
const loadRecentOrders = async () => {
  loading.value = true
  try {
    const res = await getOrderList({ page: 1, size: 5 })
    recentOrders.value = res.records || []
  } catch (error) {
    console.error('加载最近订单失败:', error)
  } finally {
    loading.value = false
  }
}

// 初始化订单趋势图
const initTrendChart = () => {
  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value)
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'cross',
          label: {
            backgroundColor: '#6a7985'
          }
        }
      },
      legend: {
        data: ['模拟流水', '订单数'],
        top: 0
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: [
        {
          type: 'category',
          boundaryGap: false,
          data: orderTrend.value.dates
        }
      ],
      yAxis: [
        {
          type: 'value',
          name: '模拟流水',
          position: 'left',
          axisLabel: {
            formatter: '¥{value}'
          }
        },
        {
          type: 'value',
          name: '订单数',
          position: 'right',
          axisLabel: {
            formatter: '{value}单'
          }
        }
      ],
      series: [
        {
          name: '模拟流水',
          type: 'line',
          stack: 'Total',
          areaStyle: {
            opacity: 0.3
          },
          emphasis: {
            focus: 'series'
          },
          data: orderTrend.value.sales
        },
        {
          name: '订单数',
          type: 'line',
          yAxisIndex: 1,
          emphasis: {
            focus: 'series'
          },
          data: orderTrend.value.orders
        }
      ]
    }
    trendChart.setOption(option)
  }
}

// 初始化热门菜品排行图
const initPopularChart = () => {
  if (popularChartRef.value) {
    popularChart = echarts.init(popularChartRef.value)
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'value',
        axisLabel: {
          formatter: '{value}份'
        }
      },
      yAxis: {
        type: 'category',
        data: popularDishes.value.map(dish => dish.name)
      },
      series: [
        {
          name: '销量',
          type: 'bar',
          data: popularDishes.value.map(dish => dish.sales),
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: '#0f9f8f' },
              { offset: 1, color: '#38bdf8' }
            ])
          }
        }
      ]
    }
    popularChart.setOption(option)
  }
}

// 响应式调整图表大小
const handleResize = () => {
  trendChart?.resize()
  popularChart?.resize()
}

onMounted(async () => {
  await loadDashboardData()
  await loadRecentOrders()
  
  // 初始化图表
  initTrendChart()
  initPopularChart()
  window.addEventListener('resize', handleResize)
})

// 监听数据变化，更新图表
watch([orderTrend, popularDishes], () => {
  if (trendChart) {
    trendChart.setOption({
      xAxis: [{
        data: orderTrend.value.dates
      }],
      series: [
        {
          data: orderTrend.value.sales
        },
        {
          data: orderTrend.value.orders
        }
      ]
    })
  }
  if (popularChart) {
    popularChart.setOption({
      yAxis: {
        data: popularDishes.value.map(dish => dish.name)
      },
      series: [{
        data: popularDishes.value.map(dish => dish.sales)
      }]
    })
  }
}, { deep: true })
</script>

<style lang="scss" scoped>
.ops-hero {
  position: relative;
  display: flex;
  justify-content: space-between;
  gap: 24px;
  overflow: hidden;
  background:
    linear-gradient(135deg, rgba(4, 47, 46, 0.95) 0%, rgba(15, 118, 110, 0.9) 48%, rgba(14, 165, 233, 0.78) 100%),
    radial-gradient(circle at 80% 20%, rgba(217, 249, 157, 0.35), transparent 30%);
  border-radius: 28px;
  padding: 34px 40px;
  margin-bottom: 22px;
  box-shadow: 0 28px 70px rgba(15, 23, 42, 0.18);

  &::after {
    content: '';
    position: absolute;
    right: -50px;
    bottom: -70px;
    width: 260px;
    height: 260px;
    border-radius: 50%;
    border: 42px solid rgba(204, 251, 241, 0.16);
  }

  .eyebrow {
    display: inline-flex;
    margin-bottom: 12px;
    color: #bef264;
    font-size: 12px;
    font-weight: 800;
    letter-spacing: 0.18em;
    text-transform: uppercase;
  }
  
  h2 {
    position: relative;
    z-index: 1;
    font-size: 30px;
    font-weight: 850;
    color: #ffffff;
    margin: 0 0 12px 0;
    letter-spacing: -0.02em;
  }
  
  p {
    position: relative;
    z-index: 1;
    max-width: 620px;
    font-size: 15px;
    color: rgba(240, 253, 250, 0.84);
    margin: 0;
  }

  .hero-badge {
    position: relative;
    z-index: 1;
    align-self: flex-start;
    min-width: 150px;
    padding: 16px 18px;
    border-radius: 20px;
    background: rgba(255, 255, 255, 0.16);
    border: 1px solid rgba(255, 255, 255, 0.26);
    color: #ffffff;
    backdrop-filter: blur(14px);

    span {
      display: block;
      margin-bottom: 4px;
      color: #ccfbf1;
      font-size: 12px;
    }

    strong {
      font-size: 16px;
    }
  }
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
  
  .stat-card {
    background: rgba(255, 255, 255, 0.86);
    border: 1px solid rgba(15, 118, 110, 0.1);
    border-radius: 22px;
    padding: 20px;
    display: flex;
    align-items: center;
    gap: 15px;
    box-shadow: 0 18px 40px rgba(15, 23, 42, 0.07);
    backdrop-filter: blur(10px);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 24px 48px rgba(15, 23, 42, 0.12);
    }
    
    .stat-icon {
      width: 50px;
      height: 50px;
      border-radius: 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 20px;
    }
    
    .stat-content {
      flex: 1;
      
      .stat-title {
        font-size: 14px;
        color: #606266;
        margin: 0 0 5px 0;
      }
      
      .stat-value {
        font-size: 24px;
        font-weight: 600;
        color: #303133;
        margin: 0;
      }
    }
  }
}

.charts-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
  
  .chart-card {
    background: rgba(255, 255, 255, 0.88);
    border: 1px solid rgba(15, 118, 110, 0.1);
    border-radius: 24px;
    padding: 22px;
    box-shadow: 0 18px 40px rgba(15, 23, 42, 0.07);
    
    .chart-container {
      height: 300px;
      width: 100%;
    }
  }
}

.recent-orders {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(15, 118, 110, 0.1);
  border-radius: 24px;
  padding: 22px;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.07);
  
  .el-table {
    border-radius: 8px;
    overflow: hidden;
    
    .el-table__header-wrapper {
      background-color: #f5f7fa;
    }
    
    .el-table__row {
      transition: background-color 0.2s ease;
      
      &:hover {
        background-color: #f5f7fa;
      }
    }
  }
}

.section-title {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 18px;

  span {
    font-size: 18px;
    font-weight: 800;
    color: #0f172a;
  }

  small {
    color: #0f766e;
    font-size: 12px;
    font-weight: 700;
  }
}
</style>
