package com.atguigu.exam.service.impl;


import com.atguigu.exam.entity.Paper;
import com.atguigu.exam.entity.PaperQuestion;
import com.atguigu.exam.mapper.PaperMapper;
import com.atguigu.exam.mapper.PaperQuestionMapper;
import com.atguigu.exam.service.PaperService;
import com.atguigu.exam.vo.PaperVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * 试卷服务实现类
 */
@Slf4j
@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {

    @Autowired
    private PaperQuestionMapper paperQuestionMapper;

    @Override
    public List<Paper> getPaperList(String name, String status) {
        LambdaQueryWrapper<Paper> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(!ObjectUtils.isEmpty(name),Paper::getName,name);
        lambdaQueryWrapper.eq(!ObjectUtils.isEmpty(status),Paper::getStatus,status);
        return list(lambdaQueryWrapper);
    }

    @Override
    @Transactional
    public Paper createPaper(PaperVo paperVo) {
        // 判断传进来的对象是否为空
        if (ObjectUtils.isEmpty(paperVo)){
            throw new RuntimeException("传入的试卷为空");
        }
        Paper paper = new Paper();
        BeanUtils.copyProperties(paperVo,paper);
        paper.setStatus("DRAFT");
        // 判断是否有传入题目
        if (ObjectUtils.isEmpty(paperVo.getQuestions())){
            log.info("传入的题目为空，该试卷只能编辑");
            paper.setTotalScore(BigDecimal.ZERO);
            paper.setQuestionCount(0);
        }
        // 总题目数量
        paper.setQuestionCount(paperVo.getQuestions().size());
        // 获取题目分数总和
        Optional<BigDecimal> totalScore = paperVo.getQuestions().values().stream().reduce(BigDecimal::add);
        // paperVo.getQuestions().values().stream().mapToInt(b ->b.intValue()).sum();
        paper.setTotalScore(totalScore.get());
        save(paper);

        // 保存试卷题目练习表
        List<PaperQuestion> paperQuestionList = new ArrayList<>();
        Map<Integer, BigDecimal> questions = paperVo.getQuestions();
        for (Map.Entry<Integer, BigDecimal> entry : questions.entrySet()) {
            PaperQuestion paperQuestion = new PaperQuestion(paper.getId().intValue(),Long.valueOf(entry.getKey()),entry.getValue());
            paperQuestionList.add(paperQuestion);
        }
        paperQuestionMapper.addPaperQuestionList(paperQuestionList);
        return paper;
    }
}