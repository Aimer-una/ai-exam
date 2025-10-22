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
 * 视频信息实体类
 * 管理视频的基本信息、上传信息、审核状态和统计数据
 */
@Data
@TableName("videos")
@Schema(description = "视频信息")
public class Video {
    
    @Schema(description = "视频ID，唯一标识", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @Schema(description = "视频标题", example = "Java基础语法入门")
    private String title;
    
    @Schema(description = "视频描述", example = "从零开始学习Java编程语言，掌握基本语法和编程思想")
    private String description;
    
    @Schema(description = "分类ID", example = "11")
    private Long categoryId;
    
    @Schema(description = "视频文件URL", example = "http://localhost:9000/videos/original/java-basic-syntax.mp4")
    private String fileUrl;
    
    @Schema(description = "封面图片URL", example = "http://localhost:9000/videos/covers/java-basic-syntax.jpg")
    private String coverUrl;
    
    @Schema(description = "视频时长（秒）", example = "1800")
    private Integer duration;
    
    @Schema(description = "文件大小（字节）", example = "104857600")
    private Long fileSize;
    
    @Schema(description = "上传者名称", example = "技术讲师")
    private String uploaderName;
    
    @Schema(description = "上传者类型：1-用户投稿，2-管理员上传", example = "2")
    private Integer uploaderType;
    
    @Schema(description = "上传用户ID（用户投稿时）", example = "123")
    private Long userId;
    
    @Schema(description = "管理员ID（管理员上传时）", example = "1")
    private Long adminId;
    
    @Schema(description = "状态：0-待审核，1-已发布，2-已拒绝，3-已下架", example = "1")
    private Integer status;
    
    @Schema(description = "审核管理员ID", example = "1")
    private Long auditAdminId;
    
    @Schema(description = "审核时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;
    
    @Schema(description = "审核原因（拒绝时）", example = "内容不符合平台规范")
    private String auditReason;
    
    @Schema(description = "观看次数", example = "156")
    private Long viewCount;
    
    @Schema(description = "点赞次数", example = "23")
    private Long likeCount;
    
    @Schema(description = "标签，逗号分隔", example = "Java,基础,语法,入门")
    private String tags;
    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // ========== 非数据库字段 ==========
    
    @Schema(description = "分类名称", example = "Java基础")
    @TableField(exist = false)
    private String categoryName;
    
    @Schema(description = "上传者类型文本", example = "管理员上传")
    @TableField(exist = false)
    private String uploaderTypeText;
    
    @Schema(description = "状态文本", example = "已发布")
    @TableField(exist = false)
    private String statusText;
    
    @Schema(description = "审核管理员名称", example = "管理员")
    @TableField(exist = false)
    private String auditAdminName;
    
    @Schema(description = "视频时长格式化文本", example = "30:00")
    @TableField(exist = false)
    private String durationText;
    
    @Schema(description = "文件大小格式化文本", example = "100MB")
    @TableField(exist = false)
    private String fileSizeText;
    
    @Schema(description = "当前用户是否已点赞", example = "true")
    @TableField(exist = false)
    private Boolean isLiked;
    
    // ========== 常量定义 ==========
    
    // 上传者类型
    public static final int UPLOADER_TYPE_USER = 1; // 用户投稿
    public static final int UPLOADER_TYPE_ADMIN = 2; // 管理员上传
    
    // 视频状态
    public static final int STATUS_PENDING = 0; // 待审核
    public static final int STATUS_PUBLISHED = 1; // 已发布
    public static final int STATUS_REJECTED = 2; // 已拒绝
    public static final int STATUS_OFFLINE = 3; // 已下架
} 