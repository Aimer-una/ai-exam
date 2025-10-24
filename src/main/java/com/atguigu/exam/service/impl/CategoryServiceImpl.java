package com.atguigu.exam.service.impl;


import com.atguigu.exam.entity.Category;
import com.atguigu.exam.mapper.CategoryMapper;
import com.atguigu.exam.mapper.QuestionMapper;
import com.atguigu.exam.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService {

    @Autowired
    private QuestionMapper questionMapper;
    @Override
    public List<Category> getAllCategories() {
        // 1. 获取所有分类的基础信息，并按sort字段排序
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        List<Category> list = list(lambdaQueryWrapper);

        // 1. 一次性查询出所有分类的题目数量
        List<Map<String,Long>> map = questionMapper.getCategoryAndCount();

        // 2. 将查询结果从 List<Map> 转换为 Map<categoryId, count>，便于快速查找
        Map<Long, Long> questionCountMap = map.stream().collect(Collectors.toMap(
                m -> Long.valueOf(m.get("category_id").toString()),
                m -> Long.valueOf(m.get("count").toString())
        ));
        for (Category category : list) {
            category.setCount(questionCountMap.getOrDefault(category.getId(),0L));
        }
        return list;
    }

    @Override
    public List<Category> getCategoryTree() {
        List<Category> categoryList = getAllCategories();
        // 1. 使用Stream API按parentId进行分组，得到 Map<parentId, List<children>>
        Map<Long, List<Category>> childrenMap = categoryList.stream().collect(Collectors.groupingBy(Category::getParentId));

        // 筛选一级分类信息
        List<Category> parentCategory = categoryList.stream().filter(c -> c.getParentId() == 0).collect(Collectors.toList());
        // 2. 遍历所有分类，为它们设置children属性，并递归地累加题目数量
        for (Category category : parentCategory) {

            // 从Map中找到当前分类的所有子分类
            List<Category> childrenCategory = childrenMap.getOrDefault(category.getId(), new ArrayList<>());
            category.setChildren(childrenCategory);

            // 汇总子分类的题目数量到父分类
            Long sonCount = childrenCategory.stream().collect(Collectors.summingLong(Category::getCount));
            category.setCount(category.getCount() + sonCount);

        }
        return parentCategory;
    }
}