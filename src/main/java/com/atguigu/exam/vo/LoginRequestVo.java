package com.atguigu.exam.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求Vo - 用户登录时提交的参数
 */
@Data
@Schema(description = "用户登录请求参数")
public class LoginRequestVo {
    
    @Schema(description = "用户名", 
            example = "admin", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    private String username; // 用户名
    
    @Schema(description = "登录密码", 
            example = "123456", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    private String password; // 密码
} 