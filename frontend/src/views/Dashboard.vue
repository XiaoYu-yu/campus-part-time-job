<template>
  <MainLayout>
    <div class="dashboard-surface">
      <div class="ops-hero">
        <div>
          <span class="eyebrow">Campus Part-time Ops</span>
          <h2>欢迎回来，{{ currentOperatorName }}</h2>
          <p>聚合校内代送、兼职身份、异常、售后和结算审计的试运营控制台。</p>
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
          <div class="stat-icon" :style="{ '--accent': stat.color }">
            <i :class="stat.icon"></i>
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
        <el-table v-loading="loading" :data="recentOrders" class="glass-table" style="width: 100%">
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
                effect="dark"
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
    </div>
  </MainLayout>
</template>

<script setup>
import MainLayout from '../layout/MainLayout.vue'
import { useUserStore } from '../stores/user'
import { computed, onMounted, ref, watch } from 'vue'
import { echarts } from '../utils/echarts'
import { getDashboardData } from '../api/statistics'
import { getOrderList } from '../api/order'
import { normalizeDisplayText } from '../utils/text'

const userStore = useUserStore()
const loading = ref(false)
const currentOperatorName = computed(() => normalizeDisplayText(userStore.currentUserInfo.name || '校园运营员'))

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
        top: 0,
        textStyle: {
          color: 'rgba(226, 232, 240, 0.72)'
        }
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
          data: orderTrend.value.dates,
          axisLine: {
            lineStyle: { color: 'rgba(226, 232, 240, 0.2)' }
          },
          axisLabel: {
            color: 'rgba(226, 232, 240, 0.58)'
          }
        }
      ],
      yAxis: [
        {
          type: 'value',
          name: '模拟流水',
          position: 'left',
          axisLabel: {
            formatter: '¥{value}',
            color: 'rgba(226, 232, 240, 0.58)'
          },
          splitLine: {
            lineStyle: { color: 'rgba(226, 232, 240, 0.08)' }
          }
        },
        {
          type: 'value',
          name: '订单数',
          position: 'right',
          axisLabel: {
            formatter: '{value}单',
            color: 'rgba(226, 232, 240, 0.58)'
          }
        }
      ],
      series: [
        {
          name: '模拟流水',
          type: 'line',
          stack: 'Total',
          smooth: true,
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
          smooth: true,
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

// 初始化服务热度排行图
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
          formatter: '{value}份',
          color: 'rgba(226, 232, 240, 0.58)'
        },
        splitLine: {
          lineStyle: { color: 'rgba(226, 232, 240, 0.08)' }
        }
      },
      yAxis: {
        type: 'category',
        data: popularDishes.value.map(dish => dish.name),
        axisLabel: {
          color: 'rgba(226, 232, 240, 0.68)'
        }
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
.dashboard-surface {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.ops-hero,
.stat-card,
.chart-card,
.recent-orders {
  border: 1px solid rgba(255, 255, 255, 0.14);
  background: rgba(255, 255, 255, 0.1);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.18);
  backdrop-filter: blur(22px);
}

.ops-hero {
  position: relative;
  display: flex;
  justify-content: space-between;
  gap: 24px;
  overflow: hidden;
  min-height: 180px;
  border-radius: 30px;
  padding: 34px 40px;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background:
      radial-gradient(circle at 78% 20%, rgba(190, 242, 100, 0.22), transparent 28%),
      linear-gradient(135deg, rgba(255, 255, 255, 0.14), rgba(255, 255, 255, 0.03));
  }

  &::after {
    content: '';
    position: absolute;
    right: -56px;
    bottom: -80px;
    width: 260px;
    height: 260px;
    border-radius: 50%;
    border: 42px solid rgba(255, 255, 255, 0.06);
  }

  .eyebrow {
    position: relative;
    z-index: 1;
    display: inline-flex;
    margin-bottom: 12px;
    color: #bef264;
    font-size: 12px;
    font-weight: 900;
    letter-spacing: 0.18em;
    text-transform: uppercase;
  }

  h2 {
    position: relative;
    z-index: 1;
    margin: 0 0 12px 0;
    color: #ffffff;
    font-size: 34px;
    font-weight: 950;
    letter-spacing: -0.05em;
  }

  p {
    position: relative;
    z-index: 1;
    max-width: 620px;
    margin: 0;
    color: rgba(226, 232, 240, 0.78);
    font-size: 15px;
    line-height: 1.8;
  }

  .hero-badge {
    position: relative;
    z-index: 1;
    align-self: flex-start;
    min-width: 162px;
    padding: 16px 18px;
    border: 1px solid rgba(255, 255, 255, 0.18);
    border-radius: 20px;
    background: rgba(255, 255, 255, 0.12);
    color: #ffffff;
    backdrop-filter: blur(14px);

    span {
      display: block;
      margin-bottom: 4px;
      color: rgba(204, 251, 241, 0.8);
      font-size: 12px;
    }

    strong {
      font-size: 16px;
    }
  }
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;

  .stat-card {
    display: flex;
    align-items: center;
    gap: 15px;
    padding: 18px;
    border-radius: 22px;
    transition: transform 0.25s ease, background 0.25s ease;

    &:hover {
      transform: translateY(-4px);
      background: rgba(255, 255, 255, 0.15);
    }

    .stat-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 48px;
      height: 48px;
      border-radius: 16px;
      background: rgba(255, 255, 255, 0.1);
      color: var(--accent);
      font-size: 20px;
      box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.16);
    }

    .stat-content {
      flex: 1;

      .stat-title {
        margin: 0 0 6px 0;
        color: rgba(226, 232, 240, 0.62);
        font-size: 13px;
        font-weight: 700;
      }

      .stat-value {
        margin: 0;
        color: #ffffff;
        font-size: 26px;
        font-weight: 900;
        letter-spacing: -0.03em;
      }
    }
  }
}

.charts-section {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(0, 0.8fr);
  gap: 16px;

  @media (max-width: 960px) {
    grid-template-columns: 1fr;
  }

  .chart-card {
    border-radius: 24px;
    padding: 22px;

    .chart-container {
      width: 100%;
      height: 300px;
    }
  }
}

.recent-orders {
  overflow: hidden;
  border-radius: 24px;
  padding: 22px;

  .glass-table {
    overflow: hidden;
    border-radius: 18px;
    background: transparent;

    :deep(.el-table__inner-wrapper::before) {
      display: none;
    }

    :deep(.el-table__header-wrapper th) {
      background: rgba(255, 255, 255, 0.12);
      color: rgba(248, 250, 252, 0.88);
      border-bottom: 1px solid rgba(255, 255, 255, 0.1);
      font-weight: 800;
    }

    :deep(.el-table__body-wrapper tr) {
      background: rgba(255, 255, 255, 0.04);
      color: rgba(226, 232, 240, 0.82);
      transition: background 0.2s ease;

      &:hover > td.el-table__cell {
        background: rgba(255, 255, 255, 0.1);
      }
    }

    :deep(.el-table__cell) {
      border-bottom: 1px solid rgba(255, 255, 255, 0.08);
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
    color: #f8fafc;
    font-size: 18px;
    font-weight: 900;
  }

  small {
    color: #9bffb8;
    font-size: 12px;
    font-weight: 800;
  }
}
</style>
