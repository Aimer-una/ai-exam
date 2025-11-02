package com.atguigu.exam.service;

import com.atguigu.exam.entity.ExamRecord;
import com.atguigu.exam.vo.ExamRankingVO;
import com.atguigu.exam.vo.StartExamVo;
import com.atguigu.exam.vo.SubmitAnswerVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;

/**
 * 考试记录Service接口
 * 定义考试记录相关的业务方法
 */
public interface ExamRecordService extends IService<ExamRecord> {

    ExamRecord startExam(StartExamVo startExamVo);

    ExamRecord getExamRecordById(Integer id);

    void submitAnswers(Integer examRecordId, List<SubmitAnswerVo> answers) throws InterruptedException;

    public ExamRecord gradeExam(Integer examRecordId) throws InterruptedException;

    void getExamRecordsPage(Page<ExamRecord> myPage, Integer status, String startDate, String studentName, String studentNumber, String endDate);

    void customRemoveById(Integer id);

    List<ExamRankingVO> customGetRanking(Integer paperId, Integer limit);
} 