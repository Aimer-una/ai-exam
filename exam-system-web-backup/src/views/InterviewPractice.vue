<template>
  <div class="interview-practice">
    <!-- 页面标题 -->
    <div class="practice-header">
      <el-button @click="$router.go(-1)" icon="el-icon-arrow-left">返回</el-button>
      <h2>真题练习</h2>
    </div>

    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="8" animated />
    </div>

    <div v-else class="practice-content">
      <!-- 题目信息 -->
      <el-card class="question-card">
        <template #header>
          <div class="card-header">
            <span>题目信息</span>
          </div>
        </template>
        <div class="question-meta">
          <el-tag :type="getDirectionType(question.direction)">{{ getDirectionLabel(question.direction) }}</el-tag>
          <el-tag :type="getDifficultyType(question.difficultyLevel)">{{ getDifficultyLabel(question.difficultyLevel) }}</el-tag>
          <span class="company">{{ question.companyName }}</span>
          <span class="year">{{ question.interviewYear }}</span>
        </div>
        <div class="question-content">
          <h3>{{ question.questionContent }}</h3>
        </div>
      </el-card>

      <!-- 答题区域 -->
      <el-card class="answer-card">
        <template #header>
          <span>我的答案</span>
        </template>
        <el-form :model="answerForm" :rules="answerRules" ref="answerFormRef">
          <el-form-item label="文字作答" prop="text">
            <el-input v-model="answerForm.text" type="textarea" :rows="6" placeholder="请详细描述你的答案..."></el-input>
          </el-form-item>
          <el-form-item label="语音作答">
            <div class="voice-section">
              <el-button v-if="!isRecording" type="primary" @click="startRecording" :disabled="!isVoiceSupported">
                <i class="el-icon-microphone"></i> 开始录音
              </el-button>
              <el-button v-else type="danger" @click="stopRecording">
                <i class="el-icon-video-pause"></i> 停止录音
              </el-button>
              <span v-if="isRecording" class="recording-time">{{ recordingTime }}s</span>
              <div v-if="answerForm.voiceUrl" class="voice-player">
                <audio :src="answerForm.voiceUrl" controls></audio>
                <el-button size="small" @click="deleteVoice">删除录音</el-button>
              </div>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="submitting">提交答案</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- AI评分结果 -->
      <el-card class="ai-score-card" v-if="aiScore">
        <template #header>
          <span>AI评分结果</span>
        </template>
        <div class="score-section">
          <div class="score-number">得分：{{ aiScore.score }} / 100</div>
          <div class="score-comment">AI评价：{{ aiScore.comment }}</div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getInterviewQuestionDetail, submitInterviewAnswer } from '@/api/interviewQuestion'

export default {
  name: 'InterviewPractice',
  setup() {
    const route = useRoute()
    // 题目信息
    const loading = ref(false)
    const question = ref({})
    // 答案相关
    const answerFormRef = ref()
    const answerForm = reactive({
      text: '',
      voiceUrl: ''
    })
    const answerRules = {
      text: [{ required: true, message: '请填写文字答案', trigger: 'blur' }]
    }
    const submitting = ref(false)
    // AI评分结果
    const aiScore = ref(null)
    // 语音录制
    const isRecording = ref(false)
    const recordingTime = ref(0)
    const recordingTimer = ref(null)
    const mediaRecorder = ref(null)
    const audioChunks = ref([])
    const isVoiceSupported = ref('MediaRecorder' in window)

    // 获取题目详情
    const fetchQuestion = async () => {
      loading.value = true
      try {
        const res = await getInterviewQuestionDetail(route.params.id)
        question.value = res.data
      } catch (error) {
        ElMessage.error('获取题目失败')
      } finally {
        loading.value = false
      }
    }

    // 提交答案
    const handleSubmit = async () => {
      try {
        await answerFormRef.value.validate()
        submitting.value = true
        // 提交答案到后端，获取AI评分
        const res = await submitInterviewAnswer({
          questionId: question.value.id,
          userAnswer: answerForm.text,
          voiceFileUrl: answerForm.voiceUrl
        })
        aiScore.value = res.data
        ElMessage.success('提交成功，已获得AI评分！')
      } catch (error) {
        ElMessage.error('提交失败')
      } finally {
        submitting.value = false
      }
    }

    // 语音录制相关
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
          answerForm.voiceUrl = URL.createObjectURL(audioBlob)
        }
        mediaRecorder.value.start()
        isRecording.value = true
        recordingTime.value = 0
        recordingTimer.value = setInterval(() => {
          recordingTime.value++
        }, 1000)
      } catch (error) {
        ElMessage.error('无法访问麦克风')
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
        mediaRecorder.value.stream.getTracks().forEach(track => track.stop())
      }
    }
    const deleteVoice = () => {
      answerForm.voiceUrl = ''
    }

    // 工具函数
    const getDirectionType = (direction) => {
      const types = {
        java: 'primary', frontend: 'success', bigdata: 'warning', algorithm: 'danger', devops: 'info', testing: ''
      }
      return types[direction] || ''
    }
    const getDirectionLabel = (direction) => {
      const labels = {
        java: 'Java', frontend: '前端', bigdata: '大数据', algorithm: '算法', devops: '运维', testing: '测试'
      }
      return labels[direction] || direction
    }
    const getDifficultyType = (difficulty) => {
      const types = { easy: 'success', medium: 'warning', hard: 'danger' }
      return types[difficulty] || ''
    }
    const getDifficultyLabel = (difficulty) => {
      const labels = { easy: '简单', medium: '中等', hard: '困难' }
      return labels[difficulty] || difficulty
    }

    onMounted(() => {
      fetchQuestion()
    })

    return {
      loading,
      question,
      answerFormRef,
      answerForm,
      answerRules,
      submitting,
      aiScore,
      isRecording,
      recordingTime,
      isVoiceSupported,
      startRecording,
      stopRecording,
      deleteVoice,
      getDirectionType,
      getDirectionLabel,
      getDifficultyType,
      getDifficultyLabel,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.interview-practice {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}
.practice-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}
.practice-header h2 {
  margin: 0;
  color: #303133;
}
.loading-container {
  padding: 40px;
}
.practice-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.question-meta {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  align-items: center;
}
.company, .year {
  color: #909399;
  font-size: 13px;
}
.question-content h3 {
  color: #303133;
  margin-bottom: 10px;
}
.voice-section {
  display: flex;
  align-items: center;
  gap: 15px;
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
.ai-score-card {
  background: #f8f9fa;
}
.score-section {
  text-align: center;
  padding: 20px;
}
.score-number {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
}
.score-comment {
  color: #606266;
  font-size: 16px;
}
</style> 