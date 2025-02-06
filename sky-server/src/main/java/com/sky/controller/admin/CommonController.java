package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;


/**
 * 公共接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "公共接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @ApiOperation("上传文件")
    public Result<String> upload(MultipartFile file) {
        log.info("上传文件：{}", file.getOriginalFilename());
        //原始文件名
        String originalFilename = file.getOriginalFilename();
        //截取原始文件名的后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //通过UUID生成
        String objectName = UUID.randomUUID().toString() + suffix;
        //上传到阿里云服务器
        String filePath = null;
        try {
            filePath = aliOssUtil.upload(file.getBytes(), objectName);
        } catch (IOException e) {
            log.error("上传文件失败：{}", e.getMessage());
        }
        return Result.success(filePath);
    }
}
