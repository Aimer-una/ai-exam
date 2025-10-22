package com.atguigu.exam.service.impl;

import com.atguigu.exam.entity.ExamRecord;
import com.atguigu.exam.mapper.ExamRecordMapper;
import com.atguigu.exam.service.ExamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * 考试服务实现类
 */
@Service
@Slf4j
public class ExamServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamService {

} 