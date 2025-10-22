package com.atguigu.exam.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * projectName: com.atguigu.exam.config
 *
 * @author: 赵伟风
 * description:
 */
@Configuration
@MapperScan("com.atguigu.exam.mapper")
public class MybatisPlusConfiguration {

}