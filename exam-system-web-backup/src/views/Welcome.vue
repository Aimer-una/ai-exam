<template>
  <div class="welcome-page">
    <!-- 顶部欢迎卡片 -->
    <el-card class="header-card" shadow="hover">
      <div class="header-content">
        <img src="../assets/logo.svg" alt="logo" class="welcome-logo" />
        <div class="welcome-text">
          <h1>欢迎回来，管理员！</h1>
          <p>今天是 {{ currentDate }}，祝您有美好的一天。</p>
        </div>
      </div>
    </el-card>

    <!-- 数据统计卡片 -->
    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon class="stat-icon icon-question"><Document /></el-icon>
              <div class="stat-text">
                <div class="stat-label">题目总数</div>
                <div class="stat-number">1,280</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon class="stat-icon icon-paper"><Files /></el-icon>
              <div class="stat-text">
                <div class="stat-label">试卷总数</div>
                <div class="stat-number">74</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon class="stat-icon icon-user"><User /></el-icon>
              <div class="stat-text">
                <div class="stat-label">用户总数</div>
                <div class="stat-number">5,621</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon class="stat-icon icon-pending"><Bell /></el-icon>
              <div class="stat-text">
                <div class="stat-label">待办事项</div>
                <div class="stat-number">3</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 快捷操作卡片 -->
    <el-card class="actions-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>快捷操作</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="6" v-for="action in quickActions" :key="action.title">
          <div class="action-item" @click="() => router.push(action.path)">
            <el-icon class="action-icon" :style="{ color: action.color }"><component :is="action.icon" /></el-icon>
            <span class="action-title">{{ action.title }}</span>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Document, Files, User, Bell, Edit, Folder, Promotion, TrendCharts } from '@element-plus/icons-vue';

const router = useRouter();
const currentDate = ref('');

const quickActions = ref([
  { title: '题目管理', icon: Document, path: '/admin/question-manage', color: '#409EFF' },
  { title: '试卷管理', icon: Files, path: '/admin/paper-manage', color: '#67C23A' },
  { title: '发布公告', icon: Promotion, path: '/admin/notice-manage', color: '#E6A23C' },
  { title: '分类管理', icon: Folder, path: '/admin/category-manage', color: '#F56C6C' },
]);

onMounted(() => {
  const today = new Date();
  currentDate.value = today.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' });
});
</script>

<style scoped>
.welcome-page {
  padding: 20px;
}

.header-card {
  margin-bottom: 20px;
  border-left: 5px solid #409EFF;
}

.header-content {
  display: flex;
  align-items: center;
}

.welcome-logo {
  width: 60px;
  height: 60px;
  margin-right: 20px;
}

.welcome-text h1 {
  font-size: 24px;
  margin: 0 0 10px 0;
  color: #303133;
}

.welcome-text p {
  margin: 0;
  color: #606266;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  font-size: 40px;
  margin-right: 15px;
  padding: 10px;
  border-radius: 50%;
  color: white;
}

.icon-question { background-color: #409EFF; }
.icon-paper { background-color: #67C23A; }
.icon-user { background-color: #E6A23C; }
.icon-pending { background-color: #F56C6C; }

.stat-text {
  display: flex;
  flex-direction: column;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.stat-number {
  font-size: 22px;
  font-weight: bold;
  color: #303133;
}

.actions-card {
  border-radius: 8px;
}

.card-header {
  font-weight: bold;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-item:hover {
  background-color: #f5f7fa;
  transform: translateY(-5px);
}

.action-icon {
  font-size: 32px;
  margin-bottom: 15px;
}

.action-title {
  font-size: 16px;
  color: #303133;
}
</style>