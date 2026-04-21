<template>
  <div class="statistics-management">
    <h2>数据统计</h2>
    
    <!-- 指标区 -->
    <div class="metrics-container">
      <div 
        v-for="(metric, index) in metricsData" 
        :key="index"
        class="metric-card"
        :style="{ borderLeftColor: metric.color }"
      >
        <div class="metric-icon" :style="{ backgroundColor: metric.color + '20' }">
          <i :class="metric.icon" :style="{ color: metric.color }"></i>
        </div>
        <div class="metric-info">
          <div class="metric-title">{{ metric.title }}</div>
          <div class="metric-value">{{ metric.value }}</div>
          <div class="metric-change" :class="metric.changeType">
            {{ metric.change }}
          </div>
        </div>
      </div>
    </div>
    
    <!-- 图表区 -->
    <div class="charts-container">
      <!-- 销售趋势图 -->
      <div class="chart-card">
        <h3>销售趋势</h3>
        <div ref="salesTrendChart" class="chart"></div>
      </div>
      
      <!-- 菜品销售分布图 -->
      <div class="chart-card">
        <h3>菜品销售分布</h3>
        <div ref="dishDistributionChart" class="chart"></div>
      </div>
      
      <!-- 订单状态分布图 -->
      <div class="chart-card">
        <h3>订单状态分布</h3>
        <div ref="orderStatusChart" class="chart"></div>
      </div>
      
      <!-- 用户地域分布图 -->
      <div class="chart-card">
        <h3>用户地域分布</h3>
        <div ref="userRegionChart" class="chart"></div>
      </div>
      
      <!-- 销售时段分布图 -->
      <div class="chart-card full-width">
        <h3>销售时段分布</h3>
        <div ref="salesTimeChart" class="chart"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { echarts } from '../utils/echarts'
import { getDashboardData, getSalesTrend, getPopularDishes } from '../api/statistics'

// 统计数据
const metricsData = ref([
  { title: '总销售额', value: '¥0', icon: 'el-icon-s-data', color: '#ff7d00', change: '+0%', changeType: 'up' },
  { title: '总订单数', value: '0', icon: 'el-icon-s-order', color: '#409eff', change: '+0%', changeType: 'up' },
  { title: '新用户数', value: '0', icon: 'el-icon-user', color: '#67c23a', change: '+0%', changeType: 'up' },
  { title: '客单价', value: '¥0', icon: 'el-icon-money', color: '#e6a23c', change: '+0%', changeType: 'up' },
  { title: '转化率', value: '0%', icon: 'el-icon-s-marketing', color: '#909399', change: '+0%', changeType: 'up' },
  { title: '复购率', value: '0%', icon: 'el-icon-s-flag', color: '#f56c6c', change: '+0%', changeType: 'up' }
])

// 图表数据
const salesTrendData = ref({
  dates: [],
  sales: [],
  orders: []
})
const dishDistributionData = ref({
  names: [],
  values: []
})
const orderStatusData = ref({
  names: [],
  values: []
})
const userRegionData = ref({
  names: [],
  values: []
})
const salesTimeData = ref({
  hours: [],
  values: []
})

// 图表引用
const salesTrendChart = ref(null)
const dishDistributionChart = ref(null)
const orderStatusChart = ref(null)
const userRegionChart = ref(null)
const salesTimeChart = ref(null)

// 图表实例
let salesTrendChartInstance = null
let dishDistributionChartInstance = null
let orderStatusChartInstance = null
let userRegionChartInstance = null
let salesTimeChartInstance = null

// 加载统计数据
const loadStatisticsData = async () => {
  try {
    const res = await getDashboardData()
    if (res) {
      // 更新指标数据
      metricsData.value = [
        { title: '总销售额', value: res.totalSales || '¥0', icon: 'el-icon-s-data', color: '#ff7d00', change: res.salesChange || '+0%', changeType: res.salesChangeType || 'up' },
        { title: '总订单数', value: String(res.totalOrders || 0), icon: 'el-icon-s-order', color: '#409eff', change: res.ordersChange || '+0%', changeType: res.ordersChangeType || 'up' },
        { title: '新用户数', value: String(res.newUsers || 0), icon: 'el-icon-user', color: '#67c23a', change: res.usersChange || '+0%', changeType: res.usersChangeType || 'up' },
        { title: '客单价', value: res.avgOrderValue || '¥0', icon: 'el-icon-money', color: '#e6a23c', change: res.avgChange || '+0%', changeType: res.avgChangeType || 'up' },
        { title: '转化率', value: res.conversionRate || '0%', icon: 'el-icon-s-marketing', color: '#909399', change: res.conversionChange || '+0%', changeType: res.conversionChangeType || 'up' },
        { title: '复购率', value: res.repurchaseRate || '0%', icon: 'el-icon-s-flag', color: '#f56c6c', change: res.repurchaseChange || '+0%', changeType: res.repurchaseChangeType || 'up' }
      ]
      
      // 更新图表数据
      if (res.orderStatus) {
        orderStatusData.value = res.orderStatus
      }
      if (res.userRegion) {
        userRegionData.value = res.userRegion
      }
      if (res.salesTime) {
        salesTimeData.value = res.salesTime
      }
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载销售趋势数据
const loadSalesTrend = async () => {
  try {
    const res = await getSalesTrend()
    if (res) {
      salesTrendData.value = res
    }
  } catch (error) {
    console.error('加载销售趋势失败:', error)
  }
}

// 加载菜品销售分布数据
const loadDishDistribution = async () => {
  try {
    const res = await getPopularDishes({ top: 8 })
    if (res && Array.isArray(res)) {
      dishDistributionData.value = {
        names: res.map(item => item.name),
        values: res.map(item => item.sales)
      }
    }
  } catch (error) {
    console.error('加载菜品销售分布失败:', error)
  }
}

// 初始化销售趋势图
const initSalesTrendChart = () => {
  if (salesTrendChart.value) {
    salesTrendChartInstance = echarts.init(salesTrendChart.value)
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
        data: ['销售额', '订单数']
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
          data: salesTrendData.value.dates
        }
      ],
      yAxis: [
        {
          type: 'value',
          name: '销售额',
          position: 'left'
        },
        {
          type: 'value',
          name: '订单数',
          position: 'right'
        }
      ],
      series: [
        {
          name: '销售额',
          type: 'line',
          data: salesTrendData.value.sales,
          smooth: true,
          lineStyle: {
            color: '#409eff'
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                offset: 0,
                color: 'rgba(64, 158, 255, 0.5)'
              },
              {
                offset: 1,
                color: 'rgba(64, 158, 255, 0.1)'
              }
            ])
          }
        },
        {
          name: '订单数',
          type: 'bar',
          yAxisIndex: 1,
          data: salesTrendData.value.orders,
          itemStyle: {
            color: '#67c23a'
          }
        }
      ]
    }
    salesTrendChartInstance.setOption(option)
  }
}

// 初始化菜品销售分布图
const initDishDistributionChart = () => {
  if (dishDistributionChart.value) {
    dishDistributionChartInstance = echarts.init(dishDistributionChart.value)
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        data: dishDistributionData.value.names
      },
      series: [
        {
          name: '菜品销售',
          type: 'pie',
          radius: '50%',
          data: dishDistributionData.value.names.map((name, index) => ({
            value: dishDistributionData.value.values[index],
            name: name
          })),
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    dishDistributionChartInstance.setOption(option)
  }
}

// 初始化订单状态分布图
const initOrderStatusChart = () => {
  if (orderStatusChart.value) {
    orderStatusChartInstance = echarts.init(orderStatusChart.value)
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        data: orderStatusData.value.names
      },
      series: [
        {
          name: '订单状态',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '18',
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: orderStatusData.value.names.map((name, index) => ({
            value: orderStatusData.value.values[index],
            name: name
          }))
        }
      ]
    }
    orderStatusChartInstance.setOption(option)
  }
}

// 初始化用户地域分布图
const initUserRegionChart = () => {
  if (userRegionChart.value) {
    userRegionChartInstance = echarts.init(userRegionChart.value)
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
        type: 'value'
      },
      yAxis: {
        type: 'category',
        data: userRegionData.value.names
      },
      series: [
        {
          name: '用户数',
          type: 'bar',
          data: userRegionData.value.values,
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              {
                offset: 0,
                color: '#ff7d00'
              },
              {
                offset: 1,
                color: '#ffb74d'
              }
            ])
          }
        }
      ]
    }
    userRegionChartInstance.setOption(option)
  }
}

// 初始化销售时段分布图
const initSalesTimeChart = () => {
  if (salesTimeChart.value) {
    salesTimeChartInstance = echarts.init(salesTimeChart.value)
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
        type: 'category',
        data: salesTimeData.value.hours
      },
      yAxis: {
        type: 'value',
        name: '销售占比(%)'
      },
      series: [
        {
          name: '销售占比',
          type: 'bar',
          data: salesTimeData.value.values,
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                offset: 0,
                color: '#f56c6c'
              },
              {
                offset: 1,
                color: '#fbc5c5'
              }
            ])
          }
        }
      ]
    }
    salesTimeChartInstance.setOption(option)
  }
}

// 初始化所有图表
const initCharts = () => {
  initSalesTrendChart()
  initDishDistributionChart()
  initOrderStatusChart()
  initUserRegionChart()
  initSalesTimeChart()
}

// 监听窗口大小变化，调整图表大小
const handleResize = () => {
  salesTrendChartInstance?.resize()
  dishDistributionChartInstance?.resize()
  orderStatusChartInstance?.resize()
  userRegionChartInstance?.resize()
  salesTimeChartInstance?.resize()
}

onMounted(async () => {
  await loadStatisticsData()
  await loadSalesTrend()
  await loadDishDistribution()
  
  initCharts()
  window.addEventListener('resize', handleResize)
})

// 监听数据变化，更新图表
watch([salesTrendData, dishDistributionData, orderStatusData, userRegionData, salesTimeData], () => {
  initCharts()
}, { deep: true })
</script>

<style scoped>
.statistics-management {
  padding: 20px;
  
  h2 {
    margin-bottom: 20px;
    color: #303133;
    font-size: 20px;
    font-weight: 600;
  }
  
  /* 指标区样式 */
  .metrics-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
    margin-bottom: 24px;
  }
  
  .metric-card {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    padding: 16px;
    border-left: 4px solid #409eff;
    display: flex;
    align-items: center;
  }
  
  .metric-icon {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 16px;
    font-size: 24px;
  }
  
  .metric-info {
    flex: 1;
  }
  
  .metric-title {
    font-size: 14px;
    color: #909399;
    margin-bottom: 4px;
  }
  
  .metric-value {
    font-size: 20px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 4px;
  }
  
  .metric-change {
    font-size: 12px;
  }
  
  .metric-change.up {
    color: #67c23a;
  }
  
  .metric-change.down {
    color: #f56c6c;
  }
  
  /* 图表区样式 */
  .charts-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
    gap: 20px;
  }
  
  .chart-card {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    padding: 20px;
  }
  
  .chart-card.full-width {
    grid-column: 1 / -1;
  }
  
  .chart-card h3 {
    margin-bottom: 16px;
    color: #303133;
    font-size: 16px;
    font-weight: 600;
  }
  
  .chart {
    width: 100%;
    height: 300px;
  }
  
  /* 响应式调整 */
  @media (max-width: 768px) {
    .metrics-container {
      grid-template-columns: repeat(2, 1fr);
    }
    
    .charts-container {
      grid-template-columns: 1fr;
    }
    
    .chart {
      height: 250px;
    }
  }
  
  @media (max-width: 480px) {
    .metrics-container {
      grid-template-columns: 1fr;
    }
  }
}
</style>
