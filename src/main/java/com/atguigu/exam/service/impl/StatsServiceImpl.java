package com.atguigu.exam.service.impl;

import com.atguigu.exam.entity.ExamRecord;
import com.atguigu.exam.mapper.*;
import com.atguigu.exam.service.StatsService;
import com.atguigu.exam.vo.StatsVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 统计数据服务实现类
 */
@Slf4j
@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private QuestionMapper questionMapper;  // 题目Mapper
    
    @Autowired
    private UserMapper userMapper;  // 用户Mapper
    
    @Autowired
    private ExamRecordMapper examRecordMapper;  // 考试记录Mapper
    
    @Autowired
    private CategoryMapper categoryMapper;  // 分类Mapper
    
    @Autowired
    private PaperMapper paperMapper;  // 试卷Mapper

    @Override
    public StatsVo getSystemStats() {
        StatsVo stats = new StatsVo();
        
        try {
            // 统计题目总数  // 查询题目数量
            Long questionCount = questionMapper.selectCount(new QueryWrapper<>());
            stats.setQuestionCount(questionCount);
            log.info("题目总数: {}", questionCount);
        } catch (Exception e) {
            log.error("查询题目总数失败: {}", e.getMessage());
            stats.setQuestionCount(0L);
        }
        
        try {
            // 统计用户总数  // 查询用户数量
            Long userCount = userMapper.selectCount(new QueryWrapper<>());
            stats.setUserCount(userCount);
            log.info("用户总数: {}", userCount);
        } catch (Exception e) {
            log.error("查询用户总数失败: {}", e.getMessage());
            stats.setUserCount(0L);
        }
        
        try {
            // 统计考试总场次  // 查询考试记录总数
            Long examCount = examRecordMapper.selectCount(new QueryWrapper<>());
            stats.setExamCount(examCount);
            log.info("考试总场次: {}", examCount);
        } catch (Exception e) {
            log.error("查询考试总场次失败: {}", e.getMessage());
            stats.setExamCount(0L);
        }
        
        try {
            // 统计今日考试次数  // 查询今天的考试记录（使用create_time字段）
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();  // 今天00:00:00
            LocalDateTime endOfDay = today.atTime(LocalTime.MAX);  // 今天23:59:59
            
            QueryWrapper<ExamRecord> todayQueryWrapper = new QueryWrapper<>();
            todayQueryWrapper.between("create_time", startOfDay, endOfDay);  // 使用正确的字段名
            Long todayExamCount = examRecordMapper.selectCount(todayQueryWrapper);
            stats.setTodayExamCount(todayExamCount);
            log.info("今日考试次数: {}", todayExamCount);
        } catch (Exception e) {
            log.error("查询今日考试次数失败: {}", e.getMessage());
            stats.setTodayExamCount(0L);
        }
        
        try {
            // 统计分类总数  // 查询分类数量
            Long categoryCount = categoryMapper.selectCount(new QueryWrapper<>());
            stats.setCategoryCount(categoryCount);
            log.info("分类总数: {}", categoryCount);
        } catch (Exception e) {
            log.error("查询分类总数失败: {}", e.getMessage());
            stats.setCategoryCount(0L);
        }
        
        try {
            // 统计试卷总数  // 查询试卷数量
            Long paperCount = paperMapper.selectCount(new QueryWrapper<>());
            stats.setPaperCount(paperCount);
            log.info("试卷总数: {}", paperCount);
        } catch (Exception e) {
            log.error("查询试卷总数失败: {}", e.getMessage());
            stats.setPaperCount(0L);
        }
        
        log.info("系统统计数据获取完成: {}", stats);
        return stats;
    }
} 