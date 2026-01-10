<template>
  <div class="category-manage-container">
    <div class="tips-section">
      <el-alert
        title="操作提示"
        type="info"
        description="管理考试题目分类，支持两级分类。删除分类前请确保该分类下没有子分类和考试题目。"
        show-icon
        :closable="false">
      </el-alert>
    </div>

    <div class="tree-section">
      <el-table 
        :data="categoryTree"
        v-loading="loading"
        style="width: 100%"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="分类名称" min-width="200"></el-table-column>
        <el-table-column label="级别" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.parentId === 0 ? 'primary' : 'success'">
              {{ scope.row.parentId === 0 ? '一级' : '二级' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="100"></el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewCategory(scope.row)">查看</el-button>
            <el-button size="small" type="primary" @click="editCategory(scope.row)">编辑</el-button>
            <el-button 
              v-if="scope.row.parentId === 0 && (scope.row.name === '选择题' || scope.row.name === '判断题' || scope.row.name === '简答题')"
              size="small" 
              type="success" 
              @click="addSubCategory(scope.row)">添加子分类</el-button>
            <el-button size="small" type="danger" @click="deleteCategory(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" @close="closeDialog">
      <el-form :model="categoryForm" :rules="formRules" ref="categoryFormRef" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称"></el-input>
        </el-form-item>
        <el-form-item v-if="isAddSubCategory" label="父分类" prop="parentId">
          <el-select v-model="categoryForm.parentId" disabled style="width: 100%">
            <el-option :label="getParentCategoryName(categoryForm.parentId)" :value="categoryForm.parentId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序序号" prop="sort">
          <el-input-number v-model="categoryForm.sort" :min="0" :max="999" placeholder="排序序号"></el-input-number>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeDialog">取消</el-button>
          <el-button type="primary" @click="saveCategory">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog title="分类详情" v-model="viewDialogVisible" width="500px">
      <div class="category-detail" v-if="viewCategoryData">
        <div class="detail-row"><span class="label">分类名称：</span><span class="value">{{ viewCategoryData.name }}</span></div>
        <div class="detail-row"><span class="label">分类级别：</span><span class="value"><el-tag :type="viewCategoryData.level === 1 ? 'primary' : 'success'">{{ viewCategoryData.level === 1 ? '一级分类' : '二级分类' }}</el-tag></span></div>
        <div class="detail-row" v-if="viewCategoryData.level === 2"><span class="label">父分类：</span><span class="value">{{ getParentCategoryName(viewCategoryData.parentId) }}</span></div>
        <div class="detail-row"><span class="label">排序序号：</span><span class="value">{{ viewCategoryData.sort }}</span></div>
        <div class="detail-row"><span class="label">分类描述：</span><div class="value description">{{ viewCategoryData.description || '-' }}</div></div>
        <div class="detail-row"><span class="label">创建时间：</span><span class="value">{{ viewCategoryData.createTime }}</span></div>
        <div class="detail-row"><span class="label">更新时间：</span><span class="value">{{ viewCategoryData.updateTime }}</span></div>
      </div>
      <template #footer>
        <span class="dialog-footer"><el-button @click="viewDialogVisible = false">关闭</el-button></span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const loading = ref(false)
const categoryTree = ref([])
const allCategories = ref([])
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const categoryFormRef = ref(null)
const viewCategoryData = ref(null)
const isAddSubCategory = ref(false)

const categoryForm = reactive({ id: null, name: '', parentId: null, level: 1, sort: 0, description: '' })

const formRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }, { min: 2, max: 50, message: '分类名称长度在 2 到 50 个字符', trigger: 'blur' }],
  sort: [{ required: true, message: '请输入排序序号', trigger: 'blur' }, { type: 'number', min: 0, max: 999, message: '排序序号范围为 0-999', trigger: 'blur' }]
}

const fetchCategories = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/categories/tree')
    categoryTree.value = response.data
    // 后台没有 /api/categories/enabled 接口，因此注释掉该调用
    // await fetchAllCategories()
    // 转而使用本地函数从树形结构生成扁平列表，以支持“父分类名称”的显示
    allCategories.value = flattenTree(response.data || [])
  } catch (error) {
    ElMessage.error('获取分类列表失败')
  } finally {
    loading.value = false
  }
}

// 从树形数据递归生成扁平列表
const flattenTree = (nodes) => {
  const list = [];
  if (!nodes) return list;
  for (const node of nodes) {
    const { children, ...rest } = node;
    list.push(rest);
    if (children && children.length > 0) {
      list.push(...flattenTree(children));
    }
  }
  return list;
}

// 不再需要此函数
// const fetchAllCategories = async () => {
//   try {
//     const response = await request.get('/api/categories/enabled')
//     allCategories.value = response.data
//   } catch (error) {
//     console.error('获取所有分类失败:', error)
//   }
// }

const getParentCategoryName = (parentId) => {
  if (!parentId) return '-'
  const parent = allCategories.value.find(cat => cat.id === parentId)
  return parent ? parent.name : '-'
}

const addSubCategory = (parentCategory) => {
  dialogTitle.value = `新增子分类（父分类：${parentCategory.name}）`
  isEdit.value = false
  isAddSubCategory.value = true
  resetForm()
  categoryForm.parentId = parentCategory.id
  categoryForm.level = 2
  dialogVisible.value = true
}

const editCategory = (category) => {
  dialogTitle.value = '编辑分类'
  isEdit.value = true
  Object.assign(categoryForm, category)
  dialogVisible.value = true
}

const viewCategory = (category) => {
  viewCategoryData.value = { ...category }
  viewDialogVisible.value = true
}

const deleteCategory = async (category) => {
  try {
    await ElMessageBox.confirm(`确定要删除分类 "${category.name}" 吗？`, '删除确认', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await request.delete(`/api/categories/${category.id}`)
    ElMessage.success('删除成功')
    fetchCategories()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

const saveCategory = async () => {
  try {
    await categoryFormRef.value.validate()
    categoryForm.level = categoryForm.parentId ? 2 : 1
    const url = isEdit.value ? `/api/categories` : '/api/categories'
    const method = isEdit.value ? 'put' : 'post'
    await request[method](url, categoryForm)
    ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
    closeDialog()
    fetchCategories()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const closeDialog = () => {
  dialogVisible.value = false
  resetForm()
}

const resetForm = () => {
  Object.assign(categoryForm, { id: null, name: '', parentId: null, level: 1, sort: 0, description: '' })
  if (categoryFormRef.value) categoryFormRef.value.clearValidate()
}

onMounted(() => {
  fetchCategories()
})

</script>

<style scoped>
.category-manage-container { padding: 20px; }
.tips-section { margin-bottom: 20px; }
.tree-section { background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.category-detail { max-height: 400px; overflow-y: auto; }
.detail-row { display: flex; margin-bottom: 15px; align-items: flex-start; }
.detail-row .label { width: 100px; font-weight: bold; color: #606266; flex-shrink: 0; }
.detail-row .value { flex: 1; color: #303133; }
.detail-row .value.description { white-space: pre-wrap; word-break: break-word; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; }
</style>