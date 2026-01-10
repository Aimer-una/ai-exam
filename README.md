

# AI链学习室

基于Spring Boot的在线考试与视频学习系统，包含AI智能组卷、自动批阅、视频管理等功能。

## 功能特性

### 考试系统
- **AI智能组卷**：基于设定的规则（题型分布、难度配比等）使用AI自动生成试卷
- **在线考试**：支持开始考试、答题提交、考试记录管理
- **自动批阅**：使用AI技术自动批阅试卷，特别是简答题的智能评分
- **考试排行榜**：展示考试成绩排行榜，支持按试卷筛选

### 题库管理
- **题目管理**：题目增删改查、分类管理、难度筛选
- **Excel导入**：支持批量导入题目，提供导入模板下载
- **AI生成题目**：使用AI技术根据指定主题和要求智能生成题目
- **热门推荐**：获取访问次数最多的热门题目

### 视频学习
- **视频管理**：视频上传、审核、发布、下架、删除
- **分类管理**：支持树形层级结构的视频分类管理
- **观看统计**：记录视频观看行为和时长统计
- **互动功能**：视频点赞、收藏功能

### 内容管理
- **轮播图管理**：前台首页轮播图的上传和管理
- **系统公告**：系统公告的发布、编辑、状态管理
- **数据统计**：系统概览统计、图表展示

## 技术栈

| 分类 | 技术 |
|------|------|
| 后端框架 | Spring Boot + MyBatis Plus |
| API文档 | Knife4j (Swagger 3.0) |
| 文件存储 | MinIO |
| 数据库 | MySQL |
| 缓存 | Redis |
| 工具库 | Lombok |
| 前端框架 | Vue.js |

## 项目结构

```
ai-chain-learning-room/
├── src/main/java/com/atguigu/exam/
│   ├── config/           # 配置类
│   │   ├── Knife4jConfiguration.java
│   │   ├── MinioConfiguration.java
│   │   ├── MybatisPlusConfiguration.java
│   │   ├── RedisConfig.java
│   │   └── WebClientConfiguration.java
│   ├── controller/       # 控制器层
│   │   ├── BannerController.java      # 轮播图管理
│   │   ├── CategoryController.java    # 题目分类管理
│   │   ├── ExamController.java        # 考试管理
│   │   ├── ExamRecordController.java  # 考试记录管理
│   │   ├── NoticeController.java      # 公告管理
│   │   ├── PaperController.java       # 试卷管理
│   │   ├── QuestionController.java    # 题目管理
│   │   ├── QuestionBatchController.java # 题目批量操作
│   │   ├── StatsController.java       # 数据统计
│   │   ├── UserController.java        # 用户管理
│   │   ├── VideoAdminController.java  # 视频管理(管理端)
│   │   ├── VideoCategoryController.java # 视频分类管理
│   │   └── VideoController.java       # 视频管理(用户端)
│   ├── entity/           # 实体类
│   ├── mapper/           # 数据访问层
│   ├── service/          # 服务层
│   │   └── impl/         # 服务实现类
│   ├── utils/            # 工具类
│   │   ├── ExcelUtil.java
│   │   ├── IpUtils.java
│   │   └── RedisUtils.java
│   └── vo/               # 视图对象
├── exam-system-web-backup/  # Vue.js前端项目
├── exam_system_new.sql      # 数据库脚本
└── pom.xml
```

## 接口文档

系统提供完整的RESTful API文档，通过Knife4j提供可视化接口文档。

### 主要接口模块

| 模块 | 路径 | 说明 |
|------|------|------|
| 用户认证 | `/api/user/login` | 用户登录验证 |
| 考试管理 | `/api/exams` | 考试流程相关操作 |
| 试卷管理 | `/api/papers` | 试卷创建、AI组卷 |
| 题目管理 | `/api/questions` | 题目CRUD、批量导入 |
| 视频管理 | `/api/videos` | 用户端视频操作 |
| 视频管理(管理端) | `/api/admin/videos` | 管理端视频审核 |
| 轮播图管理 | `/api/banners` | 轮播图增删改查 |
| 公告管理 | `/api/notices` | 系统公告管理 |
| 数据统计 | `/api/stats` | 系统统计信息 |

## 安装部署

### 环境要求
- JDK 1.8+
- MySQL 5.7+
- Redis 3.0+
- MinIO (文件存储服务)
- Maven 3.6+

### 数据库配置

1. 创建MySQL数据库：
```sql
CREATE DATABASE exam_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 导入数据库脚本：
```bash
mysql -u username -p exam_system < exam_system_new.sql
```

### 应用配置

修改 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/exam_system
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379

minio:
  endPoint: http://localhost:9000
  accessKey: your_access_key
  secretKey: your_secret_key
  bucketName: exam-bucket

kimi:
  api:
    url: https://api.moonshot.cn/v1
    apiKey: your_api_key
    model: moonshot-v1-8k
```

### 启动项目

```bash
# 克隆项目
git clone https://gitee.com/AimerUna/ai-chain-learning-room.git

# 进入项目目录
cd ai-chain-learning-room

# 编译并启动
mvn spring-boot:run
```

### 访问地址

- **API文档地址**：http://localhost:8080/doc.html
- **前端应用**：exam-system-web-backup 目录下部署

## 使用说明

### 考试管理端

1. **试卷管理**：支持手动创建试卷或使用AI智能组卷
2. **题目管理**：可单个添加或批量导入题目
3. **考试记录**：查看学生考试记录、成绩统计
4. **排行榜**：查看考试成绩排名

### 视频管理端

1. **视频上传**：管理员可直接上传视频
2. **审核管理**：审核用户投稿的视频
3. **分类管理**：维护视频分类体系
4. **数据统计**：查看视频播放、点赞等统计数据

### AI功能说明

- **AI组卷**：根据设定的题型分布、难度配比自动生成试卷
- **AI出题**：根据主题和要求智能生成题目
- **AI批阅**：自动批阅简答题，提供评分和反馈

## 许可证

本项目采用 Apache-2.0 开源协议。

## 贡献指南

欢迎贡献代码，请遵循以下规范：

1. Fork项目并创建功能分支
2. 确保代码符合Java编码规范
3. 添加必要的注释和文档
4. 提交PR并说明修改内容