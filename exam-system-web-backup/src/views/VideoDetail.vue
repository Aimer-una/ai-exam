<template>
  <div class="video-detail-page">
    <!-- 头部区域，返回按钮独立，主标题真正水平+垂直居中 -->
    <div class="video-detail-header">
      <!-- 返回按钮，绝对定位，左上角 -->
      <el-button type="text" icon="ArrowLeft" @click="goBack" class="back-btn">返回</el-button>
      <!-- 主标题和副标题，绝对定位+flex，真正水平+垂直居中 -->
      <div class="header-center">
        <!-- 主标题加底色和分割线 -->
        <div class="main-title-bg">
          <h1 class="main-title">
            <span class="highlight">3分钟</span>技术短视频
          </h1>
        </div>
        <!-- 渐变分割线 -->
        <div class="header-divider"></div>
        <div class="sub-title">高效掌握技术点</div>
      </div>
    </div>
    <div class="video-container" v-loading="loading">
      <div v-if="videoInfo" class="video-content">
        <!-- 视频播放区域 -->
        <div class="video-player-section">
          <div class="video-player">
            <video
              ref="videoPlayerRef"
              :src="videoInfo.fileUrl"
              :poster="videoInfo.coverUrl"
              controls
              preload="metadata"
              @loadedmetadata="onVideoLoaded"
              @timeupdate="onTimeUpdate"
              @ended="onVideoEnded"
              @play="onVideoPlay"
              @pause="onVideoPause"
            >
              您的浏览器不支持视频播放
            </video>
          </div>
        </div>
        <!-- 只保留白色卡片区域内的信息 -->
        <div class="video-info-section">
          <div class="video-header">
            <h1 class="video-title">{{ videoInfo.title }}</h1>
            <div class="video-meta">
              <div class="meta-left">
                <span class="view-count">
                  <el-icon><View /></el-icon>
                  {{ formatNumber(videoInfo.viewCount) }} 次观看
                </span>
                <span class="upload-time">{{ formatTime(videoInfo.createdAt) }}</span>
                <el-tag :type="getUploaderType(videoInfo.uploaderType)" size="small">
                  {{ videoInfo.uploaderType === 2 ? '官方发布' : '用户投稿' }}
                </el-tag>
              </div>
              <div class="meta-actions">
                <el-button
                  :type="videoInfo.isLiked ? 'primary' : 'default'"
                  :icon="videoInfo.isLiked ? 'StarFilled' : 'Star'"
                  @click="toggleLike"
                  :loading="likeLoading"
                >
                  {{ formatNumber(videoInfo.likeCount) }}
                </el-button>
                <el-button icon="Share" @click="shareVideo">分享</el-button>
              </div>
            </div>
          </div>

          <!-- 上传者信息 -->
          <div class="uploader-section">
            <div class="uploader-avatar">
              <el-avatar :size="40">
                {{ videoInfo.uploaderName ? videoInfo.uploaderName.charAt(0) : 'U' }}
              </el-avatar>
            </div>
            <div class="uploader-info">
              <div class="uploader-name">{{ videoInfo.uploaderName }}</div>
              <div class="uploader-stats">
                <span v-if="videoInfo.uploaderType === 2" class="official-badge">
                  <el-icon><Medal /></el-icon>
                  官方认证
                </span>
              </div>
            </div>
          </div>

          <!-- 视频描述 -->
          <div class="video-description">
            <div class="description-header">
              <h3>视频介绍</h3>
            </div>
            <div class="description-content" :class="{ expanded: descriptionExpanded }">
              <p>{{ videoInfo.description }}</p>
            </div>
            <el-button 
              v-if="videoInfo.description && videoInfo.description.length > 100"
              text 
              @click="descriptionExpanded = !descriptionExpanded"
            >
              {{ descriptionExpanded ? '收起' : '展开' }}
            </el-button>
          </div>

          <!-- 视频标签 -->
          <div class="video-tags" v-if="videoInfo.tags">
            <h4>相关标签</h4>
            <div class="tags-list">
              <el-tag
                v-for="tag in getVideoTags(videoInfo.tags)"
                :key="tag"
                @click="searchByTag(tag)"
                style="cursor: pointer; margin-right: 8px; margin-bottom: 8px;"
              >
                # {{ tag }}
              </el-tag>
            </div>
          </div>

          <!-- 视频统计信息 -->
          <div class="video-stats">
            <div class="stats-grid">
              <div class="stat-item">
                <el-icon class="stat-icon"><View /></el-icon>
                <div class="stat-info">
                  <div class="stat-number">{{ formatNumber(videoInfo.viewCount) }}</div>
                  <div class="stat-label">观看次数</div>
                </div>
              </div>
              <div class="stat-item">
                <el-icon class="stat-icon"><Star /></el-icon>
                <div class="stat-info">
                  <div class="stat-number">{{ formatNumber(videoInfo.likeCount) }}</div>
                  <div class="stat-label">点赞数</div>
                </div>
              </div>
              <div class="stat-item">
                <el-icon class="stat-icon"><Clock /></el-icon>
                <div class="stat-info">
                  <div class="stat-number">{{ videoInfo.durationText }}</div>
                  <div class="stat-label">视频时长</div>
                </div>
              </div>
              <div class="stat-item">
                <el-icon class="stat-icon"><Folder /></el-icon>
                <div class="stat-info">
                  <div class="stat-number">
                    <!-- 优先显示分类名称，没有则显示未分类，并用标签美化 -->
                    <el-tag type="info" size="small" v-if="videoInfo.categoryName">{{ videoInfo.categoryName }}</el-tag>
                    <span v-else>未分类</span>
                  </div>
                  <div class="stat-label">所属分类</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 相关推荐 -->
      <div class="related-videos-section">
        <h3 class="section-title">相关推荐</h3>
        <div class="related-videos-grid" v-loading="relatedLoading">
          <div 
            v-for="video in relatedVideos" 
            :key="video.id"
            class="related-video-card"
            @click="goToVideo(video.id)"
          >
            <div class="related-video-cover">
              <img :src="video.coverUrl || defaultCover" :alt="video.title" />
              <div class="video-duration">{{ video.durationText }}</div>
            </div>
            <div class="related-video-info">
              <h4 class="related-video-title">{{ video.title }}</h4>
              <div class="related-video-meta">
                <span class="uploader">{{ video.uploaderName }}</span>
                <span class="view-count">{{ formatNumber(video.viewCount) }} 次观看</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 视频不存在提示 -->
    <div v-if="!loading && !videoInfo" class="video-not-found">
      <el-empty description="视频不存在或已被删除">
        <el-button type="primary" @click="goBack">返回列表</el-button>
      </el-empty>
    </div>

    <!-- 分享对话框 -->
    <el-dialog v-model="shareDialogVisible" title="分享视频" width="400px">
      <div class="share-content">
        <div class="share-url">
          <el-input v-model="shareUrl" readonly>
            <template #append>
              <el-button @click="copyShareUrl">复制</el-button>
            </template>
          </el-input>
        </div>
        <div class="share-qr" v-if="qrCodeUrl">
          <img :src="qrCodeUrl" alt="分享二维码" />
          <p>扫码观看视频</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  View, 
  Star, 
  StarFilled,
  Share, 
  Medal,
  Clock,
  Folder,
  ArrowLeft
} from '@element-plus/icons-vue'
import { getVideoDetail, toggleVideoLike, recordVideoView, getPopularVideos } from '../api/video'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const relatedLoading = ref(false)
const likeLoading = ref(false)
const videoInfo = ref(null)
const relatedVideos = ref([])
const descriptionExpanded = ref(false)
const shareDialogVisible = ref(false)
const shareUrl = ref('')
const qrCodeUrl = ref('')

// 视频播放相关
const videoPlayerRef = ref()
const watchStartTime = ref(0)
const totalWatchTime = ref(0)
const lastRecordTime = ref(0)

// 默认封面
const defaultCover = '/images/default-video-cover.jpg'

// 生命周期
onMounted(() => {
  loadVideoDetail()
  loadRelatedVideos()
})

onUnmounted(() => {
  // 页面离开时记录观看时长
  recordWatchTime()
})

// 加载视频详情
const loadVideoDetail = async () => {
  loading.value = true
  try {
    const videoId = route.params.id
    const res = await getVideoDetail(videoId)
    videoInfo.value = res.data
    
    // 设置分享链接
    shareUrl.value = window.location.href
    
  } catch (error) {
    console.error('加载视频详情失败:', error)
    ElMessage.error('视频不存在或已被删除')
  } finally {
    loading.value = false
  }
}

// 加载相关视频
const loadRelatedVideos = async () => {
  relatedLoading.value = true
  try {
    const res = await getPopularVideos(6)
    relatedVideos.value = res.data || []
  } catch (error) {
    console.error('加载相关视频失败:', error)
  } finally {
    relatedLoading.value = false
  }
}

// 视频播放事件
const onVideoLoaded = () => {
  console.log('视频加载完成')
}

const onVideoPlay = () => {
  watchStartTime.value = Date.now()
}

const onVideoPause = () => {
  recordWatchTime()
}

const onVideoEnded = () => {
  recordWatchTime()
}

const onTimeUpdate = () => {
  // 每30秒记录一次观看时长
  const now = Date.now()
  if (now - lastRecordTime.value >= 30000) {
    recordWatchTime()
    lastRecordTime.value = now
  }
}

// 记录观看时长
const recordWatchTime = async () => {
  if (watchStartTime.value > 0) {
    const watchDuration = Math.floor((Date.now() - watchStartTime.value) / 1000)
    totalWatchTime.value += watchDuration
    
    try {
      await recordVideoView(videoInfo.value.id, totalWatchTime.value)
    } catch (error) {
      console.error('记录观看时长失败:', error)
    }
    
    watchStartTime.value = Date.now()
  }
}

// 切换点赞
const toggleLike = async () => {
  if (likeLoading.value) return
  
  likeLoading.value = true
  try {
    const res = await toggleVideoLike(videoInfo.value.id)
    
    // 更新点赞状态
    videoInfo.value.isLiked = res.data.isLiked
    videoInfo.value.likeCount += res.data.isLiked ? 1 : -1
    
    ElMessage.success(res.data.message)
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('操作失败，请重试')
  } finally {
    likeLoading.value = false
  }
}

// 分享视频
const shareVideo = () => {
  shareDialogVisible.value = true
  // TODO: 生成二维码
}

// 复制分享链接
const copyShareUrl = async () => {
  try {
    await navigator.clipboard.writeText(shareUrl.value)
    ElMessage.success('链接已复制到剪贴板')
  } catch (error) {
    // 降级处理
    const input = document.createElement('input')
    input.value = shareUrl.value
    document.body.appendChild(input)
    input.select()
    document.execCommand('copy')
    document.body.removeChild(input)
    ElMessage.success('链接已复制到剪贴板')
  }
}

// 跳转到其他视频
const goToVideo = (videoId) => {
  router.push(`/video/${videoId}`)
  loadVideoDetail()
  loadRelatedVideos()
}

// 返回列表
const goBack = () => {
  router.back()
}

// 根据标签搜索
const searchByTag = (tag) => {
  router.push(`/videos?keyword=${encodeURIComponent(tag)}`)
}

// 获取上传者类型
const getUploaderType = (type) => {
  return type === 2 ? 'success' : 'info'
}

// 获取视频标签
const getVideoTags = (tagsString) => {
  if (!tagsString) return []
  return tagsString.split(',').filter(tag => tag.trim())
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
</script>

<style scoped>
.video-detail-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.video-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 30px;
}

/* 视频播放区域 */
.video-player-section {
  background: black;
  border-radius: 12px;
  overflow: hidden;
  aspect-ratio: 16/9;
}

.video-player {
  width: 100%;
  height: 100%;
}

.video-player video {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

/* 视频信息区域 */
.video-info-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-top: 20px;
}

.video-header {
  margin-bottom: 24px;
}

.video-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 12px;
  line-height: 1.3;
  color: #303133;
}

.video-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.meta-left {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.view-count {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
  font-size: 14px;
}

.upload-time {
  color: #999;
  font-size: 14px;
}

.meta-actions {
  display: flex;
  gap: 12px;
}

/* 上传者信息 */
.uploader-section {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 0;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 24px;
}

.uploader-info {
  flex: 1;
}

.uploader-name {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.official-badge {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #67c23a;
  font-size: 12px;
}

/* 视频描述 */
.video-description {
  margin-bottom: 24px;
}

.description-header h3 {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 12px;
  color: #303133;
}

.description-content {
  color: #666;
  line-height: 1.6;
  max-height: 60px;
  overflow: hidden;
  transition: max-height 0.3s;
}

.description-content.expanded {
  max-height: none;
}

/* 视频标签 */
.video-tags {
  margin-bottom: 24px;
}

.video-tags h4 {
  font-size: 14px;
  font-weight: 500;
  margin: 0 0 12px;
  color: #303133;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* 视频统计 */
.video-stats {
  padding-top: 24px;
  border-top: 1px solid #ebeef5;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-icon {
  font-size: 20px;
  color: #409eff;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.stat-label {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

/* 相关推荐 */
.related-videos-section {
  grid-column: 2;
  grid-row: 1 / span 2;
  position: sticky;
  top: 120px; /* 头部高度+间距 */
  max-height: calc(100vh - 130px); /* 头部高度+padding，保证一屏 */
  overflow-y: auto;
  padding-right: 2px; /* 防止滚动条遮挡内容 */
}
.section-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 16px;
  color: #303133;
}
.related-videos-grid {
  display: flex;
  flex-direction: column;
  gap: 12px; /* 缩小间距更紧凑 */
}
.related-video-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s;
  box-shadow: 0 1px 4px rgba(64,158,255,0.04);
}
.related-video-card:hover {
  transform: translateY(-2px);
}
.related-video-cover {
  position: relative;
  aspect-ratio: 16/9;
  overflow: hidden;
}
.related-video-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.video-duration {
  position: absolute;
  bottom: 4px;
  right: 4px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 11px;
}
.related-video-info {
  padding: 10px 12px 8px 12px;
}
.related-video-title {
  font-size: 14px;
  font-weight: 500;
  margin: 0 0 6px;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.related-video-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 12px;
  color: #999;
}
/* 视频不存在 */
.video-not-found {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}

/* 分享对话框 */
.share-content {
  text-align: center;
}

.share-url {
  margin-bottom: 20px;
}

.share-qr img {
  width: 150px;
  height: 150px;
  margin-bottom: 10px;
}

/* 响应式样式 */
@media (max-width: 768px) {
  .video-container {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 16px;
  }
  .related-videos-section {
    grid-column: 1;
    grid-row: auto;
    position: static;
    max-height: none;
    overflow: visible;
    padding-right: 0;
    margin-top: 24px;
  }
  .section-title {
    font-size: 16px;
    margin-bottom: 12px;
  }
  .video-title {
    font-size: 20px;
  }
  .meta-left {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
}

.video-detail-header {
  position: relative;
  width: 100%;
  height: 110px;
  background: linear-gradient(90deg, #f8fafc 0%, #eaf6ff 100%);
  border-bottom: 1.5px solid #e4e7ed;
  box-shadow: 0 2px 8px rgba(64,158,255,0.06);
  margin-bottom: 8px;
}
.back-btn {
  position: absolute;
  left: 32px;
  top: 24px;
  font-size: 18px;
  color: #409eff;
  z-index: 2;
  background: transparent;
  font-weight: 500;
  padding: 0 8px;
}
.header-center {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  pointer-events: none;
}
/* 主标题外层底色卡片 */
.main-title-bg {
  background: rgba(255,255,255,0.85);
  border-radius: 18px;
  box-shadow: 0 2px 8px rgba(64,158,255,0.08);
  padding: 8px 32px 6px 32px;
  display: inline-block;
  margin-bottom: 6px;
}
.main-title {
  font-size: 34px;
  font-weight: 900;
  letter-spacing: 4px;
  color: #222;
  margin: 0;
  line-height: 1.2;
  text-align: center;
  user-select: none;
  pointer-events: auto;
  background: none;
}
.main-title .highlight {
  color: #409eff;
  background: linear-gradient(90deg, #67c23a 0%, #409eff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: 900;
  margin-right: 4px;
}
/* 渐变分割线 */
.header-divider {
  width: 120px;
  height: 4px;
  border-radius: 2px;
  background: linear-gradient(90deg, #67c23a 0%, #409eff 100%);
  margin: 8px 0 0 0;
  opacity: 0.7;
}
.sub-title {
  font-size: 16px;
  color: #8a99b3;
  margin-top: 10px;
  text-align: center;
  letter-spacing: 2px;
  font-weight: 400;
  pointer-events: auto;
}
@media (max-width: 768px) {
  .video-detail-header {
    height: 80px;
    padding: 0 8px;
  }
  .main-title-bg {
    padding: 4px 12px 3px 12px;
    border-radius: 10px;
  }
  .main-title {
    font-size: 22px;
    letter-spacing: 2px;
  }
  .header-divider {
    width: 60px;
    height: 3px;
    margin: 5px 0 0 0;
  }
  .sub-title {
    font-size: 13px;
    margin-top: 6px;
  }
  .back-btn {
    left: 8px;
    top: 12px;
    font-size: 15px;
  }
}
</style> 