<template>
  <div class="question-list">
    <!-- 题目列表 -->
    <el-table
      v-loading="loading"
      :data="questions"
      style="width: 100%"
    >
      <!-- 序号列 -->
      <el-table-column
        type="index"
        label="序号"
        width="60"
      />

      <!-- 题目内容列 -->
      <el-table-column prop="title" label="题目">
        <template #default="{ row }">
          <div class="question-content">
            <!-- 题目标题 -->
            <div class="title">{{ row.title }}</div>
            
            <!-- 答案和解析（只在点击查看答案时显示） -->
            <div v-if="row.showAnswer" class="answer-section">
              <!-- 选择题答案 -->
              <template v-if="row.type === 'CHOICE'">
                <div class="answer-block">
                  <!-- 1. 选项列表 -->
                  <div class="answer-line">
                    <span class="label">题目选项：</span>
                    <div class="options-list">
                      <div 
                        v-for="(choice, index) in row.choices" 
                        :key="index" 
                        class="option-item"
                      >
                        {{ String.fromCharCode(65 + index) }}. {{ choice.content }}
                      </div>
                    </div>
                  </div>
                  <!-- 2. 正确答案 -->
                  <div class="answer-line">
                    <span class="label">正确答案：</span>
                    <span class="content">
                      {{ row.choices?.filter(c => c.isCorrect).map((c, i) => String.fromCharCode(65 + row.choices.indexOf(c))).join('、') || '暂无答案' }}
                    </span>
                  </div>
                  <!-- 3. 答案解析 -->
                  <div v-if="row.analysis" class="answer-line">
                    <span class="label">答案解析：</span>
                    <span class="content">{{ row.analysis }}</span>
                  </div>
                </div>
              </template>

              <!-- 判断题答案 -->
              <template v-if="row.type === 'JUDGE'">
                <div class="answer-block">
                  <div class="answer-line">
                    <span class="label">正确答案：</span>
                    <span class="content">{{ row.answer?.answer === 'TRUE' ? '正确' : '错误' }}</span>
                  </div>
                  <div v-if="row.analysis" class="answer-line">
                    <span class="label">答案解析：</span>
                    <span class="content">{{ row.analysis }}</span>
                  </div>
                </div>
              </template>

              <!-- 简答题答案 -->
              <template v-if="row.type === 'TEXT'">
                <div class="answer-block">
                  <div class="answer-line">
                    <span class="label">参考答案：</span>
                    <span class="content">{{ row.answer?.answer || '暂无标准答案' }}</span>
                  </div>
                  <div v-if="row.analysis" class="answer-line">
                    <span class="label">答案解析：</span>
                    <span class="content">{{ row.analysis }}</span>
                  </div>
                </div>
              </template>
            </div>
          </div>
        </template>
      </el-table-column>

      <!-- 题型列 -->
      <el-table-column prop="type" label="题型" width="100">
        <template #default="{ row }">
          <el-tag :type="getQuestionTypeTag(row.type)">
            {{ getQuestionTypeText(row.type) }}
          </el-tag>
        </template>
      </el-table-column>

      <!-- 难度列 -->
      <el-table-column prop="difficulty" label="难度" width="100">
        <template #default="{ row }">
          <el-tag :type="getDifficultyTag(row.difficulty)">
            {{ getDifficultyText(row.difficulty) }}
          </el-tag>
        </template>
      </el-table-column>

      <!-- 操作列 -->
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button 
            type="primary" 
            link
            @click="toggleAnswer(row)"
          >
            {{ row.showAnswer ? '隐藏答案' : '查看答案' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch } from 'vue'
import request from '../utils/request' // 修正为相对路径
import { ElMessage } from 'element-plus'

// 定义props
const props = defineProps({
  questions: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  pagination: {
    type: Object,
    default: () => ({
      current: 1,
      size: 10,
      total: 0
    })
  }
})

// 定义事件
const emit = defineEmits(['page'])

// 切换答案显示（懒加载，点击时请求后端）
const toggleAnswer = async (row) => {
  if (!row.showAnswer) {
    // 懒加载：只在"查看答案"时请求后端
    const res = await request.get(`/api/questions/${row.id}`)
    if (res && res.data) {
      Object.assign(row, res.data)
      row.showAnswer = true
    }
  } else {
    row.showAnswer = false
  }
}

// 监听题目数据变化
watch(() => props.questions, (newQuestions) => {
  // 确保每个题目都有 showAnswer 属性
  newQuestions.forEach(question => {
    if (typeof question.showAnswer === 'undefined') {
      question.showAnswer = false
    }
  })
}, { immediate: true })

// 获取题型标签类型
const getQuestionTypeTag = (type) => {
  const map = {
    'CHOICE': 'primary',
    'JUDGE': 'success',
    'TEXT': 'warning'
  }
  return map[type] || 'info'
}

// 获取题型显示文本
const getQuestionTypeText = (type) => {
  const map = {
    'CHOICE': '选择题',
    'JUDGE': '判断题',
    'TEXT': '简答题'
  }
  return map[type] || type
}

// 获取难度标签类型
const getDifficultyTag = (difficulty) => {
  const map = {
    'EASY': 'success',
    'MEDIUM': 'warning',
    'HARD': 'danger'
  }
  return map[difficulty] || 'info'
}

// 获取难度显示文本
const getDifficultyText = (difficulty) => {
  const map = {
    'EASY': '简单',
    'MEDIUM': '中等',
    'HARD': '困难'
  }
  return map[difficulty] || difficulty
}

// 分页大小改变
const handleSizeChange = (size) => {
  emit('page', { size, current: 1 })
}

// 当前页改变
const handleCurrentChange = (current) => {
  emit('page', { size: props.pagination.size, current })
}

// 查看题目详情
const viewQuestionDetail = async (row) => {
  try {
    const res = await request.get(`/api/questions/${row.id}`)
    viewQuestion.value = res.data
    viewDialogVisible.value = true
  } catch (error) {
    console.error('获取题目详情失败：', error)
    ElMessage.error('获取题目详情失败')
  }
}
</script>

<style scoped>
.question-list {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.question-content {
  margin: 10px 0;
}

.title {
  font-weight: bold;
  margin-bottom: 15px;
  font-size: 14px;
  line-height: 1.6;
  color: #333;
}

.answer-section {
  margin-top: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 4px solid #409eff;
}

.section-title {
  font-size: 14px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
  padding-left: 8px;
  border-left: 3px solid #409eff;
}

.options-section {
  background: #fff;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 15px;
  border: 1px solid #e4e7ed;
}

.options-list {
  padding: 0 15px;
}

.option-item {
  padding: 8px 0;
  display: flex;
  align-items: flex-start;
  border-bottom: 1px dashed #e4e7ed;
}

.option-item:last-child {
  border-bottom: none;
}

.option-label {
  font-weight: bold;
  color: #409eff;
  margin-right: 15px;
  min-width: 25px;
}

.option-content {
  color: #333;
  line-height: 1.5;
}

.correct-answer-section {
  background: #f0f9eb;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 15px;
  border: 1px solid #67c23a;
}

.correct-answer-content {
  color: #67c23a;
  font-size: 16px;
  font-weight: bold;
  padding: 0 15px;
}

.analysis-section {
  background: #f4f4f5;
  border-radius: 4px;
  padding: 15px;
  border: 1px solid #e4e7ed;
}

.analysis-content {
  color: #666;
  line-height: 1.6;
  padding: 0 15px;
}

.text-answer {
  color: #333;
  line-height: 1.6;
  padding: 10px 15px;
  background: #fff;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.answer-block {
  padding: 10px 15px;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.answer-line {
  padding: 8px 0;
  border-bottom: 1px dashed #e4e7ed;
}

.answer-line:last-child {
  border-bottom: none;
}

.label {
  font-weight: bold;
  color: #409eff;
  margin-right: 10px;
}

.content {
  color: #333;
}

.options-list {
  margin: 5px 0 5px 20px;
}

.option-item {
  margin: 5px 0;
  color: #333;
  line-height: 1.5;
}
</style> 