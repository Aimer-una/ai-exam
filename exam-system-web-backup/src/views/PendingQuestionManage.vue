<template>
  <div class="pending-question-manage">
    <!-- 顶部操作栏：上传真题按钮 -->
    <div class="toolbar">
      <el-button type="primary" @click="uploadDialogVisible = true">上传真题</el-button>
    </div>

    <!-- 筛选和搜索栏 -->
    <el-form :inline="true" :model="filters" class="filter-form" style="margin-bottom: 20px;">
      <el-form-item label="审核状态">
        <el-select v-model="filters.status" placeholder="全部" style="width: 120px">
          <el-option label="全部" value="" />
          <el-option label="待审核" value="0" />
          <el-option label="已采纳" value="1" />
          <el-option label="未采纳" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="关键词">
        <el-input v-model="filters.keyword" placeholder="题目描述/联系方式" style="width: 200px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 上传真题弹窗 -->
    <el-dialog v-model="uploadDialogVisible" title="上传我的真题" width="500px">
      <el-form :model="uploadForm" label-width="80px">
        <el-form-item label="题目描述">
          <el-input v-model="uploadForm.content" type="textarea" rows="3" placeholder="请输入题目描述" />
        </el-form-item>
        <el-form-item label="图片上传">
          <el-upload
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :on-change="handleImageChange"
            :on-remove="handleImageRemove"
            :file-list="uploadForm.fileList"
            :limit="3"
            accept="image/*"
            :http-request="uploadImage"
          >
            <i class="el-icon-plus"></i>
          </el-upload>
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="uploadForm.contact" placeholder="选填，便于联系你" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUpload">提交</el-button>
      </template>
    </el-dialog>

    <!-- 审核列表 -->
    <el-table :data="list" style="width: 100%; margin-top: 20px;" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="content" label="题目描述" min-width="200" />
      <el-table-column label="图片" width="120">
        <template #default="scope">
          <el-image
            v-if="scope.row.image_urls && scope.row.image_urls.length > 0"
            :src="scope.row.image_urls[0]"
            :preview-src-list="scope.row.image_urls"
            style="width: 60px; height: 60px; object-fit: cover;"
          />
        </template>
      </el-table-column>
      <el-table-column prop="contact" label="联系方式" width="120" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="warning">待审核</el-tag>
          <el-tag v-else-if="scope.row.status === 1" type="success">已采纳</el-tag>
          <el-tag v-else type="info">未采纳</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="created_time" label="上传时间" width="160" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
          <el-button size="small" type="primary" v-if="scope.row.status === 0" @click="review(scope.row, 1)">采纳</el-button>
          <el-button size="small" type="danger" v-if="scope.row.status === 0" @click="review(scope.row, 2)">不采纳</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-if="total > pageSize"
      background
      layout="prev, pager, next, jumper"
      :total="total"
      :page-size="pageSize"
      :current-page="page"
      @current-change="handlePageChange"
      style="margin-top: 20px; text-align: right;"
    />

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="真题详情" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="题目描述">{{ detail.content }}</el-descriptions-item>
        <el-descriptions-item label="图片">
          <div v-if="detail.image_urls && detail.image_urls.length">
            <el-image
              v-for="(img, idx) in detail.image_urls"
              :key="idx"
              :src="img"
              style="width: 80px; height: 80px; margin-right: 8px; object-fit: cover;"
              :preview-src-list="detail.image_urls"
            />
          </div>
          <span v-else>无</span>
        </el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ detail.contact || '无' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detail.status === 0" type="warning">待审核</el-tag>
          <el-tag v-else-if="detail.status === 1" type="success">已采纳</el-tag>
          <el-tag v-else type="info">未采纳</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="管理员备注">{{ detail.admin_remark || '无' }}</el-descriptions-item>
        <el-descriptions-item label="上传时间">{{ detail.created_time }}</el-descriptions-item>
        <el-descriptions-item label="审核时间">{{ detail.reviewed_time || '无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import request from '../utils/request'
export default {
  name: 'PendingQuestionManage', // 组件名称
  data() {
    return {
      // 上传弹窗显示状态
      uploadDialogVisible: false,
      // 上传表单数据
      uploadForm: {
        content: '', // 题目描述
        fileList: [], // 图片文件列表（el-upload格式）
        imageUrls: [], // 已上传图片url
        contact: '' // 联系方式
      },
      // 筛选条件
      filters: {
        status: '',
        keyword: ''
      },
      // 审核列表相关
      list: [], // 真题列表
      loading: false, // 加载状态
      total: 0, // 总条数
      page: 1, // 当前页码
      pageSize: 10, // 每页数量
      // 详情弹窗
      detailDialogVisible: false,
      detail: {}
    }
  },
  methods: {
    // el-upload自定义图片上传，调用后端API
    async uploadImage(param) {
      const formData = new FormData();
      formData.append('file', param.file);
      try {
        const res = await request.post('/api/upload', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });
        // 上传成功后，将图片url加入imageUrls
        this.uploadForm.imageUrls.push(res.data.url);
        param.onSuccess(res, param.file);
      } catch (err) {
        this.$message.error('图片上传失败');
        param.onError(err);
      }
    },
    // 处理图片选择（同步el-upload的fileList）
    handleImageChange(file, fileList) {
      this.uploadForm.fileList = fileList;
    },
    // 删除图片时同步imageUrls
    handleImageRemove(file, fileList) {
      // 只保留fileList中有url的图片
      this.uploadForm.imageUrls = fileList.map(f => f.url).filter(Boolean);
    },
    // 提交上传
    async submitUpload() {
      if (!this.uploadForm.content) {
        this.$message.error('请填写题目描述');
        return;
      }
      // 检查图片是否全部上传完毕
      if (this.uploadForm.fileList.some(f => f.status === 'uploading')) {
        this.$message.warning('请等待图片上传完成');
        return;
      }
      try {
        await request.post('/api/pending-questions', {
          content: this.uploadForm.content,
          image_urls: this.uploadForm.imageUrls,
          contact: this.uploadForm.contact
        });
        this.$message.success('上传成功');
        this.uploadDialogVisible = false;
        // 清空表单
        this.uploadForm = { content: '', fileList: [], imageUrls: [], contact: '' };
        this.fetchList();
      } catch (e) {}
    },
    // 获取审核列表（分页+筛选）
    async fetchList(page = this.page) {
      this.loading = true;
      try {
        const params = {
          page,
          size: this.pageSize
        };
        if (this.filters.status !== '') params.status = this.filters.status;
        if (this.filters.keyword) params.keyword = this.filters.keyword;
        const res = await request.get('/api/pending-questions', { params });
        this.list = res.data.records;
        this.total = res.data.total;
        this.page = page;
      } catch (e) {} finally {
        this.loading = false;
      }
    },
    // 分页切换
    handlePageChange(page) {
      this.fetchList(page);
    },
    // 查询按钮
    handleSearch() {
      this.page = 1;
      this.fetchList(1);
    },
    // 重置按钮
    resetSearch() {
      this.filters = { status: '', keyword: '' };
      this.page = 1;
      this.fetchList(1);
    },
    // 查看详情
    async viewDetail(row) {
      try {
        const res = await request.get(`/api/pending-questions/${row.id}`);
        this.detail = res.data;
        this.detailDialogVisible = true;
      } catch (e) {}
    },
    // 审核操作
    async review(row, status) {
      try {
        const { value } = await this.$prompt('请输入审核备注', status === 1 ? '采纳' : '不采纳', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputPattern: /.{0,255}/,
          inputErrorMessage: '备注不能超过255字'
        });
        await request.post('/api/pending-questions/review', {
          id: row.id,
          status,
          admin_remark: value
        });
        this.$message.success('操作成功');
        this.fetchList();
      } catch (e) {}
    }
  },
  mounted() {
    this.fetchList();
  }
}
</script>

<style scoped>
.pending-question-manage {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.toolbar {
  margin-bottom: 20px;
}
.filter-form {
  margin-bottom: 20px;
}
</style> 