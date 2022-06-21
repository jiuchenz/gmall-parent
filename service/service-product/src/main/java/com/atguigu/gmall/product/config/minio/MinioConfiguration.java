package com.atguigu.gmall.product.config.minio;


import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MinioConfiguration {

    @Autowired
    MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() throws Exception {
        // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
        MinioClient minioClient = new MinioClient(minioProperties.getEndpoint(),
                minioProperties.getAccessKey(),
                minioProperties.getSecretKey());
        // 检查存储桶是否已经存在
        if (!minioClient.bucketExists(minioProperties.getBucketName())) {
            minioClient.makeBucket(minioProperties.getBucketName());
        }
        return minioClient;
    }
}
