package com.frankzhou.project.Util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.frankzhou.project.common.util.oss.MinIoTemplate;
import com.frankzhou.project.common.util.oss.OssProperties;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 对象存储测试
 * @date 2023-07-21
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class OssTests {

    @Test
    public void testMinIoClient() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://115.159.216.169:9000")
                .credentials("admin","monv205850")
                .build();

        boolean isExists =  minioClient.bucketExists(BucketExistsArgs.builder().bucket("test").build());
        if (isExists) {
            log.info("test bucket is exists");
        } else {
            log.info("test bucket is not exists");
        }
    }

    @Test
    public void testMakeBucket() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://115.159.216.169:9000")
                .credentials("admin","monv205850")
                .build();

        try {
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket("default").build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("default").build());
                log.info("defalut bucket create successfully");
            }

            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket("block-bucket").build())) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                                .bucket("block-bucket")
                                .region("eu-west-1")
                                .objectLock(true)
                        .build());
                log.info("block-bucket create successfully");
            }
        } catch (Exception e) {
            log.error("minio occur error");
        }
    }
    
    @Test
    public void testListBucket() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://115.159.216.169:9000")
                .credentials("admin","monv205850")
                .build();

        List<Bucket> bucketList = minioClient.listBuckets();
        for (Bucket bucket : bucketList) {
            log.info("create date:{} name:{}",bucket.creationDate(),bucket.name());
        }
    }

    @Test
    public void testRemoveBucket() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://115.159.216.169:9000")
                .credentials("admin","monv205850")
                .build();

        // 删除空桶 如果不空则会报错
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket("block-bucket").build());
    }

    @Test
    public void testPutObject() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://115.159.216.169:9000")
                .credentials("admin","monv205850")
                .build();

            String bucketName = "test";
            String ossFilePath = "dog.jpgju";
            if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                InputStream inputStream = (InputStream)  minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(ossFilePath).build());

                String uuidFileName = "frankzhou" + StrUtil.SLASH + UUID.fastUUID()
                        + StrUtil.SLASH + DateUtil.format(new Date(),"yyyy-MM-dd") + StrUtil.SLASH + "dog.jpg";
                minioClient.putObject(PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(uuidFileName)
                                .stream(inputStream,inputStream.available(),-1)
                        .build());
            }
    }
}
