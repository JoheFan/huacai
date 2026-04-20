package com.huacai.customer.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CustomerContractVO(
        Long id,
        Long customerId,
        String customerName,
        String companyName,
        String creditCode,
        String contractNo,
        String contractName,
        List<CustomerAttachmentVO> attachments,
        LocalDate signDate,
        String remark,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
