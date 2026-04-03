<!--
  员工管理页面组件
  @description 提供员工信息的增删改查功能，包含搜索、分页、状态管理等功能
-->
<template>
  <div class="employee-management">
    <h2>员工管理</h2>
    <div class="content">
      <!-- 搜索区 -->
      <div class="search-section">
        <el-input
          v-model="searchQuery"
          placeholder="请输入员工姓名或手机号"
          style="width: 300px; margin-right: 10px;"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </template>
        </el-input>
        <el-button type="primary" @click="handleAddEmployee">
          <el-icon><Plus /></el-icon>
          新增员工
        </el-button>
      </div>
      
      <!-- 表格区 -->
      <el-table
        v-loading="loading"
        :data="employeeList"
        style="width: 100%; margin-top: 20px;"
        border
      >
        <el-table-column prop="id" label="员工ID" width="80" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="position" label="职位" width="120" />
        <el-table-column prop="department" label="部门" width="120" />
        <el-table-column prop="entryDate" label="入职日期" width="150" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row.id, scope.row.status)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleEditEmployee(scope.row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button size="small" type="danger" @click="handleDeleteEmployee(scope.row.id)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
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
 * 员工管理页面逻辑
 * @module Employee
 */
import { ref, onMounted } from 'vue'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
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
    
    const res = await getEmployeeList(params)
    employeeList.value = res.records || []
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
  dialogTitle.value = '编辑员工'
  form.value = { 
    id: row.id,
    name: row.name,
    phone: row.phone,
    position: row.position,
    department: row.department,
    entryDate: row.entryDate
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

<style scoped>
.employee-management {
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
  
  .search-section {
    display: flex;
    align-items: center;
  }
  
  .pagination-section {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
  
  .dialog-footer {
    width: 100%;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
