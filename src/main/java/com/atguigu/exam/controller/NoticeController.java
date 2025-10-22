package com.atguigu.exam.controller;

import com.atguigu.exam.common.Result;
import com.atguigu.exam.entity.Notice;
import com.atguigu.exam.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告控制器 - 处理系统公告管理相关的HTTP请求
 * 包括公告的增删改查、状态管理、前台展示等功能
 */
@RestController  // REST控制器，返回JSON数据
@RequestMapping("/api/notices")  // 公告API路径前缀
@CrossOrigin  // 允许跨域访问
@Tag(name = "公告管理", description = "系统公告相关操作，包括公告发布、编辑、删除、状态管理等功能")  // Swagger API分组
public class NoticeController {
    
    /**
     * 注入公告业务服务
     */
    @Autowired
    private NoticeService noticeService;
    
    /**
     * 获取启用的公告（前台首页使用）
     * @return 公告列表
     */
    @GetMapping("/active")  // 处理GET请求
    @Operation(summary = "获取启用的公告", description = "获取状态为启用的公告列表，供前台首页展示使用")  // API描述
    public Result<List<Notice>> getActiveNotices() {
        return noticeService.getActiveNotices();
    }
    
    /**
     * 获取最新的几条公告（前台首页使用）
     * @param limit 限制数量，默认5条
     * @return 公告列表
     */
    @GetMapping("/latest")  // 处理GET请求
    @Operation(summary = "获取最新公告", description = "获取最新发布的公告列表，用于首页推荐展示")  // API描述
    public Result<List<Notice>> getLatestNotices(
            @Parameter(description = "限制数量", example = "5") @RequestParam(defaultValue = "5") int limit) {
        return noticeService.getLatestNotices(limit);
    }
    
    /**
     * 获取所有公告（管理后台使用）
     * @return 公告列表
     */
    @GetMapping("/list")  // 处理GET请求
    @Operation(summary = "获取所有公告", description = "获取所有公告列表，包括启用和禁用的，供管理后台使用")  // API描述
    public Result<List<Notice>> getAllNotices() {
        return noticeService.getAllNotices();
    }
    
    /**
     * 根据ID获取公告
     * @param id 公告ID
     * @return 公告详情
     */
    @GetMapping("/{id}")  // 处理GET请求
    @Operation(summary = "根据ID获取公告", description = "根据公告ID获取单个公告的详细信息")  // API描述
    public Result<Notice> getNoticeById(
            @Parameter(description = "公告ID") @PathVariable Long id) {
        Notice notice = noticeService.getById(id);
        if (notice != null) {
            return Result.success(notice);
        } else {
            return Result.error("公告不存在");
        }
    }
    
    /**
     * 添加公告
     * @param notice 公告对象
     * @return 操作结果
     */
    @PostMapping("/add")  // 处理POST请求
    @Operation(summary = "发布新公告", description = "创建并发布新的系统公告")  // API描述
    public Result<String> addNotice(@RequestBody Notice notice) {
        return noticeService.addNotice(notice);
    }
    
    /**
     * 更新公告
     * @param notice 公告对象
     * @return 操作结果
     */
    @PutMapping("/update")  // 处理PUT请求
    @Operation(summary = "更新公告信息", description = "修改公告的内容、标题、类型等信息")  // API描述
    public Result<String> updateNotice(@RequestBody Notice notice) {
        return noticeService.updateNotice(notice);
    }
    
    /**
     * 删除公告
     * @param id 公告ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{id}")  // 处理DELETE请求
    @Operation(summary = "删除公告", description = "根据ID删除指定的公告")  // API描述
    public Result<String> deleteNotice(
            @Parameter(description = "公告ID") @PathVariable Long id) {
        return noticeService.deleteNotice(id);
    }
    
    /**
     * 启用/禁用公告
     * @param id 公告ID
     * @param isActive 是否启用
     * @return 操作结果
     */
    @PutMapping("/toggle/{id}")  // 处理PUT请求
    @Operation(summary = "切换公告状态", description = "启用或禁用指定的公告，禁用后不会在前台显示")  // API描述
    public Result<String> toggleNoticeStatus(
            @Parameter(description = "公告ID") @PathVariable Long id, 
            @Parameter(description = "是否启用，true为启用，false为禁用") @RequestParam Boolean isActive) {
        return noticeService.toggleNoticeStatus(id, isActive);
    }
} 