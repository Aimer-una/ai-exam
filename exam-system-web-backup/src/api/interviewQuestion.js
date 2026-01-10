import request from '@/utils/request'

// 获取企业真题列表
export function getInterviewQuestions(params) {
  return request({
    url: '/api/interview-questions/list',
    method: 'get',
    params
  })
}

// 获取真题详情
export function getInterviewQuestionDetail(id) {
  return request({
    url: `/api/interview-questions/${id}`,
    method: 'get'
  })
}

// 上传企业真题
export function uploadQuestion(data) {
  return request({
    url: '/api/user-contributions/upload',
    method: 'post',
    data
  })
}

// 获取热门题目
export function getHotQuestions(limit = 10) {
  return request({
    url: '/api/interview-questions/hot',
    method: 'get',
    params: { limit }
  })
}

// 获取最新题目
export function getLatestQuestions(limit = 10) {
  return request({
    url: '/api/interview-questions/latest',
    method: 'get',
    params: { limit }
  })
}

// 增加浏览次数
export function incrementViewCount(id) {
  return request({
    url: `/api/interview-questions/${id}/view`,
    method: 'post'
  })
}

// 获取技术方向统计
export function getDirectionStats() {
  return request({
    url: '/api/interview-questions/stats/direction',
    method: 'get'
  })
}

// 获取公司统计
export function getCompanyStats() {
  return request({
    url: '/api/interview-questions/stats/company',
    method: 'get'
  })
}

// 模拟面试相关API
export function startMockInterview(data) {
  return request({
    url: '/api/mock-interview/start',
    method: 'post',
    data
  })
}

export function submitInterviewAnswer(data) {
  return request({
    url: '/api/mock-interview/submit-answer',
    method: 'post',
    data
  })
}

export function completeMockInterview(interviewId) {
  return request({
    url: `/api/mock-interview/${interviewId}/complete`,
    method: 'post'
  })
}

export function getMockInterviewDetail(interviewId) {
  return request({
    url: `/interview/mock-interview/${interviewId}`,
    method: 'get'
  })
}

export function getUserInterviewRecords(userId, params) {
  return request({
    url: `/api/mock-interview/user/${userId}/records`,
    method: 'get',
    params
  })
}

// 邀请码相关API
export function getInterviewCodes(params) {
  return request({
    url: '/interview/codes',
    method: 'get',
    params
  })
}

export function generateInterviewCodes(data) {
  return request({
    url: '/interview/codes/generate',
    method: 'post',
    data
  })
}

export function activateInterviewCode(code) {
  return request({
    url: '/interview/codes/activate',
    method: 'post',
    data: { code }
  })
}

export function deleteInterviewCode(codeId) {
  return request({
    url: `/interview/codes/${codeId}`,
    method: 'delete'
  })
}

export function getInviteesList() {
  return request({
    url: '/interview/codes/invitees',
    method: 'get'
  })
}

export function requestInterviewCode(data) {
  return request({
    url: '/interview/codes/request',
    method: 'post',
    data
  })
}

// 用户积分相关API
export function getUserCredits(userId) {
  return request({
    url: `/api/user-interview-credits/user/${userId}`,
    method: 'get'
  })
}

export function getActiveCredits(userId) {
  return request({
    url: `/api/user-interview-credits/active/${userId}`,
    method: 'get'
  })
}

// 获取面试结果
export function getInterviewResult(interviewId) {
  return request({
    url: `/interview/result/${interviewId}`,
    method: 'get'
  })
}

// 获取用户面试历史
export function getUserInterviewHistory(params) {
  return request({
    url: '/interview/history',
    method: 'get',
    params
  })
}

// 获取面试统计数据
export function getInterviewStatistics() {
  return request({
    url: '/interview/statistics',
    method: 'get'
  })
}

// 分享面试结果
export function shareInterviewResult(interviewId, shareType) {
  return request({
    url: '/interview/share',
    method: 'post',
    data: {
      interviewId,
      shareType
    }
  })
}

// 获取积分历史
export function getCreditsHistory() {
  return request({
    url: '/interview/credits/history',
    method: 'get'
  })
}

// 用户贡献相关API
// 已彻底删除 getUserContributions 方法
// 已彻底删除 getContributionStats 方法
// 已彻底删除 getContributionRanking 方法
// 已彻底删除 submitContribution 方法 