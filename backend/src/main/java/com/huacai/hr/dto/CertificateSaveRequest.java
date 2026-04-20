package com.huacai.hr.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CertificateSaveRequest(
    Long id,
    @NotNull(message = "员工ID不能为空")
    Long employeeId,
    @NotNull(message = "证书名称不能为空")
    String certificateName,
    String certificateNo,
    LocalDate issueDate,
    String certificateType,
    String issueOrg,
    Integer isPermanent,
    LocalDate expireDate,
    String certificateFileUrl
) {
}
