package com.atguigu.exam.service.impl;

import com.atguigu.exam.entity.AnswerRecord;
import com.atguigu.exam.entity.ExamRecord;
import com.atguigu.exam.entity.Paper;
import com.atguigu.exam.entity.Question;
import com.atguigu.exam.mapper.AnswerRecordMapper;
import com.atguigu.exam.mapper.ExamRecordMapper;
import com.atguigu.exam.mapper.PaperMapper;
import com.atguigu.exam.service.ExamRecordService;
import com.atguigu.exam.service.PaperService;
import com.atguigu.exam.vo.StartExamVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 考试记录Service实现类
 * 实现考试记录相关的业务逻辑
 */
@Service
public class ExamRecordServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamRecordService {

    @Autowired
    private PaperService paperService;
    @Autowired
    private AnswerRecordMapper answerRecordMapper;

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

    @Override
    public ExamRecord getExamRecordById(Integer id) {
        ExamRecord examRecord = getById(id);
        if (examRecord == null){
            throw new RuntimeException("您查看的考试记录已被删除");
        }

        Paper paper = paperService.getPaperById(examRecord.getExamId());
        if (paper == null){
            throw new RuntimeException("当前考试记录的试卷被删除！获取考试记录详情失败！");
        }

        // 获取考试记录对应的答题记录集合
        LambdaQueryWrapper<AnswerRecord> answerRecordLambdaQueryWrapper = new LambdaQueryWrapper<>();
        answerRecordLambdaQueryWrapper.eq(AnswerRecord::getExamRecordId,id);
        List<AnswerRecord> answerRecords = answerRecordMapper.selectList(answerRecordLambdaQueryWrapper);
        if (!ObjectUtils.isEmpty(answerRecords)){
            List<Long> questionIds = paper.getQuestions().stream().map(Question::getId).collect(Collectors.toList());
            answerRecords.sort((o1, o2) -> {
                int x = questionIds.indexOf(o1.getQuestionId());
                int y = questionIds.indexOf(o2.getQuestionId());
                return Integer.compare(x,y);
            });
        }
        // 组装数据
        examRecord.setPaper(paper);
        examRecord.setAnswerRecords(answerRecords);
        return examRecord;
    }
}