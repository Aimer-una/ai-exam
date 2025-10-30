package com.atguigu.exam.mapper;

import com.atguigu.exam.entity.PaperQuestion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @description 针对表【paper_question(试卷-题目关联表)】的数据库操作Mapper
* @createDate 2025-06-20 22:37:43
* @Entity com.exam.entity.PaperQuestion
*/
@Mapper
public interface PaperQuestionMapper extends BaseMapper<PaperQuestion> {

    void addPaperQuestionList(List<PaperQuestion> paperQuestionList);
}