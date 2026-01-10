<template>
  <div class="mock-interview">
    <!-- 面试配置页面 -->
    <div v-if="currentStep === 'config'" class="interview-config">
      <div class="config-header">
        <h2>模拟面试配置</h2>
        <p>配置面试参数，开始你的模拟面试之旅</p>
      </div>
      
      <el-card class="config-card">
        <el-form :model="interviewConfig" :rules="configRules" ref="configFormRef" label-width="120px">
          <el-form-item label="技术方向" prop="direction">
            <el-select v-model="interviewConfig.direction" placeholder="选择技术方向">
              <el-option label="Java" value="java"></el-option>
              <el-option label="前端" value="frontend"></el-option>
              <el-option label="大数据" value="bigdata"></el-option>
              <el-option label="算法" value="algorithm"></el-option>
              <el-option label="运维" value="devops"></el-option>
              <el-option label="测试" value="testing"></el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="题目数量" prop="questionCount">
            <el-input-number v-model="interviewConfig.questionCount" :min="3" :max="20" :step="1"></el-input-number>
            <span class="form-tip">建议选择5-10道题目</span>
          </el-form-item>
          
          <el-form-item label="难度级别" prop="difficulty">
            <el-select v-model="interviewConfig.difficulty" placeholder="选择难度">
              <el-option label="简单" value="easy"></el-option>
              <el-option label="中等" value="medium"></el-option>
              <el-option label="困难" value="hard"></el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="公司类型" prop="companyType">
            <el-select v-model="interviewConfig.companyType" placeholder="选择公司类型">
              <el-option label="互联网大厂" value="large"></el-option>
              <el-option label="中型公司" value="medium"></el-option>
              <el-option label="创业公司" value="startup"></el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="面试时长" prop="duration">
            <el-input-number v-model="interviewConfig.duration" :min="15" :max="120" :step="5"></el-input-number>
            <span class="form-tip">分钟</span>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleStartInterview" :loading="starting">开始面试</el-button>
            <el-button @click="$router.go(-1)">返回</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 面试进行中页面 -->
    <div v-if="currentStep === 'interviewing'" class="interview-progress">
      <div class="interview-header">
        <div class="header-left">
          <h3>模拟面试进行中</h3>
          <p>第 {{ currentQuestionIndex + 1 }} 题 / 共 {{ questions.length }} 题</p>
        </div>
        <div class="header-right">
          <div class="timer">
            <i class="el-icon-time"></i>
            <span>{{ formatTime(remainingTime) }}</span>
          </div>
          <el-button type="danger" @click="handleEndInterview">结束面试</el-button>
        </div>
      </div>
      
      <div class="question-container">
        <el-card class="question-card">
          <template #header>
            <div class="question-header">
              <span>题目 {{ currentQuestionIndex + 1 }}</span>
              <div class="question-meta">
                <el-tag size="small" :type="getDirectionType(currentQuestion.direction)">
                  {{ getDirectionLabel(currentQuestion.direction) }}
                </el-tag>
                <el-tag size="small" :type="getDifficultyType(currentQuestion.difficulty)">
                  {{ getDifficultyLabel(currentQuestion.difficulty) }}
                </el-tag>
              </div>
            </div>
          </template>
          
          <div class="question-content">
            <h4>{{ currentQuestion.title }}</h4>
            <p>{{ currentQuestion.content }}</p>
          </div>
          
          <div class="answer-section">
            <h5>你的回答：</h5>
            
            <!-- 文本回答 -->
            <el-input
              v-model="currentAnswer.text"
              type="textarea"
              :rows="6"
              placeholder="请详细描述你的答案..."
            ></el-input>
            
            <!-- 语音录制 -->
            <div class="voice-section">
              <h6>语音回答（可选）：</h6>
              <div class="voice-controls">
                <el-button 
                  v-if="!isRecording" 
                  type="primary" 
                  @click="startRecording"
                  :disabled="!isVoiceSupported"
                >
                  <i class="el-icon-microphone"></i> 开始录音
                </el-button>
                <el-button 
                  v-else 
                  type="danger" 
                  @click="stopRecording"
                >
                  <i class="el-icon-video-pause"></i> 停止录音
                </el-button>
                <span v-if="isRecording" class="recording-time">{{ recordingTime }}s</span>
              </div>
              
              <!-- 录音播放 -->
              <div v-if="currentAnswer.voiceUrl" class="voice-player">
                <audio :src="currentAnswer.voiceUrl" controls></audio>
                <el-button size="small" @click="deleteVoice">删除录音</el-button>
              </div>
            </div>
            
            <div class="answer-actions">
              <el-button type="primary" @click="handleSubmitAnswer" :loading="submitting">
                提交答案
              </el-button>
              <el-button @click="handleSkipQuestion">跳过此题</el-button>
            </div>
          </div>
        </el-card>
      </div>
      
      <!-- 进度条 -->
      <div class="progress-section">
        <el-progress 
          :percentage="(currentQuestionIndex + 1) / questions.length * 100" 
          :format="format"
        ></el-progress>
      </div>
    </div>

    <!-- 面试结果页面 -->
    <div v-if="currentStep === 'result'" class="interview-result">
      <div class="result-header">
        <h2>面试结果</h2>
        <p>恭喜你完成了本次模拟面试！</p>
      </div>
      
      <el-card class="result-card">
        <div class="result-summary">
          <div class="score-section">
            <div class="total-score">
              <h3>总分</h3>
              <div class="score-number">{{ interviewResult.totalScore }}</div>
              <div class="score-max">满分：{{ interviewResult.maxScore }}</div>
            </div>
            <div class="average-score">
              <h4>平均分</h4>
              <div class="score-number">{{ interviewResult.averageScore }}</div>
            </div>
          </div>
          
          <div class="stats-section">
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="stat-item">
                  <div class="stat-number">{{ interviewResult.totalQuestions }}</div>
                  <div class="stat-label">总题数</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="stat-item">
                  <div class="stat-number">{{ interviewResult.completedQuestions }}</div>
                  <div class="stat-label">完成题数</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="stat-item">
                  <div class="stat-number">{{ interviewResult.duration }}</div>
                  <div class="stat-label">用时(分钟)</div>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
        
        <!-- 详细结果 -->
        <div class="detail-results">
          <h4>详细评分</h4>
          <el-table :data="interviewResult.details" stripe>
            <el-table-column prop="questionTitle" label="题目" min-width="200"></el-table-column>
            <el-table-column prop="score" label="得分" width="80"></el-table-column>
            <el-table-column prop="maxScore" label="满分" width="80"></el-table-column>
            <el-table-column prop="aiEvaluation" label="AI评价" min-width="300"></el-table-column>
          </el-table>
        </div>
        
        <div class="result-actions">
          <el-button type="primary" @click="handleViewDetail">查看详情</el-button>
          <el-button @click="handleRetryInterview">重新面试</el-button>
          <el-button @click="$router.push('/interview-questions')">返回真题列表</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { startMockInterview, submitInterviewAnswer, completeMockInterview } from '@/api/interviewQuestion'

export default {
  name: 'MockInterview',
  setup() {
    const router = useRouter()
    
    // 面试状态
    const currentStep = ref('config') // config, interviewing, result
    const starting = ref(false)
    const submitting = ref(false)
    
    // 面试配置
    const configFormRef = ref()
    const interviewConfig = reactive({
      direction: '',
      questionCount: 5,
      difficulty: 'medium',
      companyType: 'large',
      duration: 30
    })
    
    const configRules = {
      direction: [{ required: true, message: '请选择技术方向', trigger: 'change' }],
      questionCount: [{ required: true, message: '请选择题目数量', trigger: 'blur' }],
      difficulty: [{ required: true, message: '请选择难度级别', trigger: 'change' }],
      companyType: [{ required: true, message: '请选择公司类型', trigger: 'change' }],
      duration: [{ required: true, message: '请设置面试时长', trigger: 'blur' }]
    }
    
    // 面试进行中
    const questions = ref([])
    const currentQuestionIndex = ref(0)
    const currentQuestion = computed(() => questions.value[currentQuestionIndex.value] || {})
    const currentAnswer = reactive({
      text: '',
      voiceUrl: ''
    })
    
    // 计时器
    const remainingTime = ref(0)
    const timer = ref(null)
    
    // 语音录制
    const isRecording = ref(false)
    const recordingTime = ref(0)
    const recordingTimer = ref(null)
    const mediaRecorder = ref(null)
    const audioChunks = ref([])
    const isVoiceSupported = ref('MediaRecorder' in window)
    
    // 面试结果
    const interviewResult = reactive({
      totalScore: 0,
      maxScore: 0,
      averageScore: 0,
      totalQuestions: 0,
      completedQuestions: 0,
      duration: 0,
      details: []
    })
    
    // 开始面试
    const handleStartInterview = async () => {
      try {
        await configFormRef.value.validate()
        starting.value = true
        
        const response = await startMockInterview(interviewConfig)
        questions.value = response.data.questions
        remainingTime.value = interviewConfig.duration * 60
        
        currentStep.value = 'interviewing'
        startTimer()
        
        ElMessage.success('面试开始！')
      } catch (error) {
        ElMessage.error('开始面试失败：' + error.message)
      } finally {
        starting.value = false
      }
    }
    
    // 计时器
    const startTimer = () => {
      timer.value = setInterval(() => {
        remainingTime.value--
        if (remainingTime.value <= 0) {
          handleEndInterview()
        }
      }, 1000)
    }
    
    const stopTimer = () => {
      if (timer.value) {
        clearInterval(timer.value)
        timer.value = null
      }
    }
    
    const formatTime = (seconds) => {
      const mins = Math.floor(seconds / 60)
      const secs = seconds % 60
      return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
    }
    
    // 提交答案
    const handleSubmitAnswer = async () => {
      if (!currentAnswer.text.trim()) {
        ElMessage.warning('请至少输入文字答案')
        return
      }
      
      submitting.value = true
      try {
        const data = {
          interviewRecordId: interviewResult.id,
          questionId: currentQuestion.value.id,
          userAnswer: currentAnswer.text,
          voiceFileUrl: currentAnswer.voiceUrl,
          answerTime: 0 // 可以计算答题用时
        }
        
        await submitInterviewAnswer(data)
        
        // 清空当前答案
        currentAnswer.text = ''
        currentAnswer.voiceUrl = ''
        
        // 下一题或结束
        if (currentQuestionIndex.value < questions.value.length - 1) {
          currentQuestionIndex.value++
        } else {
          await handleEndInterview()
        }
        
        ElMessage.success('答案提交成功！')
      } catch (error) {
        ElMessage.error('提交答案失败：' + error.message)
      } finally {
        submitting.value = false
      }
    }
    
    // 跳过题目
    const handleSkipQuestion = () => {
      if (currentQuestionIndex.value < questions.value.length - 1) {
        currentQuestionIndex.value++
        currentAnswer.text = ''
        currentAnswer.voiceUrl = ''
      } else {
        handleEndInterview()
      }
    }
    
    // 结束面试
    const handleEndInterview = async () => {
      try {
        stopTimer()
        
        const response = await completeMockInterview(interviewResult.id)
        Object.assign(interviewResult, response.data)
        
        currentStep.value = 'result'
      } catch (error) {
        ElMessage.error('结束面试失败：' + error.message)
      }
    }
    
    // 语音录制
    const startRecording = async () => {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
        mediaRecorder.value = new MediaRecorder(stream)
        audioChunks.value = []
        
        mediaRecorder.value.ondataavailable = (event) => {
          audioChunks.value.push(event.data)
        }
        
        mediaRecorder.value.onstop = () => {
          const audioBlob = new Blob(audioChunks.value, { type: 'audio/wav' })
          currentAnswer.voiceUrl = URL.createObjectURL(audioBlob)
          // 这里应该上传到服务器
        }
        
        mediaRecorder.value.start()
        isRecording.value = true
        recordingTime.value = 0
        
        recordingTimer.value = setInterval(() => {
          recordingTime.value++
        }, 1000)
      } catch (error) {
        ElMessage.error('无法访问麦克风：' + error.message)
      }
    }
    
    const stopRecording = () => {
      if (mediaRecorder.value && isRecording.value) {
        mediaRecorder.value.stop()
        isRecording.value = false
        
        if (recordingTimer.value) {
          clearInterval(recordingTimer.value)
          recordingTimer.value = null
        }
        
        // 停止音频流
        mediaRecorder.value.stream.getTracks().forEach(track => track.stop())
      }
    }
    
    const deleteVoice = () => {
      currentAnswer.voiceUrl = ''
    }
    
    // 结果页面操作
    const handleViewDetail = () => {
      router.push(`/interview-result/${interviewResult.id}`)
    }
    
    const handleRetryInterview = () => {
      currentStep.value = 'config'
      currentQuestionIndex.value = 0
      questions.value = []
      Object.assign(interviewResult, {
        totalScore: 0,
        maxScore: 0,
        averageScore: 0,
        totalQuestions: 0,
        completedQuestions: 0,
        duration: 0,
        details: []
      })
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
    
    const format = (percentage) => {
      return `${currentQuestionIndex.value + 1}/${questions.value.length}`
    }
    
    // 生命周期
    onUnmounted(() => {
      stopTimer()
      stopRecording()
    })
    
    return {
      currentStep,
      starting,
      submitting,
      configFormRef,
      interviewConfig,
      configRules,
      questions,
      currentQuestionIndex,
      currentQuestion,
      currentAnswer,
      remainingTime,
      isRecording,
      recordingTime,
      isVoiceSupported,
      interviewResult,
      handleStartInterview,
      handleSubmitAnswer,
      handleSkipQuestion,
      handleEndInterview,
      startRecording,
      stopRecording,
      deleteVoice,
      handleViewDetail,
      handleRetryInterview,
      formatTime,
      getDirectionType,
      getDirectionLabel,
      getDifficultyType,
      getDifficultyLabel,
      format
    }
  }
}
</script>

<style scoped>
.mock-interview {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 配置页面样式 */
.interview-config {
  text-align: center;
}

.config-header h2 {
  color: #303133;
  margin-bottom: 10px;
}

.config-header p {
  color: #909399;
  margin-bottom: 30px;
}

.config-card {
  max-width: 600px;
  margin: 0 auto;
}

.form-tip {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}

/* 面试进行中样式 */
.interview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.header-left h3 {
  margin: 0 0 5px 0;
  color: #303133;
}

.header-left p {
  margin: 0;
  color: #909399;
}

.timer {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: bold;
  color: #e6a23c;
}

.question-container {
  margin-bottom: 20px;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.question-meta {
  display: flex;
  gap: 8px;
}

.question-content h4 {
  margin-bottom: 15px;
  color: #303133;
}

.question-content p {
  line-height: 1.6;
  color: #606266;
}

.answer-section {
  margin-top: 30px;
}

.answer-section h5 {
  margin-bottom: 15px;
  color: #303133;
}

.voice-section {
  margin-top: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 6px;
}

.voice-section h6 {
  margin-bottom: 10px;
  color: #606266;
}

.voice-controls {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.recording-time {
  color: #f56c6c;
  font-weight: bold;
}

.voice-player {
  display: flex;
  align-items: center;
  gap: 10px;
}

.answer-actions {
  margin-top: 20px;
  text-align: center;
}

.progress-section {
  margin-top: 20px;
}

/* 结果页面样式 */
.interview-result {
  text-align: center;
}

.result-header h2 {
  color: #303133;
  margin-bottom: 10px;
}

.result-header p {
  color: #909399;
  margin-bottom: 30px;
}

.result-card {
  max-width: 800px;
  margin: 0 auto;
}

.result-summary {
  margin-bottom: 30px;
}

.score-section {
  display: flex;
  justify-content: space-around;
  margin-bottom: 30px;
}

.total-score, .average-score {
  text-align: center;
}

.score-number {
  font-size: 48px;
  font-weight: bold;
  color: #409eff;
  margin: 10px 0;
}

.score-max {
  color: #909399;
  font-size: 14px;
}

.stats-section {
  margin-bottom: 30px;
}

.stat-item {
  text-align: center;
  padding: 20px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #67c23a;
  margin-bottom: 5px;
}

.stat-label {
  color: #606266;
  font-size: 14px;
}

.detail-results {
  margin-bottom: 30px;
}

.detail-results h4 {
  margin-bottom: 15px;
  color: #303133;
}

.result-actions {
  text-align: center;
}
</style> 