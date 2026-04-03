<template>
  <div class="base-search">
    <template v-for="(item, index) in items" :key="index">
      <!-- 输入框 -->
      <el-input
        v-if="item.type === 'input'"
        v-model="form[item.prop]"
        :placeholder="item.placeholder"
        :style="item.style"
        :clearable="item.clearable !== false"
      >
        <template #prefix v-if="item.prefixIcon">
          <el-icon><component :is="item.prefixIcon" /></el-icon>
        </template>
      </el-input>

      <!-- 下拉选择 -->
      <el-select
        v-else-if="item.type === 'select'"
        v-model="form[item.prop]"
        :placeholder="item.placeholder"
        :style="item.style"
        :clearable="item.clearable !== false"
      >
        <el-option
          v-for="option in item.options"
          :key="option.value"
          :label="option.label"
          :value="option.value"
        />
      </el-select>

      <!-- 日期选择 -->
      <el-date-picker
        v-else-if="item.type === 'date'"
        v-model="form[item.prop]"
        :type="item.dateType || 'date'"
        :placeholder="item.placeholder"
        :style="item.style"
        :range-separator="item.rangeSeparator || '至'"
        :start-placeholder="item.startPlaceholder"
        :end-placeholder="item.endPlaceholder"
      />

      <!-- 自定义组件 -->
      <component
        v-else-if="item.type === 'custom'"
        :is="item.component"
        v-model="form[item.prop]"
        v-bind="item.props"
      />
    </template>

    <!-- 搜索按钮 -->
    <el-button
      type="primary"
      @click="handleSearch"
      style="margin-left: 10px"
    >
      搜索
    </el-button>

    <!-- 重置按钮 -->
    <el-button
      v-if="showReset"
      @click="handleReset"
      style="margin-left: 10px"
    >
      重置
    </el-button>

    <!-- 新增按钮 -->
    <el-button
      v-if="showAdd"
      type="primary"
      @click="handleAdd"
      style="margin-left: 10px"
    >
      新增
    </el-button>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch } from 'vue'

const props = defineProps({
  // 搜索项配置
  items: {
    type: Array,
    default: () => []
  },
  // 初始表单数据
  initialForm: {
    type: Object,
    default: () => {}
  },
  // 是否显示重置按钮
  showReset: {
    type: Boolean,
    default: true
  },
  // 是否显示新增按钮
  showAdd: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits([
  'search',
  'reset',
  'add'
])

// 表单数据
const form = ref({ ...props.initialForm })

// 监听初始表单变化
watch(() => props.initialForm, (newVal) => {
  form.value = { ...newVal }
}, { deep: true })

// 处理搜索
const handleSearch = () => {
  emit('search', { ...form.value })
}

// 处理重置
const handleReset = () => {
  form.value = { ...props.initialForm }
  emit('reset')
}

// 处理新增
const handleAdd = () => {
  emit('add')
}
</script>

<style scoped>
.base-search {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;

  :deep(.el-input),
  :deep(.el-select),
  :deep(.el-date-picker) {
    margin-right: 10px;
  }
}
</style>