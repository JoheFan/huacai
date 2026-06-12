package com.huacai.assistant.dto;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record AssistantFollowRecordPatch(
        LocalDateTime followTime,
        @Size(max = 100, message = "followerName长度不能超过100")
        String followerName,
        @Size(max = 2000, message = "followContent长度不能超过2000")
        String followContent,
        @Size(max = 500, message = "nextAction长度不能超过500")
        String nextAction,
        @Size(max = 500, message = "remark长度不能超过500")
        String remark
) {

    public boolean isEmpty() {
        return followTime == null
                && followerName == null
                && followContent == null
                && nextAction == null
                && remark == null;
    }
}
