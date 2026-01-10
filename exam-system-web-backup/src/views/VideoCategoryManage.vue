<template>
  <div class="video-category-manage-container">
    <div class="action-bar">
      <el-button type="primary" @click="showAddDialog" icon="Plus">添加分类</el-button>
      <el-button @click="getCategoryList" icon="Refresh">刷新列表</el-button>
      <el-button @click="expandAll" icon="Expand">展开全部</el-button>
      <el-button @click="collapseAll" icon="Fold">折叠全部</el-button>
    </div>

    <div class="category-list">
      <el-table :data="categoryList" v-loading="loading" stripe row-key="id" :tree-props="{ children: 'children', hasChildren: 'hasChildren' }" :default-expand-all="false" ref="categoryTableRef">
        <el-table-column prop="name" label="分类名称" min-width="200">
          <template #default="scope">
            <span class="category-name">{{ scope.row.name }}</span>
            <el-tag v-if="scope.row.level" size="small" type="info" style="margin-left: 8px;">{{ scope.row.level }}级</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="250" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">{{ scope.row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="videoCount" label="视频数" width="100">
          <template #default="scope">
            <el-tag type="info">{{ scope.row.videoCount || 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="addChildCategory(scope.row)" icon="Plus" v-if="!scope.row.parentId || scope.row.level < 3">添加子分类</el-button>
            <el-button size="small" @click="editCategory(scope.row)" icon="Edit">编辑</el-button>
            <el-button size="small" :type="scope.row.status === 1 ? 'warning' : 'success'" @click="toggleCategoryStatus(scope.row)" icon="Switch">{{ scope.row.status === 1 ? '禁用' : '启用' }}</el-button>
            <el-button size="small" type="danger" @click="deleteCategory(scope.row)" icon="Delete" :disabled="scope.row.videoCount > 0 || (scope.row.children && scope.row.children.length > 0)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" :close-on-click-modal="false">
      <el-form :model="categoryForm" :rules="categoryRules" ref="categoryFormRef" label-width="100px">
        <el-form-item label="分类名称" prop="name"><el-input v-model="categoryForm.name" placeholder="请输入分类名称" /></el-form-item>
        <el-form-item label="分类描述" prop="description"><el-input v-model="categoryForm.description" type="textarea" :rows="3" placeholder="请输入分类描述" /></el-form-item>
        <el-form-item label="父级分类" prop="parentId" v-if="!isAddChild">
          <el-tree-select v-model="categoryForm.parentId" :data="categoryTreeOptions" :props="{ label: 'name', value: 'id', children: 'children' }" placeholder="请选择父级分类（不选则为顶级分类）" clearable check-strictly :render-after-expand="false" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder"><el-input-number v-model="categoryForm.sortOrder" :min="0" :max="999" /></el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="categoryForm.status"><el-radio :label="1">启用</el-radio><el-radio :label="0">禁用</el-radio></el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Edit, Delete, Switch, Expand, Fold } from '@element-plus/icons-vue'
import { getVideoCategories, getCategoryTree, addVideoCategory, updateVideoCategory, deleteVideoCategory } from '../api/videoCategory'

const categoryList = ref([])
const loading = ref(false)
const categoryTableRef = ref()
const dialogVisible = ref(false)
const submitting = ref(false)
const categoryFormRef = ref()
const isEdit = ref(false)
const isAddChild = ref(false)
const parentCategory = ref(null)

const dialogTitle = computed(() => {
  if (isAddChild.value) return `添加子分类 - ${parentCategory.value?.name}`
  return isEdit.value ? '编辑分类' : '添加分类'
})

const categoryForm = reactive({ id: null, name: '', description: '', parentId: null, sortOrder: 0, status: 1 })
const categoryRules = { name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }], sortOrder: [{ required: true, message: '请输入排序值', trigger: 'blur' }] }
const categoryTreeOptions = ref([])

const getCategoryList = async () => {
  loading.value = true
  try {
    const res = await getCategoryTree()
    categoryList.value = res.data
    const flatRes = await getVideoCategories()
    categoryTreeOptions.value = buildTreeOptions(flatRes.data)
  } catch (error) {
    console.error('获取分类列表失败：', error)
  } finally {
    loading.value = false
  }
}

const buildTreeOptions = (categories) => {
  const map = {}
  const roots = []
  categories.forEach(c => { map[c.id] = { ...c, children: [] } })
  categories.forEach(c => {
    if (c.parentId && map[c.parentId]) map[c.parentId].children.push(map[c.id])
    else roots.push(map[c.id])
  })
  return roots
}

const showAddDialog = () => { isEdit.value = false; isAddChild.value = false; parentCategory.value = null; resetForm(); dialogVisible.value = true; }
const addChildCategory = (parent) => { isEdit.value = false; isAddChild.value = true; parentCategory.value = parent; resetForm(); categoryForm.parentId = parent.id; dialogVisible.value = true; }
const editCategory = (category) => { isEdit.value = true; isAddChild.value = false; parentCategory.value = null; Object.assign(categoryForm, { ...category, parentId: category.parentId || null }); dialogVisible.value = true; }

const resetForm = () => {
  Object.assign(categoryForm, { id: null, name: '', description: '', parentId: null, sortOrder: 0, status: 1 })
  if (categoryFormRef.value) categoryFormRef.value.clearValidate()
}

const handleSubmit = async () => {
  if (!categoryFormRef.value) return
  await categoryFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (isEdit.value) await updateVideoCategory(categoryForm)
        else await addVideoCategory(categoryForm)
        ElMessage.success(isEdit.value ? '分类更新成功' : '分类添加成功')
        dialogVisible.value = false
        getCategoryList()
      } catch (error) {
        console.error('操作失败：', error)
      } finally {
        submitting.value = false
      }
    }
  })
}

const toggleCategoryStatus = async (category) => {
  try {
    const newStatus = category.status === 1 ? 0 : 1
    await updateVideoCategory({ id: category.id, status: newStatus })
    ElMessage.success(newStatus === 1 ? '分类已启用' : '分类已禁用')
    getCategoryList()
  } catch (error) {
    console.error('状态切换失败：', error)
  }
}

const deleteCategory = async (category) => {
  if (category.videoCount > 0 || (category.children && category.children.length > 0)) {
    return ElMessage.warning('该分类下有视频或子分类，无法删除')
  }
  try {
    await ElMessageBox.confirm(`确定要删除分类"${category.name}"吗？`, '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await deleteVideoCategory(category.id)
    ElMessage.success('分类删除成功')
    getCategoryList()
  } catch (error) {
    if (error !== 'cancel') console.error('删除失败：', error)
  }
}

const expandAll = () => {
  if (!categoryTableRef.value) return
  const expandNode = (data) => {
    data.forEach(item => {
      categoryTableRef.value.toggleRowExpansion(item, true)
      if (item.children && item.children.length > 0) expandNode(item.children)
    })
  }
  expandNode(categoryList.value)
}

const collapseAll = () => {
  if (!categoryTableRef.value) return
  const collapseNode = (data) => {
    data.forEach(item => {
      categoryTableRef.value.toggleRowExpansion(item, false)
      if (item.children && item.children.length > 0) collapseNode(item.children)
    })
  }
  collapseNode(categoryList.value)
}

onMounted(() => getCategoryList())

</script>

<style scoped>
.video-category-manage-container { padding: 20px; }
.action-bar { margin-bottom: 20px; padding: 20px; background: #f8f9fa; border-radius: 8px; }
.category-list { background: white; border-radius: 8px; padding: 20px; }
.category-name { font-weight: 500; color: #303133; }
</style>