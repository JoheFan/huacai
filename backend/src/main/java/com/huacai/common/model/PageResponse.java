package com.huacai.common.model;

import java.util.List;

public record PageResponse<T>(
        List<T> records,
        long total,
        int pageNum,
        int pageSize
) {

    public static <T> PageResponse<T> empty(PageQuery query) {
        int pageNum = query.getPageNum() == null ? 1 : query.getPageNum();
        int pageSize = query.getPageSize() == null ? 20 : query.getPageSize();
        return new PageResponse<>(List.of(), 0, pageNum, pageSize);
    }
}
