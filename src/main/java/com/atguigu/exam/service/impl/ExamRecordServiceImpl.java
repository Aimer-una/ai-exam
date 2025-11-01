package com.atguigu.exam.service.impl;

import com.atguigu.exam.entity.ExamRecord;
import com.atguigu.exam.mapper.ExamRecordMapper;
import com.atguigu.exam.service.ExamRecordService;
import com.atguigu.exam.vo.StartExamVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 考试记录Service实现类
 * 实现考试记录相关的业务逻辑
 */
@Service
public class ExamRecordServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamRecordService {


    @Override
    public ExamRecord startExam(StartExamVo startExamVo) {
        LambdaQueryWrapper<ExamRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ExamRecord::getExamId,startExamVo.getPaperId());
        lambdaQueryWrapper.eq(ExamRecord::getStudentName,startExamVo.getStudentName());;
        lambdaQueryWrapper.eq(ExamRecord::getStatus,"进行中");
        ExamRecord examRecord = getOne(lambdaQueryWrapper);
        // 如果该场考试已经存在那么直接返回让用户考试
        if (examRecord != null){
            return examRecord;
        }
        // 创建新的考试记录
        examRecord = new ExamRecord();
        examRecord.setExamId(startExamVo.getPaperId());
        examRecord.setStudentName(startExamVo.getStudentName());
        examRecord.setStatus("进行中");
        examRecord.setWindowSwitches(0);
        examRecord.setStartTime(LocalDateTime.now());

        save(examRecord);
        return examRecord;
    }
}