package com.atguigu.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 试卷-题目关联表
 * @TableName paper_question
 */
@TableName(value ="paper_question")
@Data
@NoArgsConstructor
public class PaperQuestion extends BaseEntity {

    /**
     * 试卷ID
     */
    private Integer paperId; // 试卷ID

    /**
     * 题目ID
     */
    private Long questionId; // 题目ID

    /**
     * 题目分数
     */
    private BigDecimal score; // 题目分数


    /**
     * 构造函数
     * @param paperId 试卷ID
     * @param questionId 题目ID
     * @param score 分数
     */
    public PaperQuestion(Integer paperId, Long questionId, BigDecimal score) {
        this.paperId = paperId;
        this.questionId = questionId;
        this.score = score;
    }
} 