<template>
  <div class="component-demo">
    <h2>通用组件示例</h2>
    
    <!-- 搜索区示例 -->
    <div class="demo-section">
      <h3>1. 搜索区组件</h3>
      <BaseSearch
        :items="searchItems"
        :initial-form="searchForm"
        :show-add="true"
        @search="handleSearch"
        @reset="handleReset"
        @add="handleAdd"
      />
    </div>

    <!-- 表格示例 -->
    <div class="demo-section">
      <h3>2. 表格组件</h3>
      <BaseTable
        :data="tableData"
        :columns="tableColumns"
        :loading="loading"
        :show-selection="true"
        :total="total"
        :current-page="currentPage"
        :page-size="pageSize"
        :actions="tableActions"
        @selection-change="handleSelectionChange"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 弹窗表单示例 -->
    <BaseDialogForm
      v-model:visible="dialogVisible"
      :title="dialogTitle"
      :fields="formFields"
      :form-data="formData"
      :rules="formRules"
      @submit="handleFormSubmit"
      @cancel="handleFormCancel"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import BaseTable from '../components/BaseTable.vue'
import BaseSearch from '../components/BaseSearch.vue'
import BaseDialogForm from '../components/BaseDialogForm.vue'

// 搜索配置
const searchForm = ref({
  name: '',
  status: '',
  date: ''
})

const searchItems = ref([
  {
    type: 'input',
    prop: 'name',
    placeholder: '请输入名称',
    style: 'width: 200px',
    prefixIcon: Search
  },
  {
    type: 'select',
    prop: 'status',
    placeholder: '请选择状态',
    style: 'width: 150px',
    options: [
      { label: '启用', value: '1' },
      { label: '禁用', value: '0' }
    ]
  },
  {
    type: 'date',
    prop: 'date',
    placeholder: '请选择日期',
    style: 'width: 200px'
  }
])

// 表格配置
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const selectedRows = ref([])

const tableColumns = ref([
  {
    prop: 'id',
    label: 'ID',
    width: '80'
  },
  {
    prop: 'name',
    label: '名称',
    width: '150'
  },
  {
    prop: 'status',
    label: '状态',
    width: '100',
    render: StatusColumn
  },
  {
    prop: 'createTime',
    label: '创建时间',
    width: '180'
  }
])

const tableActions = ref([
  {
    name: 'edit',
    text: '编辑',
    type: 'primary',
    handler: handleEdit
  },
  {
    name: 'delete',
    text: '删除',
    type: 'danger',
    handler: handleDelete
  }
])

// 弹窗表单配置
const dialogVisible = ref(false)
const dialogTitle = ref('编辑')
const formData = ref({})
const formRules = ref({
  name: [
    { required: true, message: '请输入名称', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
})

const formFields = ref([
  {
    type: 'input',
    prop: 'name',
    label: '名称',
    placeholder: '请输入名称',
    required: true
  },
  {
    type: 'select',
    prop: 'status',
    label: '状态',
    placeholder: '请选择状态',
    required: true,
    options: [
      { label: '启用', value: '1' },
      { label: '禁用', value: '0' }
    ]
  },
  {
    type: 'textarea',
    prop: 'description',
    label: '描述',
    placeholder: '请输入描述'
  }
])

// 状态列组件
const StatusColumn = {
  props: ['row'],
  template: `
    <el-switch
      v-model="status"
      active-value="1"
      inactive-value="0"
      @change="handleChange"
    />
  `,
  computed: {
    status: {
      get() {
        return this.row.status
      },
      set(value) {
        this.$emit('update:row', { ...this.row, status: value })
      }
    }
  },
  methods: {
    handleChange(value) {
      ElMessage.success(`状态已${value === '1' ? '启用' : '禁用'}`)
    }
  }
}

// 模拟数据
const loadData = () => {
  loading.value = true
  // 模拟异步请求
  setTimeout(() => {
    const data = []
    for (let i = 1; i <= 50; i++) {
      data.push({
        id: i,
        name: `项目${i}`,
        status: i % 2 === 0 ? '1' : '0',
        createTime: new Date().toISOString()
      })
    }
    tableData.value = data
    total.value = data.length
    loading.value = false
  }, 1000)
}

// 搜索处理
const handleSearch = (form) => {
  ElMessage.success(`搜索条件: ${JSON.stringify(form)}`)
  // 实际项目中这里会根据搜索条件过滤数据
}

// 重置处理
const handleReset = () => {
  ElMessage.info('重置搜索条件')
}

// 新增处理
const handleAdd = () => {
  dialogTitle.value = '新增'
  formData.value = {
    name: '',
    status: '1',
    description: ''
  }
  dialogVisible.value = true
}

// 编辑处理
const handleEdit = (row) => {
  dialogTitle.value = '编辑'
  formData.value = { ...row }
  dialogVisible.value = true
}

// 删除处理
const handleDelete = (row) => {
  ElMessage.confirm(`确定要删除 ${row.name} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
  })
}

// 选择变更处理
const handleSelectionChange = (val) => {
  selectedRows.value = val
  ElMessage.info(`选择了 ${val.length} 条数据`)
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadData()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  loadData()
}

// 表单提交处理
const handleFormSubmit = (form) => {
  ElMessage.success(`提交数据: ${JSON.stringify(form)}`)
  dialogVisible.value = false
}

// 表单取消处理
const handleFormCancel = () => {
  dialogVisible.value = false
}

// 初始化
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.component-demo {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;

  h2 {
    margin-bottom: 30px;
    color: #303133;
  }

  .demo-section {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    margin-bottom: 30px;

    h3 {
      margin-bottom: 20px;
      color: #409eff;
      font-size: 16px;
    }
  }
}
</style>