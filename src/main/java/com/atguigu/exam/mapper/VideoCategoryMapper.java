package com.atguigu.exam.mapper;


import com.atguigu.exam.entity.VideoCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;

import java.util.List;
import java.util.Map;

/**
 * 视频分类Mapper接口
 * 提供视频分类相关的数据访问操作
 */
@Mapper
public interface VideoCategoryMapper extends BaseMapper<VideoCategory> {
    
    /**
     * 获取每个分类的视频数量统计
     * @return 包含分类ID和视频数量的结果列表
     */
    @Select("SELECT category_id, COUNT(*) as video_count FROM videos WHERE status = 1 GROUP BY category_id")
    @Results({
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "videoCount", column = "video_count")
    })
    List<Map<String, Object>> getCategoryVideoCount();
    
    /**
     * 获取所有启用的顶级分类
     * @return 顶级分类列表
     */
    @Select("SELECT * FROM video_categories WHERE parent_id = 0 AND status = 1 ORDER BY sort_order ASC")
    List<VideoCategory> getTopCategories();
    
    /**
     * 根据父级分类ID获取子分类
     * @param parentId 父级分类ID
     * @return 子分类列表
     */
    @Select("SELECT * FROM video_categories WHERE parent_id = #{parentId} AND status = 1 ORDER BY sort_order ASC")
    List<VideoCategory> getChildCategories(Long parentId);
} 