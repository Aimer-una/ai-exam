package com.atguigu.exam.mapper;

import com.atguigu.exam.entity.VideoView;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 视频观看记录Mapper接口
 * 提供视频观看统计相关的数据访问操作
 */
@Mapper
public interface VideoViewMapper extends BaseMapper<VideoView> {
    
    /**
     * 获取视频的观看总数
     * @param videoId 视频ID
     * @return 观看总数
     */
    @Select("SELECT COUNT(*) FROM video_views WHERE video_id = #{videoId}")
    Long getViewCountByVideoId(@Param("videoId") Long videoId);
    
    /**
     * 获取视频的平均观看时长
     * @param videoId 视频ID
     * @return 平均观看时长（秒）
     */
    @Select("SELECT AVG(view_duration) FROM video_views WHERE video_id = #{videoId} AND view_duration > 0")
    Double getAverageViewDuration(@Param("videoId") Long videoId);
    
    /**
     * 获取观看统计数据（按日期分组）
     * @param videoId 视频ID
     * @param days 统计天数
     * @return 观看统计列表
     */
    @Select("SELECT DATE(created_at) as view_date, COUNT(*) as view_count " +
            "FROM video_views " +
            "WHERE video_id = #{videoId} AND created_at >= DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY view_date DESC")
    List<Map<String, Object>> getViewStatsByDate(@Param("videoId") Long videoId, @Param("days") Integer days);
} 