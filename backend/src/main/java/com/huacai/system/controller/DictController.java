package com.huacai.system.controller;

import com.huacai.common.exception.BusinessException;
import com.huacai.common.model.ApiResponse;
import com.huacai.common.model.PageQuery;
import com.huacai.common.model.PageResponse;
import com.huacai.system.dto.DictItemSaveRequest;
import com.huacai.system.dto.DictTypeSaveRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "数据字典")
@RestController
@RequestMapping("/api/v1/system")
public class DictController {

    @GetMapping("/dicts/{dictCode}/items")
    public ApiResponse<List<Map<String, Object>>> listItemsByCode(@PathVariable String dictCode) {
        return ApiResponse.success(List.of(Map.of(
                "itemCode", "ENABLED",
                "itemName", "启用",
                "itemValue", "ENABLED"
        )));
    }

    @GetMapping("/dict-types")
    public ApiResponse<PageResponse<Map<String, Object>>> listTypes(@ModelAttribute PageQuery query) {
        return ApiResponse.success(PageResponse.empty(query));
    }

    @PostMapping("/dict-types")
    public ApiResponse<Void> createType(@Valid @RequestBody DictTypeSaveRequest request) {
        throw new BusinessException("字典类型管理功能规划中，暂不支持创建");
    }

    @PutMapping("/dict-types/{id}")
    public ApiResponse<Void> updateType(@PathVariable Long id, @Valid @RequestBody DictTypeSaveRequest request) {
        throw new BusinessException("字典类型管理功能规划中，暂不支持更新");
    }

    @GetMapping("/dict-items")
    public ApiResponse<PageResponse<Map<String, Object>>> listItems(@ModelAttribute PageQuery query) {
        return ApiResponse.success(PageResponse.empty(query));
    }

    @PostMapping("/dict-items")
    public ApiResponse<Void> createItem(@Valid @RequestBody DictItemSaveRequest request) {
        throw new BusinessException("字典项管理功能规划中，暂不支持创建");
    }

    @PutMapping("/dict-items/{id}")
    public ApiResponse<Void> updateItem(@PathVariable Long id, @Valid @RequestBody DictItemSaveRequest request) {
        throw new BusinessException("字典项管理功能规划中，暂不支持更新");
    }
}
