import request from '../utils/request'

/**
 * 考试相关的API
 */

// 开始考试  // 启动考试功能
export function startExam(paperId, studentName) {
  return request({
    url: '/api/exams/start',  // 添加/api前缀
    method: 'post',
    data: { 
      paperId,
      studentName 
    }
  })
}

// 提交答案  // 提交考试答案
export function submitAnswers(examRecordId, data) {
  return request({
    url: `/api/exams/${examRecordId}/submit`,  // 添加/api前缀
    method: 'post',
    data
  })
}

// 触发试卷批阅  // AI自动批阅功能
export function gradeExam(examRecordId) {
  return request({
    url: `/api/exams/${examRecordId}/grade`,  // 添加/api前缀
    method: 'post'
  })
}

// 获取我的考试记录  // 查询考试历史记录
export function getMyExamRecords() {
  return request({
    url: '/api/exams/records',  // 添加/api前缀
    method: 'get'
  })
}

// 获取考试记录详情  // 查看单个考试记录详情
export function getExamRecordById(id) {
  return request({
    url: `/api/exams/${id}`,  // 添加/api前缀
    method: 'get'
  })
} 