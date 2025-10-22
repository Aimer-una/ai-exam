package com.atguigu.exam.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * AI智能组卷的请求数据传输对象
 */
@Data
@Schema(description = "AI智能组卷请求参数")
public class AiPaperVo {

    @Schema(description = "试卷名称", 
            example = "Java高级编程考试", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "试卷描述", 
            example = "本试卷由AI根据组卷规则自动生成，涵盖多个知识点")
    private String description;

    @Schema(description = "考试时长（分钟）", 
            example = "90", 
            minimum = "1", 
            maximum = "600")
    private Integer duration;

    @Schema(description = "AI组卷规则列表，定义不同题型的数量、分值等要求", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RuleVo> rules;
} 