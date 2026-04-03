package com.cangqiong.takeaway.service.impl;

import com.cangqiong.takeaway.config.properties.UploadProperties;
import com.cangqiong.takeaway.exception.BusinessException;
import com.cangqiong.takeaway.service.LocalFileStorageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

@Service
public class LocalFileStorageServiceImpl implements LocalFileStorageService {

    @Autowired
    private UploadProperties uploadProperties;

    private Path storageRoot;

    @PostConstruct
    public void init() {
        storageRoot = Paths.get(uploadProperties.getStoragePath()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(storageRoot);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to initialize upload storage directory: " + storageRoot, e);
        }
    }

    @Override
    public String storeImage(MultipartFile file) {
        byte[] fileBytes = validateAndReadFile(file);

        String extension = getExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        Path targetPath = resolveSafePath(fileName);

        try {
            Files.write(targetPath, fileBytes);
        } catch (IOException e) {
            throw new BusinessException("文件上传失败");
        }

        return buildPublicUrl(fileName);
    }

    @Override
    public Resource loadAsResource(String fileName) {
        validateRequestedFileName(fileName);
        Path targetPath = resolveSafePath(fileName);
        if (!Files.exists(targetPath) || !Files.isRegularFile(targetPath)) {
            throw new BusinessException(404, "文件不存在");
        }

        return new FileSystemResource(targetPath);
    }

    private byte[] validateAndReadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }
        if (file.getSize() > uploadProperties.getMaxSizeBytes()) {
            throw new BusinessException("文件大小超过限制");
        }

        String extension = getExtension(file.getOriginalFilename());
        if (!uploadProperties.getAllowedExtensions().contains(extension)) {
            throw new BusinessException("不支持的文件扩展名");
        }

        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!uploadProperties.getAllowedContentTypes().contains(contentType)) {
            throw new BusinessException("不支持的文件类型");
        }

        try {
            byte[] fileBytes = file.getBytes();
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes));
            if (image == null) {
                throw new BusinessException("上传内容不是有效图片");
            }
            return fileBytes;
        } catch (IOException e) {
            throw new BusinessException("读取上传文件失败");
        }
    }

    private String getExtension(String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);
        if (extension == null || extension.isBlank()) {
            throw new BusinessException("文件缺少扩展名");
        }
        return extension.toLowerCase(Locale.ROOT);
    }

    private String buildPublicUrl(String fileName) {
        String baseUrl = uploadProperties.getPublicBaseUrl();
        if (!baseUrl.endsWith("/")) {
            baseUrl = baseUrl + "/";
        }
        return baseUrl + fileName;
    }

    private Path resolveSafePath(String fileName) {
        Path targetPath = storageRoot.resolve(fileName).normalize();
        if (!targetPath.startsWith(storageRoot)) {
            throw new BusinessException("非法文件路径");
        }
        return targetPath;
    }

    private void validateRequestedFileName(String fileName) {
        if (fileName == null || !fileName.matches("^[a-f0-9]{32}\\.(jpg|jpeg|png|webp)$")) {
            throw new BusinessException(404, "文件不存在");
        }
    }
}
