package com.huacai.customer.controller;

import com.huacai.common.model.ApiResponse;
import com.huacai.common.model.PageResponse;
import com.huacai.customer.dto.CustomerScoreSaveRequest;
import com.huacai.customer.query.CustomerRiskPageQuery;
import com.huacai.customer.service.CustomerService;
import com.huacai.customer.vo.CustomerRiskVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "客户风险评估")
@RestController
@RequestMapping("/api/v1/customer-risks")
public class CustomerRiskController {

    private final CustomerService customerService;

    public CustomerRiskController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ApiResponse<PageResponse<CustomerRiskVO>> page(@ModelAttribute CustomerRiskPageQuery query) {
        return ApiResponse.success(customerService.pageRisks(query));
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody CustomerScoreSaveRequest request) {
        customerService.createRisk(request);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody CustomerScoreSaveRequest request) {
        customerService.updateRisk(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        customerService.deleteRisk(id);
        return ApiResponse.success();
    }
}
