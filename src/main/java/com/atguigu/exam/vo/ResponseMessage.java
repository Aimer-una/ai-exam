package com.atguigu.exam.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * AI聊天响应消息Vo
 */
@Data
public class ResponseMessage implements Serializable {
    /**
     * 角色
     */
    private String role; // 角色

    /**
     * 内容
     */
    private String content; // 内容

    private static final long serialVersionUID = 1L; // 序列化版本UID
} 