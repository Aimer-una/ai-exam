<template>
  <div class="practice-page">
    <!-- 顶部导航栏 -->
    <div class="navbar">
      <div class="logo">
        <img src="../assets/logo.svg" alt="logo" class="logo-img" />
        <span class="title">智能刷题</span>
      </div>
      <div class="nav-actions">
        <el-button @click="goBack" icon="ArrowLeft">返回首页</el-button>
        <el-button type="primary" @click="goToExam" icon="Document">考试入口</el-button>
        <el-button @click="showStats" icon="DataAnalysis">练习统计</el-button>
        <el-button type="danger" @click="resetAllPractice" icon="Delete">重置练习记录</el-button>
      </div>
    </div>

    <!-- 主体区域 -->
    <div class="main-content">
      <!-- 左侧分类树 -->
      <div class="sidebar">
        <div class="sidebar-header">
          <h3>题目分类</h3>
          <el-button 
            size="small" 
            @click="resetCategoryFilter" 
            :disabled="!selectedCategoryId"
          >
            重置筛选
          </el-button>
        </div>
        <div class="category-tree">
          <el-tree
            :data="categoryTree"
            :props="{ label: 'name', children: 'children' }"
            @node-click="handleCategorySelect"
            node-key="id"
            :current-node-key="selectedCategoryId"
            highlight-current
            :expand-on-click-node="false"
            default-expand-all
          >
            <template #default="{ node, data }">
              <span class="custom-tree-node">
                <span>{{ node.label }}</span>
                <span v-if="data.count !== undefined" class="category-count">({{ data.count }})</span>
              </span>
            </template>
          </el-tree>
        </div>
      </div>

      <!-- 右侧题目列表 -->
      <div class="content-area">
        <!-- 搜索和筛选栏 -->
        <div class="search-bar">
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="搜索题目内容" 
            clearable
            style="width: 280px" 
            @input="handleSearch"
          >
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-select 
            v-model="searchForm.type" 
            placeholder="题型" 
            clearable 
            style="width: 120px" 
            @change="handleSearch"
          >
            <el-option label="选择题" value="CHOICE" />
            <el-option label="判断题" value="JUDGE" />
            <el-option label="简答题" value="TEXT" />
          </el-select>
          <el-select 
            v-model="searchForm.difficulty" 
            placeholder="难度" 
            clearable 
            style="width: 120px" 
            @change="handleSearch"
          >
            <el-option label="简单" value="EASY" />
            <el-option label="中等" value="MEDIUM" />
            <el-option label="困难" value="HARD" />
          </el-select>
          <el-button type="primary" @click="handleSearch" icon="Search">搜索</el-button>
          <el-button @click="resetSearch" icon="Refresh">重置</el-button>
          <el-button 
            v-if="!searchForm.collected" 
            type="success" 
            @click="showCollectedQuestions" 
            icon="Star"
          >
            我的收藏
          </el-button>
          <el-button 
            v-if="searchForm.collected" 
            type="warning" 
            @click="backToNormalMode" 
            icon="ArrowLeft"
          >
            返回刷题
          </el-button>
        </div>

        <!-- 收藏模式提示 -->
        <div v-if="searchForm.collected" class="collected-mode-tip">
          <el-alert
            title="当前正在查看收藏的题目"
            type="info"
            :closable="false"
            show-icon
          >
            <template #default>
              共找到 <strong>{{ pagination.total }}</strong> 道收藏题目，点击"返回刷题"可切换到正常模式
            </template>
          </el-alert>
        </div>

        <!-- 题目列表 -->
        <div class="question-list" v-loading="loading">
          <div 
            v-for="(question, index) in questionList" 
            :key="question.id" 
            class="question-card"
            :class="{ 'answered': question.isAnswered, 'correct': question.isCorrect }"
          >
            <!-- 题目头部信息 -->
            <div class="question-header">
              <div class="question-info">
                <span class="question-number">第 {{ (pagination.current - 1) * pagination.size + index + 1 }} 题</span>
                <el-tag :type="getQuestionTypeTag(question.type)" size="small">
                  {{ getQuestionTypeText(question.type) }}
                </el-tag>
                <el-tag :type="getDifficultyType(question.difficulty)" size="small">
                  {{ getDifficultyText(question.difficulty) }}
                </el-tag>
                <span class="category-name">{{ question.categoryName }}</span>
              </div>
              <div class="question-actions">
                <el-button 
                  size="small" 
                  :type="question.showAnswer ? 'warning' : 'primary'"
                  @click="toggleAnswer(question)"
                  :icon="question.showAnswer ? 'View' : 'Hide'"
                >
                  {{ question.showAnswer ? '隐藏答案' : '查看答案' }}
                </el-button>
                <el-button 
                  size="small" 
                  :type="question.isCollected ? 'success' : 'info'"
                  @click="toggleCollect(question)"
                  :icon="question.isCollected ? 'Star' : 'StarFilled'"
                >
                  {{ question.isCollected ? '已收藏' : '收藏' }}
                </el-button>
              </div>
            </div>

            <!-- 题目内容 -->
            <div class="question-content">
              <h4 class="question-title">{{ question.title }}</h4>
              
              <!-- 选择题选项 -->
              <div v-if="question.type === 'CHOICE' && question.choices" class="question-choices">
                <div 
                  v-for="(choice, choiceIndex) in question.choices" 
                  :key="choiceIndex"
                  class="choice-item"
                  :class="{ 
                    'correct': question.showAnswer && choice.isCorrect,
                    'selected': getChoiceSelected(question, choice)
                  }"
                  @click="selectChoice(question, choice)"
                >
                  <span class="choice-label">{{ String.fromCharCode(65 + choiceIndex) }}</span>
                  <span class="choice-content">{{ choice.content }}</span>
                  <el-icon v-if="question.showAnswer && choice.isCorrect" class="correct-icon">
                    <Check />
                  </el-icon>
                </div>
              </div>

              <!-- 判断题选项 -->
              <div v-if="question.type === 'JUDGE'" class="judge-options">
                <el-radio-group 
                  v-model="question.userAnswer" 
                  @change="handleJudgeAnswer(question)"
                >
                  <el-radio label="TRUE" size="large">正确</el-radio>
                  <el-radio label="FALSE" size="large">错误</el-radio>
                </el-radio-group>
              </div>

              <!-- 简答题输入框 -->
              <div v-if="question.type === 'TEXT'" class="text-answer">
                <el-input
                  v-model="question.userAnswer"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入您的答案..."
                  @blur="handleTextAnswer(question)"
                />
              </div>

              <!-- 答案显示区域 -->
              <div v-if="question.showAnswer" class="answer-section">
                <div class="answer-header">
                  <el-icon><InfoFilled /></el-icon>
                  <span>正确答案</span>
                </div>
                <div class="answer-content">
                  <div v-if="question.type === 'CHOICE'">
                    <div v-for="(choice, choiceIndex) in question.choices" :key="choiceIndex">
                      <span v-if="choice.isCorrect" class="correct-answer">
                        {{ String.fromCharCode(65 + choiceIndex) }}. {{ choice.content }}
                      </span>
                    </div>
                  </div>
                  <div v-else-if="question.type === 'JUDGE'">
                    <span class="correct-answer">
                      {{ question.answer?.answer === 'TRUE' ? '正确' : '错误' }}
                    </span>
                  </div>
                  <div v-else>
                    <div class="text-answer-content">{{ question.answer?.answer }}</div>
                  </div>
                </div>
                
                <!-- 题目解析 -->
                <div v-if="question.analysis" class="analysis-section">
                  <div class="analysis-header">
                    <el-icon><Notebook /></el-icon>
                    <span>题目解析</span>
                  </div>
                  <div class="analysis-content">{{ question.analysis }}</div>
                </div>
              </div>

              <!-- 答题结果反馈 -->
              <div v-if="question.isAnswered && question.showAnswer" class="feedback-section">
                <!-- 选择题显示对错 -->
                <div v-if="question.type === 'CHOICE'">
                  <el-alert
                    :title="question.isCorrect ? '回答正确！' : '回答错误！'"
                    :type="question.isCorrect ? 'success' : 'error'"
                    show-icon
                    :closable="false"
                  >
                    <div v-if="!question.isCorrect">
                      <div>你的答案是：<strong>{{ formatChoiceUserAnswer(question) }}</strong></div>
                    </div>
                  </el-alert>
                </div>
                <!-- 判断题显示对错 -->
                <div v-if="question.type === 'JUDGE'">
                  <el-alert
                    :title="question.isCorrect ? '回答正确！' : '回答错误！'"
                    :type="question.isCorrect ? 'success' : 'error'"
                    show-icon
                    :closable="false"
                  >
                    <div v-if="!question.isCorrect">
                      <div><strong>您的答案：</strong>{{ question.userAnswer === 'TRUE' ? '正确' : '错误' }}</div>
                      <div><strong>正确答案：</strong>{{ question.answer?.answer === 'TRUE' ? '正确' : '错误' }}</div>
                    </div>
                  </el-alert>
                </div>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-if="!loading && questionList.length === 0" class="empty-state">
            <el-empty description="暂无题目，请尝试调整筛选条件" />
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="questionList.length > 0">
          <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            :page-sizes="[5, 10, 20, 50]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <!-- 练习统计对话框 -->
    <el-dialog v-model="statsDialogVisible" title="练习统计" width="600px">
      <div class="stats-content">
        <div class="stats-overview">
          <div class="stat-item">
            <div class="stat-number">{{ practiceStats.totalQuestions }}</div>
            <div class="stat-label">总题目数</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ practiceStats.answeredCount }}</div>
            <div class="stat-label">已练习</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ practiceStats.correctCount }}</div>
            <div class="stat-label">答对题数</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ practiceStats.correctRate }}%</div>
            <div class="stat-label">正确率</div>
          </div>
        </div>
        
        <div class="stats-detail">
          <h4>分类练习情况</h4>
          <el-table :data="practiceStats.categoryStats" style="width: 100%">
            <el-table-column prop="categoryName" label="分类" />
            <el-table-column prop="totalCount" label="总题数" width="80" />
            <el-table-column prop="answeredCount" label="已练习" width="80" />
            <el-table-column prop="correctRate" label="正确率" width="80">
              <template #default="{ row }">
                <span>{{ row.correctRate }}%</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, ArrowLeft, Document, DataAnalysis, View, Hide, Star, StarFilled, 
  Check, InfoFilled, Notebook, Refresh
} from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()

// 分类树数据
const categoryTree = ref([])
const selectedCategoryId = ref('')

// 题目列表相关
const questionList = ref([])
const loading = ref(false)
const pagination = reactive({ current: 1, size: 10, total: 0 })

// 搜索表单
const searchForm = reactive({
  keyword: '',
  type: '',
  difficulty: '',
  collected: false
})

// 练习统计
const practiceStats = ref({
  totalQuestions: 0,
  answeredCount: 0,
  correctCount: 0,
  correctRate: 0,
  categoryStats: []
})

// 统计对话框
const statsDialogVisible = ref(false)

// 获取分类树
const getCategoryTree = async () => {
  try {
    console.log('开始获取分类树...') // 调试信息
    const res = await request.get('/api/categories/tree')
    categoryTree.value = res.data || []
    console.log('分类树数据：', categoryTree.value) // 调试信息
    
    // 获取分类树后，自动选择第一个有题目的子分类  // 自动选择有题目的分类
    if (categoryTree.value.length > 0) {
      let foundCategory = false
      
      // 遍历所有父分类和子分类，找到第一个有题目的子分类  // 寻找有题目的子分类
      for (const parentCategory of categoryTree.value) {
        if (parentCategory.children && parentCategory.children.length > 0) {
          for (const childCategory of parentCategory.children) {
            // 检查子分类是否有题目  // 检查题目数量
            if (childCategory.count && childCategory.count > 0) {
              console.log('找到有题目的分类：', childCategory.name, '题目数量：', childCategory.count) // 调试信息
              selectedCategoryId.value = childCategory.id
              foundCategory = true
              break
            }
          }
          if (foundCategory) break
        }
      }
      
      // 如果没找到有题目的子分类，就尝试使用题型筛选  // 备选方案：使用题型筛选
      if (!foundCategory) {
        console.log('没有找到有题目的子分类，使用题型筛选') // 调试信息
        searchForm.type = 'CHOICE' // 默认选择题
      }
      
      // 选择分类后立即加载题目  // 加载题目
      console.log('准备加载题目，selectedCategoryId:', selectedCategoryId.value, 'searchForm.type:', searchForm.type) // 调试信息
      await getQuestionList()
    }
  } catch (error) {
    console.error('获取分类树失败：', error)
    ElMessage.error('获取分类树失败')
  }
}

// 获取题目总数，用于显示统计信息
const getQuestionStats = async () => {
  try {
    const allRes = await request.get('/api/questions/list', { params: { page: 1, size: 9999 } })
    totalQuestions.value = allRes.data.total || 0
  } catch (error) {
    console.error('获取题目统计失败：', error)
  }
}

// 获取题目列表
const getQuestionList = async () => {
  console.log('开始获取题目列表...') // 调试信息
  
  // 如果是收藏模式，不需要分类ID  // 如果是收藏模式处理
  if (searchForm.collected) {
    console.log('收藏模式，调用getCollectedQuestions') // 调试信息
    await getCollectedQuestions()
    return
  }
  
  // 检查是否有有效的查询条件  // 检查查询条件
  if (!selectedCategoryId.value && !searchForm.type && !searchForm.keyword && !searchForm.difficulty) {
    // 只在用户主动操作时显示警告，初始化时不显示  // 如果没有任何筛选条件
    console.log('没有有效的查询条件') // 调试信息
    if (categoryTree.value.length > 0) {
      ElMessage.warning('请先选择题目分类或筛选条件')
    }
    return
  }
  
  loading.value = true
  try {
    const params = {
      page: pagination.current,  // 当前页码
      size: pagination.size,     // 每页大小
    }
    
    // 添加分类ID参数（如果有）  // 如果选择了具体分类，添加分类ID
    if (selectedCategoryId.value) {
      params.categoryId = selectedCategoryId.value
    }
    
    // 添加关键词搜索参数  // 如果有搜索关键词，添加到参数中
    if (searchForm.keyword) {
      params.keyword = searchForm.keyword
    }
    
    // 添加题型筛选参数  // 如果选择了题型，添加到参数中
    if (searchForm.type) {
      params.type = searchForm.type
    }
    
    // 添加难度筛选参数  // 如果选择了难度，添加到参数中
    if (searchForm.difficulty) {
      params.difficulty = searchForm.difficulty
    }
    
    console.log('请求参数：', params) // 调试信息
    
    // 发送请求获取题目列表  // 向后端发送请求
    const res = await request.get('/api/questions/list', { params })
    
    console.log('API响应：', res) // 调试信息
    
    // 设置题目列表数据  // 更新题目列表
    questionList.value = res.data.records || []
    pagination.total = res.data.total || 0
    
    console.log('题目列表：', questionList.value.length, '条题目') // 调试信息
    console.log('总数：', pagination.total) // 调试信息
    
    // 加载练习记录
    await loadPracticeRecords()
    
    // 如果当前页没有数据且不是第一页，回到第一页  // 如果没有数据且不是第一页，跳转到第一页
    if (questionList.value.length === 0 && pagination.current > 1) {
      console.log('当前页无数据，回到第一页') // 调试信息
      pagination.current = 1
      await getQuestionList()
    }
  } catch (error) {
    console.error('获取题目列表失败：', error)
    ElMessage.error('获取题目列表失败')
  } finally {
    loading.value = false
  }
}

// 新增：获取收藏的题目
const getCollectedQuestions = async () => {
  loading.value = true
  try {
    // 获取本地存储的练习记录  // 从本地存储获取用户练习记录
    const records = JSON.parse(localStorage.getItem('practiceRecords') || '{}')
    
    // 获取所有收藏的题目ID  // 找出所有被收藏的题目ID
    const collectedQuestionIds = Object.keys(records).filter(id => records[id].isCollected)
    
    if (collectedQuestionIds.length === 0) {
      questionList.value = []
      pagination.total = 0
      return
    }
    
    // 从后端获取这些题目的详细信息  // 构建查询参数
    const params = { 
      page: 1, 
      size: 1000 // 获取大量数据
    }
    
    // 添加题型筛选参数  // 如果有题型筛选，添加到参数中
    if (searchForm.type) {
      params.type = searchForm.type
    }
    
    // 添加难度筛选参数  // 如果有难度筛选，添加到参数中  
    if (searchForm.difficulty) {
      params.difficulty = searchForm.difficulty
    }
    
    // 发送请求获取题目  // 向后端请求题目数据
    const res = await request.get('/api/questions/list', { params })
    
    // 筛选出收藏的题目  // 筛选收藏的题目并应用关键词搜索
    const allQuestions = res.data.records || []
    let filteredQuestions = allQuestions.filter(q => collectedQuestionIds.includes(q.id.toString()))
    
    // 应用关键词搜索筛选  // 如果有关键词搜索，进行筛选
    if (searchForm.keyword && searchForm.keyword.trim()) {
      const keyword = searchForm.keyword.trim().toLowerCase()
      filteredQuestions = filteredQuestions.filter(q => 
        q.title && q.title.toLowerCase().includes(keyword)
      )
    }
    
    questionList.value = filteredQuestions
    
    // 分页处理  // 处理分页逻辑
    pagination.total = questionList.value.length
    const startIndex = (pagination.current - 1) * pagination.size
    const endIndex = startIndex + pagination.size
    questionList.value = questionList.value.slice(startIndex, endIndex)
    
    // 加载练习记录
    await loadPracticeRecords()
    
  } catch (error) {
    console.error('获取收藏题目失败：', error)
    ElMessage.error('获取收藏题目失败')
  } finally {
    loading.value = false
  }
}

// 加载用户练习记录
const loadPracticeRecords = async () => {
  try {
    // 这里需要实现获取用户练习记录的API
    // const res = await request.get('/practice/records', { 
    //   params: { questionIds: questionList.value.map(q => q.id) }
    // })
    // 暂时使用本地存储模拟
    const records = JSON.parse(localStorage.getItem('practiceRecords') || '{}')
    
    questionList.value.forEach(question => {
      const record = records[question.id]
      if (record) {
        question.userAnswer = record.userAnswer
        question.isAnswered = record.isAnswered
        question.isCorrect = record.isCorrect
        question.isCollected = record.isCollected
      }
    })
  } catch (error) {
    console.error('加载练习记录失败：', error)
  }
}

// 保存练习记录
const savePracticeRecord = (question) => {
  try {
    const records = JSON.parse(localStorage.getItem('practiceRecords') || '{}')
    records[question.id] = {
      userAnswer: question.userAnswer,
      isAnswered: question.isAnswered,
      isCorrect: question.isCorrect,
      isCollected: question.isCollected,
      answeredAt: new Date().toISOString()
    }
    localStorage.setItem('practiceRecords', JSON.stringify(records))
    
    // 更新统计数据
    updatePracticeStats()
  } catch (error) {
    console.error('保存练习记录失败：', error)
  }
}

// 更新练习统计
const updatePracticeStats = async () => {
  try {
    // 获取所有题目数量
    const allQuestionsRes = await request.get('/api/questions/list', { 
      params: { page: 1, size: 9999 } // 获取所有题目
    })
    const allQuestions = allQuestionsRes.data.records
    
    // 获取练习记录
    const records = JSON.parse(localStorage.getItem('practiceRecords') || '{}')
    
    // 计算总体统计
    const answered = allQuestions.filter(q => records[q.id]?.isAnswered)
    const objectiveQuestions = answered.filter(q => q.type === 'CHOICE' || q.type === 'JUDGE')
    const correct = objectiveQuestions.filter(q => records[q.id]?.isCorrect)
    
    // 计算分类统计
    const categoryStats = {}
    allQuestions.forEach(question => {
      const categoryName = question.categoryName || '未分类'
      if (!categoryStats[categoryName]) {
        categoryStats[categoryName] = {
          categoryName,
          totalCount: 0,
          answeredCount: 0,
          correctCount: 0,
          correctRate: 0
        }
      }
      
      categoryStats[categoryName].totalCount++
      
      if (records[question.id]?.isAnswered) {
        categoryStats[categoryName].answeredCount++
        
        if ((question.type === 'CHOICE' || question.type === 'JUDGE') && records[question.id]?.isCorrect) {
          categoryStats[categoryName].correctCount++
        }
      }
    })
    
    // 计算每个分类的正确率
    Object.values(categoryStats).forEach(stat => {
      const objectiveAnswered = stat.answeredCount // 这里可以进一步区分客观题和主观题
      stat.correctRate = objectiveAnswered > 0 ? Math.round((stat.correctCount / objectiveAnswered) * 100) : 0
    })
    
    practiceStats.value = {
      totalQuestions: allQuestions.length,
      answeredCount: answered.length,
      correctCount: correct.length,
      correctRate: objectiveQuestions.length > 0 ? Math.round((correct.length / objectiveQuestions.length) * 100) : 0,
      categoryStats: Object.values(categoryStats)
    }
  } catch (error) {
    console.error('更新练习统计失败：', error)
  }
}

// 分类选择
const handleCategorySelect = (data) => {
  console.log('用户点击分类：', data) // 调试信息
  
  // 清除收藏筛选状态  // 清除收藏模式
  searchForm.collected = false
  
  // 如果是父级分类（有子分类），则查询该父分类下的所有题目  // 处理父级分类点击
  if (data.children && data.children.length > 0) {
    console.log('点击的是父级分类：', data.name) // 调试信息
    // 点击的是父级分类，查询该分类类型下的所有题目
    const categoryName = data.name
    let questionType = ''
    
    // 根据分类名称确定题目类型  // 根据分类名称映射题目类型
    switch (categoryName) {
      case '选择题':
        questionType = 'CHOICE'
        break
      case '判断题':
        questionType = 'JUDGE'
        break
      case '简答题':
        questionType = 'TEXT'
        break
      default:
        questionType = ''
    }
    
    console.log('映射的题目类型：', questionType) // 调试信息
    
    // 设置题型筛选条件  // 设置题型筛选
    searchForm.type = questionType
    // 清除分类ID，使用题型筛选  // 使用题型筛选而不是分类ID
    selectedCategoryId.value = ''
    
    console.log('设置题型筛选，type:', searchForm.type, 'categoryId:', selectedCategoryId.value) // 调试信息
    
    ElMessage.info(`正在加载${categoryName}...`)
  } else {
    console.log('点击的是子分类：', data.name, 'ID:', data.id) // 调试信息
    // 叶子节点，使用分类ID查询  // 处理子分类点击
    selectedCategoryId.value = data.id
    // 清除题型筛选，使用分类ID  // 清除题型筛选
    searchForm.type = ''
    
    console.log('设置分类ID，categoryId:', selectedCategoryId.value, 'type:', searchForm.type) // 调试信息
  }
  
  // 重置到第一页并查询  // 重置分页并查询
  pagination.current = 1
  console.log('准备调用getQuestionList') // 调试信息
  getQuestionList()
}

// 重置分类筛选
const resetCategoryFilter = () => {
  // 清除收藏筛选状态
  searchForm.collected = false
  selectedCategoryId.value = ''
  pagination.current = 1
  getQuestionList()
}

// 搜索处理
const handleSearch = () => {
  // 如果不是收藏筛选，清除收藏状态
  if (!searchForm.collected) {
    searchForm.collected = false
  }
  pagination.current = 1
  getQuestionList()
}

// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    keyword: '',
    type: '',
    difficulty: '',
    collected: false
  })
  selectedCategoryId.value = ''
  handleSearch()
}

// 切换答案显示
const toggleAnswer = async (question) => {
  // 如果答案尚未显示，则从后端获取详细信息
  if (!question.showAnswer) {
    try {
      const res = await request.get(`/api/questions/${question.id}`);
      if (res.data) {
        // 将获取到的详细信息合并到当前题目对象中
        Object.assign(question, res.data);
        question.showAnswer = true; // 确保 showAnswer 状态在数据更新后设置为 true
      }
    } catch (error) {
      ElMessage.error('获取答案失败，请稍后重试');
      console.error('获取题目详情失败：', error);
      return; // 如果获取失败，则不切换显示状态
    }
  } else {
    // 如果答案已显示，则直接隐藏
    question.showAnswer = false;
  }
}

// 切换收藏状态
const toggleCollect = (question) => {
  question.isCollected = !question.isCollected
  savePracticeRecord(question)
  ElMessage.success(question.isCollected ? '已收藏' : '已取消收藏')
}

// 选择题选项点击
const selectChoice = (question, choice) => {
  if (question.multi) {
    // 多选题处理
    const answers = question.userAnswer ? question.userAnswer.split(',') : []
    const choiceContent = choice.content
    const index = answers.indexOf(choiceContent)
    
    if (index > -1) {
      answers.splice(index, 1)
    } else {
      answers.push(choiceContent)
    }
    question.userAnswer = answers.join(',')
  } else {
    // 单选题处理
    question.userAnswer = choice.content
  }
  
  question.isAnswered = true
  question.isCorrect = checkAnswerCorrect(question)
  savePracticeRecord(question)
}

// 判断题答题处理
const handleJudgeAnswer = (question) => {
  question.isAnswered = true
  question.isCorrect = checkAnswerCorrect(question)
  savePracticeRecord(question)
}

// 简答题答题处理
const handleTextAnswer = (question) => {
  if (question.userAnswer && question.userAnswer.trim()) {
    question.isAnswered = true
    // 简答题不进行正确性判断
    savePracticeRecord(question)
  }
}

// 检查答案是否正确
const checkAnswerCorrect = (question) => {
  if (question.type === 'CHOICE') {
    const correctAnswers = question.choices
      .filter(choice => choice.isCorrect)
      .map(choice => choice.content)
    
    if (question.multi) {
      const userAnswers = question.userAnswer ? question.userAnswer.split(',') : []
      return correctAnswers.length === userAnswers.length && 
             correctAnswers.every(answer => userAnswers.includes(answer))
    } else {
      return correctAnswers.includes(question.userAnswer)
    }
  } else if (question.type === 'JUDGE') {
    return question.userAnswer === question.answer?.answer
  } else {
    return true // 简答题暂时默认正确
  }
}

// 将选择题答案转换为选项标号（A、B、C、D）
const getChoiceLabel = (question, answerContent) => {
  if (!question.choices || !answerContent) return ''
  
  const choiceIndex = question.choices.findIndex(choice => choice.content === answerContent)
  return choiceIndex >= 0 ? String.fromCharCode(65 + choiceIndex) : answerContent
}

// 格式化选择题用户答案为选项标号
const formatChoiceUserAnswer = (question) => {
  if (!question.userAnswer) return ''
  
  if (question.multi) {
    // 多选题：返回多个选项标号
    const userAnswers = question.userAnswer.split(',')
    return userAnswers.map(answer => getChoiceLabel(question, answer)).join('、')
  } else {
    // 单选题：返回单个选项标号
    return getChoiceLabel(question, question.userAnswer)
  }
}

// 判断选择题选项是否被选中
const getChoiceSelected = (question, choice) => {
  if (!question.userAnswer) return false
  
  if (question.multi) {
    // 多选题：检查选项内容是否在用户答案中
    const userAnswers = question.userAnswer.split(',')
    return userAnswers.includes(choice.content)
  } else {
    // 单选题：严格相等比较
    return question.userAnswer === choice.content
  }
}

// 格式化正确答案显示
const formatCorrectAnswer = (question) => {
  if (question.type === 'CHOICE') {
    return question.choices
      .filter(choice => choice.isCorrect)
      .map(choice => choice.content)
      .join(', ')
  } else if (question.type === 'JUDGE') {
    return question.answer?.answer === 'TRUE' ? '正确' : '错误'
  } else {
    return question.answer?.answer
  }
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  getQuestionList()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  getQuestionList()
}

// 显示统计
const showStats = () => {
  updatePracticeStats()
  statsDialogVisible.value = true
}

// 工具方法
const getQuestionTypeText = (type) => {
  const typeMap = {
    'CHOICE': '选择题',
    'JUDGE': '判断题',
    'TEXT': '简答题'
  }
  return typeMap[type] || type
}

const getQuestionTypeTag = (type) => {
  const tagMap = {
    'CHOICE': 'primary',
    'JUDGE': 'success',
    'TEXT': 'warning'
  }
  return tagMap[type] || 'info'
}

const getDifficultyText = (difficulty) => {
  const difficultyMap = {
    'EASY': '简单',
    'MEDIUM': '中等',
    'HARD': '困难'
  }
  return difficultyMap[difficulty] || difficulty
}

const getDifficultyType = (difficulty) => {
  const typeMap = {
    'EASY': 'success',
    'MEDIUM': 'warning',
    'HARD': 'danger'
  }
  return typeMap[difficulty] || 'info'
}

// 导航功能
const goBack = () => {
  router.push('/home')
}

const goToExam = () => {
  router.push('/exam/list')
}

// 显示收藏的题目
const showCollectedQuestions = () => {
  // 设置筛选条件为显示收藏的题目
  searchForm.keyword = ''
  searchForm.type = ''
  searchForm.difficulty = ''
  selectedCategoryId.value = ''
  
  // 添加收藏筛选标识
  searchForm.collected = true
  
  pagination.current = 1
  getQuestionList()
  
  ElMessage.info('已筛选显示收藏的题目')
}

// 重置所有练习记录
const resetAllPractice = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要重置所有练习记录吗？此操作将清除所有答题记录、收藏状态等数据，无法恢复！',
      '重置练习记录',
      {
        confirmButtonText: '确定重置',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 清除本地存储的练习记录
    localStorage.removeItem('practiceRecords')
    
    // 重新加载题目列表，清除所有答题状态
    await getQuestionList()
    
    ElMessage.success('所有练习记录已重置')
  } catch (error) {
    // 用户取消操作
    if (error !== 'cancel') {
      console.error('重置练习记录失败：', error)
      ElMessage.error('重置练习记录失败')
    }
  }
}

// 返回正常模式
const backToNormalMode = () => {
  searchForm.collected = false
  
  // 重新自动选择第一个分类
  if (categoryTree.value.length > 0) {
    for (const parentCategory of categoryTree.value) {
      if (parentCategory.children && parentCategory.children.length > 0) {
        selectedCategoryId.value = parentCategory.children[0].id
        break
      }
    }
  }
  
  pagination.current = 1
  getQuestionList()
}

// 初始化
onMounted(async () => {
  // 先获取分类树，分类树加载完成后会自动选择第一个分类并加载题目
  await getCategoryTree()
})
</script>

<style scoped>
.practice-page {
  min-height: 100vh;
  background: #f5f6fa;
}

/* 导航栏样式 */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  padding: 0 40px;
  height: 64px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  position: sticky;
  top: 0;
  z-index: 100;
}

.logo {
  display: flex;
  align-items: center;
}

.logo-img {
  width: 36px;
  height: 36px;
  margin-right: 12px;
}

.title {
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 主体区域 */
.main-content {
  display: flex;
  max-width: 1400px;
  margin: 30px auto 0 auto;
  gap: 30px;
  padding: 0 20px;
}

/* 左侧分类区域 */
.sidebar {
  width: 280px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  min-height: 600px;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.sidebar-header h3 {
  margin: 0;
  color: #333;
}

.category-tree {
  padding: 20px;
}

.custom-tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.category-count {
  color: #999;
  font-size: 12px;
}

/* 右侧内容区域 */
.content-area {
  flex: 1;
  min-width: 0;
}

.search-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.stats-info {
  margin-bottom: 20px;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

/* 收藏模式提示 */
.collected-mode-tip {
  margin-bottom: 20px;
}

/* 题目列表样式 */
.question-list {
  background: transparent;
  padding: 0;
}

.question-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  margin-bottom: 24px;
  border: 1px solid #f0f0f0;
  overflow: hidden;
  transition: all 0.3s ease;
}

.question-card:hover {
  box-shadow: 0 4px 20px rgba(0,0,0,0.12);
  transform: translateY(-2px);
}

.question-card.answered {
  border-left: 4px solid #409eff;
}

.question-card.correct {
  border-left: 4px solid #67c23a;
}

.question-card:last-child {
  margin-bottom: 0;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #eee;
}

.question-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.question-number {
  font-weight: 600;
  color: #409eff;
}

.category-name {
  color: #666;
  font-size: 14px;
}

.question-actions {
  display: flex;
  gap: 8px;
}

.question-content {
  padding: 20px;
}

.question-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  line-height: 1.6;
}

/* 选择题样式 */
.question-choices {
  margin-bottom: 16px;
}

.choice-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 8px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.choice-item:hover {
  background: #e6f7ff;
  border-color: #409eff;
}

.choice-item.selected {
  background: #e6f7ff;
  border: 1px solid #409eff;
}

.choice-item.correct {
  background: #f6ffed;
  border: 1px solid #52c41a;
}

.choice-label {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  background: #409eff;
  color: white;
  border-radius: 50%;
  font-size: 12px;
  font-weight: bold;
  margin-right: 12px;
  flex-shrink: 0;
}

.choice-item.correct .choice-label {
  background: #52c41a;
}

.choice-content {
  flex: 1;
}

.correct-icon {
  color: #52c41a;
  font-size: 18px;
  margin-left: 8px;
}

/* 判断题样式 */
.judge-options {
  margin-bottom: 16px;
}

/* 简答题样式 */
.text-answer {
  margin-bottom: 16px;
}

/* 答案区域样式 */
.answer-section {
  background: #f0f9ff;
  border: 1px solid #b3d8ff;
  border-radius: 8px;
  padding: 16px;
  margin-top: 16px;
}

.answer-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  font-weight: 600;
  color: #1890ff;
}

.answer-content {
  margin-bottom: 16px;
}

.correct-answer {
  color: #52c41a;
  font-weight: 600;
}

.text-answer-content {
  background: white;
  padding: 12px;
  border-radius: 6px;
  border: 1px solid #d9d9d9;
  white-space: pre-wrap;
}

.analysis-section {
  border-top: 1px solid #d9d9d9;
  padding-top: 16px;
}

.analysis-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-weight: 600;
  color: #722ed1;
}

.analysis-content {
  color: #666;
  line-height: 1.6;
}

/* 反馈区域 */
.feedback-section {
  margin-top: 16px;
}

/* 空状态 */
.empty-state {
  padding: 60px 20px;
  text-align: center;
}

/* 分页 */
.pagination {
  margin-top: 30px;
  text-align: center;
}

/* 统计对话框 */
.stats-content {
  padding: 20px 0;
}

.stats-overview {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.stats-detail h4 {
  margin-bottom: 16px;
  color: #333;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .main-content {
    flex-direction: column;
    gap: 20px;
  }
  
  .sidebar {
    width: 100%;
    min-height: auto;
  }
  
  .stats-overview {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .navbar {
    padding: 0 20px;
  }
  
  .nav-actions {
    gap: 8px;
  }
  
  .nav-actions .el-button span {
    display: none;
  }
  
  .search-bar {
    flex-direction: column;
    gap: 12px;
  }
  
  .search-bar .el-input,
  .search-bar .el-select {
    width: 100% !important;
  }
  
  .question-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .question-actions {
    width: 100%;
    justify-content: flex-end;
  }
  
  .stats-overview {
    grid-template-columns: 1fr;
  }
}
</style> 