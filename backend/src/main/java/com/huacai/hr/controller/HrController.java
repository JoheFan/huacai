package com.huacai.hr.controller;

import com.huacai.common.model.ApiResponse;
import com.huacai.common.model.PageResponse;
import com.huacai.hr.dto.*;
import com.huacai.hr.query.*;
import com.huacai.hr.service.HrManageService;
import com.huacai.hr.vo.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hr")
public class HrController {

    private final HrManageService hrManageService;

    public HrController(HrManageService hrManageService) {
        this.hrManageService = hrManageService;
    }

    @GetMapping("/employees")
    public ApiResponse<PageResponse<EmployeeVO>> pageEmployees(EmployeePageQuery query) {
        return ApiResponse.success(hrManageService.pageEmployees(query));
    }

    @GetMapping("/employees/{id}")
    public ApiResponse<EmployeeDetailVO> getEmployeeDetail(@PathVariable Long id) {
        return ApiResponse.success(hrManageService.getEmployeeDetail(id));
    }

    @PostMapping("/employees")
    public ApiResponse<Void> createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        hrManageService.createEmployee(request);
        return ApiResponse.success();
    }

    @PutMapping("/employees/{id}")
    public ApiResponse<Void> updateEmployee(@PathVariable Long id, @RequestBody EmployeeUpdateRequest request) {
        hrManageService.updateEmployee(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/employees/{id}")
    public ApiResponse<Void> deleteEmployee(@PathVariable Long id) {
        hrManageService.deleteEmployee(id);
        return ApiResponse.success();
    }

    @GetMapping("/my-profile")
    public ApiResponse<EmployeeDetailVO> getMyProfile() {
        return ApiResponse.success(hrManageService.getEmployeeDetail(hrManageService.getCurrentEmployeeId()));
    }

    @PutMapping("/my-profile")
    public ApiResponse<Void> updateMyProfile(@RequestBody MyProfileUpdateRequest request) {
        hrManageService.updateMyProfile(request);
        return ApiResponse.success();
    }

    @GetMapping("/employees/{employeeId}/job-info")
    public ApiResponse<JobInfoVO> getJobInfo(@PathVariable Long employeeId) {
        return ApiResponse.success(hrManageService.getJobInfo(employeeId));
    }

    @PostMapping("/job-info")
    public ApiResponse<Void> saveJobInfo(@Valid @RequestBody JobInfoSaveRequest request) {
        hrManageService.saveJobInfo(request);
        return ApiResponse.success();
    }

    @PostMapping("/removal")
    public ApiResponse<Void> saveRemoval(@Valid @RequestBody RemovalSaveRequest request) {
        hrManageService.saveRemoval(request);
        return ApiResponse.success();
    }

    @PostMapping("/certificates")
    public ApiResponse<Void> saveCertificate(@Valid @RequestBody CertificateSaveRequest request) {
        hrManageService.saveCertificate(request);
        return ApiResponse.success();
    }

    @DeleteMapping("/certificates/{id}")
    public ApiResponse<Void> deleteCertificate(@PathVariable Long id) {
        hrManageService.deleteCertificate(id);
        return ApiResponse.success();
    }

    @PostMapping("/assessments")
    public ApiResponse<Void> saveAssessment(@Valid @RequestBody AssessmentSaveRequest request) {
        hrManageService.saveAssessment(request);
        return ApiResponse.success();
    }

    @DeleteMapping("/assessments/{id}")
    public ApiResponse<Void> deleteAssessment(@PathVariable Long id) {
        hrManageService.deleteAssessment(id);
        return ApiResponse.success();
    }

    @PostMapping("/growth")
    public ApiResponse<Void> saveGrowth(@Valid @RequestBody GrowthSaveRequest request) {
        hrManageService.saveGrowth(request);
        return ApiResponse.success();
    }

    @DeleteMapping("/growth/{id}")
    public ApiResponse<Void> deleteGrowth(@PathVariable Long id) {
        hrManageService.deleteGrowth(id);
        return ApiResponse.success();
    }

    @PostMapping("/family")
    public ApiResponse<Void> saveFamily(@Valid @RequestBody FamilySaveRequest request) {
        hrManageService.saveFamily(request);
        return ApiResponse.success();
    }

    @DeleteMapping("/family/{id}")
    public ApiResponse<Void> deleteFamily(@PathVariable Long id) {
        hrManageService.deleteFamily(id);
        return ApiResponse.success();
    }

    @PostMapping("/changes")
    public ApiResponse<Void> saveChange(@Valid @RequestBody ChangeSaveRequest request) {
        hrManageService.saveChange(request);
        return ApiResponse.success();
    }

    @PostMapping("/contracts")
    public ApiResponse<Void> saveContract(@Valid @RequestBody ContractSaveRequest request) {
        hrManageService.saveContract(request);
        return ApiResponse.success();
    }

    @DeleteMapping("/contracts/{id}")
    public ApiResponse<Void> deleteContract(@PathVariable Long id) {
        hrManageService.deleteContract(id);
        return ApiResponse.success();
    }

    @GetMapping("/salaries")
    public ApiResponse<PageResponse<SalaryStandardVO>> pageSalaryStandards(SalaryStandardPageQuery query) {
        return ApiResponse.success(hrManageService.pageSalaryStandards(query));
    }

    @GetMapping("/salaries/{id}")
    public ApiResponse<SalaryStandardVO> getSalaryStandard(@PathVariable Long id) {
        return ApiResponse.success(hrManageService.getSalaryStandard(id));
    }

    @PostMapping("/salaries")
    public ApiResponse<Void> createSalaryStandard(@Valid @RequestBody SalaryStandardCreateRequest request) {
        hrManageService.createSalaryStandard(request);
        return ApiResponse.success();
    }

    @PutMapping("/salaries/{id}")
    public ApiResponse<Void> updateSalaryStandard(@PathVariable Long id, @RequestBody SalaryStandardUpdateRequest request) {
        hrManageService.updateSalaryStandard(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/salaries/{id}")
    public ApiResponse<Void> deleteSalaryStandard(@PathVariable Long id) {
        hrManageService.deleteSalaryStandard(id);
        return ApiResponse.success();
    }

    @GetMapping("/employees/{employeeId}/current-salary")
    public ApiResponse<BigDecimal> getEmployeeCurrentSalary(@PathVariable Long employeeId) {
        return ApiResponse.success(hrManageService.getEmployeeCurrentSalary(employeeId));
    }

    @GetMapping("/workflow-attachments")
    public ApiResponse<List<AttachmentVO>> listWorkflowAttachments(
            @RequestParam String bizType,
            @RequestParam Long bizId
    ) {
        return ApiResponse.success(hrManageService.listWorkflowAttachments(bizType, bizId));
    }

    @DeleteMapping("/workflow-attachments/{id}")
    public ApiResponse<Void> deleteWorkflowAttachment(
            @PathVariable Long id,
            @RequestParam String bizType,
            @RequestParam Long bizId
    ) {
        hrManageService.deleteWorkflowAttachment(id, bizType, bizId);
        return ApiResponse.success();
    }

    @PostMapping("/workflow-attachments/upload")
    public ApiResponse<AttachmentVO> uploadWorkflowAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam String bizType,
            @RequestParam Long bizId,
            @Value("${huacai.file.local-dir:${java.io.tmpdir}/huacai-files}") String localStorageDir
    ) throws IOException {
        Path storageDir = Paths.get(localStorageDir);
        Files.createDirectories(storageDir);
        String fileName = StringUtils.hasText(file.getOriginalFilename()) ? file.getOriginalFilename() : "unknown";
        String fileExt = extractFileExt(fileName);
        String storedName = UUID.randomUUID().toString().replace("-", "") + (StringUtils.hasText(fileExt) ? "." + fileExt : "");
        Path storedPath = storageDir.resolve(storedName);
        file.transferTo(storedPath.toFile());
        return ApiResponse.success(hrManageService.uploadWorkflowAttachment(
                bizType, bizId, fileName, file.getSize(), storedPath.toString(), fileExt));
    }

    private String extractFileExt(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index < 0 || index == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(index + 1).trim().toLowerCase();
    }

    @GetMapping("/employees/list")
    public ApiResponse<List<EmployeeVO>> listEmployees(@RequestParam(required = false) String keyword) {
        return ApiResponse.success(hrManageService.listEmployees(keyword));
    }

    @GetMapping("/management-records")
    public ApiResponse<PageResponse<ManagementRecordVO>> pageManagementRecords(ManagementRecordPageQuery query) {
        return ApiResponse.success(hrManageService.pageManagementRecords(query));
    }
}
