package com.atguigu.exam.vo;

import com.atguigu.exam.entity.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 智能组卷规则的数据传输对象
 */
@Data
@Schema(description = "AI组卷规则配置")
public class RuleVo {

    @Schema(description = "题目类型", 
            example = "CHOICE", 
            allowableValues = {"CHOICE", "JUDGE", "TEXT"})
    private QuestionType type;

    @Schema(description = "指定的题目分类ID列表，为空则不限制分类", 
            example = "[1, 2, 3]")
    private List<Integer> categoryIds;

    @Schema(description = "需要抽取的题目数量", 
            example = "10", 
            minimum = "1")
    private Integer count;

    @Schema(description = "每道题目的分数", 
            example = "5", 
            minimum = "1")
    private Integer score;
} 