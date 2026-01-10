<template>
  <div class="exam-result-page">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading"><Loading /></el-icon>
      <p>正在加载考试结果，请稍候...</p>
    </div>

    <!-- 主要内容 -->
    <div v-else-if="examRecord" class="result-container">
      <!-- 考试完成提示 -->
      <div class="completion-notice">
        <div class="notice-content">
          <el-icon class="notice-icon"><SuccessFilled /></el-icon>
          <div class="notice-text">
            <h4>🎉 考试已完成</h4>
            <p>您的答卷已成功提交并批阅完成。请查看下方的详细成绩和分析报告。</p>
            <p class="notice-tip">💡 考试已结束，无法再次修改答案。如需重新挑战，请返回考试列表选择新的考试。</p>
          </div>
        </div>
      </div>
      
      <!-- 考试基本信息 -->
      <div class="result-header">
        <div class="header-left">
          <h2>🎓 考试结果</h2>
          <div class="exam-info">
            <p><strong>📋 试卷名称：</strong>{{ examRecord.paper.name }}</p>
            <p v-if="examRecord.studentName"><strong>👤 考生姓名：</strong>{{ examRecord.studentName }}</p>
            <p><strong>📅 考试时间：</strong>{{ formatDateTime(examRecord.startTime) }}</p>
          </div>
        </div>
        <div class="header-right">
          <el-tag :type="getStatusTagType(examRecord.status)" size="large">
            {{ getStatusText(examRecord.status) }}
          </el-tag>
        </div>
      </div>

      <!-- 分数展示区域（合并分数和排名卡片） -->
      <div class="score-section">
        <div class="score-rank-card">
          <div class="score-rank-main">
            <!-- 分数区块 -->
            <div class="score-block">
              <div class="score-badge">
                <div class="score-icon">🏆</div>
                <div class="score-main">
                  <div class="score-label">考试成绩</div>
                  <div class="score-number">
                    <span class="score-value">{{ examRecord.score || 0 }}</span>
                    <span class="score-divider">/</span>
                    <span class="score-total">{{ examRecord.paper.totalScore }}</span>
                  </div>
                  <div class="score-percentage">{{ scorePercentage }}%</div>
                  <div class="score-grade">{{ getGradeText(scorePercentage) }}</div>
                </div>
              </div>
            </div>
            <!-- 排名区块优化版 -->
            <div v-if="rankingInfo" class="rank-block">
              <div class="rank-top">
                <div class="trophy-icon" :class="getTrophyClass()">{{ getTrophyEmoji() }}</div>
                <div class="rank-number-group">
                  <span class="rank-number-large">{{ rankingInfo.currentRank }}</span>
                  <span class="rank-suffix">名</span>
                </div>
                <div class="rank-title">本试卷排名</div>
              </div>
              <div class="rank-info">
                <div class="rank-participants">共 {{ rankingInfo.totalParticipants }} 人参与</div>
                <div class="rank-subtitle">{{ getRankingSubtitle() }}</div>
                <div class="rank-progress-bar">
                  <div class="progress-bar-inner" :style="{ width: `${100 - (rankingInfo.currentRank / rankingInfo.totalParticipants) * 100}%` }"></div>
                </div>
                <div class="rank-progress-tip">{{ getProgressTip() }}</div>
              </div>
            </div>
          </div>
          <!-- 激励与操作按钮合并到底部 -->
          <div class="score-rank-footer">
            <div v-if="rankingInfo" class="motivation-section">
              <div class="motivation-message" :class="getMotivationClass()">
                <span class="motivation-icon">{{ getMotivationIcon() }}</span>
                <span class="motivation-text">{{ getMotivationMessage() }}</span>
              </div>
            </div>
            <div class="challenge-actions">
              <el-button type="primary" size="small" @click="retakeChallenge" class="challenge-btn">🚀 再次挑战</el-button>
              <el-button type="success" size="small" @click="viewRanking" class="ranking-btn">🏆 查看排行榜</el-button>
            </div>
          </div>
        </div>
        <!-- 详细信息卡片保持原有结构 -->
        <div class="score-details">
          <div class="detail-item">
            <div class="detail-icon">📊</div>
            <div class="detail-info">
              <div class="detail-label">得分率</div>
              <div class="detail-value">{{ scorePercentage }}%</div>
            </div>
          </div>
          <div class="detail-item">
            <div class="detail-icon">📝</div>
            <div class="detail-info">
              <div class="detail-label">题目总数</div>
              <div class="detail-value">{{ actualQuestionCount }} 道</div>
            </div>
          </div>
          <div class="detail-item">
            <div class="detail-icon">✅</div>
            <div class="detail-info">
              <div class="detail-label">答对题数</div>
              <div class="detail-value">{{ correctCount }} 道</div>
            </div>
          </div>
          <div class="detail-item">
            <div class="detail-icon">❌</div>
            <div class="detail-info">
              <div class="detail-label">错题数</div>
              <div class="detail-value">{{ wrongCount }} 道</div>
            </div>
          </div>
          <div class="detail-item">
            <div class="detail-icon">🔶</div>
            <div class="detail-info">
              <div class="detail-label">部分正确</div>
              <div class="detail-value">{{ partialCount }} 道</div>
            </div>
          </div>
          <div class="detail-item">
            <div class="detail-icon">⏰</div>
            <div class="detail-info">
              <div class="detail-label">用时</div>
              <div class="detail-value">{{ examDuration }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <!-- 返回首页按钮 -->
        <el-button 
          type="primary" 
          size="large"
          @click="goHome"
          icon="HomeFilled"
        >
          🏠 返回首页
        </el-button>
        
        <!-- 查看考试列表按钮 -->
        <el-button 
          type="info" 
          size="large"
          @click="goToExamList"
          icon="List"
        >
          📝 考试列表
        </el-button>
        
        <el-button 
          v-if="examRecord.status === '已批阅'" 
          type="success" 
          size="large"
          @click="downloadResult"
          icon="Download"
        >
          �� 下载成绩单
        </el-button>
      </div>

      <!-- AI考试总评 -->
      <div v-if="examRecord.status === '已批阅' && examRecord.answers" class="ai-summary">
        <h3>🤖 AI考试总评</h3>
        <div class="summary-content">
          <p>{{ examRecord.answers }}</p>
        </div>
      </div>

      <!-- 详细答题结果 -->
      <div v-if="examRecord.status === '已批阅'" class="detailed-results">
        <h3>📋 答题详情</h3>
        <div v-for="(answerRecord, index) in examRecord.answerRecords" :key="answerRecord.id" class="question-result-card">
          <div class="question-header">
            <div class="question-number">第{{ index + 1 }}题</div>
            <div class="question-score" :class="getScoreClassByRecord(answerRecord)">
              {{ answerRecord.score || 0 }} / {{ getQuestionMaxScore(answerRecord.questionId) }} 分
            </div>
          </div>
          
          <div class="question-content">
            <div class="question-title">
              <span class="question-type">{{ getQuestionTypeByRecord(answerRecord) }}</span>
              {{ getQuestionTitleByRecord(answerRecord) }}
            </div>
            
            <!-- 选择题选项展示 -->
            <div v-if="getQuestionByRecord(answerRecord)?.type === 'CHOICE' && getQuestionByRecord(answerRecord)?.choices" class="question-choices">
              <div class="choices-grid">
                <div v-for="(choice, idx) in getQuestionByRecord(answerRecord).choices" :key="idx" class="choice-item">
                  <div class="choice-label">{{ String.fromCharCode(65 + idx) }}</div>
                  <div class="choice-content">{{ choice.content }}</div>
                </div>
              </div>
            </div>
            
            <div class="answer-section">
              <div class="user-answer">
                <strong>💭 你的答案：</strong>
                <span class="answer-text">{{ getFormattedUserAnswer(answerRecord) }}</span>
              </div>
              <div class="correct-answer">
                <strong>✅ 标准答案：</strong>
                <span class="answer-text correct">{{ getFormattedCorrectAnswer(answerRecord) }}</span>
              </div>
            </div>
            
            <!-- AI评语 - 只有简答题才显示 -->
            <div v-if="answerRecord.aiCorrection && getQuestionByRecord(answerRecord)?.type === 'TEXT'" class="ai-feedback">
              <div class="feedback-header">
                <el-icon><ChatDotRound /></el-icon>
                <strong>AI评语</strong>
              </div>
              <p class="feedback-content">{{ answerRecord.aiCorrection }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 成绩单下载区域（隐藏） -->
    <div ref="downloadArea" class="download-area" style="position: absolute; left: -9999px; top: -9999px;">
      <div class="result-certificate">
        <div class="certificate-header">
          <h1>🎓 考试成绩单</h1>
          <div class="certificate-decoration"></div>
        </div>
        
        <div class="certificate-body">
          <div class="student-info">
            <h2>{{ examRecord?.studentName || '考生' }}</h2>
            <p>于 {{ formatDateTime(examRecord?.endTime) }} 完成</p>
          </div>
          
          <div class="exam-details">
            <h3>{{ examRecord?.paper?.name }}</h3>
            <div class="score-display-cert" :class="scoreClass">
              <div class="score-main">{{ examRecord?.score || 0 }} / {{ examRecord?.paper?.totalScore }}</div>
              <div class="score-percentage-cert">{{ scorePercentage }}%</div>
            </div>
          </div>
          
          <div class="performance-stats">
            <div class="stat-item">
              <span>题目总数</span>
              <span>{{ actualQuestionCount }} 道</span>
            </div>
            <div class="stat-item">
              <span>答对题数</span>
              <span>{{ correctCount }} 道</span>
            </div>
            <div class="stat-item">
              <span>用时</span>
              <span>{{ examDuration }}</span>
            </div>
          </div>
          
          <div v-if="examRecord?.answers" class="ai-summary-cert">
            <h4>🤖 AI学习建议</h4>
            <p>{{ examRecord.answers }}</p>
          </div>
        </div>
        
        <div class="certificate-footer">
          <p>智能学习平台 · AI驱动</p>
          <p>{{ new Date().toLocaleDateString() }} 生成</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Loading, ChatDotRound, Download, Setting, HomeFilled, List, SuccessFilled } from '@element-plus/icons-vue'
import { getExamRecordById } from '../api/exam.js'
import html2canvas from 'html2canvas'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const examRecord = ref(null)
const downloadArea = ref(null)
const rankingInfo = ref(null)

// 计算属性
const scorePercentage = computed(() => {
  if (!examRecord.value || !examRecord.value.paper) return 0
  return Math.round((examRecord.value.score / examRecord.value.paper.totalScore) * 100)
})

const scoreClass = computed(() => {
  const percentage = scorePercentage.value
  if (percentage >= 90) return 'excellent'
  if (percentage >= 80) return 'good'
  if (percentage >= 60) return 'pass'
  return 'fail'
})

const correctCount = computed(() => {
  if (!examRecord.value?.answerRecords) return 0
  return examRecord.value.answerRecords.filter(record => record.isCorrect === 1).length
})

// 实际答题的题目数量
const actualQuestionCount = computed(() => {
  if (!examRecord.value?.answerRecords) return 0
  return examRecord.value.answerRecords.length
})

// 错题数量
const wrongCount = computed(() => {
  if (!examRecord.value?.answerRecords) return 0
  return examRecord.value.answerRecords.filter(record => record.isCorrect === 0).length
})

// 部分正确题数
const partialCount = computed(() => {
  if (!examRecord.value?.answerRecords) return 0
  return examRecord.value.answerRecords.filter(record => record.isCorrect === 2).length
})

const examDuration = computed(() => {
  if (!examRecord.value?.startTime || !examRecord.value?.endTime) return '未知'
  const start = new Date(examRecord.value.startTime)
  const end = new Date(examRecord.value.endTime)
  const diffMs = end - start
  const diffMins = Math.floor(diffMs / 60000)
  return `${diffMins} 分钟`
})

// 方法
const getStatusText = (status) => {
  const statusMap = {
    '进行中': '考试中',
    '已完成': '待批阅',
    '已批阅': '已完成'
  }
  return statusMap[status] || status
}

const getStatusTagType = (status) => {
  const typeMap = {
    '进行中': 'warning',
    '已完成': 'info',
    '已批阅': 'success'
  }
  return typeMap[status] || 'info'
}

const getQuestionTypeText = (type) => {
  const typeMap = {
    'CHOICE': '选择题',
    'JUDGE': '判断题',
    'TEXT': '简答题'
  }
  return typeMap[type] || '未知题型'
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '未知时间'
  return new Date(dateTime).toLocaleString('zh-CN')
}

const getUserAnswer = (questionId) => {
  if (!examRecord.value?.answerRecords) return ''
  const record = examRecord.value.answerRecords.find(r => r.questionId === questionId)
  return record?.userAnswer || ''
}

const getQuestionScore = (questionId) => {
  if (!examRecord.value?.answerRecords) return 0
  const record = examRecord.value.answerRecords.find(r => r.questionId === questionId)
  return record?.score || 0
}

const getScoreClass = (questionId) => {
  const score = getQuestionScore(questionId)
  const question = examRecord.value.paper.questions.find(q => q.id === questionId)
  if (!question) return 'zero'
  
  const percentage = (score / question.paperScore) * 100
  if (percentage === 100) return 'full'
  if (percentage >= 60) return 'partial'
  return 'zero'
}

const getAIFeedback = (questionId) => {
  if (!examRecord.value?.answerRecords) return ''
  const record = examRecord.value.answerRecords.find(r => r.questionId === questionId)
  return record?.aiCorrection || ''
}

// 基于答题记录的辅助函数
const getQuestionByRecord = (answerRecord) => {
  if (!examRecord.value?.paper?.questions) return null
  return examRecord.value.paper.questions.find(q => q.id === answerRecord.questionId)
}

const getQuestionTitleByRecord = (answerRecord) => {
  const question = getQuestionByRecord(answerRecord)
  return question?.title || '题目信息缺失'
}

const getQuestionTypeByRecord = (answerRecord) => {
  const question = getQuestionByRecord(answerRecord)
  return getQuestionTypeText(question?.type || 'UNKNOWN')
}

const getQuestionMaxScore = (questionId) => {
  const question = examRecord.value?.paper?.questions?.find(q => q.id === questionId)
  return question?.paperScore || 10
}

// 格式化判断题答案显示
const formatJudgeAnswer = (answer) => {
  if (!answer) return '未作答'
  
  const answerStr = answer.toString().toUpperCase()
  
  // 处理各种格式的判断题答案
  switch (answerStr) {
    case 'T':
    case 'TRUE':
    case '正确':
    case '对':
      return '正确'
    case 'F':
    case 'FALSE':
    case '错误':
    case '错':
      return '错误'
    default:
      return answer
  }
}

// 获取格式化的用户答案
const getFormattedUserAnswer = (answerRecord) => {
  if (!answerRecord.userAnswer) return '未作答'
  
  const question = getQuestionByRecord(answerRecord)
  if (question?.type === 'JUDGE') {
    return formatJudgeAnswer(answerRecord.userAnswer)
  }
  
  return answerRecord.userAnswer
}

// 获取格式化的标准答案
const getFormattedCorrectAnswer = (answerRecord) => {
  const question = getQuestionByRecord(answerRecord)
  if (!question?.answer?.answer) return '答案信息缺失'
  
  if (question.type === 'JUDGE') {
    return formatJudgeAnswer(question.answer.answer)
  }
  
  return question.answer.answer
}

const getCorrectAnswerByRecord = (answerRecord) => {
  const question = getQuestionByRecord(answerRecord)
  return question?.answer?.answer || '答案信息缺失'
}

const getScoreClassByRecord = (answerRecord) => {
  const score = answerRecord.score || 0
  const maxScore = getQuestionMaxScore(answerRecord.questionId)
  
  if (score === 0) return 'zero'
  if (score === maxScore) return 'full'
  return 'partial'
}

// 获取成绩等级文本
const getGradeText = (percentage) => {
  if (percentage >= 90) return '优秀'
  if (percentage >= 80) return '良好'
  if (percentage >= 60) return '及格'
  return '不及格'
}

// 获取排名信息
const fetchRankingInfo = async (examRecordId, paperId) => {
  try {
    const response = await fetch(`http://localhost:8080/api/exam-records/ranking?paperId=${paperId}&limit=1000`)
    const result = await response.json()
    
    if (result.code === 200) {
      const rankings = result.data
      
      // 找到当前考试记录的排名
      const sortedRankings = rankings.sort((a, b) => b.score - a.score)
      const currentRankIndex = sortedRankings.findIndex(record => record.id === examRecordId)
      
      if (currentRankIndex !== -1) {
        const currentRank = currentRankIndex + 1
        const totalParticipants = rankings.length
        const beatCount = totalParticipants - currentRank
        const beatPercentage = totalParticipants > 1 ? Math.round((beatCount / (totalParticipants - 1)) * 100) : 0
        
        rankingInfo.value = {
          currentRank,
          totalParticipants,
          beatPercentage
        }
      }
    }
  } catch (error) {
    console.error('获取排名信息失败:', error)
    // 排名获取失败不影响页面显示
  }
}

// 获取考试结果
const fetchExamResult = async () => {
  loading.value = true
  try {
    const examRecordId = route.params.id || route.query.id
    console.log('获取考试结果，ID:', examRecordId)
    
    if (!examRecordId) {
      throw new Error('缺少考试记录ID')
    }
    
    const res = await getExamRecordById(examRecordId)
    examRecord.value = res.data
    console.log('加载的考试记录:', examRecord.value)
    
    // 添加详细的调试信息
    console.log('=== 考试结果调试信息 ===')
    console.log('考试总分:', examRecord.value.score)
    console.log('试卷满分:', examRecord.value.paper?.totalScore)
    console.log('试卷配置题目数:', examRecord.value.paper?.questionCount)
    console.log('实际答题记录数:', examRecord.value.answerRecords?.length)
    console.log('答题记录详情:', examRecord.value.answerRecords)
    
    if (examRecord.value.answerRecords) {
      const correctAnswers = examRecord.value.answerRecords.filter(record => record.isCorrect === 1)
      const wrongAnswers = examRecord.value.answerRecords.filter(record => record.isCorrect === 0)
      const partialAnswers = examRecord.value.answerRecords.filter(record => record.isCorrect === 2)
      
      console.log('完全正确的题目数:', correctAnswers.length)
      console.log('错误的题目数:', wrongAnswers.length)
      console.log('部分正确的题目数:', partialAnswers.length)
      
      // 检查每道题的分数
      examRecord.value.answerRecords.forEach((record, index) => {
        const question = examRecord.value.paper?.questions?.find(q => q.id === record.questionId)
        console.log(`第${index + 1}题 - 题目ID: ${record.questionId}, 得分: ${record.score}, 满分: ${question?.paperScore}, 正确性: ${record.isCorrect}`)
      })
    }
    
    console.log('=== 调试信息结束 ===')
    
    // 为每个题目预处理用户答案
    if (examRecord.value.paper?.questions) {
      examRecord.value.paper.questions.forEach(q => {
        q.userAnswer = getUserAnswer(q.id)
      })
    }
    
    // 获取排名信息
    if (examRecord.value.status === '已批阅') {
      await fetchRankingInfo(examRecord.value.id, examRecord.value.examId)
    }
  } catch (error) {
    console.error('加载考试结果失败:', error)
    ElMessage.error('加载考试结果失败')
  } finally {
    loading.value = false
  }
}

// 下载成绩单
const downloadResult = async () => {
  try {
    ElMessage.info('正在生成成绩单，请稍候...')
    
    // 使用html2canvas生成图片
    const canvas = await html2canvas(downloadArea.value, {
      backgroundColor: '#ffffff',
      scale: 2,
      useCORS: true,
      allowTaint: true
    })
    
    // 创建下载链接
    const link = document.createElement('a')
    link.download = `考试成绩单_${examRecord.value.studentName}_${new Date().toLocaleDateString()}.png`
    link.href = canvas.toDataURL('image/png')
    
    // 触发下载
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success('成绩单下载成功！')
  } catch (error) {
    console.error('下载失败:', error)
    ElMessage.error('下载失败，请稍后重试')
  }
}

// 获取奖杯样式类
const getTrophyClass = () => {
  if (!rankingInfo.value) return ''
  const rank = rankingInfo.value.currentRank
  if (rank === 1) return 'trophy-gold'
  if (rank === 2) return 'trophy-silver'
  if (rank === 3) return 'trophy-bronze'
  if (rank <= 10) return 'trophy-top10'
  return 'trophy-normal'
}

// 获取奖杯表情
const getTrophyEmoji = () => {
  if (!rankingInfo.value) return '🏆'
  const rank = rankingInfo.value.currentRank
  if (rank === 1) return '🥇'
  if (rank === 2) return '🥈'
  if (rank === 3) return '🥉'
  if (rank <= 10) return '🏆'
  return '📊'
}

// 获取排名副标题
const getRankingSubtitle = () => {
  if (!rankingInfo.value) return ''
  const rank = rankingInfo.value.currentRank
  if (rank === 1) return '恭喜你获得第一名！'
  if (rank === 2) return '仅次于第一名！'
  if (rank === 3) return '站上领奖台！'
  if (rank <= 10) return '进入前十强！'
  if (rank <= 20) return '表现优秀！'
  return '继续努力！'
}

// 获取排名样式类
const getRankClass = () => {
  if (!rankingInfo.value) return ''
  const rank = rankingInfo.value.currentRank
  if (rank === 1) return 'rank-gold'
  if (rank === 2) return 'rank-silver'
  if (rank === 3) return 'rank-bronze'
  if (rank <= 10) return 'rank-top10'
  return 'rank-normal'
}

// 获取成就样式类
const getAchievementClass = () => {
  if (!rankingInfo.value) return ''
  const percentage = rankingInfo.value.beatPercentage
  if (percentage >= 90) return 'achievement-legendary'
  if (percentage >= 70) return 'achievement-excellent'
  if (percentage >= 50) return 'achievement-good'
  return 'achievement-normal'
}

// 获取成就图标
const getAchievementIcon = () => {
  if (!rankingInfo.value) return '⭐'
  const percentage = rankingInfo.value.beatPercentage
  if (percentage >= 90) return '👑'
  if (percentage >= 70) return '🌟'
  if (percentage >= 50) return '⭐'
  return '💪'
}

// 获取成就文本
const getAchievementText = () => {
  if (!rankingInfo.value) return ''
  const percentage = rankingInfo.value.beatPercentage
  if (percentage >= 90) return '传说级表现'
  if (percentage >= 70) return '卓越表现'
  if (percentage >= 50) return '优秀表现'
  return '继续加油'
}

// 获取超越百分比样式类
const getBeatClass = () => {
  if (!rankingInfo.value) return ''
  const percentage = rankingInfo.value.beatPercentage
  if (percentage >= 90) return 'beat-legendary'
  if (percentage >= 70) return 'beat-excellent'
  if (percentage >= 50) return 'beat-good'
  return 'beat-normal'
}

// 获取进度提示
const getProgressTip = () => {
  if (!rankingInfo.value) return ''
  const rank = rankingInfo.value.currentRank
  const total = rankingInfo.value.totalParticipants
  const percentage = ((total - rank) / total) * 100
  
  if (rank === 1) return '你已经是第一名了！'
  if (rank <= 3) return '冲击第一名！'
  if (rank <= 10) return '努力进入前三名！'
  if (percentage >= 70) return '向前十名进发！'
  return '每一次练习都是进步！'
}

// 获取激励样式类
const getMotivationClass = () => {
  if (!rankingInfo.value) return ''
  const rank = rankingInfo.value.currentRank
  if (rank <= 3) return 'motivation-champion'
  if (rank <= 10) return 'motivation-excellent'
  return 'motivation-encourage'
}

// 获取激励图标
const getMotivationIcon = () => {
  if (!rankingInfo.value) return '💪'
  const rank = rankingInfo.value.currentRank
  if (rank === 1) return '🎉'
  if (rank <= 3) return '🔥'
  if (rank <= 10) return '⚡'
  return '💪'
}

// 获取激励消息
const getMotivationMessage = () => {
  if (!rankingInfo.value) return ''
  const rank = rankingInfo.value.currentRank
  const score = examRecord.value?.score || 0
  const totalScore = examRecord.value?.paper?.totalScore || 100
  const percentage = Math.round((score / totalScore) * 100)
  
  if (rank === 1 && percentage >= 90) return '完美表现！你是真正的学霸！'
  if (rank === 1) return '恭喜夺冠！继续保持领先优势！'
  if (rank === 2) return '距离第一名只有一步之遥！'
  if (rank === 3) return '勇夺季军！向更高目标冲刺！'
  if (rank <= 10) return '进入前十强，实力不容小觑！'
  if (percentage >= 80) return '分数很高，排名还有提升空间！'
  return '每一次努力都会有收获，继续加油！'
}

// 再次挑战
const retakeChallenge = () => {
  // 跳转到本试卷的考试开始页面，用户可重新输入姓名开始考试
  if (examRecord.value && examRecord.value.paper && examRecord.value.paper.id) {
    // 跳转到 /exam/start/:paperId
    router.push(`/exam/start/${examRecord.value.paper.id}`)
  } else {
    // 如果没有试卷信息，回到考试列表
    ElMessage.warning('未获取到试卷信息，返回考试列表')
    router.push('/exam/list')
  }
}

// 查看排行榜
const viewRanking = () => {
  // 跳转到排行榜页面，并传递当前试卷ID参数
  if (examRecord.value && examRecord.value.paper && examRecord.value.paper.id) {
    // 跳转到 /exam-ranking?paperId=xxx
    router.push({ path: '/exam-ranking', query: { paperId: examRecord.value.paper.id } })
  } else {
    ElMessage.warning('未获取到试卷信息，无法查看排行榜')
  }
}

// 历史数据（示例数据，实际应该从后端获取）
const historicalData = ref(null)

// 获取分数变化
const getScoreChange = () => {
  // 示例逻辑，实际应该基于历史数据
  return '+5分'
}

// 获取分数变化样式类
const getScoreChangeClass = () => {
  return 'score-increase' // 或 'score-decrease', 'score-same'
}

// 获取排名变化
const getRankChange = () => {
  return '↑2位'
}

// 获取排名变化样式类
const getRankChangeClass = () => {
  return 'rank-increase' // 或 'rank-decrease', 'rank-same'
}

// 返回首页
const goHome = () => {
  router.push('/')
}

// 查看考试列表
const goToExamList = () => {
  router.push('/exam/list')
}

// 防止用户通过浏览器返回按钮回到已完成的考试页面
const preventBackToExam = () => {
  // 在历史记录中添加一个虚拟状态，防止直接返回到考试页面
  const currentUrl = window.location.href
  
  // 添加当前页面到历史记录（这样返回时还是当前页面）
  window.history.pushState({ page: 'exam-result', preventBack: true }, '', currentUrl)
  
  // 监听浏览器的 popstate 事件（返回按钮）
  const handlePopState = (event) => {
    console.log('检测到浏览器返回操作:', event.state)
    
    // 检查是否是我们设置的防返回状态
    if (event.state && event.state.preventBack) {
      // 如果是，再次推入当前状态，阻止返回
      window.history.pushState({ page: 'exam-result', preventBack: true }, '', currentUrl)
      ElMessage.warning('考试已完成，请使用页面上的按钮进行导航')
      return
    }
    
    // 如果用户试图返回到其他页面，检查上一页是否可能是考试页面
    const referrer = document.referrer
    console.log('来源页面:', referrer)
    
    if (referrer && (referrer.includes('/exam/') || referrer.includes('exam'))) {
      // 如果来源页面可能是考试页面，阻止返回并跳转到考试列表
      event.preventDefault()
      ElMessage.warning('考试已完成，不能返回到考试页面。正在跳转到考试列表...')
      
      // 延迟跳转，给用户看到提示
      setTimeout(() => {
        router.replace('/exam/list')
      }, 1500)
      
      // 重新推入当前状态
      window.history.pushState({ page: 'exam-result', preventBack: true }, '', currentUrl)
    } else {
      // 允许返回到非考试页面（如考试列表、首页等）
      console.log('允许返回到安全页面')
    }
  }
  
  // 添加页面卸载前的警告
  const handleBeforeUnload = (event) => {
    // 检查是否试图导航到考试页面
    const targetUrl = event.target?.activeElement?.href || ''
    if (targetUrl && targetUrl.includes('/exam/')) {
      event.preventDefault()
      event.returnValue = '考试已完成，确定要离开结果页面吗？'
      return '考试已完成，确定要离开结果页面吗？'
    }
  }
  
  // 添加事件监听器
  window.addEventListener('popstate', handlePopState)
  window.addEventListener('beforeunload', handleBeforeUnload)
  
  // 返回清理函数
  return () => {
    window.removeEventListener('popstate', handlePopState)
    window.removeEventListener('beforeunload', handleBeforeUnload)
  }
}

onMounted(() => {
  fetchExamResult()
  
  // 使用 replace 替换当前历史记录，防止返回到考试页面
  const currentPath = route.path
  const examId = route.params.id
  
  // 检查当前URL，如果是通过考试页面跳转来的，替换历史记录
  if (currentPath.includes('/exam-result/')) {
    // 使用 replace 模式，移除考试页面的历史记录
    router.replace({
      path: currentPath,
      query: { ...route.query, fromExam: 'true' }
    })
  }
  
  // 设置防止返回的监听器
  const cleanup = preventBackToExam()
  
  // 在组件卸载时清理
  onUnmounted(() => {
    cleanup()
  })
})
</script>

<style scoped>
/* 主容器 */
.exam-result-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  color: white;
}

.loading-container .el-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

/* 主内容容器 */
.result-container {
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 考试完成提示 */
.completion-notice {
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f3ff 100%);
  padding: 20px;
  border-radius: 15px;
  margin-bottom: 20px;
}

.notice-content {
  display: flex;
  align-items: center;
}

.notice-icon {
  font-size: 48px;
  margin-right: 20px;
}

.notice-text {
  flex: 1;
}

.notice-text h4 {
  margin: 0 0 10px 0;
  font-size: 24px;
  font-weight: 600;
}

.notice-text p {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.notice-tip {
  font-size: 14px;
  font-weight: 500;
  color: #409eff;
}

/* 头部信息 */
.result-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left h2 {
  margin: 0 0 15px 0;
  font-size: 28px;
  font-weight: 600;
}

.exam-info p {
  margin: 8px 0;
  font-size: 16px;
  opacity: 0.9;
}

.header-right .el-tag {
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 500;
}

/* 分数展示区域整体布局优化 */
.score-section {
  display: flex;
  align-items: stretch;
  justify-content: center;
  gap: 32px;
  padding: 40px 0 40px 32px;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  flex-wrap: wrap;
  min-height: 560px; /* 新增：设置最小高度，保证两侧卡片对齐 */
}
.score-rank-card,
.score-details {
  height: 100%; /* 保证自动等高 */
  min-height: 0;
}

/* 分数卡片样式优化 */
.score-display {
  flex: 0 0 220px;
  min-height: 400px; /* 高度收紧 */
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 0;
  padding: 0 0 0 0;
}

.score-badge {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 0 24px 0; /* 上下间距收紧 */
  border-radius: 20px;
  background: transparent;
  min-width: 180px;
  min-height: 320px;
  justify-content: center;
}

.score-badge::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 6px;
  background: linear-gradient(45deg, #667eea, #764ba2);
}

.score-icon {
  font-size: 48px;
  margin-bottom: 15px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

.score-main {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.score-label {
  font-size: 16px;
  color: #666;
  margin-bottom: 12px;
  font-weight: 500;
}

.score-number {
  display: flex;
  align-items: baseline;
  margin-bottom: 12px;
}

.score-value {
  font-size: 48px;
  font-weight: 700;
  color: #303133;
}

.score-divider {
  font-size: 24px;
  color: #999;
  margin: 0 8px;
}

.score-total {
  font-size: 24px;
  color: #999;
}

.score-percentage {
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 8px;
}

.score-grade {
  font-size: 18px;
  font-weight: 600;
  padding: 6px 16px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.8);
  color: #67c23a;
  border: 2px solid #67c23a;
}

/* 分数等级颜色 */
.score-display.excellent {
  /* 移除背景和文字颜色设置，保持白色背景 */
}

.score-display.excellent .score-value {
  color: #67c23a;
}

.score-display.excellent .score-percentage {
  color: #67c23a;
}

.score-display.good {
  /* 移除背景和文字颜色设置，保持白色背景 */
}

.score-display.good .score-value {
  color: #409eff;
}

.score-display.good .score-percentage {
  color: #409eff;
}

.score-display.pass {
  /* 移除背景和文字颜色设置，保持白色背景 */
}

.score-display.pass .score-value {
  color: #e6a23c;
}

.score-display.pass .score-percentage {
  color: #e6a23c;
}

.score-display.fail {
  /* 移除背景和文字颜色设置，保持白色背景 */
}

.score-display.fail .score-value {
  color: #f56c6c;
}

.score-display.fail .score-percentage {
  color: #f56c6c;
}

/* 排名系统样式 */
.ranking-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 20px;
  align-items: center; /* 整体居中 */
}

/* 统一分数卡片和排名卡片的宽高，保证视觉一致 */
.ranking-card-main {
  width: 220px !important;         /* 和 .score-display 一致 */
  min-height: 400px !important;    /* 和 .score-display 一致 */
  max-width: 220px !important;     /* 和 .score-display 一致 */
  padding: 0 !important;           /* 和 .score-display 一致 */
  margin: 0 !important;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

/* 响应式下也同步 */
@media (max-width: 1100px) {
  .score-display, .ranking-card-main {
    min-height: 320px !important;
    width: 100% !important;
    max-width: 420px !important;
    height: auto !important;
  }
}

/* 排名卡片样式优化 */
.ranking-card-main {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 24px;
  box-shadow: 0 4px 24px rgba(102, 126, 234, 0.10);
  color: white;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 340px;
  min-height: 320px; /* 高度进一步降低 */
  max-width: 340px;
  box-sizing: border-box;
  justify-content: center;
  padding: 18px 0 18px 0; /* 上下间距收紧 */
  margin: 0;
}

.ranking-card-main::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  transform: rotate(45deg);
  animation: shimmer 3s infinite;
}

@keyframes shimmer {
  0% { transform: translateX(-100%) translateY(-100%) rotate(45deg); }
  100% { transform: translateX(100%) translateY(100%) rotate(45deg); }
}

.ranking-header-main,
.ranking-title-main,
.ranking-content-main,
.ranking-position-main,
.achievement-section,
.ranking-progress,
.motivation-section {
  width: 100%;
  text-align: center;
  align-items: center;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

.achievement-section {
  justify-content: center;
  gap: 18px;
  margin-bottom: 22px;
}

.challenge-actions {
  display: flex;
  gap: 24px;
  justify-content: center;
  max-width: 420px;         /* 限制按钮组最大宽度，防止拉满 */
  margin: 24px auto 0 auto; /* 上下间距，左右居中 */
}

.challenge-btn, .ranking-btn {
  border-radius: 24px !important; /* 圆角适中 */
  font-weight: 700 !important;   /* 字体加粗 */
  padding: 0 32px !important;    /* 左右内边距适中 */
  font-size: 18px !important;    /* 字体适中 */
  height: 44px !important;       /* 高度适中 */
  min-width: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.trophy-animation {
  margin-right: 20px;
  position: relative;
}

.trophy-icon {
  font-size: 60px;
  animation: trophyBounce 2s ease-in-out infinite alternate;
  filter: drop-shadow(0 0 10px rgba(255, 215, 0, 0.5));
}

.trophy-icon.trophy-gold {
  animation: goldGlow 2s ease-in-out infinite alternate;
}

.trophy-icon.trophy-silver {
  animation: silverGlow 2s ease-in-out infinite alternate;
}

.trophy-icon.trophy-bronze {
  animation: bronzeGlow 2s ease-in-out infinite alternate;
}

@keyframes trophyBounce {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

@keyframes goldGlow {
  0% { filter: drop-shadow(0 0 10px rgba(255, 215, 0, 0.5)); }
  100% { filter: drop-shadow(0 0 20px rgba(255, 215, 0, 0.8)); }
}

@keyframes silverGlow {
  0% { filter: drop-shadow(0 0 10px rgba(192, 192, 192, 0.5)); }
  100% { filter: drop-shadow(0 0 20px rgba(192, 192, 192, 0.8)); }
}

@keyframes bronzeGlow {
  0% { filter: drop-shadow(0 0 10px rgba(205, 127, 50, 0.5)); }
  100% { filter: drop-shadow(0 0 20px rgba(205, 127, 50, 0.8)); }
}

.ranking-sparkles {
  position: absolute;
  top: -10px;
  right: -10px;
  font-size: 20px;
  animation: sparkle 1.5s ease-in-out infinite;
}

@keyframes sparkle {
  0%, 100% { opacity: 0; transform: scale(0.5); }
  50% { opacity: 1; transform: scale(1.2); }
}

.ranking-title-main h3 {
  margin: 0 0 5px 0;
  font-size: 24px;
  font-weight: 700;
}

.ranking-subtitle {
  font-size: 16px;
  opacity: 0.9;
  font-weight: 500;
}

.ranking-content-main {
  width: 100%;
  text-align: center;
  align-items: center;
  display: flex;
  flex-direction: column;
  gap: 10px; /* 行间距更紧凑 */
  box-sizing: border-box;
}

.ranking-row {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  width: 100%;
  gap: 16px;
}

.ranking-position-row {
  margin-bottom: 8px;
}

.rank-badge {
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50px;
  padding: 10px 18px;
  margin-bottom: 0;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}

.rank-total-info {
  font-size: 14px;
  opacity: 0.8;
  margin-left: 6px;
}

.ranking-progress {
  margin-bottom: 25px;
  width: 100%;
}

.progress-label, .progress-tips {
  text-align: center;
}

.progress-bar-container {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  height: 8px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #4facfe, #00f2fe);
  border-radius: 10px;
  transition: width 0.8s ease;
}

.motivation-section {
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  padding-top: 20px;
  position: relative;
  z-index: 1;
  width: 100%;
  text-align: center; /* 居中 */
}

.motivation-message {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(5px);
  border-radius: 15px;
  padding: 15px;
  margin-bottom: 15px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  text-align: center;
  width: 100%;
}

.motivation-message.motivation-champion {
  background: linear-gradient(135deg, rgba(255, 215, 0, 0.3), rgba(255, 237, 78, 0.3));
  border-color: #ffd700;
}

.motivation-icon {
  font-size: 20px;
  margin-right: 8px;
}

.motivation-text {
  font-size: 14px;
  font-weight: 500;
}

.challenge-actions {
  display: flex;
  gap: 24px;
  justify-content: center;
  max-width: 420px;         /* 限制按钮组最大宽度，防止拉满 */
  margin: 24px auto 0 auto; /* 上下间距，左右居中 */
}

.challenge-btn, .ranking-btn {
  border-radius: 24px !important; /* 圆角适中 */
  font-weight: 700 !important;   /* 字体加粗 */
  padding: 0 32px !important;    /* 左右内边距适中 */
  font-size: 18px !important;    /* 字体适中 */
  height: 44px !important;       /* 高度适中 */
  min-width: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.challenge-btn {
  background: linear-gradient(135deg, #ff6b6b, #ff8e8e) !important;
  border: none !important;
  color: #fff !important;
}

.ranking-btn {
  background: linear-gradient(135deg, #ffd700, #ffed4e) !important;
  color: #333 !important;
  border: none !important;
}

/* 历史成绩对比 */
.historical-comparison {
  background: white;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.historical-comparison h4 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 16px;
}

.comparison-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.stat-comparison {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 10px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.stat-current {
  font-weight: 600;
  color: #303133;
}

.stat-change {
  font-size: 12px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 10px;
}

.stat-change.score-increase {
  background: #f0f9ff;
  color: #67c23a;
}

.stat-change.rank-increase {
  background: #f0f9ff;
  color: #67c23a;
}

/* 响应式设计 */
@media (max-width: 1100px) {
  .score-section {
    flex-direction: column;
    align-items: center;
    gap: 24px;
    padding: 24px 0 24px 0;
  }
  .score-display, .ranking-card-main, .score-details {
    min-height: 320px;
    width: 100%;
    max-width: 420px;
    height: auto;
  }
  .score-details {
    min-width: 0;
    grid-template-columns: 1fr;
    padding: 24px 8px;
  }
}

/* 详细信息 */
.score-details {
  flex: 1 1 340px;
  min-width: 340px;
  min-height: 0;
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  align-content: center;
  justify-content: center;
  padding: 32px 24px;
  margin: 0;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  height: 100%; /* 新增：自动撑满父容器 */
}

.detail-item {
  background: #fff;
  padding: 20px 0;
  border-radius: 15px;
  box-shadow: none;
  text-align: center;
  border: 1px solid #f0f0f0;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 90px;
}

.detail-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.detail-icon {
  font-size: 32px;
  margin-bottom: 12px;
  display: block;
}

.detail-info {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.detail-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
  font-weight: 500;
}

.detail-value {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

/* 操作按钮 */
.action-buttons {
  padding: 30px 40px;
  text-align: center;
  background: #fafafa;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 15px;
}

.action-buttons .el-button {
  margin: 0;
  padding: 12px 30px;
  font-size: 16px;
  border-radius: 25px;
  font-weight: 500;
  min-width: 140px;
  transition: all 0.3s ease;
}

/* 返回首页按钮 - 主要操作 */
.action-buttons .el-button--primary {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  border: none;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.action-buttons .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
}

/* 考试列表按钮 - 次要操作 */
.action-buttons .el-button--info {
  background: linear-gradient(135deg, #909399, #a6a9ad);
  border: none;
  color: white;
  box-shadow: 0 4px 12px rgba(144, 147, 153, 0.3);
}

.action-buttons .el-button--info:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(144, 147, 153, 0.4);
  color: white;
}

/* 下载按钮 - 成功操作 */
.action-buttons .el-button--success {
  background: linear-gradient(135deg, #67c23a, #85ce61);
  border: none;
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
}

.action-buttons .el-button--success:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(103, 194, 58, 0.4);
}

/* AI总评 */
.ai-summary {
  padding: 30px 40px;
  background: linear-gradient(135deg, #e8f4fd 0%, #f0f9ff 100%);
  border-top: 1px solid #e1ecf4;
}

.ai-summary h3 {
  margin: 0 0 20px 0;
  color: #1890ff;
  font-size: 20px;
  font-weight: 600;
}

.summary-content {
  background: white;
  padding: 25px;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  border-left: 4px solid #1890ff;
}

.summary-content p {
  margin: 0;
  font-size: 16px;
  line-height: 1.6;
  color: #303133;
}

/* 详细结果 */
.detailed-results {
  padding: 40px;
}

.detailed-results h3 {
  margin: 0 0 30px 0;
  font-size: 22px;
  color: #303133;
  font-weight: 600;
}

.question-result-card {
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 15px;
  margin-bottom: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.question-result-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.question-header {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e4e7ed;
}

.question-number {
  font-weight: 600;
  color: #495057;
  font-size: 16px;
}

.question-score {
  font-weight: 600;
  font-size: 16px;
  padding: 5px 12px;
  border-radius: 15px;
}

.question-score.full {
  background: #f0f9ff;
  color: #67c23a;
}

.question-score.partial {
  background: #fff7e6;
  color: #e6a23c;
}

.question-score.zero {
  background: #fef0f0;
  color: #f56c6c;
}

.question-content {
  padding: 20px;
}

.question-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 15px;
  line-height: 1.5;
}

.question-type {
  background: #e1f3d8;
  color: #67c23a;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  margin-right: 10px;
  font-weight: 500;
}

.question-choices {
  margin: 20px 0;
  padding: 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border: 1px solid #e9ecef;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.choices-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.choice-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  background: white;
  border: 2px solid #e9ecef;
  border-radius: 10px;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.choice-item:hover {
  border-color: #409eff;
  transform: translateY(-1px);
  box-shadow: 0 3px 8px rgba(64, 158, 255, 0.2);
}

.choice-label {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: white;
  border-radius: 50%;
  font-weight: 700;
  font-size: 14px;
  margin-right: 12px;
  flex-shrink: 0;
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.3);
}

.choice-content {
  flex: 1;
  color: #303133;
  font-size: 15px;
  font-weight: 500;
  line-height: 1.4;
}

.answer-section {
  margin: 20px 0;
  display: grid;
  gap: 15px;
}

.user-answer, .correct-answer {
  padding: 15px;
  border-radius: 10px;
  border-left: 4px solid;
}

.user-answer {
  background: #f8f9fa;
  border-left-color: #6c757d;
}

.correct-answer {
  background: #f0f9ff;
  border-left-color: #67c23a;
}

.answer-text {
  color: #303133;
  font-size: 15px;
  margin-left: 10px;
}

.answer-text.correct {
  color: #67c23a;
  font-weight: 500;
}

/* AI反馈 */
.ai-feedback {
  margin-top: 20px;
  background: linear-gradient(135deg, #fff8e1 0%, #fffbf0 100%);
  border: 1px solid #ffe082;
  border-radius: 12px;
  padding: 20px;
}

.feedback-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  color: #f57c00;
  font-weight: 600;
}

.feedback-header .el-icon {
  margin-right: 8px;
  font-size: 18px;
}

.feedback-content {
  margin: 0;
  color: #5d4037;
  font-size: 14px;
  line-height: 1.5;
  font-style: italic;
}

/* 下载成绩单样式 */
.download-area {
  width: 800px;
  background: white;
}

.result-certificate {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 60px;
  font-family: 'Microsoft YaHei', sans-serif;
}

.certificate-header {
  text-align: center;
  margin-bottom: 50px;
}

.certificate-header h1 {
  font-size: 36px;
  margin: 0 0 20px 0;
  font-weight: 700;
}

.certificate-decoration {
  width: 100px;
  height: 4px;
  background: linear-gradient(45deg, #ffd700, #ffed4e);
  margin: 0 auto;
  border-radius: 2px;
}

.certificate-body {
  text-align: center;
}

.student-info h2 {
  font-size: 28px;
  margin: 0 0 10px 0;
  font-weight: 600;
}

.student-info p {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 40px;
}

.exam-details h3 {
  font-size: 24px;
  margin: 0 0 30px 0;
  font-weight: 500;
}

.score-display-cert {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 30px;
  margin: 30px auto;
  max-width: 300px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.score-main {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 10px;
}

.score-percentage-cert {
  font-size: 24px;
  font-weight: 600;
}

.performance-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin: 40px 0;
}

.stat-item {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(5px);
  padding: 20px;
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-item span:first-child {
  font-size: 14px;
  opacity: 0.8;
}

.stat-item span:last-child {
  font-size: 18px;
  font-weight: 600;
}

.ai-summary-cert {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(5px);
  padding: 25px;
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  margin: 30px 0;
  text-align: left;
}

.ai-summary-cert h4 {
  margin: 0 0 15px 0;
  font-size: 18px;
  font-weight: 600;
}

.ai-summary-cert p {
  margin: 0;
  line-height: 1.6;
  font-size: 14px;
}

.certificate-footer {
  margin-top: 50px;
  text-align: center;
  opacity: 0.8;
  font-size: 14px;
}

.certificate-footer p {
  margin: 5px 0;
}

/* 样式部分：合并卡片整体布局 */
.score-rank-card {
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  margin: 0 0 32px 0;
  padding: 0;
  max-width: 480px;
  min-height: 420px;
  overflow: hidden;
  height: 100%; /* 新增：自动撑满父容器 */
}
.score-rank-main {
  display: flex;
  flex-direction: row;
  width: 100%;
  min-height: 320px;
}
.score-block, .rank-block {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px 0;
}
.score-block {
  border-right: 1px solid #f0f0f0;
}
.rank-block {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px 0;
  min-width: 220px;
  min-height: 320px;
}
.rank-top {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  margin-bottom: 18px;
}
.trophy-icon {
  font-size: 56px;
  margin-bottom: 6px;
}
.rank-number-group {
  display: flex;
  align-items: baseline;
  gap: 4px;
}
.rank-number-large {
  font-size: 44px;
  font-weight: 800;
  color: #ffd700;
  text-shadow: 0 2px 8px rgba(0,0,0,0.12);
}
.rank-suffix {
  font-size: 20px;
  font-weight: 500;
  color: #fff;
}
.rank-title {
  font-size: 22px;
  font-weight: 700;
  margin-top: 2px;
  letter-spacing: 2px;
}
.rank-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  width: 100%;
}
.rank-participants {
  font-size: 15px;
  opacity: 0.95;
}
.rank-subtitle {
  font-size: 16px;
  font-weight: 500;
  opacity: 0.92;
  margin-bottom: 2px;
}
.rank-progress-bar {
  width: 80%;
  height: 10px;
  background: rgba(255,255,255,0.18);
  border-radius: 8px;
  margin: 6px 0 2px 0;
  overflow: hidden;
}
.progress-bar-inner {
  height: 100%;
  background: linear-gradient(90deg, #ffd700, #ffed4e);
  border-radius: 8px;
  transition: width 0.8s cubic-bezier(.4,0,.2,1);
}
.rank-progress-tip {
  font-size: 14px;
  color: #fffbe6;
  margin-top: 2px;
}
.score-rank-footer {
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
  padding: 18px 0 32px 0;      /* 底部padding加大，按钮不贴底 */
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}
@media (max-width: 900px) {
  .score-rank-card {
    max-width: 100%;
    min-width: 0;
  }
  .score-rank-main {
    flex-direction: column;
  }
  .score-block {
    border-right: none;
    border-bottom: 1px solid #f0f0f0;
  }
}
</style> 