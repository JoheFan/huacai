package com.huacai.opportunity.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OpportunitySaveRequest(
        Long customerId,
        @Size(max = 32, message = "优先级长度不能超过32")
        String priorityLevel,
        @Size(max = 32, message = "阶段编码长度不能超过32")
        String stageCode,
        Long ownerUserId,
        @DecimalMin(value = "0", message = "预估金额不能为负数")
        BigDecimal estimatedAmount,
        @Size(max = 32, message = "意向等级长度不能超过32")
        String intentLevel,
        @Size(max = 32, message = "状态长度不能超过32")
        String status,
        LocalDateTime nextFollowTime,
        @Size(max = 1000, message = "备注长度不能超过1000")
        String remark
) {
}
