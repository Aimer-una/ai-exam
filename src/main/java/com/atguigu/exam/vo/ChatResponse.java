package com.atguigu.exam.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * AI聊天响应Vo
 */
@Data
public class ChatResponse implements Serializable {
    /**
     * ID
     */
    private String id; // ID

    /**
     * 对象类型
     */
    private String object; // 对象类型

    /**
     * 创建时间
     */
    private long created; // 创建时间

    /**
     * 模型名称
     */
    private String model; // 模型名称

    /**
     * 选项列表
     */
    private List<ChatChoice> choices; // 选项列表

    /**
     * Token使用情况
     */
    private Usage usage; // Token使用情况

    private static final long serialVersionUID = 1L; // 序列化版本UID
} 