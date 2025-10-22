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
 * 视频观看记录实体类
 * 记录用户的视频观看行为和统计数据
 */
@Data
@TableName("video_views")
@Schema(description = "视频观看记录")
public class VideoView {
    
    @Schema(description = "观看ID，唯一标识", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @Schema(description = "视频ID", example = "1")
    private Long videoId;
    
    @Schema(description = "用户IP地址", example = "192.168.1.100")
    private String userIp;
    
    @Schema(description = "用户代理信息", example = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
    private String userAgent;
    
    @Schema(description = "观看时长（秒）", example = "1650")
    private Integer viewDuration;
    
    @Schema(description = "观看时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    // ========== 非数据库字段 ==========
    
    @Schema(description = "视频标题", example = "Java基础语法入门")
    @TableField(exist = false)
    private String videoTitle;
    
    @Schema(description = "观看时长格式化文本", example = "27:30")
    @TableField(exist = false)
    private String viewDurationText;
    
    @Schema(description = "观看完成度百分比", example = "91.7")
    @TableField(exist = false)
    private Double completionRate;
} 