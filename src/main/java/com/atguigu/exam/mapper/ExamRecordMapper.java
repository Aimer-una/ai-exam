package com.atguigu.exam.mapper;


import com.atguigu.exam.entity.ExamRecord;
import com.atguigu.exam.vo.ExamRankingVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description 针对表【exam_record(考试记录表)】的数据库操作Mapper
 * @createDate 2025-06-20 22:37:43
 * @Entity com.atguigu.exam.entity.ExamRecord
 */
@Mapper
public interface ExamRecordMapper extends BaseMapper<ExamRecord> {

    List<ExamRankingVO> customQueryRanking(@Param("paperId") Integer paperId, @Param("limit") Integer limit);
} 