package com.atguigu.exam.vo;

import lombok.Data;

import java.util.List;

/**
 * 分页结果封装类
 */
@Data
public class PageResult<T> {
    /** 数据列表 */
    private List<T> records;
    /** 总记录数 */
    private long total;
    /** 当前页码 */
    private long current;
    /** 每页大小 */
    private long size;
    /** 总页数 */
    private long pages;
    
    /**
     * 构造方法
     */
    public PageResult() {}
    
    /**
     * 构造方法
     * @param records 数据列表
     * @param total 总记录数
     * @param current 当前页码
     * @param size 每页大小
     */
    public PageResult(List<T> records, long total, long current, long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = size > 0 ? (total + size - 1) / size : 0;  // 计算总页数
    }
} 