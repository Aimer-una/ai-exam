package com.atguigu.exam.mapper;

import com.atguigu.exam.entity.QuestionAnswer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface QuestionAnswerMapper extends BaseMapper<QuestionAnswer> {

    @Select("select * from question_answers where question_id = #{id} and is_deleted = 0")
    QuestionAnswer getQuestionAnswer(Long id);
}