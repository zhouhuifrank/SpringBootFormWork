package com.frankzhou.project.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.common.util.oss.OssFile;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description minIo对象存储控制器
 * @date 2023-07-21
 */
@Slf4j
@RestController
@RequestMapping("/minio")
@Api(tags = {"对象存储"})
public class OssController {

    @ApiOperation(value = "上传文件")
    @PostMapping("/upload/file")
    public ResultDTO<OssFile> uploadOssFile(MultipartFile file) throws IOException {
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://115.159.216.169:9000")
                .credentials("admin","monv205850")
                .build();

        InputStream inputStream = file.getInputStream();
        String originalFilename = file.getOriginalFilename();
        String bucketName = "test";
        // 生成随机文件名
        String uuidFileName = "frankzhou" + StrUtil.SLASH + UUID.fastUUID() + StrUtil.SLASH
                + DateUtil.format(new Date(),"yyyy-MM-dd") + StrUtil.SLASH + originalFilename;
        try {
            minioClient.putObject(PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(uuidFileName)
                            .stream(inputStream,inputStream.available(),-1)
                    .build());
            log.info("file upload success");
        } catch (Exception e) {
            log.error("minio occur error");
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        OssFile ossFile = OssFile.builder()
                .filePath(uuidFileName)
                .originalFileName(originalFilename)
                .build();
        return ResultDTO.getSuccessResult(ossFile);
    }

    @ApiOperation(value = "下载文件")
    @GetMapping("/download/file")
    public void downloadFile(String ossFilePath, HttpServletResponse response) {

    }

    @ApiOperation(value = "删除文件")
    @GetMapping("/delete/file")
    public ResultDTO<Boolean> deleteOssFile(@RequestParam("filePath") String ossFilePath) {
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://115.159.216.169:9000")
                .credentials("admin","monv205850")
                .build();

        String bucketName = "test";
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(ossFilePath).build());
        } catch (Exception e) {
            log.error("minio occur error");
        }

        return ResultDTO.getSuccessResult(Boolean.TRUE);
    }
}
