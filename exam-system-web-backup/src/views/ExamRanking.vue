<template>
  <div class="exam-ranking-page">
    <!-- 页面标题 - 重新设计 -->
    <div class="page-header">
      <div class="header-decoration">
        <div class="trophy-animation">🏆</div>
        <div class="stars">
          <span class="star">⭐</span>
          <span class="star">⭐</span>
          <span class="star">⭐</span>
        </div>
      </div>
      <h2 class="main-title">🏆 考试排行榜 🏆</h2>
      <p class="subtitle">🎯 挑战极限，追求卓越！看看谁是学霸之王？</p>
      <div class="title-underline"></div>
    </div>

    <!-- 筛选条件 - 美化 -->
    <div class="filter-bar">
      <div class="filter-label">🔍 筛选条件：</div>
      <el-select 
        v-model="selectedPaperId" 
        placeholder="📚 选择试卷" 
        clearable 
        style="width: 300px"
        @change="loadRanking"
        class="custom-select"
      >
        <el-option 
          v-for="paper in paperList" 
          :key="paper.id" 
          :label="paper.name" 
          :value="paper.id" 
        />
      </el-select>
      <el-select 
        v-model="rankingLimit" 
        placeholder="📊 显示数量" 
        style="width: 150px"
        @change="loadRanking"
        class="custom-select"
      >
        <el-option label="前10名" :value="10" />
        <el-option label="前20名" :value="20" />
        <el-option label="前50名" :value="50" />
        <el-option label="前100名" :value="100" />
      </el-select>
      <el-button 
        type="primary" 
        @click="loadRanking" 
        :loading="loading" 
        icon="Refresh"
        class="refresh-btn"
      >
        🔄 刷新排行榜
      </el-button>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧排行榜列表 -->
      <div class="ranking-container">
        <!-- 冠军展示区 -->
        <div v-if="rankingList.length > 0" class="champion-showcase">
          <div class="champion-crown">👑</div>
          <div class="champion-info">
            <div class="champion-name">{{ rankingList[0].studentName }}</div>
            <div class="champion-score">{{ rankingList[0].score }}分</div>
            <div class="champion-title">🎉 当前考试之王！🎉</div>
          </div>
        </div>

        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="10" animated />
        </div>
        
        <div v-else-if="rankingList.length > 0" class="ranking-list">
          <div 
            v-for="(record, index) in rankingList" 
            :key="record.id" 
            class="ranking-item"
            :class="{ 'top-three': index < 3 }"
          >
            <div class="rank-number" :class="getRankClass(index + 1)">
              <span v-if="index === 0">🥇</span>
              <span v-else-if="index === 1">🥈</span>
              <span v-else-if="index === 2">🥉</span>
              <span v-else>{{ index + 1 }}</span>
            </div>
            <div class="student-info">
              <div class="student-name">{{ record.studentName }}</div>
              <div class="paper-name">📝 {{ record.paper?.name }}</div>
              <div class="exam-time">📅 {{ formatDateTime(record.endTime) }}</div>
            </div>
            <div class="score-info">
              <div class="score">{{ record.score }}</div>
              <div class="total-score">/ {{ record.paper?.totalScore }}</div>
              <div class="percentage">
                {{ calculatePercentage(record.score, record.paper?.totalScore) }}%
              </div>
            </div>
          </div>
        </div>
        
        <div v-else class="empty-state">
          <div class="empty-icon">📭</div>
          <div class="empty-text">暂无排行榜数据</div>
          <div class="empty-hint">快去参加考试，成为第一个上榜的人吧！</div>
        </div>
      </div>

      <!-- 右侧统计信息 -->
      <div v-if="allRecords.length > 0" class="statistics-sidebar">
        <div class="stats-title">{{ statsTitle }}</div>
        <div class="stats-vertical">
          <div class="stat-card-vertical">
            <div class="stat-icon">👥</div>
            <div class="stat-info">
              <div class="stat-value">{{ totalParticipants }}</div>
              <div class="stat-label">参与人数</div>
            </div>
          </div>
          <div class="stat-card-vertical">
            <div class="stat-icon">📊</div>
            <div class="stat-info">
              <div class="stat-value">{{ averageScore }}</div>
              <div class="stat-label">平均分</div>
            </div>
          </div>
          <div class="stat-card-vertical">
            <div class="stat-icon">🎯</div>
            <div class="stat-info">
              <div class="stat-value">{{ maxScore }}</div>
              <div class="stat-label">最高分</div>
            </div>
          </div>
          <div class="stat-card-vertical">
            <div class="stat-icon">📉</div>
            <div class="stat-info">
              <div class="stat-value">{{ minScore }}</div>
              <div class="stat-label">最低分</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部激励区域 -->
    <div class="motivation-section">
      <div class="motivation-text">💪 每一次挑战都是成长的机会！加油冲刺更高的排名吧！</div>
      <div class="floating-emojis">
        <span class="emoji">🌟</span>
        <span class="emoji">🚀</span>
        <span class="emoji">💎</span>
        <span class="emoji">🔥</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import request from '../utils/request'

// 响应式数据
const loading = ref(false)
const rankingList = ref([])
const paperList = ref([])
const selectedPaperId = ref(null)
const rankingLimit = ref(10)
const allRecords = ref([]) // 用于统计的所有考试记录

// 计算属性 - 基于所有记录进行统计
const averageScore = computed(() => {
  if (allRecords.value.length === 0) return 0
  const total = allRecords.value.reduce((sum, record) => sum + Number(record.score), 0)
  return (total / allRecords.value.length).toFixed(1)
})

const maxScore = computed(() => {
  if (allRecords.value.length === 0) return 0
  return Math.max(...allRecords.value.map(record => Number(record.score)))
})

const minScore = computed(() => {
  if (allRecords.value.length === 0) return 0
  return Math.min(...allRecords.value.map(record => Number(record.score)))
})

const totalParticipants = computed(() => {
  return allRecords.value.length
})

// 动态统计标题
const statsTitle = computed(() => {
  if (selectedPaperId.value) {
    const selectedPaper = paperList.value.find(p => p.id === selectedPaperId.value)
    return `📊 ${selectedPaper?.name || '选中试卷'} 统计`
  }
  return '📊 全部试卷统计'
})

// 获取试卷列表
const getPaperList = async () => {
  try {
    // 调用后端试卷列表API，只传递状态筛选参数
    const res = await request.get('/api/papers/list', {
      params: {
        status: 'PUBLISHED'  // 只获取已发布的试卷
      }
    })
    // 修正数据解析：后端返回的数据直接在res.data中，不是res.data.records
    paperList.value = res.data || []
    console.log('试卷列表加载成功，共', paperList.value.length, '个试卷')
  } catch (error) {
    console.error('获取试卷列表失败：', error)
    ElMessage.error('获取试卷列表失败')
  }
}

// 加载排行榜数据
const loadRanking = async () => {
  loading.value = true
  try {
    // 修正API调用参数，使用后端支持的paperId和limit参数
    const displayParams = {
      paperId: selectedPaperId.value,   // 试卷ID筛选参数
      limit: rankingLimit.value        // 显示数量限制参数
    }
    
    const statsParams = {
      paperId: selectedPaperId.value,   // 试卷ID筛选参数  
      limit: 1000                      // 统计时获取所有记录
    }
    
    // 并行调用两个API：一个用于显示，一个用于统计
    const [rankingRes, statsRes] = await Promise.all([
      request.get('/api/exam-records/ranking', { params: displayParams }),
      request.get('/api/exam-records/ranking', { params: statsParams })
    ])
    
    // 设置排行榜数据和统计数据
    rankingList.value = rankingRes.data || []
    allRecords.value = statsRes.data || []
  } catch (error) {
    console.error('获取排行榜数据失败：', error)
    ElMessage.error('获取排行榜数据失败')
  } finally {
    loading.value = false
  }
}

// 获取排名样式类
const getRankClass = (rank) => {
  if (rank === 1) return 'rank-gold'
  if (rank === 2) return 'rank-silver'
  if (rank === 3) return 'rank-bronze'
  return 'rank-normal'
}

// 计算百分比
const calculatePercentage = (score, totalScore) => {
  if (!score || !totalScore) return 0
  return ((score / totalScore) * 100).toFixed(1)
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

onMounted(() => {
  getPaperList()
  loadRanking()
})
</script>

<style scoped>
/* 全局背景 */
html, body {
  margin: 0;
  padding: 0;
  min-height: 100vh;
}

.exam-ranking-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  background: linear-gradient(135deg, #a8b5ff 0%, #b8c5ff 100%);
  min-height: 100vh;
  position: relative;
}

/* 添加全屏背景 */
.exam-ranking-page::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #a8b5ff 0%, #b8c5ff 100%);
  z-index: -1;
}

/* 页面标题重新设计 */
.page-header {
  text-align: center;
  margin-bottom: 40px;
  position: relative;
}

.header-decoration {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.trophy-animation {
  font-size: 48px;
  animation: bounce 2s ease-in-out infinite;
}

.stars {
  display: flex;
  gap: 10px;
}

.star {
  font-size: 24px;
  animation: twinkle 1.5s ease-in-out infinite;
}

.star:nth-child(2) {
  animation-delay: 0.5s;
}

.star:nth-child(3) {
  animation-delay: 1s;
}

.main-title {
  font-size: 36px;
  color: white;
  margin: 0 0 15px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  font-weight: 700;
}

.subtitle {
  color: rgba(255, 255, 255, 0.9);
  margin: 0 0 20px;
  font-size: 18px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.title-underline {
  width: 100px;
  height: 4px;
  background: linear-gradient(90deg, #ff6b6b, #ffa500, #48bb78);
  margin: 0 auto;
  border-radius: 2px;
  animation: rainbow 3s ease-in-out infinite;
}

/* 筛选条件美化 */
.filter-bar {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
  justify-content: center;
  align-items: center;
  background: rgba(255, 255, 255, 0.15);
  padding: 20px;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.filter-label {
  color: white;
  font-weight: 600;
  font-size: 16px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.custom-select {
  border-radius: 12px;
}

.refresh-btn {
  border-radius: 12px !important;
  font-weight: 600 !important;
  padding: 12px 24px !important;
  background: linear-gradient(135deg, #ff6b6b, #ffa500) !important;
  border: none !important;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3) !important;
  transition: all 0.3s ease !important;
}

.refresh-btn:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4) !important;
}

/* 冠军展示区 */
.champion-showcase {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  padding: 30px;
  border-radius: 20px;
  text-align: center;
  margin-bottom: 30px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(255, 215, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.champion-showcase::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  animation: sweep 3s ease-in-out infinite;
}

.champion-crown {
  font-size: 48px;
  margin-bottom: 15px;
  animation: crownFloat 2s ease-in-out infinite;
}

.champion-name {
  font-size: 28px;
  font-weight: 800;
  color: #b8860b;
  margin-bottom: 10px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.champion-score {
  font-size: 36px;
  font-weight: 900;
  color: #8b6914;
  margin-bottom: 10px;
}

.champion-title {
  font-size: 16px;
  color: #8b6914;
  font-weight: 600;
}

/* 主内容区域 */
.main-content {
  display: flex;
  gap: 30px;
  align-items: flex-start;
}

.ranking-container {
  flex: 2;
  margin-bottom: 0;
}

.loading-container {
  padding: 40px;
  text-align: center;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.ranking-item {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.ranking-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

/* 前三名特殊样式保持不变，但调整文字颜色 */
.ranking-item.top-three {
  color: white;
  border: 2px solid transparent;
  position: relative;
  overflow: hidden;
  font-weight: 500;
}

/* 第一名 - 优雅橙红色 */
.ranking-item.top-three:nth-child(1) {
  background: linear-gradient(135deg, #ff6b6b 0%, #ffa500 50%, #ff8c42 100%);
  border-color: #ff6b6b;
  box-shadow: 0 6px 25px rgba(255, 107, 107, 0.4);
}

.ranking-item.top-three:nth-child(1)::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #ff4757, #ff6b6b, #ff4757);
  animation: firstShine 2s ease-in-out infinite;
}

/* 第二名 - 现代蓝紫色 */
.ranking-item.top-three:nth-child(2) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #5a67d8 100%);
  border-color: #667eea;
  box-shadow: 0 6px 25px rgba(102, 126, 234, 0.4);
}

.ranking-item.top-three:nth-child(2)::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #4c63d2, #667eea, #4c63d2);
  animation: secondShine 2.5s ease-in-out infinite;
}

/* 第三名 - 温暖绿色 */
.ranking-item.top-three:nth-child(3) {
  background: linear-gradient(135deg, #48bb78 0%, #38a169 50%, #2f855a 100%);
  border-color: #48bb78;
  box-shadow: 0 6px 25px rgba(72, 187, 120, 0.4);
}

.ranking-item.top-three:nth-child(3)::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #38a169, #48bb78, #38a169);
  animation: thirdShine 3s ease-in-out infinite;
}

/* 前三名悬停效果增强 */
.ranking-item.top-three:hover {
  transform: translateY(-4px) scale(1.01);
  z-index: 10;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
}

.rank-number {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: bold;
  margin-right: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.ranking-item:hover .rank-number {
  transform: scale(1.1);
}

.rank-gold {
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  color: #8b6914;
  border: 3px solid #ffed4e;
  box-shadow: 0 4px 20px rgba(255, 215, 0, 0.5);
}

.rank-silver {
  background: linear-gradient(135deg, #c0c0c0 0%, #e5e5e5 100%);
  color: #666;
  border: 3px solid #e5e5e5;
  box-shadow: 0 4px 20px rgba(192, 192, 192, 0.5);
}

.rank-bronze {
  background: linear-gradient(135deg, #cd7f32 0%, #daa520 100%);
  color: white;
  border: 3px solid #daa520;
  box-shadow: 0 4px 20px rgba(205, 127, 50, 0.5);
}

.rank-normal {
  background: #667eea;
  color: white;
  border: 2px solid #5a67d8;
}

.student-info {
  flex: 1;
}

.student-name {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 5px;
  color: #2d3748;
}

.paper-name {
  font-size: 14px;
  color: #4a5568;
  margin-bottom: 3px;
  font-weight: 500;
}

.exam-time {
  font-size: 12px;
  color: #718096;
  font-weight: 500;
}

.score-info {
  text-align: right;
  margin-left: 20px;
}

.score {
  font-size: 24px;
  font-weight: bold;
  color: #667eea;
}

.total-score {
  font-size: 14px;
  color: #4a5568;
  font-weight: 600;
}

.percentage {
  font-size: 12px;
  color: #48bb78;
  margin-top: 5px;
  font-weight: 600;
}

/* 前三名文字样式统一 */
.top-three .score {
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  font-weight: 700;
}

.top-three .total-score {
  color: rgba(255, 255, 255, 0.9);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.top-three .percentage {
  color: rgba(255, 255, 255, 0.95);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
  font-weight: 600;
}

.top-three .student-name {
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
  font-weight: 600;
  color: white;
}

.top-three .paper-name {
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
  color: rgba(255, 255, 255, 0.9);
}

.top-three .exam-time {
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
  color: rgba(255, 255, 255, 0.8);
}

/* 前三名排名圆圈重新设计 */
.ranking-item.top-three:nth-child(1) .rank-number {
  background: rgba(255, 255, 255, 0.95);
  color: #e53e3e;
  border: 3px solid rgba(255, 255, 255, 0.8);
  font-weight: 900;
  font-size: 22px;
  text-shadow: none;
}

.ranking-item.top-three:nth-child(2) .rank-number {
  background: rgba(255, 255, 255, 0.95);
  color: #4c63d2;
  border: 3px solid rgba(255, 255, 255, 0.8);
  font-weight: 800;
  font-size: 22px;
  text-shadow: none;
}

.ranking-item.top-three:nth-child(3) .rank-number {
  background: rgba(255, 255, 255, 0.95);
  color: #2f855a;
  border: 3px solid rgba(255, 255, 255, 0.8);
  font-weight: 800;
  font-size: 22px;
  text-shadow: none;
}

/* 统计侧边栏 */
.statistics-sidebar {
  flex: 1;
  max-width: 300px;
  min-width: 280px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  height: fit-content;
  position: sticky;
  top: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.stats-title {
  font-size: 16px;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 15px;
  padding-bottom: 8px;
  border-bottom: 2px solid #e1ecf4;
  text-align: center;
  word-break: break-all;
  line-height: 1.4;
}

.stats-vertical {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stat-card-vertical {
  flex: 1;
  text-align: center;
  padding: 20px;
  background: rgba(248, 249, 255, 0.8);
  color: #333;
  border-radius: 12px;
  border: 1px solid #e1ecf4;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.stat-card-vertical:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
  background: rgba(240, 246, 255, 0.9);
}

.stat-icon {
  font-size: 28px;
  margin-bottom: 12px;
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.1));
}

.stat-info {
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 6px;
  text-shadow: none;
}

.stat-label {
  font-size: 13px;
  color: #4a5568;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  animation: float 3s ease-in-out infinite;
}

.empty-text {
  font-size: 20px;
  color: #4a5568;
  margin-bottom: 10px;
  font-weight: 600;
}

.empty-hint {
  font-size: 14px;
  color: #718096;
  font-weight: 500;
}

/* 底部激励区域 */
.motivation-section {
  margin-top: 40px;
  text-align: center;
  background: rgba(255, 255, 255, 0.15);
  padding: 30px;
  border-radius: 20px;
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.motivation-text {
  font-size: 18px;
  color: white;
  margin-bottom: 20px;
  font-weight: 600;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
}

.floating-emojis {
  display: flex;
  justify-content: center;
  gap: 30px;
}

.emoji {
  font-size: 32px;
  animation: float 2s ease-in-out infinite;
}

.emoji:nth-child(1) { animation-delay: 0s; }
.emoji:nth-child(2) { animation-delay: 0.5s; }
.emoji:nth-child(3) { animation-delay: 1s; }
.emoji:nth-child(4) { animation-delay: 1.5s; }

/* 动画效果 */
@keyframes bounce {
  0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(-30px); }
  60% { transform: translateY(-15px); }
}

@keyframes twinkle {
  0%, 100% { opacity: 0.5; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.2); }
}

@keyframes rainbow {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

@keyframes sweep {
  0% { transform: translateX(-100%) translateY(-100%) rotate(45deg); }
  100% { transform: translateX(100%) translateY(100%) rotate(45deg); }
}

@keyframes crownFloat {
  0%, 100% { transform: translateY(0) rotate(-3deg); }
  50% { transform: translateY(-10px) rotate(3deg); }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

@keyframes firstShine {
  0%, 100% { opacity: 0.8; }
  50% { opacity: 1; }
}

@keyframes secondShine {
  0%, 100% { opacity: 0.7; }
  50% { opacity: 1; }
}

@keyframes thirdShine {
  0%, 100% { opacity: 0.6; }
  50% { opacity: 0.9; }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .exam-ranking-page {
    padding: 15px;
  }
  
  .main-title {
    font-size: 28px;
  }
  
  .subtitle {
    font-size: 16px;
  }
  
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .main-content {
    flex-direction: column;
    gap: 20px;
  }
  
  .ranking-container {
    flex: none;
  }
  
  .statistics-sidebar {
    position: static;
    max-width: none;
    min-width: auto;
    margin-top: 20px;
  }
  
  .champion-showcase {
    padding: 20px;
  }
  
  .champion-name {
    font-size: 24px;
  }
  
  .champion-score {
    font-size: 28px;
  }
  
  .stats-vertical {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
  }
  
  .stat-card-vertical {
    padding: 15px;
  }
  
  .stat-value {
    font-size: 20px;
  }
  
  .stat-icon {
    font-size: 24px;
    margin-bottom: 8px;
  }
  
  .ranking-item {
    flex-direction: column;
    text-align: center;
    gap: 15px;
    padding: 15px;
  }
  
  .rank-number {
    margin-right: 0;
    width: 50px;
    height: 50px;
    font-size: 18px;
  }
  
  .score-info {
    margin-left: 0;
  }
  
  .score {
    font-size: 20px;
  }
  
  .floating-emojis {
    gap: 20px;
  }
  
  .emoji {
    font-size: 24px;
  }
}
</style> 