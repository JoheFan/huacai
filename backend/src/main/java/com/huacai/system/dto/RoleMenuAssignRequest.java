package com.huacai.system.dto;

import java.util.List;

public record RoleMenuAssignRequest(
        List<Long> menuIds
) {
}
