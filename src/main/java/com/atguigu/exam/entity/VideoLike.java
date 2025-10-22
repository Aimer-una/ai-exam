package com.atguigu.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 视频点赞实体类
 * 记录用户对视频的点赞行为，基于IP进行匿名点赞
 */
@Data
@TableName("video_likes")
@Schema(description = "视频点赞记录")
public class VideoLike {
    
    @Schema(description = "点赞ID，唯一标识", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @Schema(description = "视频ID", example = "1")
    private Long videoId;
    
    @Schema(description = "用户IP地址（匿名点赞）", example = "192.168.1.100")
    private String userIp;
    
    @Schema(description = "用户代理信息", example = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
    private String userAgent;
    
    @Schema(description = "点赞时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    // ========== 非数据库字段 ==========
    
    @Schema(description = "视频标题", example = "Java基础语法入门")
    @TableField(exist = false)
    private String videoTitle;
} 