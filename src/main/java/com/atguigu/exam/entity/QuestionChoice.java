package com.atguigu.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 题目选项实体类 - 选择题的选项信息
 */
@Data
@TableName("question_choices")
@Schema(description = "题目选项信息")
public class QuestionChoice extends BaseEntity{

    
    @Schema(description = "关联的题目ID", 
            example = "1")
    private Long questionId;  // 题目ID
    
    @Schema(description = "选项内容", 
            example = "Java是面向对象编程语言")
    private String content;  // 选项内容
    
    @Schema(description = "是否为正确答案", 
            example = "true")
    private Boolean isCorrect;  // 是否正确答案
    
    @Schema(description = "选项排序序号", 
            example = "1")
    private Integer sort;  // 排序
} 