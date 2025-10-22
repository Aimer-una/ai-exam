package com.atguigu.exam.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * AI聊天响应选项Vo
 */
@Data
public class ChatChoice implements Serializable {
    /**
     * 索引
     */
    private int index; // 索引

    /**
     * 消息
     */
    private ResponseMessage message; // 消息

    /**
     * 结束原因
     */
    @JsonProperty("finish_reason")
    private String finishReason; // 结束原因

    private static final long serialVersionUID = 1L; // 序列化版本UID
} 