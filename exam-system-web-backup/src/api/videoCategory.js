import request from '../utils/request'

/**
 * 视频分类相关的API
 */

/**
 * 获取分类列表（包含视频数量）
 */
export function getVideoCategories() {
  return request({
    url: '/api/video-categories',
    method: 'get'
  })
}

/**
 * 获取分类树形结构
 */
export function getVideoCategoryTree() {
  return request({
    url: '/api/video-categories/tree',
    method: 'get'
  })
}

/**
 * 获取顶级分类列表
 */
export function getTopVideoCategories() {
  return request({
    url: '/api/video-categories/top',
    method: 'get'
  })
}

/**
 * 根据父级分类ID获取子分类
 * @param {number} parentId - 父级分类ID
 */
export function getChildVideoCategories(parentId) {
  return request({
    url: `/api/video-categories/children/${parentId}`,
    method: 'get'
  })
}

/**
 * 根据ID获取分类详情
 * @param {number} id - 分类ID
 */
export function getVideoCategoryById(id) {
  return request({
    url: `/api/video-categories/${id}`,
    method: 'get'
  })
}

/**
 * 添加分类
 * @param {Object} category - 分类信息
 */
export function addVideoCategory(category) {
  return request({
    url: '/api/video-categories',
    method: 'post',
    data: category
  })
}

/**
 * 更新分类
 * @param {Object} category - 分类信息
 */
export function updateVideoCategory(category) {
  return request({
    url: '/api/video-categories',
    method: 'put',
    data: category
  })
}

/**
 * 删除分类
 * @param {number} id - 分类ID
 */
export function deleteVideoCategory(id) {
  return request({
    url: `/api/video-categories/${id}`,
    method: 'delete'
  })
}

/**
 * 获取分类树形结构（别名）
 */
export function getCategoryTree() {
  return request({
    url: '/api/video-categories/tree',
    method: 'get'
  })
} 