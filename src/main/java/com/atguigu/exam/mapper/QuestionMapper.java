package com.atguigu.exam.mapper;


import com.atguigu.exam.entity.Question;
import com.atguigu.exam.vo.QuestionQueryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 题目Mapper接口
 * 继承MyBatis Plus的BaseMapper，提供基础的CRUD操作
 */
public interface QuestionMapper extends BaseMapper<Question> {
    @Select("select category_id,count(*) as count from questions where is_deleted = 0 group by category_id;")
    List<Map<String, Long>> getCategoryAndCount();


    /**
     * 分页查询题目信息，第一步！一会要触发，根据题目id查询选项！！
     * @param page 分页对象
     * @param questionQueryVo 自己实体类,封装的查询对象！
     * @return
     */
    IPage<Question> selectQuestionPage(IPage page,@Param("queryVo") QuestionQueryVo questionQueryVo);

    /**
     * 根据试卷id查询题目集合
     * @param paperId
     * @return
     */
    List<Question> customQueryQuestionListByPaperId(Integer paperId);


}