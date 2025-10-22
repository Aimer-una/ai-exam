package com.atguigu.exam.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * AI Token使用情况Vo
 */
@Data
public class Usage implements Serializable {
    /**
     * 提示词Token数
     */
    @JsonProperty("prompt_tokens")
    private int promptTokens; // 提示词Token数

    /**
     * 完成Token数
     */
    @JsonProperty("completion_tokens")
    private int completionTokens; // 完成Token数

    /**
     * 总Token数
     */
    @JsonProperty("total_tokens")
    private int totalTokens; // 总Token数

    private static final long serialVersionUID = 1L; // 序列化版本UID
} 