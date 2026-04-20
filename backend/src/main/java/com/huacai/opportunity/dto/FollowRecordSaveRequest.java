package com.huacai.opportunity.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record FollowRecordSaveRequest(
        @NotNull(message = "商机ID不能为空")
        Long opportunityId,
        @NotNull(message = "跟进时间不能为空")
        LocalDateTime followTime,
        String followerName,
        @NotNull(message = "跟进详情不能为空")
        String followContent,
        String nextAction,
        String remark
) {
}
