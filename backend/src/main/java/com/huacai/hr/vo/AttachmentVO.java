package com.huacai.hr.vo;

import java.time.LocalDateTime;

public record AttachmentVO(
    Long id,
    String bizType,
    Long bizId,
    String fileName,
    Long fileSize,
    String fileUrl,
    String fileType,
    Long uploaderId,
    String uploaderName,
    LocalDateTime uploadTime
) {
}
