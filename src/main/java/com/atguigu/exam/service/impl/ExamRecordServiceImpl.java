package com.atguigu.exam.service.impl;

import com.atguigu.exam.common.GradingResult;
import com.atguigu.exam.entity.AnswerRecord;
import com.atguigu.exam.entity.ExamRecord;
import com.atguigu.exam.entity.Paper;
import com.atguigu.exam.entity.Question;
import com.atguigu.exam.mapper.AnswerRecordMapper;
import com.atguigu.exam.mapper.ExamRecordMapper;
import com.atguigu.exam.mapper.PaperMapper;
import com.atguigu.exam.service.AnswerRecordService;
import com.atguigu.exam.service.ExamRecordService;
import com.atguigu.exam.service.KimiAiService;
import com.atguigu.exam.service.PaperService;
import com.atguigu.exam.vo.ExamRankingVO;
import com.atguigu.exam.vo.StartExamVo;
import com.atguigu.exam.vo.SubmitAnswerVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 考试记录Service实现类
 * 实现考试记录相关的业务逻辑
 */
@Service
@Slf4j
public class ExamRecordServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamRecordService {

    @Autowired
    private PaperService paperService;
    @Autowired
    private AnswerRecordMapper answerRecordMapper;
    @Autowired
    private AnswerRecordService answerRecordService;
    @Autowired
    private KimiAiService kimiAiService;
    @Autowired
    private ExamRecordMapper examRecordMapper;

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

    @Override
    public void submitAnswers(Integer examRecordId, List<SubmitAnswerVo> answers) throws InterruptedException {
        // 判断是否有传入答题信息
        if (ObjectUtils.isEmpty(answers)){
            throw new RuntimeException("你没有填写答题信息不能提交");
        }
        // 获取answerRecord集合
        List<AnswerRecord> answerRecordList = answers.stream().map(s -> new AnswerRecord(examRecordId, s.getQuestionId(), s.getUserAnswer())).collect(Collectors.toList());
        answerRecordService.saveBatch(answerRecordList);

        //2. 暂时修改下考试记录状态（状态 -》 已完成 || 结束时间 - 设置）
        ExamRecord examRecord = getById(examRecordId);
        examRecord.setEndTime(LocalDateTime.now());
        examRecord.setStatus("已完成");
        updateById(examRecord);
        gradeExam(examRecordId);
    }

    @Override
    public ExamRecord gradeExam(Integer examRecordId) throws InterruptedException {
        //宏观：  获取考试记录相关的信息（考试记录对象 考试记录答题记录 考试对应试卷）
        //  进行循环判断（1.答题记录进行修改 2.总体提到总分数 总正确数量）  修改考试记录（状态 -》 已批阅  修改 -》 总分数）   进行ai评语生成（总正确的题目数量）
        //  修改考试记录表  返回考试记录对象
        //1.获取考试记录和相关的信息（试卷和答题记录）
        ExamRecord examRecord = getExamRecordById(examRecordId);
        Paper paper = examRecord.getPaper();
        if (paper == null) {
            examRecord.setStatus("已批阅");
            examRecord.setAnswers("考试对应的试卷被删除！无法进行成绩判定！");
            updateById(examRecord);
            throw new RuntimeException("考试对应的试卷被删除！无法进行成绩判定！");
        }
        List<AnswerRecord> answerRecords = examRecord.getAnswerRecords();
        if (ObjectUtils.isEmpty(answerRecords)){
            //没有提交
            examRecord.setStatus("已批阅");
            examRecord.setScore(0);
            examRecord.setAnswers("没有提交记录！成绩为零！继续加油！");
            updateById(examRecord);
            return examRecord;
        }
        //2.进行循环的判卷（1.记录总分数 2.记录正确题目数量 3. 修改每个答题记录的状态（得分，是否正确 0 1 2 ，text-》ai评语））
        int correctNumber = 0 ; //正确题目数量
        int totalScore = 0; //总得分
        //报错继续！ 某个记录错了，后续还需要继续判卷
        //将正确题目转成map,方便每次判断获取正确答案
        Map<Long, Question> questionMap = paper.getQuestions().stream().collect(Collectors.toMap(Question::getId, s -> s));
        for (AnswerRecord answerRecord : answerRecords) {
            try{
                //1.先获取 答题记录对应的题目对象
                Question question = questionMap.get(answerRecord.getQuestionId().longValue());
                String systemAnswer = question.getAnswer().getAnswer();
                String userAnswer = answerRecord.getUserAnswer();
                if ("JUDGE".equalsIgnoreCase(question.getType())){
                    userAnswer = normalizeJudgeAnswer(userAnswer);
                }
                if (!"TEXT".equals(question.getType())){
                    //2.判断题目类型(选择和判断直接判卷)
                    if (systemAnswer.equalsIgnoreCase(userAnswer)){
                        answerRecord.setIsCorrect(1); //正确
                        answerRecord.setScore(question.getPaperScore().intValue());
                    }else {
                        answerRecord.setIsCorrect(0); //错误
                        answerRecord.setScore(0);
                    }
                }else {
                    //3.简答题进行ai判断
                    //简答题
                    GradingResult result =
                            kimiAiService.gradingTextQuestion(question,userAnswer,question.getPaperScore().intValue());
                    //分
                    answerRecord.setScore(result.getScore());
                    //ai评价 正确  feedback  非正确 reason
                    //是否正确 （满分 1 0分 0 其余就是2）
                    if (result.getScore() == 0){
                        answerRecord.setIsCorrect(0);
                        answerRecord.setAiCorrection(result.getReason());
                    }else if (result.getScore() == question.getPaperScore().intValue()){
                        answerRecord.setIsCorrect(1);
                        answerRecord.setAiCorrection(result.getFeedback());
                    }else{
                        answerRecord.setIsCorrect(2);
                        answerRecord.setAiCorrection(result.getReason());
                    }
                }
            }catch (Exception e){
                answerRecord.setScore(0);
                answerRecord.setIsCorrect(0);
                answerRecord.setAiCorrection("判题过程出错！");
            }
            //进行记录修改
            //进行总分数赋值
            totalScore += answerRecord.getScore();
            //正确题目数量累加
            if (answerRecord.getIsCorrect() == 1){
                correctNumber++;
            }
        }
        // 修改学生答题记录
        answerRecordService.updateBatchById(answerRecords);
        // 调用kimi进行ai评价
        String summary = kimiAiService.
                buildSummary(totalScore, paper.getTotalScore().intValue(), paper.getQuestionCount(), correctNumber);
        examRecord.setScore(totalScore);
        examRecord.setAnswers(summary);
        examRecord.setStatus("已批阅");
        updateById(examRecord);
        return examRecord;
    }

    @Override
    public void getExamRecordsPage(Page<ExamRecord> myPage, Integer status, String startDate, String studentName, String studentNumber, String endDate) {
        LambdaQueryWrapper<ExamRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(!ObjectUtils.isEmpty(studentName),ExamRecord::getStudentName,studentName);
        if (!ObjectUtils.isEmpty(status)){
            String strStatus = switch (status){
                case 0 -> "进行中";
                case 1 -> "已完成";
                case 2 -> "已批阅";
                default -> null;
            };
            lambdaQueryWrapper.eq(!ObjectUtils.isEmpty(strStatus),ExamRecord::getStatus,strStatus);
        }
        lambdaQueryWrapper.ge(!ObjectUtils.isEmpty(startDate),ExamRecord::getStartTime,startDate);
        lambdaQueryWrapper.le(!ObjectUtils.isEmpty(endDate),ExamRecord::getEndTime,endDate);
        page(myPage,lambdaQueryWrapper);
        // 查看是否有查到数据
        if (ObjectUtils.isEmpty(myPage.getRecords())){
            log.info("考试记录为空没必要查询对应试卷信息");
            return;
        }
        List<Integer> paperIds = myPage.getRecords().stream().map(ExamRecord::getExamId).collect(Collectors.toList());
        List<Paper> paperList = paperService.listByIds(paperIds);
        Map<Long, Paper> paperMap = paperList.stream().collect(Collectors.toMap(Paper::getId, s -> s));
        for (ExamRecord record : myPage.getRecords()) {
            record.setPaper(paperMap.get(record.getExamId().longValue()));
        }
    }

    @Override
    public void customRemoveById(Integer id) {
        //重要的关联数据校验，有删除失败！
        //判断自身状态，进行中不能删除
        ExamRecord examRecord = getById(id);
        if ("进行中".equals(examRecord.getStatus())){
            throw new RuntimeException("正在考试中，无法直接删除！");
        }
        //删除自身数据，同时删除答题记录
        removeById(id);
        answerRecordService.remove(new LambdaQueryWrapper<AnswerRecord>().eq(AnswerRecord::getExamRecordId,id));
    }

    /**
     * 获取考试排行榜 - 优化版本
     * 使用SQL关联查询，一次性获取所有需要的数据，避免N+1查询问题
     * @param paperId 试卷ID，可选参数，不传则查询所有试卷
     * @param limit 显示数量限制，可选参数，不传则返回所有记录
     * @return 排行榜列表
     */
    @Override
    public List<ExamRankingVO> customGetRanking(Integer paperId, Integer limit) {
        return examRecordMapper.customQueryRanking(paperId,limit);
    }

    /**
     * 标准化判断题答案，将T/F转换为TRUE/FALSE
     * @param answer 原始答案
     * @return 标准化后的答案
     */
    private String normalizeJudgeAnswer(String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            return "";
        }

        String normalized = answer.trim().toUpperCase();
        switch (normalized) {
            case "T":
            case "TRUE":
            case "正确":
                return "TRUE";
            case "F":
            case "FALSE":
            case "错":
                return "FALSE";
            default:
                return normalized;
        }
    }
}