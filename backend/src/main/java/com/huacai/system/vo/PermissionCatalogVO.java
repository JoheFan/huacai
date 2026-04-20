package com.huacai.system.vo;

import java.util.List;

public record PermissionCatalogVO(
        List<PermissionItemVO> pageItems,
        List<PermissionItemVO> buttonItems
) {
}
