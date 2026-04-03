package com.cangqiong.takeaway.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface LocalFileStorageService {

    String storeImage(MultipartFile file);

    Resource loadAsResource(String fileName);
}
