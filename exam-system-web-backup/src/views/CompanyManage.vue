<template>
  <!-- 企业管理页面主内容区域 -->
  <div class="company-manage">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>企业管理</h2>
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        新增企业
      </el-button>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="search-section">
      <el-form :inline="true" class="search-form">
        <el-form-item label="企业名称">
          <el-input 
            v-model="searchParams.keyword" 
            placeholder="请输入企业名称或行业"
            clearable
            @keyup.enter="searchCompanies">
          </el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchParams.status" placeholder="全部状态" clearable>
            <el-option label="全部" :value="null"></el-option>
            <el-option label="启用" :value="1"></el-option>
            <el-option label="禁用" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchCompanies">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 企业列表表格 -->
    <div class="table-section">
      <el-table 
        :data="companyList" 
        v-loading="loading"
        style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="企业名称" min-width="150"></el-table-column>
        <el-table-column prop="industry" label="所属行业" width="120"></el-table-column>
        <el-table-column prop="size" label="企业规模" width="120"></el-table-column>
        <el-table-column prop="location" label="企业位置" width="120"></el-table-column>
        <el-table-column prop="website" label="官网" width="150" show-overflow-tooltip></el-table-column>
        <el-table-column prop="contactPerson" label="联系人" width="100"></el-table-column>
        <el-table-column prop="contactPhone" label="联系电话" width="120"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewCompany(scope.row)">查看</el-button>
            <el-button size="small" type="primary" @click="editCompany(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteCompany(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :small="false"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange">
        </el-pagination>
      </div>
    </div>

    <!-- 新增/编辑企业对话框 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogVisible" 
      width="600px"
      @close="closeDialog">
      <el-form 
        :model="companyForm" 
        :rules="formRules" 
        ref="companyFormRef" 
        label-width="100px">
        <el-form-item label="企业名称" prop="name">
          <el-input v-model="companyForm.name" placeholder="请输入企业名称"></el-input>
        </el-form-item>
        <el-form-item label="logo图片" prop="logoUrl">
          <el-input v-model="companyForm.logoUrl" placeholder="请输入logo图片URL"></el-input>
        </el-form-item>
        <el-form-item label="企业描述" prop="description">
          <el-input 
            type="textarea" 
            v-model="companyForm.description" 
            placeholder="请输入企业描述"
            :rows="3">
          </el-input>
        </el-form-item>
        <el-form-item label="所属行业" prop="industry">
          <el-input v-model="companyForm.industry" placeholder="请输入所属行业"></el-input>
        </el-form-item>
        <el-form-item label="企业规模" prop="size">
          <el-select v-model="companyForm.size" placeholder="请选择企业规模">
            <el-option label="小型" value="小型"></el-option>
            <el-option label="中型" value="中型"></el-option>
            <el-option label="大型" value="大型"></el-option>
            <el-option label="超大型" value="超大型"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="企业位置" prop="location">
          <el-input v-model="companyForm.location" placeholder="请输入企业位置"></el-input>
        </el-form-item>
        <el-form-item label="企业官网" prop="website">
          <el-input v-model="companyForm.website" placeholder="请输入企业官网URL"></el-input>
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="companyForm.contactPerson" placeholder="请输入联系人"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="companyForm.contactPhone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
        <el-form-item label="联系邮箱" prop="contactEmail">
          <el-input v-model="companyForm.contactEmail" placeholder="请输入联系邮箱"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="companyForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeDialog">取消</el-button>
          <el-button type="primary" @click="saveCompany">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看企业详情对话框 -->
    <el-dialog 
      title="企业详情" 
      v-model="viewDialogVisible" 
      width="600px">
      <div class="company-detail" v-if="viewCompany">
        <div class="detail-row">
          <span class="label">企业名称：</span>
          <span class="value">{{ viewCompanyData.name }}</span>
        </div>
        <div class="detail-row">
          <span class="label">所属行业：</span>
          <span class="value">{{ viewCompanyData.industry }}</span>
        </div>
        <div class="detail-row">
          <span class="label">企业规模：</span>
          <span class="value">{{ viewCompanyData.size }}</span>
        </div>
        <div class="detail-row">
          <span class="label">企业位置：</span>
          <span class="value">{{ viewCompanyData.location }}</span>
        </div>
        <div class="detail-row">
          <span class="label">企业官网：</span>
          <span class="value">
            <a :href="viewCompanyData.website" target="_blank" v-if="viewCompanyData.website">
              {{ viewCompanyData.website }}
            </a>
            <span v-else>-</span>
          </span>
        </div>
        <div class="detail-row">
          <span class="label">联系人：</span>
          <span class="value">{{ viewCompanyData.contactPerson || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">联系电话：</span>
          <span class="value">{{ viewCompanyData.contactPhone || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">联系邮箱：</span>
          <span class="value">{{ viewCompanyData.contactEmail || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">状态：</span>
          <span class="value">
            <el-tag :type="viewCompanyData.status === 1 ? 'success' : 'danger'">
              {{ viewCompanyData.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </span>
        </div>
        <div class="detail-row">
          <span class="label">企业描述：</span>
          <div class="value description">{{ viewCompanyData.description || '-' }}</div>
        </div>
        <div class="detail-row">
          <span class="label">创建时间：</span>
          <span class="value">{{ viewCompanyData.createdTime }}</span>
        </div>
        <div class="detail-row">
          <span class="label">更新时间：</span>
          <span class="value">{{ viewCompanyData.updatedTime }}</span>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'CompanyManage', // 组件名称
  components: {
    Plus,
    Search
  },
  setup() {
    // 响应式数据定义
    const loading = ref(false) // 加载状态
    const companyList = ref([]) // 企业列表数据
    const dialogVisible = ref(false) // 新增/编辑对话框显示状态
    const viewDialogVisible = ref(false) // 查看详情对话框显示状态
    const dialogTitle = ref('') // 对话框标题
    const isEdit = ref(false) // 是否为编辑模式
    const companyFormRef = ref(null) // 表单引用
    const viewCompanyData = ref({}) // 查看的企业数据

    // 搜索参数
    const searchParams = reactive({
      keyword: '', // 搜索关键词
      status: null // 状态筛选
    })

    // 分页参数
    const pagination = reactive({
      current: 1, // 当前页码
      size: 10, // 每页大小
      total: 0 // 总记录数
    })

    // 企业表单数据
    const companyForm = reactive({
      id: null,
      name: '',
      logoUrl: '',
      description: '',
      industry: '',
      size: '',
      location: '',
      website: '',
      contactPerson: '',
      contactPhone: '',
      contactEmail: '',
      status: 1
    })

    // 表单验证规则
    const formRules = {
      name: [
        { required: true, message: '请输入企业名称', trigger: 'blur' },
        { min: 2, max: 100, message: '企业名称长度在 2 到 100 个字符', trigger: 'blur' }
      ],
      industry: [
        { required: true, message: '请输入所属行业', trigger: 'blur' }
      ],
      size: [
        { required: true, message: '请选择企业规模', trigger: 'change' }
      ],
      location: [
        { required: true, message: '请输入企业位置', trigger: 'blur' }
      ],
      contactEmail: [
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
      ]
    }

    // 获取企业列表数据
    const fetchCompanies = async () => {
      loading.value = true
      try {
        const response = await axios.get('/api/companies/list', {
          params: {
            page: pagination.current,
            size: pagination.size,
            keyword: searchParams.keyword || undefined,
            status: searchParams.status
          }
        })
        
        if (response.data.code === 200) {
          companyList.value = response.data.data.records
          pagination.total = response.data.data.total
        } else {
          ElMessage.error('获取企业列表失败：' + response.data.message)
        }
      } catch (error) {
        console.error('获取企业列表失败:', error)
        ElMessage.error('获取企业列表失败，请检查网络连接')
      } finally {
        loading.value = false
      }
    }

    // 搜索企业
    const searchCompanies = () => {
      pagination.current = 1 // 重置到第一页
      fetchCompanies()
    }

    // 重置搜索条件
    const resetSearch = () => {
      searchParams.keyword = ''
      searchParams.status = null
      searchCompanies()
    }

    // 分页大小改变
    const handleSizeChange = (newSize) => {
      pagination.size = newSize
      pagination.current = 1
      fetchCompanies()
    }

    // 当前页改变
    const handleCurrentChange = (newPage) => {
      pagination.current = newPage
      fetchCompanies()
    }

    // 显示新增对话框
    const showAddDialog = () => {
      dialogTitle.value = '新增企业'
      isEdit.value = false
      resetForm()
      dialogVisible.value = true
    }

    // 编辑企业
    const editCompany = (company) => {
      dialogTitle.value = '编辑企业'
      isEdit.value = true
      Object.assign(companyForm, company)
      dialogVisible.value = true
    }

    // 查看企业详情
    const viewCompany = (company) => {
      viewCompanyData.value = { ...company }
      viewDialogVisible.value = true
    }

    // 删除企业
    const deleteCompany = async (company) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除企业 "${company.name}" 吗？`,
          '删除确认',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        )

        const response = await axios.delete(`/api/companies/${company.id}`)
        if (response.data.code === 200) {
          ElMessage.success('删除成功')
          fetchCompanies() // 重新加载列表
        } else {
          ElMessage.error('删除失败：' + response.data.message)
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除企业失败:', error)
          ElMessage.error('删除失败，请检查网络连接')
        }
      }
    }

    // 保存企业
    const saveCompany = async () => {
      try {
        // 表单验证
        await companyFormRef.value.validate()

        const isEditMode = isEdit.value
        const url = isEditMode ? `/api/companies/${companyForm.id}` : '/api/companies'
        const method = isEditMode ? 'put' : 'post'

        const response = await axios[method](url, companyForm)
        
        if (response.data.code === 200) {
          ElMessage.success(isEditMode ? '更新成功' : '新增成功')
          closeDialog()
          fetchCompanies() // 重新加载列表
        } else {
          ElMessage.error(response.data.message)
        }
      } catch (error) {
        if (error.response) {
          ElMessage.error('保存失败：' + error.response.data.message)
        } else {
          console.error('保存企业失败:', error)
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
      Object.assign(companyForm, {
        id: null,
        name: '',
        logoUrl: '',
        description: '',
        industry: '',
        size: '',
        location: '',
        website: '',
        contactPerson: '',
        contactPhone: '',
        contactEmail: '',
        status: 1
      })
      // 清除表单验证状态
      if (companyFormRef.value) {
        companyFormRef.value.clearValidate()
      }
    }

    // 组件挂载时获取数据
    onMounted(() => {
      fetchCompanies()
    })

    return {
      // 响应式数据
      loading,
      companyList,
      dialogVisible,
      viewDialogVisible,
      dialogTitle,
      isEdit,
      companyFormRef,
      searchParams,
      pagination,
      companyForm,
      formRules,
      viewCompanyData,
      
      // 方法
      fetchCompanies,
      searchCompanies,
      resetSearch,
      handleSizeChange,
      handleCurrentChange,
      showAddDialog,
      editCompany,
      viewCompany,
      deleteCompany,
      saveCompany,
      closeDialog,
      resetForm
    }
  }
}
</script>

<style scoped>
/* 页面样式 */
.company-manage {
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

.search-section {
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.search-form {
  margin: 0;
}

.table-section {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.pagination-section {
  padding: 20px;
  display: flex;
  justify-content: center;
  background: white;
}

/* 企业详情样式 */
.company-detail {
  max-height: 500px;
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

.detail-row .value a {
  color: #409eff;
  text-decoration: none;
}

.detail-row .value a:hover {
  text-decoration: underline;
}

/* 对话框底部样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 