package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.config.minio.MinioProperties;
import com.atguigu.gmall.product.service.FileService;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    MinioClient minioClient;
    @Autowired
    MinioProperties minioProperties;

    @Override
    public String fileUpload(MultipartFile file) throws Exception {
        String fileName = UUID.randomUUID().toString().replace("-","")+file.getOriginalFilename();
        PutObjectOptions options = new PutObjectOptions(file.getSize(),-1);
        options.setContentType(file.getContentType());
        minioClient.putObject(minioProperties.getBucketName(),fileName,file.getInputStream(),options);
        String url = minioProperties.getEndpoint()+"/"+minioProperties.getBucketName()+"/"+fileName;
        return url;
    }
}
