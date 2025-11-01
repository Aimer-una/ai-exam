package com.atguigu.exam.controller;


import com.atguigu.exam.common.Result;
import com.atguigu.exam.entity.ExamRecord;
import com.atguigu.exam.service.ExamRecordService;
import com.atguigu.exam.vo.StartExamVo;
import com.atguigu.exam.vo.SubmitAnswerVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考试控制器 - 处理考试流程相关的HTTP请求
 * 包括开始考试、提交答案、AI批阅、成绩查询等功能
 */
@RestController  // REST控制器，返回JSON数据
@RequestMapping("/api/exams")  // 考试API路径前缀
@CrossOrigin(origins = "*")  // 允许跨域访问
@Tag(name = "考试管理", description = "考试流程相关操作，包括开始考试、答题提交、AI批阅、成绩查询等功能")  // Swagger API分组
public class ExamController {

    @Autowired
    private ExamRecordService examRecordService;
    /**
     * 开始考试 - 创建新的考试记录
     * @param startExamVo 开始考试请求DTO
     * @return 考试记录
     */
    @PostMapping("/start")  // 处理POST请求
    @Operation(summary = "开始考试", description = "学生开始考试，创建考试记录并返回试卷内容")  // API描述
    public Result<ExamRecord> startExam(@RequestBody StartExamVo startExamVo) {
        // TODO: 从SecurityContext获取当前登录用户ID  // 暂时使用固定用户ID
        ExamRecord examRecord = examRecordService.startExam(startExamVo);
        return Result.success(examRecord, "考试开始成功");
    }

    /**
     * 提交答案 - 学生提交考试答案
     * @param examRecordId 考试记录ID
     * @param answers      答案列表
     */
    @PostMapping("/{examRecordId}/submit")  // 处理POST请求
    @Operation(summary = "提交考试答案", description = "学生提交考试答案，系统记录答题情况")  // API描述
    public Result<Void> submitAnswers(
            @Parameter(description = "考试记录ID") @PathVariable Integer examRecordId, 
            @RequestBody List<SubmitAnswerVo> answers) {
        return Result.success("答案提交成功");
    }

    /**
     * AI自动批阅 - 触发试卷智能批阅
     * @param examRecordId 考试记录ID
     */
    @PostMapping("/{examRecordId}/grade")  // 处理POST请求
    @Operation(summary = "AI自动批阅", description = "使用AI技术自动批阅试卷，特别是简答题的智能评分")  // API描述
    public Result<ExamRecord> gradeExam(
            @Parameter(description = "考试记录ID") @PathVariable Integer examRecordId) {

        return Result.success(null, "试卷批阅完成");
    }

    /**
     * 根据ID获取考试记录详情 - 查询具体考试结果
     */
    @GetMapping("/{id}")  // 处理GET请求
    @Operation(summary = "查询考试记录详情", description = "获取指定考试记录的详细信息，包括答题情况和得分")  // API描述
    public Result<ExamRecord> getExamRecordById(
            @Parameter(description = "考试记录ID") @PathVariable Integer id) {
        ExamRecord examRecord = examRecordService.getExamRecordById(id);
        return Result.success(examRecord);
    }

    /**
     * 获取考试记录列表 - 查询所有考试记录
     */
    @GetMapping("/records")  // 处理GET请求
    @Operation(summary = "获取考试记录列表", description = "获取所有考试记录列表，包含基本信息和成绩")  // API描述
    public Result<List<ExamRecord>> getMyRecords() {
        return Result.success(null);
    }
} 