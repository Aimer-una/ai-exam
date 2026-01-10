<template>
  <div class="home-page">
    <!-- 顶部导航栏 -->
    <div class="navbar">
      <div class="logo">
        <img src="../assets/logo.svg" alt="logo" class="logo-img" />
        <span class="title">智能学习平台</span>
      </div>
      <div class="nav-actions">
        <el-button type="primary" @click="goToExam" icon="Document">考试入口</el-button>
        <el-button @click="goToRanking" icon="Trophy">考试排行榜</el-button>
        <el-button @click="showAdminLogin" icon="Edit">管理员后台</el-button>
      </div>
    </div>

    <!-- 主体内容区域 -->
    <div class="main-container">
      <!-- 顶部横幅区域 -->
      <div class="hero-section">
        <!-- 轮播图区域 -->
        <div class="carousel-section">
          <el-carousel 
            v-model="activeBannerIndex"
            :interval="5000" 
            height="280px"
            indicator-position="inside"
            arrow="hover"
          >
            <el-carousel-item v-for="(banner, index) in bannerList" :key="index">
              <div class="banner-item" @click="handleBannerClick(banner)">
                <img :src="banner.imageUrl" alt="" class="banner-img" />
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>

        <!-- 轮播公告区域 -->
        <div class="notice-section">
          <div class="notice-header">
            <el-icon class="notice-icon"><Bell /></el-icon>
            <span class="notice-title">系统公告</span>
          </div>
          <div class="notice-carousel">
            <el-carousel 
              direction="vertical" 
              :interval="3000" 
              height="220px"
              :show-arrow="false"
              indicator-position="none"
            >
              <el-carousel-item v-for="(notice, index) in noticeList" :key="index">
                <div class="notice-item" @click="handleNoticeClick(notice)">
                  <div class="notice-date">
                    <span class="date-day">{{ formatNoticeDate(notice.createTime).day }}</span>
                    <span class="date-month">{{ formatNoticeDate(notice.createTime).month }}</span>
                  </div>
                  <div class="notice-content">
                    <h4 class="notice-item-title">{{ notice.title }}</h4>
                    <p class="notice-item-desc">{{ notice.content }}</p>
                    <div class="notice-meta">
                      <el-tag size="small" :type="getNoticeTypeTag(notice.type)">
                        {{ getNoticeTypeText(notice.type) }}
                      </el-tag>
                      <span class="notice-time">{{ formatTime(notice.createTime) }}</span>
                    </div>
                  </div>
                </div>
              </el-carousel-item>
            </el-carousel>
          </div>
        </div>
      </div>

      <!-- 快捷功能区域 -->
      <div class="quick-actions">
        <h2 class="section-title">快捷功能</h2>
        <div class="action-cards">
          <div class="action-card" @click="goToExam">
            <el-icon class="card-icon exam-icon"><Document /></el-icon>
            <h3>智能考试</h3>
            <p>AI智能出题，自动批阅，精准评估学习效果</p>
          </div>
          <div class="action-card" @click="goToPractice">
            <el-icon class="card-icon practice-icon"><Edit /></el-icon>
            <h3>智能刷题</h3>
            <p>AI推荐题目，个性化练习，智能分析弱项</p>
          </div>
          <!-- 移除企业真题卡片 -->
          <!--
          <div class="action-card" @click="goToInterviewQuestions">
            <el-icon class="card-icon interview-icon"><ChatDotRound /></el-icon>
            <h3>企业真题</h3>
            <p>各大企业真实面试题，提前了解面试重点</p>
          </div>
          -->
          <!-- 移除模拟面试卡片 -->
          <!--
          <div class="action-card" @click="goToMockInterview">
            <el-icon class="card-icon mock-icon"><Microphone /></el-icon>
            <h3>模拟面试</h3>
            <p>AI模拟面试官，语音答题，智能评分反馈</p>
          </div>
          -->
          <div class="action-card" @click="goToRanking">
            <el-icon class="card-icon ranking-icon"><Trophy /></el-icon>
            <h3>学习排行</h3>
            <p>查看学习排名，与同学竞争，激发学习动力</p>
          </div>
          <div class="action-card" @click="goToAnalysis">
            <el-icon class="card-icon analysis-icon"><DataAnalysis /></el-icon>
            <h3>AI分析</h3>
            <p>智能学习报告，能力雷达图，个性化学习建议</p>
          </div>
          <div class="action-card" @click="goToVideos">
            <el-icon class="card-icon video-icon"><VideoPlay /></el-icon>
            <h3>视频百科</h3>
            <p>技术点讲解视频，分类学习，互动点赞分享</p>
          </div>
        </div>
      </div>

      <!-- 热门题目推荐 -->
      <div class="popular-section">
        <div class="section-header">
          <h2 class="section-title">热门题目</h2>
          <el-button text @click="goToPractice">查看更多 →</el-button>
        </div>
        <div class="popular-grid">
          <div class="popular-card" v-for="question in popularQuestions" :key="question.id">
            <div class="question-type">
              <el-tag :type="getQuestionTypeTag(question.type)" size="small">
                {{ getQuestionTypeText(question.type) }}
              </el-tag>
              <el-tag :type="getDifficultyType(question.difficulty)" size="small">
                {{ getDifficultyText(question.difficulty) }}
              </el-tag>
            </div>
            <h4 class="question-title">{{ question.title }}</h4>
            <p class="question-category">{{ question.categoryName }}</p>
            <div class="question-stats">
              <span><el-icon><View /></el-icon> {{ question.viewCount || 0 }}次查看</span>
              <span><el-icon><Check /></el-icon> {{ question.correctRate || 0 }}%正确率</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 统计数据展示 -->
      <div class="stats-section">
        <div class="stats-grid">
          <div class="stat-card">
            <el-icon class="stat-icon"><Document /></el-icon>
            <div class="stat-content">
              <h3 class="stat-number">{{ stats.questionCount || 0 }}</h3>
              <p class="stat-label">题目总数</p>
            </div>
          </div>
          <div class="stat-card">
            <el-icon class="stat-icon"><User /></el-icon>
            <div class="stat-content">
              <h3 class="stat-number">{{ stats.userCount || 0 }}</h3>
              <p class="stat-label">用户总数</p>
            </div>
          </div>
          <div class="stat-card">
            <el-icon class="stat-icon"><Files /></el-icon>
            <div class="stat-content">
              <h3 class="stat-number">{{ stats.examCount || 0 }}</h3>
              <p class="stat-label">考试场次</p>
            </div>
          </div>
          <div class="stat-card">
            <el-icon class="stat-icon"><TrendCharts /></el-icon>
            <div class="stat-content">
              <h3 class="stat-number">{{ stats.todayExamCount || 0 }}</h3>
              <p class="stat-label">今日考试</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 管理员登录对话框 -->
    <el-dialog v-model="adminLoginVisible" title="管理员登录" width="400px" :close-on-click-modal="false">
      <el-form :model="adminLoginForm" :rules="adminLoginRules" ref="adminLoginFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="adminLoginForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="adminLoginForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adminLoginVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAdminLogin" :loading="adminLoginLoading">登录</el-button>
      </template>
    </el-dialog>

    <!-- 公告详情对话框 -->
    <el-dialog v-model="noticeDetailVisible" :title="selectedNotice?.title" width="600px">
      <div class="notice-detail" v-if="selectedNotice">
        <div class="notice-detail-meta">
          <el-tag :type="getNoticeTypeTag(selectedNotice.type)">
            {{ getNoticeTypeText(selectedNotice.type) }}
          </el-tag>
          <span class="notice-detail-time">{{ formatTime(selectedNotice.createTime) }}</span>
        </div>
        <div class="notice-detail-content" v-html="selectedNotice.content"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Search, Document, Edit, Trophy, Bell, DataAnalysis, View, Check, User, Files, TrendCharts, VideoPlay, ChatDotRound, Microphone
} from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()

// 轮播图数据（示例数据）
const bannerList = ref([])

// 公告数据（示例数据）
const noticeList = ref([])

// 热门题目数据
const popularQuestions = ref([])

// 统计数据
const stats = ref({
  questionCount: 0,
  userCount: 0,
  examCount: 0,
  todayExamCount: 0
})

// 管理员登录相关
const adminLoginVisible = ref(false)
const adminLoginLoading = ref(false)
const adminLoginFormRef = ref()
const adminLoginForm = reactive({ username: '', password: '' })
const adminLoginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 公告详情
const noticeDetailVisible = ref(false)
const selectedNotice = ref(null)

// 轮播图当前索引
const activeBannerIndex = ref(0)

// 获取轮播图数据
const getBannerList = async () => {
  try {
    const res = await request.get('/api/banners/active')
    bannerList.value = res.data || []
    console.log('轮播图数据加载完成')
  } catch (error) {
    console.error('获取轮播图数据失败：', error)
    // 如果API失败，使用示例数据
    bannerList.value = [
      {
        id: 1,
        title: '智能AI生成题目',
        description: '利用先进AI技术，快速生成高质量考试题目',
        imageUrl: '/api/banners/ai-generate.jpg',
        linkUrl: '/ai-generate',
        isActive: true
      },
      {
        id: 2,
        title: '海量题库资源',
        description: '覆盖多个学科领域，题目类型丰富多样',
        imageUrl: '/api/banners/question-bank.jpg',
        linkUrl: '/practice',
        isActive: true
      },
      {
        id: 3,
        title: '智能学习分析',
        description: '详细的答题报告，帮助您精准提升',
        imageUrl: '/api/banners/analysis.jpg',
        linkUrl: '/analysis',
        isActive: true
      }
    ]
  }
}

// 获取公告数据
const getNoticeList = async () => {
  try {
    const res = await request.get('/api/notices/latest', { params: { limit: 5 } })
    noticeList.value = res.data || []
    console.log('公告数据加载完成')
  } catch (error) {
    console.error('获取公告数据失败：', error)
    // 如果API失败，使用示例数据
    noticeList.value = [
      {
        id: 1,
        title: '系统升级公告',
        content: '为了提供更好的服务体验，系统将于本周末进行升级维护。维护期间可能会出现短暂的服务中断，请大家合理安排考试时间。感谢您的理解与配合！',
        type: 'SYSTEM',
        createTime: '2024-06-24 10:00:00',
        isActive: true
      },
      {
        id: 2,
        title: '新增AI智能生成功能',
        content: '我们很高兴地宣布，系统新增了AI智能生成题目功能，可以快速生成高质量的考试题目。该功能支持多种题型和难度级别，让出题更加高效便捷。',
        type: 'FEATURE',
        createTime: '2024-06-23 15:30:00',
        isActive: true
      },
      {
        id: 3,
        title: '考试注意事项',
        content: '各位同学在参加在线考试时，请确保网络连接稳定，不要随意切换窗口。考试过程中如遇到技术问题，请及时联系技术支持。祝大家取得好成绩！',
        type: 'NOTICE',
        createTime: '2024-06-22 09:00:00',
        isActive: true
      }
    ]
  }
}

// 获取热门题目
const getPopularQuestions = async () => {
  try {
    const res = await request.get('/api/questions/popular', { params: { size: 6 } })
    popularQuestions.value = res.data || []
  } catch (error) {
    console.error('获取热门题目失败：', error)
  }
}

// 获取统计数据
const getStats = async () => {
  try {
    // 调用后台API获取真实统计数据  // 从数据库获取真实数据
    const res = await request.get('/api/stats/overview')
    if (res.code === 200) {
      stats.value = {
        questionCount: res.data.questionCount || 0,
        userCount: res.data.userCount || 0,
        examCount: res.data.examCount || 0,
        todayExamCount: res.data.todayExamCount || 0
      }
      console.log('统计数据获取成功：', stats.value)
    } else {
      console.error('获取统计数据失败：', res.message)
      // 使用默认值  // 接口失败时的备用数据
      stats.value = {
        questionCount: 0,
        userCount: 0,
        examCount: 0,
        todayExamCount: 0
      }
    }
  } catch (error) {
    console.error('获取统计数据失败：', error)
    // 接口调用失败时使用默认值  // 网络错误时的备用数据
    stats.value = {
      questionCount: 0,
      userCount: 0,
      examCount: 0,
      todayExamCount: 0
    }
  }
}

// 轮播图点击处理
const handleBannerClick = (banner) => {
  if (banner.linkUrl) {
    if (banner.linkUrl.startsWith('http://') || banner.linkUrl.startsWith('https://')) {
      window.open(banner.linkUrl, '_blank')
    } else {
      router.push(banner.linkUrl)
    }
  }
}

// 公告点击处理
const handleNoticeClick = (notice) => {
  selectedNotice.value = notice
  noticeDetailVisible.value = true
}

// 格式化公告日期
const formatNoticeDate = (dateStr) => {
  const date = new Date(dateStr)
  return {
    day: date.getDate().toString().padStart(2, '0'),
    month: (date.getMonth() + 1).toString().padStart(2, '0') + '月'
  }
}

// 格式化时间
const formatTime = (dateStr) => {
  return new Date(dateStr).toLocaleString('zh-CN')
}

// 获取公告类型标签样式
const getNoticeTypeTag = (type) => {
  const tagMap = {
    'SYSTEM': 'danger',
    'FEATURE': 'success',
    'NOTICE': 'warning'
  }
  return tagMap[type] || 'info'
}

// 获取公告类型文本
const getNoticeTypeText = (type) => {
  const textMap = {
    'SYSTEM': '系统',
    'FEATURE': '新功能',
    'NOTICE': '通知'
  }
  return textMap[type] || '其他'
}

// 获取题目类型标签样式
const getQuestionTypeTag = (type) => {
  const tagMap = {
    'CHOICE': 'primary',
    'JUDGE': 'success',
    'TEXT': 'warning'
  }
  return tagMap[type] || 'info'
}

// 获取题目类型文本
const getQuestionTypeText = (type) => {
  const textMap = {
    'CHOICE': '选择题',
    'JUDGE': '判断题',
    'TEXT': '简答题'
  }
  return textMap[type] || type
}

// 获取难度标签样式
const getDifficultyType = (difficulty) => {
  const typeMap = {
    'EASY': 'success',
    'MEDIUM': 'warning',
    'HARD': 'danger'
  }
  return typeMap[difficulty] || 'info'
}

// 获取难度文本
const getDifficultyText = (difficulty) => {
  const textMap = {
    'EASY': '简单',
    'MEDIUM': '中等',
    'HARD': '困难'
  }
  return textMap[difficulty] || difficulty
}

// 导航功能
const goToExam = () => {
  router.push('/exam/list')
}

const goToPractice = () => {
  router.push('/practice')
}

const goToRanking = () => {
  router.push('/exam-ranking')
}

const goToAnalysis = () => {
  router.push('/analysis')
}

const goToVideos = () => {
  router.push('/videos')
}

const goToInterviewQuestions = () => {
  router.push('/interview-questions')
}

const goToMockInterview = () => {
  router.push('/mock-interview')
}

// 管理员登录弹窗
const showAdminLogin = () => {
  adminLoginVisible.value = true
}

// 管理员登录
const handleAdminLogin = async () => {
  if (!adminLoginFormRef.value) return
  await adminLoginFormRef.value.validate(async (valid) => {
    if (valid) {
      adminLoginLoading.value = true
      try {
        const res = await request.post('/api/user/login', adminLoginForm)
        localStorage.setItem('userInfo', JSON.stringify(res.data))
        ElMessage.success('登录成功，正在跳转到管理员后台...')
        adminLoginVisible.value = false
        adminLoginForm.username = ''
        adminLoginForm.password = ''
        router.push('/admin')
      } catch (e) {
        // 错误提示由axios拦截器处理
      } finally {
        adminLoginLoading.value = false
      }
    }
  })
}

// 初始化
onMounted(() => {
  getBannerList()
  getNoticeList()
  getPopularQuestions()
  getStats()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);  /* 小清新渐变：薄荷绿到淡粉色 */
}

/* 导航栏样式 */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 0 40px;
  height: 64px;
  box-shadow: 0 2px 20px rgba(0,0,0,0.1);
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
  background: linear-gradient(45deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 主容器 */
.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

/* 顶部横幅区域 */
.hero-section {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
  margin-bottom: 60px;
}

/* 轮播图区域 */
.carousel-section {
  /* 移除背景色、阴影、边框，保证图片无遮挡 */
  background: none;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: none;
  border: none;
}

.banner-item {
  width: 100%;
  height: 100%;
  position: relative;
  cursor: pointer;
  overflow: hidden;
  transition: transform 0.3s ease;
}

.banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  /* 极浅黑色半透明蒙层，图片主体非常清晰 */
  background: rgba(0,0,0,0.15);
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-content {
  text-align: center;
  color: white;
  padding: 24px;  /* 适当减少内边距 */
}

.banner-title {
  font-size: 2rem;  /* 适当减小标题字体 */
  font-weight: bold;
  margin-bottom: 14px;
  /* 主标题底色极淡，图片更清晰 */
  background: rgba(0,0,0,0.12);
  border-radius: 10px;
  padding: 6px 24px;
  display: inline-block;
  text-shadow: 0 4px 16px rgba(0,0,0,0.7), 0 2px 4px rgba(0,0,0,0.5);
}

.banner-desc {
  font-size: 1.1rem;  /* 适当减小描述字体 */
  margin-bottom: 20px;
  opacity: 0.95;
  line-height: 1.6;
  /* 副标题底色极淡，图片更清晰 */
  background: rgba(0,0,0,0.06);
  border-radius: 8px;
  padding: 4px 16px;
  display: inline-block;
  text-shadow: 0 2px 8px rgba(0,0,0,0.5);
}

/* 公告区域 */
.notice-section {
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.1);
  overflow: hidden;
}

.notice-header {
  display: flex;
  align-items: center;
  padding: 20px 24px;
  background: linear-gradient(45deg, #ff6b6b, #ffa500);
  color: white;
}

.notice-icon {
  font-size: 20px;
  margin-right: 8px;
}

.notice-title {
  font-size: 1.1rem;
  font-weight: bold;
}

.notice-carousel {
  height: 220px;
}

.notice-item {
  display: flex;
  padding: 20px 24px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  height: 100%;
  align-items: center;
}

.notice-item:hover {
  background-color: #f8f9fa;
}

.notice-date {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  background: linear-gradient(45deg, #667eea, #764ba2);
  color: white;
  border-radius: 12px;
  margin-right: 16px;
  flex-shrink: 0;
}

.date-day {
  font-size: 1.5rem;
  font-weight: bold;
  line-height: 1;
}

.date-month {
  font-size: 0.75rem;
  opacity: 0.8;
}

.notice-content {
  flex: 1;
  min-width: 0;
}

.notice-item-title {
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: 8px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notice-item-desc {
  font-size: 0.875rem;
  color: #666;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.notice-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.notice-time {
  font-size: 0.75rem;
  color: #999;
}

/* 快捷功能区域 */
.quick-actions {
  margin-bottom: 60px;
}

.section-title {
  font-size: 2rem;
  font-weight: bold;
  color: white;
  text-align: center;
  margin-bottom: 40px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
}

.action-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
}

.action-card {
  background: white;
  border-radius: 16px;
  padding: 32px 24px;
  text-align: center;
  box-shadow: 0 10px 40px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);
}

.card-icon {
  font-size: 3rem;
  margin-bottom: 16px;
}

.exam-icon { color: #667eea; }
.practice-icon { color: #ff6b6b; }
.ranking-icon { color: #ffa500; }
.analysis-icon { color: #51cf66; }
.video-icon { color: #ffa500; }
.interview-icon { color: #ff6b6b; }
.mock-icon { color: #ffa500; }

.action-card h3 {
  font-size: 1.25rem;
  font-weight: bold;
  margin-bottom: 12px;
  color: #333;
}

.action-card p {
  color: #666;
  line-height: 1.6;
}

/* 热门题目区域 */
.popular-section {
  margin-bottom: 60px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.popular-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 24px;
}

.popular-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  transition: all 0.3s ease;
}

.popular-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0,0,0,0.12);
}

.question-type {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.question-title {
  font-size: 1rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.question-category {
  font-size: 0.875rem;
  color: #666;
  margin-bottom: 12px;
}

.question-stats {
  display: flex;
  justify-content: space-between;
  font-size: 0.75rem;
  color: #999;
}

.question-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 统计数据区域 */
.stats-section {
  margin-bottom: 40px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 24px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  padding: 32px 24px;
  text-align: center;
  color: white;
  transition: all 0.3s ease;
}

.stat-card:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-4px);
}

.stat-icon {
  font-size: 2.5rem;
  margin-bottom: 16px;
  opacity: 0.8;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 8px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
}

.stat-label {
  font-size: 1rem;
  opacity: 0.9;
}

/* 公告详情对话框 */
.notice-detail-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.notice-detail-time {
  color: #999;
  font-size: 0.875rem;
}

.notice-detail-content {
  line-height: 1.8;
  color: #333;
}

/* 响应式设计 */
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
  
  .main-container {
    padding: 20px 16px;
  }
  
  .hero-section {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .carousel-section .el-carousel {
    height: 280px !important;  /* 移动端轮播图高度和桌面端保持一致 */
  }
  
  .banner-item {
    height: 100% !important;
  }
  
  .banner-img {
    height: 100% !important;
  }
  
  .banner-title {
    font-size: 1.4rem;  /* 移动端调整标题字体 */
  }
  
  .banner-desc {
    font-size: 0.95rem;  /* 移动端调整描述字体 */
  }
  
  .section-title {
    font-size: 1.5rem;
  }
  
  .action-cards {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  }
  
  .popular-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .action-card {
    padding: 24px 16px;
  }
}

.banner-text-content {
  /* 一行紧凑显示，居中对齐 */
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 14px;
  margin-bottom: 8px;
}
.banner-text-content .banner-title,
.banner-text-content .banner-desc {
  background: none; /* 去除底色 */
  color: #222;
  font-size: 1.1rem;
  font-weight: 500;
  margin: 0;
  padding: 0;
  display: inline;
  text-shadow: none;
}
.banner-text-content .banner-title {
  font-weight: bold;
  font-size: 1.25rem;
  margin-right: 8px;
}
.banner-text-content .banner-desc {
  background: none; /* 去除底色 */
  color: #444;
  font-size: 1.1rem;
  margin: 0;
  display: inline;
}
.banner-text-content .el-button {
  margin-left: 12px;
  padding: 4px 18px;
  font-size: 1rem;
}
</style> 