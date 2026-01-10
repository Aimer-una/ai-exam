<template>
  <div class="app-container">
    <!-- 左侧菜单 -->
    <div class="sidebar">
      <h3 class="sidebar-title">管理菜单</h3>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        @select="handleMenuSelect"
        router
      >
        <!-- 试题管理分组 -->
        <el-submenu index="question">
          <template #title>试题管理</template>
          <el-menu-item index="/admin/question-manage">
            <el-icon><Document /></el-icon>
            <span>题目管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/category-manage">
            <el-icon><Folder /></el-icon>
            <span>类别管理</span>
          </el-menu-item>
        </el-submenu>
        <!-- 考试管理分组 -->
        <el-submenu index="exam">
          <template #title>考试管理</template>
          <el-menu-item index="/admin/paper-manage">
            <el-icon><Files /></el-icon>
            <span>试卷管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/score-manage">
            <el-icon><DataAnalysis /></el-icon>
            <span>成绩管理</span>
          </el-menu-item>
        </el-submenu>
        <!-- 系统管理分组 -->
        <el-submenu index="system">
          <template #title>系统管理</template>
          <el-menu-item index="/admin/banner-manage">
            <el-icon><Picture /></el-icon>
            <span>轮播图管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/notice-manage">
            <el-icon><Bell /></el-icon>
            <span>公告管理</span>
          </el-menu-item>
        </el-submenu>
        <!-- 视频管理分组 -->
        <el-submenu index="video">
          <template #title>视频管理</template>
          <el-menu-item index="/admin/video-manage">
            <el-icon><VideoPlay /></el-icon>
            <span>视频管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/video-category-manage">
            <el-icon><Collection /></el-icon>
            <span>视频分类</span>
          </el-menu-item>
        </el-submenu>
      </el-menu>
    </div>

    <!-- 右侧内容区 -->
    <div class="main-content">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  Document, Folder, Files, DataAnalysis, 
  Picture, Bell, VideoPlay, Collection 
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => {
  return route.path
})

const handleMenuSelect = (index) => {
  router.push(index)
}
</script>

<style scoped>
.app-container {
  display: flex;
  height: 100vh;
  background-color: #f5f5f5;
}

.sidebar {
  width: 250px;
  background: white;
  box-shadow: 2px 0 8px rgba(0,0,0,0.1);
}

.sidebar-title {
  padding: 20px;
  margin: 0;
  border-bottom: 1px solid #eee;
  color: #333;
}

.sidebar-menu {
  border: none;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
</style>
