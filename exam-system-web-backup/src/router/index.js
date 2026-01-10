import { createRouter, createWebHistory } from 'vue-router'

// 路由配置
const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('../views/AdminLayout.vue'),
    redirect: '/admin/welcome',
    children: [
      {
        path: 'welcome',
        name: 'Welcome',
        component: () => import('../views/Welcome.vue'),
        meta: { title: '欢迎' }
      },
      {
        path: 'question-manage',
        name: 'QuestionManage',
        component: () => import('../views/QuestionManage.vue'),
        meta: { title: '题目管理' }
      },
      {
        path: 'category-manage',
        name: 'CategoryManage',
        component: () => import('../views/CategoryManage.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'paper-manage',
        name: 'PaperManage',
        component: () => import('../views/PaperManage.vue'),
        meta: { title: '试卷管理' }
      },
      {
        path: 'score-manage',
        name: 'ScoreManage',
        component: () => import('../views/ScoreManage.vue'),
        meta: { title: '成绩管理' }
      },
      {
        path: 'banner-manage',
        name: 'BannerManage',
        component: () => import('../views/BannerManage.vue'),
        meta: { title: '轮播图管理' }
      },
      {
        path: 'notice-manage',
        name: 'NoticeManage',
        component: () => import('../views/NoticeManage.vue'),
        meta: { title: '公告管理' }
      },
      {
        path: 'video-manage',
        name: 'VideoManage',
        component: () => import('../views/VideoManage.vue'),
        meta: { title: '视频管理' }
      },
      {
        path: 'video-category-manage',
        name: 'VideoCategoryManage',
        component: () => import('../views/VideoCategoryManage.vue'),
        meta: { title: '视频分类管理' }
      },
      {
        path: 'paper-create',
        name: 'PaperCreate',
        component: () => import('../views/PaperCreate.vue'),
        meta: { title: '创建新试卷' }
      }
    ]
  },
  {
    path: '/exam/list',
    name: 'PaperListForExam',
    component: () => import('../views/PaperListForExam.vue'),
    meta: { title: '选择考试' }
  },
  {
    path: '/exam/start/:paperId',
    name: 'ExamStart',
    component: () => import('../views/ExamStart.vue'),
    meta: { title: '开始考试' }
  },
  {
    path: '/exam/:id',
    name: 'Exam',
    component: () => import('../views/Exam.vue'),
    meta: { title: '在线考试' }
  },
  {
    path: '/exam-result',
    name: 'ExamResult',
    component: () => import('../views/ExamResult.vue'),
    meta: { title: '考试结果' }
  },
  {
    path: '/exam-result/:id',
    name: 'ExamResultById',
    component: () => import('../views/ExamResult.vue'),
    meta: { title: '考试结果' }
  },
  {
    path: '/exam-ranking',
    name: 'ExamRanking',
    component: () => import('../views/ExamRanking.vue'),
    meta: { title: '考试排行榜' }
  },
  {
    path: '/practice',
    name: 'Practice',
    component: () => import('../views/Practice.vue'),
    meta: { title: '刷题练习' }
  },
  // 3分钟技术短视频相关路由
  {
    path: '/videos',
    name: 'VideoList',
    component: () => import('../views/VideoList.vue'),
    meta: { title: '3分钟技术短视频' }
  },
  {
    path: '/videos/:id',
    name: 'VideoDetail',
    component: () => import('../views/VideoDetail.vue'),
    meta: { title: '3分钟技术短视频' }
  },
  // 兼容 /video/:id 旧路径，重定向到 /videos/:id
  {
    path: '/video/:id',
    redirect: to => `/videos/${to.params.id}`
  },
  // 企业真题相关路由
  {
    path: '/interview-questions',
    name: 'InterviewQuestionList',
    component: () => import('../views/InterviewQuestionList.vue'),
    meta: { title: '企业面试真题' }
  },
  {
    path: '/interview-questions/:id',
    name: 'InterviewQuestionDetail',
    component: () => import('../views/InterviewQuestionDetail.vue'),
    meta: { title: '真题详情' }
  },
  {
    path: '/interview-practice/:id',
    name: 'InterviewPractice',
    component: () => import('../views/InterviewPractice.vue'),
    meta: { title: '真题练习' }
  },
  // 模拟面试相关路由
  {
    path: '/mock-interview',
    name: 'MockInterview',
    component: () => import('../views/MockInterview.vue'),
    meta: { title: '模拟面试' }
  },
  {
    path: '/mock-interview/:id',
    name: 'MockInterviewDetail',
    component: () => import('../views/MockInterviewDetail.vue'),
    meta: { title: '面试详情' }
  },
  {
    path: '/interview-result/:id',
    name: 'InterviewResult',
    component: () => import('../views/InterviewResult.vue'),
    meta: { title: '面试结果' }
  },
  // 用户贡献相关路由
  // 已彻底删除 path:'/user-contributions' 路由配置
  // 邀请码相关路由
  {
    path: '/interview-codes',
    name: 'InterviewCodes',
    component: () => import('../views/InterviewCodes.vue'),
    meta: { title: '邀请码管理' }
  },
  {
    path: '/interview-codes/activate',
    name: 'ActivateCode',
    component: () => import('../views/ActivateCode.vue'),
    meta: { title: '激活邀请码' }
  },
  {
    path: '/company-manage', // 企业管理页面路由
    name: 'CompanyManage', // 路由名称
    component: () => import('../views/CompanyManage.vue'), // 组件路径
    meta: { title: '企业管理' } // 页面标题
  },
  {
    path: '/pending-question-manage', // 用户上传审核页面路由
    name: 'PendingQuestionManage', // 路由名称
    component: () => import('../views/PendingQuestionManage.vue'), // 组件路径
    meta: { title: '用户上传审核' } // 页面标题
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title + ' - 智能学习平台'
  }
  
  // 防止从考试结果页面返回到考试页面的逻辑
  if (from.path && from.path.includes('/exam-result/') && to.path && to.path.includes('/exam/')) {
    // 如果用户试图从结果页面返回到考试页面
    console.log('检测到从结果页面返回到考试页面的尝试')
    
    // 检查目标考试ID和来源结果页面ID是否相同
    const fromExamId = from.params.id
    const toExamId = to.params.id
    
    if (fromExamId === toExamId) {
      // 如果是同一个考试，阻止返回并重定向到考试列表
      console.log('阻止返回到已完成的考试，重定向到考试列表')
      next('/exam/list')
      return
    }
  }
  
  // 如果是访问考试页面，我们不能在这里检查考试状态（因为需要API调用）
  // 这个检查已经在Exam.vue的getExamData函数中处理了
  
  next()
})

// 路由后置守卫 - 设置页面标题  // 每次路由切换后设置页面标题
router.afterEach((to) => {
  document.title = to.meta.title + ' - 智能学习平台'  // 更改标题后缀
})

export default router 