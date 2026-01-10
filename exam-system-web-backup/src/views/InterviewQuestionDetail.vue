<template>
  <div class="interview-question-detail">
    <div class="detail-header">
      <el-button @click="$router.go(-1)" icon="el-icon-arrow-left">返回</el-button>
      <h2>真题详情</h2>
    </div>
    
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>
    
    <div v-else class="detail-content">
      <!-- 题目基本信息 -->
      <el-card class="question-info-card">
        <template #header>
          <div class="card-header">
            <span>题目信息</span>
            <div class="header-actions">
              <el-button type="primary" @click="handleStartPractice">开始练习</el-button>
              <el-button @click="handleToggleFavorite">
                <i :class="isFavorited ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
                {{ isFavorited ? '取消收藏' : '收藏' }}
              </el-button>
            </div>
          </div>
        </template>
        
        <div class="question-meta">
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="meta-item">
                <label>技术方向：</label>
                <el-tag :type="getDirectionType(question.direction)">
                  {{ getDirectionLabel(question.direction) }}
                </el-tag>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="meta-item">
                <label>公司名称：</label>
                <span>{{ question.companyName }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="meta-item">
                <label>面试年份：</label>
                <span>{{ question.interviewYear }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="meta-item">
                <label>难度级别：</label>
                <el-tag :type="getDifficultyType(question.difficultyLevel)">
                  {{ getDifficultyLabel(question.difficultyLevel) }}
                </el-tag>
              </div>
            </el-col>
          </el-row>
          
          <el-row :gutter="20" style="margin-top: 20px;">
            <el-col :span="6">
              <div class="meta-item">
                <label>浏览次数：</label>
                <span>{{ question.viewCount }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="meta-item">
                <label>收藏次数：</label>
                <span>{{ question.favoriteCount }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="meta-item">
                <label>上传时间：</label>
                <span>{{ formatDate(question.createdTime) }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="meta-item">
                <label>状态：</label>
                <el-tag :type="question.status === 'approved' ? 'success' : 'warning'">
                  {{ question.status === 'approved' ? '已审核' : '待审核' }}
                </el-tag>
              </div>
            </el-col>
          </el-row>
        </div>
        
        <div class="question-content">
          <h3>题目内容</h3>
          <div class="content-text">{{ question.questionContent }}</div>
        </div>
      </el-card>
      
      <!-- 参考答案 -->
      <el-card class="answer-card" v-if="question.referenceAnswer">
        <template #header>
          <span>参考答案</span>
        </template>
        
        <div class="answer-content">
          <div class="answer-text">{{ question.referenceAnswer }}</div>
        </div>
      </el-card>
      
      <!-- 用户评价 -->
      <el-card class="evaluation-card">
        <template #header>
          <span>用户评价</span>
        </template>
        
        <div class="evaluation-stats">
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="stat-item">
                <div class="stat-number">{{ evaluationStats.averageRating }}</div>
                <div class="stat-label">平均评分</div>
                <el-rate v-model="evaluationStats.averageRating" disabled show-score></el-rate>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-item">
                <div class="stat-number">{{ evaluationStats.totalEvaluations }}</div>
                <div class="stat-label">评价总数</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-item">
                <div class="stat-number">{{ evaluationStats.difficultyRating }}</div>
                <div class="stat-label">难度评分</div>
                <el-rate v-model="evaluationStats.difficultyRating" disabled show-score></el-rate>
              </div>
            </el-col>
          </el-row>
        </div>
        
        <!-- 评价列表 -->
        <div class="evaluation-list" v-if="evaluations.length > 0">
          <h4>详细评价</h4>
          <div v-for="evaluation in evaluations" :key="evaluation.id" class="evaluation-item">
            <div class="evaluation-header">
              <div class="user-info">
                <span class="username">{{ evaluation.userName }}</span>
                <span class="time">{{ formatDate(evaluation.createdTime) }}</span>
              </div>
              <div class="rating">
                <el-rate v-model="evaluation.rating" disabled></el-rate>
              </div>
            </div>
            <div class="evaluation-content">{{ evaluation.comment }}</div>
          </div>
        </div>
        
        <!-- 添加评价 -->
        <div class="add-evaluation">
          <h4>添加评价</h4>
          <el-form :model="newEvaluation" :rules="evaluationRules" ref="evaluationFormRef">
            <el-form-item label="评分" prop="rating">
              <el-rate v-model="newEvaluation.rating" show-score></el-rate>
            </el-form-item>
            <el-form-item label="评价内容" prop="comment">
              <el-input 
                v-model="newEvaluation.comment" 
                type="textarea" 
                :rows="3" 
                placeholder="请分享你的看法..."
              ></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSubmitEvaluation" :loading="submitting">
                提交评价
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-card>
      
      <!-- 相关题目推荐 -->
      <el-card class="related-card" v-if="relatedQuestions.length > 0">
        <template #header>
          <span>相关题目推荐</span>
        </template>
        
        <div class="related-list">
          <div 
            v-for="related in relatedQuestions" 
            :key="related.id" 
            class="related-item"
            @click="handleViewRelated(related.id)"
          >
            <div class="related-title">{{ related.questionContent }}</div>
            <div class="related-meta">
              <el-tag size="small" :type="getDirectionType(related.direction)">
                {{ getDirectionLabel(related.direction) }}
              </el-tag>
              <el-tag size="small" :type="getDifficultyType(related.difficultyLevel)">
                {{ getDifficultyLabel(related.difficultyLevel) }}
              </el-tag>
              <span class="company">{{ related.companyName }}</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  getInterviewQuestionDetail, 
  incrementViewCount,
  getRelatedQuestions,
  submitEvaluation,
  toggleFavorite
} from '@/api/interviewQuestion'

export default {
  name: 'InterviewQuestionDetail',
  setup() {
    const route = useRoute()
    const router = useRouter()
    
    // 响应式数据
    const loading = ref(false)
    const submitting = ref(false)
    const question = ref({})
    const isFavorited = ref(false)
    
    // 评价相关
    const evaluationFormRef = ref()
    const evaluationStats = reactive({
      averageRating: 0,
      totalEvaluations: 0,
      difficultyRating: 0
    })
    const evaluations = ref([])
    const newEvaluation = reactive({
      rating: 0,
      comment: ''
    })
    
    const evaluationRules = {
      rating: [{ required: true, message: '请选择评分', trigger: 'change' }],
      comment: [{ required: true, message: '请输入评价内容', trigger: 'blur' }]
    }
    
    // 相关题目
    const relatedQuestions = ref([])
    
    // 获取题目详情
    const fetchQuestionDetail = async () => {
      loading.value = true
      try {
        const response = await getInterviewQuestionDetail(route.params.id)
        question.value = response.data
        isFavorited.value = response.data.isFavorited || false
        
        // 增加浏览次数
        await incrementViewCount(route.params.id)
        
        // 获取相关题目
        await fetchRelatedQuestions()
      } catch (error) {
        ElMessage.error('获取题目详情失败')
      } finally {
        loading.value = false
      }
    }
    
    // 获取相关题目
    const fetchRelatedQuestions = async () => {
      try {
        const response = await getRelatedQuestions(route.params.id, {
          direction: question.value.direction,
          limit: 5
        })
        relatedQuestions.value = response.data
      } catch (error) {
        console.error('获取相关题目失败:', error)
      }
    }
    
    // 开始练习
    const handleStartPractice = () => {
      router.push(`/interview-practice/${route.params.id}`)
    }
    
    // 收藏/取消收藏
    const handleToggleFavorite = async () => {
      try {
        await toggleFavorite(route.params.id)
        isFavorited.value = !isFavorited.value
        question.value.favoriteCount += isFavorited.value ? 1 : -1
        ElMessage.success(isFavorited.value ? '收藏成功' : '取消收藏成功')
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
    
    // 提交评价
    const handleSubmitEvaluation = async () => {
      try {
        await evaluationFormRef.value.validate()
        submitting.value = true
        
        await submitEvaluation({
          questionId: route.params.id,
          rating: newEvaluation.rating,
          comment: newEvaluation.comment
        })
        
        ElMessage.success('评价提交成功')
        newEvaluation.rating = 0
        newEvaluation.comment = ''
        
        // 重新获取评价数据
        // await fetchEvaluations()
      } catch (error) {
        ElMessage.error('提交评价失败')
      } finally {
        submitting.value = false
      }
    }
    
    // 查看相关题目
    const handleViewRelated = (id) => {
      router.push(`/interview-questions/${id}`)
    }
    
    // 工具函数
    const getDirectionType = (direction) => {
      const types = {
        java: 'primary',
        frontend: 'success',
        bigdata: 'warning',
        algorithm: 'danger',
        devops: 'info',
        testing: ''
      }
      return types[direction] || ''
    }
    
    const getDirectionLabel = (direction) => {
      const labels = {
        java: 'Java',
        frontend: '前端',
        bigdata: '大数据',
        algorithm: '算法',
        devops: '运维',
        testing: '测试'
      }
      return labels[direction] || direction
    }
    
    const getDifficultyType = (difficulty) => {
      const types = {
        easy: 'success',
        medium: 'warning',
        hard: 'danger'
      }
      return types[difficulty] || ''
    }
    
    const getDifficultyLabel = (difficulty) => {
      const labels = {
        easy: '简单',
        medium: '中等',
        hard: '困难'
      }
      return labels[difficulty] || difficulty
    }
    
    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN')
    }
    
    // 生命周期
    onMounted(() => {
      fetchQuestionDetail()
    })
    
    return {
      loading,
      submitting,
      question,
      isFavorited,
      evaluationFormRef,
      evaluationStats,
      evaluations,
      newEvaluation,
      evaluationRules,
      relatedQuestions,
      handleStartPractice,
      handleToggleFavorite,
      handleSubmitEvaluation,
      handleViewRelated,
      getDirectionType,
      getDirectionLabel,
      getDifficultyType,
      getDifficultyLabel,
      formatDate
    }
  }
}
</script>

<style scoped>
.interview-question-detail {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.detail-header h2 {
  margin: 0;
  color: #303133;
}

.loading-container {
  padding: 40px;
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.question-meta {
  margin-bottom: 30px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 15px;
}

.meta-item label {
  font-weight: 500;
  color: #606266;
  min-width: 80px;
}

.question-content h3 {
  margin-bottom: 15px;
  color: #303133;
}

.content-text {
  line-height: 1.6;
  color: #606266;
  font-size: 16px;
}

.answer-content {
  padding: 15px;
  background: #f8f9fa;
  border-radius: 6px;
}

.answer-text {
  line-height: 1.6;
  color: #606266;
}

.evaluation-stats {
  margin-bottom: 30px;
}

.stat-item {
  text-align: center;
  padding: 20px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 5px;
}

.stat-label {
  color: #606266;
  font-size: 14px;
  margin-bottom: 10px;
}

.evaluation-list {
  margin-bottom: 30px;
}

.evaluation-list h4 {
  margin-bottom: 15px;
  color: #303133;
}

.evaluation-item {
  padding: 15px;
  border-bottom: 1px solid #ebeef5;
}

.evaluation-item:last-child {
  border-bottom: none;
}

.evaluation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.username {
  font-weight: 500;
  color: #303133;
}

.time {
  font-size: 12px;
  color: #909399;
}

.evaluation-content {
  line-height: 1.5;
  color: #606266;
}

.add-evaluation h4 {
  margin-bottom: 15px;
  color: #303133;
}

.related-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.related-item {
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.related-item:hover {
  border-color: #409eff;
  background: #f8f9fa;
}

.related-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 10px;
  line-height: 1.4;
}

.related-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.company {
  color: #909399;
  font-size: 12px;
}
</style> 