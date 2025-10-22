package com.atguigu.exam.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户答案提交Vo - 学生提交考试答案时使用
 */
@Data
@Schema(description = "考试答案提交数据")
public class SubmitAnswerVo implements Serializable {

    @Schema(description = "题目ID，指定回答的是哪道题", 
            example = "1", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "题目ID不能为空")
    private Integer questionId; // 题目ID

    @Schema(description = "用户提交的答案", 
            example = "A", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String userAnswer; // 用户答案

    private static final long serialVersionUID = 1L; // 序列化版本UID
} 