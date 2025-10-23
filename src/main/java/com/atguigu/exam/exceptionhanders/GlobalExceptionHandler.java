package com.atguigu.exam.exceptionhanders;

import com.atguigu.exam.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result exception(Exception e) {
        e.printStackTrace();
        // 记录日常日志
        log.error("服务器发生运行时异常！异常信息为：{}",e.getMessage());
        // 返回对应的提示
        return Result.error(e.getMessage());
    }

}
