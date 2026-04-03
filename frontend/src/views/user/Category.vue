<template>
  <UserLayout>
    <div class="category-page">
      <div class="search-bar">
        <el-input v-model="searchKeyword" placeholder="搜索菜品" clearable @input="loadDishes" />
      </div>

      <div class="category-container">
        <aside class="category-sidebar">
          <button
            v-for="category in displayCategories"
            :key="category.id"
            class="category-item"
            :class="{ active: selectedCategoryId === category.id }"
            @click="selectCategory(category.id)"
          >
            {{ category.name }}
          </button>
        </aside>

        <section class="dish-container">
          <div class="dish-header">
            <h2>{{ selectedCategoryName }}</h2>
            <div class="sort-options">
              <button class="sort-btn" :class="{ active: sortBy === 'default' }" @click="sortBy = 'default'">默认</button>
              <button class="sort-btn" :class="{ active: sortBy === 'price' }" @click="sortBy = 'price'">价格</button>
            </div>
          </div>

          <div v-if="filteredDishes.length" class="dish-grid">
            <div v-for="dish in filteredDishes" :key="dish.id" class="dish-card" @click="goToDishDetail(dish.id)">
              <img :src="dish.image" :alt="dish.name" class="dish-image" />
              <div class="dish-info">
                <h3>{{ dish.name }}</h3>
                <p>{{ dish.description || '暂无描述' }}</p>
                <div class="dish-footer">
                  <span>¥{{ Number(dish.price).toFixed(2) }}</span>
                  <button class="add-btn" @click.stop="addDishToCart(dish)">加入购物车</button>
                </div>
              </div>
            </div>
          </div>

          <div v-else class="empty-state">没有找到匹配的菜品</div>
        </section>
      </div>
    </div>
  </UserLayout>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import UserLayout from '../../layout/UserLayout.vue'
import { getPublicCategories, getPublicDishes } from '../../api/public'
import { addCartItem } from '../../api/cart'

const route = useRoute()
const router = useRouter()

const categories = ref([])
const dishes = ref([])
const searchKeyword = ref('')
const selectedCategoryId = ref(Number(route.query.categoryId) || 0)
const sortBy = ref('default')

const displayCategories = computed(() => [{ id: 0, name: '全部' }, ...categories.value])

const selectedCategoryName = computed(() => displayCategories.value.find(item => item.id === selectedCategoryId.value)?.name || '全部')

const filteredDishes = computed(() => {
  const result = [...dishes.value]
  if (sortBy.value === 'price') {
    result.sort((a, b) => Number(a.price) - Number(b.price))
  }
  return result
})

const loadCategories = async () => {
  categories.value = await getPublicCategories()
}

const loadDishes = async () => {
  const params = {
    page: 1,
    pageSize: 100,
    name: searchKeyword.value || undefined,
    categoryId: selectedCategoryId.value || undefined
  }
  const res = await getPublicDishes(params)
  dishes.value = res.records || []
}

const selectCategory = (categoryId) => {
  selectedCategoryId.value = categoryId
  router.push({ path: '/user/category', query: categoryId ? { categoryId } : {} })
  loadDishes()
}

const goToDishDetail = (dishId) => {
  router.push(`/user/dish/${dishId}`)
}

const addDishToCart = async (dish) => {
  try {
    await addCartItem({ dishId: dish.id, quantity: 1 })
    ElMessage.success(`已将 ${dish.name} 加入购物车`)
  } catch (error) {
    console.error('加入购物车失败:', error)
  }
}

watch(() => route.query.categoryId, (value) => {
  selectedCategoryId.value = Number(value) || 0
  loadDishes()
})

onMounted(async () => {
  await loadCategories()
  await loadDishes()
})
</script>

<style scoped lang="scss">
.category-page {
  height: calc(100vh - 116px);
  display: flex;
  flex-direction: column;
}

.search-bar {
  padding: 12px 16px;
  background: white;
}

.category-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.category-sidebar {
  width: 96px;
  background: #f5f5f5;
  padding: 8px;
  overflow-y: auto;
}

.category-item {
  width: 100%;
  border: none;
  background: transparent;
  padding: 12px 8px;
  border-radius: 10px;
  cursor: pointer;

  &.active {
    background: white;
    color: #ff7d00;
    font-weight: 700;
  }
}

.dish-container {
  flex: 1;
  background: white;
  padding: 16px;
  overflow-y: auto;
}

.dish-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.sort-options {
  display: flex;
  gap: 8px;
}

.sort-btn,
.add-btn {
  border: none;
  border-radius: 999px;
  cursor: pointer;
}

.sort-btn {
  background: #f5f5f5;
  padding: 6px 12px;

  &.active {
    background: #ff7d00;
    color: white;
  }
}

.dish-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
}

.dish-card {
  overflow: hidden;
  border-radius: 12px;
  background: #fafafa;
}

.dish-image {
  width: 100%;
  height: 130px;
  object-fit: cover;
}

.dish-info {
  padding: 12px;

  h3 {
    margin: 0 0 6px;
  }

  p {
    color: #909399;
    font-size: 13px;
    min-height: 32px;
    margin: 0 0 10px;
  }
}

.dish-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.add-btn {
  background: #ff7d00;
  color: white;
  padding: 6px 12px;
}

.empty-state {
  color: #909399;
  padding: 32px 0;
}
</style>
