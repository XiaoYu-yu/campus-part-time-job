<template>
  <UserLayout>
    <div class="cart-page">
      <h1 class="page-title">购物车</h1>

      <div v-if="cartItems.length" class="cart-list">
        <div v-for="item in cartItems" :key="item.id" class="cart-item">
          <el-checkbox :model-value="item.checked" @change="(value) => handleCheckedChange(item, value)" />
          <img :src="item.image" :alt="item.name" class="cart-item-image" />
          <div class="cart-item-info">
            <h3>{{ item.name }}</h3>
            <p>{{ item.description || '暂无描述' }}</p>
            <div class="cart-item-footer">
              <span class="price">¥{{ Number(item.price).toFixed(2) }}</span>
              <div class="quantity-box">
                <button @click="changeQuantity(item, item.quantity - 1)" :disabled="item.quantity <= 1">-</button>
                <span>{{ item.quantity }}</span>
                <button @click="changeQuantity(item, item.quantity + 1)">+</button>
              </div>
            </div>
          </div>
          <button class="delete-btn" @click="removeItem(item.id)">删除</button>
        </div>
      </div>

      <div v-else class="empty-cart">
        <p>购物车还是空的</p>
        <button @click="router.push('/user')">去逛逛</button>
      </div>

      <div v-if="cartItems.length" class="checkout-bar">
        <div>
          <el-checkbox :model-value="selectAll" @change="toggleAll">全选</el-checkbox>
          <div class="total">合计：¥{{ totalPrice.toFixed(2) }}</div>
        </div>
        <div class="actions">
          <button class="clear-btn" @click="handleClearCart">清空</button>
          <button class="checkout-btn" :disabled="selectedCount === 0" @click="router.push('/user/checkout')">
            去结算({{ selectedCount }})
          </button>
        </div>
      </div>
    </div>
  </UserLayout>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import UserLayout from '../../layout/UserLayout.vue'
import { clearCart, deleteCartItem, getCartList, updateCartItem } from '../../api/cart'

const router = useRouter()
const cartItems = ref([])

const loadCart = async () => {
  try {
    const res = await getCartList()
    cartItems.value = (res || []).map(item => ({
      ...item,
      checked: item.checked === 1
    }))
  } catch (error) {
    console.error('加载购物车失败:', error)
  }
}

const selectedCount = computed(() => cartItems.value.filter(item => item.checked).length)
const totalPrice = computed(() => cartItems.value.filter(item => item.checked).reduce((sum, item) => sum + Number(item.price) * item.quantity, 0))
const selectAll = computed(() => cartItems.value.length > 0 && cartItems.value.every(item => item.checked))

const changeQuantity = async (item, quantity) => {
  if (quantity < 1) return
  await updateCartItem(item.id, { quantity, checked: item.checked ? 1 : 0 })
  await loadCart()
}

const handleCheckedChange = async (item, value) => {
  await updateCartItem(item.id, { quantity: item.quantity, checked: value ? 1 : 0 })
  item.checked = value
}

const toggleAll = async (value) => {
  await Promise.all(cartItems.value.map(item => updateCartItem(item.id, { quantity: item.quantity, checked: value ? 1 : 0 })))
  await loadCart()
}

const removeItem = async (id) => {
  await deleteCartItem(id)
  ElMessage.success('商品已移除')
  await loadCart()
}

const handleClearCart = async () => {
  await clearCart()
  ElMessage.success('购物车已清空')
  await loadCart()
}

onMounted(() => loadCart())
</script>

<style scoped lang="scss">
.cart-page {
  padding: 16px 16px 96px;
}

.page-title {
  margin: 0 0 16px;
}

.cart-list,
.empty-cart {
  background: white;
  border-radius: 16px;
  padding: 16px;
}

.cart-item {
  display: grid;
  grid-template-columns: 24px 88px 1fr auto;
  gap: 12px;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.cart-item:last-child {
  border-bottom: none;
}

.cart-item-image {
  width: 88px;
  height: 88px;
  object-fit: cover;
  border-radius: 12px;
}

.cart-item-info h3 {
  margin: 0 0 6px;
}

.cart-item-info p {
  margin: 0 0 10px;
  color: #909399;
  font-size: 13px;
}

.cart-item-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: #ff7d00;
  font-weight: 700;
}

.quantity-box {
  display: flex;
  gap: 8px;
  align-items: center;
}

.quantity-box button,
.delete-btn,
.clear-btn,
.checkout-btn,
.empty-cart button {
  border: none;
  border-radius: 999px;
  cursor: pointer;
}

.quantity-box button {
  width: 28px;
  height: 28px;
}

.delete-btn,
.clear-btn {
  background: #fef0f0;
  color: #f56c6c;
  padding: 8px 14px;
}

.checkout-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: white;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.08);
}

.actions {
  display: flex;
  gap: 10px;
}

.checkout-btn {
  background: #ff7d00;
  color: white;
  padding: 10px 18px;
}

.total {
  margin-top: 8px;
  color: #ff7d00;
  font-weight: 700;
}

.empty-cart {
  text-align: center;
  padding: 48px 16px;
}

.empty-cart button {
  margin-top: 12px;
  background: #ff7d00;
  color: white;
  padding: 10px 20px;
}
</style>
