<template>
  <div class="app-container">
    <!-- 右侧内容区 -->
    <div class="main-content">
      <!-- 题目管理视图 -->
      <template v-if="activeView === 'questions'">
        <!-- 搜索和筛选 -->
        <div class="search-bar">
          <!-- 第一行：搜索和筛选条件 -->
          <el-row :gutter="20" class="search-row">
            <el-col :span="6">
              <el-input
                v-model="searchForm.keyword"
                placeholder="搜索题目内容"
                clearable
                @input="handleSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </el-col>
            <el-col :span="5">
              <el-select v-model="searchForm.type" placeholder="选择类型" clearable @change="handleSearch">
                <el-option label="选择题" value="CHOICE" />
                <el-option label="判断题" value="JUDGE" />
                <el-option label="简答题" value="TEXT" />
              </el-select>
            </el-col>
            <el-col :span="5">
              <el-select v-model="searchForm.difficulty" placeholder="选择难度" clearable @change="handleSearch">
                <el-option label="简单" value="EASY" />
                <el-option label="中等" value="MEDIUM" />
                <el-option label="困难" value="HARD" />
              </el-select>
            </el-col>
            <el-col :span="8">
              <el-button type="primary" @click="handleSearch" icon="Search">搜索</el-button>
              <el-button @click="resetSearch" icon="Refresh">重置</el-button>
            </el-col>
          </el-row>
          
          <!-- 第二行：操作按钮 -->
          <el-row :gutter="20" class="action-row">
            <el-col :span="24">
              <div class="action-buttons">
                <el-button type="primary" @click="showAddDialog" icon="Plus">添加题目</el-button>
                
                <!-- 批量删除按钮 -->
                <el-button 
                  type="danger" 
                  @click="handleBatchDelete" 
                  icon="Delete"
                  :disabled="selectedQuestions.length === 0"
                >
                  批量删除 ({{ selectedQuestions.length }})
                </el-button>
                
                <!-- 批量导入下拉按钮 -->
                <el-dropdown split-button type="success" @click="showBatchImportDialog">
                  <el-icon><Upload /></el-icon>
                  批量导入
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item @click="downloadTemplate">
                        <el-icon><Download /></el-icon>
                        下载模板
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
                
                <!-- AI生成按钮 -->
                <el-button type="warning" @click="showAiGenerateDialog" icon="MagicStick">
                  AI生成
                </el-button>
              </div>
            </el-col>
          </el-row>
        </div>

        <div class="content-wrapper">
          <!-- 左侧题目列表 -->
          <div class="question-section">
            <div class="question-list">
              <el-table :data="questionList" v-loading="loading" stripe @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55" />
                <el-table-column prop="title" label="题目内容" min-width="300" show-overflow-tooltip />
                <el-table-column prop="type" label="类型" width="100">
                  <template #default="{ row }">
                    <el-tag :type="getQuestionTypeTag(row.type)">
                      {{ getQuestionTypeText(row.type) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="difficulty" label="难度" width="100">
                  <template #default="{ row }">
                    <el-tag :type="getDifficultyType(row.difficulty)">
                      {{ getDifficultyText(row.difficulty) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="180" fixed="right">
                  <template #default="{ row }">
                    <el-button size="small" @click="showEditDialog(row)" icon="Edit">编辑</el-button>
                    <el-button size="small" type="danger" @click="handleDelete(row)" icon="Delete">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              
              <!-- 分页 -->
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
          </div>

          <!-- 右侧分类列表 -->
          <div class="category-section">
            <div class="category-header">
              <h3>题目分类</h3>
              <el-button 
                size="small" 
                @click="resetCategoryFilter" 
                :disabled="!searchForm.categoryId"
                style="margin-top: 10px;"
              >
                重置分类筛选
              </el-button>
            </div>
            <div class="category-list">
              <el-tree
                :data="categoryTree"
                :props="{ label: 'name', children: 'children' }"
                @node-click="handleNodeClick"
                node-key="id"
                :current-node-key="searchForm.categoryId"
                highlight-current
              >
                <template #default="{ node, data }">
                  <span class="custom-tree-node">
                    <span>{{ node.label }}</span>
                    <span v-if="data.count !== undefined" class="category-count">({{ data.count }})</span>
                  </span>
                </template>
              </el-tree>
            </div>
          </div>
        </div>
      </template>

      <!-- 类别管理视图 -->
      <template v-if="activeView === 'categories'">
        <CategoryManage />
      </template>
      <!-- 试卷管理视图 -->
      <template v-if="activeView === 'papers'">
        <PaperManage />
      </template>
      <!-- 成绩管理视图 -->
      <template v-if="activeView === 'scores'">
        <ScoreManage />
      </template>
      <!-- 轮播图管理视图 -->
      <template v-if="activeView === 'banners'">
        <BannerManage />
      </template>
      <!-- 公告管理视图 -->
      <template v-if="activeView === 'notices'">
        <NoticeManage />
      </template>
      <!-- 视频管理视图 -->
      <template v-if="activeView === 'videos'">
        <VideoManage />
      </template>
      <!-- 视频分类管理视图 -->
      <template v-if="activeView === 'video-categories'">
        <VideoCategoryManage />
      </template>
      <!-- 企业管理视图 -->
      <!--
      <template v-if="activeView === 'company-manage'">
        <CompanyManage />
      </template>
      -->
      <!-- 企业真题类别管理视图 -->
      <!--
      <template v-if="activeView === 'company-question-category-manage'">
        <CompanyQuestionCategoryManage />
      </template>
      -->
      <!-- 企业真题管理视图 -->
      <!--
      <template v-if="activeView === 'interview-question-manage'">
        <InterviewQuestionManage />
      </template>
      -->
    </div>

    <!-- 添加/编辑题目对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
    >
      <el-form :model="questionForm" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="题目内容" prop="title">
          <el-input
            v-model="questionForm.title"
            type="textarea"
            :rows="3"
            placeholder="请输入题目内容"
          />
        </el-form-item>
        
        <el-form-item label="题目类型" prop="type">
          <el-select v-model="questionForm.type" placeholder="请选择题目类型" @change="onTypeChange">
            <el-option label="选择题" value="CHOICE"></el-option>
            <el-option label="判断题" value="JUDGE"></el-option>
            <el-option label="简答题" value="TEXT"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item v-if="questionForm.type === 'CHOICE'" label="是否为多选题" prop="multi">
          <el-switch v-model="questionForm.multi"></el-switch>
        </el-form-item>

        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="questionForm.categoryId" placeholder="选择分类">
            <el-option 
              v-for="category in filteredCategories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="难度等级" prop="difficulty">
          <el-select v-model="questionForm.difficulty" placeholder="选择难度">
            <el-option label="简单" value="EASY" />
            <el-option label="中等" value="MEDIUM" />
            <el-option label="困难" value="HARD" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="分值" prop="score">
          <el-input-number v-model="questionForm.score" :min="1" :max="100" />
        </el-form-item>
        
        <el-form-item label="题目解析" prop="analysis">
          <el-input
            v-model="questionForm.analysis"
            type="textarea"
            :rows="3"
            placeholder="请输入题目解析"
          />
        </el-form-item>

        <!-- 选择题选项 -->
        <template v-if="questionForm.type === 'CHOICE'">
          <el-form-item
            v-for="(choice, index) in questionForm.choices"
            :key="index"
            :label="'选项' + String.fromCharCode(65 + index)"
            :prop="'choices.' + index + '.content'"
            :rules="{ required: true, message: '请输入选项内容', trigger: 'blur' }"
          >
            <div class="choice-item">
              <el-input v-model="choice.content" placeholder="请输入选项内容" />
              <el-checkbox v-model="choice.isCorrect">正确答案</el-checkbox>
              <el-button 
                type="danger" 
                icon="Delete" 
                circle 
                @click="removeChoice(index)"
                v-if="questionForm.choices.length > 2"
              />
            </div>
          </el-form-item>
          <el-form-item>
            <el-button 
              type="primary" 
              icon="Plus" 
              @click="addChoice" 
              v-if="questionForm.choices.length < 6"
            >
              添加选项
            </el-button>
          </el-form-item>
        </template>

        <!-- 判断题答案 -->
        <template v-if="questionForm.type === 'JUDGE'">
          <el-form-item label="正确答案" prop="answer.answer">
            <el-radio-group v-model="questionForm.answer.answer">
              <el-radio label="TRUE">正确</el-radio>
              <el-radio label="FALSE">错误</el-radio>
            </el-radio-group>
          </el-form-item>
        </template>

        <!-- 简答题答案 -->
        <template v-if="questionForm.type === 'TEXT'">
          <el-form-item label="参考答案" prop="answer.answer">
            <el-input
              v-model="questionForm.answer.answer"
              type="textarea"
              :rows="3"
              placeholder="请输入参考答案"
            />
          </el-form-item>
        </template>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新增：批量导入对话框 -->
    <el-dialog
      v-model="batchImportDialogVisible"
      title="批量导入题目"
      width="900px"
      @close="resetBatchImport"
    >
      <div class="batch-import-container">
        <!-- 步骤指示器 -->
        <el-steps :active="batchImportStep" finish-status="success" align-center>
          <el-step title="选择文件" description="上传Excel文件"></el-step>
          <el-step title="预览数据" description="检查导入数据"></el-step>
          <el-step title="导入完成" description="确认导入结果"></el-step>
        </el-steps>

        <div class="step-content">
          <!-- 第一步：文件上传 -->
          <div v-if="batchImportStep === 0" class="upload-step">
            <el-alert
              title="使用说明"
              type="info"
              :closable="false"
              show-icon
              style="margin-bottom: 20px;"
            >
              <template #default>
                <ol>
                  <li>请先下载Excel模板，按照格式填写题目数据</li>
                  <li>支持选择题（单选/多选）、判断题、简答题</li>
                  <li>Excel文件大小不超过10MB，题目数量建议不超过1000道</li>
                  <li>请确保必填字段完整，数据格式正确</li>
                </ol>
              </template>
            </el-alert>

            <div class="upload-actions">
              <el-button type="primary" @click="downloadTemplate" icon="Download">
                下载Excel模板
              </el-button>
            </div>

            <el-upload
              ref="uploadRef"
              class="upload-demo"
              drag
              :auto-upload="false"
              :limit="1"
              :on-change="handleFileChange"
              :before-upload="beforeUpload"
              accept=".xlsx,.xls"
            >
              <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
              <div class="el-upload__text">
                将Excel文件拖到此处，或<em>点击上传</em>
              </div>
              <template #tip>
                <div class="el-upload__tip">
                  只能上传xlsx/xls文件，且不超过10MB
                </div>
              </template>
            </el-upload>

            <div class="step-actions" v-if="selectedFile">
              <el-button @click="resetBatchImport">取消</el-button>
              <el-button type="primary" @click="previewExcelData" :loading="previewLoading">
                下一步：预览数据
              </el-button>
            </div>
          </div>

          <!-- 第二步：数据预览 -->
          <div v-if="batchImportStep === 1" class="preview-step">
            <div class="preview-header">
              <el-alert
                :title="`解析成功！共找到 ${previewData.length} 道题目`"
                type="success"
                :closable="false"
                show-icon
                style="margin-bottom: 20px;"
              />
            </div>

            <div class="preview-table">
              <el-table :data="previewData" max-height="400" border>
                <el-table-column prop="title" label="题目内容" min-width="200" show-overflow-tooltip />
                <el-table-column prop="type" label="类型" width="80">
                  <template #default="{ row }">
                    <el-tag size="small" :type="getQuestionTypeTag(row.type)">
                      {{ getQuestionTypeText(row.type) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="difficulty" label="难度" width="80">
                  <template #default="{ row }">
                    <el-tag size="small" :type="getDifficultyType(row.difficulty)">
                      {{ getDifficultyText(row.difficulty) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="score" label="分值" width="60" />
                <el-table-column label="选项/答案" min-width="150">
                  <template #default="{ row }">
                    <div v-if="row.type === 'CHOICE' && row.choices">
                      <div v-for="(choice, index) in row.choices" :key="index" class="choice-preview">
                        <span :class="{ 'correct-choice': choice.isCorrect }">
                          {{ String.fromCharCode(65 + index) }}. {{ choice.content }}
                        </span>
                      </div>
                    </div>
                    <div v-else>
                      {{ row.answer }}
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </div>

            <div class="step-actions">
              <el-button @click="batchImportStep = 0">上一步</el-button>
              <el-button type="primary" @click="confirmImport" :loading="importLoading">
                确认导入
              </el-button>
            </div>
          </div>

          <!-- 第三步：导入结果 -->
          <div v-if="batchImportStep === 2" class="result-step">
            <el-result
              :icon="importResult.success ? 'success' : 'warning'"
              :title="importResult.title"
              :sub-title="importResult.message"
            >
              <template #extra>
                <el-button type="primary" @click="resetBatchImport">继续导入</el-button>
                <el-button @click="batchImportDialogVisible = false">关闭</el-button>
              </template>
            </el-result>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 新增：AI生成题目对话框 -->
    <el-dialog
      v-model="aiGenerateDialogVisible"
      title="AI智能生成题目"
      width="900px"
      @close="resetAiGenerate"
    >
      <div class="ai-generate-container">
        <!-- 步骤指示器 -->
        <el-steps :active="aiGenerateStep" finish-status="success" align-center>
          <el-step title="设置参数" description="配置生成要求"></el-step>
          <el-step title="AI生成中" description="请稍候..."></el-step>
          <el-step title="预览确认" description="检查生成结果"></el-step>
          <el-step title="导入完成" description="确认导入结果"></el-step>
        </el-steps>

        <div class="step-content">
          <!-- 第一步：参数设置 -->
          <div v-if="aiGenerateStep === 0" class="params-step">
            <el-form :model="aiGenerateForm" :rules="aiGenerateRules" ref="aiFormRef" label-width="120px">
              <el-form-item label="题目主题" prop="topic">
                <el-input
                  v-model="aiGenerateForm.topic"
                  placeholder="例如：Spring框架、Java并发编程、MySQL数据库等"
                  clearable
                />
                <div class="form-tip">描述你想要生成题目的技术领域或知识点</div>
              </el-form-item>

              <el-form-item label="生成数量" prop="count">
                <el-input-number
                  v-model="aiGenerateForm.count"
                  :min="1"
                  :max="20"
                  placeholder="1-20道题目"
                />
                <div class="form-tip">建议一次生成5-10道题目，质量更高</div>
              </el-form-item>

              <el-form-item label="题目类型" prop="types">
                <el-checkbox-group v-model="aiGenerateForm.types">
                  <el-checkbox label="CHOICE">选择题</el-checkbox>
                  <el-checkbox label="JUDGE">判断题</el-checkbox>
                  <el-checkbox label="TEXT">简答题</el-checkbox>
                </el-checkbox-group>
              </el-form-item>

              <el-form-item label="难度等级" prop="difficulty">
                <el-radio-group v-model="aiGenerateForm.difficulty">
                  <el-radio label="EASY">简单</el-radio>
                  <el-radio label="MEDIUM">中等</el-radio>
                  <el-radio label="HARD">困难</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="题目分类" prop="categoryId">
                <el-select v-model="aiGenerateForm.categoryId" placeholder="选择分类">
                  <el-option 
                    v-for="category in getAllCategories()"
                    :key="category.id"
                    :label="category.name"
                    :value="category.id"
                  />
                </el-select>
              </el-form-item>

              <el-form-item label="包含多选题">
                <el-switch v-model="aiGenerateForm.includeMultiple" />
                <div class="form-tip">选择题中是否包含多选题</div>
              </el-form-item>

              <el-form-item label="额外要求">
                <el-input
                  v-model="aiGenerateForm.requirements"
                  type="textarea"
                  :rows="3"
                  placeholder="例如：重点考察实际应用场景、包含代码示例、侧重面试常考点等"
                />
                <div class="form-tip">可选，用于指导AI生成更符合需求的题目</div>
              </el-form-item>
            </el-form>

            <div class="step-actions">
              <el-button @click="aiGenerateDialogVisible = false">取消</el-button>
              <el-button type="primary" @click="startAiGenerate" :loading="aiGenerating">
                开始生成
              </el-button>
            </div>
          </div>

          <!-- 第二步：AI生成中 -->
          <div v-if="aiGenerateStep === 1" class="generating-step">
            <div class="generating-content">
              <el-icon class="loading-icon" :size="60"><Loading /></el-icon>
              <h3>AI正在为您生成题目...</h3>
              <p>主题：{{ aiGenerateForm.topic }}</p>
              <p>数量：{{ aiGenerateForm.count }} 道</p>
              <p>类型：{{ getSelectedTypesText() }}</p>
              <el-progress :percentage="generateProgress" :status="generateProgress === 100 ? 'success' : ''" />
            </div>
          </div>

          <!-- 第三步：预览生成结果 -->
          <div v-if="aiGenerateStep === 2" class="ai-preview-step">
            <div class="preview-header">
              <el-alert
                :title="`AI生成完成！共生成 ${aiGeneratedData.length} 道题目`"
                type="success"
                :closable="false"
                show-icon
                style="margin-bottom: 20px;"
              />
              <div class="preview-tips">
                <el-text type="info">
                  <el-icon><InfoFilled /></el-icon>
                  您可以点击题目进行编辑修改，确认无误后批量导入
                </el-text>
              </div>
            </div>

            <div class="ai-preview-table">
              <el-table :data="aiGeneratedData" max-height="500" border>
                <el-table-column prop="title" label="题目内容" min-width="250" show-overflow-tooltip>
                  <template #default="{ row, $index }">
                    <div class="question-title" @click="editAiQuestion($index)">
                      {{ row.title }}
                      <el-icon class="edit-icon"><Edit /></el-icon>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column prop="type" label="类型" width="90">
                  <template #default="{ row }">
                    <el-tag size="small" :type="getQuestionTypeTag(row.type)">
                      {{ getQuestionTypeText(row.type) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="difficulty" label="难度" width="80">
                  <template #default="{ row }">
                    <el-tag size="small" :type="getDifficultyType(row.difficulty)">
                      {{ getDifficultyText(row.difficulty) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="选项/答案" min-width="200">
                  <template #default="{ row }">
                    <div v-if="row.type === 'CHOICE' && row.choices">
                      <div v-for="(choice, index) in row.choices" :key="index" class="choice-preview">
                        <span :class="{ 'correct-choice': choice.isCorrect }">
                          {{ String.fromCharCode(65 + index) }}. {{ choice.content }}
                        </span>
                      </div>
                    </div>
                    <div v-else class="answer-preview">
                      {{ row.answer }}
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="解析" min-width="200" show-overflow-tooltip>
                  <template #default="{ row }">
                    <el-text size="small">{{ row.analysis }}</el-text>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="100" fixed="right">
                  <template #default="{ row, $index }">
                    <el-button size="small" type="primary" @click="editAiQuestion($index)" icon="Edit">编辑</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>

            <div class="step-actions">
              <el-button @click="resetAiGenerate">重新生成</el-button>
              <el-button type="primary" @click="confirmAiImport" :loading="aiImportLoading">
                确认导入 ({{ aiGeneratedData.length }}道)
              </el-button>
            </div>
          </div>

          <!-- 第四步：导入结果 -->
          <div v-if="aiGenerateStep === 3" class="ai-result-step">
            <el-result
              :icon="aiImportResult.success ? 'success' : 'warning'"
              :title="aiImportResult.title"
              :sub-title="aiImportResult.message"
            >
              <template #extra>
                <el-button type="primary" @click="resetAiGenerate">继续生成</el-button>
                <el-button @click="aiGenerateDialogVisible = false">关闭</el-button>
              </template>
            </el-result>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 新增：编辑AI生成题目的对话框 -->
    <el-dialog
      v-model="editAiQuestionDialogVisible"
      title="编辑AI生成的题目"
      width="700px"
    >
      <el-form :model="editingAiQuestion" label-width="100px" v-if="editingAiQuestion">
        <el-form-item label="题目内容">
          <el-input
            v-model="editingAiQuestion.title"
            type="textarea"
            :rows="3"
          />
        </el-form-item>

        <el-form-item label="题目类型">
          <el-tag :type="getQuestionTypeTag(editingAiQuestion.type)">
            {{ getQuestionTypeText(editingAiQuestion.type) }}
          </el-tag>
        </el-form-item>

        <!-- 选择题选项编辑 -->
        <template v-if="editingAiQuestion.type === 'CHOICE'">
          <el-form-item
            v-for="(choice, index) in editingAiQuestion.choices"
            :key="index"
            :label="'选项' + String.fromCharCode(65 + index)"
          >
            <div class="choice-edit-item">
              <el-input v-model="choice.content" />
              <el-checkbox v-model="choice.isCorrect">正确</el-checkbox>
            </div>
          </el-form-item>
        </template>

        <!-- 判断题/简答题答案编辑 -->
        <template v-else>
          <el-form-item label="答案">
            <el-input
              v-model="editingAiQuestion.answer"
              :type="editingAiQuestion.type === 'TEXT' ? 'textarea' : 'text'"
              :rows="editingAiQuestion.type === 'TEXT' ? 3 : 1"
            />
          </el-form-item>
        </template>

        <el-form-item label="题目解析">
          <el-input
            v-model="editingAiQuestion.analysis"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editAiQuestionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAiQuestionEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Document, Search, Refresh, Edit, Delete, Plus, Folder, Files, DataAnalysis,
  Upload, Download, MagicStick, UploadFilled, Loading, InfoFilled, Picture, Bell, VideoPlay, Collection, OfficeBuilding, FolderOpened, DocumentChecked
} from '@element-plus/icons-vue'
import request from '../utils/request'
import CategoryManage from './CategoryManage.vue'
import PaperManage from './PaperManage.vue'
import ScoreManage from './ScoreManage.vue'
import BannerManage from './BannerManage.vue'
import NoticeManage from './NoticeManage.vue'
import VideoManage from './VideoManage.vue'
import VideoCategoryManage from './VideoCategoryManage.vue'
import PendingQuestionManage from './PendingQuestionManage.vue'
import CompanyManage from './CompanyManage.vue'
import CompanyQuestionCategoryManage from './CompanyQuestionCategoryManage.vue'
import InterviewQuestionManage from './InterviewQuestionManage.vue'

// 视图控制
const activeView = ref('questions')

const handleMenuSelect = (index) => {
  activeView.value = index
}

const router = useRouter()

// 用户信息
const userInfo = ref({})

// 题目列表相关
const questionList = ref([])
const loading = ref(false)
const pagination = reactive({ current: 1, size: 10, total: 0 })

// 分类数据
const categories = ref([])
const categoryTree = ref([])

// 搜索表单
const searchForm = reactive({
  keyword: '',
  categoryId: '',
  difficulty: '',
  type: ''
})

// 对话框相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref()

const dialogTitle = computed(() => isEdit.value ? '编辑题目' : '添加题目')

// 新增：批量导入相关
const batchImportDialogVisible = ref(false)
const batchImportStep = ref(0)
const selectedFile = ref(null)
const uploadRef = ref()
const previewLoading = ref(false)
const importLoading = ref(false)
const previewData = ref([])
const importResult = reactive({
  success: false,
  title: '',
  message: ''
})

// 新增：AI生成相关
const aiGenerateDialogVisible = ref(false)
const aiGenerateStep = ref(0)
const aiGenerating = ref(false)
const generateProgress = ref(0)
const aiGeneratedData = ref([])
const aiImportLoading = ref(false)
const aiImportResult = reactive({
  success: false,
  title: '',
  message: ''
})

// AI生成表单
const aiGenerateForm = reactive({
  topic: '',
  count: 5,
  types: ['CHOICE'],
  difficulty: 'MEDIUM',
  categoryId: '',
  includeMultiple: false,
  requirements: ''
})

// AI生成表单验证规则
const aiGenerateRules = {
  topic: [{ required: true, message: '请输入题目主题', trigger: 'blur' }],
  count: [{ required: true, message: '请设置生成数量', trigger: 'blur' }],
  types: [{ required: true, type: 'array', min: 1, message: '请选择至少一种题目类型', trigger: 'change' }],
  difficulty: [{ required: true, message: '请选择难度等级', trigger: 'change' }],
  categoryId: [{ required: true, message: '请选择题目分类', trigger: 'change' }]
}

const aiFormRef = ref()

// 新增：编辑AI生成题目相关
const editAiQuestionDialogVisible = ref(false)
const editingAiQuestion = ref(null)
const editingAiQuestionIndex = ref(-1)

// 根据题目类型筛选分类
const filteredCategories = computed(() => {
  if (!questionForm.type || !categoryTree.value.length) {
    return []
  }
  let parentCategoryName = ''
  switch (questionForm.type) {
    case 'CHOICE':
      parentCategoryName = '选择题'
      break
    case 'JUDGE':
      parentCategoryName = '判断题'
      break
    case 'TEXT':
      parentCategoryName = '简答题'
      break
    default:
      return []
  }
  const parentCategory = categoryTree.value.find(cat => cat.name === parentCategoryName)
  return parentCategory ? parentCategory.children : []
})

// 表单校验规则
const rules = {
  title: [{ required: true, message: '请输入题目内容', trigger: 'blur' }],
  type: [{ required: true, message: '请选择题目类型', trigger: 'change' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  difficulty: [{ required: true, message: '请选择难度等级', trigger: 'change' }],
  score: [{ required: true, message: '请输入分值', trigger: 'blur' }]
}

// 初始化表单数据
const initQuestionForm = () => {
  return {
    id: null,
    title: '',
    type: 'CHOICE',
    categoryId: '',
    difficulty: 'MEDIUM',
    score: 5,
    analysis: '',
    choices: [
      { content: '', isCorrect: false },
      { content: '', isCorrect: false }
    ],
    answer: {
      answer: ''
    },
    multi: false // 默认不是多选题
  }
}

const questionForm = reactive(initQuestionForm())

// 获取题目列表
const fetchQuestions = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/questions/list', {
      params: {
        ...searchForm,
        page: pagination.current,
        size: pagination.size
      }
    })
    questionList.value = res.data.records
    pagination.total = res.data.total
    // 清空选择状态
    selectedQuestions.value = []
  } catch (error) {
    ElMessage.error('获取题目列表失败')
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const res = await request.get('/api/categories/tree')
    categoryTree.value = res.data
    categories.value = res.data
  } catch (error) {
    ElMessage.error('获取分类列表失败')
  }
}

// 新增：批量导入相关方法
const showBatchImportDialog = () => {
  batchImportDialogVisible.value = true
  batchImportStep.value = 0
}

const resetBatchImport = () => {
  batchImportStep.value = 0
  selectedFile.value = null
  previewData.value = []
  importResult.success = false
  importResult.title = ''
  importResult.message = ''
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

// 下载Excel模板
const downloadTemplate = async () => {
  try {
    const response = await fetch('/api/questions/batch/template')
    const blob = await response.blob()
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '题目导入模板.xlsx'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('模板下载成功')
  } catch (error) {
    ElMessage.error('模板下载失败')
  }
}

// 文件选择处理
const handleFileChange = (file) => {
  selectedFile.value = file
}

// 文件上传前验证
const beforeUpload = (file) => {
  const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || 
                  file.type === 'application/vnd.ms-excel'
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isExcel) {
    ElMessage.error('只能上传Excel文件！')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过10MB！')
    return false
  }
  return false // 阻止自动上传
}

// 预览Excel数据
const previewExcelData = async () => {
  if (!selectedFile.value) {
    ElMessage.error('请先选择文件')
    return
  }

  previewLoading.value = true
  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value.raw)

    const res = await request.post('/api/questions/batch/preview-excel', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    previewData.value = res.data
    batchImportStep.value = 1
    ElMessage.success(`解析成功，共找到 ${res.data.length} 道题目`)
  } catch (error) {
    ElMessage.error(error.message || '文件解析失败')
  } finally {
    previewLoading.value = false
  }
}

// 确认导入
const confirmImport = async () => {
  importLoading.value = true
  try {
    const res = await request.post('/api/questions/batch/import-questions', previewData.value)
    
    importResult.success = true
    importResult.title = '导入成功！'
    importResult.message = res.data
    batchImportStep.value = 2

    // 刷新题目列表
    await fetchQuestions()
    await fetchCategories() // 重新获取分类数据以更新数量
  } catch (error) {
    importResult.success = false
    importResult.title = '导入失败'
    importResult.message = error.message || '导入过程中发生错误'
    batchImportStep.value = 2
  } finally {
    importLoading.value = false
  }
}

// 新增：AI生成相关方法
const showAiGenerateDialog = () => {
  aiGenerateDialogVisible.value = true
  aiGenerateStep.value = 0
  resetAiGenerateForm()
}

const resetAiGenerate = () => {
  aiGenerateStep.value = 0
  aiGenerating.value = false
  generateProgress.value = 0
  aiGeneratedData.value = []
  aiImportResult.success = false
  aiImportResult.title = ''
  aiImportResult.message = ''
  resetAiGenerateForm()
}

const resetAiGenerateForm = () => {
  Object.assign(aiGenerateForm, {
    topic: '',
    count: 5,
    types: ['CHOICE'],
    difficulty: 'MEDIUM',
    categoryId: '',
    includeMultiple: false,
    requirements: ''
  })
}

// 获取所有分类（用于AI生成）
const getAllCategories = () => {
  const allCategories = []
  
  // 根据选择的题目类型筛选对应的分类
  if (aiGenerateForm.types && aiGenerateForm.types.length > 0) {
    // 获取主要题目类型（如果选择了多种类型，使用第一种）
    const primaryType = aiGenerateForm.types[0]
    let parentCategoryName = ''
    
    switch (primaryType) {
      case 'CHOICE':
        parentCategoryName = '选择题'
        break
      case 'JUDGE':
        parentCategoryName = '判断题'
        break
      case 'TEXT':
        parentCategoryName = '简答题'
        break
      default:
        // 如果没有匹配的类型，返回所有分类
        categoryTree.value.forEach(parent => {
          if (parent.children) {
            allCategories.push(...parent.children)
          }
        })
        return allCategories
    }
    
    // 查找对应的父级分类
    const parentCategory = categoryTree.value.find(cat => cat.name === parentCategoryName)
    if (parentCategory && parentCategory.children) {
      allCategories.push(...parentCategory.children)
    }
  } else {
    // 如果没有选择题目类型，返回所有分类
    categoryTree.value.forEach(parent => {
      if (parent.children) {
        allCategories.push(...parent.children)
      }
    })
  }
  
  return allCategories
}

// 获取选中的题目类型文本
const getSelectedTypesText = () => {
  const typeMap = {
    'CHOICE': '选择题',
    'JUDGE': '判断题',
    'TEXT': '简答题'
  }
  return aiGenerateForm.types.map(type => typeMap[type]).join('、')
}

// 开始AI生成
const startAiGenerate = async () => {
  // 表单验证
  if (!aiFormRef.value) return
  
  const valid = await aiFormRef.value.validate().catch(() => false)
  if (!valid) return

  aiGenerating.value = true
  aiGenerateStep.value = 1
  generateProgress.value = 0

  // 更详细的进度模拟
  const progressInterval = setInterval(() => {
    if (generateProgress.value < 85) {
      generateProgress.value += Math.random() * 8 // 更慢的进度增长
    }
  }, 1500) // 每1.5秒更新一次

  try {
    const requestData = {
      ...aiGenerateForm,
      types: aiGenerateForm.types.join(',')
    }

    // 增加请求超时时间到3分钟
    const controller = new AbortController()
    const timeoutId = setTimeout(() => controller.abort(), 180000) // 3分钟超时

    const res = await request.post('/api/questions/batch/ai-generate', requestData, {
      signal: controller.signal,
      timeout: 180000 // 3分钟超时
    })
    
    clearTimeout(timeoutId)
    clearInterval(progressInterval)
    generateProgress.value = 100

    setTimeout(() => {
      aiGeneratedData.value = res.data
      aiGenerateStep.value = 2
      ElMessage.success(`AI生成完成！共生成 ${res.data.length} 道题目`)
    }, 500)

  } catch (error) {
    clearInterval(progressInterval)
    
    let errorMessage = '生成失败，请稍后重试'
    
    if (error.name === 'AbortError' || error.message.includes('timeout')) {
      errorMessage = 'AI生成超时，请尝试减少生成数量或简化需求后重试'
    } else if (error.response?.data?.message) {
      errorMessage = error.response.data.message
    } else if (error.message) {
      errorMessage = error.message
    }
    
    ElMessage.error(errorMessage)
    
    // 显示重试选项
    setTimeout(() => {
      ElMessageBox.confirm(
        '是否要重新尝试生成？您可以尝试减少生成数量或简化需求。',
        '生成失败',
        {
          confirmButtonText: '重试',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        // 重置为参数设置步骤
        aiGenerateStep.value = 0
        generateProgress.value = 0
      }).catch(() => {
        // 用户取消，关闭对话框
        aiGenerateDialogVisible.value = false
      })
    }, 1000)
    
  } finally {
    aiGenerating.value = false
  }
}

// 编辑AI生成的题目
const editAiQuestion = (index) => {
  editingAiQuestionIndex.value = index
  editingAiQuestion.value = JSON.parse(JSON.stringify(aiGeneratedData.value[index]))
  editAiQuestionDialogVisible.value = true
}

// 保存AI题目编辑
const saveAiQuestionEdit = () => {
  if (editingAiQuestionIndex.value >= 0 && editingAiQuestion.value) {
    aiGeneratedData.value[editingAiQuestionIndex.value] = JSON.parse(JSON.stringify(editingAiQuestion.value))
    editAiQuestionDialogVisible.value = false
    ElMessage.success('题目修改成功')
  }
}

// 确认AI生成题目导入
const confirmAiImport = async () => {
  aiImportLoading.value = true
  try {
    const res = await request.post('/api/questions/batch/import-questions', aiGeneratedData.value)
    
    aiImportResult.success = true
    aiImportResult.title = 'AI题目导入成功！'
    aiImportResult.message = res.data
    aiGenerateStep.value = 3

    // 刷新题目列表
    await fetchQuestions()
    await fetchCategories() // 重新获取分类数据以更新数量
  } catch (error) {
    aiImportResult.success = false
    aiImportResult.title = '导入失败'
    aiImportResult.message = error.message || '导入过程中发生错误'
    aiGenerateStep.value = 3
  } finally {
    aiImportLoading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  pagination.current = 1
  fetchQuestions()
}

// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    keyword: '',
    categoryId: '',
    difficulty: '',
    type: ''
  })
  handleSearch()
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  fetchQuestions()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  fetchQuestions()
}

// 分类节点点击
const handleNodeClick = (data) => {
  console.log('点击的分类节点:', data) // 调试日志
  
  // 只有子分类（叶子节点）才能查询题目
  // 判断是否为叶子节点：没有children或者children为空数组
  if (!data.children || data.children.length === 0) {
    console.log('点击的是子分类，设置分类ID:', data.id) // 调试日志
    searchForm.categoryId = data.id
    handleSearch()
  } else {
    console.log('点击的是父分类，不执行查询') // 调试日志
    // 可以选择展开/折叠父分类节点，但不执行查询
  }
}

// 显示添加对话框
const showAddDialog = () => {
  isEdit.value = false
  Object.assign(questionForm, initQuestionForm())
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row) => {
  isEdit.value = true
  // 深拷贝数据
  Object.assign(questionForm, JSON.parse(JSON.stringify(row)))
  dialogVisible.value = true
}

// 题目类型改变处理
const onTypeChange = () => {
  if (questionForm.type === 'CHOICE') {
    questionForm.choices = [
      { content: '', isCorrect: false },
      { content: '', isCorrect: false }
    ]
  } else {
    questionForm.answer = { answer: '' }
  }
}

// 添加选项
const addChoice = () => {
  if (questionForm.choices.length < 6) {
    questionForm.choices.push({ content: '', isCorrect: false })
  }
}

// 删除选项
const removeChoice = (index) => {
  if (questionForm.choices.length > 2) {
    questionForm.choices.splice(index, 1)
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value) {
      await request.put(`/api/questions/${questionForm.id}`, questionForm)
      ElMessage.success('题目更新成功')
    } else {
      await request.post('/api/questions', questionForm)
      ElMessage.success('题目添加成功')
    }
    
    dialogVisible.value = false
    await fetchQuestions()
    await fetchCategories() // 重新获取分类数据以更新数量
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

// 删除题目
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这道题目吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await request.delete(`/api/questions/${row.id}`)
    ElMessage.success('删除成功')
    await fetchQuestions()
    await fetchCategories() // 重新获取分类数据以更新数量
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 工具方法
const getQuestionTypeText = (type) => {
  const typeMap = {
    'CHOICE': '选择题',
    'JUDGE': '判断题',
    'TEXT': '简答题'
  }
  return typeMap[type] || type
}

const getQuestionTypeTag = (type) => {
  const tagMap = {
    'CHOICE': 'primary',
    'JUDGE': 'success',
    'TEXT': 'warning'
  }
  return tagMap[type] || 'info'
}

const getDifficultyText = (difficulty) => {
  const difficultyMap = {
    'EASY': '简单',
    'MEDIUM': '中等',
    'HARD': '困难'
  }
  return difficultyMap[difficulty] || difficulty
}

const getDifficultyType = (difficulty) => {
  const typeMap = {
    'EASY': 'success',
    'MEDIUM': 'warning',
    'HARD': 'danger'
  }
  return typeMap[difficulty] || 'info'
}

// 监听AI生成表单中题目类型的变化
watch(() => aiGenerateForm.types, (newTypes, oldTypes) => {
  // 如果题目类型发生变化，重置分类选择
  if (newTypes && oldTypes && JSON.stringify(newTypes) !== JSON.stringify(oldTypes)) {
    aiGenerateForm.categoryId = ''
    // 提示用户重新选择分类
    nextTick(() => {
      ElMessage.info('题目类型已更改，请重新选择对应的分类')
    })
  }
}, { deep: true })

// 初始化
onMounted(() => {
  fetchCategories()
  fetchQuestions()
})

// 新增：重置分类筛选
const resetCategoryFilter = () => {
  searchForm.categoryId = ''
  handleSearch()
}

// 新增：批量删除
const selectedQuestions = ref([])
const handleBatchDelete = async () => {
  if (selectedQuestions.value.length === 0) {
    ElMessage.warning('请先选择要删除的题目')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedQuestions.value.length} 道题目吗？此操作不可撤销。`,
      '批量删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: false
      }
    )
    
    // 批量删除API调用
    const deletePromises = selectedQuestions.value.map(id => 
      request.delete(`/api/questions/${id}`)
    )
    
    await Promise.all(deletePromises)
    
    ElMessage.success(`成功删除 ${selectedQuestions.value.length} 道题目`)
    selectedQuestions.value = []
    await fetchQuestions()
    await fetchCategories() // 重新获取分类数据以更新数量
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败：' + (error.message || '未知错误'))
    }
  }
}

// 新增：题目选择处理
const handleSelectionChange = (selection) => {
  selectedQuestions.value = selection.map(item => item.id)
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

.search-bar {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

/* 搜索栏按钮间距优化 */
.search-bar .el-button {
  margin-right: 8px;
}

.search-bar .el-button:last-child {
  margin-right: 0;
}

.search-bar .el-dropdown {
  margin-right: 0;
}

/* 新增：两行布局样式 */
.search-row {
  margin-bottom: 15px;
}

.action-row {
  margin-bottom: 0;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.action-buttons .el-button,
.action-buttons .el-dropdown {
  margin-right: 0;
}

.content-wrapper {
  display: flex;
  gap: 20px;
}

.question-section {
  flex: 1;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.question-list {
  padding: 20px;
}

.category-section {
  width: 300px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.category-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.category-header h3 {
  margin: 0;
  color: #333;
}

.category-list {
  padding: 20px;
}

.custom-tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.category-count {
  color: #999;
  font-size: 12px;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}

.choice-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.choice-item .el-input {
  flex: 1;
}

/* 新增：批量导入样式 */
.batch-import-container {
  padding: 20px 0;
}

.step-content {
  margin-top: 30px;
}

.upload-step {
  text-align: center;
}

.upload-actions {
  margin: 20px 0;
}

.step-actions {
  margin-top: 30px;
  text-align: center;
}

.preview-step {
  max-height: 600px;
  overflow-y: auto;
}

.choice-preview {
  margin: 2px 0;
  font-size: 13px;
}

.correct-choice {
  color: #67c23a;
  font-weight: bold;
}

/* 新增：AI生成样式 */
.ai-generate-container {
  padding: 20px 0;
}

.params-step .form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.generating-step {
  text-align: center;
  padding: 40px 20px;
}

.generating-content h3 {
  margin: 20px 0 10px;
  color: #333;
}

.generating-content p {
  color: #666;
  margin: 5px 0;
}

.loading-icon {
  animation: rotation 1s linear infinite;
  color: #409eff;
  margin-bottom: 20px;
}

@keyframes rotation {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.ai-preview-step {
  max-height: 700px;
  overflow-y: auto;
}

.preview-tips {
  margin-bottom: 15px;
}

.question-title {
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 5px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.question-title:hover {
  background-color: #f5f7fa;
}

.edit-icon {
  opacity: 0;
  transition: opacity 0.2s;
  color: #409eff;
}

.question-title:hover .edit-icon {
  opacity: 1;
}

.answer-preview {
  color: #606266;
  font-size: 13px;
}

.choice-edit-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.choice-edit-item .el-input {
  flex: 1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .app-container {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    height: auto;
  }
  
  .content-wrapper {
    flex-direction: column;
  }
  
  .category-section {
    width: 100%;
  }
}
</style> 