<template>
  <div class="mock-interview-detail">
    <!-- 页面标题 -->
    <div class="detail-header">
      <el-button @click="$router.go(-1)" icon="el-icon-arrow-left">返回</el-button>
      <h2>模拟面试详情</h2>
    </div>

    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>

    <div v-else class="detail-content">
      <!-- 面试基本信息 -->
      <el-card class="interview-info-card">
        <template #header>
          <span>面试信息</span>
        </template>
        <div class="interview-meta">
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="meta-item">
                <label>面试ID：</label>
                <span>{{ interviewDetail.id }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="meta-item">
                <label>技术方向：</label>
                <el-tag :type="getDirectionType(interviewDetail.direction)">
                  {{ getDirectionLabel(interviewDetail.direction) }}
                </el-tag>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="meta-item">
                <label>开始时间：</label>
                <span>{{ formatDateTime(interviewDetail.startTime) }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="meta-item">
                <label>结束时间：</label>
                <span>{{ formatDateTime(interviewDetail.endTime) }}</span>
              </div>
            </el-col>
          </el-row>
          <el-row :gutter="20" style="margin-top: 20px;">
            <el-col :span="6">
              <div class="meta-item">
                <label>总题数：</label>
                <span>{{ interviewDetail.totalQuestions }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="meta-item">
                <label>完成题数：</label>
                <span>{{ interviewDetail.completedQuestions }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="meta-item">
                <label>总用时：</label>
                <span>{{ interviewDetail.duration }}分钟</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="meta-item">
                <label>状态：</label>
                <el-tag :type="interviewDetail.status === 'completed' ? 'success' : 'warning'">
                  {{ interviewDetail.status === 'completed' ? '已完成' : '进行中' }}
                </el-tag>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-card>

      <!-- 面试成绩 -->
      <el-card class="score-card" v-if="interviewDetail.status === 'completed'">
        <template #header>
          <span>面试成绩</span>
        </template>
        <div class="score-summary">
          <div class="total-score">
            <div class="score-number">{{ interviewDetail.totalScore }}</div>
            <div class="score-label">总分</div>
            <div class="score-max">满分：{{ interviewDetail.maxScore }}</div>
          </div>
          <div class="average-score">
            <div class="score-number">{{ interviewDetail.averageScore }}</div>
            <div class="score-label">平均分</div>
          </div>
        </div>
      </el-card>

      <!-- 答题详情 -->
      <el-card class="answers-card">
        <template #header>
          <span>答题详情</span>
        </template>
        <div class="answers-list">
          <div v-for="(answer, index) in interviewDetail.answers" :key="answer.id" class="answer-item">
            <div class="answer-header">
              <h4>第{{ index + 1 }}题</h4>
              <div class="answer-meta">
                <el-tag size="small" :type="getDirectionType(answer.question.direction)">
                  {{ getDirectionLabel(answer.question.direction) }}
                </el-tag>
                <el-tag size="small" :type="getDifficultyType(answer.question.difficultyLevel)">
                  {{ getDifficultyLabel(answer.question.difficultyLevel) }}
                </el-tag>
                <span class="score">得分：{{ answer.score }} / {{ answer.maxScore }}</span>
              </div>
            </div>
            
            <div class="question-content">
              <h5>题目：</h5>
              <p>{{ answer.question.questionContent }}</p>
            </div>
            
            <div class="user-answer">
              <h5>我的答案：</h5>
              <div class="answer-text">{{ answer.userAnswer }}</div>
              <div v-if="answer.voiceFileUrl" class="voice-answer">
                <h6>语音答案：</h6>
                <audio :src="answer.voiceFileUrl" controls></audio>
              </div>
            </div>
            
            <div class="ai-evaluation">
              <h5>AI评价：</h5>
              <div class="evaluation-content">{{ answer.aiEvaluation }}</div>
              <div class="evaluation-details">
                <div class="detail-item">
                  <span class="label">技术准确性：</span>
                  <el-rate v-model="answer.technicalAccuracy" disabled show-score></el-rate>
                </div>
                <div class="detail-item">
                  <span class="label">表达清晰度：</span>
                  <el-rate v-model="answer.clarity" disabled show-score></el-rate>
                </div>
                <div class="detail-item">
                  <span class="label">逻辑思维：</span>
                  <el-rate v-model="answer.logic" disabled show-score></el-rate>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 面试官总结 -->
      <el-card class="summary-card" v-if="interviewDetail.interviewerSummary">
        <template #header>
          <span>面试官总结</span>
        </template>
        <div class="summary-content">
          <div class="summary-text">{{ interviewDetail.interviewerSummary }}</div>
          <div class="improvement-suggestions">
            <h5>改进建议：</h5>
            <ul>
              <li v-for="(suggestion, index) in interviewDetail.improvementSuggestions" :key="index">
                {{ suggestion }}
              </li>
            </ul>
          </div>
        </div>
      </el-card>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button type="primary" @click="handleRetryInterview">重新面试</el-button>
        <el-button @click="handleViewResult">查看结果</el-button>
        <el-button @click="$router.push('/interview-questions')">返回真题列表</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMockInterviewDetail } from '@/api/interviewQuestion'

export default {
  name: 'MockInterviewDetail',
  setup() {
    const route = useRoute()
    const router = useRouter()
    
    // 响应式数据
    const loading = ref(false)
    const interviewDetail = ref({
      id: '',
      direction: '',
      startTime: '',
      endTime: '',
      totalQuestions: 0,
      completedQuestions: 0,
      duration: 0,
      status: '',
      totalScore: 0,
      maxScore: 0,
      averageScore: 0,
      answers: [],
      interviewerSummary: '',
      improvementSuggestions: []
    })
    
    // 获取面试详情
    const fetchInterviewDetail = async () => {
      loading.value = true
      try {
        const response = await getMockInterviewDetail(route.params.id)
        interviewDetail.value = response.data
      } catch (error) {
        ElMessage.error('获取面试详情失败')
      } finally {
        loading.value = false
      }
    }
    
    // 重新面试
    const handleRetryInterview = () => {
      router.push('/mock-interview')
    }
    
    // 查看结果
    const handleViewResult = () => {
      router.push(`/interview-result/${route.params.id}`)
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
    
    const formatDateTime = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN')
    }
    
    // 生命周期
    onMounted(() => {
      fetchInterviewDetail()
    })
    
    return {
      loading,
      interviewDetail,
      handleRetryInterview,
      handleViewResult,
      getDirectionType,
      getDirectionLabel,
      getDifficultyType,
      getDifficultyLabel,
      formatDateTime
    }
  }
}
</script>

<style scoped>
.mock-interview-detail {
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

.interview-meta {
  margin-bottom: 20px;
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

.score-summary {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 20px;
}

.total-score, .average-score {
  text-align: center;
}

.score-number {
  font-size: 36px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.score-label {
  font-size: 16px;
  color: #606266;
  margin-bottom: 5px;
}

.score-max {
  font-size: 14px;
  color: #909399;
}

.answers-list {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.answer-item {
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fafafa;
}

.answer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.answer-header h4 {
  margin: 0;
  color: #303133;
}

.answer-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.score {
  font-weight: 500;
  color: #409eff;
}

.question-content h5,
.user-answer h5,
.ai-evaluation h5 {
  margin-bottom: 10px;
  color: #303133;
  font-size: 14px;
}

.question-content p {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 15px;
}

.answer-text {
  background: white;
  padding: 15px;
  border-radius: 6px;
  border: 1px solid #dcdfe6;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 15px;
}

.voice-answer {
  margin-top: 10px;
}

.voice-answer h6 {
  margin-bottom: 8px;
  color: #606266;
  font-size: 13px;
}

.evaluation-content {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 6px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 15px;
}

.evaluation-details {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.detail-item .label {
  min-width: 100px;
  color: #606266;
  font-size: 13px;
}

.summary-content {
  padding: 20px;
}

.summary-text {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 20px;
}

.improvement-suggestions h5 {
  margin-bottom: 10px;
  color: #303133;
}

.improvement-suggestions ul {
  padding-left: 20px;
}

.improvement-suggestions li {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 8px;
}

.action-buttons {
  text-align: center;
  margin-top: 20px;
}
</style> 