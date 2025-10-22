package com.atguigu.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 试卷实体类
 * 对应数据库exams表
 */
@Data
@TableName("exams")
public class Exam extends BaseEntity {
    
    private String name; // 试卷名称
    
    private String description; // 试卷描述
    
    private Integer duration; // 考试时长（分钟）
    
    @TableField("pass_score")
    private Integer passScore; // 及格分数
    
    @TableField("total_score")
    private Integer totalScore; // 总分
    
    @TableField("question_count")
    private Integer questionCount; // 题目数量
    
    private String status; // 试卷状态：DRAFT(草稿)、PUBLISHED(已发布)、CLOSED(已关闭)

} 