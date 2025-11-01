package com.atguigu.exam.service;

import com.atguigu.exam.entity.ExamRecord;
import com.atguigu.exam.vo.StartExamVo;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;

/**
 * 考试记录Service接口
 * 定义考试记录相关的业务方法
 */
public interface ExamRecordService extends IService<ExamRecord> {

    ExamRecord startExam(StartExamVo startExamVo);
} 