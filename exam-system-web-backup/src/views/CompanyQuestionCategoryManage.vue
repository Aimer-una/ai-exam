<template>
  <!-- 面试真题分类管理页面主内容区域 -->
  <div class="category-manage">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>面试真题分类管理</h2>
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        新增分类
      </el-button>
    </div>

    <!-- 操作提示 -->
    <div class="tips-section">
      <el-alert
        title="操作提示"
        type="info"
        description="支持两级分类管理，一级分类为主分类，二级分类为子分类。删除分类前请确保该分类下没有子分类和面试真题。"
        show-icon
        :closable="false">
      </el-alert>
    </div>

    <!-- 分类树形列表 -->
    <div class="tree-section">
      <el-table 
        :data="categoryTree" 
        v-loading="loading"
        style="width: 100%"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="分类名称" min-width="200"></el-table-column>
        <el-table-column prop="level" label="级别" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.level === 1 ? 'primary' : 'success'">
              {{ scope.row.level === 1 ? '一级' : '二级' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="100"></el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewCategory(scope.row)">查看</el-button>
            <el-button 
              size="small" 
              type="primary" 
              @click="editCategory(scope.row)">编辑</el-button>
            <el-button 
              size="small" 
              type="success" 
              v-if="scope.row.level === 1"
              @click="addSubCategory(scope.row)">添加子分类</el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="deleteCategory(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑分类对话框 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogVisible" 
      width="500px"
      @close="closeDialog">
      <el-form 
        :model="categoryForm" 
        :rules="formRules" 
        ref="categoryFormRef" 
        label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称"></el-input>
        </el-form-item>
        <el-form-item label="父分类" prop="parentId">
          <el-select 
            v-model="categoryForm.parentId" 
            placeholder="请选择父分类（不选为一级分类）"
            clearable
            style="width: 100%">
            <el-option 
              v-for="category in parentCategoryOptions" 
              :key="category.id" 
              :label="category.name" 
              :value="category.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序序号" prop="sortOrder">
          <el-input-number 
            v-model="categoryForm.sortOrder" 
            :min="0" 
            :max="999" 
            placeholder="排序序号">
          </el-input-number>
        </el-form-item>
        <el-form-item label="分类描述" prop="description">
          <el-input 
            type="textarea" 
            v-model="categoryForm.description" 
            placeholder="请输入分类描述"
            :rows="3">
          </el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="categoryForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeDialog">取消</el-button>
          <el-button type="primary" @click="saveCategory">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看分类详情对话框 -->
    <el-dialog 
      title="分类详情" 
      v-model="viewDialogVisible" 
      width="500px">
      <div class="category-detail" v-if="viewCategoryData">
        <div class="detail-row">
          <span class="label">分类名称：</span>
          <span class="value">{{ viewCategoryData.name }}</span>
        </div>
        <div class="detail-row">
          <span class="label">分类级别：</span>
          <span class="value">
            <el-tag :type="viewCategoryData.level === 1 ? 'primary' : 'success'">
              {{ viewCategoryData.level === 1 ? '一级分类' : '二级分类' }}
            </el-tag>
          </span>
        </div>
        <div class="detail-row" v-if="viewCategoryData.level === 2">
          <span class="label">父分类：</span>
          <span class="value">{{ getParentCategoryName(viewCategoryData.parentId) }}</span>
        </div>
        <div class="detail-row">
          <span class="label">排序序号：</span>
          <span class="value">{{ viewCategoryData.sortOrder }}</span>
        </div>
        <div class="detail-row">
          <span class="label">状态：</span>
          <span class="value">
            <el-tag :type="viewCategoryData.status === 1 ? 'success' : 'danger'">
              {{ viewCategoryData.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </span>
        </div>
        <div class="detail-row">
          <span class="label">分类描述：</span>
          <div class="value description">{{ viewCategoryData.description || '-' }}</div>
        </div>
        <div class="detail-row">
          <span class="label">创建时间：</span>
          <span class="value">{{ viewCategoryData.createdTime }}</span>
        </div>
        <div class="detail-row">
          <span class="label">更新时间：</span>
          <span class="value">{{ viewCategoryData.updatedTime }}</span>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="viewDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'CompanyQuestionCategoryManage', // 组件名称
  components: {
    Plus
  },
  setup() {
    // 响应式数据定义
    const loading = ref(false) // 加载状态
    const categoryTree = ref([]) // 分类树数据
    const allCategories = ref([]) // 所有分类数据（用于查找父分类名称）
    const dialogVisible = ref(false) // 新增/编辑对话框显示状态
    const viewDialogVisible = ref(false) // 查看详情对话框显示状态
    const dialogTitle = ref('') // 对话框标题
    const isEdit = ref(false) // 是否为编辑模式
    const categoryFormRef = ref(null) // 表单引用
    const viewCategoryData = ref(null) // 查看的分类数据

    // 分类表单数据
    const categoryForm = reactive({
      id: null,
      name: '',
      parentId: null,
      level: 1,
      sortOrder: 0,
      description: '',
      status: 1
    })

    // 表单验证规则
    const formRules = {
      name: [
        { required: true, message: '请输入分类名称', trigger: 'blur' },
        { min: 2, max: 50, message: '分类名称长度在 2 到 50 个字符', trigger: 'blur' }
      ],
      sortOrder: [
        { required: true, message: '请输入排序序号', trigger: 'blur' },
        { type: 'number', min: 0, max: 999, message: '排序序号范围为 0-999', trigger: 'blur' }
      ]
    }

    // 父分类选项（只显示一级分类）
    const parentCategoryOptions = computed(() => {
      return allCategories.value.filter(cat => cat.level === 1)
    })

    // 获取分类树数据
    const fetchCategories = async () => {
      loading.value = true
      try {
        const response = await axios.get('/api/company-question-categories/tree')
        
        if (response.data.code === 200) {
          categoryTree.value = response.data.data
          // 同时获取所有分类的平铺数据
          await fetchAllCategories()
        } else {
          ElMessage.error('获取分类列表失败：' + response.data.message)
        }
      } catch (error) {
        console.error('获取分类列表失败:', error)
        ElMessage.error('获取分类列表失败，请检查网络连接')
      } finally {
        loading.value = false
      }
    }

    // 获取所有分类的平铺数据
    const fetchAllCategories = async () => {
      try {
        const response = await axios.get('/api/company-question-categories/enabled')
        if (response.data.code === 200) {
          allCategories.value = response.data.data
        }
      } catch (error) {
        console.error('获取所有分类失败:', error)
      }
    }

    // 根据父分类ID获取父分类名称
    const getParentCategoryName = (parentId) => {
      if (!parentId) return '-'
      const parent = allCategories.value.find(cat => cat.id === parentId)
      return parent ? parent.name : '-'
    }

    // 显示新增对话框
    const showAddDialog = () => {
      dialogTitle.value = '新增分类'
      isEdit.value = false
      resetForm()
      dialogVisible.value = true
    }

    // 添加子分类
    const addSubCategory = (parentCategory) => {
      dialogTitle.value = `新增子分类（父分类：${parentCategory.name}）`
      isEdit.value = false
      resetForm()
      categoryForm.parentId = parentCategory.id
      categoryForm.level = 2
      dialogVisible.value = true
    }

    // 编辑分类
    const editCategory = (category) => {
      dialogTitle.value = '编辑分类'
      isEdit.value = true
      Object.assign(categoryForm, category)
      dialogVisible.value = true
    }

    // 查看分类详情
    const viewCategory = (category) => {
      viewCategoryData.value = { ...category }
      viewDialogVisible.value = true
    }

    // 删除分类
    const deleteCategory = async (category) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除分类 "${category.name}" 吗？`,
          '删除确认',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        )

        const response = await axios.delete(`/api/company-question-categories/${category.id}`)
        if (response.data.code === 200) {
          ElMessage.success('删除成功')
          fetchCategories() // 重新加载列表
        } else {
          ElMessage.error('删除失败：' + response.data.message)
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除分类失败:', error)
          ElMessage.error('删除失败，请检查网络连接')
        }
      }
    }

    // 保存分类
    const saveCategory = async () => {
      try {
        // 表单验证
        await categoryFormRef.value.validate()

        // 根据parentId设置level
        categoryForm.level = categoryForm.parentId ? 2 : 1

        const isEditMode = isEdit.value
        const url = isEditMode ? `/api/company-question-categories/${categoryForm.id}` : '/api/company-question-categories'
        const method = isEditMode ? 'put' : 'post'

        const response = await axios[method](url, categoryForm)
        
        if (response.data.code === 200) {
          ElMessage.success(isEditMode ? '更新成功' : '新增成功')
          closeDialog()
          fetchCategories() // 重新加载列表
        } else {
          ElMessage.error(response.data.message)
        }
      } catch (error) {
        if (error.response) {
          ElMessage.error('保存失败：' + error.response.data.message)
        } else {
          console.error('保存分类失败:', error)
          ElMessage.error('保存失败，请检查网络连接')
        }
      }
    }

    // 关闭对话框
    const closeDialog = () => {
      dialogVisible.value = false
      resetForm()
    }

    // 重置表单
    const resetForm = () => {
      Object.assign(categoryForm, {
        id: null,
        name: '',
        parentId: null,
        level: 1,
        sortOrder: 0,
        description: '',
        status: 1
      })
      // 清除表单验证状态
      if (categoryFormRef.value) {
        categoryFormRef.value.clearValidate()
      }
    }

    // 组件挂载时获取数据
    onMounted(() => {
      fetchCategories()
    })

    return {
      // 响应式数据
      loading,
      categoryTree,
      allCategories,
      dialogVisible,
      viewDialogVisible,
      dialogTitle,
      isEdit,
      categoryFormRef,
      categoryForm,
      formRules,
      viewCategoryData,
      parentCategoryOptions,
      
      // 方法
      fetchCategories,
      getParentCategoryName,
      showAddDialog,
      addSubCategory,
      editCategory,
      viewCategory,
      deleteCategory,
      saveCategory,
      closeDialog,
      resetForm
    }
  }
}
</script>

<style scoped>
/* 页面样式 */
.category-manage {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.tips-section {
  margin-bottom: 20px;
}

.tree-section {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

/* 分类详情样式 */
.category-detail {
  max-height: 400px;
  overflow-y: auto;
}

.detail-row {
  display: flex;
  margin-bottom: 15px;
  align-items: flex-start;
}

.detail-row .label {
  width: 100px;
  font-weight: bold;
  color: #606266;
  flex-shrink: 0;
}

.detail-row .value {
  flex: 1;
  color: #303133;
}

.detail-row .value.description {
  white-space: pre-wrap;
  word-break: break-word;
}

/* 对话框底部样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 