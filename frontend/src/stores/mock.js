import { defineStore } from 'pinia'

export const useMockStore = defineStore('mock', {
  state: () => ({
    shopStatus: {
      isOpen: true,
      businessHours: [
        {
          day: 'monday',
          dayName: '周一',
          isOpen: true,
          openTime: '09:00',
          closeTime: '22:00'
        },
        {
          day: 'tuesday',
          dayName: '周二',
          isOpen: true,
          openTime: '09:00',
          closeTime: '22:00'
        },
        {
          day: 'wednesday',
          dayName: '周三',
          isOpen: true,
          openTime: '09:00',
          closeTime: '22:00'
        },
        {
          day: 'thursday',
          dayName: '周四',
          isOpen: true,
          openTime: '09:00',
          closeTime: '22:00'
        },
        {
          day: 'friday',
          dayName: '周五',
          isOpen: true,
          openTime: '09:00',
          closeTime: '23:00'
        },
        {
          day: 'saturday',
          dayName: '周六',
          isOpen: true,
          openTime: '10:00',
          closeTime: '23:00'
        },
        {
          day: 'sunday',
          dayName: '周日',
          isOpen: true,
          openTime: '10:00',
          closeTime: '22:00'
        }
      ],
      restNotice: '本店将于2026年4月5日清明节休息一天，给您带来不便敬请谅解！',
      lastUpdated: '2026-04-01 10:30:00'
    },
    dashboardData: {
      stats: [
        {
          title: '模拟流水',
          value: '¥123,456',
          icon: 'el-icon-s-data',
          color: '#ff7d00'
        },
        {
          title: '订单数',
          value: '1,234',
          icon: 'el-icon-s-order',
          color: '#409eff'
        },
        {
          title: '用户数',
          value: '5,678',
          icon: 'el-icon-user',
          color: '#67c23a'
        },
        {
          title: '客单价',
          value: '¥99.99',
          icon: 'el-icon-money',
          color: '#e6a23c'
        }
      ],
      recentOrders: [
        {
          id: '1001',
          customer: '张三',
          amount: '¥1,234',
          status: '已完成',
          date: '2026-04-01'
        },
        {
          id: '1002',
          customer: '李四',
          amount: '¥567',
          status: '处理中',
          date: '2026-04-01'
        },
        {
          id: '1003',
          customer: '王五',
          amount: '¥890',
          status: '已完成',
          date: '2026-03-31'
        }
      ],
      orderTrend: {
        dates: ['3月26日', '3月27日', '3月28日', '3月29日', '3月30日', '3月31日', '4月1日'],
        sales: [8500, 9200, 7800, 10500, 9800, 11200, 12000],
        orders: [85, 92, 78, 105, 98, 112, 120]
      },
      popularDishes: [
        {
          name: '红烧肉',
          sales: 156,
          revenue: '¥12,480'
        },
        {
          name: '宫保鸡丁',
          sales: 132,
          revenue: '¥8,652'
        },
        {
          name: '麻婆豆腐',
          sales: 118,
          revenue: '¥6,490'
        },
        {
          name: '鱼香肉丝',
          sales: 105,
          revenue: '¥7,350'
        },
        {
          name: '糖醋排骨',
          sales: 98,
          revenue: '¥9,800'
        }
      ]
    },
    statisticsData: {
      // 指标数据
      metrics: [
        {
          title: '模拟服务流水',
          value: '¥1,234,567',
          icon: 'el-icon-s-data',
          color: '#ff7d00',
          change: '+12.5%',
          changeType: 'up'
        },
        {
          title: '总订单数',
          value: '12,345',
          icon: 'el-icon-s-order',
          color: '#409eff',
          change: '+8.2%',
          changeType: 'up'
        },
        {
          title: '新用户数',
          value: '5,678',
          icon: 'el-icon-user',
          color: '#67c23a',
          change: '+15.3%',
          changeType: 'up'
        },
        {
          title: '客单价',
          value: '¥99.99',
          icon: 'el-icon-money',
          color: '#e6a23c',
          change: '-2.1%',
          changeType: 'down'
        },
        {
          title: '转化率',
          value: '23.5%',
          icon: 'el-icon-s-marketing',
          color: '#909399',
          change: '+3.2%',
          changeType: 'up'
        },
        {
          title: '复购率',
          value: '45.2%',
          icon: 'el-icon-s-flag',
          color: '#f56c6c',
          change: '+5.7%',
          changeType: 'up'
        }
      ],
      // 销售趋势数据
      salesTrend: {
        dates: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
        sales: [120000, 132000, 101000, 134000, 90000, 230000, 210000, 250000, 220000, 300000, 280000, 320000],
        orders: [1200, 1320, 1010, 1340, 900, 2300, 2100, 2500, 2200, 3000, 2800, 3200]
      },
      // 服务类型分布
      dishDistribution: {
        names: ['红烧肉', '宫保鸡丁', '麻婆豆腐', '鱼香肉丝', '糖醋排骨', '拍黄瓜', '凉拌木耳', '西红柿鸡蛋汤'],
        values: [156, 132, 118, 105, 98, 89, 76, 65]
      },
      // 订单状态分布
      orderStatus: {
        names: ['已完成', '处理中', '待支付', '已取消'],
        values: [85, 10, 3, 2]
      },
      // 用户地域分布
      userRegion: {
        names: ['北京', '上海', '广州', '深圳', '杭州', '成都', '武汉', '其他'],
        values: [25, 20, 15, 12, 8, 7, 5, 8]
      },
      // 销售时段分布
      salesTime: {
        hours: ['00:00', '03:00', '06:00', '09:00', '12:00', '15:00', '18:00', '21:00'],
        values: [5, 2, 3, 15, 30, 10, 25, 10]
      }
    },
    orders: [
      {
        id: '1001',
        customerName: '张三',
        customerPhone: '13800138001',
        customerAddress: '北京市朝阳区建国路88号',
        totalAmount: 1234.00,
        status: '已完成',
        createTime: '2026-04-01 10:30:00',
        paymentTime: '2026-04-01 10:35:00',
        deliveryTime: '2026-04-01 11:30:00',
        items: [
          {
            name: '红烧肉',
            quantity: 2,
            price: 88.00,
            total: 176.00
          },
          {
            name: '宫保鸡丁',
            quantity: 1,
            price: 68.00,
            total: 68.00
          },
          {
            name: '米饭',
            quantity: 3,
            price: 5.00,
            total: 15.00
          }
        ]
      },
      {
        id: '1002',
        customerName: '李四',
        customerPhone: '13900139002',
        customerAddress: '上海市浦东新区张江高科技园区',
        totalAmount: 567.00,
        status: '处理中',
        createTime: '2026-04-01 11:15:00',
        paymentTime: '2026-04-01 11:20:00',
        items: [
          {
            name: '鱼香肉丝',
            quantity: 1,
            price: 58.00,
            total: 58.00
          },
          {
            name: '麻婆豆腐',
            quantity: 1,
            price: 48.00,
            total: 48.00
          },
          {
            name: '米饭',
            quantity: 2,
            price: 5.00,
            total: 10.00
          }
        ]
      },
      {
        id: '1003',
        customerName: '王五',
        customerPhone: '13700137003',
        customerAddress: '广州市天河区珠江新城',
        totalAmount: 890.00,
        status: '已完成',
        createTime: '2026-03-31 18:45:00',
        paymentTime: '2026-03-31 18:50:00',
        deliveryTime: '2026-03-31 19:45:00',
        items: [
          {
            name: '糖醋排骨',
            quantity: 2,
            price: 78.00,
            total: 156.00
          },
          {
            name: '拍黄瓜',
            quantity: 1,
            price: 28.00,
            total: 28.00
          },
          {
            name: '西红柿鸡蛋汤',
            quantity: 1,
            price: 25.00,
            total: 25.00
          },
          {
            name: '米饭',
            quantity: 3,
            price: 5.00,
            total: 15.00
          }
        ]
      },
      {
        id: '1004',
        customerName: '赵六',
        customerPhone: '13600136004',
        customerAddress: '深圳市南山区科技园',
        totalAmount: 1560.00,
        status: '待支付',
        createTime: '2026-04-02 09:30:00',
        items: [
          {
            name: '豪华套餐A',
            quantity: 2,
            price: 198.00,
            total: 396.00
          },
          {
            name: '提拉米苏',
            quantity: 2,
            price: 48.00,
            total: 96.00
          }
        ]
      },
      {
        id: '1005',
        customerName: '钱七',
        customerPhone: '13500135005',
        customerAddress: '杭州市西湖区西溪湿地',
        totalAmount: 789.00,
        status: '已取消',
        createTime: '2026-04-02 10:00:00',
        items: [
          {
            name: '经济套餐B',
            quantity: 1,
            price: 98.00,
            total: 98.00
          },
          {
            name: '宫保鸡丁',
            quantity: 1,
            price: 68.00,
            total: 68.00
          },
          {
            name: '米饭',
            quantity: 2,
            price: 5.00,
            total: 10.00
          }
        ]
      }
    ],
    categories: [
      {
        id: 1,
        name: '热菜',
        status: 1,
        sort: 1,
        createTime: '2026-03-01 10:00:00'
      },
      {
        id: 2,
        name: '凉菜',
        status: 1,
        sort: 2,
        createTime: '2026-03-02 11:00:00'
      },
      {
        id: 3,
        name: '汤品',
        status: 0,
        sort: 3,
        createTime: '2026-03-03 12:00:00'
      },
      {
        id: 4,
        name: '主食',
        status: 1,
        sort: 4,
        createTime: '2026-03-04 13:00:00'
      },
      {
        id: 5,
        name: '甜点',
        status: 1,
        sort: 5,
        createTime: '2026-03-05 14:00:00'
      }
    ],
    dishes: [
      {
        id: 1,
        name: '红烧肉',
        categoryId: 1,
        price: 88.00,
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=delicious%20braised%20pork%20in%20soy%20sauce%20chinese%20food&image_size=square',
        status: 1,
        description: '传统红烧肉，肥而不腻',
        createTime: '2026-03-01 10:00:00'
      },
      {
        id: 2,
        name: '宫保鸡丁',
        categoryId: 1,
        price: 68.00,
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=kung%20pao%20chicken%20chinese%20food&image_size=square',
        status: 1,
        description: '经典川菜，麻辣鲜香',
        createTime: '2026-03-02 11:00:00'
      },
      {
        id: 3,
        name: '麻婆豆腐',
        categoryId: 1,
        price: 48.00,
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=mapo%20tofu%20chinese%20food&image_size=square',
        status: 1,
        description: '四川特色，麻辣豆腐',
        createTime: '2026-03-03 12:00:00'
      },
      {
        id: 4,
        name: '鱼香肉丝',
        categoryId: 1,
        price: 58.00,
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=yu%20xiang%20rou%20si%20chinese%20food&image_size=square',
        status: 1,
        description: '鱼香口味，肉丝鲜嫩',
        createTime: '2026-03-04 13:00:00'
      },
      {
        id: 5,
        name: '糖醋排骨',
        categoryId: 1,
        price: 78.00,
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=sweet%20and%20sour%20ribs%20chinese%20food&image_size=square',
        status: 1,
        description: '酸甜可口，排骨酥烂',
        createTime: '2026-03-05 14:00:00'
      },
      {
        id: 6,
        name: '拍黄瓜',
        categoryId: 2,
        price: 28.00,
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cucumber%20salad%20chinese%20cold%20dish&image_size=square',
        status: 1,
        description: '清爽开胃，夏日必备',
        createTime: '2026-03-06 15:00:00'
      },
      {
        id: 7,
        name: '凉拌木耳',
        categoryId: 2,
        price: 32.00,
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=black%20fungus%20salad%20chinese%20cold%20dish&image_size=square',
        status: 1,
        description: '爽口木耳，健康营养',
        createTime: '2026-03-07 16:00:00'
      },
      {
        id: 8,
        name: '酸辣土豆丝',
        categoryId: 2,
        price: 38.00,
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=spicy%20and%20sour%20potato%20shreds%20chinese%20food&image_size=square',
        status: 0,
        description: '酸辣爽口，下饭神器',
        createTime: '2026-03-08 17:00:00'
      },
      {
        id: 9,
        name: '西红柿鸡蛋汤',
        categoryId: 3,
        price: 25.00,
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=tomato%20and%20egg%20soup%20chinese%20food&image_size=square',
        status: 1,
        description: '经典汤品，营养丰富',
        createTime: '2026-03-09 18:00:00'
      },
      {
        id: 10,
        name: '米饭',
        categoryId: 4,
        price: 5.00,
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=steamed%20rice%20chinese%20food&image_size=square',
        status: 1,
        description: '香喷喷的白米饭',
        createTime: '2026-03-10 19:00:00'
      },
      {
        id: 11,
        name: '馒头',
        categoryId: 4,
        price: 3.00,
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=steamed%20bun%20chinese%20food&image_size=square',
        status: 1,
        description: '传统面食，松软可口',
        createTime: '2026-03-11 20:00:00'
      },
      {
        id: 12,
        name: '提拉米苏',
        categoryId: 5,
        price: 48.00,
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=tiramisu%20dessert&image_size=square',
        status: 1,
        description: '意大利甜点，口感丰富',
        createTime: '2026-03-12 21:00:00'
      }
    ],
    setmeals: [
      {
        id: 1,
        name: '豪华套餐A',
        categoryId: 1,
        price: 198.00,
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=luxury%20chinese%20food%20set%20meal%20with%20multiple%20dishes&image_size=square',
        status: 1,
        description: '包含红烧肉、宫保鸡丁、麻婆豆腐、米饭和汤品',
        createTime: '2026-03-01 10:00:00',
        dishIds: [1, 2, 3, 9, 10]
      },
      {
        id: 2,
        name: '经济套餐B',
        categoryId: 1,
        price: 98.00,
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=economic%20chinese%20food%20set%20meal&image_size=square',
        status: 1,
        description: '包含鱼香肉丝、拍黄瓜、米饭',
        createTime: '2026-03-02 11:00:00',
        dishIds: [4, 6, 10]
      },
      {
        id: 3,
        name: '素食套餐C',
        categoryId: 2,
        price: 88.00,
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=vegetarian%20chinese%20food%20set%20meal&image_size=square',
        status: 1,
        description: '包含凉拌木耳、酸辣土豆丝、米饭',
        createTime: '2026-03-03 12:00:00',
        dishIds: [7, 8, 10]
      },
      {
        id: 4,
        name: '家庭套餐D',
        categoryId: 1,
        price: 298.00,
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=family%20chinese%20food%20set%20meal&image_size=square',
        status: 0,
        description: '包含红烧肉、宫保鸡丁、鱼香肉丝、糖醋排骨、拍黄瓜、米饭',
        createTime: '2026-03-04 13:00:00',
        dishIds: [1, 2, 4, 5, 6, 10]
      },
      {
        id: 5,
        name: '商务套餐E',
        categoryId: 1,
        price: 158.00,
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=business%20chinese%20food%20set%20meal&image_size=square',
        status: 1,
        description: '包含宫保鸡丁、鱼香肉丝、米饭、汤品',
        createTime: '2026-03-05 14:00:00',
        dishIds: [2, 4, 9, 10]
      }
    ]
  }),
  actions: {
    getDashboardData() {
      return this.dashboardData
    },
    getCategories() {
      return this.categories
    },
    getDishes() {
      return this.dishes
    },
    updateCategoryStatus(id, status) {
      const category = this.categories.find(cat => cat.id === id)
      if (category) {
        category.status = status
      }
    },
    updateCategorySort(id, newSort) {
      const category = this.categories.find(cat => cat.id === id)
      if (category) {
        category.sort = newSort
        this.categories.sort((a, b) => a.sort - b.sort)
      }
    },
    updateDishStatus(id, status) {
      const dish = this.dishes.find(dish => dish.id === id)
      if (dish) {
        dish.status = status
      }
    },
    batchUpdateDishStatus(ids, status) {
      ids.forEach(id => {
        const dish = this.dishes.find(dish => dish.id === id)
        if (dish) {
          dish.status = status
        }
      })
    },
    getSetmeals() {
      return this.setmeals
    },
    updateSetmealStatus(id, status) {
      const setmeal = this.setmeals.find(setmeal => setmeal.id === id)
      if (setmeal) {
        setmeal.status = status
      }
    },
    batchUpdateSetmealStatus(ids, status) {
      ids.forEach(id => {
        const setmeal = this.setmeals.find(setmeal => setmeal.id === id)
        if (setmeal) {
          setmeal.status = status
        }
      })
    },
    getOrders() {
      return this.orders
    },
    getOrderById(id) {
      return this.orders.find(order => order.id === id)
    },
    getStatisticsData() {
      return this.statisticsData
    },
    getShopStatus() {
      return this.shopStatus
    },
    updateShopStatus(status) {
      this.shopStatus = { ...this.shopStatus, ...status, lastUpdated: new Date().toLocaleString('zh-CN') }
    },
    updateBusinessHours(day, data) {
      const index = this.shopStatus.businessHours.findIndex(item => item.day === day)
      if (index !== -1) {
        this.shopStatus.businessHours[index] = { ...this.shopStatus.businessHours[index], ...data }
        this.shopStatus.lastUpdated = new Date().toLocaleString('zh-CN')
      }
    }
  }
})