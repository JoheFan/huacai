package com.huacai.analysis.service;

import com.huacai.analysis.dto.*;
import com.huacai.analysis.query.*;
import com.huacai.analysis.vo.*;
import com.huacai.common.model.PageResponse;

public interface AnalysisService {

    // Increment Summary (增量总览)
    PageResponse<IncrementSummaryVO> pageIncrementSummary(IncrementSummaryQuery query);
    IncrementSummaryVO getIncrementSummaryDetail(Long id);
    void createIncrementSummary(IncrementSummarySaveDTO dto);
    void updateIncrementSummary(Long id, IncrementSummarySaveDTO dto);
    void deleteIncrementSummary(Long id);

    // Increment Detail (增量详情)
    PageResponse<IncrementDetailVO> pageIncrementDetail(IncrementDetailQuery query);
    IncrementDetailVO getIncrementDetailDetail(Long id);
    void createIncrementDetail(IncrementDetailSaveDTO dto);
    void updateIncrementDetail(Long id, IncrementDetailSaveDTO dto);
    void deleteIncrementDetail(Long id);

    // Daily Increment (日增量)
    PageResponse<DailyIncrementVO> pageDailyIncrement(DailyIncrementQuery query);
    DailyIncrementVO getDailyIncrementDetail(Long id);
    void createDailyIncrement(DailyIncrementSaveDTO dto);
    void updateDailyIncrement(Long id, DailyIncrementSaveDTO dto);
    void deleteDailyIncrement(Long id);

    // Employee Performance (员工绩效)
    PageResponse<EmployeePerformanceVO> pageEmployeePerformance(EmployeePerformanceQuery query);
    EmployeePerformanceVO getEmployeePerformanceDetail(Long id);
    void createEmployeePerformance(EmployeePerformanceSaveDTO dto);
    void updateEmployeePerformance(Long id, EmployeePerformanceSaveDTO dto);
    void deleteEmployeePerformance(Long id);
}
