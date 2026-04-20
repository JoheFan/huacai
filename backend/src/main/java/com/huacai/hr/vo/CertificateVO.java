package com.huacai.hr.vo;

import java.time.LocalDate;

public record CertificateVO(
    Long id,
    Long employeeId,
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
