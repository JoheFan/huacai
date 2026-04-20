package com.huacai.workbench.vo;

public record WorkbenchRecordVO(
        Long id,
        String customerName,
        String recordType,
        String relationInfo,
        String actionDate,
        String status,
        String priority,
        String routePath
) {
}
