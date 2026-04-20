package com.huacai.analysis.controller;

import com.huacai.analysis.dto.*;
import com.huacai.analysis.query.*;
import com.huacai.analysis.service.AnalysisService;
import com.huacai.analysis.vo.*;
import com.huacai.common.model.ApiResponse;
import com.huacai.common.model.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "经营分析")
@RestController
@RequestMapping("/api/v1/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    // ===================== Increment Summary =====================

    // Alias for frontend: /increment-overview -> /increment-summary
    @GetMapping("/increment-overview")
    public ApiResponse<PageResponse<IncrementSummaryVO>> pageIncrementOverview(@ModelAttribute IncrementSummaryQuery query) {
        return ApiResponse.success(analysisService.pageIncrementSummary(query));
    }

    @GetMapping("/increment-summary")
    public ApiResponse<PageResponse<IncrementSummaryVO>> pageIncrementSummary(@ModelAttribute IncrementSummaryQuery query) {
        return ApiResponse.success(analysisService.pageIncrementSummary(query));
    }

    @GetMapping("/increment-summary/{id}")
    public ApiResponse<IncrementSummaryVO> getIncrementSummaryDetail(@PathVariable Long id) {
        return ApiResponse.success(analysisService.getIncrementSummaryDetail(id));
    }

    @PostMapping("/increment-summary")
    public ApiResponse<Void> createIncrementSummary(@Valid @RequestBody IncrementSummarySaveDTO dto) {
        analysisService.createIncrementSummary(dto);
        return ApiResponse.success();
    }

    @PutMapping("/increment-summary/{id}")
    public ApiResponse<Void> updateIncrementSummary(@PathVariable Long id, @Valid @RequestBody IncrementSummarySaveDTO dto) {
        analysisService.updateIncrementSummary(id, dto);
        return ApiResponse.success();
    }

    @DeleteMapping("/increment-summary/{id}")
    public ApiResponse<Void> deleteIncrementSummary(@PathVariable Long id) {
        analysisService.deleteIncrementSummary(id);
        return ApiResponse.success();
    }

    // ===================== Increment Detail =====================

    @GetMapping("/increment-detail")
    public ApiResponse<PageResponse<IncrementDetailVO>> pageIncrementDetail(@ModelAttribute IncrementDetailQuery query) {
        return ApiResponse.success(analysisService.pageIncrementDetail(query));
    }

    @GetMapping("/increment-detail/{id}")
    public ApiResponse<IncrementDetailVO> getIncrementDetailDetail(@PathVariable Long id) {
        return ApiResponse.success(analysisService.getIncrementDetailDetail(id));
    }

    @PostMapping("/increment-detail")
    public ApiResponse<Void> createIncrementDetail(@Valid @RequestBody IncrementDetailSaveDTO dto) {
        analysisService.createIncrementDetail(dto);
        return ApiResponse.success();
    }

    @PutMapping("/increment-detail/{id}")
    public ApiResponse<Void> updateIncrementDetail(@PathVariable Long id, @Valid @RequestBody IncrementDetailSaveDTO dto) {
        analysisService.updateIncrementDetail(id, dto);
        return ApiResponse.success();
    }

    @DeleteMapping("/increment-detail/{id}")
    public ApiResponse<Void> deleteIncrementDetail(@PathVariable Long id) {
        analysisService.deleteIncrementDetail(id);
        return ApiResponse.success();
    }

    // ===================== Daily Increment =====================

    @GetMapping("/daily-increment")
    public ApiResponse<PageResponse<DailyIncrementVO>> pageDailyIncrement(@ModelAttribute DailyIncrementQuery query) {
        return ApiResponse.success(analysisService.pageDailyIncrement(query));
    }

    @GetMapping("/daily-increment/{id}")
    public ApiResponse<DailyIncrementVO> getDailyIncrementDetail(@PathVariable Long id) {
        return ApiResponse.success(analysisService.getDailyIncrementDetail(id));
    }

    @PostMapping("/daily-increment")
    public ApiResponse<Void> createDailyIncrement(@Valid @RequestBody DailyIncrementSaveDTO dto) {
        analysisService.createDailyIncrement(dto);
        return ApiResponse.success();
    }

    @PutMapping("/daily-increment/{id}")
    public ApiResponse<Void> updateDailyIncrement(@PathVariable Long id, @Valid @RequestBody DailyIncrementSaveDTO dto) {
        analysisService.updateDailyIncrement(id, dto);
        return ApiResponse.success();
    }

    @DeleteMapping("/daily-increment/{id}")
    public ApiResponse<Void> deleteDailyIncrement(@PathVariable Long id) {
        analysisService.deleteDailyIncrement(id);
        return ApiResponse.success();
    }

    // Alias for frontend: /performance -> /employee-performance
    @GetMapping("/performance")
    public ApiResponse<PageResponse<EmployeePerformanceVO>> pagePerformance(@ModelAttribute EmployeePerformanceQuery query) {
        return ApiResponse.success(analysisService.pageEmployeePerformance(query));
    }

    @GetMapping("/employee-performance")
    public ApiResponse<PageResponse<EmployeePerformanceVO>> pageEmployeePerformance(@ModelAttribute EmployeePerformanceQuery query) {
        return ApiResponse.success(analysisService.pageEmployeePerformance(query));
    }

    @GetMapping("/employee-performance/{id}")
    public ApiResponse<EmployeePerformanceVO> getEmployeePerformanceDetail(@PathVariable Long id) {
        return ApiResponse.success(analysisService.getEmployeePerformanceDetail(id));
    }

    @PostMapping("/employee-performance")
    public ApiResponse<Void> createEmployeePerformance(@Valid @RequestBody EmployeePerformanceSaveDTO dto) {
        analysisService.createEmployeePerformance(dto);
        return ApiResponse.success();
    }

    @PutMapping("/employee-performance/{id}")
    public ApiResponse<Void> updateEmployeePerformance(@PathVariable Long id, @Valid @RequestBody EmployeePerformanceSaveDTO dto) {
        analysisService.updateEmployeePerformance(id, dto);
        return ApiResponse.success();
    }

    @DeleteMapping("/employee-performance/{id}")
    public ApiResponse<Void> deleteEmployeePerformance(@PathVariable Long id) {
        analysisService.deleteEmployeePerformance(id);
        return ApiResponse.success();
    }
}
