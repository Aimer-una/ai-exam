<template>
  <div class="notice-manage-container">
    <div class="action-bar">
      <el-button type="primary" @click="showAddDialog" icon="Plus">添加公告</el-button>
      <el-button @click="getNoticeList" icon="Refresh">刷新列表</el-button>
    </div>

    <div class="notice-list">
      <el-table :data="noticeList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip>
          <template #default="scope">
            <div class="content-preview">{{ scope.row.content.substring(0, 50) }}{{ scope.row.content.length > 50 ? '...' : '' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="100">
          <template #default="scope">
            <el-tag :type="getTypeTagColor(scope.row.type)">{{ getTypeText(scope.row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="优先级" width="100">
          <template #default="scope">
            <el-tag :type="getPriorityTagColor(scope.row.priority)">{{ getPriorityText(scope.row.priority) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.isActive ? 'success' : 'danger'">{{ scope.row.isActive ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewNoticeDetail(scope.row)" icon="View">查看</el-button>
            <el-button size="small" @click="editNotice(scope.row)" icon="Edit">编辑</el-button>
            <el-button size="small" :type="scope.row.isActive ? 'warning' : 'success'" @click="toggleStatus(scope.row)" icon="Switch">{{ scope.row.isActive ? '禁用' : '启用' }}</el-button>
            <el-button size="small" type="danger" @click="deleteNotice(scope.row)" icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" :close-on-click-modal="false">
      <el-form :model="noticeForm" :rules="noticeRules" ref="noticeFormRef" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="noticeForm.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="noticeForm.content" type="textarea" :rows="6" placeholder="请输入公告内容" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="noticeForm.type" placeholder="请选择公告类型">
            <el-option label="系统公告" value="SYSTEM" />
            <el-option label="新功能" value="FEATURE" />
            <el-option label="通知" value="NOTICE" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="noticeForm.priority" placeholder="请选择优先级">
            <el-option label="普通" :value="0" />
            <el-option label="重要" :value="1" />
            <el-option label="紧急" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="noticeForm.isActive" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitNotice" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="viewDialogVisible" :title="viewNotice.title" width="600px">
      <div class="notice-detail" v-if="viewNotice">
        <div class="notice-meta">
          <el-tag :type="getTypeTagColor(viewNotice.type)" style="margin-right: 10px;">{{ getTypeText(viewNotice.type) }}</el-tag>
          <el-tag :type="getPriorityTagColor(viewNotice.priority)" style="margin-right: 10px;">{{ getPriorityText(viewNotice.priority) }}</el-tag>
          <el-tag :type="viewNotice.isActive ? 'success' : 'danger'">{{ viewNotice.isActive ? '启用' : '禁用' }}</el-tag>
        </div>
        <div class="notice-content">{{ viewNotice.content }}</div>
        <div class="notice-time">创建时间：{{ viewNotice.createTime }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, View, Edit, Switch, Delete } from '@element-plus/icons-vue'
import request from '../utils/request'

const loading = ref(false)
const noticeList = ref([])
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)
const noticeFormRef = ref()
const viewNotice = ref({})

const noticeForm = reactive({ id: null, title: '', content: '', type: 'NOTICE', priority: 0, isActive: true })

const noticeRules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }],
  type: [{ required: true, message: '请选择公告类型', trigger: 'change' }]
}

const getNoticeList = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/notices/list')
    noticeList.value = res.data || []
  } catch (error) {
    ElMessage.error('获取公告列表失败')
  } finally {
    loading.value = false
  }
}

const showAddDialog = () => { resetForm(); dialogTitle.value = '添加公告'; dialogVisible.value = true; }
const viewNoticeDetail = (notice) => { viewNotice.value = notice; viewDialogVisible.value = true; }
const editNotice = (notice) => { Object.assign(noticeForm, notice); dialogTitle.value = '编辑公告'; dialogVisible.value = true; }

const resetForm = () => {
  Object.assign(noticeForm, { id: null, title: '', content: '', type: 'NOTICE', priority: 0, isActive: true })
  if (noticeFormRef.value) noticeFormRef.value.resetFields()
}

const submitNotice = async () => {
  if (!noticeFormRef.value) return
  try {
    await noticeFormRef.value.validate()
    submitLoading.value = true
    const isEdit = noticeForm.id !== null
    const url = isEdit ? '/api/notices/update' : '/api/notices/add'
    const method = isEdit ? 'put' : 'post'
    await request[method](url, noticeForm)
    ElMessage.success(isEdit ? '公告更新成功' : '公告添加成功')
    dialogVisible.value = false
    await getNoticeList()
  } catch (error) {
    console.error('提交失败：', error)
  } finally {
    submitLoading.value = false
  }
}

const toggleStatus = async (notice) => {
  try {
    const newStatus = !notice.isActive
    const statusText = newStatus ? '启用' : '禁用'
    await ElMessageBox.confirm(`确认${statusText}公告"${notice.title}"？`, '确认操作', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' })
    await request.put(`/api/notices/toggle/${notice.id}?isActive=${newStatus}`)
    ElMessage.success(`公告${statusText}成功`)
    await getNoticeList()
  } catch (error) {
    if (error !== 'cancel') console.error('状态切换失败：', error)
  }
}

const deleteNotice = async (notice) => {
  try {
    await ElMessageBox.confirm(`确认删除公告"${notice.title}"？此操作不可恢复！`, '确认删除', { confirmButtonText: '确认删除', cancelButtonText: '取消', type: 'danger' })
    await request.delete(`/api/notices/delete/${notice.id}`)
    ElMessage.success('公告删除成功')
    await getNoticeList()
  } catch (error) {
    if (error !== 'cancel') console.error('删除失败：', error)
  }
}

const getTypeTagColor = (type) => ({ 'SYSTEM': 'danger', 'FEATURE': 'success', 'NOTICE': 'warning' }[type] || 'info')
const getTypeText = (type) => ({ 'SYSTEM': '系统', 'FEATURE': '新功能', 'NOTICE': '通知' }[type] || '其他')
const getPriorityTagColor = (priority) => ({ 0: '', 1: 'warning', 2: 'danger' }[priority] || '')
const getPriorityText = (priority) => ({ 0: '普通', 1: '重要', 2: '紧急' }[priority] || '普通')

onMounted(() => getNoticeList())

</script>

<style scoped>
.notice-manage-container { padding: 20px; }
.action-bar { margin-bottom: 20px; }
.notice-list { background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.content-preview { color: #666; line-height: 1.4; }
.notice-detail { padding: 20px 0; }
.notice-meta { margin-bottom: 20px; padding-bottom: 15px; border-bottom: 1px solid #eee; }
.notice-content { margin-bottom: 20px; line-height: 1.6; color: #333; white-space: pre-wrap; }
.notice-time { color: #999; font-size: 12px; text-align: right; }
</style>