<template>
  <div class="home-container">
    <div class="page-header">
      <el-page-header @back="goBack" title="返回" content="选择试卷开始考试" />
    </div>

    <div class="toolbar">
      <el-input 
        v-model="searchKeyword" 
        placeholder="按试卷名称搜索" 
        class="search-input"
        clearable
        @clear="fetchPublishedPapers"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button :icon="Search" @click="handleSearch" />
        </template>
      </el-input>
    </div>

    <div class="paper-list-vertical" v-loading="loading">
      <div v-if="paperList.length > 0" class="list-content">
        <div 
          v-for="paper in paperList" 
          :key="paper.id" 
          class="paper-item"
        >
          <div class="paper-info">
            <h3 class="paper-name">{{ paper.name }}</h3>
            <p class="paper-description">{{ paper.description || '暂无描述' }}</p>
            <div class="paper-meta">
              <span><el-icon><CollectionTag /></el-icon> 题目数量: {{ paper.questionCount }} 道</span>
              <span><el-icon><TrophyBase /></el-icon> 总分: {{ paper.totalScore }} 分</span>
              <span><el-icon><Timer /></el-icon> 考试时长: {{ paper.duration }} 分钟</span>
            </div>
          </div>
          <div class="paper-actions">
            <el-button type="primary" size="large" @click="handleStartExam(paper.id)" :loading="startingExamId === paper.id">开始考试</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无已发布的试卷或未找到匹配项" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPapers } from '../api/paper.js'
import { startExam } from '../api/exam.js'
import { Search, CollectionTag, TrophyBase, Timer } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const paperList = ref([])
const searchKeyword = ref('')
const startingExamId = ref(null);

const fetchPublishedPapers = async () => {
  loading.value = true
  try {
    const params = { 
      status: 'PUBLISHED',
      name: searchKeyword.value
    }
    const res = await getPapers(params)
    paperList.value = res.data
  } catch (error) {
    ElMessage.error('获取试卷列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  fetchPublishedPapers()
}

const handleStartExam = async (paperId) => {
  // 跳转到考试开始页面，让用户输入姓名
  router.push(`/exam/start/${paperId}`)
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  fetchPublishedPapers()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
}

.toolbar {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}

.search-input {
  width: 50%;
  max-width: 600px;
}

.paper-list-vertical {
  width: 100%;
}

.list-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.paper-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #f8f9fa;
  padding: 25px 30px;
  border-radius: 12px;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.paper-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.1);
  border-color: #409eff;
}

.paper-info {
  flex-grow: 1;
}

.paper-name {
  margin: 0 0 10px;
  font-size: 22px;
  color: #303133;
}

.paper-description {
  margin: 0 0 15px;
  color: #606266;
  font-size: 15px;
  min-height: 22px;
}

.paper-meta {
  display: flex;
  gap: 25px;
  color: #909399;
  font-size: 14px;
  align-items: center;
}

.paper-meta span {
  display: flex;
  align-items: center;
  gap: 6px;
}

.paper-actions {
  flex-shrink: 0;
  margin-left: 30px;
}
</style> 