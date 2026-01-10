<template>
  <div class="exam-start-container">
    <div class="exam-start-card">
      <div class="card-header">
        <h2 class="title">开始考试</h2>
        <p class="subtitle">请填写您的信息开始考试</p>
      </div>
      
      <div class="paper-info" v-if="paperInfo">
        <h3 class="paper-name">{{ paperInfo.name }}</h3>
        <p class="paper-description">{{ paperInfo.description || '暂无描述' }}</p>
        <div class="paper-meta">
          <span><el-icon><CollectionTag /></el-icon> 题目数量: {{ paperInfo.questionCount }} 道</span>
          <span><el-icon><TrophyBase /></el-icon> 总分: {{ paperInfo.totalScore }} 分</span>
          <span><el-icon><Timer /></el-icon> 考试时长: {{ paperInfo.duration }} 分钟</span>
        </div>
      </div>

      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        class="exam-form"
        @submit.prevent="handleStartExam"
      >
        <el-form-item label="考生姓名" prop="studentName">
          <el-input 
            v-model="form.studentName" 
            placeholder="请输入您的姓名"
            size="large"
            maxlength="20"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button 
            type="primary" 
            size="large" 
            @click="handleStartExam" 
            :loading="loading"
            style="width: 100%"
          >
            开始考试
          </el-button>
        </el-form-item>
      </el-form>

      <div class="exam-rules">
        <h4>考试规则</h4>
        <ul>
          <li>请确保网络连接稳定</li>
          <li>考试过程中请勿切换窗口或刷新页面</li>
          <li>考试时间到后将自动交卷</li>
          <li>提交后将无法修改答案</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPaperById } from '../api/paper.js'
import { startExam } from '../api/exam.js'
import { CollectionTag, TrophyBase, Timer } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const formRef = ref(null)
const loading = ref(false)
const paperInfo = ref(null)

// 表单数据
const form = ref({
  studentName: '' // 考生姓名
})

// 表单验证规则
const rules = {
  studentName: [
    { required: true, message: '请输入考生姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ]
}

// 获取试卷信息
const getPaperInfo = async () => {
  try {
    const paperId = route.params.paperId
    const res = await getPaperById(paperId)
    paperInfo.value = res.data
  } catch (error) {
    ElMessage.error('获取试卷信息失败')
    router.push('/exam/list')
  }
}

// 开始考试
const handleStartExam = async () => {
  try {
    // 验证表单
    await formRef.value.validate()
    
    loading.value = true
    const paperId = route.params.paperId
    
    // 调用开始考试API
    const res = await startExam(paperId, form.value.studentName)
    
    ElMessage.success('考试创建成功，正在跳转...')
    
    // 跳转到考试页面
    router.push(`/exam/${res.data.id}`)
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('开始考试失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getPaperInfo()
})
</script>

<style scoped>
.exam-start-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.exam-start-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  padding: 40px;
  max-width: 600px;
  width: 100%;
}

.card-header {
  text-align: center;
  margin-bottom: 30px;
}

.title {
  font-size: 28px;
  color: #303133;
  margin: 0 0 10px;
  font-weight: 600;
}

.subtitle {
  color: #909399;
  margin: 0;
  font-size: 16px;
}

.paper-info {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 30px;
  border: 1px solid #e9ecef;
}

.paper-name {
  font-size: 20px;
  color: #303133;
  margin: 0 0 10px;
  font-weight: 600;
}

.paper-description {
  color: #606266;
  margin: 0 0 15px;
  line-height: 1.5;
}

.paper-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  color: #909399;
  font-size: 14px;
}

.paper-meta span {
  display: flex;
  align-items: center;
  gap: 6px;
}

.exam-form {
  margin-bottom: 30px;
}

.exam-rules {
  background: #fff7e6;
  border: 1px solid #ffd591;
  border-radius: 8px;
  padding: 20px;
}

.exam-rules h4 {
  color: #d46b08;
  margin: 0 0 15px;
  font-size: 16px;
}

.exam-rules ul {
  margin: 0;
  padding-left: 20px;
  color: #8b5a00;
}

.exam-rules li {
  margin-bottom: 8px;
  line-height: 1.5;
}

.exam-rules li:last-child {
  margin-bottom: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .exam-start-card {
    padding: 30px 20px;
  }
  
  .title {
    font-size: 24px;
  }
  
  .paper-meta {
    flex-direction: column;
    gap: 10px;
  }
}
</style> 