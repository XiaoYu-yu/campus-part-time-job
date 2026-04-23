<!--
  运营人员页面组件
  @description 提供后台运营人员信息的增删改查功能，包含搜索、分页、状态管理等功能
-->
<template>
  <div class="employee-management">
    <section class="employee-hero">
      <div>
        <span class="eyebrow">Campus Staff</span>
        <h2>运营人员</h2>
        <p>复用 employee 账号体系，管理校内兼职平台后台运营与审核人员。</p>
      </div>
      <div class="hero-notes">
        <span>后台账号</span>
        <strong>employee</strong>
      </div>
    </section>

    <div class="content">
      <!-- 搜索区 -->
      <div class="toolbar-card">
        <div class="toolbar-copy">
          <span class="section-kicker">筛选与维护</span>
          <h3>按姓名或手机号定位运营人员</h3>
          <p>这里只维护后台 employee 账号，不改变旧订单、地址或外卖兼容模块。</p>
        </div>
        <div class="search-section">
          <el-input
            v-model="searchQuery"
            class="staff-search"
            placeholder="输入姓名或手机号"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button @click="handleSearch">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
            </template>
          </el-input>
          <el-button type="primary" class="add-button" @click="handleAddEmployee">
            <el-icon><Plus /></el-icon>
            新增运营人员
          </el-button>
        </div>
      </div>

      <!-- 表格区 -->
      <div class="table-card">
        <div class="table-heading">
          <div>
            <span class="section-kicker">人员列表</span>
            <h3>后台运营账号</h3>
          </div>
          <el-tag type="info" effect="plain">共 {{ total }} 人</el-tag>
        </div>

        <el-table
          v-loading="loading"
          :data="employeeList"
          class="campus-table"
          style="width: 100%;"
          empty-text="暂无运营人员，可先新增一个后台账号"
        >
          <el-table-column prop="id" label="员工ID" width="90" />
          <el-table-column prop="name" label="姓名" min-width="130" show-overflow-tooltip />
          <el-table-column prop="phone" label="手机号" min-width="150" />
          <el-table-column prop="position" label="职位" min-width="130" show-overflow-tooltip />
          <el-table-column prop="department" label="部门" min-width="140" show-overflow-tooltip />
          <el-table-column prop="entryDate" label="入职日期" width="150" />
          <el-table-column label="状态" width="150">
            <template #default="scope">
              <div class="status-cell">
                <el-switch
                  v-model="scope.row.status"
                  :active-value="1"
                  :inactive-value="0"
                  @change="handleStatusChange(scope.row.id, scope.row.status)"
                />
                <span :class="['status-text', scope.row.status === 1 ? 'is-active' : 'is-disabled']">
                  {{ scope.row.status === 1 ? '启用' : '禁用' }}
                </span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="190" fixed="right">
            <template #default="scope">
              <div class="action-cell">
                <el-button size="small" @click="handleEditEmployee(scope.row)">
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button size="small" type="danger" plain @click="handleDeleteEmployee(scope.row.id)">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页区 -->
      <div class="pagination-section">
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
      
      <!-- 新增/编辑员工弹窗 -->
      <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="500px"
        class="employee-dialog"
      >
        <el-form :model="form" label-width="80px" :rules="formRules" ref="formRef">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="form.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="职位" prop="position">
            <el-input v-model="form.position" placeholder="请输入职位" />
          </el-form-item>
          <el-form-item label="部门" prop="department">
            <el-input v-model="form.department" placeholder="请输入部门" />
          </el-form-item>
          <el-form-item label="入职日期" prop="entryDate">
            <el-date-picker
              v-model="form.entryDate"
              type="date"
              placeholder="选择入职日期"
              style="width: 100%;"
              value-format="YYYY-MM-DD"
            />
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
 * 运营人员页面逻辑
 * @module Employee
 */
import { ref, onMounted } from 'vue'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { normalizeTextFields } from '../utils/text'
import {
  getEmployeeList,
  addEmployee,
  updateEmployee,
  deleteEmployee,
  updateEmployeeStatus
} from '../api/employee'

// ==================== 响应式数据 ====================

/** @type {import('vue').Ref<boolean>} 表格加载状态 */
const loading = ref(false)

/** @type {import('vue').Ref<boolean>} 保存按钮加载状态 */
const saveLoading = ref(false)

/** @type {import('vue').Ref<Array>} 员工列表数据 */
const employeeList = ref([])

/** @type {import('vue').Ref<number>} 员工总数 */
const total = ref(0)

/** @type {import('vue').Ref<string>} 搜索关键词 */
const searchQuery = ref('')

/** @type {import('vue').Ref<number>} 当前页码 */
const currentPage = ref(1)

/** @type {import('vue').Ref<number>} 每页显示条数 */
const pageSize = ref(10)

/** @type {import('vue').Ref<boolean>} 弹窗显示状态 */
const dialogVisible = ref(false)

/** @type {import('vue').Ref<string>} 弹窗标题 */
const dialogTitle = ref('新增员工')

/** @type {import('vue').Ref<Object>} 表单引用 */
const formRef = ref(null)

/**
 * @typedef {Object} EmployeeForm
 * @property {number|null} id - 员工ID
 * @property {string} name - 姓名
 * @property {string} phone - 手机号
 * @property {string} position - 职位
 * @property {string} department - 部门
 * @property {string} entryDate - 入职日期
 */

/** @type {import('vue').Ref<EmployeeForm>} 表单数据 */
const form = ref({
  id: null,
  name: '',
  phone: '',
  position: '',
  department: '',
  entryDate: ''
})

/** @type {Object} 表单校验规则 */
const formRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  position: [
    { required: true, message: '请输入职位', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请输入部门', trigger: 'blur' }
  ],
  entryDate: [
    { required: true, message: '请选择入职日期', trigger: 'change' }
  ]
}

// ==================== 方法 ====================

/**
 * 加载员工列表数据
 * @description 根据当前分页和搜索条件获取员工列表
 * @returns {Promise<void>}
 */
const loadEmployeeList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    
    // 如果有搜索关键词，添加到参数中
    if (searchQuery.value) {
      // 判断是手机号还是姓名
      if (/^\d+$/.test(searchQuery.value)) {
        params.phone = searchQuery.value
      } else {
        params.name = searchQuery.value
      }
    }
    
    const res = normalizeTextFields(await getEmployeeList(params))
    employeeList.value = normalizeTextFields(res.records || [])
    total.value = res.total || 0
  } catch (error) {
    console.error('加载员工列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 搜索处理
 * @description 重置页码并重新加载数据
 */
const handleSearch = () => {
  currentPage.value = 1
  loadEmployeeList()
}

/**
 * 打开新增员工弹窗
 * @description 重置表单数据并显示弹窗
 */
const handleAddEmployee = () => {
  dialogTitle.value = '新增员工'
  form.value = {
    id: null,
    name: '',
    phone: '',
    position: '',
    department: '',
    entryDate: ''
  }
  dialogVisible.value = true
}

/**
 * 打开编辑员工弹窗
 * @param {Object} row - 当前行数据
 * @param {number} row.id - 员工ID
 * @param {string} row.name - 姓名
 * @param {string} row.phone - 手机号
 * @param {string} row.position - 职位
 * @param {string} row.department - 部门
 * @param {string} row.entryDate - 入职日期
 */
const handleEditEmployee = (row) => {
  const normalizedRow = normalizeTextFields(row)
  dialogTitle.value = '编辑员工'
  form.value = { 
    id: normalizedRow.id,
    name: normalizedRow.name,
    phone: normalizedRow.phone,
    position: normalizedRow.position,
    department: normalizedRow.department,
    entryDate: normalizedRow.entryDate
  }
  dialogVisible.value = true
}

/**
 * 删除员工
 * @param {number} id - 员工ID
 * @description 弹出确认框后执行删除操作
 */
const handleDeleteEmployee = (id) => {
  ElMessageBox.confirm(
    '确定要删除该员工吗？此操作不可恢复',
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteEmployee(id)
      ElMessage.success('删除成功')
      loadEmployeeList()
    } catch (error) {
      console.error('删除员工失败:', error)
    }
  }).catch(() => {
    // 取消删除
  })
}

/**
 * 保存员工信息
 * @description 表单校验通过后，根据是否有ID判断是新增还是编辑
 */
const handleSave = async () => {
  try {
    await formRef.value.validate()
    saveLoading.value = true
    
    if (form.value.id) {
      // 编辑
      await updateEmployee(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      // 新增
      await addEmployee(form.value)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    loadEmployeeList()
  } catch (error) {
    console.error('保存员工失败:', error)
  } finally {
    saveLoading.value = false
  }
}

/**
 * 修改员工状态
 * @param {number} id - 员工ID
 * @param {number} status - 状态（0-禁用，1-启用）
 */
const handleStatusChange = async (id, status) => {
  try {
    await updateEmployeeStatus(id, status)
    ElMessage.success(`员工状态已${status === 1 ? '启用' : '禁用'}`)
  } catch (error) {
    console.error('修改状态失败:', error)
    // 恢复原状态
    loadEmployeeList()
  }
}

/**
 * 每页条数改变
 * @param {number} size - 新的每页条数
 */
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadEmployeeList()
}

/**
 * 页码改变
 * @param {number} current - 新的页码
 */
const handleCurrentChange = (current) => {
  currentPage.value = current
  loadEmployeeList()
}

// ==================== 生命周期 ====================

/**
 * 组件挂载时加载数据
 */
onMounted(() => {
  loadEmployeeList()
})
</script>

<style lang="scss" scoped>
.employee-management {
  display: flex;
  flex-direction: column;
  gap: 22px;

  .employee-hero {
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

    p {
      margin: 0;
      color: #64748b;
      font-size: 13px;
    }
  }

  .search-section {
    display: flex;
    align-items: center;
    gap: 12px;
    flex-wrap: wrap;
    justify-content: flex-end;

    .staff-search {
      width: 320px;
    }

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

  .status-cell,
  .action-cell {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .status-text {
    font-size: 12px;
    font-weight: 800;

    &.is-active {
      color: #0f766e;
    }

    &.is-disabled {
      color: #94a3b8;
    }
  }

  .pagination-section {
    padding: 4px 2px 0;
    display: flex;
    justify-content: flex-end;
  }

  .dialog-footer {
    width: 100%;
    display: flex;
    justify-content: flex-end;
  }
}

@media (max-width: 900px) {
  .employee-management {
    .employee-hero,
    .toolbar-card {
      align-items: flex-start;
      flex-direction: column;
    }

    .search-section {
      justify-content: flex-start;
      width: 100%;

      .staff-search {
        width: 100%;
      }
    }
  }
}
</style>
