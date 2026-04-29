<template>
  <div class="base-table">
    <!-- 表格 -->
    <el-table
      v-loading="loading"
      :data="data"
      style="width: 100%"
      @selection-change="handleSelectionChange"
      v-bind="tableProps"
    >
      <!-- 选择列 -->
      <el-table-column
        v-if="showSelection"
        type="selection"
        width="55"
      />

      <!-- 自定义列 -->
      <template v-for="column in columns" :key="column.prop || column.label">
        <el-table-column
          v-if="!column.render"
          :prop="column.prop"
          :label="column.label"
          :width="column.width"
          :fixed="column.fixed"
          :align="column.align"
        />
        <el-table-column
          v-else
          :label="column.label"
          :width="column.width"
          :fixed="column.fixed"
          :align="column.align"
        >
          <template #default="scope">
            <component
              :is="column.render"
              :row="scope.row"
              :index="scope.$index"
              :column="column"
              v-bind="column.props"
            />
          </template>
        </el-table-column>
      </template>

      <!-- 操作列 -->
      <el-table-column
        v-if="actions && actions.length > 0"
        label="操作"
        :width="actionsWidth"
      >
        <template #default="scope">
          <el-button
            v-for="action in actions"
            :key="action.name"
            :size="action.size || 'small'"
            :type="action.type"
            :disabled="action.disabled && action.disabled(scope.row)"
            @click="action.handler(scope.row)"
          >
            {{ action.text }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination" v-if="showPagination">
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :page-sizes="pageSizes"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits } from 'vue'

const props = defineProps({
  // 表格数据
  data: {
    type: Array,
    default: () => []
  },
  // 表格列配置
  columns: {
    type: Array,
    default: () => []
  },
  // 加载状态
  loading: {
    type: Boolean,
    default: false
  },
  // 是否显示选择列
  showSelection: {
    type: Boolean,
    default: false
  },
  // 是否显示分页
  showPagination: {
    type: Boolean,
    default: true
  },
  // 总条数
  total: {
    type: Number,
    default: 0
  },
  // 当前页码
  currentPage: {
    type: Number,
    default: 1
  },
  // 每页大小
  pageSize: {
    type: Number,
    default: 10
  },
  // 分页大小选项
  pageSizes: {
    type: Array,
    default: () => [10, 20, 50, 100]
  },
  // 操作按钮配置
  actions: {
    type: Array,
    default: () => []
  },
  // 操作列宽度
  actionsWidth: {
    type: String,
    default: '180'
  },
  // 表格其他属性
  tableProps: {
    type: Object,
    default: () => {}
  }
})

const emit = defineEmits([
  'selection-change',
  'size-change',
  'current-change'
])

// 处理选择变更
const handleSelectionChange = (val) => {
  emit('selection-change', val)
}

// 处理分页大小变更
const handleSizeChange = (size) => {
  emit('size-change', size)
}

// 处理当前页码变更
const handleCurrentChange = (current) => {
  emit('current-change', current)
}
</script>

<style scoped>
.base-table {
  .pagination {
    margin-top: 20px;
    text-align: right;
  }
}
</style>
