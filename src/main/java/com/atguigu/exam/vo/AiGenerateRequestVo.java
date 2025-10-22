package com.atguigu.exam.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI生成题目请求Vo - 智能生成题目所需的参数
 */
@Data
@Schema(description = "AI智能生成题目请求参数")
public class AiGenerateRequestVo {
    
    @Schema(description = "生成题目的主题", 
            example = "Java面向对象编程", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "主题不能为空")
    private String topic; // 生成题目的主题，如"Spring框架"
    
    @Schema(description = "生成题目的数量，范围1-20", 
            example = "5", 
            minimum = "1", 
            maximum = "20",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1, message = "题目数量至少为1")
    @Max(value = 20, message = "题目数量最多为20")
    private Integer count; // 生成题目数量
    
    @Schema(description = "题目类型，多个用逗号分隔", 
            example = "CHOICE,JUDGE,TEXT", 
            allowableValues = {"CHOICE", "JUDGE", "TEXT", "CHOICE,JUDGE", "CHOICE,TEXT", "JUDGE,TEXT", "CHOICE,JUDGE,TEXT"})
    private String types; // 题目类型：如"CHOICE,JUDGE,TEXT" 多个用逗号分隔
    
    @Schema(description = "题目难度等级", 
            example = "MEDIUM", 
            allowableValues = {"EASY", "MEDIUM", "HARD"})
    private String difficulty; // 难度：EASY、MEDIUM、HARD
    
    @Schema(description = "题目分类ID，可以指定生成题目所属的分类", 
            example = "1")
    private Long categoryId; // 分类ID
    
    @Schema(description = "是否包含多选题，仅在题目类型包含CHOICE时有效", 
            example = "false")
    private Boolean includeMultiple; // 是否包含多选题
    
    @Schema(description = "额外的生成要求和说明", 
            example = "重点考察实际应用，包含代码示例")
    private String requirements; // 额外要求，如"重点考察实际应用"
} 