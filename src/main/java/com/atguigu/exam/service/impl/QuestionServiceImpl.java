package com.atguigu.exam.service.impl;

import com.atguigu.exam.entity.Question;
import com.atguigu.exam.entity.QuestionAnswer;
import com.atguigu.exam.entity.QuestionChoice;
import com.atguigu.exam.mapper.PaperQuestionMapper;
import com.atguigu.exam.mapper.QuestionAnswerMapper;
import com.atguigu.exam.mapper.QuestionChoiceMapper;
import com.atguigu.exam.mapper.QuestionMapper;
import com.atguigu.exam.service.QuestionService;
import com.atguigu.exam.vo.QuestionQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 题目Service实现类
 * 实现题目相关的业务逻辑
 */
@Slf4j
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Autowired
    private PaperQuestionMapper paperQuestionMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionAnswerMapper questionAnswerMapper;
    @Autowired
    private QuestionChoiceMapper questionChoiceMapper;
    @Override
    public void selectQuestionPage(Page<Question> questionPage, QuestionQueryVo questionQueryVo) {
        questionMapper.selectQuestionPage(questionPage,questionQueryVo);
    }

    @Override
    public void selectQuestionPageStream(Page<Question> questionPage, QuestionQueryVo questionQueryVo) {
        // 先实现问题表的分页查询
        LambdaQueryWrapper<Question> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(!ObjectUtils.isEmpty(questionQueryVo.getType()),Question::getType,questionQueryVo.getType());
        lambdaQueryWrapper.eq(!ObjectUtils.isEmpty(questionQueryVo.getCategoryId()),Question::getCategoryId,questionQueryVo.getCategoryId());
        lambdaQueryWrapper.eq(!ObjectUtils.isEmpty(questionQueryVo.getDifficulty()),Question::getDifficulty,questionQueryVo.getDifficulty());
        lambdaQueryWrapper.like(!ObjectUtils.isEmpty(questionQueryVo.getKeyword()),Question::getTitle,questionQueryVo.getKeyword());
        // 时间的倒序排序！！
        lambdaQueryWrapper.orderByDesc(Question::getCreateTime);
        page(questionPage,lambdaQueryWrapper);

        List<Question> questionList = questionPage.getRecords();
        // 判断是否查询到数据，如果没有直接返回
        if (!ObjectUtils.isEmpty(questionList)){
            return;
        }

        // 获取所有题目id
        List<Long> questionIds = questionList.stream().map(Question::getId).collect(Collectors.toList());

        // 获取正确答案数据
        LambdaQueryWrapper<QuestionAnswer> questionAnswerWrapper = new LambdaQueryWrapper<>();
        questionAnswerWrapper.in(QuestionAnswer::getQuestionId,questionIds);
        List<QuestionAnswer> questionAnswers = questionAnswerMapper.selectList(questionAnswerWrapper);

        // 获取题目对应选项列表
        LambdaQueryWrapper<QuestionChoice> choiceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        choiceLambdaQueryWrapper.in(QuestionChoice::getQuestionId,questionIds);
        List<QuestionChoice> questionChoices = questionChoiceMapper.selectList(choiceLambdaQueryWrapper);

        // 答案和选项进行map转化
        Map<Long, QuestionAnswer> answerMap = questionAnswers.stream().collect(Collectors.toMap(QuestionAnswer::getQuestionId, a -> a));
        Map<Long, List<QuestionChoice>> questionChoicesMap = questionChoices.stream().collect(Collectors.groupingBy(QuestionChoice::getQuestionId));

        // 遍历questionList并对其赋值
        for (Question question : questionList) {
            // 每个题目一定答案
            question.setAnswer(answerMap.get(question.getId()));
            // 选择题才有选项
            if ("CHOICE".equals(question.getType())){
                List<QuestionChoice> choiceList = questionChoicesMap.get(question.getId());
                if (!CollectionUtils.isEmpty(choiceList)){
                    choiceList.sort(Comparator.comparing(QuestionChoice::getSort));
                    question.setChoices(choiceList);
                }
            }
        }
    }
}