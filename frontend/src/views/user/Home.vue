<template>
  <UserLayout>
    <div class="home-page">
      <section class="hero-card">
        <h2>今天想吃点什么？</h2>
        <p>已接入真实菜品、购物车和订单接口，当前页面不再依赖 mock 数据。</p>
      </section>

      <section class="category-section">
        <div class="section-header">
          <h3>分类</h3>
          <router-link to="/user/category">查看全部</router-link>
        </div>
        <div class="category-grid">
          <button v-for="item in categories" :key="item.id" class="category-item" @click="goToCategory(item.id)">
            {{ item.name }}
          </button>
        </div>
      </section>

      <section class="dish-section">
        <div class="section-header">
          <h3>推荐菜品</h3>
          <router-link to="/user/category">更多菜品</router-link>
        </div>
        <div class="dish-grid">
          <div v-for="dish in recommendedDishes" :key="dish.id" class="dish-card" @click="goToDishDetail(dish.id)">
            <img :src="dish.image" :alt="dish.name" class="dish-image" />
            <div class="dish-body">
              <h4>{{ dish.name }}</h4>
              <p>{{ dish.description || '暂无描述' }}</p>
              <div class="dish-footer">
                <span class="price">¥{{ Number(dish.price).toFixed(2) }}</span>
                <button class="add-btn" @click.stop="addDishToCart(dish)">加入购物车</button>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </UserLayout>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import UserLayout from '../../layout/UserLayout.vue'
import { getPublicCategories, getPublicDishes } from '../../api/public'
import { addCartItem } from '../../api/cart'

const router = useRouter()
const categories = ref([])
const recommendedDishes = ref([])

const loadData = async () => {
  try {
    const [categoryRes, dishRes] = await Promise.all([
      getPublicCategories(),
      getPublicDishes({ page: 1, pageSize: 8 })
    ])
    categories.value = categoryRes || []
    recommendedDishes.value = dishRes.records || []
  } catch (error) {
    console.error('加载首页数据失败:', error)
  }
}

const goToCategory = (categoryId) => {
  router.push({ path: '/user/category', query: { categoryId } })
}

const goToDishDetail = (id) => {
  router.push(`/user/dish/${id}`)
}

const addDishToCart = async (dish) => {
  try {
    await addCartItem({ dishId: dish.id, quantity: 1 })
    ElMessage.success(`已将 ${dish.name} 加入购物车`)
  } catch (error) {
    console.error('加入购物车失败:', error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.home-page {
  padding: 16px;
}

.hero-card,
.category-section,
.dish-section {
  background: white;
  border-radius: 16px;
  padding: 18px;
  margin-bottom: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
}

.hero-card {
  background: linear-gradient(135deg, #fff1e6 0%, #ffffff 100%);

  h2 {
    margin: 0 0 8px;
  }

  p {
    margin: 0;
    color: #606266;
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.category-item {
  border: 1px solid #ffe2c4;
  background: #fff8f1;
  border-radius: 12px;
  padding: 12px 10px;
  color: #ff7d00;
  cursor: pointer;
}

.dish-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
}

.dish-card {
  border-radius: 14px;
  overflow: hidden;
  background: #fafafa;
  cursor: pointer;
}

.dish-image {
  width: 100%;
  height: 140px;
  object-fit: cover;
}

.dish-body {
  padding: 12px;

  h4 {
    margin: 0 0 6px;
  }

  p {
    margin: 0 0 10px;
    color: #909399;
    font-size: 13px;
    min-height: 36px;
  }
}

.dish-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: #ff7d00;
  font-weight: 700;
}

.add-btn {
  border: none;
  background: #ff7d00;
  color: white;
  border-radius: 999px;
  padding: 6px 12px;
  cursor: pointer;
}
</style>
