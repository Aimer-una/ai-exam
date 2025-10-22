package com.atguigu.exam.service;

import com.atguigu.exam.common.Result;
import com.atguigu.exam.entity.Notice;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 公告服务接口
 */
public interface NoticeService extends IService<Notice> {
    /**
     * 获取所有启用的公告
     * @return 公告列表
     */
    Result<List<Notice>> getActiveNotices();

    /**
     * 获取最新的几条公告
     * @param limit 限制数量
     * @return 公告列表
     */
    Result<List<Notice>> getLatestNotices(int limit);

    /**
     * 获取所有公告（管理页面用）
     * @return 公告列表
     */
    Result<List<Notice>> getAllNotices();

    /**
     * 添加公告
     * @param notice 公告对象
     * @return 操作结果
     */
    Result<String> addNotice(Notice notice);

    /**
     * 更新公告
     * @param notice 公告对象
     * @return 操作结果
     */
    Result<String> updateNotice(Notice notice);

    /**
     * 删除公告
     * @param id 公告ID
     * @return 操作结果
     */
    Result<String> deleteNotice(Long id);

    /**
     * 启用/禁用公告
     * @param id 公告ID
     * @param isActive 是否启用
     * @return 操作结果
     */
    Result<String> toggleNoticeStatus(Long id, Boolean isActive);

} 