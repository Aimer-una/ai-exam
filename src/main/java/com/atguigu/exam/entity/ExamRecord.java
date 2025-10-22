package com.atguigu.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 考试记录表 - 存储学生的考试过程和结果数据
 */
@TableName(value ="exam_records")
@Data
@Schema(description = "考试记录信息")
public class ExamRecord extends BaseEntity {

    @Schema(description = "试卷ID，关联的考试试卷", 
            example = "1")
    private Integer examId; // 试卷ID

    @Schema(description = "考生姓名", 
            example = "张三")
    private String studentName; // 考生姓名

    @Schema(description = "考试得分", 
            example = "85")
    private Integer score; // 得分

    @Schema(description = "答题记录，JSON格式存储所有答题内容", 
            example = "[{\"questionId\":1,\"userAnswer\":\"A\"},{\"questionId\":2,\"userAnswer\":\"B\"}]")
    private String answers; // 答题记录

    @Schema(description = "考试开始时间", 
            example = "2024-01-15 09:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime; // 开始时间

    @Schema(description = "考试结束时间", 
            example = "2024-01-15 11:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime; // 结束时间

    @Schema(description = "考试状态", 
            example = "已批阅", 
            allowableValues = {"进行中", "已完成", "已批阅"})
    private String status; // 考试状态

    @Schema(description = "窗口切换次数，用于监控考试过程中的异常行为", 
            example = "2")
    private Integer windowSwitches; // 窗口切换次数


    @Schema(description = "详细的答题记录列表，包含每题的答案和得分情况")
    @TableField(exist = false)
    private List<AnswerRecord> answerRecords; // 答案记录列表

    @Schema(description = "关联的试卷信息，包含试卷详细内容和题目")
    @TableField(exist = false)
    private Paper paper; // 试卷信息

} 