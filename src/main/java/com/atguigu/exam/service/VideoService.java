package com.atguigu.exam.service;

import com.atguigu.exam.entity.Video;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 视频Service接口
 * 提供视频管理的完整业务逻辑操作
 */
public interface VideoService {
    
    // ========== 用户端接口 ==========
    
    /**
     * 分页获取已发布的视频列表
     * @param page 页码
     * @param size 每页大小
     * @param categoryId 分类ID（可选）
     * @param keyword 搜索关键字（可选）
     * @param request HTTP请求（用于获取用户IP）
     * @return 视频分页结果
     */
    IPage<Video> getPublishedVideos(Integer page, Integer size, Long categoryId, String keyword, HttpServletRequest request);
    
    /**
     * 获取视频详情（含用户点赞状态）
     * @param id 视频ID
     * @param request HTTP请求（用于获取用户IP）
     * @return 视频详情
     */
    Video getVideoDetail(Long id, HttpServletRequest request);
    
    /**
     * 获取热门视频列表
     * @param limit 限制数量
     * @return 热门视频列表
     */
    List<Video> getPopularVideos(Integer limit);
    
    /**
     * 获取最新视频列表
     * @param limit 限制数量
     * @return 最新视频列表
     */
    List<Video> getLatestVideos(Integer limit);
    
    /**
     * 记录视频观看
     * @param videoId 视频ID
     * @param viewDuration 观看时长（秒）
     * @param request HTTP请求（用于获取用户IP）
     */
    void recordVideoView(Long videoId, Integer viewDuration, HttpServletRequest request);
    
    /**
     * 切换视频点赞状态
     * @param videoId 视频ID
     * @param request HTTP请求（用于获取用户IP）
     * @return 点赞状态：true-已点赞，false-已取消点赞
     */
    boolean toggleVideoLike(Long videoId, HttpServletRequest request);
    
    /**
     * 用户投稿视频
     * @param video 视频基本信息
     * @param videoFile 视频文件
     * @param coverFile 封面文件（可选）
     * @return 上传结果
     */
    Map<String, Object> submitVideo(Video video, MultipartFile videoFile, MultipartFile coverFile);
    
    // ========== 管理端接口 ==========
    
    /**
     * 管理端分页获取视频列表
     * @param page 页码
     * @param size 每页大小
     * @param status 状态筛选（可选）
     * @param uploaderType 上传者类型筛选（可选）
     * @param keyword 搜索关键字（可选）
     * @return 视频分页结果
     */
    IPage<Video> getVideosForAdmin(Integer page, Integer size, Integer status, Integer uploaderType, String keyword);
    
    /**
     * 管理员上传视频
     * @param video 视频基本信息
     * @param videoFile 视频文件
     * @param coverFile 封面文件（可选）
     * @param adminId 管理员ID
     * @return 上传结果
     */
    Map<String, Object> uploadVideoByAdmin(Video video, MultipartFile videoFile, MultipartFile coverFile, Long adminId);
    
    /**
     * 审核视频
     * @param videoId 视频ID
     * @param status 审核状态（1-通过，2-拒绝）
     * @param reason 审核原因（拒绝时必填）
     * @param adminId 审核管理员ID
     */
    void auditVideo(Long videoId, Integer status, String reason, Long adminId);
    
    /**
     * 下架视频
     * @param videoId 视频ID
     * @param adminId 管理员ID
     */
    void offlineVideo(Long videoId, Long adminId);
    
    /**
     * 删除视频
     * @param videoId 视频ID
     */
    void deleteVideo(Long videoId);
    
    /**
     * 获取视频统计数据
     * @return 统计数据
     */
    Map<String, Object> getVideoStatistics();
    
    /**
     * 获取视频详细统计数据（观看、点赞等）
     * @param videoId 视频ID
     * @param days 统计天数
     * @return 详细统计数据
     */
    Map<String, Object> getVideoDetailStats(Long videoId, Integer days);
} 