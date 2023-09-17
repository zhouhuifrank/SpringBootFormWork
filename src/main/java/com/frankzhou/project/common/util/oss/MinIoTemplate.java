package com.frankzhou.project.common.util.oss;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description minIo操作模板类
 * @date 2023-07-21
 */
@Slf4j
@AllArgsConstructor
public class MinIoTemplate {

    MinioClient minioClient;

    OssProperties ossProperties;

    @PostConstruct
    public void initDefaultBucket() {
        String defaultBucketName = "default";
        if (bucketExits(defaultBucketName)) {
            log.info("默认桶已经存在");
        } else {
            makeBucket(defaultBucketName);
            log.info("默认桶创建完成");
        }
    }

    /**
     * 查询所有存储桶
     */
    @SneakyThrows
    public List<Bucket> listBuckets() {
        return minioClient.listBuckets();
    }

    /**
     * 查询存储桶是否存在
     */
    @SneakyThrows
    public boolean bucketExits(String bucketName) {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    /**
     * 创建一个存储桶
     */
    @SneakyThrows
    public void makeBucket(String bucketName) {
        if (!bucketExits(bucketName)) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     * 删除一个存储桶
     */
    @SneakyThrows
    public void deleteBucket(String bucketName) {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
    }

    /**
     * 存入一个对象
     */
    @SneakyThrows
    public OssFile putObject(InputStream inputStream,String bucketName,String originalFileName) {
        String uuidFileName = generateRandomFileName(originalFileName);
        try {
            if (StrUtil.isNotBlank(bucketName)) {
                minioClient.putObject(PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(uuidFileName)
                                .stream(inputStream,inputStream.available(),-1)
                                .build());

                OssFile ossFile = OssFile.builder()
                        .filePath(uuidFileName)
                        .originalFileName(originalFileName)
                        .build();
                return ossFile;
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return null;
    }

    /**
     * 返回临时带前面、过期时间一天和Get方式请求的Url
     */
    @SneakyThrows
    public String getPreSignedObjectUrl(String bucketName, String ossFilePath) {
        String uploadUrl =  minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(ossFilePath)
                        .expiry(60 * 60 * 24)
                        .build());
        return uploadUrl;
    }

    private String getDownloadUrl(String bucketName, String pathFile) {
        return ossProperties.getEndpoint() + StrUtil.SLASH + bucketName + pathFile;
    }



    /**
     * 获取某个桶内的文件 此操作需要对Object有读权限
     */
    @SneakyThrows
    public InputStream getObject(String bucketName,String filePath) {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(filePath)
                .build());
    }

    /**
     * 使用UUID生成随机文件名，防止重复
     */
    public String generateRandomFileName(String originalFileName) {
        String uuid = UUID.fastUUID().toString();
        String fileName = "files" + StrUtil.SLASH + uuid + StrUtil.SLASH + DateUtil.format(new Date(),"yyyy-MM-dd") + StrUtil.SLASH + originalFileName;
        return fileName;
    }
}
