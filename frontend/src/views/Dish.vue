<!--
  商品兼容管理页面组件
  @description 旧模块兼容 — 商品管理，保留旧模块兼容能力，不作为当前校园兼职主业务入口
-->
<template>
  <div class="dish-management">
    <div class="compat-notice">
      <el-alert type="info" :closable="false" show-icon>
        <template #title>
          该页面保留旧模块兼容能力，不作为当前校园兼职主业务入口。
        </template>
      </el-alert>
    </div>
    <h2>商品兼容管理</h2>
    <div class="content">
      <!-- 搜索和筛选 -->
      <div class="search-filter">
        <el-input
          v-model="searchForm.name"
          placeholder="请输入菜品名称"
          style="width: 200px; margin-right: 10px"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select
          v-model="searchForm.categoryId"
          placeholder="请选择分类"
          style="width: 150px; margin-right: 10px"
          clearable
        >
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="primary" @click="handleAdd" style="margin-left: 10px;">
          <el-icon><Plus /></el-icon>
          新增菜品
        </el-button>
      </div>

      <!-- 批量操作 -->
      <div class="batch-actions" style="margin: 20px 0;">
        <el-button
          type="primary"
          :disabled="selectedDishes.length === 0"
          @click="handleBatchEnable"
        >
          批量启用
        </el-button>
        <el-button
          type="danger"
          :disabled="selectedDishes.length === 0"
          @click="handleBatchDisable"
        >
          批量禁用
        </el-button>
      </div>

      <!-- 菜品列表 -->
      <el-table
        v-loading="loading"
        :data="dishList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="菜品ID" width="80" />
        <el-table-column prop="name" label="菜品名称" width="150" />
        <el-table-column label="菜品图片" width="120">
          <template #default="scope">
            <el-image
              :src="scope.row.image"
              fit="cover"
              style="width: 80px; height: 80px; border-radius: 4px"
            />
          </template>
        </el-table-column>
        <el-table-column label="分类" width="100">
          <template #default="scope">
            {{ scope.row.categoryName || getCategoryName(scope.row.categoryId) }}
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100">
          <template #default="scope">
            ¥{{ Number(scope.row.price).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row.id, scope.row.status)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination" style="margin-top: 20px; text-align: right;">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>

      <!-- 新增/编辑菜品弹窗 -->
      <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="600px"
      >
        <el-form :model="form" label-width="100px" :rules="formRules" ref="formRef">
          <el-form-item label="菜品名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入菜品名称" />
          </el-form-item>
          <el-form-item label="分类" prop="categoryId">
            <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%;">
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="价格" prop="price">
            <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%;" />
          </el-form-item>
          <el-form-item label="图片" prop="image">
            <el-upload
              class="image-uploader"
              :show-file-list="false"
              :http-request="handleUpload"
              accept="image/*"
            >
              <el-image
                v-if="form.image"
                :src="form.image"
                fit="cover"
                class="uploaded-image"
              />
              <div v-else class="upload-placeholder">
                <el-icon><Plus /></el-icon>
                <span>点击上传图片</span>
              </div>
            </el-upload>
          </el-form-item>
          <el-form-item label="描述" prop="description">
            <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入描述" />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSave" :loading="saveLoading">保存</el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
/**
 * 菜品管理页面逻辑
 * @module Dish
 */
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import {
  getDishList,
  addDish,
  updateDish,
  deleteDish,
  updateDishStatus,
  batchUpdateDishStatus
} from '../api/dish'
import { getCategoryList } from '../api/category'
import { uploadImage } from '../api/upload'

// ==================== 响应式数据 ====================

/** @type {import('vue').Ref<boolean>} 表格加载状态 */
const loading = ref(false)

/** @type {import('vue').Ref<boolean>} 保存按钮加载状态 */
const saveLoading = ref(false)

/** @type {import('vue').Ref<Array>} 菜品列表数据 */
const dishList = ref([])

/** @type {import('vue').Ref<Array>} 分类列表数据 */
const categories = ref([])

/** @type {import('vue').Ref<Array>} 选中的菜品列表 */
const selectedDishes = ref([])

/** @type {import('vue').Ref<number>} 当前页码 */
const currentPage = ref(1)

/** @type {import('vue').Ref<number>} 每页显示条数 */
const pageSize = ref(10)

/** @type {import('vue').Ref<number>} 菜品总数 */
const total = ref(0)

/**
 * @typedef {Object} SearchForm
 * @property {string} name - 菜品名称
 * @property {number|string} categoryId - 分类ID
 */

/** @type {import('vue').Ref<SearchForm>} 搜索表单数据 */
const searchForm = ref({
  name: '',
  categoryId: ''
})

/** @type {import('vue').Ref<boolean>} 弹窗显示状态 */
const dialogVisible = ref(false)

/** @type {import('vue').Ref<string>} 弹窗标题 */
const dialogTitle = ref('新增菜品')

/** @type {import('vue').Ref<Object>} 表单引用 */
const formRef = ref(null)

/**
 * @typedef {Object} DishForm
 * @property {number|null} id - 菜品ID
 * @property {string} name - 菜品名称
 * @property {number|string} categoryId - 分类ID
 * @property {number} price - 价格
 * @property {string} image - 图片URL
 * @property {string} description - 描述
 */

/** @type {import('vue').Ref<DishForm>} 表单数据 */
const form = ref({
  id: null,
  name: '',
  categoryId: '',
  price: 0,
  image: '',
  description: ''
})

/** @type {Object} 表单校验规则 */
const formRules = {
  name: [
    { required: true, message: '请输入菜品名称', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' }
  ]
}

// ==================== 方法 ====================

/**
 * 格式化日期时间
 * @param {string} dateTime - ISO格式日期字符串
 * @returns {string} 格式化后的日期时间字符串 (YYYY-MM-DD HH:mm:ss)
 */
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  }).replace(/\//g, '-')
}

/**
 * 根据分类ID获取分类名称
 * @param {number} categoryId - 分类ID
 * @returns {string} 分类名称，未找到返回'未知分类'
 */
const getCategoryName = (categoryId) => {
  const category = categories.value.find(cat => cat.id === categoryId)
  return category ? category.name : '未知分类'
}

/**
 * 加载菜品列表
 * @description 根据当前分页和搜索条件获取菜品列表
 * @returns {Promise<void>}
 */
const loadDishList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      name: searchForm.value.name || undefined,
      categoryId: searchForm.value.categoryId || undefined
    }

    const res = await getDishList(params)
    dishList.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    console.error('加载菜品列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 加载分类列表
 * @description 获取所有分类用于下拉选择
 * @returns {Promise<void>}
 */
const loadCategories = async () => {
  try {
    const res = await getCategoryList()
    categories.value = res || []
  } catch (error) {
    console.error('加载分类列表失败:', error)
  }
}

/**
 * 搜索处理
 * @description 重置页码并重新加载数据
 */
const handleSearch = () => {
  currentPage.value = 1
  loadDishList()
}

/**
 * 打开新增菜品弹窗
 * @description 重置表单数据并显示弹窗
 */
const handleAdd = () => {
  dialogTitle.value = '新增菜品'
  form.value = {
    id: null,
    name: '',
    categoryId: '',
    price: 0,
    image: '',
    description: ''
  }
  dialogVisible.value = true
}

/**
 * 打开编辑菜品弹窗
 * @param {Object} row - 当前行数据
 */
const handleEdit = (row) => {
  dialogTitle.value = '编辑菜品'
  form.value = {
    id: row.id,
    name: row.name,
    categoryId: row.categoryId,
    price: Number(row.price),
    image: row.image,
    description: row.description
  }
  dialogVisible.value = true
}

/**
 * 删除菜品
 * @param {number} id - 菜品ID
 * @description 弹出确认框后执行删除操作
 */
const handleDelete = (id) => {
  ElMessageBox.confirm(
    '确定要删除该菜品吗？此操作不可恢复',
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteDish(id)
      ElMessage.success('删除成功')
      loadDishList()
    } catch (error) {
      console.error('删除菜品失败:', error)
    }
  }).catch(() => {
    // 取消删除
  })
}

/**
 * 保存菜品信息
 * @description 表单校验通过后，根据是否有ID判断是新增还是编辑
 */
const handleSave = async () => {
  try {
    await formRef.value.validate()
    saveLoading.value = true

    if (form.value.id) {
      // 编辑
      await updateDish(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      // 新增
      await addDish(form.value)
      ElMessage.success('添加成功')
    }

    dialogVisible.value = false
    loadDishList()
  } catch (error) {
    console.error('保存菜品失败:', error)
  } finally {
    saveLoading.value = false
  }
}

/**
 * 修改菜品状态
 * @param {number} id - 菜品ID
 * @param {number} status - 状态（0-禁用，1-启用）
 */
const handleStatusChange = async (id, status) => {
  try {
    await updateDishStatus(id, status)
    ElMessage.success(`菜品状态已${status === 1 ? '启用' : '禁用'}`)
  } catch (error) {
    console.error('修改状态失败:', error)
    loadDishList()
  }
}

/**
 * 批量启用菜品
 * @description 将选中的菜品批量设置为启用状态
 */
const handleBatchEnable = async () => {
  if (selectedDishes.value.length === 0) {
    ElMessage.warning('请选择要操作的菜品')
    return
  }

  const ids = selectedDishes.value.map(dish => dish.id)
  try {
    await batchUpdateDishStatus(ids, 1)
    ElMessage.success('批量启用成功')
    selectedDishes.value = []
    loadDishList()
  } catch (error) {
    console.error('批量启用失败:', error)
  }
}

/**
 * 批量禁用菜品
 * @description 将选中的菜品批量设置为禁用状态
 */
const handleBatchDisable = async () => {
  if (selectedDishes.value.length === 0) {
    ElMessage.warning('请选择要操作的菜品')
    return
  }

  const ids = selectedDishes.value.map(dish => dish.id)
  try {
    await batchUpdateDishStatus(ids, 0)
    ElMessage.success('批量禁用成功')
    selectedDishes.value = []
    loadDishList()
  } catch (error) {
    console.error('批量禁用失败:', error)
  }
}

/**
 * 表格选择变更处理
 * @param {Array} val - 选中的行数据
 */
const handleSelectionChange = (val) => {
  selectedDishes.value = val
}

/**
 * 图片上传处理
 * @param {Object} options - 上传选项
 * @param {File} options.file - 上传的文件对象
 */
const handleUpload = async (options) => {
  try {
    const res = await uploadImage(options.file)
    form.value.image = res
    ElMessage.success('图片上传成功')
  } catch (error) {
    console.error('图片上传失败:', error)
    ElMessage.error('图片上传失败')
  }
}

/**
 * 每页条数改变
 * @param {number} size - 新的每页条数
 */
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadDishList()
}

/**
 * 页码改变
 * @param {number} current - 新的页码
 */
const handleCurrentChange = (current) => {
  currentPage.value = current
  loadDishList()
}

// ==================== 生命周期 ====================

/**
 * 组件挂载时初始化数据
 */
onMounted(() => {
  loadCategories()
  loadDishList()
})
</script>

<style scoped>
.dish-management {
  .compat-notice {
    margin-bottom: 16px;
  }

  h2 {
    margin-bottom: 20px;
    color: #303133;
  }

  .content {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }

  .search-filter {
    margin-bottom: 20px;
  }

  .batch-actions {
    margin: 20px 0;
  }

  .pagination {
    margin-top: 20px;
    text-align: right;
  }

  .dialog-footer {
    width: 100%;
    display: flex;
    justify-content: flex-end;
  }

  .image-uploader {
    :deep(.el-upload) {
      border: 1px dashed var(--el-border-color);
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: var(--el-transition-duration-fast);

      &:hover {
        border-color: var(--el-color-primary);
      }
    }

    .uploaded-image {
      width: 178px;
      height: 178px;
      display: block;
    }

    .upload-placeholder {
      width: 178px;
      height: 178px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: #8c939d;

      .el-icon {
        font-size: 28px;
        margin-bottom: 8px;
      }

      span {
        font-size: 14px;
      }
    }
  }
}
</style>
