<template>
  <div class="interview-result">
    <!-- 页面标题 -->
    <div class="result-header">
      <el-button @click="$router.go(-1)" icon="el-icon-arrow-left">返回</el-button>
      <h2>面试结果</h2>
    </div>

    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="8" animated />
    </div>

    <div v-else class="result-content">
      <!-- 成绩概览 -->
      <el-card class="score-overview-card">
        <div class="score-overview">
          <div class="main-score">
            <div class="score-circle">
              <div class="score-number">{{ interviewResult.totalScore }}</div>
              <div class="score-label">总分</div>
              <div class="score-max">满分：{{ interviewResult.maxScore }}</div>
            </div>
          </div>
          <div class="score-details">
            <div class="detail-item">
              <div class="detail-number">{{ interviewResult.averageScore }}</div>
              <div class="detail-label">平均分</div>
            </div>
            <div class="detail-item">
              <div class="detail-number">{{ interviewResult.completedQuestions }}</div>
              <div class="detail-label">完成题数</div>
            </div>
            <div class="detail-item">
              <div class="detail-number">{{ interviewResult.duration }}</div>
              <div class="detail-label">用时(分钟)</div>
            </div>
            <div class="detail-item">
              <div class="detail-number">{{ getScoreLevel(interviewResult.totalScore) }}</div>
              <div class="detail-label">成绩等级</div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 能力雷达图 -->
      <el-card class="radar-card">
        <template #header>
          <span>能力分析</span>
        </template>
        <div class="radar-chart" ref="radarChartRef"></div>
      </el-card>

      <!-- 各题得分详情 -->
      <el-card class="questions-card">
        <template #header>
          <span>各题得分详情</span>
        </template>
        <el-table :data="interviewResult.answers" stripe>
          <el-table-column prop="questionIndex" label="题号" width="80">
            <template #default="{ row, $index }">
              第{{ $index + 1 }}题
            </template>
          </el-table-column>
          <el-table-column prop="question.questionContent" label="题目" min-width="300">
            <template #default="{ row }">
              <div class="question-content">
                <div class="question-text">{{ row.question.questionContent }}</div>
                <div class="question-meta">
                  <el-tag size="small" :type="getDirectionType(row.question.direction)">
                    {{ getDirectionLabel(row.question.direction) }}
                  </el-tag>
                  <el-tag size="small" :type="getDifficultyType(row.question.difficultyLevel)">
                    {{ getDifficultyLabel(row.question.difficultyLevel) }}
                  </el-tag>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="score" label="得分" width="120">
            <template #default="{ row }">
              <span class="score-text">{{ row.score }} / {{ row.maxScore }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="scoreRate" label="得分率" width="120">
            <template #default="{ row }">
              <el-progress 
                :percentage="(row.score / row.maxScore) * 100" 
                :color="getProgressColor((row.score / row.maxScore) * 100)"
              ></el-progress>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button size="small" @click="handleViewAnswer(row)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 面试官点评 -->
      <el-card class="feedback-card" v-if="interviewResult.interviewerFeedback">
        <template #header>
          <span>面试官点评</span>
        </template>
        <div class="feedback-content">
          <div class="feedback-summary">
            <h4>总体评价</h4>
            <p>{{ interviewResult.interviewerFeedback.summary }}</p>
          </div>
          <div class="feedback-strengths">
            <h4>优势表现</h4>
            <ul>
              <li v-for="(strength, index) in interviewResult.interviewerFeedback.strengths" :key="index">
                {{ strength }}
              </li>
            </ul>
          </div>
          <div class="feedback-improvements">
            <h4>改进建议</h4>
            <ul>
              <li v-for="(improvement, index) in interviewResult.interviewerFeedback.improvements" :key="index">
                {{ improvement }}
              </li>
            </ul>
          </div>
        </div>
      </el-card>

      <!-- 学习建议 -->
      <el-card class="suggestions-card">
        <template #header>
          <span>学习建议</span>
        </template>
        <div class="suggestions-content">
          <div class="suggestion-item" v-for="(suggestion, index) in interviewResult.learningSuggestions" :key="index">
            <div class="suggestion-icon">
              <el-icon><Lightbulb /></el-icon>
            </div>
            <div class="suggestion-content">
              <h5>{{ suggestion.title }}</h5>
              <p>{{ suggestion.content }}</p>
              <div class="suggestion-resources" v-if="suggestion.resources">
                <span>推荐资源：</span>
                <el-tag 
                  v-for="resource in suggestion.resources" 
                  :key="resource.id" 
                  size="small" 
                  @click="handleViewResource(resource)"
                  style="cursor: pointer; margin-left: 5px;"
                >
                  {{ resource.name }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button type="primary" @click="handleRetryInterview">重新面试</el-button>
        <el-button @click="handleViewDetail">查看详情</el-button>
        <el-button @click="handleShareResult">分享结果</el-button>
        <el-button @click="$router.push('/interview-questions')">返回真题列表</el-button>
      </div>
    </div>

    <!-- 答题详情对话框 -->
    <el-dialog v-model="answerDetailVisible" title="答题详情" width="800px">
      <div v-if="selectedAnswer" class="answer-detail">
        <div class="question-section">
          <h4>题目：</h4>
          <p>{{ selectedAnswer.question.questionContent }}</p>
        </div>
        <div class="answer-section">
          <h4>我的答案：</h4>
          <div class="answer-text">{{ selectedAnswer.userAnswer }}</div>
          <div v-if="selectedAnswer.voiceFileUrl" class="voice-answer">
            <h5>语音答案：</h5>
            <audio :src="selectedAnswer.voiceFileUrl" controls></audio>
          </div>
        </div>
        <div class="evaluation-section">
          <h4>AI评价：</h4>
          <div class="evaluation-text">{{ selectedAnswer.aiEvaluation }}</div>
          <div class="evaluation-scores">
            <div class="score-item">
              <span>技术准确性：</span>
              <el-rate v-model="selectedAnswer.technicalAccuracy" disabled show-score></el-rate>
            </div>
            <div class="score-item">
              <span>表达清晰度：</span>
              <el-rate v-model="selectedAnswer.clarity" disabled show-score></el-rate>
            </div>
            <div class="score-item">
              <span>逻辑思维：</span>
              <el-rate v-model="selectedAnswer.logic" disabled show-score></el-rate>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lightbulb } from '@element-plus/icons-vue'
import { getMockInterviewDetail } from '@/api/interviewQuestion'
import * as echarts from 'echarts'

export default {
  name: 'InterviewResult',
  components: {
    Lightbulb
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    
    // 响应式数据
    const loading = ref(false)
    const interviewResult = ref({
      id: '',
      totalScore: 0,
      maxScore: 0,
      averageScore: 0,
      completedQuestions: 0,
      duration: 0,
      answers: [],
      interviewerFeedback: null,
      learningSuggestions: [],
      abilityScores: {}
    })
    
    // 雷达图相关
    const radarChartRef = ref(null)
    const radarChart = ref(null)
    
    // 答题详情对话框
    const answerDetailVisible = ref(false)
    const selectedAnswer = ref(null)
    
    // 获取面试结果
    const fetchInterviewResult = async () => {
      loading.value = true
      try {
        const response = await getMockInterviewDetail(route.params.id)
        interviewResult.value = response.data
        await nextTick()
        initRadarChart()
      } catch (error) {
        ElMessage.error('获取面试结果失败')
      } finally {
        loading.value = false
      }
    }
    
    // 初始化雷达图
    const initRadarChart = () => {
      if (!radarChartRef.value) return
      
      radarChart.value = echarts.init(radarChartRef.value)
      const option = {
        radar: {
          indicator: [
            { name: '技术准确性', max: 100 },
            { name: '表达清晰度', max: 100 },
            { name: '逻辑思维', max: 100 },
            { name: '知识广度', max: 100 },
            { name: '实践经验', max: 100 }
          ]
        },
        series: [{
          type: 'radar',
          data: [{
            value: [
              interviewResult.value.abilityScores.technicalAccuracy || 0,
              interviewResult.value.abilityScores.clarity || 0,
              interviewResult.value.abilityScores.logic || 0,
              interviewResult.value.abilityScores.knowledge || 0,
              interviewResult.value.abilityScores.experience || 0
            ],
            name: '能力评分'
          }]
        }]
      }
      radarChart.value.setOption(option)
    }
    
    // 查看答题详情
    const handleViewAnswer = (answer) => {
      selectedAnswer.value = answer
      answerDetailVisible.value = true
    }
    
    // 重新面试
    const handleRetryInterview = () => {
      router.push('/mock-interview')
    }
    
    // 查看详情
    const handleViewDetail = () => {
      router.push(`/mock-interview/${route.params.id}`)
    }
    
    // 分享结果
    const handleShareResult = () => {
      // 实现分享功能
      ElMessage.success('分享功能开发中...')
    }
    
    // 查看资源
    const handleViewResource = (resource) => {
      // 跳转到资源页面
      router.push(`/resource/${resource.id}`)
    }
    
    // 工具函数
    const getScoreLevel = (score) => {
      if (score >= 90) return '优秀'
      if (score >= 80) return '良好'
      if (score >= 70) return '中等'
      if (score >= 60) return '及格'
      return '不及格'
    }
    
    const getProgressColor = (percentage) => {
      if (percentage >= 80) return '#67c23a'
      if (percentage >= 60) return '#e6a23c'
      return '#f56c6c'
    }
    
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
    
    // 生命周期
    onMounted(() => {
      fetchInterviewResult()
    })
    
    return {
      loading,
      interviewResult,
      radarChartRef,
      answerDetailVisible,
      selectedAnswer,
      handleViewAnswer,
      handleRetryInterview,
      handleViewDetail,
      handleShareResult,
      handleViewResource,
      getScoreLevel,
      getProgressColor,
      getDirectionType,
      getDirectionLabel,
      getDifficultyType,
      getDifficultyLabel
    }
  }
}
</script>

<style scoped>
.interview-result {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.result-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.result-header h2 {
  margin: 0;
  color: #303133;
}

.loading-container {
  padding: 40px;
}

.result-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.score-overview {
  display: flex;
  align-items: center;
  gap: 40px;
}

.main-score {
  flex-shrink: 0;
}

.score-circle {
  text-align: center;
  padding: 30px;
  border: 3px solid #409eff;
  border-radius: 50%;
  width: 150px;
  height: 150px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.score-number {
  font-size: 36px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 5px;
}

.score-label {
  font-size: 16px;
  color: #606266;
  margin-bottom: 5px;
}

.score-max {
  font-size: 12px;
  color: #909399;
}

.score-details {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  flex: 1;
}

.detail-item {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.detail-number {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 5px;
}

.detail-label {
  color: #606266;
  font-size: 14px;
}

.radar-chart {
  height: 400px;
  width: 100%;
}

.question-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.question-text {
  color: #303133;
  line-height: 1.4;
}

.question-meta {
  display: flex;
  gap: 8px;
}

.score-text {
  font-weight: 500;
  color: #409eff;
}

.feedback-content {
  padding: 20px;
}

.feedback-content h4 {
  margin-bottom: 10px;
  color: #303133;
}

.feedback-content p {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 20px;
}

.feedback-content ul {
  padding-left: 20px;
  margin-bottom: 20px;
}

.feedback-content li {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 8px;
}

.suggestions-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.suggestion-item {
  display: flex;
  gap: 15px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.suggestion-icon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  background: #409eff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.suggestion-content h5 {
  margin-bottom: 8px;
  color: #303133;
}

.suggestion-content p {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 10px;
}

.suggestion-resources {
  color: #909399;
  font-size: 13px;
}

.action-buttons {
  text-align: center;
  margin-top: 20px;
}

.answer-detail {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.answer-detail h4 {
  margin-bottom: 10px;
  color: #303133;
}

.answer-text {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 6px;
  color: #606266;
  line-height: 1.6;
}

.voice-answer h5 {
  margin-bottom: 8px;
  color: #606266;
}

.evaluation-text {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 6px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 15px;
}

.evaluation-scores {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.score-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.score-item span {
  min-width: 100px;
  color: #606266;
}
</style> 