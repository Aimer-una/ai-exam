package com.atguigu.exam.service.impl;


import com.atguigu.exam.entity.Paper;
import com.atguigu.exam.mapper.PaperMapper;
import com.atguigu.exam.service.PaperService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 试卷服务实现类
 */
@Slf4j
@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {


    @Override
    public List<Paper> getPaperList(String name, String status) {
        LambdaQueryWrapper<Paper> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(!ObjectUtils.isEmpty(name),Paper::getName,name);
        lambdaQueryWrapper.eq(!ObjectUtils.isEmpty(status),Paper::getStatus,status);
        return list(lambdaQueryWrapper);
    }
}