package com.atguigu.exam.mapper;

import com.atguigu.exam.entity.Video;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 视频信息Mapper接口
 * 提供视频相关的复杂数据访问操作
 */
@Mapper
public interface VideoMapper extends BaseMapper<Video> {
    
    /**
     * 分页查询已发布的视频列表（包含分类名称）
     * @param page 分页对象
     * @param categoryId 分类ID（可选）
     * @param keyword 搜索关键字（可选）
     * @return 视频分页结果
     */
    @Select("<script>" +
            "SELECT v.*, vc.name as category_name " +
            "FROM videos v " +
            "LEFT JOIN video_categories vc ON v.category_id = vc.id " +
            "WHERE v.status = 1 " +
            "<if test='categoryId != null'> AND v.category_id = #{categoryId} </if>" +
            "<if test='keyword != null and keyword != \"\"'> " +
            "AND (v.title LIKE CONCAT('%', #{keyword}, '%') " +
            "OR v.tags LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "ORDER BY v.created_at DESC" +
            "</script>")
    IPage<Video> getPublishedVideosPage(Page<?> page, 
                                       @Param("categoryId") Long categoryId, 
                                       @Param("keyword") String keyword);
    
    /**
     * 管理端分页查询视频列表（包含分类名称和审核管理员信息）
     * @param page 分页对象
     * @param status 状态筛选（可选）
     * @param uploaderType 上传者类型筛选（可选）
     * @param keyword 搜索关键字（可选）
     * @return 视频分页结果
     */
    @Select("<script>" +
            "SELECT v.*, vc.name as category_name, u.real_name as audit_admin_name " +
            "FROM videos v " +
            "LEFT JOIN video_categories vc ON v.category_id = vc.id " +
            "LEFT JOIN users u ON v.audit_admin_id = u.id " +
            "WHERE 1=1 " +
            "<if test='status != null'> AND v.status = #{status} </if>" +
            "<if test='uploaderType != null'> AND v.uploader_type = #{uploaderType} </if>" +
            "<if test='keyword != null and keyword != \"\"'> " +
            "AND (v.title LIKE CONCAT('%', #{keyword}, '%') " +
            "OR v.uploader_name LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "ORDER BY v.created_at DESC" +
            "</script>")
    IPage<Video> getVideosForAdmin(Page<?> page,
                                  @Param("status") Integer status,
                                  @Param("uploaderType") Integer uploaderType,
                                  @Param("keyword") String keyword);
    
    /**
     * 获取热门视频列表（按观看次数排序）
     * @param limit 限制数量
     * @return 热门视频列表
     */
    @Select("SELECT v.*, vc.name as category_name " +
            "FROM videos v " +
            "LEFT JOIN video_categories vc ON v.category_id = vc.id " +
            "WHERE v.status = 1 " +
            "ORDER BY v.view_count DESC " +
            "LIMIT #{limit}")
    List<Video> getPopularVideos(@Param("limit") Integer limit);
    
    /**
     * 获取最新视频列表
     * @param limit 限制数量
     * @return 最新视频列表
     */
    @Select("SELECT v.*, vc.name as category_name " +
            "FROM videos v " +
            "LEFT JOIN video_categories vc ON v.category_id = vc.id " +
            "WHERE v.status = 1 " +
            "ORDER BY v.created_at DESC " +
            "LIMIT #{limit}")
    List<Video> getLatestVideos(@Param("limit") Integer limit);
    
    /**
     * 获取视频统计信息
     * @return 统计数据
     */
    @Select("SELECT " +
            "COUNT(*) as total_count, " +
            "COUNT(CASE WHEN status = 0 THEN 1 END) as pending_count, " +
            "COUNT(CASE WHEN status = 1 THEN 1 END) as published_count, " +
            "COUNT(CASE WHEN status = 2 THEN 1 END) as rejected_count, " +
            "COUNT(CASE WHEN uploader_type = 1 THEN 1 END) as user_upload_count, " +
            "COUNT(CASE WHEN uploader_type = 2 THEN 1 END) as admin_upload_count " +
            "FROM videos")
    Map<String, Object> getVideoStatistics();
    
    /**
     * 增加视频观看次数
     * @param videoId 视频ID
     * @return 更新行数
     */
    @Update("UPDATE videos SET view_count = view_count + 1 WHERE id = #{videoId}")
    int incrementViewCount(@Param("videoId") Long videoId);
    
    /**
     * 增加视频点赞次数
     * @param videoId 视频ID
     * @return 更新行数
     */
    @Update("UPDATE videos SET like_count = like_count + 1 WHERE id = #{videoId}")
    int incrementLikeCount(@Param("videoId") Long videoId);
    
    /**
     * 减少视频点赞次数
     * @param videoId 视频ID
     * @return 更新行数
     */
    @Update("UPDATE videos SET like_count = like_count - 1 WHERE id = #{videoId} AND like_count > 0")
    int decrementLikeCount(@Param("videoId") Long videoId);
} 