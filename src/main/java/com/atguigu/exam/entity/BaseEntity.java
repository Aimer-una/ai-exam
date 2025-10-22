package com.atguigu.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.java.Log;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseEntity implements Serializable {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "创建时间")
    // 时间格式化(局部)
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    // json格式返回时不返回该字段
    @JsonIgnore
    @Schema(description = "修改时间")
    private Date updateTime;

    // json格式返回时不返回该字段
    @JsonIgnore
    @Schema(description = "逻辑删除")
    @TableField("is_deleted")
    // 逻辑删除
    // @TableLogic
    private Byte isDeleted;

}