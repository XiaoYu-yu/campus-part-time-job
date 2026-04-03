<!--
  分类管理页面组件
  @description 提供菜品/套餐分类的增删改查功能，支持排序和状态管理
-->
<template>
  <div class="category-management">
    <h2>分类管理</h2>
    <div class="content">
      <!-- 操作栏 -->
      <div class="operation-bar">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增分类
        </el-button>
      </div>

      <el-table v-loading="loading" :data="categories" style="width: 100%">
        <el-table-column prop="id" label="分类ID" width="80" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row.id, scope.row.status)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="120">
          <template #default="scope">
            <el-input-number
              v-model="scope.row.sort"
              @change="handleSortChange(scope.row.id, scope.row.sort)"
              :min="1"
              :max="categories.length"
              size="small"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 新增/编辑分类弹窗 -->
      <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="500px"
      >
        <el-form :model="form" label-width="80px" :rules="formRules" ref="formRef">
          <el-form-item label="分类名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入分类名称" />
          </el-form-item>
          <el-form-item label="排序" prop="sort">
            <el-input-number v-model="form.sort" :min="1" style="width: 100%;" />
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
 * 分类管理页面逻辑
 * @module Category
 */
import { ref, onMounted } from 'vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getCategoryList,
  addCategory,
  updateCategory,
  deleteCategory,
  updateCategoryStatus,
  updateCategorySort
} from '../api/category'

// ==================== 响应式数据 ====================

/** @type {import('vue').Ref<boolean>} 表格加载状态 */
const loading = ref(false)

/** @type {import('vue').Ref<boolean>} 保存按钮加载状态 */
const saveLoading = ref(false)

/** @type {import('vue').Ref<Array>} 分类列表数据 */
const categories = ref([])

/** @type {import('vue').Ref<boolean>} 弹窗显示状态 */
const dialogVisible = ref(false)

/** @type {import('vue').Ref<string>} 弹窗标题 */
const dialogTitle = ref('新增分类')

/** @type {import('vue').Ref<Object>} 表单引用 */
const formRef = ref(null)

/**
 * @typedef {Object} CategoryForm
 * @property {number|null} id - 分类ID
 * @property {string} name - 分类名称
 * @property {number} sort - 排序值
 */

/** @type {import('vue').Ref<CategoryForm>} 表单数据 */
const form = ref({
  id: null,
  name: '',
  sort: 1
})

/** @type {Object} 表单校验规则 */
const formRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ],
  sort: [
    { required: true, message: '请输入排序', trigger: 'blur' }
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
 * 加载分类列表
 * @description 从服务器获取所有分类数据
 * @returns {Promise<void>}
 */
const loadCategories = async () => {
  loading.value = true
  try {
    const res = await getCategoryList()
    categories.value = res || []
  } catch (error) {
    console.error('加载分类列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 打开新增分类弹窗
 * @description 重置表单并设置默认排序值为当前分类数+1
 */
const handleAdd = () => {
  dialogTitle.value = '新增分类'
  form.value = {
    id: null,
    name: '',
    sort: categories.value.length + 1
  }
  dialogVisible.value = true
}

/**
 * 打开编辑分类弹窗
 * @param {Object} row - 当前行数据
 * @param {number} row.id - 分类ID
 * @param {string} row.name - 分类名称
 * @param {number} row.sort - 排序值
 */
const handleEdit = (row) => {
  dialogTitle.value = '编辑分类'
  form.value = {
    id: row.id,
    name: row.name,
    sort: row.sort
  }
  dialogVisible.value = true
}

/**
 * 删除分类
 * @param {number} id - 分类ID
 * @description 弹出确认框后执行删除操作
 */
const handleDelete = (id) => {
  ElMessageBox.confirm(
    '确定要删除该分类吗？此操作不可恢复',
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteCategory(id)
      ElMessage.success('删除成功')
      loadCategories()
    } catch (error) {
      console.error('删除分类失败:', error)
    }
  }).catch(() => {
    // 取消删除
  })
}

/**
 * 保存分类信息
 * @description 表单校验通过后，根据是否有ID判断是新增还是编辑
 */
const handleSave = async () => {
  try {
    await formRef.value.validate()
    saveLoading.value = true

    if (form.value.id) {
      // 编辑
      await updateCategory(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      // 新增
      await addCategory(form.value)
      ElMessage.success('添加成功')
    }

    dialogVisible.value = false
    loadCategories()
  } catch (error) {
    console.error('保存分类失败:', error)
  } finally {
    saveLoading.value = false
  }
}

/**
 * 修改分类状态
 * @param {number} id - 分类ID
 * @param {number} status - 状态（0-禁用，1-启用）
 */
const handleStatusChange = async (id, status) => {
  try {
    await updateCategoryStatus(id, status)
    ElMessage.success(`分类状态已${status === 1 ? '启用' : '禁用'}`)
  } catch (error) {
    console.error('修改状态失败:', error)
    // 恢复原状态
    loadCategories()
  }
}

/**
 * 修改分类排序
 * @param {number} id - 分类ID
 * @param {number} newSort - 新的排序值
 */
const handleSortChange = async (id, newSort) => {
  try {
    await updateCategorySort(id, newSort)
    ElMessage.success('排序已更新')
    loadCategories()
  } catch (error) {
    console.error('修改排序失败:', error)
    loadCategories()
  }
}

// ==================== 生命周期 ====================

/**
 * 组件挂载时加载数据
 */
onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.category-management {
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

  .operation-bar {
    margin-bottom: 20px;
  }

  :deep(.el-table) {
    margin-top: 20px;
  }

  :deep(.el-table__header-wrapper th) {
    background-color: #f5f7fa;
    font-weight: 600;
  }

  :deep(.el-table__row:nth-child(even)) {
    background-color: #fafafa;
  }

  .dialog-footer {
    width: 100%;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
