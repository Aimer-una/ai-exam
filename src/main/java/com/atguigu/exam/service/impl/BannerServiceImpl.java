package com.atguigu.exam.service.impl;

import com.atguigu.exam.entity.Banner;
import com.atguigu.exam.mapper.BannerMapper;
import com.atguigu.exam.service.BannerService;

import com.atguigu.exam.service.FileUploadService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
/**
 * 轮播图服务实现类
 */
@Service
@Slf4j
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Autowired
    private FileUploadService fileUploadService;
    @Override
    public String getFileUrl(MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // 判断上传的文件是否为空
        if (ObjectUtils.isEmpty(file)) {
            throw new RuntimeException("请选择要上传的文件");
        }
        // 判断文件的类型是否正确
        String contentType = file.getContentType();
        if (StringUtils.isEmpty(contentType) || !contentType.startsWith("image")){
            throw new RuntimeException("请选择正确格式的文件");
        }

        // 文件大小限制
        if (file.getSize() > 5 * 1024 * 1024){
            throw new RuntimeException("上传的文件不可以超过5MB");
        }

        // 调用上传文件的业务
        String url = fileUploadService.uploadFile(file, "banner");
        log.info("完成banner图片上传，图片回显地址：{}",url);
        return url;
    }
}