package com.huacai.workbench.controller;

import com.huacai.common.model.ApiResponse;
import com.huacai.workbench.query.WorkbenchQuery;
import com.huacai.workbench.service.WorkbenchService;
import com.huacai.workbench.vo.WorkbenchOverviewVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "工作台")
@RestController
@RequestMapping("/api/v1/workbench")
public class WorkbenchController {

    private final WorkbenchService workbenchService;

    public WorkbenchController(WorkbenchService workbenchService) {
        this.workbenchService = workbenchService;
    }

    @GetMapping("/overview")
    public ApiResponse<WorkbenchOverviewVO> overview(@ModelAttribute WorkbenchQuery query) {
        return ApiResponse.success(workbenchService.overview(query));
    }
}
