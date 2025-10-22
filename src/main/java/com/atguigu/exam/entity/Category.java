package com.atguigu.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 题目分类实体类
 */
@Data
@TableName("categories")
@Schema(description = "题目分类信息")
public class Category extends BaseEntity {
    
    @Schema(description = "分类名称", 
            example = "Java基础")
    private String name;  // 分类名称
    
    @Schema(description = "父分类ID，顶级分类为0", 
            example = "0")
    private Long parentId;  // 父分类ID，顶级分类为0
    
    @Schema(description = "排序序号，数字越小越靠前", 
            example = "1")
    private Integer sort;  // 排序字段
    
    @Schema(description = "子分类列表，用于构建分类树结构")
    @TableField(exist = false)
    private List<Category> children;  // 子分类列表，不映射到数据库

    @Schema(description = "该分类下的题目数量", 
            example = "25")
    @TableField(exist = false)
    private Long count;  // 题目数量，不映射到数据库
} 