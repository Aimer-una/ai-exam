package com.atguigu.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 题目答案实体类 - 题目的标准答案信息
 */
@Data
@TableName("question_answers")
@Schema(description = "题目答案信息")
public class QuestionAnswer  extends BaseEntity{

    @Schema(description = "关联的题目ID", 
            example = "1")
    private Long questionId;  // 题目ID
    
    @Schema(description = "标准答案内容", 
            example = "正确")
    private String answer;  // 答案内容
    
    @Schema(description = "评分关键词，用于简答题AI评分", 
            example = "面向对象,封装,继承,多态")
    private String keywords;  // 关键词（用于简答题评分）
} 