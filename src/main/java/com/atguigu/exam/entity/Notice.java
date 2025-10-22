package com.atguigu.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告实体类 - 系统公告信息
 */
@Data
@TableName("notices")
@Schema(description = "系统公告信息")
public class Notice extends BaseEntity {

    
    @Schema(description = "公告标题", 
            example = "系统维护通知")
    private String title; // 公告标题
    
    @Schema(description = "公告内容详情", 
            example = "系统将于今晚22:00-24:00进行维护升级，期间无法访问，请合理安排考试时间...")
    private String content; // 公告内容
    
    @Schema(description = "公告类型", 
            example = "SYSTEM", 
            allowableValues = {"SYSTEM", "FEATURE", "NOTICE"})
    private String type; // 公告类型：SYSTEM(系统)、FEATURE(新功能)、NOTICE(通知)
    
    @Schema(description = "优先级级别", 
            example = "1", 
            allowableValues = {"0", "1", "2"})
    private Integer priority; // 优先级：0-普通，1-重要，2-紧急
    
    @Schema(description = "是否启用显示", 
            example = "true")
    private Boolean isActive; // 是否启用

} 