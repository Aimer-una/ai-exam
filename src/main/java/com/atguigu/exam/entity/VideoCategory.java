package com.atguigu.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 视频分类实体类
 * 支持层级分类结构，用于组织视频内容
 */
@Data
@TableName("video_categories")
@Schema(description = "视频分类信息")
public class VideoCategory {
    
    @Schema(description = "分类ID，唯一标识", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @Schema(description = "分类名称", example = "Java基础")
    private String name;
    
    @Schema(description = "分类描述", example = "Java编程语言基础知识讲解")
    private String description;
    
    @Schema(description = "父级分类ID，0为顶级分类", example = "0")
    private Long parentId;
    
    @Schema(description = "排序权重，数字越小越靠前", example = "1")
    private Integer sortOrder;
    
    @Schema(description = "状态：1-启用，0-禁用", example = "1")
    private Integer status;
    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // ========== 非数据库字段 ==========
    
    @Schema(description = "子分类列表，用于构建分类树结构")
    @TableField(exist = false)
    private List<VideoCategory> children;
    
    @Schema(description = "该分类下的视频数量", example = "15")
    @TableField(exist = false)
    private Long videoCount;
    
    @Schema(description = "父级分类名称", example = "编程技术")
    @TableField(exist = false)
    private String parentName;
} 