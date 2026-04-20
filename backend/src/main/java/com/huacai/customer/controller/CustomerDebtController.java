package com.huacai.customer.controller;

import com.huacai.common.model.ApiResponse;
import com.huacai.common.model.PageResponse;
import com.huacai.customer.dto.CustomerDebtSaveRequest;
import com.huacai.customer.query.CustomerDebtPageQuery;
import com.huacai.customer.service.CustomerService;
import com.huacai.customer.vo.CustomerDebtVO;
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

@Tag(name = "客户负债登记")
@RestController
@RequestMapping("/api/v1/customer-debts")
public class CustomerDebtController {

    private final CustomerService customerService;

    public CustomerDebtController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ApiResponse<PageResponse<CustomerDebtVO>> page(@ModelAttribute CustomerDebtPageQuery query) {
        return ApiResponse.success(customerService.pageDebts(query));
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody CustomerDebtSaveRequest request) {
        customerService.createDebt(request);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody CustomerDebtSaveRequest request) {
        customerService.updateDebt(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        customerService.deleteDebt(id);
        return ApiResponse.success();
    }
}
