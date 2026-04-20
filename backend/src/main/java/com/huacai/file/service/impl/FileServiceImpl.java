package com.huacai.file.service.impl;

import com.huacai.file.entity.SysFile;
import com.huacai.file.mapper.FileMapper;
import com.huacai.file.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private final FileMapper fileMapper;
    private final Path localStorageDir;

    public FileServiceImpl(
            FileMapper fileMapper,
            @Value("${huacai.file.local-dir:${java.io.tmpdir}/huacai-files}") String localStorageDir
    ) {
        this.fileMapper = fileMapper;
        this.localStorageDir = Paths.get(localStorageDir);
    }

    @Override
    public SysFile saveFile(MultipartFile file, String bizType, Long bizId, Long uploaderId) throws IOException {
        Files.createDirectories(localStorageDir);
        String fileName = StringUtils.hasText(file.getOriginalFilename()) ? file.getOriginalFilename() : "unknown";
        String fileExt = extractFileExt(fileName);
        String storedName = UUID.randomUUID().toString().replace("-", "") + (StringUtils.hasText(fileExt) ? "." + fileExt : "");
        Path storedPath = localStorageDir.resolve(storedName);
        file.transferTo(storedPath.toFile());

        SysFile entity = new SysFile();
        entity.setBizType(StringUtils.hasText(bizType) ? bizType.trim() : null);
        entity.setBizId(bizId);
        entity.setFileName(fileName);
        entity.setFilePath(storedPath.toString());
        entity.setFileExt(fileExt);
        entity.setFileSize(file.getSize());
        entity.setStorageType("LOCAL");
        entity.setUploaderId(uploaderId);
        fileMapper.insert(entity);
        return entity;
    }

    @Override
    public void deleteFile(Long fileId) throws IOException {
        SysFile file = fileMapper.selectById(fileId);
        if (file != null) {
            fileMapper.deleteById(fileId);
            if (StringUtils.hasText(file.getFilePath())) {
                Files.deleteIfExists(Paths.get(file.getFilePath()));
            }
        }
    }

    private String extractFileExt(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index < 0 || index == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(index + 1).trim().toLowerCase();
    }
}
