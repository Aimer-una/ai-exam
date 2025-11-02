package com.atguigu.exam.service;

import com.atguigu.exam.entity.ExamRecord;
import com.atguigu.exam.vo.StartExamVo;
import com.atguigu.exam.vo.SubmitAnswerVo;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;

/**
 * 考试记录Service接口
 * 定义考试记录相关的业务方法
 */
public interface ExamRecordService extends IService<ExamRecord> {

    ExamRecord startExam(StartExamVo startExamVo);

    ExamRecord getExamRecordById(Integer id);

    void submitAnswers(Integer examRecordId, List<SubmitAnswerVo> answers);

    public ExamRecord gradeExam(Integer examRecordId);
} 