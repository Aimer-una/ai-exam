<template>
  <div class="paper-manage-container">
    <div class="header">
      <div class="header-right">
        <el-input 
          v-model="searchKeyword" 
          placeholder="按试卷名称搜索" 
          class="search-input"
          clearable
          @clear="getPaperList"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
        <el-button 
          type="danger" 
          @click="handleBatchDelete" 
          :icon="Delete"
          :disabled="selectedPapers.length === 0"
        >
          批量删除 ({{ selectedPapers.length }})
        </el-button>
        <el-button type="primary" @click="goToCreatePage" :icon="Plus" class="create-btn">创建新试卷</el-button>
      </div>
    </div>
    
    <div class="paper-list">
      <el-table :data="paperList" v-loading="loading" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="试卷名称" min-width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'info'">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="试卷描述" min-width="250" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="questionCount" label="题目数量" width="100">
          <template #default="{ row }">
            {{ row.questionCount }} 道
          </template>
        </el-table-column>
        <el-table-column prop="totalScore" label="总分" width="120">
           <template #default="{ row }">
            {{ row.totalScore }} 分
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="考试时长" width="120">
          <template #default="{ row }">
            {{ row.duration }} 分钟
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'DRAFT' || row.status === '待发布'" size="small" type="success" @click="updateStatus(row, 'PUBLISHED')" :icon="CaretRight">发布</el-button>
            <el-button v-if="row.status === 'PUBLISHED'" size="small" type="warning" @click="updateStatus(row, 'DRAFT')" :icon="VideoPause">停止</el-button>
            <el-button size="small" type="primary" @click="editPaper(row)" :icon="Edit">编辑</el-button>
            <el-button size="small" type="danger" @click="deletePaper(row)" :icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, CaretRight, Edit, Delete, VideoPause, Search } from '@element-plus/icons-vue'
import { getPapers } from '../api/paper.js'
import request from '../utils/request'

const router = useRouter()
const loading = ref(false)
const paperList = ref([])
const searchKeyword = ref('')
const selectedPapers = ref([])

const getStatusText = (status) => ({ 'PUBLISHED': '已发布', 'DRAFT': '草稿', '待发布': '待发布' }[status] || '未知状态')

const getPaperList = async () => {
  loading.value = true
  try {
    const res = await getPapers({ name: searchKeyword.value })
    paperList.value = res.data
  } catch (error) {
    ElMessage.error('获取试卷列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => getPaperList()
const goToCreatePage = () => router.push('/admin/paper-create')
const editPaper = (paper) => router.push(`/admin/paper-create?id=${paper.id}`)

const updateStatus = async (paper, status) => {
  try {
    await request.post(`/api/papers/${paper.id}/status?status=${status}`)
    ElMessage.success(`试卷状态已更新为${getStatusText(status)}`)
    getPaperList()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const deletePaper = async (paper) => {
  try {
    await ElMessageBox.confirm(`确定要删除试卷"${paper.name}"吗？`, '确认删除', { confirmButtonText: '确认删除', cancelButtonText: '取消', type: 'danger' })
    await request.delete(`/api/papers/${paper.id}`)
    ElMessage.success('试卷删除成功')
    await getPaperList()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.message)
  }
}

const handleBatchDelete = async () => {
  if (selectedPapers.value.length === 0) return ElMessage.warning('请先选择要删除的试卷')
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedPapers.value.length} 份试卷吗？`, '确认批量删除', { confirmButtonText: '确认删除', cancelButtonText: '取消', type: 'danger' })
    const deletePromises = selectedPapers.value.map(p => request.delete(`/api/papers/${p.id}`))
    await Promise.all(deletePromises)
    ElMessage.success(`成功删除 ${selectedPapers.value.length} 份试卷`)
    selectedPapers.value = []
    await getPaperList()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('批量删除失败')
  }
}

const handleSelectionChange = (selected) => { selectedPapers.value = selected }

onMounted(() => getPaperList())

</script>

<style scoped>
.paper-manage-container { padding: 20px; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
.header h2 { margin: 0; color: #333; }
.paper-list { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
.search-input { width: 240px; margin-right: 10px; }
.create-btn { margin-left: 10px; }
</style>