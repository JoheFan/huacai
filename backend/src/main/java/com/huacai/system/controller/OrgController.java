package com.huacai.system.controller;

import com.huacai.common.model.ApiResponse;
import com.huacai.common.model.StatusUpdateRequest;
import com.huacai.system.dto.OrgCreateRequest;
import com.huacai.system.dto.OrgUpdateRequest;
import com.huacai.system.service.SystemManageService;
import com.huacai.system.vo.OrgVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "组织管理")
@RestController
@RequestMapping("/api/v1/system/orgs")
public class OrgController {

    private final SystemManageService systemManageService;

    public OrgController(SystemManageService systemManageService) {
        this.systemManageService = systemManageService;
    }

    @GetMapping("/tree")
    public ApiResponse<List<OrgVO>> tree() {
        return ApiResponse.success(systemManageService.getOrgTree());
    }

    @GetMapping("/{id}")
    public ApiResponse<OrgVO> detail(@PathVariable Long id) {
        return ApiResponse.success(systemManageService.getOrgDetail(id));
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody OrgCreateRequest request) {
        systemManageService.createOrg(request);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody OrgUpdateRequest request) {
        systemManageService.updateOrg(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        systemManageService.deleteOrg(id);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        systemManageService.updateOrgStatus(id, request.status());
        return ApiResponse.success();
    }
}
