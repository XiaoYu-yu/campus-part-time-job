<template>
  <UserLayout>
    <div class="checkout-page">
      <h1 class="page-title">确认订单</h1>

      <section class="card">
        <div class="section-header">
          <h3>收货地址</h3>
          <button class="ghost-btn" @click="openAddressDialog()">新增地址</button>
        </div>
        <div v-if="addresses.length" class="address-list">
          <label v-for="address in addresses" :key="address.id" class="address-item">
            <input v-model="selectedAddressId" :value="address.id" type="radio" />
            <div class="address-content">
              <div class="address-title">
                <strong>{{ address.consignee }}</strong>
                <span>{{ address.phone }}</span>
                <span v-if="address.isDefault === 1" class="default-tag">默认</span>
              </div>
              <div>{{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}</div>
            </div>
            <button class="text-btn" @click.prevent="openAddressDialog(address)">编辑</button>
          </label>
        </div>
        <div v-else class="empty-tip">还没有地址，请先新增一个地址</div>
      </section>

      <section class="card">
        <h3>商品清单</h3>
        <div v-for="item in checkedItems" :key="item.id" class="order-item">
          <img :src="item.image" :alt="item.name" />
          <div class="item-info">
            <strong>{{ item.name }}</strong>
            <span>{{ item.description || '暂无描述' }}</span>
          </div>
          <div class="item-price">¥{{ Number(item.price).toFixed(2) }} x {{ item.quantity }}</div>
        </div>
      </section>

      <section class="card">
        <h3>配送信息</h3>
        <el-form label-position="top">
          <el-form-item label="配送方式">
            <el-select v-model="deliveryType">
              <el-option label="快递配送" value="express" />
              <el-option label="到店自取" value="self" />
            </el-select>
          </el-form-item>
          <el-form-item label="配送时间">
            <el-select v-model="deliveryTime">
              <el-option label="尽快送达" value="asap" />
              <el-option label="10:00-11:00" value="10-11" />
              <el-option label="11:00-12:00" value="11-12" />
              <el-option label="12:00-13:00" value="12-13" />
            </el-select>
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="remark" type="textarea" :rows="2" placeholder="请输入备注信息" />
          </el-form-item>
        </el-form>
      </section>

      <section class="card">
        <h3>费用明细</h3>
        <div class="amount-row"><span>商品总价</span><strong>¥{{ subtotal.toFixed(2) }}</strong></div>
        <div class="amount-row"><span>配送费</span><strong>¥{{ deliveryFee.toFixed(2) }}</strong></div>
        <div class="amount-row total"><span>实付金额</span><strong>¥{{ totalPrice.toFixed(2) }}</strong></div>
      </section>

      <div class="submit-bar">
        <div>合计：<strong>¥{{ totalPrice.toFixed(2) }}</strong></div>
        <button class="submit-btn" @click="submitOrder">提交订单</button>
      </div>

      <el-dialog v-model="addressDialogVisible" :title="editingAddressId ? '编辑地址' : '新增地址'">
        <el-form label-position="top">
          <el-form-item label="收货人"><el-input v-model="addressForm.consignee" /></el-form-item>
          <el-form-item label="手机号"><el-input v-model="addressForm.phone" /></el-form-item>
          <el-form-item label="省"><el-input v-model="addressForm.province" /></el-form-item>
          <el-form-item label="市"><el-input v-model="addressForm.city" /></el-form-item>
          <el-form-item label="区"><el-input v-model="addressForm.district" /></el-form-item>
          <el-form-item label="详细地址"><el-input v-model="addressForm.detail" /></el-form-item>
          <el-checkbox v-model="addressForm.isDefaultBoolean">设为默认地址</el-checkbox>
        </el-form>
        <template #footer>
          <el-button @click="addressDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveAddress">保存</el-button>
        </template>
      </el-dialog>
    </div>
  </UserLayout>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import UserLayout from '../../layout/UserLayout.vue'
import { addAddress, getAddressList, updateAddress } from '../../api/address'
import { getCartList } from '../../api/cart'
import { submitUserOrder } from '../../api/customer-order'

const router = useRouter()
const addresses = ref([])
const checkedItems = ref([])
const selectedAddressId = ref(null)
const deliveryType = ref('express')
const deliveryTime = ref('asap')
const remark = ref('')
const deliveryFee = ref(5)

const addressDialogVisible = ref(false)
const editingAddressId = ref(null)
const addressForm = reactive({
  consignee: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefaultBoolean: false
})

const subtotal = computed(() => checkedItems.value.reduce((sum, item) => sum + Number(item.price) * item.quantity, 0))
const totalPrice = computed(() => subtotal.value + deliveryFee.value)

const loadData = async () => {
  const [addressRes, cartRes] = await Promise.all([getAddressList(), getCartList()])
  addresses.value = addressRes || []
  checkedItems.value = (cartRes || []).filter(item => item.checked === 1)
  selectedAddressId.value = addresses.value.find(item => item.isDefault === 1)?.id || addresses.value[0]?.id || null
}

const openAddressDialog = (address = null) => {
  editingAddressId.value = address?.id || null
  addressForm.consignee = address?.consignee || ''
  addressForm.phone = address?.phone || ''
  addressForm.province = address?.province || ''
  addressForm.city = address?.city || ''
  addressForm.district = address?.district || ''
  addressForm.detail = address?.detail || ''
  addressForm.isDefaultBoolean = address?.isDefault === 1
  addressDialogVisible.value = true
}

const saveAddress = async () => {
  const payload = {
    consignee: addressForm.consignee,
    phone: addressForm.phone,
    province: addressForm.province,
    city: addressForm.city,
    district: addressForm.district,
    detail: addressForm.detail,
    isDefault: addressForm.isDefaultBoolean ? 1 : 0
  }

  if (editingAddressId.value) {
    await updateAddress(editingAddressId.value, payload)
  } else {
    await addAddress(payload)
  }
  ElMessage.success('地址已保存')
  addressDialogVisible.value = false
  await loadData()
}

const submitOrder = async () => {
  if (!selectedAddressId.value) {
    ElMessage.warning('请先选择收货地址')
    return
  }
  if (!checkedItems.value.length) {
    ElMessage.warning('没有可结算的商品')
    return
  }

  const orderId = await submitUserOrder({
    addressId: selectedAddressId.value,
    deliveryType: deliveryType.value,
    deliveryTime: deliveryTime.value,
    remark: remark.value
  })

  ElMessage.success(`订单已提交：${orderId}`)
  router.push('/user/orders')
}

onMounted(() => loadData())
</script>

<style scoped lang="scss">
.checkout-page {
  padding: 16px 16px 96px;
}

.page-title {
  margin: 0 0 16px;
}

.card {
  background: white;
  border-radius: 16px;
  padding: 16px;
  margin-bottom: 14px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.address-item {
  display: grid;
  grid-template-columns: 20px 1fr auto;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.address-item:last-child {
  border-bottom: none;
}

.address-title {
  display: flex;
  gap: 10px;
  margin-bottom: 6px;
}

.default-tag {
  color: #ff7d00;
}

.order-item {
  display: grid;
  grid-template-columns: 72px 1fr auto;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.order-item img {
  width: 72px;
  height: 72px;
  object-fit: cover;
  border-radius: 10px;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.item-info span {
  color: #909399;
  font-size: 13px;
}

.item-price {
  color: #ff7d00;
  font-weight: 700;
}

.amount-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.amount-row.total {
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.submit-bar {
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

.submit-btn,
.ghost-btn,
.text-btn {
  border: none;
  cursor: pointer;
}

.submit-btn {
  background: #ff7d00;
  color: white;
  border-radius: 999px;
  padding: 10px 18px;
}

.ghost-btn,
.text-btn {
  background: transparent;
  color: #ff7d00;
}

.empty-tip {
  color: #909399;
}
</style>
