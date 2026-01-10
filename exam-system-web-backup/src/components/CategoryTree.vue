<template>
  <div class="category-tree">
    <!-- 分类树组件，支持多级分类 -->
    <el-tree
      :data="treeData"
      :props="defaultProps"
      node-key="id"
      highlight-current
      default-expand-all
      @node-click="handleNodeClick"
      :expand-on-click-node="false"
    >
      <template #default="{ node, data }">
        <span>
          <el-icon v-if="data.icon" :size="16" style="margin-right:4px"><component :is="data.icon" /></el-icon>
          {{ data.name }}
        </span>
      </template>
    </el-tree>
  </div>
</template>

<script setup>
import { ref, watch, defineProps, defineEmits } from 'vue'
import { Document, Edit, List } from '@element-plus/icons-vue'

// 定义props，支持外部传入分类数据
const props = defineProps({
  categories: {
    type: Array,
    default: () => []
  }
})

// 定义emits，点击分类时通知父组件
const emit = defineEmits(['select'])

// 默认树结构属性
const defaultProps = {
  children: 'children',
  label: 'name'
}

// 分类树数据，支持多级
const treeData = ref([])

// 监听外部传入的categories，动态更新树数据
watch(() => props.categories, (val) => {
  treeData.value = val
}, { immediate: true })

// 节点点击事件
const handleNodeClick = (data) => {
  emit('select', data)
}
</script>

<style scoped>
.category-tree {
  padding: 10px 0;
}
</style> 