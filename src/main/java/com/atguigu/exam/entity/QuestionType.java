package com.atguigu.exam.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 题目类型的枚举
 */
public enum QuestionType {
    /**
     * 选择题
     */
    CHOICE,
    /**
     * 判断题
     */
    JUDGE,
    /**
     * 简答题
     */
    TEXT;

    /**
     * 自定义一个创建方法，用于Jackson反序列化
     * 这使得将字符串转换为枚举的过程更加健壮，可以忽略大小写
     * @param value 前端传来的字符串值
     * @return 对应的枚举常量，如果找不到则返回null或抛出异常
     */
    @JsonCreator
    public static QuestionType fromString(String value) {
        if (value == null) {
            return null;
        }
        for (QuestionType type : QuestionType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        // 如果严格要求必须匹配，可以抛出异常
        // throw new IllegalArgumentException("未知的题目类型: " + value);
        return null; // 或者返回null，让后续的校验逻辑处理
    }
} 