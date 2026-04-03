package com.cangqiong.takeaway.controller;

import com.cangqiong.takeaway.service.LocalFileStorageService;
import com.cangqiong.takeaway.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 * 处理图片等文件的上传操作
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private LocalFileStorageService localFileStorageService;

    /**
     * 上传图片
     * @param file 上传的文件
     * @return 上传后的文件访问 URL
     */
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }

        return Result.success(localFileStorageService.storeImage(file));
    }
}
