package com.atguigu.exam.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统统计数据Vo - 用于首页数据展示
 */
@Data
@Schema(description = "系统统计数据")
public class StatsVo {
    
    @Schema(description = "题目总数", 
            example = "150")
    private Long questionCount;      // 题目总数
    
    @Schema(description = "用户总数", 
            example = "50")
    private Long userCount;          // 用户总数
    
    @Schema(description = "考试总场次", 
            example = "25")
    private Long examCount;          // 考试总场次
    
    @Schema(description = "今日考试次数", 
            example = "5")
    private Long todayExamCount;     // 今日考试次数
    
    @Schema(description = "分类总数", 
            example = "8")
    private Long categoryCount;      // 分类总数
    
    @Schema(description = "试卷总数", 
            example = "12")
    private Long paperCount;         // 试卷总数
    
    public StatsVo() {
        // 默认构造函数  // 初始化默认值
        this.questionCount = 0L;
        this.userCount = 0L;
        this.examCount = 0L;
        this.todayExamCount = 0L;
        this.categoryCount = 0L;
        this.paperCount = 0L;
    }
} 