package com.atguigu.exam.service.impl;

import com.atguigu.exam.common.CacheConstants;
import com.atguigu.exam.entity.Question;
import com.atguigu.exam.entity.QuestionAnswer;
import com.atguigu.exam.entity.QuestionChoice;
import com.atguigu.exam.mapper.PaperQuestionMapper;
import com.atguigu.exam.mapper.QuestionAnswerMapper;
import com.atguigu.exam.mapper.QuestionChoiceMapper;
import com.atguigu.exam.mapper.QuestionMapper;
import com.atguigu.exam.service.QuestionService;
import com.atguigu.exam.utils.RedisUtils;
import com.atguigu.exam.vo.QuestionQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private RedisUtils redisUtils;
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

    @Override
    public Question getByQuestion(Long id) {
        // 根据id获取题目
        Question question = getById(id);
        // 判断题目是否存在
        if (ObjectUtils.isEmpty(question)){
            throw new RuntimeException("题目不存在");
        }
        // 判断该题目是否是选择题
        if ("CHOICE".equals(question.getType())){
            // 根据id获取题目的选项
            List<QuestionChoice> questionChoices = questionChoiceMapper.selectListByQuestionId(id);
            question.setChoices(questionChoices);
        }
        // 根据id获取正确答案
        QuestionAnswer questionAnswer = questionAnswerMapper.getQuestionAnswer(id);
        question.setAnswer(questionAnswer);

        // 进行热点题目缓存
        new Thread(()->{
            incrementQuestion(id);
        }).start();
        return question;
    }

    @Override
    @Transactional
    public void addQuestion(Question question) {
        // 同一个类型不能题目title相同
        LambdaQueryWrapper<Question> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Question::getType,question.getType());
        lambdaQueryWrapper.eq(Question::getTitle,question.getTitle());
        Question questionExist = getOne(lambdaQueryWrapper);
        if (questionExist != null){
            //同一类型，title相同
            throw new RuntimeException("在%s下，存在%s 名称的题目已经存在！保存失败！".formatted(question.getType(),question.getTitle()));
        }

        // 新增题目插入到题目表并获取id
        save(question);
        Long questionId = question.getId();

        StringBuilder sb = new StringBuilder();
        QuestionAnswer questionAnswer = new QuestionAnswer();
        questionAnswer.setQuestionId(questionId);
        // 判断题目是否是选择题
        if ("CHOICE".equals(question.getType())){
            // 将题目的选项添加到选项表中
            List<QuestionChoice> choices = question.getChoices();
            for (int i = 0; i < choices.size(); i++) {
                QuestionChoice choice = choices.get(i);
                choice.setSort(i);
                choice.setQuestionId(questionId);
                // true 本次是正确答案
                if (choice.getIsCorrect()){
                   if (sb.length() > 0){
                       sb.append(",");
                   }
                   sb.append((char)('A'+i));
                }
            }
            // 设置题目的正确答案
            questionAnswer.setAnswer(sb.toString());
            questionChoiceMapper.insertBatch(choices);

            // 插入答案表
            questionAnswerMapper.insert(questionAnswer);
        }

    }

    // 定义进行题目访问次数增长的方法
    // 异步方法
    private void incrementQuestion(Long questionId){
        Double score = redisUtils.zIncrementScore(CacheConstants.POPULAR_QUESTIONS_KEY, questionId, 1);
        log.info("完成{}题目分数累计，累计后分数为：{}",questionId,score);
    }
}