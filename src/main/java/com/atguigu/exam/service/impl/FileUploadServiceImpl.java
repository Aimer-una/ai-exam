package com.atguigu.exam.service.impl;

import com.atguigu.exam.config.properties.MinioProperties;
import com.atguigu.exam.service.FileUploadService;
import io.minio.*;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * projectName: com.atguigu.exam.service.impl
 *
 * @author: 赵伟风
 * description:
 */
@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinioProperties minioProperties;

    @Override
    public String uploadFile(MultipartFile file, String folder) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // 1.判断桶是否存在
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.getBucketName()).build());
        // 2.不存在，创建桶，同时设置访问权限
        if (!bucketExists){
            // 创建桶
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucketName()).build());
            String config = """
                        {
                              "Statement" : [ {
                                "Action" : "s3:GetObject",
                                "Effect" : "Allow",
                                "Principal" : "*",
                                "Resource" : "arn:aws:s3:::%s/*"
                              } ],
                              "Version" : "2012-10-17"
                        }
                    """.formatted(minioProperties.getBucketName());
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(minioProperties.getBucketName()).config(config).build());
        }

        // 3.处理上传的对象名（影响，minio桶中的文件结构！）
        String objectName = folder + "/" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + UUID.randomUUID().toString().replaceAll("-","") + "_" + file.getOriginalFilename();
        log.debug("文件上传核心业务方法，处理后的文件对象名：{}",objectName);

        // 4.上传文件
        // putObject . 上传文件数据 .steam(文件输入流)
        // uploadObject .上传文件数据 .filename(文件的磁盘地址 c:\\)
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .contentType(file.getContentType())
                .object(objectName)
                .stream(file.getInputStream(),file.getSize(),-1) // -1 我们不指定文件切割大小！让minio自动
                .build());

        // 5. 拼接回显地址 【端点 + 桶 + 对象名】
        String url = String.join("/", minioProperties.getEndPoint(), minioProperties.getBucketName(), objectName);
        log.info("文件上传核心业务，完成{}文件上传，返回地址为：{}",objectName,url);
        return url;
    }
}
