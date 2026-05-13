<!--
  分类管理页面组件
  @description 旧模块兼容 — 分类管理，保留旧模块兼容能力，不作为当前校园兼职主业务入口
-->
<template>
  <MainLayout>
    <div class="category-management">
      <section class="page-hero">
        <div>
          <span class="eyebrow">Legacy Compat</span>
          <h2>分类管理</h2>
          <p>旧模块兼容 — 维护商品分类体系，保留旧模块兼容能力。</p>
        </div>
        <div class="hero-notes">
          <span>旧模块</span>
          <strong>category</strong>
        </div>
      </section>

      <div class="content">
        <!-- 工具栏 -->
        <div class="toolbar-card">
          <div class="toolbar-copy">
            <span class="section-kicker">分类维护</span>
            <h3>新增或编辑商品分类</h3>
          </div>
          <div class="toolbar-actions">
            <el-button type="primary" class="add-button" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增分类
            </el-button>
          </div>
        </div>

        <!-- 表格区 -->
        <div class="table-card">
          <div class="table-heading">
            <div>
              <span class="section-kicker">分类列表</span>
              <h3>全部商品分类</h3>
            </div>
          </div>

          <el-table v-loading="loading" :data="categories" class="campus-table" style="width: 100%">
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
            <el-table-column label="操作" width="150">
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
        </div>

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
  </MainLayout>
</template>

<script setup>
/**
 * 分类管理页面逻辑
 * @module Category
 */
import { ref, onMounted } from 'vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import MainLayout from '../layout/MainLayout.vue'
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

<style lang="scss" scoped>
.category-management {
  display: flex;
  flex-direction: column;
  gap: 22px;

  .page-hero {
    position: relative;
    overflow: hidden;
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    gap: 24px;
    min-height: 170px;
    padding: 34px 40px;
    border: 1px solid rgba(15, 118, 110, 0.12);
    border-radius: 28px;
    background:
      radial-gradient(circle at 86% 18%, rgba(132, 204, 22, 0.28), transparent 28%),
      radial-gradient(circle at 16% 20%, rgba(14, 165, 233, 0.16), transparent 32%),
      linear-gradient(135deg, rgba(255, 255, 255, 0.94) 0%, rgba(236, 253, 245, 0.86) 54%, rgba(224, 242, 254, 0.86) 100%);
    box-shadow: 0 24px 60px rgba(15, 23, 42, 0.09);
    color: #0f172a;

    &::after {
      content: '';
      position: absolute;
      right: -80px;
      bottom: -110px;
      width: 270px;
      height: 270px;
      border-radius: 50%;
      border: 38px solid rgba(15, 118, 110, 0.07);
    }

    h2 {
      position: relative;
      margin: 8px 0 10px;
      font-size: 34px;
      font-weight: 900;
      letter-spacing: -0.03em;
    }

    p {
      position: relative;
      max-width: 560px;
      margin: 0;
      color: #475569;
      font-size: 15px;
      line-height: 1.8;
    }
  }

  .eyebrow,
  .section-kicker {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    color: #0f766e;
    font-size: 12px;
    font-weight: 900;
    letter-spacing: 0.14em;
    text-transform: uppercase;
  }

  .hero-notes {
    position: relative;
    z-index: 1;
    display: grid;
    gap: 4px;
    min-width: 142px;
    padding: 18px 20px;
    border: 1px solid rgba(15, 118, 110, 0.12);
    border-radius: 22px;
    background: rgba(255, 255, 255, 0.72);
    backdrop-filter: blur(14px);

    span {
      color: #64748b;
      font-size: 12px;
    }

    strong {
      color: #0f172a;
      font-size: 20px;
      letter-spacing: 0.02em;
    }
  }

  .content {
    display: flex;
    flex-direction: column;
    gap: 18px;
  }

  .toolbar-card,
  .table-card {
    border: 1px solid rgba(15, 118, 110, 0.1);
    border-radius: 24px;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(12px);
    box-shadow: 0 18px 42px rgba(15, 23, 42, 0.08);
  }

  .toolbar-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 18px;
    padding: 22px 24px;

    h3 {
      margin: 6px 0 6px;
      color: #0f172a;
      font-size: 18px;
      font-weight: 900;
    }
  }

  .toolbar-actions {
    display: flex;
    align-items: center;
    gap: 12px;

    .add-button {
      min-width: 142px;
      border: none;
      background: linear-gradient(135deg, #0f766e, #0ea5e9);
      box-shadow: 0 12px 24px rgba(14, 165, 233, 0.22);
    }
  }

  .table-card {
    padding: 22px 24px 16px;
  }

  .table-heading {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 16px;
    margin-bottom: 16px;

    h3 {
      margin: 6px 0 0;
      color: #0f172a;
      font-size: 20px;
      font-weight: 900;
    }
  }

  .campus-table {
    overflow: hidden;
    border: 1px solid #e2e8f0;
    border-radius: 18px;

    :deep(.el-table__header-wrapper th) {
      background: #f8fafc;
      color: #0f172a;
      font-weight: 800;
    }

    :deep(.el-table__row) {
      color: #334155;
    }

    :deep(.el-table__cell) {
      border-bottom-color: #edf2f7;
    }
  }

  .dialog-footer {
    width: 100%;
    display: flex;
    justify-content: flex-end;
  }
}

@media (max-width: 900px) {
  .category-management {
    .page-hero,
    .toolbar-card {
      align-items: flex-start;
      flex-direction: column;
    }
  }
}
</style>
