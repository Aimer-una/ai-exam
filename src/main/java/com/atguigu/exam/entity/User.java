package com.atguigu.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类 - 系统用户信息模型
 * 
 * MyBatis Plus教学要点：
 * 1. @TableName注解：指定对应的数据库表名
 * 2. @TableId注解：标识主键字段，AUTO表示数据库自增
 * 3. @TableField注解：处理字段映射，特别是驼峰命名与下划线的转换
 * 4. @Data注解：Lombok自动生成getter/setter、toString等方法
 * 
 * 数据库设计：
 * - 对应表：users
 * - 主键：id（自增）
 * - 索引：username（唯一索引）
 * 
 * @author 智能学习平台开发团队
 * @version 1.0
 */
@Data  // Lombok注解：自动生成getter、setter、toString、equals、hashCode方法
@TableName("users")  // MyBatis Plus注解：指定对应的数据库表名
@Schema(description = "用户信息")
public class User extends BaseEntity {
    
    @Schema(description = "用户名，用于登录", 
            example = "admin")
    private String username;
    
    @Schema(description = "用户密码", 
            example = "******")
    private String password;
    
    @Schema(description = "用户真实姓名", 
            example = "张三")
    @TableField("real_name")  // 显式指定数据库字段名
    private String realName;
    
    @Schema(description = "用户角色", 
            example = "ADMIN", 
            allowableValues = {"ADMIN", "TEACHER", "STUDENT"})
    private String role;
    
    @Schema(description = "用户状态", 
            example = "ACTIVE", 
            allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;
} 