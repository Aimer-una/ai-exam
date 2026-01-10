<template>
  <!-- 面试真题管理页面主内容区域 -->
  <div class="interview-question-manage">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>面试真题管理</h2>
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        新增真题
      </el-button>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="search-section">
      <el-form :inline="true" class="search-form">
        <!-- 企业下拉框，宽度加宽到180px -->
        <el-select v-model="searchParams.companyId" placeholder="企业" style="width: 180px; margin-right: 16px;" clearable>
            <el-option 
              v-for="company in companyOptions" 
              :key="company.id" 
              :label="company.name" 
              :value="company.id">
            </el-option>
          </el-select>
        <!-- 分类下拉框，宽度加宽到180px -->
        <el-select v-model="searchParams.categoryId" placeholder="分类" style="width: 180px; margin-right: 16px;" clearable>
            <el-option 
              v-for="category in categoryOptions" 
              :key="category.id" 
              :label="category.name" 
              :value="category.id">
            </el-option>
          </el-select>
        <!-- 难度下拉框，宽度加宽到180px -->
        <el-select v-model="searchParams.difficulty" placeholder="难度" style="width: 180px; margin-right: 16px;" clearable>
            <el-option label="简单" :value="1"></el-option>
            <el-option label="中等" :value="2"></el-option>
            <el-option label="困难" :value="3"></el-option>
          </el-select>
        <!-- 关键词输入框，宽度加宽到240px -->
        <el-input 
            v-model="searchParams.keyword" 
            placeholder="请输入题目标题或内容"
            style="width: 240px; margin-right: 16px;"
            clearable
            @keyup.enter="searchQuestions">
          </el-input>
        <el-form-item>
          <el-button type="primary" @click="searchQuestions">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 真题列表表格 -->
    <div class="table-section">
      <el-table 
        :data="questionList" 
        v-loading="loading"
        style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="title" label="题目标题" min-width="200"></el-table-column>
        <el-table-column prop="companyName" label="企业" width="120"></el-table-column>
        <el-table-column prop="categoryName" label="分类" width="120"></el-table-column>
        <el-table-column prop="questionType" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="getQuestionTypeColor(scope.row.questionType)">
              {{ getQuestionTypeText(scope.row.questionType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="difficulty" label="难度" width="100">
          <template #default="scope">
            <el-tag :type="getDifficultyColor(scope.row.difficulty)">
              {{ getDifficultyText(scope.row.difficulty) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="查看次数" width="100"></el-table-column>
        <el-table-column prop="likeCount" label="点赞数" width="100"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewQuestion(scope.row)">查看</el-button>
            <el-button size="small" type="primary" @click="editQuestion(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteQuestion(scope.row)">删除</el-button>
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

    <!-- 新增/编辑真题对话框 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogVisible" 
      width="800px"
      @close="closeDialog">
      <el-form 
        :model="questionForm" 
        :rules="formRules" 
        ref="questionFormRef" 
        label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="企业" prop="companyId">
              <el-select v-model="questionForm.companyId" placeholder="请选择企业" style="width: 100%">
                <el-option 
                  v-for="company in companyOptions" 
                  :key="company.id" 
                  :label="company.name" 
                  :value="company.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryId">
              <el-select v-model="questionForm.categoryId" placeholder="请选择分类" style="width: 100%">
                <el-option 
                  v-for="category in categoryOptions" 
                  :key="category.id" 
                  :label="category.name" 
                  :value="category.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="题目标题" prop="title">
          <el-input v-model="questionForm.title" placeholder="请输入题目标题"></el-input>
        </el-form-item>
        <el-form-item label="题目内容" prop="content">
          <el-input 
            type="textarea" 
            v-model="questionForm.content" 
            placeholder="请输入题目内容"
            :rows="5">
          </el-input>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="8">
            <!-- 去掉新增/编辑弹窗中的题目类型字段 -->
            <!--
            <el-form-item label="题目类型" prop="questionType">
              <el-select v-model="questionForm.questionType" placeholder="请选择类型" style="width: 100%">
                <el-option label="笔试题" :value="1"></el-option>
                <el-option label="面试题" :value="2"></el-option>
                <el-option label="编程题" :value="3"></el-option>
              </el-select>
            </el-form-item>
            -->
          </el-col>
          <el-col :span="8">
            <el-form-item label="难度" prop="difficulty">
              <el-select v-model="questionForm.difficulty" placeholder="请选择难度" style="width: 100%">
                <el-option label="简单" :value="1"></el-option>
                <el-option label="中等" :value="2"></el-option>
                <el-option label="困难" :value="3"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="questionForm.status">
                <el-radio :label="1">上架</el-radio>
                <el-radio :label="0">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="参考答案" prop="answer">
          <el-input 
            type="textarea" 
            v-model="questionForm.answer" 
            placeholder="请输入参考答案"
            :rows="3">
          </el-input>
        </el-form-item>
        <el-form-item label="题目解析" prop="analysis">
          <el-input 
            type="textarea" 
            v-model="questionForm.analysis" 
            placeholder="请输入题目解析"
            :rows="3">
          </el-input>
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-input v-model="questionForm.tags" placeholder="请输入标签，多个标签用逗号分隔"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeDialog">取消</el-button>
          <el-button type="primary" @click="saveQuestion">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看真题详情对话框 -->
    <el-dialog 
      title="真题详情" 
      v-model="viewDialogVisible" 
      width="800px">
      <div class="question-detail" v-if="viewQuestionData">
        <div class="detail-row">
          <span class="label">题目标题：</span>
          <span class="value">{{ viewQuestionData.title }}</span>
        </div>
        <div class="detail-row">
          <span class="label">企业：</span>
          <span class="value">{{ viewQuestionData.companyName }}</span>
        </div>
        <div class="detail-row">
          <span class="label">分类：</span>
          <span class="value">{{ viewQuestionData.categoryName }}</span>
        </div>
        <div class="detail-row">
          <span class="label">类型：</span>
          <span class="value">
            <el-tag :type="getQuestionTypeColor(viewQuestionData.questionType)">
              {{ getQuestionTypeText(viewQuestionData.questionType) }}
            </el-tag>
          </span>
        </div>
        <div class="detail-row">
          <span class="label">难度：</span>
          <span class="value">
            <el-tag :type="getDifficultyColor(viewQuestionData.difficulty)">
              {{ getDifficultyText(viewQuestionData.difficulty) }}
            </el-tag>
          </span>
        </div>
        <div class="detail-row">
          <span class="label">题目内容：</span>
          <div class="value content">{{ viewQuestionData.content }}</div>
        </div>
        <div class="detail-row" v-if="viewQuestionData.answer">
          <span class="label">参考答案：</span>
          <div class="value content">{{ viewQuestionData.answer }}</div>
        </div>
        <div class="detail-row" v-if="viewQuestionData.analysis">
          <span class="label">题目解析：</span>
          <div class="value content">{{ viewQuestionData.analysis }}</div>
        </div>
        <div class="detail-row" v-if="viewQuestionData.tags">
          <span class="label">标签：</span>
          <span class="value">{{ viewQuestionData.tags }}</span>
        </div>
        <div class="detail-row">
          <span class="label">查看次数：</span>
          <span class="value">{{ viewQuestionData.viewCount }}</span>
        </div>
        <div class="detail-row">
          <span class="label">点赞数：</span>
          <span class="value">{{ viewQuestionData.likeCount }}</span>
        </div>
        <div class="detail-row">
          <span class="label">状态：</span>
          <span class="value">
            <el-tag :type="viewQuestionData.status === 1 ? 'success' : 'danger'">
              {{ viewQuestionData.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </span>
        </div>
        <div class="detail-row">
          <span class="label">创建时间：</span>
          <span class="value">{{ viewQuestionData.createdTime }}</span>
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
  name: 'InterviewQuestionManage', // 组件名称
  components: {
    Plus,
    Search
  },
  setup() {
    // 响应式数据定义
    const loading = ref(false) // 加载状态
    const questionList = ref([]) // 真题列表数据
    const companyOptions = ref([]) // 企业选项
    const categoryOptions = ref([]) // 分类选项
    const dialogVisible = ref(false) // 新增/编辑对话框显示状态
    const viewDialogVisible = ref(false) // 查看详情对话框显示状态
    const dialogTitle = ref('') // 对话框标题
    const isEdit = ref(false) // 是否为编辑模式
    const questionFormRef = ref(null) // 表单引用
    const viewQuestionData = ref({}) // 查看的真题数据

    // 搜索参数
    const searchParams = reactive({
      companyId: null, // 企业ID
      categoryId: null, // 分类ID
      difficulty: null, // 难度
      keyword: '' // 关键词
    })

    // 分页参数
    const pagination = reactive({
      current: 1, // 当前页码
      size: 10, // 每页大小
      total: 0 // 总记录数
    })

    // 真题表单数据
    const questionForm = reactive({
      id: null,
      companyId: null,
      categoryId: null,
      title: '',
      content: '',
      difficulty: 1, // 默认简单
      answer: '',
      analysis: '',
      tags: '',
      status: 1
    })

    // 表单验证规则
    const formRules = {
      companyId: [
        { required: true, message: '请选择企业', trigger: 'change' }
      ],
      categoryId: [
        { required: true, message: '请选择分类', trigger: 'change' }
      ],
      title: [
        { required: true, message: '请输入题目标题', trigger: 'blur' },
        { min: 5, max: 200, message: '题目标题长度在 5 到 200 个字符', trigger: 'blur' }
      ],
      content: [
        { required: true, message: '请输入题目内容', trigger: 'blur' },
        { min: 10, message: '题目内容至少 10 个字符', trigger: 'blur' }
      ],
      difficulty: [
        { required: true, message: '请选择难度', trigger: 'change' }
      ]
    }

    // 获取题目类型颜色
    const getQuestionTypeColor = (type) => {
      const colors = { 1: 'primary', 2: 'success', 3: 'warning' }
      return colors[type] || 'info'
    }

    // 获取题目类型文本
    const getQuestionTypeText = (type) => {
      const texts = { 1: '笔试题', 2: '面试题', 3: '编程题' }
      return texts[type] || '未知'
    }

    // 获取难度颜色
    const getDifficultyColor = (difficulty) => {
      const colors = { 1: 'success', 2: 'warning', 3: 'danger' }
      return colors[difficulty] || 'info'
    }

    // 获取难度文本
    const getDifficultyText = (difficulty) => {
      const texts = { 1: '简单', 2: '中等', 3: '困难' }
      return texts[difficulty] || '未知'
    }

    // 获取真题列表数据（模拟数据，实际需要调用API）
    const fetchQuestions = async () => {
      loading.value = true
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 500))
        
        // 模拟数据
        questionList.value = [
          {
            id: 1,
            title: 'Java多线程面试题：如何保证线程安全？',
            companyName: '阿里巴巴',
            categoryName: 'Java开发',
            questionType: 2,
            difficulty: 2,
            viewCount: 156,
            likeCount: 23,
            status: 1,
            createdTime: '2024-01-15 10:30:00'
          },
          {
            id: 2,
            title: 'React Hooks面试题：useEffect的使用场景',
            companyName: '字节跳动',
            categoryName: '前端开发',
            questionType: 2,
            difficulty: 2,
            viewCount: 89,
            likeCount: 15,
            status: 1,
            createdTime: '2024-01-14 14:20:00'
          }
        ]
        pagination.total = 2
        
        ElMessage.success('数据加载成功（当前为模拟数据）')
      } catch (error) {
        console.error('获取真题列表失败:', error)
        ElMessage.error('获取真题列表失败，请检查网络连接')
      } finally {
        loading.value = false
      }
    }

    // 获取企业选项
    const fetchCompanyOptions = async () => {
      try {
        const response = await axios.get('/api/companies/enabled')
        if (response.data.code === 200) {
          companyOptions.value = response.data.data
        }
      } catch (error) {
        console.error('获取企业列表失败:', error)
      }
    }

    // 获取分类选项
    const fetchCategoryOptions = async () => {
      try {
        const response = await axios.get('/api/company-question-categories/enabled')
        if (response.data.code === 200) {
          categoryOptions.value = response.data.data
        }
      } catch (error) {
        console.error('获取分类列表失败:', error)
      }
    }

    // 搜索真题
    const searchQuestions = () => {
      pagination.current = 1 // 重置到第一页
      fetchQuestions()
    }

    // 重置搜索条件
    const resetSearch = () => {
      Object.assign(searchParams, {
        companyId: null,
        categoryId: null,
        difficulty: null,
        keyword: ''
      })
      searchQuestions()
    }

    // 分页大小改变
    const handleSizeChange = (newSize) => {
      pagination.size = newSize
      pagination.current = 1
      fetchQuestions()
    }

    // 当前页改变
    const handleCurrentChange = (newPage) => {
      pagination.current = newPage
      fetchQuestions()
    }

    // 显示新增对话框
    const showAddDialog = () => {
      dialogTitle.value = '新增真题'
      isEdit.value = false
      resetForm()
      dialogVisible.value = true
    }

    // 编辑真题
    const editQuestion = (question) => {
      dialogTitle.value = '编辑真题'
      isEdit.value = true
      Object.assign(questionForm, question)
      dialogVisible.value = true
    }

    // 查看真题详情
    const viewQuestion = (question) => {
      viewQuestionData.value = { ...question }
      viewDialogVisible.value = true
    }

    // 删除真题
    const deleteQuestion = async (question) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除真题 "${question.title}" 吗？`,
          '删除确认',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        )

        // 模拟删除API调用
        ElMessage.success('删除成功（模拟操作）')
        fetchQuestions() // 重新加载列表
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除真题失败:', error)
          ElMessage.error('删除失败，请检查网络连接')
        }
      }
    }

    // 保存真题
    const saveQuestion = async () => {
      try {
        // 表单验证
        await questionFormRef.value.validate()

        // 模拟保存API调用
        ElMessage.success((isEdit.value ? '更新' : '新增') + '成功（模拟操作）')
        closeDialog()
        fetchQuestions() // 重新加载列表
      } catch (error) {
        console.error('保存真题失败:', error)
        ElMessage.error('保存失败，请检查表单填写')
      }
    }

    // 关闭对话框
    const closeDialog = () => {
      dialogVisible.value = false
      resetForm()
    }

    // 重置表单
    const resetForm = () => {
      Object.assign(questionForm, {
        id: null,
        companyId: null,
        categoryId: null,
        title: '',
        content: '',
        difficulty: 1, // 默认简单
        answer: '',
        analysis: '',
        tags: '',
        status: 1
      })
      // 清除表单验证状态
      if (questionFormRef.value) {
        questionFormRef.value.clearValidate()
      }
    }

    // 组件挂载时获取数据
    onMounted(() => {
      fetchQuestions()
      fetchCompanyOptions()
      fetchCategoryOptions()
    })

    return {
      // 响应式数据
      loading,
      questionList,
      companyOptions,
      categoryOptions,
      dialogVisible,
      viewDialogVisible,
      dialogTitle,
      isEdit,
      questionFormRef,
      searchParams,
      pagination,
      questionForm,
      formRules,
      viewQuestionData,
      
      // 方法
      getQuestionTypeColor,
      getQuestionTypeText,
      getDifficultyColor,
      getDifficultyText,
      fetchQuestions,
      searchQuestions,
      resetSearch,
      handleSizeChange,
      handleCurrentChange,
      showAddDialog,
      editQuestion,
      viewQuestion,
      deleteQuestion,
      saveQuestion,
      closeDialog,
      resetForm
    }
  }
}
</script>

<style scoped>
/* 页面样式 */
.interview-question-manage {
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

/* 真题详情样式 */
.question-detail {
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

.detail-row .value.content {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.6;
}

/* 对话框底部样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 