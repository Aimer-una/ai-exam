package com.atguigu.exam.controller;


import com.atguigu.exam.common.Result;
import com.atguigu.exam.service.StatsService;
import com.atguigu.exam.vo.StatsVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计控制器 - 处理系统数据统计相关的HTTP请求
 * 包括系统概览数据、各种统计信息等功能
 */
@RestController  // REST控制器，返回JSON数据
@RequestMapping("/api/stats")  // 统计API路径前缀
@CrossOrigin(origins = "*")  // 允许跨域访问
@Tag(name = "数据统计", description = "系统统计相关操作，包括概览数据、图表统计等功能")  // Swagger API分组
public class StatsController {

    /**
     * 注入统计业务服务
     */
    @Autowired
    private StatsService statsService;

    /**
     * 获取系统统计数据
     * 包括题目数量、用户数量、考试数量等
     * @return 系统统计信息
     */
    @GetMapping("/overview")  // 处理GET请求
    @Operation(summary = "获取系统概览统计", description = "获取系统的概览统计数据，包括题目、用户、考试等各项数量统计")  // API描述
    public Result<StatsVo> getSystemStats() {
        StatsVo stats = statsService.getSystemStats();
        return Result.success(stats);
    }

    /**
     * 测试数据库连接
     * @return 测试结果
     */
    @GetMapping("/test")  // 处理GET请求
    @Operation(summary = "测试数据库连接", description = "测试系统数据库连接状态，用于系统健康检查")  // API描述
    public Result<String> testDatabase() {
        try {
            // 简单测试数据库连接，通过获取统计数据来验证
            StatsVo stats = statsService.getSystemStats();
            return Result.success("数据库连接正常，统计数据：" + stats.toString());
        } catch (Exception e) {
            return Result.error("数据库连接测试异常：" + e.getMessage());
        }
    }
} 