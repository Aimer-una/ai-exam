package com.atguigu.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 试卷表 - 存储考试试卷的基本信息
 */
@TableName(value ="paper")
@Data
@Schema(description = "试卷信息")
public class Paper extends BaseEntity {

    @Schema(description = "试卷名称", 
            example = "Java基础知识测试")
    private String name; // 试卷名称

    @Schema(description = "试卷描述", 
            example = "本试卷主要考察Java基础语法、面向对象编程等知识点")
    private String description; // 试卷描述

    @Schema(description = "试卷状态", 
            example = "PUBLISHED", 
            allowableValues = {"DRAFT", "PUBLISHED", "STOPPED"})
    private String status;

    @Schema(description = "试卷总分", 
            example = "100.0")
    private BigDecimal totalScore; // 总分

    @Schema(description = "题目数量", 
            example = "20")
    private Integer questionCount; // 题目数量

    @Schema(description = "考试时长，单位：分钟", 
            example = "120")
    private Integer duration; // 考试时长

    @Schema(description = "试卷包含的题目列表，含题目详细信息和分值")
    @TableField(exist = false)
    private List<Question> questions; // 题目列表，非数据库字段

} 