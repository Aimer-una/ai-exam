package com.atguigu.exam.service;


import com.atguigu.exam.vo.AiGenerateRequestVo;

/**
 * Kimi AI服务接口
 * 用于调用Kimi API生成题目
 */
public interface KimiAiService {
    String buildPrompt(AiGenerateRequestVo request);

    String callKimiAi(String prompt) throws InterruptedException;

} 