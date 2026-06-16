package com.huacai.customer.controller;

import com.huacai.common.model.ApiResponse;
import com.huacai.common.model.PageResponse;
import com.huacai.common.model.StatusUpdateRequest;
import com.huacai.customer.dto.CustomerArchiveSaveRequest;
import com.huacai.customer.dto.CustomerContractSaveRequest;
import com.huacai.customer.dto.CustomerDebtSaveRequest;
import com.huacai.customer.dto.CustomerSaveRequest;
import com.huacai.customer.dto.CustomerScoreSaveRequest;
import com.huacai.customer.dto.CustomerStatusLogSaveRequest;
import com.huacai.customer.dto.CustomerTradeSaveRequest;
import com.huacai.customer.query.CustomerDebtPageQuery;
import com.huacai.customer.query.CustomerPageQuery;
import com.huacai.customer.query.CustomerRiskPageQuery;
import com.huacai.customer.service.CustomerService;
import com.huacai.customer.vo.CustomerArchiveVO;
import com.huacai.customer.vo.CustomerContractVO;
import com.huacai.customer.vo.CustomerDebtVO;
import com.huacai.customer.vo.CustomerRiskVO;
import com.huacai.customer.vo.CustomerStatusLogVO;
import com.huacai.customer.vo.CustomerTradeVO;
import com.huacai.customer.vo.CustomerVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "客户管理")
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ApiResponse<PageResponse<CustomerVO>> page(@ModelAttribute CustomerPageQuery query) {
        return ApiResponse.success(customerService.page(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<CustomerVO> detail(@PathVariable Long id) {
        return ApiResponse.success(customerService.detail(id));
    }

    @GetMapping("/{id}/archive")
    public ApiResponse<CustomerArchiveVO> detailArchive(@PathVariable Long id) {
        return ApiResponse.success(customerService.detailArchive(id));
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody CustomerSaveRequest request) {
        customerService.create(request);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody CustomerSaveRequest request) {
        customerService.update(id, request);
        return ApiResponse.success();
    }

    @PostMapping("/archive")
    public ApiResponse<Void> createArchive(@Valid @RequestBody CustomerArchiveSaveRequest request) {
        customerService.createArchive(request);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/archive")
    public ApiResponse<Void> updateArchive(@PathVariable Long id, @Valid @RequestBody CustomerArchiveSaveRequest request) {
        customerService.updateArchive(id, request);
        return ApiResponse.success();
    }

    @GetMapping("/risks")
    public ApiResponse<PageResponse<CustomerRiskVO>> pageRisks(@ModelAttribute CustomerRiskPageQuery query) {
        return ApiResponse.success(customerService.pageRisks(query));
    }

    @GetMapping("/debts")
    public ApiResponse<PageResponse<CustomerDebtVO>> pageDebts(@ModelAttribute CustomerDebtPageQuery query) {
        return ApiResponse.success(customerService.pageDebts(query));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        customerService.updateCustomerStatus(id, request.status(), request.statusName());
        return ApiResponse.success();
    }

    @GetMapping("/{customerId}/scores")
    public ApiResponse<List<CustomerRiskVO>> scores(@PathVariable Long customerId) {
        return ApiResponse.success(customerService.listRisksByCustomer(customerId));
    }

    @GetMapping("/{customerId}/scores/{id}")
    public ApiResponse<CustomerRiskVO> scoreDetail(@PathVariable Long customerId, @PathVariable Long id) {
        return ApiResponse.success(customerService.getRisk(id));
    }

    @PostMapping("/{customerId}/scores")
    public ApiResponse<Void> createScore(@PathVariable Long customerId, @Valid @RequestBody CustomerScoreSaveRequest request) {
        customerService.createRisk(new CustomerScoreSaveRequest(
                request.id(),
                customerId,
                request.testDate(),
                request.testLimit(),
                request.trafficValue(),
                request.compositeScore(),
                request.thirdPartyScore(),
                request.remark()
        ));
        return ApiResponse.success();
    }

    @PutMapping("/{customerId}/scores/{id}")
    public ApiResponse<Void> updateScore(@PathVariable Long customerId, @PathVariable Long id,
                                         @Valid @RequestBody CustomerScoreSaveRequest request) {
        customerService.updateRisk(id, new CustomerScoreSaveRequest(
                request.id(),
                customerId,
                request.testDate(),
                request.testLimit(),
                request.trafficValue(),
                request.compositeScore(),
                request.thirdPartyScore(),
                request.remark()
        ));
        return ApiResponse.success();
    }

    @DeleteMapping("/{customerId}/scores/{id}")
    public ApiResponse<Void> deleteScore(@PathVariable Long customerId, @PathVariable Long id) {
        customerService.deleteRisk(id);
        return ApiResponse.success();
    }

    @GetMapping("/{customerId}/status-logs")
    public ApiResponse<List<CustomerStatusLogVO>> statusLogs(@PathVariable Long customerId) {
        return ApiResponse.success(customerService.listStatusLogs(customerId));
    }

    @PostMapping("/{customerId}/status-logs")
    public ApiResponse<Void> createStatusLog(@PathVariable Long customerId,
                                             @Valid @RequestBody CustomerStatusLogSaveRequest request) {
        customerService.updateCustomerStatus(customerId, request.statusCode(), request.statusName());
        return ApiResponse.success();
    }

    @GetMapping("/{customerId}/debts")
    public ApiResponse<List<CustomerDebtVO>> debts(@PathVariable Long customerId) {
        return ApiResponse.success(customerService.listDebtsByCustomer(customerId));
    }

    @GetMapping("/{customerId}/debts/{id}")
    public ApiResponse<CustomerDebtVO> debtDetail(@PathVariable Long customerId, @PathVariable Long id) {
        return ApiResponse.success(customerService.getDebt(id));
    }

    @PostMapping("/{customerId}/debts")
    public ApiResponse<Void> createDebt(@PathVariable Long customerId, @Valid @RequestBody CustomerDebtSaveRequest request) {
        customerService.createDebt(new CustomerDebtSaveRequest(
                request.id(),
                customerId,
                request.debtType(),
                request.debtAmount(),
                request.totalRepaymentAmount(),
                request.advancePaidAmount(),
                request.pendingAmount(),
                request.installmentAmount(),
                request.repaymentDay(),
                request.remark()
        ));
        return ApiResponse.success();
    }

    @PutMapping("/{customerId}/debts/{id}")
    public ApiResponse<Void> updateDebt(@PathVariable Long customerId, @PathVariable Long id,
                                        @Valid @RequestBody CustomerDebtSaveRequest request) {
        customerService.updateDebt(id, new CustomerDebtSaveRequest(
                request.id(),
                customerId,
                request.debtType(),
                request.debtAmount(),
                request.totalRepaymentAmount(),
                request.advancePaidAmount(),
                request.pendingAmount(),
                request.installmentAmount(),
                request.repaymentDay(),
                request.remark()
        ));
        return ApiResponse.success();
    }

    @DeleteMapping("/{customerId}/debts/{id}")
    public ApiResponse<Void> deleteDebt(@PathVariable Long customerId, @PathVariable Long id) {
        customerService.deleteDebt(id);
        return ApiResponse.success();
    }

    @GetMapping("/{customerId}/contracts")
    public ApiResponse<List<CustomerContractVO>> contracts(@PathVariable Long customerId) {
        return ApiResponse.success(customerService.listContractsByCustomer(customerId));
    }

    @GetMapping("/{customerId}/contracts/{id}")
    public ApiResponse<CustomerContractVO> contractDetail(@PathVariable Long customerId, @PathVariable Long id) {
        return ApiResponse.success(customerService.getContract(id));
    }

    @PostMapping("/{customerId}/contracts")
    public ApiResponse<Void> createContract(@PathVariable Long customerId,
                                            @Valid @RequestBody CustomerContractSaveRequest request) {
        customerService.createContract(customerId, request);
        return ApiResponse.success();
    }

    @PutMapping("/{customerId}/contracts/{id}")
    public ApiResponse<Void> updateContract(@PathVariable Long customerId, @PathVariable Long id,
                                            @Valid @RequestBody CustomerContractSaveRequest request) {
        customerService.updateContract(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{customerId}/contracts/{id}")
    public ApiResponse<Void> deleteContract(@PathVariable Long customerId, @PathVariable Long id) {
        customerService.deleteContract(id);
        return ApiResponse.success();
    }

    @GetMapping("/{customerId}/trades")
    public ApiResponse<List<CustomerTradeVO>> trades(@PathVariable Long customerId) {
        return ApiResponse.success(customerService.listTradesByCustomer(customerId));
    }

    @GetMapping("/{customerId}/trades/{id}")
    public ApiResponse<CustomerTradeVO> tradeDetail(@PathVariable Long customerId, @PathVariable Long id) {
        return ApiResponse.success(customerService.getTrade(id));
    }

    @PostMapping("/{customerId}/trades")
    public ApiResponse<Void> createTrade(@PathVariable Long customerId, @Valid @RequestBody CustomerTradeSaveRequest request) {
        customerService.createTrade(customerId, request);
        return ApiResponse.success();
    }

    @PutMapping("/{customerId}/trades/{id}")
    public ApiResponse<Void> updateTrade(@PathVariable Long customerId, @PathVariable Long id,
                                         @Valid @RequestBody CustomerTradeSaveRequest request) {
        customerService.updateTrade(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{customerId}/trades/{id}")
    public ApiResponse<Void> deleteTrade(@PathVariable Long customerId, @PathVariable Long id) {
        customerService.deleteTrade(id);
        return ApiResponse.success();
    }

    @PostMapping("/import")
    public ApiResponse<Map<String, Object>> importCustomers() {
        throw new com.huacai.common.exception.BusinessException("客户数据导入功能规划中，暂不支持");
    }

    @GetMapping("/import/template")
    public ApiResponse<Map<String, Object>> importTemplate() {
        throw new com.huacai.common.exception.BusinessException("客户数据导入功能规划中，暂不支持");
    }

    @GetMapping("/export")
    public ApiResponse<Map<String, Object>> export() {
        throw new com.huacai.common.exception.BusinessException("客户数据导出功能规划中，暂不支持");
    }
}
