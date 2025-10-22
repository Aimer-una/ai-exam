package com.atguigu.exam.service;

import com.atguigu.exam.entity.VideoCategory;

import java.util.List;

/**
 * 视频分类Service接口
 * 提供视频分类管理的业务逻辑操作
 */
public interface VideoCategoryService {
    
    /**
     * 获取分类树（包含视频数量统计）
     * @return 分类树列表
     */
    List<VideoCategory> getCategoryTree();
    
    /**
     * 获取所有分类列表
     * @return 分类列表
     */
    List<VideoCategory> getAllCategories();
    
    /**
     * 获取启用的顶级分类
     * @return 顶级分类列表
     */
    List<VideoCategory> getTopCategories();
    
    /**
     * 根据父级分类ID获取子分类
     * @param parentId 父级分类ID
     * @return 子分类列表
     */
    List<VideoCategory> getChildCategories(Long parentId);
    
    /**
     * 根据ID获取分类详情
     * @param id 分类ID
     * @return 分类信息
     */
    VideoCategory getCategoryById(Long id);
    
    /**
     * 添加分类
     * @param category 分类信息
     */
    void addCategory(VideoCategory category);
    
    /**
     * 更新分类
     * @param category 分类信息
     */
    void updateCategory(VideoCategory category);
    
    /**
     * 删除分类
     * @param id 分类ID
     */
    void deleteCategory(Long id);
} 