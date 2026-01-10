<template>
  <div class="score-manage-container">
    <div class="search-bar">
      <el-input v-model="searchForm.studentName" placeholder="考生姓名" clearable style="width: 200px" @input="handleSearch" />
      <el-input v-model="searchForm.studentNumber" placeholder="学号/工号" clearable style="width: 200px" @input="handleSearch" />
      <el-select v-model="searchForm.status" placeholder="考试状态" clearable style="width: 150px" @change="handleSearch">
        <el-option label="进行中" :value="0" />
        <el-option label="已完成" :value="1" />
        <el-option label="已批阅" :value="2" />
      </el-select>
      <el-date-picker
        v-model="searchForm.dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        format="YYYY-MM-DD"
        value-format="YYYY-MM-DD"
        style="width: 300px"
        @change="handleSearch"
      />
      <el-button type="primary" @click="handleSearch" icon="Search">搜索</el-button>
      <el-button @click="resetSearch" icon="Refresh">重置</el-button>
    </div>

    <div class="table-container">
      <el-table :data="examRecords" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="id" label="记录ID" width="80" />
        <el-table-column prop="studentName" label="考生姓名" width="120" />
        <el-table-column prop="studentNumber" label="学号/工号" width="120" />
        <el-table-column prop="paper.name" label="试卷名称" min-width="200" />
        <el-table-column prop="score" label="得分" width="100">
          <template #default="scope">
            <span v-if="scope.row.status === '已批阅'">{{ scope.row.score }}</span>
            <span v-else class="text-muted">待批阅</span>
          </template>
        </el-table-column>
        <el-table-column prop="paper.totalScore" label="总分" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #default="scope">{{ formatDateTime(scope.row.startTime) }}</template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="180">
          <template #default="scope">{{ scope.row.endTime ? formatDateTime(scope.row.endTime) : '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewDetail(scope.row)" icon="View">查看详情</el-button>
            <el-button v-if="scope.row.status === '已完成'" size="small" type="primary" @click="gradeExam(scope.row)" :loading="scope.row.grading" icon="Edit">批阅</el-button>
            <el-button size="small" type="danger" @click="deleteRecord(scope.row)" icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, View, Edit, Delete } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const examRecords = ref([])
const loading = ref(false)
const pagination = reactive({ current: 1, size: 20, total: 0 })
const searchForm = reactive({ studentName: '', studentNumber: '', status: '', dateRange: [] })

const getExamRecords = async () => {
  loading.value = true
  try {
    const params = { 
      page: pagination.current, size: pagination.size, studentName: searchForm.studentName, 
      studentNumber: searchForm.studentNumber, status: searchForm.status, 
      startDate: searchForm.dateRange?.[0], endDate: searchForm.dateRange?.[1] 
    }
    const res = await request.get('/api/exam-records/list', { params })
    examRecords.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取考试记录失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pagination.current = 1; getExamRecords(); }
const resetSearch = () => { Object.assign(searchForm, { studentName: '', studentNumber: '', status: '', dateRange: [] }); handleSearch(); }
const handleSizeChange = (size) => { pagination.size = size; pagination.current = 1; getExamRecords(); }
const handleCurrentChange = (current) => { pagination.current = current; getExamRecords(); }

const getStatusText = (status) => ({ '进行中': '进行中', '已完成': '已完成', '已批阅': '已批阅' }[status] || status)
const getStatusType = (status) => ({ '进行中': 'warning', '已完成': 'info', '已批阅': 'success' }[status] || 'info')
const formatDateTime = (dateTime) => dateTime ? new Date(dateTime).toLocaleString('zh-CN') : '-'

const viewDetail = (record) => router.push(`/exam-result/${record.id}`)

const gradeExam = async (record) => {
  try {
    record.grading = true
    await request.post(`/api/exams/${record.id}/grade`)
    ElMessage.success('批阅完成')
    getExamRecords()
  } catch (error) {
    ElMessage.error('批阅失败')
  } finally {
    record.grading = false
  }
}

const deleteRecord = async (record) => {
  try {
    await ElMessageBox.confirm(`确定要删除考生 ${record.studentName} 的考试记录吗？`, '确认删除', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await request.delete(`/api/exam-records/${record.id}`)
    ElMessage.success('删除成功')
    getExamRecords()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => getExamRecords())

</script>

<style scoped>
.score-manage-container { padding: 20px; }
.search-bar { background: white; padding: 20px; border-radius: 8px; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); display: flex; gap: 16px; align-items: center; flex-wrap: wrap; }
.table-container { background: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); overflow: hidden; }
.pagination-container { padding: 20px; display: flex; justify-content: center; }
.text-muted { color: #999; }
</style>