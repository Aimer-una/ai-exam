package com.atguigu.exam.common;

/**
 * 缓存常量类
 * 定义系统中使用的缓存名称和key前缀
 */
public class CacheConstants {
    
    /**
     * 题目模块缓存名称
     */
    public static final String QUESTION_CACHE = "question";
    
    /**
     * 试卷模块缓存名称
     */
    public static final String PAPER_CACHE = "paper";
    
    /**
     * 考试记录模块缓存名称
     */
    public static final String EXAM_RECORD_CACHE = "exam_record";
    
    /**
     * 题目详情缓存key前缀
     */
    public static final String QUESTION_DETAIL_KEY = "question:detail:";
    
    /**
     * 分类题目列表缓存key前缀
     */
    public static final String QUESTION_CATEGORY_KEY = "question:category:";
    
    /**
     * 试卷详情缓存key前缀
     */
    public static final String PAPER_DETAIL_KEY = "paper:detail:";
    
    /**
     * 考试记录详情缓存key前缀
     */
    public static final String EXAM_RECORD_DETAIL_KEY = "exam_record:detail:";
    
    /**
     * 热门题目缓存key
     */
    public static final String POPULAR_QUESTIONS_KEY = "question:popular";
    
    /**
     * 题目访问计数key
     */
    public static final String QUESTION_VIEW_COUNT_KEY = "question:view_count";
    
    /**
     * 热门题目数量
     */
    public static final int POPULAR_QUESTIONS_COUNT = 10;
    
    /**
     * 缓存过期时间（秒）
     */
    public static final long DEFAULT_EXPIRE_SECONDS = 1800; // 30分钟
    
    /**
     * 热点数据缓存过期时间（秒）
     */
    public static final long HOT_DATA_EXPIRE_SECONDS = 3600; // 1小时
}