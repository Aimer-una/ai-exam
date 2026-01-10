<template>
  <div class="video-submit-form">
    <!-- 短视频投稿说明 -->
    <div class="short-video-tip">
      <el-alert
        title="仅支持3分钟以内短视频，文件大小不超过150M。建议横屏16:9比例，内容简明有趣。"
        type="info"
        show-icon
        :closable="false"
        style="margin-bottom: 18px;"
      />
    </div>
    <el-form 
      ref="formRef" 
      :model="form" 
      :rules="rules" 
      label-width="100px"
      @submit.prevent
    >
      <!-- 视频标题 -->
      <el-form-item label="视频标题" prop="title">
        <el-input
          v-model="form.title"
          placeholder="请输入视频标题（建议20字以内）"
          maxlength="50"
          show-word-limit
        />
      </el-form-item>

      <!-- 视频描述 -->
      <el-form-item label="视频描述" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="4"
          placeholder="请简要描述视频内容，让观众更好地了解视频主题"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>

      <!-- 视频分类 -->
      <el-form-item label="视频分类" prop="categoryId">
        <el-cascader
          v-model="categoryPath"
          :options="categoryOptions"
          :props="cascaderProps"
          placeholder="请选择视频分类"
          style="width: 100%"
          @change="handleCategoryChange"
        />
      </el-form-item>

      <!-- 上传者姓名 -->
      <el-form-item label="上传者" prop="uploaderName">
        <el-input
          v-model="form.uploaderName"
          placeholder="请输入您的姓名或昵称"
          maxlength="20"
        />
      </el-form-item>

      <!-- 视频标签 -->
      <el-form-item label="视频标签" prop="tags">
        <div class="tags-input-section">
          <el-input
            v-model="currentTag"
            placeholder="输入标签后按回车添加（最多5个）"
            @keyup.enter="addTag"
            @blur="addTag"
            :disabled="tagList.length >= 5"
          />
          <div class="tags-display" v-if="tagList.length > 0">
            <el-tag
              v-for="tag in tagList"
              :key="tag"
              closable
              @close="removeTag(tag)"
              style="margin-right: 8px; margin-bottom: 8px;"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>
      </el-form-item>

      <!-- 视频文件上传 -->
      <el-form-item label="视频文件" prop="videoFile" required>
        <el-upload
          ref="videoUploadRef"
          class="video-upload"
          :auto-upload="false"
          :show-file-list="true"
          :limit="1"
          :accept="'.mp4,.avi,.mov,.wmv,.flv,.webm'"
          :on-change="handleVideoChange"
          :on-remove="handleVideoRemove"
          :before-upload="beforeVideoUpload"
        >
          <el-button type="primary" icon="Upload">选择视频文件</el-button>
          <template #tip>
            <div class="upload-tip">
              <p>支持格式：MP4、AVI、MOV、WMV、FLV、WebM</p>
              <p>文件大小：不超过150MB，时长不超过3分钟</p>
              <p>建议分辨率：1280x720或1920x1080</p>
            </div>
          </template>
        </el-upload>
      </el-form-item>

      <!-- 视频时长自动识别显示 -->
      <el-form-item label="视频时长" prop="duration">
        <el-input
          v-model="form.duration"
          :disabled="durationAuto"
          placeholder="自动识别，单位：秒"
          style="width: 200px;"
        >
          <template #append>秒</template>
        </el-input>
        <el-text v-if="durationAuto" type="info" size="small" style="margin-left:8px;">已自动识别</el-text>
        <el-text v-else type="warning" size="small" style="margin-left:8px;">如无法识别请手动填写</el-text>
      </el-form-item>

      <!-- 封面图上传 -->
      <el-form-item label="视频封面" prop="coverFile">
        <el-upload
          ref="coverUploadRef"
          class="cover-upload"
          :auto-upload="false"
          :show-file-list="false"
          :limit="1"
          :accept="'.jpg,.jpeg,.png,.gif'"
          :on-change="handleCoverChange"
          :before-upload="beforeCoverUpload"
        >
          <div class="cover-upload-area">
            <img v-if="coverPreview" :src="coverPreview" class="cover-preview" />
            <div v-else class="cover-placeholder">
              <el-icon class="upload-icon"><Plus /></el-icon>
              <div class="upload-text">点击上传封面</div>
            </div>
          </div>
          <template #tip>
            <div class="upload-tip">
              <p>支持格式：JPG、PNG、GIF</p>
              <p>建议尺寸：16:9比例，如1280x720</p>
              <p>文件大小：不超过5MB</p>
            </div>
          </template>
        </el-upload>
      </el-form-item>

      <!-- 投稿协议 -->
      <el-form-item prop="agree">
        <el-checkbox v-model="form.agree">
          我已阅读并同意
          <el-button type="text" @click="showAgreement">《视频投稿协议》</el-button>
        </el-checkbox>
      </el-form-item>
    </el-form>

    <!-- 按钮区域 -->
    <div class="form-actions">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">
        {{ submitting ? '投稿中...' : '提交投稿' }}
      </el-button>
    </div>

    <!-- 投稿协议对话框 -->
    <el-dialog v-model="agreementVisible" title="视频投稿协议" width="600px">
      <div class="agreement-content">
        <h3>视频投稿协议</h3>
        <p>欢迎您向智能学习平台投稿视频内容。在投稿前，请仔细阅读以下协议条款：</p>
        
        <h4>1. 内容要求</h4>
        <ul>
          <li>投稿内容必须为原创或已获得合法授权的视频</li>
          <li>内容应具有教育价值，符合技术学习主题</li>
          <li>不得包含违法、暴力、色情、侵权等不当内容</li>
          <li>视频画质清晰，音频清楚，具有良好的观看体验</li>
        </ul>
        
        <h4>2. 版权声明</h4>
        <ul>
          <li>投稿者保证拥有视频的完整版权或已获得相应授权</li>
          <li>平台有权对投稿内容进行审核和编辑</li>
          <li>投稿内容一经采用，平台获得展示和推广权</li>
        </ul>
        
        <h4>3. 审核机制</h4>
        <ul>
          <li>所有投稿内容需经过平台审核后方可发布</li>
          <li>审核时间通常为1-3个工作日</li>
          <li>平台有权拒绝不符合要求的投稿内容</li>
        </ul>
        
        <h4>4. 其他条款</h4>
        <ul>
          <li>投稿者应承担因内容引起的一切法律责任</li>
          <li>平台有权随时修改本协议条款</li>
          <li>本协议的最终解释权归平台所有</li>
        </ul>
      </div>
      <template #footer>
        <el-button @click="agreementVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Upload } from '@element-plus/icons-vue'
import { submitVideo } from '../api/video'
import { getVideoCategoryTree } from '../api/videoCategory'

// 定义事件
const emit = defineEmits(['success', 'cancel'])

// 响应式数据
const formRef = ref()
const videoUploadRef = ref()
const coverUploadRef = ref()
const submitting = ref(false)
const agreementVisible = ref(false)
const categoryOptions = ref([])
const categoryPath = ref([])
const currentTag = ref('')
const tagList = ref([])
const coverPreview = ref('')
const durationAuto = ref(false) // 是否自动识别时长

// 表单数据
const form = reactive({
  title: '',
  description: '',
  categoryId: null,
  uploaderName: '',
  tags: '',
  videoFile: null,
  coverFile: null,
  duration: null, // 视频时长（秒）
  agree: false
})

// 级联选择器配置
const cascaderProps = {
  value: 'id',
  label: 'name',
  children: 'children',
  emitPath: false,
  checkStrictly: true
}

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入视频标题', trigger: 'blur' },
    { min: 5, max: 50, message: '标题长度在5到50个字符之间', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入视频描述', trigger: 'blur' },
    { min: 10, max: 500, message: '描述长度在10到500个字符之间', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择视频分类', trigger: 'change' }
  ],
  uploaderName: [
    { required: true, message: '请输入上传者姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在2到20个字符之间', trigger: 'blur' }
  ],
  duration: [
    { required: true, message: '请填写视频时长', trigger: 'blur' },
    { type: 'number', min: 1, max: 180, message: '视频时长需在1-180秒内', trigger: 'blur' }
  ],
  agree: [
    { 
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('请阅读并同意投稿协议'))
        } else {
          callback()
        }
      }, 
      trigger: 'change' 
    }
  ]
}

// 生命周期
onMounted(() => {
  loadCategories()
})

// 加载分类数据
const loadCategories = async () => {
  try {
    const res = await getVideoCategoryTree()
    categoryOptions.value = res.data || []
  } catch (error) {
    console.error('加载分类失败:', error)
    ElMessage.error('加载分类失败')
  }
}

// 处理分类变化
const handleCategoryChange = (value) => {
  form.categoryId = value
}

// 添加标签
const addTag = () => {
  const tag = currentTag.value.trim()
  if (tag && !tagList.value.includes(tag) && tagList.value.length < 5) {
    tagList.value.push(tag)
    form.tags = tagList.value.join(',')
    currentTag.value = ''
  }
}

// 移除标签
const removeTag = (tag) => {
  const index = tagList.value.indexOf(tag)
  if (index > -1) {
    tagList.value.splice(index, 1)
    form.tags = tagList.value.join(',')
  }
}

// 处理视频文件变化
const handleVideoChange = (file, fileList) => {
  form.videoFile = file.raw // 赋值视频文件
  // 自动识别视频时长
  getVideoDuration(file.raw)
}

// 读取视频时长
function getVideoDuration(file) {
  // 创建URL对象
  const url = URL.createObjectURL(file)
  const video = document.createElement('video')
  video.preload = 'metadata'
  video.src = url
  video.onloadedmetadata = () => {
    // 读取时长（秒，向上取整）
    const duration = Math.ceil(video.duration)
    if (duration && duration <= 180) {
      form.duration = duration
      durationAuto.value = true
    } else {
      // 超过3分钟或无法识别
      form.duration = null
      durationAuto.value = false
      ElMessage.warning('视频时长超过3分钟或无法识别，请选择合规视频或手动填写时长！')
    }
    URL.revokeObjectURL(url)
  }
  video.onerror = () => {
    form.duration = null
    durationAuto.value = false
    ElMessage.warning('无法识别视频时长，请手动填写！')
    URL.revokeObjectURL(url)
  }
}

// 处理视频文件移除
const handleVideoRemove = () => {
  form.videoFile = null
}

// 视频文件上传前的检查
const beforeVideoUpload = (file) => {
  // 判断视频类型
  const isVideo = ['video/mp4', 'video/avi', 'video/quicktime', 'video/x-ms-wmv', 'video/x-flv', 'video/webm'].includes(file.type)
  // 判断大小是否小于150M
  const isLt150M = file.size / 1024 / 1024 < 150
  if (!isVideo) {
    ElMessage.error('只能上传视频文件！')
    return false
  }
  if (!isLt150M) {
    ElMessage.error('视频文件大小不能超过150MB！')
    return false
  }
  return false // 阻止自动上传
}

// 处理封面文件变化
const handleCoverChange = (file, fileList) => {
  form.coverFile = file.raw
  
  // 生成预览
  const reader = new FileReader()
  reader.onload = (e) => {
    coverPreview.value = e.target.result
  }
  reader.readAsDataURL(file.raw)
}

// 封面文件上传前的检查
const beforeCoverUpload = (file) => {
  const isImage = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'].includes(file.type)
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片文件大小不能超过5MB！')
    return false
  }
  return false // 阻止自动上传
}

// 显示协议
const showAgreement = () => {
  agreementVisible.value = true
}

// 取消操作
const handleCancel = () => {
  emit('cancel')
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (!form.videoFile) {
      ElMessage.error('请选择视频文件')
      return
    }
    if (!form.duration) {
      ElMessage.error('请填写或识别视频时长')
      return
    }
    submitting.value = true
    const formData = new FormData()
    formData.append('title', form.title)
    formData.append('description', form.description)
    formData.append('categoryId', form.categoryId)
    formData.append('uploaderName', form.uploaderName)
    formData.append('tags', form.tags)
    formData.append('videoFile', form.videoFile)
    formData.append('duration', form.duration) // 加入时长
    if (form.coverFile) {
      formData.append('coverFile', form.coverFile)
    }
    const res = await submitVideo(formData)
    ElMessage.success('视频投稿成功，请等待审核')
    emit('success', res.data)
  } catch (error) {
    console.error('投稿失败:', error)
    ElMessage.error(error.message || '投稿失败，请重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.video-submit-form {
  padding: 20px 0;
}

.tags-input-section {
  width: 100%;
}

.tags-display {
  margin-top: 12px;
}

.video-upload {
  width: 100%;
}

.cover-upload {
  width: 100%;
}

.cover-upload-area {
  width: 200px;
  height: 120px;
  border: 2px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.cover-upload-area:hover {
  border-color: #409eff;
}

.cover-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
}

.upload-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 14px;
}

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #666;
  line-height: 1.4;
}

.upload-tip p {
  margin: 2px 0;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.agreement-content {
  max-height: 400px;
  overflow-y: auto;
  padding: 0 10px;
  line-height: 1.6;
}

.agreement-content h3 {
  color: #303133;
  margin-bottom: 16px;
}

.agreement-content h4 {
  color: #606266;
  margin: 16px 0 8px;
}

.agreement-content ul {
  margin: 8px 0 16px 20px;
}

.agreement-content li {
  margin-bottom: 4px;
  color: #666;
}

.short-video-tip {
  margin-bottom: 10px;
}
</style> 