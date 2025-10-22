package com.atguigu.exam.service.impl;

import com.atguigu.exam.entity.Video;
import com.atguigu.exam.entity.VideoCategory;
import com.atguigu.exam.entity.VideoLike;
import com.atguigu.exam.entity.VideoView;
import com.atguigu.exam.mapper.VideoCategoryMapper;
import com.atguigu.exam.mapper.VideoLikeMapper;
import com.atguigu.exam.mapper.VideoMapper;
import com.atguigu.exam.mapper.VideoViewMapper;
import com.atguigu.exam.service.FileUploadService;
import com.atguigu.exam.service.VideoService;
import com.atguigu.exam.utils.IpUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频Service实现类
 * 提供视频管理的完整业务逻辑实现
 */
@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private VideoLikeMapper videoLikeMapper;

    @Autowired
    private VideoViewMapper videoViewMapper;

    @Autowired
    private VideoCategoryMapper videoCategoryMapper;

    @Autowired
    private FileUploadService fileUploadService;

    @Override
    public IPage<Video> getPublishedVideos(Integer page, Integer size, Long categoryId, String keyword, HttpServletRequest request) {
        Page<Video> pageObj = new Page<>(page, size);
        IPage<Video> result = videoMapper.getPublishedVideosPage(pageObj, categoryId, keyword);
        
        // 如果有IP，填充点赞状态
        if (request != null) {
            String userIp = IpUtils.getClientIp(request);
            result.getRecords().forEach(video -> {
                boolean isLiked = videoLikeMapper.isLikedByIp(video.getId(), userIp);
                video.setIsLiked(isLiked);
                // 格式化时长和文件大小
                formatVideoInfo(video);
            });
        }
        
        return result;
    }

    @Override
    public Video getVideoDetail(Long id, HttpServletRequest request) {
        Video video = videoMapper.selectById(id);
        if (video == null) {
            throw new RuntimeException("视频不存在");
        }
        
        // 只有已发布的视频才能查看详情
        if (video.getStatus() != Video.STATUS_PUBLISHED) {
            throw new RuntimeException("视频未发布或已下架");
        }
        
        // 获取分类名称并赋值
        if (video.getCategoryId() != null) {
            VideoCategory category = videoCategoryMapper.selectById(video.getCategoryId()); // 查询分类信息
            if (category != null) {
                video.setCategoryName(category.getName()); // 设置分类名称
            } else {
                video.setCategoryName("未分类"); // 分类不存在时显示未分类
            }
        } else {
            video.setCategoryName("未分类"); // 没有分类ID时显示未分类
        }
        
        // 如果有IP，填充点赞状态
        if (request != null) {
            String userIp = IpUtils.getClientIp(request);
            boolean isLiked = videoLikeMapper.isLikedByIp(id, userIp);
            video.setIsLiked(isLiked);
        }
        
        // 格式化信息
        formatVideoInfo(video);
        
        return video;
    }

    @Override
    public List<Video> getPopularVideos(Integer limit) {
        List<Video> videos = videoMapper.getPopularVideos(limit);
        videos.forEach(this::formatVideoInfo);
        return videos;
    }

    @Override
    public List<Video> getLatestVideos(Integer limit) {
        List<Video> videos = videoMapper.getLatestVideos(limit);
        videos.forEach(this::formatVideoInfo);
        return videos;
    }

    @Override
    @Transactional
    public void recordVideoView(Long videoId, Integer viewDuration, HttpServletRequest request) {
        if (request == null) return;
        
        String userIp = IpUtils.getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        
        // 创建观看记录
        VideoView videoView = new VideoView();
        videoView.setVideoId(videoId);
        videoView.setUserIp(userIp);
        videoView.setUserAgent(userAgent);
        videoView.setViewDuration(viewDuration);
        videoView.setCreatedAt(LocalDateTime.now());
        
        videoViewMapper.insert(videoView);
        
        // 增加视频观看次数
        videoMapper.incrementViewCount(videoId);
    }

    @Override
    @Transactional
    public boolean toggleVideoLike(Long videoId, HttpServletRequest request) {
        if (request == null) {
            throw new RuntimeException("请求信息为空");
        }
        
        String userIp = IpUtils.getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        
        // 检查是否已点赞
        boolean isLiked = videoLikeMapper.isLikedByIp(videoId, userIp);
        
        if (isLiked) {
            // 取消点赞
            videoLikeMapper.delete(
                new LambdaQueryWrapper<VideoLike>()
                    .eq(VideoLike::getVideoId, videoId)
                    .eq(VideoLike::getUserIp, userIp)
            );
            // 减少点赞数
            videoMapper.decrementLikeCount(videoId);
            return false;
        } else {
            // 添加点赞
            VideoLike videoLike = new VideoLike();
            videoLike.setVideoId(videoId);
            videoLike.setUserIp(userIp);
            videoLike.setUserAgent(userAgent);
            videoLike.setCreatedAt(LocalDateTime.now());
            
            videoLikeMapper.insert(videoLike);
            // 增加点赞数
            videoMapper.incrementLikeCount(videoId);
            return true;
        }
    }

    @Override
    @Transactional
    public Map<String, Object> submitVideo(Video video, MultipartFile videoFile, MultipartFile coverFile) {
        Map<String, Object> result = new HashMap<>();
        
        if (videoFile == null || videoFile.isEmpty()) {
            throw new RuntimeException("视频文件不能为空");
        }
        
        try {
            // 上传视频文件
            Map<String, Object> videoUploadResult = null; //todo: 文件上传以后开放即可！
                    // fileUploadService.uploadFile(videoFile, "videos/original/");
            video.setFileUrl(videoUploadResult.get("url").toString());
            video.setFileSize(videoFile.getSize());
            
            // 上传封面文件（可选）
            if (coverFile != null && !coverFile.isEmpty()) {
                Map<String, Object> coverUploadResult = null; //todo： 文件上传以后开放即可
                        // fileUploadService.uploadFile(coverFile, "videos/covers/");
                video.setCoverUrl(coverUploadResult.get("url").toString());
            }
            
            // 设置用户投稿默认值
            video.setUploaderType(Video.UPLOADER_TYPE_USER);
            video.setStatus(Video.STATUS_PENDING); // 待审核
            video.setViewCount(0L);
            video.setLikeCount(0L);
            video.setCreatedAt(LocalDateTime.now());
            video.setUpdatedAt(LocalDateTime.now());
            
            // 保存视频信息
            videoMapper.insert(video);
            
            result.put("success", true);
            result.put("message", "视频投稿成功，请等待审核");
            result.put("videoId", video.getId());
            
        } catch (Exception e) {
            throw new RuntimeException("视频上传失败：" + e.getMessage());
        }
        
        return result;
    }

    @Override
    public IPage<Video> getVideosForAdmin(Integer page, Integer size, Integer status, Integer uploaderType, String keyword) {
        Page<Video> pageObj = new Page<>(page, size);
        IPage<Video> result = videoMapper.getVideosForAdmin(pageObj, status, uploaderType, keyword);
        
        // 格式化视频信息
        result.getRecords().forEach(video -> {
            formatVideoInfo(video);
            formatVideoStatus(video);
        });
        
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> uploadVideoByAdmin(Video video, MultipartFile videoFile, MultipartFile coverFile, Long adminId) {
        Map<String, Object> result = new HashMap<>();
        
        if (videoFile == null || videoFile.isEmpty()) {
            throw new RuntimeException("视频文件不能为空");
        }
        
        try {
            // 上传视频文件 todo: 文件上传实现以后开放即可！
            Map<String, Object> videoUploadResult = null;
                    //fileUploadService.uploadFile(videoFile, "videos/original/");
            video.setFileUrl(videoUploadResult.get("url").toString());
            video.setFileSize(videoFile.getSize());
            
            // 上传封面文件（可选）
            if (coverFile != null && !coverFile.isEmpty()) {
                Map<String, Object> coverUploadResult = null;
                       // fileUploadService.uploadFile(coverFile, "videos/covers/");
                video.setCoverUrl(coverUploadResult.get("url").toString());
            }
            
            // 设置管理员上传默认值
            video.setUploaderType(Video.UPLOADER_TYPE_ADMIN);
            video.setAdminId(adminId);
            video.setStatus(Video.STATUS_PUBLISHED); // 管理员上传直接发布
            video.setAuditAdminId(adminId);
            video.setAuditTime(LocalDateTime.now());
            video.setViewCount(0L);
            video.setLikeCount(0L);
            video.setCreatedAt(LocalDateTime.now());
            video.setUpdatedAt(LocalDateTime.now());
            
            // 保存视频信息
            videoMapper.insert(video);
            
            result.put("success", true);
            result.put("message", "视频上传成功");
            result.put("videoId", video.getId());
            
        } catch (Exception e) {
            throw new RuntimeException("视频上传失败：" + e.getMessage());
        }
        
        return result;
    }

    @Override
    @Transactional
    public void auditVideo(Long videoId, Integer status, String reason, Long adminId) {
        Video video = videoMapper.selectById(videoId);
        if (video == null) {
            throw new RuntimeException("视频不存在");
        }
        
        if (video.getStatus() != Video.STATUS_PENDING) {
            throw new RuntimeException("只能审核待审核状态的视频");
        }
        
        if (status == Video.STATUS_REJECTED && (reason == null || reason.trim().isEmpty())) {
            throw new RuntimeException("拒绝审核时必须填写拒绝原因");
        }
        
        // 更新审核信息
        video.setStatus(status);
        video.setAuditAdminId(adminId);
        video.setAuditTime(LocalDateTime.now());
        video.setAuditReason(reason);
        video.setUpdatedAt(LocalDateTime.now());
        
        videoMapper.updateById(video);
    }

    @Override
    @Transactional
    public void offlineVideo(Long videoId, Long adminId) {
        Video video = videoMapper.selectById(videoId);
        if (video == null) {
            throw new RuntimeException("视频不存在");
        }
        
        if (video.getStatus() != Video.STATUS_PUBLISHED) {
            throw new RuntimeException("只能下架已发布的视频");
        }
        
        video.setStatus(Video.STATUS_OFFLINE);
        video.setAuditAdminId(adminId);
        video.setAuditTime(LocalDateTime.now());
        video.setUpdatedAt(LocalDateTime.now());
        
        videoMapper.updateById(video);
    }

    @Override
    @Transactional
    public void deleteVideo(Long videoId) {
        Video video = videoMapper.selectById(videoId);
        if (video == null) {
            throw new RuntimeException("视频不存在");
        }
        
        // 删除相关数据
        videoLikeMapper.delete(new LambdaQueryWrapper<VideoLike>().eq(VideoLike::getVideoId, videoId));
        videoViewMapper.delete(new LambdaQueryWrapper<VideoView>().eq(VideoView::getVideoId, videoId));
        
        // 删除视频记录
        videoMapper.deleteById(videoId);
        
        // TODO: 删除文件存储中的视频文件和封面文件
    }

    @Override
    public Map<String, Object> getVideoStatistics() {
        return videoMapper.getVideoStatistics();
    }

    @Override
    public Map<String, Object> getVideoDetailStats(Long videoId, Integer days) {
        Map<String, Object> stats = new HashMap<>();
        
        // 基本统计
        Long viewCount = videoViewMapper.getViewCountByVideoId(videoId);
        Long likeCount = videoLikeMapper.getLikeCountByVideoId(videoId);
        Double avgDuration = videoViewMapper.getAverageViewDuration(videoId);
        
        stats.put("viewCount", viewCount);
        stats.put("likeCount", likeCount);
        stats.put("averageViewDuration", avgDuration);
        
        // 按日期统计
        List<Map<String, Object>> dailyStats = videoViewMapper.getViewStatsByDate(videoId, days);
        stats.put("dailyViewStats", dailyStats);
        
        return stats;
    }

    /**
     * 格式化视频信息
     */
    private void formatVideoInfo(Video video) {
        // 格式化时长
        if (video.getDuration() != null) {
            int minutes = video.getDuration() / 60;
            int seconds = video.getDuration() % 60;
            video.setDurationText(String.format("%02d:%02d", minutes, seconds));
        }
        
        // 格式化文件大小
        if (video.getFileSize() != null) {
            video.setFileSizeText(formatFileSize(video.getFileSize()));
        }
    }

    /**
     * 格式化视频状态信息
     */
    private void formatVideoStatus(Video video) {
        // 上传者类型文本
        if (video.getUploaderType() == Video.UPLOADER_TYPE_USER) {
            video.setUploaderTypeText("用户投稿");
        } else if (video.getUploaderType() == Video.UPLOADER_TYPE_ADMIN) {
            video.setUploaderTypeText("管理员上传");
        }
        
        // 状态文本
        switch (video.getStatus()) {
            case 0:
                video.setStatusText("待审核");
                break;
            case 1:
                video.setStatusText("已发布");
                break;
            case 2:
                video.setStatusText("已拒绝");
                break;
            case 3:
                video.setStatusText("已下架");
                break;
            default:
                video.setStatusText("未知状态");
        }
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(Long size) {
        if (size < 1024) {
            return size + "B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1fKB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1fMB", size / (1024.0 * 1024));
        } else {
            return String.format("%.1fGB", size / (1024.0 * 1024 * 1024));
        }
    }
} 