package com.atguigu.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

/**
 * 题目实体类 - 考试题目信息模型
 * 
 * 这是系统的核心实体类之一，用于存储各种类型的考试题目
 * 
 * MyBatis Plus高级特性教学：
 * 1. @TableField(exist = false)：标记非数据库字段，用于关联查询结果
 * 2. FieldFill.INSERT/INSERT_UPDATE：自动填充创建和更新时间
 * 3. 实体类关联：通过@TableField(exist = false)实现多表关联
 * 
 * 业务设计：
 * - 支持多种题型：选择题(CHOICE)、判断题(JUDGE)、简答题(TEXT)
 * - 支持难度分级：简单(EASY)、中等(MEDIUM)、困难(HARD)
 * - 支持分类管理：通过categoryId关联分类表
 * 
 * @author 智能学习平台开发团队
 * @version 1.0
 */
@Data  // Lombok注解：自动生成所有getter、setter方法
@TableName("questions")  // 对应数据库表：questions
@Schema(description = "题目信息")
public class Question extends BaseEntity {
    
    @Schema(description = "题目标题内容", 
            example = "以下关于Java面向对象编程的说法正确的是？")
    private String title;
    
    @Schema(description = "题目类型", 
            example = "CHOICE", 
            allowableValues = {"CHOICE", "JUDGE", "TEXT"})
    private String type;
    
    @Schema(description = "是否为多选题，仅选择题有效", 
            example = "false")
    private Boolean multi;
    
    @Schema(description = "题目分类ID", 
            example = "1")
    private Long categoryId;
    
    @Schema(description = "题目难度等级", 
            example = "MEDIUM", 
            allowableValues = {"EASY", "MEDIUM", "HARD"})
    private String difficulty;
    
    @Schema(description = "题目默认分值", 
            example = "5")
    private Integer score;
    
    @Schema(description = "在特定试卷中的分值", 
            example = "10.0")
    @TableField(exist = false)  // 标记为非数据库字段
    private BigDecimal paperScore;
    
    @Schema(description = "题目解析，详细的答案说明", 
            example = "Java是面向对象编程语言，支持封装、继承、多态三大特性...")
    private String analysis;

    //多表
    @Schema(description = "选择题选项列表，包含A、B、C、D等选项")
    @TableField(exist = false)
    private List<QuestionChoice> choices;

    //多表
    @Schema(description = "题目答案信息，包含正确答案和评分标准")
    @TableField(exist = false)
    private QuestionAnswer answer;

    //多表
    @Schema(description = "题目所属分类信息")
    @TableField(exist = false)
    private Category category;
} 