package com.atguigu.exam.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * AI聊天消息Vo
 */
@Data
@NoArgsConstructor
public class ChatMessage implements Serializable {
    /**
     * 角色 (system, user, assistant)
     */
    private String role; // 角色

    /**
     * 消息内容
     */
    private String content; // 消息内容

    private static final long serialVersionUID = 1L; // 序列化版本UID

    /**
     * 构造函数
     * @param role 角色
     * @param content 内容
     */
    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
} 