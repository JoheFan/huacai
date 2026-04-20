package com.huacai.system.vo;

import java.time.LocalDateTime;
import java.util.List;

public record OrgVO(
    Long id,
    Long parentId,
    String parentName,
    String orgName,
    String orgType,
    Integer sortNo,
    String status,
    String remark,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<OrgVO> children
) {
}
