package com.huacai.file.service;

import com.huacai.file.entity.SysFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileService {

    SysFile saveFile(MultipartFile file, String bizType, Long bizId, Long uploaderId) throws IOException;

    void deleteFile(Long fileId) throws IOException;
}
