package com.huacai.system.service;

import com.huacai.common.model.PageResponse;
import com.huacai.system.query.OperationLogPageQuery;
import com.huacai.system.vo.OperationLogVO;

public interface OperationLogService {

    PageResponse<OperationLogVO> page(OperationLogPageQuery query);

    OperationLogVO detail(Long id);

    void writeLog(OperationLogContent content);

    record OperationLogContent(
            Long userId,
            String userName,
            String moduleCode,
            String bizType,
            String actionCode,
            String actionDesc,
            String targetType,
            Long targetId,
            String requestUri,
            String requestMethod,
            String resultCode,
            String resultMsg,
            String ip
    ) {}
}