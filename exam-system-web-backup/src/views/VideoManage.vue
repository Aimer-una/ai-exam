<template>
  <div class="video-manage-container">
    <div class="search-bar">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input v-model="searchForm.keyword" placeholder="搜索视频标题或描述" clearable @input="handleSearch">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable @change="handleSearch">
            <el-option label="待审核" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已拒绝" :value="2" />
            <el-option label="已下架" :value="3" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchForm.uploaderType" placeholder="上传者类型" clearable @change="handleSearch">
            <el-option label="用户投稿" :value="1" />
            <el-option label="管理员上传" :value="2" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchForm.categoryId" placeholder="选择分类" clearable @change="handleSearch">
            <el-option v-for="category in categories" :key="category.id" :label="category.name" :value="category.id" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="handleSearch" icon="Search">搜索</el-button>
          <el-button @click="resetSearch" icon="Refresh">重置</el-button>
          <el-button type="success" @click="showUploadDialog" icon="Plus">上传视频</el-button>
        </el-col>
      </el-row>
    </div>

    <div class="video-list">
      <el-table :data="videoList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="封面" width="120">
          <template #default="scope">
            <el-image :src="scope.row.coverUrl" :preview-src-list="[scope.row.coverUrl]" fit="cover" style="width: 80px; height: 45px; border-radius: 4px;" :preview-teleported="true">
              <template #error><div class="image-slot"><el-icon><Picture /></el-icon></div></template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="uploaderName" label="上传者" width="120" />
        <el-table-column label="上传类型" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.uploaderType === 2 ? 'success' : 'info'">{{ scope.row.uploaderType === 2 ? '管理员' : '用户投稿' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="观看次数" width="100" />
        <el-table-column prop="likeCount" label="点赞数" width="80" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="previewVideo(scope.row)" icon="VideoPlay">预览</el-button>
            <el-button size="small" @click="editVideo(scope.row)" icon="Edit">编辑</el-button>
            <el-button v-if="scope.row.status === 0" size="small" type="success" @click="auditVideoAction(scope.row, 1)" icon="Check">通过</el-button>
            <el-button v-if="scope.row.status === 0" size="small" type="warning" @click="showRejectDialog(scope.row)" icon="Close">拒绝</el-button>
            <el-button v-if="scope.row.status === 1" size="small" type="warning" @click="offlineVideoAction(scope.row)" icon="Bottom">下架</el-button>
            <el-button size="small" type="danger" @click="deleteVideoAction(scope.row)" icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
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

    <el-dialog v-model="uploadDialogVisible" title="上传视频" width="800px" :close-on-click-modal="false">
      <el-form :model="uploadForm" :rules="uploadRules" ref="uploadFormRef" label-width="100px">
        <el-form-item label="视频标题" prop="title"><el-input v-model="uploadForm.title" placeholder="请输入视频标题" /></el-form-item>
        <el-form-item label="视频描述" prop="description"><el-input v-model="uploadForm.description" type="textarea" :rows="3" placeholder="请输入视频描述" /></el-form-item>
        <el-form-item label="视频分类" prop="categoryId">
          <el-cascader v-model="categoryPath" :options="categoryTree" :props="cascaderProps" placeholder="请先选择一级分类，再选择二级分类" style="width: 100%" @change="handleCategoryChange" clearable />
        </el-form-item>
        <el-form-item label="视频标签" prop="tags">
          <div class="tags-input-section">
            <el-input v-model="currentTag" placeholder="输入标签后回车添加（最多5个）" @keyup.enter="addTag" @blur="addTag" :disabled="tagList.length >= 5" />
            <div class="tags-display" v-if="tagList.length > 0">
              <el-tag v-for="tag in tagList" :key="tag" closable @close="removeTag(tag)" style="margin-right: 8px; margin-bottom: 8px;">{{ tag }}</el-tag>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="上传者名称" prop="uploaderName"><el-input v-model="uploadForm.uploaderName" placeholder="请输入上传者名称" /></el-form-item>
        <el-form-item label="视频时长" prop="duration">
          <div class="duration-input-wrapper">
            <el-input-number v-model="uploadForm.duration" :min="1" placeholder="例如：300" style="width: 200px;" />
            <span class="duration-unit">秒</span>
            <div class="duration-tips"><el-text type="info" size="small">请输入视频时长（秒），例如：5分钟 = 300秒，1小时 = 3600秒</el-text></div>
          </div>
        </el-form-item>
        <el-form-item label="视频文件" prop="videoFile">
          <el-upload ref="videoUploadRef" :auto-upload="false" :limit="1" :on-change="handleVideoFileChange" accept="video/*" drag>
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">将视频文件拖到此处，或<em>点击上传</em></div>
            <template #tip><div class="el-upload__tip">支持MP4、AVI、MOV等格式，文件大小不超过500MB</div></template>
          </el-upload>
        </el-form-item>
        <el-form-item label="封面图片">
          <el-upload ref="coverUploadRef" :auto-upload="false" :limit="1" :on-change="handleCoverFileChange" accept="image/*" list-type="picture-card">
            <el-icon><Plus /></el-icon>
            <template #tip><div class="el-upload__tip">支持JPG、PNG格式，建议尺寸16:9，文件大小不超过2MB</div></template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpload" :loading="uploading">确定上传</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="previewDialogVisible" :title="currentPreviewVideo.title" width="900px">
      <div class="video-preview" v-if="currentPreviewVideo.fileUrl">
        <video :src="currentPreviewVideo.fileUrl" controls style="width: 100%; max-height: 500px;" />
        <div class="video-info">
          <p><strong>描述：</strong>{{ currentPreviewVideo.description }}</p>
          <p><strong>分类：</strong>{{ currentPreviewVideo.categoryName }}</p>
          <p><strong>标签：</strong>{{ currentPreviewVideo.tags }}</p>
          <p><strong>上传者：</strong>{{ currentPreviewVideo.uploaderName }}</p>
          <p><strong>观看次数：</strong>{{ currentPreviewVideo.viewCount }}</p>
          <p><strong>点赞数：</strong>{{ currentPreviewVideo.likeCount }}</p>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="rejectDialogVisible" title="拒绝审核" width="500px">
      <el-form :model="rejectForm" :rules="rejectRules" ref="rejectFormRef" label-width="100px">
        <el-form-item label="拒绝原因" prop="reason"><el-input v-model="rejectForm.reason" type="textarea" :rows="4" placeholder="请输入拒绝原因" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="handleReject" :loading="rejecting">确定拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Edit, Delete, Plus, VideoPlay, Check, Close, Bottom, UploadFilled, Picture } from '@element-plus/icons-vue'
import { getVideosForAdmin, uploadVideoByAdmin, auditVideo, offlineVideo, deleteVideo } from '../api/video'
import { getVideoCategories, getVideoCategoryTree } from '../api/videoCategory'

const searchForm = reactive({ keyword: '', status: '', uploaderType: '', categoryId: '' })
const videoList = ref([])
const loading = ref(false)
const pagination = reactive({ current: 1, size: 10, total: 0 })
const categories = ref([])
const categoryTree = ref([])
const categoryPath = ref([])
const uploadDialogVisible = ref(false)
const uploading = ref(false)
const uploadFormRef = ref()
const videoUploadRef = ref()
const coverUploadRef = ref()
const uploadForm = reactive({ title: '', description: '', categoryId: '', tags: '', uploaderName: '', duration: null, videoFile: null, coverFile: null })
const uploadRules = {
  title: [{ required: true, message: '请输入视频标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  uploaderName: [{ required: true, message: '请输入上传者名称', trigger: 'blur' }],
  videoFile: [{ required: true, message: '请上传视频文件', trigger: 'change' }]
}
const previewDialogVisible = ref(false)
const currentPreviewVideo = ref({})
const rejectDialogVisible = ref(false)
const rejecting = ref(false)
const rejectFormRef = ref()
const currentRejectVideo = ref({})
const rejectForm = reactive({ reason: '' })
const rejectRules = { reason: [{ required: true, message: '请输入拒绝原因', trigger: 'blur' }] }

const getStatusType = (status) => ({ 0: 'warning', 1: 'success', 2: 'danger', 3: 'info' }[status] || 'info')
const getStatusText = (status) => ({ 0: '待审核', 1: '已发布', 2: '已拒绝', 3: '已下架' }[status] || '未知')

const getVideoList = async () => {
  loading.value = true
  try {
    const res = await getVideosForAdmin({ page: pagination.current, size: pagination.size, ...searchForm })
    videoList.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取视频列表失败：', error)
  } finally {
    loading.value = false
  }
}

const getCategoryList = async () => {
  try {
    const res = await getVideoCategories()
    categories.value = res.data
    const treeRes = await getVideoCategoryTree()
    categoryTree.value = treeRes.data || []
  } catch (error) {
    console.error('获取分类列表失败：', error)
  }
}

const handleSearch = () => { pagination.current = 1; getVideoList(); }
const resetSearch = () => { Object.assign(searchForm, { keyword: '', status: '', uploaderType: '', categoryId: '' }); handleSearch(); }
const handleSizeChange = (size) => { pagination.size = size; getVideoList(); }
const handleCurrentChange = (current) => { pagination.current = current; getVideoList(); }
const showUploadDialog = () => { uploadDialogVisible.value = true; }

const handleVideoFileChange = (file) => {
  uploadForm.videoFile = file.raw
  const url = URL.createObjectURL(file.raw)
  const video = document.createElement('video')
  video.preload = 'metadata'
  video.src = url
  video.onloadedmetadata = () => {
    const duration = Math.ceil(video.duration)
    if (duration) uploadForm.duration = duration
    URL.revokeObjectURL(url)
  }
  video.onerror = () => { uploadForm.duration = null; URL.revokeObjectURL(url); }
}

const handleCoverFileChange = (file) => { uploadForm.coverFile = file.raw; }

const handleUpload = async () => {
  if (!uploadFormRef.value) return
  await uploadFormRef.value.validate(async (valid) => {
    if (valid) {
      uploading.value = true
      try {
        const formData = new FormData()
        Object.keys(uploadForm).forEach(key => formData.append(key, uploadForm[key]))
        await uploadVideoByAdmin(formData)
        ElMessage.success('视频上传成功')
        uploadDialogVisible.value = false
        resetUploadForm()
        getVideoList()
      } catch (error) {
        console.error('视频上传失败：', error)
      } finally {
        uploading.value = false
      }
    }
  })
}

const resetUploadForm = () => {
  Object.assign(uploadForm, { title: '', description: '', categoryId: '', tags: '', uploaderName: '', duration: null, videoFile: null, coverFile: null })
  categoryPath.value = []
  tagList.value = []
  currentTag.value = ''
  if (videoUploadRef.value) videoUploadRef.value.clearFiles()
  if (coverUploadRef.value) coverUploadRef.value.clearFiles()
}

const previewVideo = (video) => { currentPreviewVideo.value = video; previewDialogVisible.value = true; }
const editVideo = (video) => { ElMessage.info('编辑功能开发中...'); }

const auditVideoAction = async (video, status) => {
  try {
    await auditVideo(video.id, status)
    ElMessage.success(status === 1 ? '审核通过' : '审核拒绝')
    getVideoList()
  } catch (error) {
    console.error('审核失败：', error)
  }
}

const showRejectDialog = (video) => { currentRejectVideo.value = video; rejectDialogVisible.value = true; }

const handleReject = async () => {
  if (!rejectFormRef.value) return
  await rejectFormRef.value.validate(async (valid) => {
    if (valid) {
      rejecting.value = true
      try {
        await auditVideo(currentRejectVideo.value.id, 2, rejectForm.reason)
        ElMessage.success('已拒绝该视频')
        rejectDialogVisible.value = false
        rejectForm.reason = ''
        getVideoList()
      } catch (error) {
        console.error('拒绝失败：', error)
      } finally {
        rejecting.value = false
      }
    }
  })
}

const offlineVideoAction = async (video) => {
  try {
    await ElMessageBox.confirm('确定要下架此视频吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await offlineVideo(video.id)
    ElMessage.success('视频已下架')
    getVideoList()
  } catch (error) {
    if (error !== 'cancel') console.error('下架失败：', error)
  }
}

const deleteVideoAction = async (video) => {
  try {
    await ElMessageBox.confirm('确定要删除此视频吗？删除后无法恢复！', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await deleteVideo(video.id)
    ElMessage.success('视频已删除')
    getVideoList()
  } catch (error) {
    if (error !== 'cancel') console.error('删除失败：', error)
  }
}

const cascaderProps = { value: 'id', label: 'name', children: 'children', emitPath: false, checkStrictly: true }
const handleCategoryChange = (value) => { uploadForm.categoryId = value; }

const currentTag = ref('')
const tagList = ref([])

const addTag = () => {
  const tag = currentTag.value.trim()
  if (tag && !tagList.value.includes(tag) && tagList.value.length < 5) {
    tagList.value.push(tag)
    uploadForm.tags = tagList.value.join(',')
    currentTag.value = ''
  }
}

const removeTag = (tag) => {
  const index = tagList.value.indexOf(tag)
  if (index > -1) {
    tagList.value.splice(index, 1)
    uploadForm.tags = tagList.value.join(',')
  }
}

onMounted(() => { getCategoryList(); getVideoList(); })

</script>

<style scoped>
.video-manage-container { padding: 20px; }
.search-bar { margin-bottom: 20px; padding: 20px; background: #f8f9fa; border-radius: 8px; }
.video-list { background: white; border-radius: 8px; padding: 20px; }
.pagination { margin-top: 20px; text-align: right; }
.image-slot { display: flex; justify-content: center; align-items: center; width: 100%; height: 100%; background: #f5f7fa; color: #909399; }
.video-preview { text-align: center; }
.video-info { margin-top: 20px; text-align: left; }
.video-info p { margin: 8px 0; color: #606266; }
.duration-input-wrapper { display: flex; align-items: center; }
.duration-unit { margin-left: 10px; margin-right: 10px; }
.duration-tips { margin-top: 5px; }
.tags-input-section { display: flex; align-items: center; }
.tags-display { margin-left: 10px; }
</style>