/*
 Navicat Premium Data Transfer

 Source Server         : Elysia
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : localhost:3306
 Source Schema         : exam_system_new

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 24/12/2025 09:59:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for answer_record
-- ----------------------------
DROP TABLE IF EXISTS `answer_record`;
CREATE TABLE `answer_record`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `exam_record_id` int(0) NOT NULL,
  `question_id` int(0) NOT NULL,
  `user_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `score` int(0) NULL DEFAULT 0,
  `is_correct` int(0) NULL DEFAULT 0,
  `ai_correction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 293 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of answer_record
-- ----------------------------
INSERT INTO `answer_record` VALUES (231, 41, 66, 'T', 5, 1, NULL, '2025-07-30 18:55:11', '2025-11-02 16:33:30', 1);
INSERT INTO `answer_record` VALUES (232, 41, 68, 'F', 0, 0, NULL, '2025-07-30 18:55:11', '2025-11-02 16:33:30', 1);
INSERT INTO `answer_record` VALUES (233, 41, 69, '阿达大', 0, 0, '答案与题目要求不符，完全错误', '2025-07-30 18:55:11', '2025-11-02 16:33:30', 1);
INSERT INTO `answer_record` VALUES (234, 41, 71, 'A', 0, 0, NULL, '2025-07-30 18:55:11', '2025-11-02 16:33:30', 1);
INSERT INTO `answer_record` VALUES (235, 41, 72, 'A', 0, 0, NULL, '2025-07-30 18:55:11', '2025-11-02 16:33:30', 1);
INSERT INTO `answer_record` VALUES (236, 41, 73, '阿达大', 0, 0, '答案与题目要求不符', '2025-07-30 18:55:11', '2025-11-02 16:33:30', 1);
INSERT INTO `answer_record` VALUES (237, 41, 74, 'T', 5, 1, NULL, '2025-07-30 18:55:11', '2025-11-02 16:33:30', 1);
INSERT INTO `answer_record` VALUES (238, 41, 75, 'B', 0, 0, NULL, '2025-07-30 18:55:11', '2025-11-02 16:33:30', 1);
INSERT INTO `answer_record` VALUES (239, 41, 76, 'A,B', 0, 0, NULL, '2025-07-30 18:55:11', '2025-11-02 16:33:30', 1);
INSERT INTO `answer_record` VALUES (240, 41, 77, 'A', 0, 0, NULL, '2025-07-30 18:55:11', '2025-11-02 16:33:30', 1);
INSERT INTO `answer_record` VALUES (241, 41, 78, 'B', 0, 0, NULL, '2025-07-30 18:55:11', '2025-11-02 16:33:30', 1);
INSERT INTO `answer_record` VALUES (242, 41, 79, 'B', 0, 0, NULL, '2025-07-30 18:55:11', '2025-11-02 16:33:30', 1);
INSERT INTO `answer_record` VALUES (243, 42, 68, 'T', 0, 0, NULL, '2025-11-02 14:23:30', '2025-11-02 16:34:13', 1);
INSERT INTO `answer_record` VALUES (244, 42, 69, '31312', 0, 0, NULL, '2025-11-02 14:23:30', '2025-11-02 16:34:13', 1);
INSERT INTO `answer_record` VALUES (245, 42, 73, '231321', 0, 0, NULL, '2025-11-02 14:23:30', '2025-11-02 16:34:13', 1);
INSERT INTO `answer_record` VALUES (246, 42, 84, 'A', 0, 0, NULL, '2025-11-02 14:23:30', '2025-11-02 16:34:13', 1);
INSERT INTO `answer_record` VALUES (247, 42, 86, 'B', 0, 0, NULL, '2025-11-02 14:23:30', '2025-11-02 16:34:13', 1);
INSERT INTO `answer_record` VALUES (248, 43, 68, 'T', 0, 0, NULL, '2025-11-02 14:26:15', '2025-11-02 14:26:15', 0);
INSERT INTO `answer_record` VALUES (249, 43, 69, '213213', 0, 0, NULL, '2025-11-02 14:26:15', '2025-11-02 14:26:15', 0);
INSERT INTO `answer_record` VALUES (250, 43, 73, '321312', 0, 0, NULL, '2025-11-02 14:26:15', '2025-11-02 14:26:15', 0);
INSERT INTO `answer_record` VALUES (251, 43, 84, 'A', 0, 0, NULL, '2025-11-02 14:26:15', '2025-11-02 14:26:15', 0);
INSERT INTO `answer_record` VALUES (252, 43, 86, 'B', 0, 0, NULL, '2025-11-02 14:26:15', '2025-11-02 14:26:15', 0);
INSERT INTO `answer_record` VALUES (253, 44, 66, 'T', 5, 1, NULL, '2025-11-02 14:30:14', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (254, 44, 68, 'T', 5, 1, NULL, '2025-11-02 14:30:14', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (255, 44, 69, '1232', 5, 1, NULL, '2025-11-02 14:30:14', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (256, 44, 71, 'A', 0, 0, NULL, '2025-11-02 14:30:14', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (257, 44, 72, 'A', 0, 0, NULL, '2025-11-02 14:30:14', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (258, 44, 73, '32131', 5, 1, NULL, '2025-11-02 14:30:14', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (259, 44, 74, 'T', 5, 1, NULL, '2025-11-02 14:30:14', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (260, 44, 75, 'A,B,C', 5, 1, NULL, '2025-11-02 14:30:14', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (261, 44, 76, 'D', 0, 0, NULL, '2025-11-02 14:30:14', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (262, 44, 77, 'D', 0, 0, NULL, '2025-11-02 14:30:14', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (263, 44, 78, 'D', 0, 0, NULL, '2025-11-02 14:30:14', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (264, 44, 79, 'B', 0, 0, NULL, '2025-11-02 14:30:14', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (265, 44, 66, 'T', 5, 1, NULL, '2025-11-02 14:34:54', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (266, 44, 68, 'T', 5, 1, NULL, '2025-11-02 14:34:54', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (267, 44, 69, '1232', 5, 1, NULL, '2025-11-02 14:34:54', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (268, 44, 71, 'A', 0, 0, NULL, '2025-11-02 14:34:54', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (269, 44, 72, 'A', 0, 0, NULL, '2025-11-02 14:34:54', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (270, 44, 73, '32131', 5, 1, NULL, '2025-11-02 14:34:54', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (271, 44, 74, 'T', 5, 1, NULL, '2025-11-02 14:34:54', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (272, 44, 75, 'A,B,C', 5, 1, NULL, '2025-11-02 14:34:54', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (273, 44, 76, 'D', 0, 0, NULL, '2025-11-02 14:34:54', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (274, 44, 77, 'D', 0, 0, NULL, '2025-11-02 14:34:54', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (275, 44, 78, 'D', 0, 0, NULL, '2025-11-02 14:34:54', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (276, 44, 79, 'B', 0, 0, NULL, '2025-11-02 14:34:54', '2025-11-02 16:32:41', 1);
INSERT INTO `answer_record` VALUES (277, 45, 68, 'F', 0, 0, NULL, '2025-11-02 14:36:34', '2025-11-02 16:33:13', 1);
INSERT INTO `answer_record` VALUES (278, 45, 69, '12', 10, 1, NULL, '2025-11-02 14:36:34', '2025-11-02 16:33:13', 1);
INSERT INTO `answer_record` VALUES (279, 45, 73, '312', 10, 1, NULL, '2025-11-02 14:36:34', '2025-11-02 16:33:13', 1);
INSERT INTO `answer_record` VALUES (280, 45, 84, 'A', 5, 1, NULL, '2025-11-02 14:36:34', '2025-11-02 16:33:13', 1);
INSERT INTO `answer_record` VALUES (281, 45, 86, 'D', 0, 0, NULL, '2025-11-02 14:36:34', '2025-11-02 16:33:13', 1);
INSERT INTO `answer_record` VALUES (282, 46, 66, 'T', 5, 1, NULL, '2025-11-02 15:36:29', '2025-11-02 15:36:29', 0);
INSERT INTO `answer_record` VALUES (283, 46, 68, 'T', 5, 1, NULL, '2025-11-02 15:36:29', '2025-11-02 15:36:29', 0);
INSERT INTO `answer_record` VALUES (284, 46, 69, '宝宝我不知道', 0, 0, '完全未作答，无相关性', '2025-11-02 15:36:29', '2025-11-02 15:36:29', 0);
INSERT INTO `answer_record` VALUES (285, 46, 71, 'A', 0, 0, NULL, '2025-11-02 15:36:29', '2025-11-02 15:36:29', 0);
INSERT INTO `answer_record` VALUES (286, 46, 72, 'A', 0, 0, NULL, '2025-11-02 15:36:29', '2025-11-02 15:36:29', 0);
INSERT INTO `answer_record` VALUES (287, 46, 73, 'ioc依赖注入 aop面向切面编程', 4, 2, '标准答案为\'啦啦啦\'，学生答案提到了ioc和aop', '2025-11-02 15:36:29', '2025-11-02 15:36:29', 0);
INSERT INTO `answer_record` VALUES (288, 46, 74, 'T', 5, 1, NULL, '2025-11-02 15:36:29', '2025-11-02 15:36:29', 0);
INSERT INTO `answer_record` VALUES (289, 46, 75, 'A,B,C', 5, 1, NULL, '2025-11-02 15:36:29', '2025-11-02 15:36:29', 0);
INSERT INTO `answer_record` VALUES (290, 46, 76, 'D', 0, 0, NULL, '2025-11-02 15:36:29', '2025-11-02 15:36:29', 0);
INSERT INTO `answer_record` VALUES (291, 46, 77, 'A', 0, 0, NULL, '2025-11-02 15:36:29', '2025-11-02 15:36:29', 0);
INSERT INTO `answer_record` VALUES (292, 46, 78, 'B', 0, 0, NULL, '2025-11-02 15:36:29', '2025-11-02 15:36:29', 0);
INSERT INTO `answer_record` VALUES (293, 46, 79, 'A,B,C,D', 5, 1, NULL, '2025-11-02 15:36:29', '2025-11-02 15:36:29', 0);

-- ----------------------------
-- Table structure for banners
-- ----------------------------
DROP TABLE IF EXISTS `banners`;
CREATE TABLE `banners`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '轮播图标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '轮播图描述',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL',
  `link_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '跳转链接',
  `sort_order` int(0) NULL DEFAULT 0 COMMENT '排序顺序',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sort_order`(`sort_order`) USING BTREE,
  INDEX `idx_is_active`(`is_active`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '轮播图表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of banners
-- ----------------------------
INSERT INTO `banners` VALUES (4, 'Java+AI应用开发', '以Java+大模型应用为核，广度延伸至大数据&人工智能！', 'http://47.94.86.115:9000/java1229/banners/2025/07/18/c65f24222a724d0db9a481a46cd3b64a.png', 'https://www.atguigu.com/java/', 1, 1, '2025-06-25 18:27:00', '2025-07-24 16:56:02', 1);
INSERT INTO `banners` VALUES (5, '嵌入式&万物互联', '大牛手把手教你实操企业真实项目，无缝对接企业真实工作流程', 'http://47.94.86.115:9000/java1229/banners/2025/07/18/538d9315bcaa48599fa58f59682e3b75.png', 'https://www.atguigu.com/iot/', 2, 1, '2025-06-25 18:27:00', '2025-10-22 16:17:44', 0);
INSERT INTO `banners` VALUES (6, '人工智能大模型', '全球人工智能浪潮正劲，时代红利，先到先得！', 'http://47.94.86.115:9000/java1229/banners/2025/07/18/c041fd1e11ff4bc09aa5831c4cb08e5b.png', 'https://www.atguigu.com/ai/', 3, 1, '2025-06-25 18:27:00', '2025-07-23 19:05:59', 0);
INSERT INTO `banners` VALUES (7, 'AI智能运维', 'BAT级实战课研团队,直面百亿级业务运维现场！', 'http://47.94.86.115:9000/java1229/banners/2025/07/18/791f809ce55d463d920058de2338da35.png', 'https://www.atguigu.com/sre/', 4, 1, '2025-06-26 15:36:37', '2025-10-22 16:28:37', 1);
INSERT INTO `banners` VALUES (8, '智能考试系统介绍', '基于AI技术的智能考试平台，支持在线考试、智能组卷等功能', 'http://47.94.86.115:9000/exam-system/banners/20250724/9d1d9845-ff09-4042-8397-5a3dc2335c7f-xxlx.png', 'https://example.com/about', 1, 1, '2025-07-24 16:42:53', '2025-10-22 16:17:42', 0);
INSERT INTO `banners` VALUES (9, '测试轮播图', '测试轮播图描述', 'http://47.94.86.115:9000/java1229/banners/2025/07/24/ec851da3deeb402f88190c712df3462a.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250724%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250724T085529Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=1223dc892d2c4726df8d1ca04a1ed12446ffa40191b36e2df52a53e3ece4b6a9', 'http://www.baidu.com', 2, 1, '2025-07-24 16:55:46', '2025-10-22 16:17:48', 0);
INSERT INTO `banners` VALUES (10, '213213', '张润12', 'http://192.168.100.168:9000/onlinebucket/banner/20251023/2c818f4277284ceda3e50e3ddbb05f5e_1.jpg', '', 1, 1, '2025-10-23 18:31:52', '2025-10-23 18:34:07', 0);

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `parent_id` bigint(0) NULL DEFAULT 0,
  `sort` int(0) NULL DEFAULT 0,
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `categories` VALUES (13, '选择题', 0, 1, '2025-07-23 14:17:32', '2025-07-23 14:17:32', 0);
INSERT INTO `categories` VALUES (14, '判断题', 0, 2, '2025-07-23 14:17:32', '2025-07-23 14:17:32', 0);
INSERT INTO `categories` VALUES (15, '简答题', 0, 3, '2025-07-23 14:17:32', '2025-07-23 14:17:32', 0);
INSERT INTO `categories` VALUES (16, 'Java基础', 13, 1, '2025-07-23 14:17:32', '2025-07-23 14:17:32', 0);
INSERT INTO `categories` VALUES (17, 'Java进阶', 13, 2, '2025-07-23 14:17:32', '2025-07-23 14:17:32', 0);
INSERT INTO `categories` VALUES (18, 'Spring框架', 13, 3, '2025-07-23 14:17:32', '2025-07-23 14:17:32', 0);
INSERT INTO `categories` VALUES (19, '数据库', 13, 4, '2025-07-23 14:17:32', '2025-07-23 14:17:32', 0);
INSERT INTO `categories` VALUES (20, '前端技术', 13, 5, '2025-07-23 14:17:32', '2025-07-23 14:17:32', 1);
INSERT INTO `categories` VALUES (21, 'Java语法', 14, 1, '2025-07-23 14:17:32', '2025-07-23 14:17:32', 0);
INSERT INTO `categories` VALUES (22, '编程规范', 14, 2, '2025-07-23 14:17:32', '2025-07-23 14:17:32', 0);
INSERT INTO `categories` VALUES (23, '最佳实践', 14, 3, '2025-07-23 14:17:32', '2025-07-23 14:17:32', 0);
INSERT INTO `categories` VALUES (24, '系统设计', 15, 1, '2025-07-23 14:17:32', '2025-07-23 14:17:32', 0);
INSERT INTO `categories` VALUES (25, '性能优化', 15, 2, '2025-07-23 14:17:32', '2025-07-23 14:17:32', 0);
INSERT INTO `categories` VALUES (26, '架构设计', 15, 3, '2025-07-23 14:17:32', '2025-07-23 14:17:32', 1);
INSERT INTO `categories` VALUES (29, 'maven工具', 13, 0, '2025-07-25 11:12:21', '2025-07-25 11:12:21', 0);
INSERT INTO `categories` VALUES (30, 'git', 13, 0, '2025-07-25 11:39:43', '2025-07-25 11:39:43', 0);
INSERT INTO `categories` VALUES (31, '测试4', 14, 6, '2025-07-25 11:50:31', '2025-07-25 11:52:59', 0);
INSERT INTO `categories` VALUES (32, '张润1', 13, 1, '2025-10-26 10:37:10', '2025-10-26 11:32:37', 1);
INSERT INTO `categories` VALUES (33, '刘和静', 13, 1, '2025-10-26 10:46:40', '2025-10-26 10:46:40', 0);

-- ----------------------------
-- Table structure for exam_records
-- ----------------------------
DROP TABLE IF EXISTS `exam_records`;
CREATE TABLE `exam_records`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `exam_id` int(0) NOT NULL,
  `student_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `score` int(0) NULL DEFAULT 0,
  `answers` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `start_time` timestamp(0) NULL DEFAULT NULL,
  `end_time` timestamp(0) NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'ONGOING',
  `window_switches` int(0) NULL DEFAULT 0,
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_records
-- ----------------------------
INSERT INTO `exam_records` VALUES (38, 15, '赵伟风', 0, NULL, '2025-07-30 15:18:44', NULL, '进行中', 0, '2025-07-30 15:18:43', '2025-07-30 15:18:43', 0);
INSERT INTO `exam_records` VALUES (39, 17, 'xxx', 0, NULL, '2025-07-30 16:32:25', NULL, '进行中', 0, '2025-07-30 16:32:25', '2025-07-30 16:32:25', 0);
INSERT INTO `exam_records` VALUES (40, 20, '法国德国', 0, NULL, '2025-07-30 16:47:43', NULL, '进行中', 0, '2025-07-30 16:47:43', '2025-07-30 16:47:42', 0);
INSERT INTO `exam_records` VALUES (41, 20, '测试', 10, '本次考试成绩显示，学生在理解和掌握课程内容方面存在较大差距。得分率仅为16.7%，反映出学生对知识点的掌握不全面，解题技巧和应用能力亟待提高。优势在于能够答对两道题目，显示出一定的基础理解能力。不足之处在于多数题目未能正确解答，需要加强知识点的深入理解和运用。建议学生从基础知识入手，加强概念理解，多做练习题，提高解题速度和准确度。同时，注重培养逻辑思维和分析问题的能力。保持积极态度，相信自己能够通过努力提高成绩。加油，你有能力做得更好！', '2025-07-30 18:54:51', '2025-07-30 18:55:11', '已批阅', 0, '2025-07-30 18:54:51', '2025-11-02 16:33:30', 1);
INSERT INTO `exam_records` VALUES (42, 29, '张润', 0, '考试对应的试卷被删除！无法进行成绩判定！', '2025-11-01 17:19:47', '2025-11-02 14:23:31', '已批阅', 0, '2025-11-01 17:19:47', '2025-11-02 16:34:13', 1);
INSERT INTO `exam_records` VALUES (43, 29, '施贝贝', 0, '考试对应的试卷被删除！无法进行成绩判定！', '2025-11-02 14:26:06', '2025-11-02 14:26:16', '已批阅', 0, '2025-11-02 14:26:06', '2025-11-02 14:26:06', 0);
INSERT INTO `exam_records` VALUES (44, 9, '张润1', 60, '暂时不用ai评价', '2025-11-02 14:29:50', '2025-11-02 14:34:54', '已批阅', 0, '2025-11-02 14:29:50', '2025-11-02 16:32:41', 1);
INSERT INTO `exam_records` VALUES (45, 29, '施贝贝2', 25, '暂时不用ai评价', '2025-11-02 14:36:21', '2025-11-02 14:36:34', '已批阅', 0, '2025-11-02 14:36:21', '2025-11-02 16:33:13', 1);
INSERT INTO `exam_records` VALUES (46, 9, '张润', 29, '本次考试成绩显示，你在这次考试中的表现有待提高。得分率为48.3%，答对题数为5道，说明基础知识掌握不够牢固，解题技巧和应试能力有待加强。你的优势在于能够正确回答一半的题目，显示出一定的理解力。不足之处在于对部分知识点的掌握不够深入，导致错误较多。建议你加强基础知识的学习，多做练习题，提高解题速度和准确率。同时，要学会分析错题，找出错误原因，针对性地进行改进。保持积极态度，相信你通过努力，下次考试一定能取得更好的成绩。加油！', '2025-11-02 15:35:26', '2025-11-02 15:36:29', '已批阅', 0, '2025-11-02 15:35:26', '2025-11-02 15:35:26', 0);

-- ----------------------------
-- Table structure for notices
-- ----------------------------
DROP TABLE IF EXISTS `notices`;
CREATE TABLE `notices`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告内容',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'NOTICE' COMMENT '公告类型：SYSTEM(系统)、FEATURE(新功能)、NOTICE(通知)',
  `priority` int(0) NULL DEFAULT 0 COMMENT '优先级：0-普通，1-重要，2-紧急',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type`(`type`) USING BTREE,
  INDEX `idx_priority`(`priority`) USING BTREE,
  INDEX `idx_is_active`(`is_active`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notices
-- ----------------------------
INSERT INTO `notices` VALUES (4, 'Updated Title', 'Updated Content1', 'SYSTEM', 2, 1, '2025-06-25 18:27:00', '2025-10-23 22:35:59', 0);
INSERT INTO `notices` VALUES (5, 'New AI Generation Feature', 'We are excited to announce the new AI question generation feature. It supports multiple question types and difficulty levels.', 'FEATURE', 1, 1, '2025-06-25 18:27:00', '2025-06-25 18:27:00', 1);
INSERT INTO `notices` VALUES (6, 'Exam Guidelines', 'Please ensure stable network connection during online exams. Do not switch windows unnecessarily. Contact support if you encounter technical issues.', 'NOTICE', 0, 1, '2025-06-25 18:27:00', '2025-06-25 18:27:00', 0);
INSERT INTO `notices` VALUES (7, 'Welcome to Online Exam System', 'This system provides complete online examination solutions including question management, paper generation, online testing, and automatic grading.', 'NOTICE', 0, 1, '2025-06-25 18:27:00', '2025-06-25 18:27:00', 0);
INSERT INTO `notices` VALUES (8, 'Test Notice', 'This is a test notice', 'NOTICE', 0, 1, '2025-06-25 23:42:10', '2025-10-23 22:36:28', 0);
INSERT INTO `notices` VALUES (9, 'TestNotice', 'Test', 'NOTICE', 0, 1, '2025-06-25 23:42:18', '2025-06-25 23:42:18', 1);

-- ----------------------------
-- Table structure for paper
-- ----------------------------
DROP TABLE IF EXISTS `paper`;
CREATE TABLE `paper`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'DRAFT',
  `total_score` decimal(10, 2) NULL DEFAULT 0.00,
  `question_count` int(0) NULL DEFAULT 0,
  `duration` int(0) NOT NULL,
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of paper
-- ----------------------------
INSERT INTO `paper` VALUES (9, '测试1', '阿达大大大', 'PUBLISHED', 60.00, 12, 60, '2025-07-29 18:31:44', '2025-07-30 11:22:11', 0);
INSERT INTO `paper` VALUES (15, '测试ai组卷', '测试ai组卷 描述！', 'PUBLISHED', 25.00, 4, 60, '2025-07-30 10:52:25', '2025-07-30 11:22:12', 0);
INSERT INTO `paper` VALUES (16, '测试ai组卷', '测试ai组卷 描述！', 'DRAFT', 40.00, 7, 60, '2025-07-30 10:53:22', '2025-07-30 11:22:22', 1);
INSERT INTO `paper` VALUES (17, '手动2', '1111', 'PUBLISHED', 70.00, 12, 60, '2025-07-30 16:32:01', '2025-07-30 16:32:05', 0);
INSERT INTO `paper` VALUES (18, '手动333', '333', 'DRAFT', 51.00, 12, 20, NULL, '2025-07-30 16:43:11', 0);
INSERT INTO `paper` VALUES (19, '手动44', '大概打发', 'PUBLISHED', 60.00, 12, 60, NULL, '2025-07-30 16:44:34', 0);
INSERT INTO `paper` VALUES (20, '手动55', '相对湿度防守打法', 'PUBLISHED', 60.00, 12, 60, '2025-07-30 16:46:37', '2025-07-30 16:46:40', 0);
INSERT INTO `paper` VALUES (21, 'ai组卷222', '阿斯达大厦', 'DRAFT', 79.00, 12, 50, '2025-07-30 16:57:34', '2025-07-30 16:57:34', 0);
INSERT INTO `paper` VALUES (25, '张润试卷', '测试试卷', 'DRAFT', 25.00, 5, 60, '2025-10-30 18:23:58', '2025-10-30 18:23:58', 0);
INSERT INTO `paper` VALUES (26, 'ai组卷测试', '', 'DRAFT', 10.00, 1, 60, '2025-11-01 10:30:08', '2025-11-01 10:30:08', 0);
INSERT INTO `paper` VALUES (27, '张润', '', 'DRAFT', 0.00, 0, 60, '2025-11-01 10:34:13', '2025-11-01 10:34:13', 0);
INSERT INTO `paper` VALUES (28, '张润', '', 'DRAFT', 0.00, 0, 60, '2025-11-01 10:34:25', '2025-11-01 13:38:12', 1);
INSERT INTO `paper` VALUES (29, '张润', '', 'PUBLISHED', 35.00, 5, 60, '2025-11-01 10:37:18', '2025-11-01 13:23:54', 0);

-- ----------------------------
-- Table structure for paper_question
-- ----------------------------
DROP TABLE IF EXISTS `paper_question`;
CREATE TABLE `paper_question`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `paper_id` int(0) NOT NULL,
  `question_id` bigint(0) NOT NULL,
  `score` decimal(10, 2) NOT NULL,
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 203 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of paper_question
-- ----------------------------
INSERT INTO `paper_question` VALUES (97, 9, 66, 5.00, '2025-07-29 18:31:44', '2025-07-29 18:31:44', 0);
INSERT INTO `paper_question` VALUES (98, 9, 68, 5.00, '2025-07-29 18:31:44', '2025-07-29 18:31:44', 0);
INSERT INTO `paper_question` VALUES (99, 9, 69, 5.00, '2025-07-29 18:31:44', '2025-07-29 18:31:44', 0);
INSERT INTO `paper_question` VALUES (100, 9, 71, 5.00, '2025-07-29 18:31:44', '2025-07-29 18:31:44', 0);
INSERT INTO `paper_question` VALUES (101, 9, 72, 5.00, '2025-07-29 18:31:44', '2025-07-29 18:31:44', 0);
INSERT INTO `paper_question` VALUES (102, 9, 73, 5.00, '2025-07-29 18:31:44', '2025-07-29 18:31:44', 0);
INSERT INTO `paper_question` VALUES (103, 9, 74, 5.00, '2025-07-29 18:31:44', '2025-07-29 18:31:44', 0);
INSERT INTO `paper_question` VALUES (104, 9, 75, 5.00, '2025-07-29 18:31:44', '2025-07-29 18:31:44', 0);
INSERT INTO `paper_question` VALUES (105, 9, 76, 5.00, '2025-07-29 18:31:44', '2025-07-29 18:31:44', 0);
INSERT INTO `paper_question` VALUES (106, 9, 77, 5.00, '2025-07-29 18:31:44', '2025-07-29 18:31:44', 0);
INSERT INTO `paper_question` VALUES (107, 9, 78, 5.00, '2025-07-29 18:31:44', '2025-07-29 18:31:44', 0);
INSERT INTO `paper_question` VALUES (108, 9, 79, 5.00, '2025-07-29 18:31:44', '2025-07-29 18:31:44', 0);
INSERT INTO `paper_question` VALUES (109, 15, 75, 5.00, '2025-07-30 10:52:27', '2025-07-30 11:15:17', 1);
INSERT INTO `paper_question` VALUES (110, 15, 76, 5.00, '2025-07-30 10:52:27', '2025-07-30 11:15:17', 1);
INSERT INTO `paper_question` VALUES (111, 15, 77, 5.00, '2025-07-30 10:52:27', '2025-07-30 11:15:17', 1);
INSERT INTO `paper_question` VALUES (112, 15, 78, 5.00, '2025-07-30 10:52:27', '2025-07-30 11:15:17', 1);
INSERT INTO `paper_question` VALUES (113, 15, 79, 5.00, '2025-07-30 10:52:27', '2025-07-30 11:15:17', 1);
INSERT INTO `paper_question` VALUES (114, 15, 68, 5.00, '2025-07-30 10:52:29', '2025-07-30 11:15:17', 1);
INSERT INTO `paper_question` VALUES (115, 15, 69, 10.00, '2025-07-30 10:52:31', '2025-07-30 11:15:17', 1);
INSERT INTO `paper_question` VALUES (116, 16, 75, 5.00, '2025-07-30 10:53:23', '2025-07-30 11:22:22', 1);
INSERT INTO `paper_question` VALUES (117, 16, 76, 5.00, '2025-07-30 10:53:23', '2025-07-30 11:22:22', 1);
INSERT INTO `paper_question` VALUES (118, 16, 77, 5.00, '2025-07-30 10:53:23', '2025-07-30 11:22:22', 1);
INSERT INTO `paper_question` VALUES (119, 16, 78, 5.00, '2025-07-30 10:53:23', '2025-07-30 11:22:22', 1);
INSERT INTO `paper_question` VALUES (120, 16, 79, 5.00, '2025-07-30 10:53:23', '2025-07-30 11:22:22', 1);
INSERT INTO `paper_question` VALUES (121, 16, 68, 5.00, '2025-07-30 10:53:23', '2025-07-30 11:22:22', 1);
INSERT INTO `paper_question` VALUES (122, 16, 69, 10.00, '2025-07-30 10:53:23', '2025-07-30 11:22:22', 1);
INSERT INTO `paper_question` VALUES (123, 15, 69, 10.00, '2025-07-30 11:15:17', '2025-07-30 11:15:17', 0);
INSERT INTO `paper_question` VALUES (124, 15, 72, 5.00, '2025-07-30 11:15:17', '2025-07-30 11:15:17', 0);
INSERT INTO `paper_question` VALUES (125, 15, 74, 5.00, '2025-07-30 11:15:17', '2025-07-30 11:15:17', 0);
INSERT INTO `paper_question` VALUES (126, 15, 79, 5.00, '2025-07-30 11:15:17', '2025-07-30 11:15:17', 0);
INSERT INTO `paper_question` VALUES (127, 17, 66, 5.00, NULL, NULL, NULL);
INSERT INTO `paper_question` VALUES (128, 17, 68, 5.00, NULL, NULL, NULL);
INSERT INTO `paper_question` VALUES (129, 17, 69, 5.00, NULL, NULL, NULL);
INSERT INTO `paper_question` VALUES (130, 17, 71, 5.00, NULL, NULL, NULL);
INSERT INTO `paper_question` VALUES (131, 17, 72, 5.00, NULL, NULL, NULL);
INSERT INTO `paper_question` VALUES (132, 17, 73, 9.00, NULL, NULL, NULL);
INSERT INTO `paper_question` VALUES (133, 17, 74, 9.00, NULL, NULL, NULL);
INSERT INTO `paper_question` VALUES (134, 17, 75, 7.00, NULL, NULL, NULL);
INSERT INTO `paper_question` VALUES (135, 17, 76, 5.00, NULL, NULL, NULL);
INSERT INTO `paper_question` VALUES (136, 17, 77, 5.00, NULL, NULL, NULL);
INSERT INTO `paper_question` VALUES (137, 17, 78, 5.00, NULL, NULL, NULL);
INSERT INTO `paper_question` VALUES (138, 17, 79, 5.00, NULL, NULL, NULL);
INSERT INTO `paper_question` VALUES (139, 18, 66, 5.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (140, 18, 68, 5.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (141, 18, 69, 5.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (142, 18, 71, 5.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (143, 18, 72, 5.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (144, 18, 73, 5.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (145, 18, 74, 5.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (146, 18, 75, 5.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (147, 18, 76, 1.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (148, 18, 77, 2.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (149, 18, 78, 3.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (150, 18, 79, 5.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (151, 19, 66, 5.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (152, 19, 68, 5.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (153, 19, 69, 5.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (154, 19, 71, 5.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (155, 19, 72, 5.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (156, 19, 73, 5.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (157, 19, 74, 5.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (158, 19, 75, 5.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (159, 19, 76, 5.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (160, 19, 77, 5.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (161, 19, 78, 5.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (162, 19, 79, 5.00, NULL, NULL, 0);
INSERT INTO `paper_question` VALUES (163, 20, 66, 5.00, '2025-07-30 16:46:37', NULL, 0);
INSERT INTO `paper_question` VALUES (164, 20, 68, 5.00, '2025-07-30 16:46:37', NULL, 0);
INSERT INTO `paper_question` VALUES (165, 20, 69, 5.00, '2025-07-30 16:46:37', NULL, 0);
INSERT INTO `paper_question` VALUES (166, 20, 71, 5.00, '2025-07-30 16:46:37', NULL, 0);
INSERT INTO `paper_question` VALUES (167, 20, 72, 5.00, '2025-07-30 16:46:37', NULL, 0);
INSERT INTO `paper_question` VALUES (168, 20, 73, 5.00, '2025-07-30 16:46:37', NULL, 0);
INSERT INTO `paper_question` VALUES (169, 20, 74, 5.00, '2025-07-30 16:46:37', NULL, 0);
INSERT INTO `paper_question` VALUES (170, 20, 75, 5.00, '2025-07-30 16:46:37', NULL, 0);
INSERT INTO `paper_question` VALUES (171, 20, 76, 5.00, '2025-07-30 16:46:37', NULL, 0);
INSERT INTO `paper_question` VALUES (172, 20, 77, 5.00, '2025-07-30 16:46:37', NULL, 0);
INSERT INTO `paper_question` VALUES (173, 20, 78, 5.00, '2025-07-30 16:46:37', NULL, 0);
INSERT INTO `paper_question` VALUES (174, 20, 79, 5.00, '2025-07-30 16:46:37', NULL, 0);
INSERT INTO `paper_question` VALUES (175, 21, 71, 5.00, '2025-07-30 16:57:34', NULL, 0);
INSERT INTO `paper_question` VALUES (176, 21, 72, 5.00, '2025-07-30 16:57:34', NULL, 0);
INSERT INTO `paper_question` VALUES (177, 21, 75, 5.00, '2025-07-30 16:57:34', NULL, 0);
INSERT INTO `paper_question` VALUES (178, 21, 76, 5.00, '2025-07-30 16:57:34', NULL, 0);
INSERT INTO `paper_question` VALUES (179, 21, 77, 5.00, '2025-07-30 16:57:34', NULL, 0);
INSERT INTO `paper_question` VALUES (180, 21, 78, 5.00, '2025-07-30 16:57:34', NULL, 0);
INSERT INTO `paper_question` VALUES (181, 21, 79, 5.00, '2025-07-30 16:57:34', NULL, 0);
INSERT INTO `paper_question` VALUES (182, 21, 66, 8.00, '2025-07-30 16:57:34', NULL, 0);
INSERT INTO `paper_question` VALUES (183, 21, 68, 8.00, '2025-07-30 16:57:34', NULL, 0);
INSERT INTO `paper_question` VALUES (184, 21, 74, 8.00, '2025-07-30 16:57:34', NULL, 0);
INSERT INTO `paper_question` VALUES (185, 21, 69, 10.00, '2025-07-30 16:57:34', NULL, 0);
INSERT INTO `paper_question` VALUES (186, 21, 73, 10.00, '2025-07-30 16:57:34', NULL, 0);
INSERT INTO `paper_question` VALUES (187, 25, 69, 5.00, '2025-10-30 18:24:01', '2025-10-30 18:24:01', 0);
INSERT INTO `paper_question` VALUES (188, 25, 73, 5.00, '2025-10-30 18:24:01', '2025-10-30 18:24:01', 0);
INSERT INTO `paper_question` VALUES (189, 25, 74, 5.00, '2025-10-30 18:24:01', '2025-10-30 18:24:01', 0);
INSERT INTO `paper_question` VALUES (190, 25, 85, 5.00, '2025-10-30 18:24:01', '2025-10-30 18:24:01', 0);
INSERT INTO `paper_question` VALUES (191, 25, 86, 5.00, '2025-10-30 18:24:01', '2025-10-30 18:24:01', 0);
INSERT INTO `paper_question` VALUES (192, 26, 69, 10.00, '2025-11-01 10:30:08', '2025-11-01 10:30:08', 0);
INSERT INTO `paper_question` VALUES (193, 27, 86, 5.00, '2025-11-01 10:34:13', '2025-11-01 10:34:13', 0);
INSERT INTO `paper_question` VALUES (194, 27, 84, 5.00, '2025-11-01 10:34:13', '2025-11-01 10:34:13', 0);
INSERT INTO `paper_question` VALUES (195, 27, 68, 5.00, '2025-11-01 10:34:13', '2025-11-01 10:34:13', 0);
INSERT INTO `paper_question` VALUES (196, 28, 86, 5.00, '2025-11-01 10:34:25', '2025-11-01 13:38:12', 1);
INSERT INTO `paper_question` VALUES (197, 28, 84, 5.00, '2025-11-01 10:34:25', '2025-11-01 13:38:12', 1);
INSERT INTO `paper_question` VALUES (198, 28, 68, 5.00, '2025-11-01 10:34:25', '2025-11-01 13:38:12', 1);
INSERT INTO `paper_question` VALUES (199, 29, 84, 5.00, '2025-11-01 10:37:18', '2025-11-01 10:37:18', 0);
INSERT INTO `paper_question` VALUES (200, 29, 86, 5.00, '2025-11-01 10:37:18', '2025-11-01 10:37:18', 0);
INSERT INTO `paper_question` VALUES (201, 29, 68, 5.00, '2025-11-01 10:37:18', '2025-11-01 10:37:18', 0);
INSERT INTO `paper_question` VALUES (202, 29, 73, 10.00, '2025-11-01 10:37:18', '2025-11-01 10:37:18', 0);
INSERT INTO `paper_question` VALUES (203, 29, 69, 10.00, '2025-11-01 10:37:18', '2025-11-01 10:37:18', 0);

-- ----------------------------
-- Table structure for question_answers
-- ----------------------------
DROP TABLE IF EXISTS `question_answers`;
CREATE TABLE `question_answers`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `question_id` bigint(0) NOT NULL,
  `answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `keywords` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 79 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question_answers
-- ----------------------------
INSERT INTO `question_answers` VALUES (59, 65, 'A', NULL, '2025-07-25 17:15:17', '2025-07-27 12:38:02', 1);
INSERT INTO `question_answers` VALUES (60, 66, 'TRUE', NULL, '2025-07-25 17:20:05', '2025-07-25 17:27:21', 0);
INSERT INTO `question_answers` VALUES (61, 67, 'B,C', NULL, '2025-07-27 11:16:13', '2025-07-27 11:29:36', 1);
INSERT INTO `question_answers` VALUES (62, 68, 'TRUE', NULL, '2025-07-27 11:17:05', '2025-07-27 11:17:05', 0);
INSERT INTO `question_answers` VALUES (63, 69, '简答题答案！', NULL, '2025-07-27 11:17:31', '2025-07-27 11:17:31', 0);
INSERT INTO `question_answers` VALUES (64, 67, 'A,B,D', NULL, '2025-07-27 11:29:36', '2025-07-27 12:37:23', 1);
INSERT INTO `question_answers` VALUES (65, 70, 'A,D', NULL, '2025-07-27 12:39:06', '2025-07-27 12:40:29', 1);
INSERT INTO `question_answers` VALUES (66, 71, 'B,C', NULL, '2025-07-27 17:23:32', '2025-07-27 17:23:32', 0);
INSERT INTO `question_answers` VALUES (67, 72, 'A,C', NULL, '2025-07-27 18:28:56', '2025-07-27 18:28:56', 0);
INSERT INTO `question_answers` VALUES (68, 73, '啦啦啦', NULL, '2025-07-28 17:28:42', '2025-07-28 17:28:42', 0);
INSERT INTO `question_answers` VALUES (69, 74, 'true', NULL, '2025-07-28 17:28:42', '2025-07-28 17:28:42', 0);
INSERT INTO `question_answers` VALUES (70, 75, 'A,B,C', NULL, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_answers` VALUES (71, 76, 'A,B,D', NULL, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_answers` VALUES (72, 77, 'A,B,C,D', NULL, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_answers` VALUES (73, 78, 'A,B,C', NULL, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_answers` VALUES (74, 79, 'A,B,C,D', NULL, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_answers` VALUES (75, 81, 'A', NULL, '2025-10-28 19:45:15', '2025-10-28 22:55:39', 1);
INSERT INTO `question_answers` VALUES (76, 82, 'A', NULL, '2025-10-28 19:49:35', '2025-10-28 23:59:21', 1);
INSERT INTO `question_answers` VALUES (77, 84, 'A', NULL, '2025-10-28 19:57:03', '2025-10-28 19:57:03', 0);
INSERT INTO `question_answers` VALUES (78, 85, 'D', NULL, '2025-10-29 13:33:16', '2025-10-29 13:33:16', 0);
INSERT INTO `question_answers` VALUES (79, 86, 'B', NULL, '2025-10-30 15:10:29', '2025-10-30 15:10:29', 0);

-- ----------------------------
-- Table structure for question_choices
-- ----------------------------
DROP TABLE IF EXISTS `question_choices`;
CREATE TABLE `question_choices`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `question_id` bigint(0) NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_correct` tinyint(1) NULL DEFAULT 0,
  `sort` int(0) NULL DEFAULT 0,
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 124 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question_choices
-- ----------------------------
INSERT INTO `question_choices` VALUES (62, 65, '测试1', 0, 0, '2025-07-25 17:14:42', '2025-07-27 12:38:02', 1);
INSERT INTO `question_choices` VALUES (63, 65, '测试2', 1, 0, '2025-07-25 17:14:52', '2025-07-27 12:38:02', 1);
INSERT INTO `question_choices` VALUES (64, 65, '判断1', 0, 0, '2025-07-25 17:22:08', '2025-07-27 12:38:02', 1);
INSERT INTO `question_choices` VALUES (65, 65, '判断2', 0, 0, '2025-07-25 17:22:17', '2025-07-27 12:38:02', 1);
INSERT INTO `question_choices` VALUES (66, 67, '111', 0, 0, '2025-07-27 11:16:13', '2025-07-27 11:29:36', 1);
INSERT INTO `question_choices` VALUES (67, 67, '222', 1, 0, '2025-07-27 11:16:13', '2025-07-27 11:29:36', 1);
INSERT INTO `question_choices` VALUES (68, 67, '333', 1, 0, '2025-07-27 11:16:13', '2025-07-27 11:29:36', 1);
INSERT INTO `question_choices` VALUES (69, 67, '444', 0, 0, '2025-07-27 11:16:13', '2025-07-27 11:29:36', 1);
INSERT INTO `question_choices` VALUES (70, 67, '111', 1, 0, '2025-07-27 11:16:13', '2025-07-27 12:37:23', 1);
INSERT INTO `question_choices` VALUES (71, 67, '222', 1, 0, '2025-07-27 11:16:13', '2025-07-27 12:37:23', 1);
INSERT INTO `question_choices` VALUES (72, 67, '333', 0, 0, '2025-07-27 11:16:13', '2025-07-27 12:37:23', 1);
INSERT INTO `question_choices` VALUES (73, 67, '444', 1, 0, '2025-07-27 11:16:13', '2025-07-27 12:37:23', 1);
INSERT INTO `question_choices` VALUES (74, 70, '999', 1, 0, '2025-07-27 12:39:06', '2025-07-27 12:40:29', 1);
INSERT INTO `question_choices` VALUES (75, 70, '888', 0, 0, '2025-07-27 12:39:06', '2025-07-27 12:40:29', 1);
INSERT INTO `question_choices` VALUES (76, 70, '777', 0, 0, '2025-07-27 12:39:06', '2025-07-27 12:40:29', 1);
INSERT INTO `question_choices` VALUES (77, 70, '666', 1, 0, '2025-07-27 12:39:06', '2025-07-27 12:40:29', 1);
INSERT INTO `question_choices` VALUES (78, 71, '111', 0, 0, '2025-07-27 17:23:32', '2025-07-27 17:23:32', 0);
INSERT INTO `question_choices` VALUES (79, 71, '222', 1, 0, '2025-07-27 17:23:32', '2025-07-27 17:23:32', 0);
INSERT INTO `question_choices` VALUES (80, 71, '333', 1, 0, '2025-07-27 17:23:32', '2025-07-27 17:23:32', 0);
INSERT INTO `question_choices` VALUES (81, 71, '4444', 0, 0, '2025-07-27 17:23:32', '2025-07-27 17:23:32', 0);
INSERT INTO `question_choices` VALUES (82, 72, '1', 1, 0, '2025-07-27 18:28:56', '2025-07-27 18:28:56', 0);
INSERT INTO `question_choices` VALUES (83, 72, '2', 0, 0, '2025-07-27 18:28:56', '2025-07-27 18:28:56', 0);
INSERT INTO `question_choices` VALUES (84, 72, '3', 1, 0, '2025-07-27 18:28:56', '2025-07-27 18:28:56', 0);
INSERT INTO `question_choices` VALUES (85, 72, '4', 0, 0, '2025-07-27 18:28:56', '2025-07-27 18:28:56', 0);
INSERT INTO `question_choices` VALUES (86, 75, '构造器注入', 1, 1, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_choices` VALUES (87, 75, '字段注入', 1, 2, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_choices` VALUES (88, 75, 'Setter方法注入', 1, 3, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_choices` VALUES (89, 75, '接口注入', 0, 4, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_choices` VALUES (90, 76, '@Required', 1, 1, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_choices` VALUES (91, 76, '@Autowired', 1, 2, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_choices` VALUES (92, 76, '@Qualifier', 0, 3, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_choices` VALUES (93, 76, '@Service', 1, 4, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_choices` VALUES (94, 77, 'execution表达式', 1, 1, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_choices` VALUES (95, 77, 'within表达式', 1, 2, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_choices` VALUES (96, 77, 'this表达式', 1, 3, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_choices` VALUES (97, 77, 'bean表达式', 1, 4, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_choices` VALUES (98, 78, 'REQUIRED', 1, 1, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_choices` VALUES (99, 78, 'SUPPORTS', 1, 2, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_choices` VALUES (100, 78, 'MANDATORY', 1, 3, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_choices` VALUES (101, 78, 'NEVER', 0, 4, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_choices` VALUES (102, 79, '@Component', 1, 1, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_choices` VALUES (103, 79, '@Service', 1, 2, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_choices` VALUES (104, 79, '@Repository', 1, 3, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_choices` VALUES (105, 79, '@Controller', 1, 4, '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `question_choices` VALUES (109, 81, '答案1', 0, 0, NULL, '2025-10-28 22:55:39', 1);
INSERT INTO `question_choices` VALUES (110, 81, '答案2', 0, 1, NULL, '2025-10-28 22:55:39', 1);
INSERT INTO `question_choices` VALUES (111, 81, '答案3', 0, 2, NULL, '2025-10-28 22:55:39', 1);
INSERT INTO `question_choices` VALUES (112, 82, '张润1', 1, 0, NULL, '2025-10-28 23:59:21', 1);
INSERT INTO `question_choices` VALUES (113, 82, '张润2', 0, 1, NULL, '2025-10-28 23:59:21', 1);
INSERT INTO `question_choices` VALUES (114, 82, '张润3', 0, 2, NULL, '2025-10-28 23:59:21', 1);
INSERT INTO `question_choices` VALUES (115, 84, '施贝贝1', 1, 0, '2025-10-28 19:57:03', '2025-10-28 19:57:03', 0);
INSERT INTO `question_choices` VALUES (116, 84, '施贝贝2', 0, 1, '2025-10-28 19:57:03', '2025-10-28 19:57:03', 0);
INSERT INTO `question_choices` VALUES (117, 85, '依赖注入', 0, 0, '2025-10-29 13:33:16', '2025-10-29 13:33:16', 0);
INSERT INTO `question_choices` VALUES (118, 85, '面向切面编程', 0, 1, '2025-10-29 13:33:16', '2025-10-29 13:33:16', 0);
INSERT INTO `question_choices` VALUES (119, 85, '事务管理', 0, 2, '2025-10-29 13:33:16', '2025-10-29 13:33:16', 0);
INSERT INTO `question_choices` VALUES (120, 85, '以上都是', 1, 3, '2025-10-29 13:33:16', '2025-10-29 13:33:16', 0);
INSERT INTO `question_choices` VALUES (121, 86, '@Autowired', 0, 0, '2025-10-30 15:10:29', '2025-10-30 15:10:29', 0);
INSERT INTO `question_choices` VALUES (122, 86, '@Service', 1, 1, '2025-10-30 15:10:29', '2025-10-30 15:10:29', 0);
INSERT INTO `question_choices` VALUES (123, 86, '@Aspect', 0, 2, '2025-10-30 15:10:29', '2025-10-30 15:10:29', 0);
INSERT INTO `question_choices` VALUES (124, 86, '@PathVariable', 0, 3, '2025-10-30 15:10:29', '2025-10-30 15:10:29', 0);

-- ----------------------------
-- Table structure for questions
-- ----------------------------
DROP TABLE IF EXISTS `questions`;
CREATE TABLE `questions`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `title` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `multi` tinyint(1) NULL DEFAULT 0,
  `category_id` bigint(0) NOT NULL,
  `difficulty` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `score` int(0) NULL DEFAULT 5,
  `analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 86 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of questions
-- ----------------------------
INSERT INTO `questions` VALUES (65, '测试选择题', 'CHOICE', 0, 13, 'MEDIUM', 5, '数据库的数据类型', '2025-07-24 18:39:22', '2025-07-27 12:38:02', 1);
INSERT INTO `questions` VALUES (66, '测试判断题', 'JUDGE', 0, 14, 'HARD', 5, 'java是世界上最好的语言', '2025-07-25 17:14:14', '2025-07-25 17:14:14', 0);
INSERT INTO `questions` VALUES (67, '选择题1222', 'CHOICE', 1, 17, 'HARD', 5, '选择题解析', '2025-07-27 11:16:13', '2025-07-27 12:37:23', 1);
INSERT INTO `questions` VALUES (68, '判断题', 'JUDGE', 0, 22, 'MEDIUM', 5, '判断题解析！', '2025-07-27 11:16:13', '2025-07-27 11:17:05', 0);
INSERT INTO `questions` VALUES (69, '简答题1', 'TEXT', 0, 24, 'MEDIUM', 5, '简答题解析！', '2025-07-27 11:16:13', '2025-07-27 11:17:31', 0);
INSERT INTO `questions` VALUES (70, '选择题999', 'CHOICE', 1, 30, 'MEDIUM', 5, '89999', '2025-07-27 11:16:13', '2025-07-27 12:40:29', 1);
INSERT INTO `questions` VALUES (71, '选择题00000', 'CHOICE', 1, 18, 'EASY', 5, '测试', '2025-07-27 17:23:32', '2025-07-27 17:23:32', 0);
INSERT INTO `questions` VALUES (72, '选择题2222', 'CHOICE', 1, 19, 'MEDIUM', 5, '选择题解释解释解释', '2025-07-27 18:28:56', '2025-07-27 18:28:56', 0);
INSERT INTO `questions` VALUES (73, '以下哪个是Spring框架的核心特性？', 'TEXT', 0, 1, 'MEDIUM', 5, 'Spring框架的核心特性包括依赖注入、面向切面编程和事务管理等。', '2025-07-28 17:28:42', '2025-10-28 21:52:00', 0);
INSERT INTO `questions` VALUES (74, '判断题哈哈', 'JUDGE', 0, 16, 'MEDIUM', 10, '答案正确的！', '2025-07-28 17:28:42', '2025-07-28 17:28:42', 0);
INSERT INTO `questions` VALUES (75, 'Spring框架中的依赖注入(DI)可以通过哪些方式实现？', 'CHOICE', 1, 17, 'EASY', 5, 'Spring框架支持多种依赖注入方式，包括构造器注入、字段注入和Setter方法注入，这些都是常见的依赖注入方式。接口注入不是Spring框架中支持的依赖注入方式。', '2025-07-29 16:51:12', '2025-07-29 16:51:12', 0);
INSERT INTO `questions` VALUES (76, '以下哪些注解用于Spring框架中声明Bean？', 'CHOICE', 1, 17, 'EASY', 5, '在Spring框架中，@Required、@Autowired和@Service注解都可以用来声明Bean。@Required注解用于指示必须注入一个bean，否则会抛出异常；@Autowired注解用于自动注入依赖；@Service注解用于标注服务层组件。@Qualifier注解用于指定注入的具体Bean，但本身不声明Bean。', '2025-07-29 16:51:13', '2025-07-29 16:51:13', 0);
INSERT INTO `questions` VALUES (77, 'Spring框架中的AOP（面向切面编程）支持哪些类型的切点表达式？', 'CHOICE', 1, 17, 'EASY', 5, 'Spring框架中的AOP支持多种类型的切点表达式，包括execution表达式用于匹配方法执行，within表达式用于匹配指定类中的所有方法，this表达式用于匹配当前代理对象，以及bean表达式用于匹配特定名称的Bean。', '2025-07-29 16:51:13', '2025-07-29 16:51:13', 0);
INSERT INTO `questions` VALUES (78, 'Spring框架中的@Transactional注解默认支持哪些事务传播行为？', 'CHOICE', 1, 17, 'EASY', 5, 'Spring框架中的@Transactional注解默认支持REQUIRED、SUPPORTS和MANDATORY三种事务传播行为。REQUIRED表示如果当前存在事务，则加入该事务；SUPPORTS表示如果当前存在事务，可以加入事务，否则以非事务方式执行；MANDATORY表示当前必须存在事务，否则抛出异常。NEVER表示当前方法不允许在事务中执行，与默认行为不同。', '2025-07-29 16:51:13', '2025-07-29 16:51:13', 0);
INSERT INTO `questions` VALUES (79, '在Spring框架中，以下哪些注解可以用来声明组件？', 'CHOICE', 1, 17, 'EASY', 5, '在Spring框架中，@Component、@Service、@Repository和@Controller注解都可以用来声明组件。@Component注解用于泛指组件，@Service注解用于服务层组件，@Repository注解用于数据访问层组件，@Controller注解用于表现层组件。这些注解都可以使类成为Spring容器的Bean，从而被自动扫描和注册。', '2025-07-29 16:51:13', '2025-07-29 16:51:13', 0);
INSERT INTO `questions` VALUES (81, '刘和静1', 'CHOICE', 0, 33, 'MEDIUM', 5, '', '2025-10-28 19:45:15', '2025-10-28 22:55:39', 1);
INSERT INTO `questions` VALUES (82, '刘和静2', 'CHOICE', 0, 33, 'MEDIUM', 5, '1111', '2025-10-28 19:49:35', '2025-10-28 23:59:21', 1);
INSERT INTO `questions` VALUES (84, '刘和静3', 'CHOICE', 0, 33, 'MEDIUM', 5, '', '2025-10-28 19:57:03', '2025-10-28 19:57:03', 0);
INSERT INTO `questions` VALUES (85, '以下哪个是Spring框架的核心特性？', 'CHOICE', 0, 17, 'MEDIUM', 5, 'Spring框架的核心特性包括依赖注入、面向切面编程和事务管理等。', '2025-10-29 13:33:16', '2025-10-29 13:35:43', 0);
INSERT INTO `questions` VALUES (86, '在Spring框架中，以下哪个注解用于声明一个组件，使其成为Spring IoC容器的管理对象？', 'CHOICE', 0, 33, 'MEDIUM', 5, '@Service注解是用于声明一个服务组件，使其成为Spring IoC容器的管理对象。@Autowired注解用于自动注入依赖，@Aspect注解用于声明一个切面，@PathVariable注解用于获取URL中的参数值。因此，正确答案是@Service。', '2025-10-30 15:10:29', '2025-10-30 15:10:29', 0);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'user',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'active',
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', 'admin123', 'Admin', 'admin', 'active', '2025-06-23 22:00:18', '2025-06-23 22:00:18', 0);

-- ----------------------------
-- Table structure for video_categories
-- ----------------------------
DROP TABLE IF EXISTS `video_categories`;
CREATE TABLE `video_categories`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '分类描述',
  `parent_id` bigint(0) NULL DEFAULT 0 COMMENT '父级分类ID，0为顶级',
  `sort_order` int(0) NULL DEFAULT 0 COMMENT '排序权重',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `created_at` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id`) USING BTREE,
  INDEX `idx_sort_order`(`sort_order`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of video_categories
-- ----------------------------
INSERT INTO `video_categories` VALUES (1, 'Java基础', 'Java编程语言基础知识讲解，适合初学者', 0, 1, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (2, 'Java进阶', 'Java高级特性、框架应用和最佳实践', 0, 2, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (3, 'Spring框架', 'Spring生态系统相关技术栈详解', 0, 3, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (4, '数据库', '数据库设计、优化和管理相关知识', 0, 4, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (5, '前端技术', '前端开发技术栈和工具使用', 0, 5, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (6, '系统设计', '架构设计、系统分析和最佳实践', 0, 6, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (7, '面试指导', '技术面试准备和常见问题解答', 0, 7, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (8, '项目实战', '完整项目开发流程和实战经验分享', 0, 8, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (11, '语法基础', 'Java基础语法和概念', 1, 1, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (12, '面向对象', 'Java面向对象编程思想', 1, 2, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (13, '集合框架', 'Java集合类和数据结构', 1, 3, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (14, '异常处理', 'Java异常机制和处理方式', 1, 4, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (21, '多线程', 'Java并发编程和线程安全', 2, 1, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (22, 'JVM原理', 'Java虚拟机内存管理和性能调优', 2, 2, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (23, '设计模式', '常用设计模式在Java中的应用', 2, 3, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (24, '网络编程', 'Java网络编程和Socket通信', 2, 4, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (31, 'Spring Core', 'Spring核心容器和IoC/DI', 3, 1, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (32, 'Spring MVC', 'Spring Web MVC框架', 3, 2, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (33, 'Spring Boot', 'Spring Boot快速开发', 3, 3, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (34, 'Spring Cloud', 'Spring Cloud微服务架构', 3, 4, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (41, 'MySQL', 'MySQL数据库管理和优化', 4, 1, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (42, 'Redis', 'Redis缓存技术和应用', 4, 2, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (43, 'MongoDB', 'MongoDB NoSQL数据库', 4, 3, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (44, '数据库设计', '数据库建模和设计规范', 4, 4, 1, '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `video_categories` VALUES (45, '嵌入式', '嵌入式技术', 0, 8, 1, '2025-06-29 22:37:42', '2025-06-29 22:37:42');
INSERT INTO `video_categories` VALUES (46, '嵌入式测试1', 'ccc', 45, 0, 1, '2025-06-29 22:37:55', '2025-06-29 22:37:55');

-- ----------------------------
-- Table structure for video_likes
-- ----------------------------
DROP TABLE IF EXISTS `video_likes`;
CREATE TABLE `video_likes`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `video_id` bigint(0) NOT NULL COMMENT '视频ID',
  `user_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户IP（匿名点赞）',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户代理信息',
  `created_at` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_video_ip`(`video_id`, `user_ip`) USING BTREE,
  INDEX `idx_video_id`(`video_id`) USING BTREE,
  INDEX `idx_created_at`(`created_at`) USING BTREE,
  CONSTRAINT `video_likes_ibfk_1` FOREIGN KEY (`video_id`) REFERENCES `videos` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of video_likes
-- ----------------------------
INSERT INTO `video_likes` VALUES (1, 1, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '2025-06-29 19:40:04');
INSERT INTO `video_likes` VALUES (2, 1, '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', '2025-06-29 19:40:04');
INSERT INTO `video_likes` VALUES (3, 2, '192.168.1.102', 'Mozilla/5.0 (X11; Linux x86_64)', '2025-06-29 19:40:04');
INSERT INTO `video_likes` VALUES (4, 2, '192.168.1.103', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '2025-06-29 19:40:04');
INSERT INTO `video_likes` VALUES (5, 3, '192.168.1.104', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X)', '2025-06-29 19:40:04');
INSERT INTO `video_likes` VALUES (6, 6, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', '2025-06-29 23:00:50');
INSERT INTO `video_likes` VALUES (7, 9, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', '2025-07-09 18:03:15');

-- ----------------------------
-- Table structure for video_views
-- ----------------------------
DROP TABLE IF EXISTS `video_views`;
CREATE TABLE `video_views`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '观看ID',
  `video_id` bigint(0) NOT NULL COMMENT '视频ID',
  `user_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户IP',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户代理信息',
  `view_duration` int(0) NULL DEFAULT NULL COMMENT '观看时长（秒）',
  `created_at` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '观看时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_video_id`(`video_id`) USING BTREE,
  INDEX `idx_created_at`(`created_at`) USING BTREE,
  INDEX `idx_user_ip`(`user_ip`) USING BTREE,
  CONSTRAINT `video_views_ibfk_1` FOREIGN KEY (`video_id`) REFERENCES `videos` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频观看记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of video_views
-- ----------------------------
INSERT INTO `video_views` VALUES (1, 1, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 1650, '2025-06-29 19:40:04');
INSERT INTO `video_views` VALUES (2, 1, '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', 1800, '2025-06-29 19:40:04');
INSERT INTO `video_views` VALUES (3, 2, '192.168.1.102', 'Mozilla/5.0 (X11; Linux x86_64)', 2100, '2025-06-29 19:40:04');
INSERT INTO `video_views` VALUES (4, 2, '192.168.1.103', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 2400, '2025-06-29 19:40:04');
INSERT INTO `video_views` VALUES (5, 3, '192.168.1.104', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X)', 2800, '2025-06-29 19:40:04');
INSERT INTO `video_views` VALUES (6, 6, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', 0, '2025-06-29 23:00:41');
INSERT INTO `video_views` VALUES (7, 6, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', 3, '2025-06-29 23:00:44');
INSERT INTO `video_views` VALUES (8, 6, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', 3, '2025-06-29 23:00:45');
INSERT INTO `video_views` VALUES (10, 6, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', 9, '2025-06-29 23:00:49');
INSERT INTO `video_views` VALUES (11, 6, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', 9, '2025-06-29 23:02:16');
INSERT INTO `video_views` VALUES (12, 6, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', 21, '2025-06-29 23:02:22');
INSERT INTO `video_views` VALUES (14, 6, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', 0, '2025-06-29 23:10:53');
INSERT INTO `video_views` VALUES (15, 6, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', 6, '2025-06-29 23:10:59');
INSERT INTO `video_views` VALUES (17, 6, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', 18, '2025-06-29 23:11:10');
INSERT INTO `video_views` VALUES (19, 6, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', 127, '2025-06-29 23:12:54');
INSERT INTO `video_views` VALUES (20, 6, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', 0, '2025-06-29 23:32:07');
INSERT INTO `video_views` VALUES (21, 6, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', 4, '2025-06-29 23:32:12');
INSERT INTO `video_views` VALUES (22, 7, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', 0, '2025-06-29 23:39:46');
INSERT INTO `video_views` VALUES (23, 7, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', 2, '2025-06-29 23:39:48');
INSERT INTO `video_views` VALUES (24, 7, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', 0, '2025-07-03 18:58:50');
INSERT INTO `video_views` VALUES (25, 7, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', 2, '2025-07-03 18:58:53');
INSERT INTO `video_views` VALUES (27, 9, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', 0, '2025-07-09 18:03:04');
INSERT INTO `video_views` VALUES (28, 9, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', 2, '2025-07-09 18:03:07');
INSERT INTO `video_views` VALUES (29, 9, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', 23, '2025-07-09 18:03:29');
INSERT INTO `video_views` VALUES (30, 9, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', 0, '2025-07-16 14:45:11');
INSERT INTO `video_views` VALUES (31, 9, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', 10, '2025-07-16 14:45:22');
INSERT INTO `video_views` VALUES (32, 9, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36', 20, '2025-07-16 14:45:22');

-- ----------------------------
-- Table structure for videos
-- ----------------------------
DROP TABLE IF EXISTS `videos`;
CREATE TABLE `videos`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '视频ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '视频描述',
  `category_id` bigint(0) NOT NULL COMMENT '分类ID',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频文件URL',
  `cover_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图片URL',
  `duration` int(0) NULL DEFAULT NULL COMMENT '视频时长（秒）',
  `file_size` bigint(0) NULL DEFAULT NULL COMMENT '文件大小（字节）',
  `uploader_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '上传者名称',
  `uploader_type` tinyint(0) NULL DEFAULT 1 COMMENT '上传者类型：1-用户投稿，2-管理员上传',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '上传用户ID（用户投稿时）',
  `admin_id` bigint(0) NULL DEFAULT NULL COMMENT '管理员ID（管理员上传时）',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '状态：0-待审核，1-已发布，2-已拒绝，3-已下架',
  `audit_admin_id` bigint(0) NULL DEFAULT NULL COMMENT '审核管理员ID',
  `audit_time` timestamp(0) NULL DEFAULT NULL COMMENT '审核时间',
  `audit_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '审核原因（拒绝时）',
  `view_count` bigint(0) NULL DEFAULT 0 COMMENT '观看次数',
  `like_count` bigint(0) NULL DEFAULT 0 COMMENT '点赞次数',
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签，逗号分隔',
  `created_at` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_uploader_type`(`uploader_type`) USING BTREE,
  INDEX `idx_view_count`(`view_count`) USING BTREE,
  INDEX `idx_like_count`(`like_count`) USING BTREE,
  INDEX `idx_created_at`(`created_at`) USING BTREE,
  CONSTRAINT `videos_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `video_categories` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of videos
-- ----------------------------
INSERT INTO `videos` VALUES (1, 'Java基础语法入门', '从零开始学习Java编程语言，掌握基本语法和编程思想', 11, 'http://localhost:9000/videos/original/java-basic-syntax.mp4', 'http://localhost:9000/videos/covers/java-basic-syntax.jpg', 1800, 104857600, '技术讲师', 2, NULL, 1, 1, 1, '2025-06-29 19:40:04', '视频内容优质，适合初学者学习', 156, 23, 'Java,基础,语法,入门', '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `videos` VALUES (2, 'Spring Boot快速上手', '快速掌握Spring Boot核心特性和快速开发技巧', 33, 'http://localhost:9000/videos/original/springboot-quickstart.mp4', 'http://localhost:9000/videos/covers/springboot-quickstart.jpg', 2400, 156784532, '架构师', 2, NULL, 1, 1, 1, '2025-06-29 19:40:04', '内容详实，示例丰富', 234, 45, 'Spring Boot,快速开发,微服务', '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `videos` VALUES (3, 'MySQL性能优化实战', '深入讲解MySQL查询优化和性能调优的最佳实践', 41, 'http://localhost:9000/videos/original/mysql-performance.mp4', 'http://localhost:9000/videos/covers/mysql-performance.jpg', 3600, 287654321, 'DBA专家', 2, NULL, 1, 1, 1, '2025-06-29 19:40:04', '干货满满，实用性强', 89, 12, 'MySQL,性能优化,数据库,DBA', '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `videos` VALUES (4, 'Vue3前端开发指南', '全面介绍Vue3新特性和现代前端开发最佳实践', 5, 'http://localhost:9000/videos/original/vue3-guide.mp4', 'http://localhost:9000/videos/covers/vue3-guide.jpg', 2700, 198765432, '前端工程师', 2, NULL, 1, 1, 1, '2025-06-29 19:40:04', '内容新颖，跟上技术潮流', 178, 34, 'Vue3,前端,JavaScript,组件化', '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `videos` VALUES (5, '微服务架构设计', '深入解析微服务架构设计原理和实践经验', 6, 'http://localhost:9000/videos/original/microservice-design.mp4', 'http://localhost:9000/videos/covers/microservice-design.jpg', 4200, 345678901, '系统架构师', 2, NULL, 1, 1, 1, '2025-06-29 19:40:04', '架构设计精髓，值得反复学习', 67, 8, '微服务,架构设计,分布式,系统设计', '2025-06-29 19:40:04', '2025-06-29 19:40:04');
INSERT INTO `videos` VALUES (6, '客户端测试1', '大叔大婶打打卡时间看哈的撒', 11, 'http://47.94.86.115:9000/java1229/videos/original/2025/06/29/55d20a1e1cbd4f30b40ace7c6a1ea6e2.mp4?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250629%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250629T144913Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=70df9c57f84e1a6a35259bdd6cefafb63613aa9c84aef007bb48eed96a4ab523', 'http://47.94.86.115:9000/java1229/videos/covers/2025/06/29/8f9f3fa440e74695b1e68c318d50d57a.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250629%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250629T144913Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=a29c62df33e81c15c8a281cbee74ae8cceba301d7a6c6890d75ee6da24b622b6', 7, 2181933, '赵伟风', 1, NULL, NULL, 1, 1, '2025-06-29 22:49:42', NULL, 12, 1, '测试,测试1', '2025-06-29 22:49:13', '2025-06-29 23:32:11');
INSERT INTO `videos` VALUES (7, '好视频', '湿哒哒大大大阿德撒旦撒旦撒阿达撒打算打算', 12, 'http://47.94.86.115:9000/java1229/videos/original/2025/06/29/f43918e6c5f7447ebb50714ae52dbcc1.mp4?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250629%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250629T153917Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=85a8f056a051ef18a7e0e681a5494521743696b1204dfb9507083f2e12cf4f4e', 'http://47.94.86.115:9000/java1229/videos/covers/2025/06/29/70aaa472d0d747518ba135da3bcb1d24.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250629%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250629T153917Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=a1bb1243ff02990c75ac306357491860fc587f371ad9301b92089c29cf48adbd', 100, 2181933, '管理员', 2, NULL, 1, 1, 1, '2025-06-29 23:39:17', NULL, 4, 0, 'xxx,x,x,x,xx', '2025-06-29 23:39:17', '2025-07-03 18:58:52');
INSERT INTO `videos` VALUES (8, '测试视频', '手打达到休息休息休息休息', 22, 'http://47.94.86.115:9000/java1229/videos/original/2025/06/29/b88c8da5a28b4346946f590f23c3684a.mp4?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250629%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250629T154527Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=b69c792e34b8706ddd08f8baee810f7f8aab62406231eeb62dde29323182c030', 'http://47.94.86.115:9000/java1229/videos/covers/2025/06/29/4983e532cbde43c28bb8f69ee83644d0.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250629%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250629T154527Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=f0ba29ce86ff0f8df5e5b34dc3e3529e833050e45cbdeb0dab2edfaa88e97af9', 514, 44436864, '赵伟风', 2, NULL, 1, 1, 1, '2025-06-29 23:45:27', NULL, 0, 0, '测试,好好,大家好', '2025-06-29 23:45:27', '2025-06-29 23:45:27');
INSERT INTO `videos` VALUES (9, 'xxx', 'xxx', 12, 'http://47.94.86.115:9000/java1229/videos/original/2025/07/09/dbec754f1dd4499d9b5ced33226ba682.mp4?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250709%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250709T100222Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=1d65df5a592ea6d728f23058d3d7b5a2f09a8f1b2a0f99b9b5c18d8c8d17d319', 'http://47.94.86.115:9000/java1229/videos/covers/2025/07/09/e02edb9cef0241beb5bd08ef22b8f284.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20250709%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250709T100222Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=71be85ee4763fe795306274a88834be88f4a7741a37cde68dfed171f75baf8aa', 444, 35402445, 'zhaoweinfeg', 2, NULL, 1, 1, 1, '2025-07-09 18:02:23', NULL, 6, 1, 'che,he', '2025-07-09 18:02:23', '2025-07-16 14:45:22');

SET FOREIGN_KEY_CHECKS = 1;
