package com.atguigu.exam.service.impl;

import com.atguigu.exam.common.Result;
import com.atguigu.exam.entity.Notice;
import com.atguigu.exam.mapper.NoticeMapper;
import com.atguigu.exam.service.NoticeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 公告服务实现类
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Override
    public Result<List<Notice>> getActiveNotices() {
        try {
            List<Notice> notices = baseMapper.selectActiveNotices();
            return Result.success(notices);
        } catch (Exception e) {
            return Result.error("获取公告失败：" + e.getMessage());
        }
    }

    @Override
    public Result<List<Notice>> getLatestNotices(int limit) {
        try {
            List<Notice> notices = baseMapper.selectLatestNotices(limit);
            return Result.success(notices);
        } catch (Exception e) {
            return Result.error("获取最新公告失败：" + e.getMessage());
        }
    }

    @Override
    public Result<List<Notice>> getAllNotices() {
        try {
            QueryWrapper<Notice> wrapper = new QueryWrapper<>();
            wrapper.orderByDesc("priority", "create_time");
            List<Notice> notices = this.list(wrapper);
            return Result.success(notices);
        } catch (Exception e) {
            return Result.error("获取公告列表失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> addNotice(Notice notice) {
        try {
            notice.setCreateTime(new Date());
            notice.setUpdateTime(new Date());
            if (notice.getIsActive() == null) {
                notice.setIsActive(true); // 默认启用
            }
            if (notice.getPriority() == null) {
                notice.setPriority(0); // 默认普通优先级
            }
            if (notice.getType() == null) {
                notice.setType("NOTICE"); // 默认通知类型
            }

            boolean success = this.save(notice);
            if (success) {
                return Result.success("公告添加成功");
            } else {
                return Result.error("公告添加失败");
            }
        } catch (Exception e) {
            return Result.error("公告添加失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> updateNotice(Notice notice) {
        try {
            notice.setUpdateTime(new Date());
            boolean success = this.updateById(notice);
            if (success) {
                return Result.success("公告更新成功");
            } else {
                return Result.error("公告更新失败");
            }
        } catch (Exception e) {
            return Result.error("公告更新失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> deleteNotice(Long id) {
        try {
            boolean success = this.removeById(id);
            if (success) {
                return Result.success("公告删除成功");
            } else {
                return Result.error("公告删除失败");
            }
        } catch (Exception e) {
            return Result.error("公告删除失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> toggleNoticeStatus(Long id, Boolean isActive) {
        try {
            Notice notice = new Notice();
            notice.setId(id);
            notice.setIsActive(isActive);
            notice.setUpdateTime(new Date());

            boolean success = this.updateById(notice);
            if (success) {
                String status = isActive ? "启用" : "禁用";
                return Result.success("公告" + status + "成功");
            } else {
                return Result.error("公告状态更新失败");
            }
        } catch (Exception e) {
            return Result.error("公告状态更新失败：" + e.getMessage());
        }
    }
} 