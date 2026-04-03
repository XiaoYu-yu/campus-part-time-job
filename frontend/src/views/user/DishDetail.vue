<template>
  <UserLayout>
    <div class="dish-detail-page" v-if="dish">
      <button class="back-btn" @click="router.back()">返回</button>
      <img :src="dish.image" :alt="dish.name" class="hero-image" />
      <section class="detail-card">
        <h1>{{ dish.name }}</h1>
        <div class="price">¥{{ Number(dish.price).toFixed(2) }}</div>
        <p class="description">{{ dish.description || '暂无描述' }}</p>
      </section>

      <section class="detail-card">
        <div class="section-header">
          <h3>推荐菜品</h3>
        </div>
        <div class="recommend-grid">
          <div v-for="item in recommendations" :key="item.id" class="recommend-item" @click="goToDish(item.id)">
            <img :src="item.image" :alt="item.name" />
            <span>{{ item.name }}</span>
            <strong>¥{{ Number(item.price).toFixed(2) }}</strong>
          </div>
        </div>
      </section>

      <div class="bottom-bar">
        <button class="secondary-btn" @click="addToCart">加入购物车</button>
        <button class="primary-btn" @click="buyNow">立即购买</button>
      </div>
    </div>
  </UserLayout>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import UserLayout from '../../layout/UserLayout.vue'
import { getPublicDishById, getPublicDishes } from '../../api/public'
import { addCartItem } from '../../api/cart'

const route = useRoute()
const router = useRouter()
const dish = ref(null)
const recommendations = ref([])

const loadDish = async () => {
  dish.value = await getPublicDishById(route.params.id)
  const res = await getPublicDishes({ page: 1, pageSize: 4 })
  recommendations.value = (res.records || []).filter(item => item.id !== dish.value.id).slice(0, 3)
}

const addToCart = async () => {
  await addCartItem({ dishId: dish.value.id, quantity: 1 })
  ElMessage.success('已加入购物车')
}

const buyNow = async () => {
  await addToCart()
  router.push('/user/cart')
}

const goToDish = (id) => {
  router.push(`/user/dish/${id}`)
}

watch(() => route.params.id, () => loadDish())

onMounted(() => loadDish())
</script>

<style scoped lang="scss">
.dish-detail-page {
  padding: 16px 16px 90px;
}

.back-btn,
.secondary-btn,
.primary-btn {
  border: none;
  border-radius: 999px;
  cursor: pointer;
}

.back-btn {
  margin-bottom: 12px;
  padding: 8px 16px;
}

.hero-image {
  width: 100%;
  height: 240px;
  object-fit: cover;
  border-radius: 16px;
}

.detail-card {
  background: white;
  border-radius: 16px;
  padding: 18px;
  margin-top: 14px;
}

.price {
  color: #ff7d00;
  font-size: 24px;
  font-weight: 700;
  margin: 10px 0;
}

.description {
  color: #606266;
  line-height: 1.7;
}

.recommend-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.recommend-item {
  display: flex;
  flex-direction: column;
  gap: 6px;

  img {
    width: 100%;
    height: 84px;
    object-fit: cover;
    border-radius: 10px;
  }
}

.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  padding: 12px 16px;
  background: white;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.08);
}

.secondary-btn {
  background: white;
  border: 1px solid #ff7d00;
  color: #ff7d00;
  padding: 12px 0;
}

.primary-btn {
  background: #ff7d00;
  color: white;
}
</style>
