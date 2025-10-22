package com.atguigu.exam.controller;

import com.atguigu.exam.common.Result;
import com.atguigu.exam.entity.Video;
import com.atguigu.exam.service.VideoService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 用户端视频控制器
 * 处理用户端视频浏览、观看、点赞、投稿等功能
 */
@RestController
@RequestMapping("/api/videos")
@Tag(name = "视频管理(用户端)", description = "用户端视频相关操作，包括视频浏览、观看、点赞、投稿等功能")
public class VideoController {

    @Autowired
    private VideoService videoService;

    /**
     * 分页获取已发布的视频列表
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @param categoryId 分类ID（可选）
     * @param keyword 搜索关键字（可选）
     * @param request HTTP请求对象
     * @return 视频分页数据
     */
    @GetMapping
    @Operation(summary = "获取视频列表", description = "分页获取已发布的视频列表，支持按分类和关键字筛选")
    public Result<IPage<Video>> getVideos(
            @Parameter(description = "页码，默认1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小，默认10") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        IPage<Video> result = videoService.getPublishedVideos(page, size, categoryId, keyword, request);
        return Result.success(result);
    }

    /**
     * 获取视频详情
     * @param id 视频ID
     * @param request HTTP请求对象
     * @return 视频详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取视频详情", description = "根据视频ID获取详细信息，包含用户点赞状态")
    public Result<Video> getVideoDetail(
            @Parameter(description = "视频ID") @PathVariable Long id,
            HttpServletRequest request) {
        Video video = videoService.getVideoDetail(id, request);
        return Result.success(video);
    }

    /**
     * 获取热门视频列表
     * @param limit 限制数量，默认10
     * @return 热门视频列表
     */
    @GetMapping("/popular")
    @Operation(summary = "获取热门视频", description = "根据观看次数获取热门视频列表")
    public Result<List<Video>> getPopularVideos(
            @Parameter(description = "限制数量，默认10") @RequestParam(defaultValue = "10") Integer limit) {
        List<Video> videos = videoService.getPopularVideos(limit);
        return Result.success(videos);
    }

    /**
     * 获取最新视频列表
     * @param limit 限制数量，默认10
     * @return 最新视频列表
     */
    @GetMapping("/latest")
    @Operation(summary = "获取最新视频", description = "根据发布时间获取最新视频列表")
    public Result<List<Video>> getLatestVideos(
            @Parameter(description = "限制数量，默认10") @RequestParam(defaultValue = "10") Integer limit) {
        List<Video> videos = videoService.getLatestVideos(limit);
        return Result.success(videos);
    }

    /**
     * 记录视频观看
     * @param videoId 视频ID
     * @param viewDuration 观看时长（秒）
     * @param request HTTP请求对象
     * @return 操作结果
     */
    @PostMapping("/{videoId}/view")
    @Operation(summary = "记录视频观看", description = "记录用户观看视频的行为和时长")
    public Result<Void> recordVideoView(
            @Parameter(description = "视频ID") @PathVariable Long videoId,
            @Parameter(description = "观看时长（秒）") @RequestParam Integer viewDuration,
            HttpServletRequest request) {
        videoService.recordVideoView(videoId, viewDuration, request);
        return Result.success(null);
    }

    /**
     * 切换视频点赞状态
     * @param videoId 视频ID
     * @param request HTTP请求对象
     * @return 点赞状态
     */
    @PostMapping("/{videoId}/like")
    @Operation(summary = "切换点赞状态", description = "切换用户对视频的点赞状态，返回当前状态")
    public Result<Map<String, Object>> toggleVideoLike(
            @Parameter(description = "视频ID") @PathVariable Long videoId,
            HttpServletRequest request) {
        boolean isLiked = videoService.toggleVideoLike(videoId, request);
        return Result.success(Map.of(
            "isLiked", isLiked,
            "message", isLiked ? "点赞成功" : "取消点赞成功"
        ));
    }

    /**
     * 用户投稿视频
     * @param title 视频标题
     * @param description 视频描述
     * @param categoryId 分类ID
     * @param tags 标签
     * @param uploaderName 上传者名称
     * @param duration 视频时长（秒）
     * @param videoFile 视频文件
     * @param coverFile 封面文件（可选）
     * @return 投稿结果
     */
    @PostMapping("/submit")
    @Operation(summary = "用户投稿视频", description = "用户上传视频进行投稿，需要等待审核")
    public Result<Map<String, Object>> submitVideo(
            @Parameter(description = "视频标题") @RequestParam String title,
            @Parameter(description = "视频描述") @RequestParam(required = false) String description,
            @Parameter(description = "分类ID") @RequestParam Long categoryId,
            @Parameter(description = "标签") @RequestParam(required = false) String tags,
            @Parameter(description = "上传者名称") @RequestParam String uploaderName,
            @Parameter(description = "视频时长（秒）") @RequestParam Integer duration,
            @Parameter(description = "视频文件") @RequestParam MultipartFile videoFile,
            @Parameter(description = "封面文件") @RequestParam(required = false) MultipartFile coverFile) {
        
        // 构建视频对象
        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setCategoryId(categoryId);
        video.setTags(tags);
        video.setUploaderName(uploaderName);
        video.setDuration(duration);
        
        Map<String, Object> result = videoService.submitVideo(video, videoFile, coverFile);
        return Result.success(result);
    }
} 