<template>
  <div class="activate-code">
    <!-- 页面标题 -->
    <div class="activate-header">
      <el-button @click="$router.go(-1)" icon="el-icon-arrow-left">返回</el-button>
      <h2>激活邀请码</h2>
    </div>

    <div class="activate-content">
      <!-- 激活说明 -->
      <el-card class="info-card">
        <template #header>
          <span>激活说明</span>
        </template>
        <div class="info-content">
          <div class="info-item">
            <el-icon><InfoFilled /></el-icon>
            <span>邀请码是访问企业真题功能的必要凭证</span>
          </div>
          <div class="info-item">
            <el-icon><InfoFilled /></el-icon>
            <span>每个邀请码只能使用一次，请妥善保管</span>
          </div>
          <div class="info-item">
            <el-icon><InfoFilled /></el-icon>
            <span>激活后即可享受完整的企业真题和模拟面试功能</span>
          </div>
          <div class="info-item">
            <el-icon><InfoFilled /></el-icon>
            <span>邀请码有效期为30天，请及时激活</span>
          </div>
        </div>
      </el-card>

      <!-- 激活表单 -->
      <el-card class="activate-card">
        <template #header>
          <span>输入邀请码</span>
        </template>
        <div class="activate-form">
          <el-form :model="activateForm" :rules="activateRules" ref="activateFormRef" label-width="0">
            <el-form-item prop="code">
              <el-input 
                v-model="activateForm.code" 
                placeholder="请输入邀请码"
                size="large"
                maxlength="20"
                show-word-limit
                clearable
                @keyup.enter="handleActivate"
              >
                <template #prefix>
                  <el-icon><Key /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button 
                type="primary" 
                size="large" 
                @click="handleActivate" 
                :loading="activating"
                style="width: 100%;"
              >
                激活邀请码
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-card>

      <!-- 邀请码类型说明 -->
      <el-card class="types-card">
        <template #header>
          <span>邀请码类型</span>
        </template>
        <div class="types-content">
          <div class="type-item">
            <div class="type-header">
              <el-tag type="info">普通邀请码</el-tag>
              <span class="type-desc">基础功能访问权限</span>
            </div>
            <ul class="type-features">
              <li>浏览企业真题</li>
              <li>参与模拟面试</li>
              <li>查看面试结果</li>
              <li>基础积分奖励</li>
            </ul>
          </div>
          <div class="type-item">
            <div class="type-header">
              <el-tag type="warning">VIP邀请码</el-tag>
              <span class="type-desc">高级功能访问权限</span>
            </div>
            <ul class="type-features">
              <li>所有普通功能</li>
              <li>优先AI评分</li>
              <li>详细能力分析</li>
              <li>专属学习建议</li>
              <li>双倍积分奖励</li>
            </ul>
          </div>
          <div class="type-item">
            <div class="type-header">
              <el-tag type="success">企业邀请码</el-tag>
              <span class="type-desc">企业级功能访问权限</span>
            </div>
            <ul class="type-features">
              <li>所有VIP功能</li>
              <li>企业真题优先</li>
              <li>面试官点评</li>
              <li>团队协作功能</li>
              <li>三倍积分奖励</li>
            </ul>
          </div>
        </div>
      </el-card>

      <!-- 获取邀请码 -->
      <el-card class="get-code-card">
        <template #header>
          <span>获取邀请码</span>
        </template>
        <div class="get-code-content">
          <div class="get-code-methods">
            <div class="method-item">
              <div class="method-icon">
                <el-icon><User /></el-icon>
              </div>
              <div class="method-info">
                <h4>好友邀请</h4>
                <p>向已有邀请码的好友申请</p>
                <el-button size="small" @click="handleRequestFromFriend">申请邀请</el-button>
              </div>
            </div>
            <div class="method-item">
              <div class="method-icon">
                <el-icon><Message /></el-icon>
              </div>
              <div class="method-info">
                <h4>官方申请</h4>
                <p>通过官方渠道申请邀请码</p>
                <el-button size="small" @click="handleRequestFromOfficial">官方申请</el-button>
              </div>
            </div>
            <div class="method-item">
              <div class="method-icon">
                <el-icon><ShoppingCart /></el-icon>
              </div>
              <div class="method-info">
                <h4>购买获取</h4>
                <p>通过付费方式获取邀请码</p>
                <el-button size="small" @click="handlePurchaseCode">立即购买</el-button>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 申请邀请对话框 -->
    <el-dialog v-model="requestDialogVisible" title="申请邀请码" width="500px">
      <el-form :model="requestForm" :rules="requestRules" ref="requestFormRef" label-width="100px">
        <el-form-item label="申请类型" prop="type">
          <el-select v-model="requestForm.type" placeholder="请选择申请类型">
            <el-option label="普通邀请码" value="normal"></el-option>
            <el-option label="VIP邀请码" value="vip"></el-option>
            <el-option label="企业邀请码" value="enterprise"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="申请理由" prop="reason">
          <el-input 
            v-model="requestForm.reason" 
            type="textarea" 
            :rows="4" 
            placeholder="请详细说明申请理由..."
          ></el-input>
        </el-form-item>
        <el-form-item label="联系方式" prop="contact">
          <el-input v-model="requestForm.contact" placeholder="请输入邮箱或手机号"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="requestDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitRequest" :loading="requesting">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { InfoFilled, Key, User, Message, ShoppingCart } from '@element-plus/icons-vue'
import { activateInterviewCode } from '@/api/interviewQuestion'

export default {
  name: 'ActivateCode',
  components: {
    InfoFilled,
    Key,
    User,
    Message,
    ShoppingCart
  },
  setup() {
    // 响应式数据
    const activating = ref(false)
    const requesting = ref(false)
    const requestDialogVisible = ref(false)
    const activateFormRef = ref()
    const requestFormRef = ref()
    
    // 激活表单
    const activateForm = reactive({
      code: ''
    })
    
    // 申请表单
    const requestForm = reactive({
      type: 'normal',
      reason: '',
      contact: ''
    })
    
    // 表单验证规则
    const activateRules = {
      code: [
        { required: true, message: '请输入邀请码', trigger: 'blur' },
        { min: 6, max: 20, message: '邀请码长度在6-20个字符', trigger: 'blur' }
      ]
    }
    
    const requestRules = {
      type: [
        { required: true, message: '请选择申请类型', trigger: 'change' }
      ],
      reason: [
        { required: true, message: '请输入申请理由', trigger: 'blur' },
        { min: 10, message: '申请理由至少10个字符', trigger: 'blur' }
      ],
      contact: [
        { required: true, message: '请输入联系方式', trigger: 'blur' }
      ]
    }
    
    // 激活邀请码
    const handleActivate = async () => {
      try {
        await activateFormRef.value.validate()
        activating.value = true
        await activateInterviewCode(activateForm.code)
        ElMessage.success('邀请码激活成功！')
        // 跳转到企业真题页面
        this.$router.push('/interview-questions')
      } catch (error) {
        ElMessage.error('激活失败，请检查邀请码是否正确')
      } finally {
        activating.value = false
      }
    }
    
    // 好友申请
    const handleRequestFromFriend = () => {
      ElMessage.info('好友申请功能开发中...')
    }
    
    // 官方申请
    const handleRequestFromOfficial = () => {
      requestDialogVisible.value = true
    }
    
    // 购买邀请码
    const handlePurchaseCode = () => {
      ElMessage.info('购买功能开发中...')
    }
    
    // 提交申请
    const handleSubmitRequest = async () => {
      try {
        await requestFormRef.value.validate()
        requesting.value = true
        // 这里应该调用申请邀请码的API
        ElMessage.success('申请提交成功，我们会尽快处理')
        requestDialogVisible.value = false
      } catch (error) {
        ElMessage.error('提交失败')
      } finally {
        requesting.value = false
      }
    }
    
    return {
      activating,
      requesting,
      requestDialogVisible,
      activateFormRef,
      requestFormRef,
      activateForm,
      requestForm,
      activateRules,
      requestRules,
      handleActivate,
      handleRequestFromFriend,
      handleRequestFromOfficial,
      handlePurchaseCode,
      handleSubmitRequest
    }
  }
}
</script>

<style scoped>
.activate-code {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.activate-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.activate-header h2 {
  margin: 0;
  color: #303133;
}

.activate-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-content {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #606266;
}

.info-item .el-icon {
  color: #409eff;
}

.activate-form {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px 0;
}

.types-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.type-item {
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fafafa;
}

.type-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.type-desc {
  color: #909399;
  font-size: 14px;
}

.type-features {
  margin: 0;
  padding-left: 20px;
  color: #606266;
}

.type-features li {
  margin-bottom: 8px;
  line-height: 1.4;
}

.get-code-methods {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.method-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fafafa;
}

.method-icon {
  width: 60px;
  height: 60px;
  background: #409eff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.method-info {
  flex: 1;
}

.method-info h4 {
  margin: 0 0 8px 0;
  color: #303133;
}

.method-info p {
  margin: 0 0 15px 0;
  color: #606266;
  font-size: 14px;
}
</style> 