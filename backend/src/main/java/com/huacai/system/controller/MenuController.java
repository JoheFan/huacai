package com.huacai.system.controller;

import com.huacai.common.exception.BusinessException;
import com.huacai.common.model.ApiResponse;
import com.huacai.system.dto.MenuSaveRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "菜单权限")
@RestController
@RequestMapping("/api/v1/system/menus")
public class MenuController {

    @GetMapping("/tree")
    public ApiResponse<List<Map<String, Object>>> tree() {
        return ApiResponse.success(List.of(Map.of(
                "id", 1L,
                "menuName", "工作台",
                "menuType", "MENU",
                "routePath", "/dashboard"
        )));
    }

    @GetMapping("/routes")
    public ApiResponse<List<Map<String, Object>>> routes() {
        return tree();
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody MenuSaveRequest request) {
        throw new BusinessException("菜单管理功能规划中，暂不支持创建");
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody MenuSaveRequest request) {
        throw new BusinessException("菜单管理功能规划中，暂不支持更新");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        throw new BusinessException("菜单管理功能规划中，暂不支持删除");
    }
}
