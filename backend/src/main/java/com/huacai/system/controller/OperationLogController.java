package com.huacai.system.controller;

import com.huacai.common.model.ApiResponse;
import com.huacai.common.model.PageResponse;
import com.huacai.system.query.OperationLogPageQuery;
import com.huacai.system.service.OperationLogService;
import com.huacai.system.vo.OperationLogVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "操作日志")
@RestController
@RequestMapping("/api/v1/system/operation-logs")
public class OperationLogController {

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @GetMapping
    public ApiResponse<PageResponse<OperationLogVO>> page(@ModelAttribute OperationLogPageQuery query) {
        return ApiResponse.success(operationLogService.page(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<OperationLogVO> detail(@PathVariable Long id) {
        return ApiResponse.success(operationLogService.detail(id));
    }
}