package com.atguigu.exam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * projectName: com.atguigu
 *
 * @author: 赵伟风
 * description:
 */
@SpringBootApplication
public class AdminWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminWebApplication.class, args);
        System.out.println("智能学习系统启动成功！！");
    }
}