package com.huacai.file.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huacai.common.exception.BusinessException;
import com.huacai.common.model.ApiResponse;
import com.huacai.file.entity.SysFile;
import com.huacai.file.mapper.FileMapper;
import com.huacai.file.service.FileService;
import com.huacai.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "文件管理")
@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final FileService fileService;
    private final FileMapper fileMapper;
    private final CurrentUserProvider currentUserProvider;
    private final Path localStorageDir;

    public FileController(
            FileService fileService,
            FileMapper fileMapper,
            CurrentUserProvider currentUserProvider,
            @Value("${huacai.file.local-dir:${java.io.tmpdir}/huacai-files}") String localStorageDir
    ) {
        this.fileService = fileService;
        this.fileMapper = fileMapper;
        this.currentUserProvider = currentUserProvider;
        this.localStorageDir = Paths.get(localStorageDir);
    }

    @PostMapping("/upload")
    public ApiResponse<Map<String, Object>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "bizType", required = false) String bizType,
            @RequestParam(value = "bizId", required = false) Long bizId
    ) throws IOException {
        Long uploaderId = currentUserProvider.getCurrentUserId();
        SysFile savedFile = fileService.saveFile(file, bizType, bizId, uploaderId);
        return ApiResponse.success(Map.of(
                "id", savedFile.getId(),
                "fileName", savedFile.getFileName(),
                "bizType", savedFile.getBizType() == null ? "" : savedFile.getBizType(),
                "bizId", savedFile.getBizId() == null ? 0L : savedFile.getBizId()
        ));
    }

    @GetMapping("/list")
    public ApiResponse<List<Map<String, Object>>> listByBiz(
            @RequestParam(value = "bizType", required = false) String bizType,
            @RequestParam(value = "bizId", required = false) Long bizId
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        if (currentUserId == null) {
            throw new IllegalArgumentException("未登录");
        }
        QueryWrapper<SysFile> wrapper = new QueryWrapper<>();
        wrapper.eq("uploader_id", currentUserId);
        if (StringUtils.hasText(bizType)) {
            wrapper.eq("biz_type", bizType.trim());
        }
        if (bizId != null) {
            wrapper.eq("biz_id", bizId);
        }
        wrapper.orderByDesc("created_at");
        List<SysFile> fileList = fileMapper.selectList(wrapper);
        List<Map<String, Object>> records = fileList.stream().map(f -> Map.<String, Object>of(
                "id", f.getId(),
                "fileName", f.getFileName(),
                "fileExt", f.getFileExt() == null ? "" : f.getFileExt(),
                "fileSize", f.getFileSize() == null ? 0L : f.getFileSize(),
                "bizType", f.getBizType() == null ? "" : f.getBizType(),
                "bizId", f.getBizId() == null ? 0L : f.getBizId()
        )).collect(Collectors.toList());
        return ApiResponse.success(records);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> detail(@PathVariable Long id) {
        SysFile file = getFileOrThrow(id);
        Long currentUserId = currentUserProvider.getCurrentUserId();
        if (currentUserId == null || !currentUserId.equals(file.getUploaderId())) {
            throw new BusinessException("无权访问此文件");
        }
        return ApiResponse.success(Map.of(
                "id", file.getId(),
                "fileName", file.getFileName(),
                "fileExt", file.getFileExt() == null ? "" : file.getFileExt(),
                "fileSize", file.getFileSize() == null ? 0L : file.getFileSize(),
                "bizType", file.getBizType() == null ? "" : file.getBizType(),
                "bizId", file.getBizId() == null ? 0L : file.getBizId()
        ));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) throws IOException {
        SysFile file = getFileOrThrow(id);
        Long currentUserId = currentUserProvider.getCurrentUserId();
        if (currentUserId == null || !currentUserId.equals(file.getUploaderId())) {
            throw new BusinessException("无权下载此文件");
        }
        Path filePath = Paths.get(file.getFilePath());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFileName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(Files.readAllBytes(filePath));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) throws IOException {
        SysFile file = getFileOrThrow(id);
        Long currentUserId = currentUserProvider.getCurrentUserId();
        if (currentUserId == null || !currentUserId.equals(file.getUploaderId())) {
            throw new BusinessException("无权删除此文件");
        }
        fileService.deleteFile(id);
        return ApiResponse.success();
    }

    private SysFile getFileOrThrow(Long id) {
        SysFile file = fileMapper.selectOne(new QueryWrapper<SysFile>().eq("id", id));
        if (file == null) {
            throw new BusinessException("文件不存在");
        }
        return file;
    }
}
