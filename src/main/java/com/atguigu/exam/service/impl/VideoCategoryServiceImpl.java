package com.atguigu.exam.service.impl;

import com.atguigu.exam.entity.Video;
import com.atguigu.exam.entity.VideoCategory;
import com.atguigu.exam.mapper.VideoCategoryMapper;
import com.atguigu.exam.mapper.VideoMapper;
import com.atguigu.exam.service.VideoCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 视频分类Service实现类
 * 提供视频分类管理的业务逻辑实现
 */
@Service
public class VideoCategoryServiceImpl implements VideoCategoryService {

    @Autowired
    private VideoCategoryMapper videoCategoryMapper;

    @Autowired
    private VideoMapper videoMapper;

    @Override
    public List<VideoCategory> getCategoryTree() {
        // 获取所有启用的分类
        List<VideoCategory> allCategories = videoCategoryMapper.selectList(
            new LambdaQueryWrapper<VideoCategory>()
                .eq(VideoCategory::getStatus, 1)
                .orderByAsc(VideoCategory::getSortOrder)
        );
        
        // 获取并填充每个分类的视频数量
        fillVideoCount(allCategories);
        
        // 构建树形结构
        return buildTree(allCategories);
    }

    @Override
    public List<VideoCategory> getAllCategories() {
        // 获取所有分类
        List<VideoCategory> categories = videoCategoryMapper.selectList(
            new LambdaQueryWrapper<VideoCategory>()
                .orderByAsc(VideoCategory::getSortOrder)
        );
        
        // 获取并填充每个分类的视频数量
        fillVideoCount(categories);
        
        return categories;
    }

    @Override
    public List<VideoCategory> getTopCategories() {
        return videoCategoryMapper.getTopCategories();
    }

    @Override
    public List<VideoCategory> getChildCategories(Long parentId) {
        return videoCategoryMapper.getChildCategories(parentId);
    }

    @Override
    public VideoCategory getCategoryById(Long id) {
        VideoCategory category = videoCategoryMapper.selectById(id);
        if (category != null) {
            // 获取视频数量
            Long videoCount = videoMapper.selectCount(
                new LambdaQueryWrapper<Video>()
                    .eq(Video::getCategoryId, id)
                    .eq(Video::getStatus, Video.STATUS_PUBLISHED)
            );
            category.setVideoCount(videoCount);
            
            // 如果有父级分类，获取父级分类名称
            if (category.getParentId() != null && category.getParentId() > 0) {
                VideoCategory parentCategory = videoCategoryMapper.selectById(category.getParentId());
                if (parentCategory != null) {
                    category.setParentName(parentCategory.getName());
                }
            }
        }
        return category;
    }

    @Override
    public void addCategory(VideoCategory category) {
        // 验证父级分类是否存在
        if (category.getParentId() != null && category.getParentId() > 0) {
            VideoCategory parentCategory = videoCategoryMapper.selectById(category.getParentId());
            if (parentCategory == null) {
                throw new RuntimeException("父级分类不存在");
            }
            if (parentCategory.getStatus() == 0) {
                throw new RuntimeException("父级分类已被禁用");
            }
        }
        
        // 检查同级分类名称是否重复
        Long count = videoCategoryMapper.selectCount(
            new LambdaQueryWrapper<VideoCategory>()
                .eq(VideoCategory::getName, category.getName())
                .eq(VideoCategory::getParentId, category.getParentId() == null ? 0 : category.getParentId())
        );
        if (count > 0) {
            throw new RuntimeException("同级分类下已存在相同名称的分类");
        }
        
        // 设置默认值
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        
        videoCategoryMapper.insert(category);
    }

    @Override
    public void updateCategory(VideoCategory category) {
        VideoCategory existingCategory = videoCategoryMapper.selectById(category.getId());
        if (existingCategory == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 验证父级分类
        if (category.getParentId() != null && category.getParentId() > 0) {
            // 不能将自己设为父级分类
            if (category.getParentId().equals(category.getId())) {
                throw new RuntimeException("不能将自己设为父级分类");
            }
            
            // 验证父级分类是否存在
            VideoCategory parentCategory = videoCategoryMapper.selectById(category.getParentId());
            if (parentCategory == null) {
                throw new RuntimeException("父级分类不存在");
            }
        }
        
        // 检查同级分类名称是否重复
        Long count = videoCategoryMapper.selectCount(
            new LambdaQueryWrapper<VideoCategory>()
                .eq(VideoCategory::getName, category.getName())
                .eq(VideoCategory::getParentId, category.getParentId() == null ? 0 : category.getParentId())
                .ne(VideoCategory::getId, category.getId())
        );
        if (count > 0) {
            throw new RuntimeException("同级分类下已存在相同名称的分类");
        }
        
        videoCategoryMapper.updateById(category);
    }

    @Override
    public void deleteCategory(Long id) {
        VideoCategory category = videoCategoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 检查是否有子分类
        Long childCount = videoCategoryMapper.selectCount(
            new LambdaQueryWrapper<VideoCategory>()
                .eq(VideoCategory::getParentId, id)
        );
        if (childCount > 0) {
            throw new RuntimeException("该分类下有子分类，无法删除");
        }
        
        // 检查是否有视频
        Long videoCount = videoMapper.selectCount(
            new LambdaQueryWrapper<Video>()
                .eq(Video::getCategoryId, id)
        );
        if (videoCount > 0) {
            throw new RuntimeException("该分类下有视频，无法删除");
        }
        
        videoCategoryMapper.deleteById(id);
    }

    /**
     * 填充分类的视频数量
     */
    private void fillVideoCount(List<VideoCategory> categories) {
        // 获取每个分类的视频数量
        List<Map<String, Object>> videoCountList = videoCategoryMapper.getCategoryVideoCount();
        
        // 将结果转换为Map<Long, Long>格式
        Map<Long, Long> videoCountMap = videoCountList.stream()
            .collect(Collectors.toMap(
                map -> Long.valueOf(map.get("categoryId").toString()),
                map -> Long.valueOf(map.get("videoCount").toString())
            ));
        
        // 设置视频数量
        categories.forEach(category -> 
            category.setVideoCount(videoCountMap.getOrDefault(category.getId(), 0L))
        );
    }

    /**
     * 构建树形结构
     */
    private List<VideoCategory> buildTree(List<VideoCategory> categories) {
        // 按parentId分组
        Map<Long, List<VideoCategory>> childrenMap = categories.stream()
            .collect(Collectors.groupingBy(VideoCategory::getParentId));
        
        // 设置children属性，并从下至上汇总视频数量
        categories.forEach(category -> {
            List<VideoCategory> children = childrenMap.getOrDefault(category.getId(), new ArrayList<>());
            category.setChildren(children);
            
            // 汇总子分类的视频数量到父分类
            long childrenCount = children.stream()
                .mapToLong(c -> c.getVideoCount() != null ? c.getVideoCount() : 0L)
                .sum();
            long selfCount = category.getVideoCount() != null ? category.getVideoCount() : 0L;
            category.setVideoCount(selfCount + childrenCount);
        });
        
        // 返回顶级分类（parentId = 0）
        return categories.stream()
            .filter(c -> c.getParentId() == 0)
            .collect(Collectors.toList());
    }
} 