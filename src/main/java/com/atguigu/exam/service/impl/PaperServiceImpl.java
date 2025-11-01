package com.atguigu.exam.service.impl;


import com.atguigu.exam.entity.ExamRecord;
import com.atguigu.exam.entity.Paper;
import com.atguigu.exam.entity.PaperQuestion;
import com.atguigu.exam.entity.Question;
import com.atguigu.exam.mapper.ExamRecordMapper;
import com.atguigu.exam.mapper.PaperMapper;
import com.atguigu.exam.mapper.PaperQuestionMapper;
import com.atguigu.exam.mapper.QuestionMapper;
import com.atguigu.exam.service.PaperService;
import com.atguigu.exam.vo.AiPaperVo;
import com.atguigu.exam.vo.PaperVo;
import com.atguigu.exam.vo.RuleVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 试卷服务实现类
 */
@Slf4j
@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {

    @Autowired
    private PaperQuestionMapper paperQuestionMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private ExamRecordMapper examRecordMapper;

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
        // List<PaperQuestion> collect = paperVo.getQuestions().entrySet().stream().map(s -> new PaperQuestion(paper.getId().intValue(), Long.valueOf(s.getKey()), s.getValue())).collect(Collectors.toList());
        for (Map.Entry<Integer, BigDecimal> entry : questions.entrySet()) {
            PaperQuestion paperQuestion = new PaperQuestion(paper.getId().intValue(),Long.valueOf(entry.getKey()),entry.getValue());
            paperQuestionList.add(paperQuestion);
        }
        paperQuestionMapper.addPaperQuestionList(paperQuestionList);
        return paper;
    }

    @Override
    @Transactional
    public void createPaperWithAI(AiPaperVo aiPaperVo) {
        if (ObjectUtils.isEmpty(aiPaperVo)) {
            throw new RuntimeException("不能传入空的组卷信息");
        }
        Paper paper = new Paper();
        BeanUtils.copyProperties(aiPaperVo,paper);
        save(paper);
        // 定义题目总数
        int questionCount = 0;
        // 定义总分数
        BigDecimal questionSumCount = BigDecimal.ZERO;
        for (RuleVo rule : aiPaperVo.getRules()) {
            //步骤1：校验规则下的题目数量 = 0 跳过
            if (rule.getCount() == 0){
                log.warn("在：{}类型下，不需要出题！",rule.getType().name());
                continue;
            }
            LambdaQueryWrapper<Question> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Question::getType,rule.getType().name());
            lambdaQueryWrapper.in(!ObjectUtils.isEmpty(rule.getCategoryIds()),Question::getCategoryId,rule.getCategoryIds());
            List<Question> questionList = questionMapper.selectList(lambdaQueryWrapper);
            // 检查获取到的题目集合
            if (ObjectUtils.isEmpty(questionList)){
                // 如果为空则跳过本次
                log.warn("在：{}类型下，我们指定的分类：{},没有查询到题目信息！",rule.getType().name(),rule.getCategoryIds());
                continue;
            }
            //步骤4：判断下是否有规则下count数量！ 没有要全部了
            int realNumber = Math.min(rule.getCount(), questionList.size());

            //步骤5：本次规则下添加的数量和分数累加
            questionCount += realNumber;
            questionSumCount =questionSumCount.add(BigDecimal.valueOf((long) realNumber * rule.getScore()));

            //步骤6：先打乱数据，再截取需要题目数量
            Collections.shuffle(questionList);
            List<Question> realQuestionList = questionList.subList(0, realNumber);

            // 转成中间表保存
            List<PaperQuestion> paperQuestionList = realQuestionList.stream().map(question -> new PaperQuestion(paper.getId().intValue(), question.getId(), BigDecimal.valueOf(rule.getScore()))).collect(Collectors.toList());
            paperQuestionMapper.addPaperQuestionList(paperQuestionList);
        }
        //3. 修改试卷信息（总题数，总分数）
        paper.setQuestionCount(questionCount);
        paper.setTotalScore(questionSumCount);
        updateById(paper);

    }

    @Override
    @Transactional
    public void updatePaper(Integer id , PaperVo paperVo) {
        // 判断要修改的试卷是否为发布状态
        Paper paper = getById(id);
        if ("PUBLISHED".equals(paper.getStatus())){
            throw new RuntimeException("发布状态的试卷不允许修改");
        }
        // 判断要更新的试卷名字是否已经存在
        LambdaQueryWrapper<Paper> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Paper::getName,paperVo.getName());
        long count = count(lambdaQueryWrapper);
        if (count > 0){
            throw new RuntimeException("要更新的试卷名字已经存在，请换一个名字");
        }
        BeanUtils.copyProperties(paperVo,paper);
        paper.setQuestionCount(paperVo.getQuestions().size());
        paper.setTotalScore(paperVo.getQuestions().values().stream().reduce(BigDecimal.ZERO,BigDecimal::add));
        updateById(paper);
        // 删除题目试卷管理表中与id管理的所以信息
        LambdaQueryWrapper<PaperQuestion> paperQuestionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        paperQuestionLambdaQueryWrapper.eq(PaperQuestion::getPaperId,id);
        paperQuestionMapper.delete(paperQuestionLambdaQueryWrapper);

        // 添加所有更新的题目加入到题目试卷关联表中
        List<PaperQuestion> paperQuestionList = paperVo.getQuestions().entrySet().stream().map(entry -> new PaperQuestion(paper.getId().intValue(), Long.valueOf(entry.getKey()), entry.getValue())).collect(Collectors.toList());
        paperQuestionMapper.addPaperQuestionList(paperQuestionList);
    }

    @Override
    public void updatePaperStatus(Integer id, String status) {
        LambdaUpdateWrapper<Paper> paperLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        paperLambdaUpdateWrapper.eq(Paper::getId,id);
        paperLambdaUpdateWrapper.set(Paper::getStatus,status);
        update(paperLambdaUpdateWrapper);
    }

    @Override
    @Transactional
    public void deletePaper(Integer id) {
        // 发布状态不能删
        Paper paper = getById(id);
        if ("PUBLISHED".equals(paper.getStatus())){
            throw new RuntimeException("发布状态的试卷不能删除");
        }

        // 有关联考试记录的不能删除
        LambdaQueryWrapper<ExamRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ExamRecord::getExamId,id);
        Long count = examRecordMapper.selectCount(lambdaQueryWrapper);
        if (count > 0){
            throw new RuntimeException("当前试卷：%s 下面有关联 %s条考试记录！无法直接删除！".formatted(id,count));
        }
        // 删除试卷
        removeById(id);

        // 删除试卷题目关系表
        paperQuestionMapper.delete(new LambdaQueryWrapper<PaperQuestion>().eq(PaperQuestion::getPaperId,id));
    }

    @Override
    public Paper getPaperById(Integer id) {
        Paper paper = getById(id);
        if (paper == null){
            throw new RuntimeException("你查询的试卷已经被删除");
        }
        // 查询对应的题目信息
        List<Question> questionList = questionMapper.customQueryQuestionListByPaperId(id);
        if (ObjectUtils.isEmpty(questionList)){
            paper.setQuestions(new ArrayList<Question>());
            log.warn("试卷中没有题目！可以进行试卷编辑！但是不能用于考试！！,对应试卷id：{}",id);
            return paper;
        }

        // 对题目分类进行排序（选择 判断 简答）
        questionList.sort(((o1, o2) -> Integer.compare(typeToInt(o1.getType()),typeToInt(o2.getType()))));
        paper.setQuestions(questionList);
        return paper;
    }
    /**
     * 获取题目类型的排序顺序
     * @param type 题目类型
     * @return 排序序号
     */
    private int typeToInt(String type) {
        switch (type){
            case "CHOICE": return 1;
            case "JUDGE" : return 2;
            case "TEXT" : return 3;
            default:return 4;
        }
    }
}