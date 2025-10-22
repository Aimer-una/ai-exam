package com.atguigu.exam.mapper;


import com.atguigu.exam.entity.VideoLike;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

/**
 * 视频点赞Mapper接口
 * 提供视频点赞相关的数据访问操作
 */
@Mapper
public interface VideoLikeMapper extends BaseMapper<VideoLike> {
    
    /**
     * 检查用户是否已点赞该视频（基于IP）
     * @param videoId 视频ID
     * @param userIp 用户IP
     * @return 是否已点赞
     */
    @Select("SELECT COUNT(*) > 0 FROM video_likes WHERE video_id = #{videoId} AND user_ip = #{userIp}")
    boolean isLikedByIp(@Param("videoId") Long videoId, @Param("userIp") String userIp);
    
    /**
     * 获取视频的点赞总数
     * @param videoId 视频ID
     * @return 点赞总数
     */
    @Select("SELECT COUNT(*) FROM video_likes WHERE video_id = #{videoId}")
    Long getLikeCountByVideoId(@Param("videoId") Long videoId);
} 