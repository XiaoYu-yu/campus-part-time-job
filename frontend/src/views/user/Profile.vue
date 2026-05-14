<template>
  <UserLayout>
    <div class="profile-page">
      <section class="profile-card">
        <img :src="userInfo.avatar || defaultAvatar" :alt="userInfo.name" class="avatar" />
        <div class="profile-info">
          <h2>{{ userInfo.name || '未命名用户' }}</h2>
          <p>{{ userInfo.phone || '暂无手机号' }}</p>
        </div>
      </section>

      <section class="stats-card">
        <div class="stat-item">
          <strong>{{ stats.orders }}</strong>
          <span>订单数</span>
        </div>
        <div class="stat-item">
          <strong>{{ stats.addresses }}</strong>
          <span>地址数</span>
        </div>
      </section>

      <section class="card">
        <div class="section-header">
          <h3>地址管理</h3>
          <button class="text-btn" @click="openAddressDialog()">新增地址</button>
        </div>
        <div v-if="addresses.length" class="address-list">
          <div v-for="address in addresses" :key="address.id" class="address-item">
            <div>
              <div class="address-title">
                <strong>{{ address.consignee }}</strong>
                <span>{{ address.phone }}</span>
                <span v-if="address.isDefault === 1" class="default-tag">默认</span>
              </div>
              <div>{{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}</div>
            </div>
            <div class="address-actions">
              <button class="text-btn" @click="openAddressDialog(address)">编辑</button>
            </div>
          </div>
        </div>
        <div v-else class="empty-tip">暂无地址信息</div>
      </section>

      <section class="card">
        <div class="menu-item" @click="router.push('/user/orders')">我的订单</div>
        <div class="menu-item" @click="router.push('/user/cart')">我的购物车</div>
      </section>

      <section class="card">
        <div class="section-header">
          <h3>校园代送</h3>
          <span class="section-tip">常用入口</span>
        </div>
        <div class="menu-item" @click="router.push('/user/campus/order-result')">查看代送进度</div>
        <div class="menu-item" @click="router.push('/user/campus/after-sale-result')">查看售后结果</div>
        <div class="menu-item" @click="router.push('/user/campus/courier-onboarding')">报名校园兼职</div>
        <div class="menu-item" @click="router.push('/parttime/login')">兼职端登录</div>
        <div class="menu-item" @click="router.push('/parttime/workbench')">兼职工作台</div>
        <div class="menu-item" @click="router.push('/feedback?role=USER&from=user-profile')">问题反馈</div>
        <div class="menu-item" @click="router.push('/legal/privacy')">隐私政策</div>
      </section>

      <button class="logout-btn" @click="handleLogout">退出登录</button>

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
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import UserLayout from '../../layout/UserLayout.vue'
import { addAddress, getAddressList, updateAddress } from '../../api/address'
import { customerLogout, getCustomerInfo } from '../../api/customer'
import { getUserOrders } from '../../api/customer-order'
import { useCustomerStore } from '../../stores/customer'

const router = useRouter()
const customerStore = useCustomerStore()
const defaultAvatar = 'https://dummyimage.com/120x120/f3f4f6/9ca3af&text=U'

const userInfo = ref({ ...customerStore.userInfo })
const addresses = ref([])
const stats = ref({ orders: 0, addresses: 0 })

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

const loadProfile = async () => {
  const [userRes, addressRes, orderRes] = await Promise.all([
    getCustomerInfo(),
    getAddressList(),
    getUserOrders({ page: 1, pageSize: 100 })
  ])
  userInfo.value = userRes
  customerStore.setUserInfo(userRes)
  addresses.value = addressRes || []
  stats.value = {
    orders: orderRes.total || 0,
    addresses: addresses.value.length
  }
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
  await loadProfile()
}

const handleLogout = async () => {
  await customerLogout()
  customerStore.logout()
  ElMessage.success('已退出登录')
  router.push('/user/login')
}

onMounted(() => loadProfile())
</script>

<style scoped lang="scss">
.profile-page {
  padding: 16px;
  overflow-x: hidden;
}

.profile-card,
.stats-card,
.card {
  background: #ffffff;
  border-radius: 14px;
  padding: 16px;
  margin-bottom: 14px;
  border: 1px solid #e4e4e7;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05), 0 4px 12px rgba(0, 0, 0, 0.03);
}

.profile-card {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e4e4e7;
  flex-shrink: 0;
}

.profile-info {
  min-width: 0;

  h2 {
    margin: 0 0 4px;
    font-size: 18px;
    color: #18181b;
    font-weight: 700;
  }

  p {
    margin: 0;
    color: #71717a;
    font-size: 14px;
  }
}

.stats-card {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.stat-item {
  text-align: center;
  padding: 12px 8px;
  border-radius: 12px;
  background: #fafafa;
  border: 1px solid #e4e4e7;

  strong {
    display: block;
    font-size: 22px;
    color: #0f9f8f;
    font-weight: 700;
    margin-bottom: 4px;
  }

  span {
    color: #71717a;
    font-size: 13px;
  }
}

.section-header,
.address-item,
.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header {
  margin-bottom: 12px;

  h3 {
    margin: 0;
    font-size: 16px;
    color: #18181b;
    font-weight: 700;
  }
}

.address-item {
  padding: 12px 0;
  border-bottom: 1px solid #e4e4e7;
  font-size: 14px;
  color: #3f3f46;

  &:last-child {
    border-bottom: none;
  }
}

.address-title {
  display: flex;
  gap: 10px;
  margin-bottom: 6px;
  flex-wrap: wrap;

  strong {
    color: #18181b;
  }

  span {
    color: #71717a;
  }
}

.default-tag {
  color: #f59e0b;
  font-size: 12px;
  font-weight: 600;
  background: #fffbeb;
  padding: 2px 8px;
  border-radius: 6px;
}

.section-tip {
  color: #a1a1aa;
  font-size: 12px;
}

.menu-item {
  padding: 14px 0;
  border-bottom: 1px solid #f4f4f5;
  cursor: pointer;
  font-size: 14px;
  color: #18181b;
  transition: background 0.15s;

  &::after {
    content: '';
    width: 7px;
    height: 7px;
    border-top: 2px solid #a1a1aa;
    border-right: 2px solid #a1a1aa;
    transform: rotate(45deg);
    flex-shrink: 0;
    margin-left: 8px;
  }

  &:active {
    background: #f4f4f5;
  }
}

.menu-item:last-child {
  border-bottom: none;
}

.logout-btn,
.text-btn {
  border: none;
  cursor: pointer;
  font-family: inherit;
}

.logout-btn {
  width: 100%;
  min-height: 48px;
  background: #ef4444;
  color: #ffffff;
  border-radius: 12px;
  padding: 14px 0;
  font-size: 15px;
  font-weight: 600;
  margin-top: 8px;
  transition: background 0.2s, box-shadow 0.2s;

  &:hover {
    background: #dc2626;
    box-shadow: 0 4px 14px rgba(239, 68, 68, 0.3);
  }

  &:active {
    background: #b91c1c;
  }
}

.text-btn {
  background: transparent;
  color: #0f9f8f;
  font-weight: 600;
  font-size: 13px;
  padding: 4px 0;
}

.empty-tip {
  color: #a1a1aa;
  font-size: 14px;
  padding: 12px 0;
}

@media (max-width: 360px) {
  .profile-page {
    padding: 12px;
  }

  .profile-card,
  .stats-card,
  .card {
    padding: 14px 12px;
    border-radius: 12px;
  }

  .avatar {
    width: 60px;
    height: 60px;
  }

  .profile-info h2 {
    font-size: 16px;
  }

  .stat-item strong {
    font-size: 20px;
  }
}
</style>
