package com.atguigu.exam.service;

import com.atguigu.exam.entity.Paper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 试卷服务接口
 */
public interface PaperService extends IService<Paper> {

    List<Paper> getPaperList(String name, String status);
} 