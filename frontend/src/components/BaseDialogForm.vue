<template>
  <el-dialog
    v-model="dialogVisible"
    :title="title"
    :width="width"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
  >
    <el-form
      ref="formRef"
      :model="internalFormData"
      :rules="rules"
      label-width="100px"
    >
      <template v-for="(item, index) in fields" :key="index">
        <!-- 输入框 -->
        <el-form-item
          v-if="item.type === 'input'"
          :label="item.label"
          :prop="item.prop"
          :required="item.required"
        >
          <el-input
            v-model="internalFormData[item.prop]"
            :placeholder="item.placeholder"
            :type="item.inputType || 'text'"
            :disabled="item.disabled"
            :maxlength="item.maxlength"
          />
        </el-form-item>

        <!-- 下拉选择 -->
        <el-form-item
          v-else-if="item.type === 'select'"
          :label="item.label"
          :prop="item.prop"
          :required="item.required"
        >
          <el-select
            v-model="internalFormData[item.prop]"
            :placeholder="item.placeholder"
            :disabled="item.disabled"
            :multiple="item.multiple"
          >
            <el-option
              v-for="option in item.options"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>

        <!-- 开关 -->
        <el-form-item
          v-else-if="item.type === 'switch'"
          :label="item.label"
          :prop="item.prop"
        >
          <el-switch
            v-model="internalFormData[item.prop]"
            :active-value="item.activeValue || 1"
            :inactive-value="item.inactiveValue || 0"
            :disabled="item.disabled"
          />
        </el-form-item>

        <!-- 数字输入 -->
        <el-form-item
          v-else-if="item.type === 'number'"
          :label="item.label"
          :prop="item.prop"
          :required="item.required"
        >
          <el-input-number
            v-model="internalFormData[item.prop]"
            :min="item.min"
            :max="item.max"
            :step="item.step || 1"
            :disabled="item.disabled"
          />
        </el-form-item>

        <!-- 日期选择 -->
        <el-form-item
          v-else-if="item.type === 'date'"
          :label="item.label"
          :prop="item.prop"
          :required="item.required"
        >
          <el-date-picker
            v-model="internalFormData[item.prop]"
            :type="item.dateType || 'date'"
            :placeholder="item.placeholder"
            :disabled="item.disabled"
          />
        </el-form-item>

        <!-- 文本域 -->
        <el-form-item
          v-else-if="item.type === 'textarea'"
          :label="item.label"
          :prop="item.prop"
          :required="item.required"
        >
          <el-input
            v-model="internalFormData[item.prop]"
            type="textarea"
            :placeholder="item.placeholder"
            :disabled="item.disabled"
            :rows="item.rows || 3"
            :maxlength="item.maxlength"
          />
        </el-form-item>

        <!-- 自定义组件 -->
        <el-form-item
          v-else-if="item.type === 'custom'"
          :label="item.label"
          :prop="item.prop"
          :required="item.required"
        >
          <component
            :is="item.component"
            v-model="internalFormData[item.prop]"
            v-bind="item.props"
          />
        </el-form-item>
      </template>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch, nextTick } from 'vue'

const props = defineProps({
  // 弹窗是否可见
  visible: {
    type: Boolean,
    default: false
  },
  // 弹窗标题
  title: {
    type: String,
    default: '编辑'
  },
  // 弹窗宽度
  width: {
    type: String,
    default: '500px'
  },
  // 表单字段配置
  fields: {
    type: Array,
    default: () => []
  },
  // 表单数据
  formData: {
    type: Object,
    default: () => {}
  },
  // 表单验证规则
  rules: {
    type: Object,
    default: () => {}
  }
})

const emit = defineEmits([
  'update:visible',
  'submit',
  'cancel'
])

const dialogVisible = ref(props.visible)
const formRef = ref(null)
const internalFormData = ref({ ...props.formData })

// 监听visible变化
watch(() => props.visible, (newVal) => {
  dialogVisible.value = newVal
})

// 监听formData变化
watch(() => props.formData, (newVal) => {
  nextTick(() => {
    internalFormData.value = { ...newVal }
  })
}, { deep: true })

// 监听dialogVisible变化
watch(dialogVisible, (newVal) => {
  emit('update:visible', newVal)
})

// 处理提交
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    emit('submit', { ...internalFormData.value })
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 处理取消
const handleCancel = () => {
  emit('cancel')
  dialogVisible.value = false
}
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
