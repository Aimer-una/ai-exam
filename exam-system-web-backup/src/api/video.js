import request from '../utils/request'

/**
 * 视频相关的API
 */

// ========== 用户端视频API ==========

/**
 * 获取视频列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码，默认1
 * @param {number} params.size - 每页大小，默认10
 * @param {number} params.categoryId - 分类ID（可选）
 * @param {string} params.keyword - 搜索关键字（可选）
 */
export function getVideos(params) {
  return request({
    url: '/api/videos',
    method: 'get',
    params
  })
}

/**
 * 获取视频详情
 * @param {number} id - 视频ID
 */
export function getVideoDetail(id) {
  return request({
    url: `/api/videos/${id}`,
    method: 'get'
  })
}

/**
 * 获取热门视频列表
 * @param {number} limit - 限制数量，默认10
 */
export function getPopularVideos(limit = 10) {
  return request({
    url: '/api/videos/popular',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取最新视频列表
 * @param {number} limit - 限制数量，默认10
 */
export function getLatestVideos(limit = 10) {
  return request({
    url: '/api/videos/latest',
    method: 'get',
    params: { limit }
  })
}

/**
 * 记录视频观看
 * @param {number} videoId - 视频ID
 * @param {number} viewDuration - 观看时长（秒）
 */
export function recordVideoView(videoId, viewDuration) {
  return request({
    url: `/api/videos/${videoId}/view`,
    method: 'post',
    params: { viewDuration }
  })
}

/**
 * 切换视频点赞状态
 * @param {number} videoId - 视频ID
 */
export function toggleVideoLike(videoId) {
  return request({
    url: `/api/videos/${videoId}/like`,
    method: 'post'
  })
}

/**
 * 用户投稿视频
 * @param {FormData} formData - 包含视频信息和文件的表单数据
 */
export function submitVideo(formData) {
  return request({
    url: '/api/videos/submit',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// ========== 管理端视频API ==========

/**
 * 管理端获取视频列表
 * @param {Object} params - 查询参数
 */
export function getVideosForAdmin(params) {
  return request({
    url: '/api/admin/videos',
    method: 'get',
    params
  })
}

/**
 * 管理员上传视频
 * @param {FormData} formData - 包含视频信息和文件的表单数据
 */
export function uploadVideoByAdmin(formData) {
  return request({
    url: '/api/admin/videos/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 审核视频
 * @param {number} videoId - 视频ID
 * @param {number} status - 审核状态（1-通过，2-拒绝）
 * @param {string} reason - 审核原因（拒绝时必填）
 */
export function auditVideo(videoId, status, reason) {
  return request({
    url: `/api/admin/videos/${videoId}/audit`,
    method: 'post',
    params: { status, reason }
  })
}

/**
 * 下架视频
 * @param {number} videoId - 视频ID
 */
export function offlineVideo(videoId) {
  return request({
    url: `/api/admin/videos/${videoId}/offline`,
    method: 'post'
  })
}

/**
 * 删除视频
 * @param {number} videoId - 视频ID
 */
export function deleteVideo(videoId) {
  return request({
    url: `/api/admin/videos/${videoId}`,
    method: 'delete'
  })
}

/**
 * 获取视频统计数据
 */
export function getVideoStatistics() {
  return request({
    url: '/api/admin/videos/statistics',
    method: 'get'
  })
}

/**
 * 获取视频详细统计数据
 * @param {number} videoId - 视频ID
 * @param {number} days - 统计天数，默认30天
 */
export function getVideoDetailStats(videoId, days = 30) {
  return request({
    url: `/api/admin/videos/${videoId}/stats`,
    method: 'get',
    params: { days }
  })
} 