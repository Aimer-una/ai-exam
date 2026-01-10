<template>
  <div class="video-list-page">
    <!-- 优化后的页面头部 -->
    <div class="page-header">
      <div class="header-main">
        <div class="title-section">
          <h1 class="page-title">
            <el-icon><VideoCamera /></el-icon>
            3分钟技术短视频
          </h1>
        </div>
        <p class="page-desc">3分钟掌握一个技术点，短视频高效学习编程技能</p>
      </div>
    </div>

    <!-- 紧凑的分类导航 -->
    <div class="category-nav">
      <div class="category-list">
        <div 
          class="category-item" 
          :class="{ active: selectedCategoryId === null }" 
          @click="selectCategory(null)"
        >
          全部
        </div>
        <div 
          v-for="category in topCategories" 
          :key="category.id"
          class="category-item" 
          :class="{ active: selectedCategoryId === category.id }"
          @click="selectCategory(category.id)"
        >
          {{ category.name }}
        </div>
      </div>
    </div>

    <!-- 优化的搜索和操作区域 -->
    <div class="search-section">
      <div class="search-controls">
        <!-- 左侧：搜索区域 -->
        <div class="search-area">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索视频内容..."
            prefix-icon="Search"
            @keyup.enter="handleSearch"
            clearable
            style="width: 280px;"
          />
          <el-button type="primary" @click="handleSearch" icon="Search">搜索</el-button>
        </div>
        
        <!-- 右侧：投稿和排序 -->
        <div class="action-area">
          <el-button 
            type="success" 
            size="default"
            @click="showSubmitDialog" 
            icon="Plus"
            class="submit-btn"
          >
            我要投稿
          </el-button>
          
          <div class="sort-buttons">
            <span class="sort-label">排序：</span>
            <el-button-group>
              <el-button 
                :type="sortType === 'latest' ? 'primary' : ''"
                @click="changeSortType('latest')"
                size="small"
              >
                最新
              </el-button>
              <el-button 
                :type="sortType === 'popular' ? 'primary' : ''"
                @click="changeSortType('popular')"
                size="small"
              >
                热门
              </el-button>
              <el-button 
                :type="sortType === 'mostViewed' ? 'primary' : ''"
                @click="changeSortType('mostViewed')"
                size="small"
              >
                观看
              </el-button>
              <el-button 
                :type="sortType === 'mostLiked' ? 'primary' : ''"
                @click="changeSortType('mostLiked')"
                size="small"
              >
                点赞
              </el-button>
            </el-button-group>
          </div>
        </div>
      </div>
    </div>

    <!-- 视频网格列表 -->
    <div class="video-grid" v-loading="loading">
      <div class="video-card" v-for="video in videoList" :key="video.id" @click="goToVideoDetail(video.id)">
        <!-- 视频封面 -->
        <div class="video-cover">
          <img :src="video.coverUrl || defaultCover" :alt="video.title" />
          <div class="video-duration">{{ video.durationText }}</div>
          <div class="video-overlay">
            <el-icon class="play-icon"><CaretRight /></el-icon>
          </div>
        </div>
        
        <!-- 视频信息 -->
        <div class="video-info">
          <h3 class="video-title" :title="video.title">{{ video.title }}</h3>
          <p class="video-desc">{{ video.description }}</p>
          
          <!-- 视频标签 -->
          <div class="video-tags" v-if="video.tags">
            <el-tag 
              v-for="tag in getVideoTags(video.tags)" 
              :key="tag" 
              size="small"
              effect="plain"
            >
              {{ tag }}
            </el-tag>
          </div>
          
          <!-- 视频统计信息 -->
          <div class="video-stats">
            <div class="stats-left">
              <span class="stat-item">
                <el-icon><View /></el-icon>
                {{ formatNumber(video.viewCount) }}
              </span>
              <span class="stat-item">
                <el-icon><Star /></el-icon>
                {{ formatNumber(video.likeCount) }}
              </span>
            </div>
            <div class="stats-right">
              <span class="upload-time">{{ formatTime(video.createdAt) }}</span>
            </div>
          </div>
          
          <!-- 上传者信息 -->
          <div class="uploader-info">
            <el-tag 
              :type="video.uploaderType === 2 ? 'success' : 'info'" 
              size="small"
              effect="plain"
            >
              {{ video.uploaderType === 2 ? '官方' : '投稿' }}
            </el-tag>
            <span class="uploader-name">{{ video.uploaderName }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && videoList.length === 0" class="empty-state">
      <el-empty description="暂无视频内容">
        <el-button type="primary" @click="showSubmitDialog">立即投稿</el-button>
      </el-empty>
    </div>

    <!-- 分页组件 -->
    <div class="pagination-wrapper" v-if="videoList.length > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[12, 24, 48]"
        :total="totalCount"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>

    <!-- 投稿对话框 -->
    <el-dialog v-model="submitDialogVisible" title="视频投稿" width="600px" :close-on-click-modal="false">
      <VideoSubmitForm @success="handleSubmitSuccess" @cancel="submitDialogVisible = false" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  VideoCamera, 
  Plus, 
  Search, 
  CaretRight, 
  View, 
  Star 
} from '@element-plus/icons-vue'
import { getVideos, getPopularVideos, getLatestVideos } from '../api/video'
import { getTopVideoCategories } from '../api/videoCategory'
import VideoSubmitForm from '../components/VideoSubmitForm.vue'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const videoList = ref([])
const topCategories = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const totalCount = ref(0)
const selectedCategoryId = ref(null)
const searchKeyword = ref('')
const sortType = ref('latest')
const submitDialogVisible = ref(false)

// 默认封面图
const defaultCover = '/images/default-video-cover.jpg'

// 生命周期
onMounted(() => {
  loadTopCategories()
  loadVideoList()
})

// 加载顶级分类
const loadTopCategories = async () => {
  try {
    const res = await getTopVideoCategories()
    topCategories.value = res.data || []
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

// 加载视频列表
const loadVideoList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      categoryId: selectedCategoryId.value,
      keyword: searchKeyword.value
    }
    
    let apiCall = getVideos
    
    // 根据排序类型选择不同的API
    if (sortType.value === 'popular' && !selectedCategoryId.value && !searchKeyword.value) {
      apiCall = () => getPopularVideos(pageSize.value)
    } else if (sortType.value === 'latest' && !selectedCategoryId.value && !searchKeyword.value) {
      apiCall = () => getLatestVideos(pageSize.value)
    }
    
    const res = await apiCall(params)
    
    if (res.data.records) {
      // 分页数据
      videoList.value = res.data.records
      totalCount.value = res.data.total
    } else {
      // 直接数组数据（热门、最新接口）
      videoList.value = res.data || []
      totalCount.value = videoList.value.length
    }
  } catch (error) {
    console.error('加载视频列表失败:', error)
    ElMessage.error('加载视频列表失败')
  } finally {
    loading.value = false
  }
}

// 选择分类
const selectCategory = (categoryId) => {
  selectedCategoryId.value = categoryId
  currentPage.value = 1
  loadVideoList()
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  loadVideoList()
}

// 处理页码变化
const handlePageChange = (page) => {
  currentPage.value = page
  loadVideoList()
}

// 处理页面大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadVideoList()
}

// 跳转到视频详情
const goToVideoDetail = (videoId) => {
  router.push(`/video/${videoId}`)
}

// 显示投稿对话框
const showSubmitDialog = () => {
  submitDialogVisible.value = true
}

// 处理投稿成功
const handleSubmitSuccess = () => {
  submitDialogVisible.value = false
  ElMessage.success('视频投稿成功，请等待审核')
  // 刷新列表
  loadVideoList()
}

// 获取视频标签数组
const getVideoTags = (tagsString) => {
  if (!tagsString) return []
  return tagsString.split(',').filter(tag => tag.trim()).slice(0, 3) // 最多显示3个标签
}

// 格式化数字
const formatNumber = (num) => {
  if (!num) return '0'
  if (num < 1000) return num.toString()
  if (num < 10000) return (num / 1000).toFixed(1) + 'k'
  return (num / 10000).toFixed(1) + 'w'
}

// 格式化时间
const formatTime = (timeString) => {
  if (!timeString) return ''
  const date = new Date(timeString)
  const now = new Date()
  const diff = now - date
  
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  const month = 30 * day
  
  if (diff < hour) {
    return Math.floor(diff / minute) + '分钟前'
  } else if (diff < day) {
    return Math.floor(diff / hour) + '小时前'
  } else if (diff < month) {
    return Math.floor(diff / day) + '天前'
  } else {
    return date.toLocaleDateString()
  }
}

// 处理排序变化
const changeSortType = (newSortType) => {
  sortType.value = newSortType
  currentPage.value = 1
  loadVideoList()
}
</script>

<style scoped>
.video-list-page {
  min-height: 100vh;
  background: #f5f7fa;
}

/* 页面头部样式 */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-align: center;
  padding: 30px 20px;
  position: relative;
}

.header-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.title-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-bottom: 8px;
}

.page-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0;
}

.page-title .el-icon {
  margin-right: 12px;
  font-size: 36px;
}

.page-desc {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
}

/* 分类导航样式 */
.category-nav {
  background: white;
  border-bottom: 1px solid #e4e7ed;
  padding: 12px 0;
  overflow-x: auto;
}

.category-list {
  display: flex;
  gap: 8px;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  min-width: max-content;
}

.category-item {
  padding: 8px 16px;
  border-radius: 20px;
  background: #f5f7fa;
  color: #606266;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
  font-size: 14px;
  border: 1px solid transparent;
}

.category-item:hover {
  background: #ecf5ff;
  color: #409eff;
  border-color: #409eff;
}

.category-item.active {
  background: #409eff;
  color: white;
  border-color: #409eff;
}

.category-name {
  font-weight: 500;
}

.category-count {
  font-size: 12px;
  opacity: 0.7;
}

/* 搜索区域样式 */
.search-section {
  background: white;
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;
}

.search-controls {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto;
  gap: 12px;
}

.search-area {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-area {
  display: flex;
  align-items: center;
  gap: 16px;
}

.submit-btn {
  font-weight: 600;
  padding: 10px 20px;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.3);
  transition: all 0.3s;
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.4);
}

.sort-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}

/* 视频网格样式 */
.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  padding: 30px 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.video-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s;
}

.video-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

/* 视频封面样式 */
.video-cover {
  position: relative;
  aspect-ratio: 16/9;
  overflow: hidden;
  background: #f0f2f5;
}

.video-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-duration {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.video-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.video-card:hover .video-overlay {
  opacity: 1;
}

.play-icon {
  font-size: 48px;
  color: white;
}

/* 视频信息样式 */
.video-info {
  padding: 16px;
}

.video-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.video-desc {
  font-size: 13px;
  color: #666;
  margin: 0 0 12px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.video-tags {
  display: flex;
  gap: 6px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.video-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 13px;
  color: #666;
}

.stats-left {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.uploader-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #999;
}

.uploader-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 空状态样式 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
}

/* 分页样式 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 40px 20px;
  background: white;
  margin-top: 20px;
}

/* 响应式样式 */
@media (max-width: 768px) {
  .page-title {
    font-size: 32px;
  }
  
  .video-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 16px;
    padding: 20px 16px;
  }
  
  .search-section {
    padding: 16px;
  }
  
  .search-controls {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .search-area {
    justify-content: center;
  }
  
  .search-area .el-input {
    width: 100% !important;
    max-width: 300px;
  }
  
  .action-area {
    flex-direction: column;
    gap: 12px;
  }
  
  .submit-btn {
    width: 100%;
    max-width: 200px;
    margin: 0 auto;
  }
  
  .sort-buttons {
    justify-content: center;
    flex-wrap: wrap;
  }
  
  .sort-label {
    margin-bottom: 4px;
  }
}
</style> 