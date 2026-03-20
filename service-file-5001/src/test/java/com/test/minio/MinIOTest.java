package com.test.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import org.junit.jupiter.api.Test;

public class MinIOTest {

    @Test
    public void testUpload() throws Exception {
        // 创建客户端
        MinioClient minioClient = MinioClient.builder()
                        .endpoint("http://192.168.10.9:9000")
                        .credentials("admin", "admin123456")
                        .build();

        // 如果没有对应的bucket则创建
        String bucketName = "localjava";
        boolean found = minioClient
                .bucketExists(
                        BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        } else {
            System.out.println("Bucket " + bucketName + " already exists.");
        }

        // 上传文件
        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(bucketName)
                        .object("abc.jpg")
                        .filename("/Users/xingma/Desktop/temp/face/2034574273133408257.jpg")
                        .build());
    }

}
