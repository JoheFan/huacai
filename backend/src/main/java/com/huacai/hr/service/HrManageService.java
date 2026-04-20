package com.huacai.hr.service;

import com.huacai.common.exception.BusinessException;
import com.huacai.common.model.PageResponse;
import com.huacai.hr.dto.*;
import com.huacai.hr.query.*;
import com.huacai.hr.vo.*;
import java.math.BigDecimal;
import java.util.List;

public interface HrManageService {

    PageResponse<EmployeeVO> pageEmployees(EmployeePageQuery query);

    EmployeeDetailVO getEmployeeDetail(Long id);

    Long getCurrentEmployeeId();

    void createEmployee(EmployeeCreateRequest request);

    void updateEmployee(Long id, EmployeeUpdateRequest request);

    void deleteEmployee(Long id);

    void updateMyProfile(MyProfileUpdateRequest request);

    JobInfoVO getJobInfo(Long employeeId);

    void saveJobInfo(JobInfoSaveRequest request);

    void saveRemoval(RemovalSaveRequest request);

    void saveCertificate(CertificateSaveRequest request);

    void deleteCertificate(Long id);

    void saveAssessment(AssessmentSaveRequest request);

    void deleteAssessment(Long id);

    void saveGrowth(GrowthSaveRequest request);

    void deleteGrowth(Long id);

    void saveFamily(FamilySaveRequest request);

    void deleteFamily(Long id);

    void saveChange(ChangeSaveRequest request);

    void saveContract(ContractSaveRequest request);

    void deleteContract(Long id);

    PageResponse<SalaryStandardVO> pageSalaryStandards(SalaryStandardPageQuery query);

    SalaryStandardVO getSalaryStandard(Long id);

    void createSalaryStandard(SalaryStandardCreateRequest request);

    void updateSalaryStandard(Long id, SalaryStandardUpdateRequest request);

    void deleteSalaryStandard(Long id);

    BigDecimal getEmployeeCurrentSalary(Long employeeId);

    PageResponse<TransitionApplyVO> pageTransitionApplies(TransitionApplyPageQuery query);

    TransitionApplyVO getTransitionApply(Long id);

    void createTransitionApply(TransitionApplyCreateRequest request);

    void updateTransitionApply(Long id, TransitionApplyUpdateRequest request);

    void submitTransitionApply(Long id);

    void approveTransitionApply(Long id, ApprovalOpinionRequest request);

    void rejectTransitionApply(Long id, ApprovalOpinionRequest request);

    PageResponse<SalaryAdjustApplyVO> pageSalaryAdjustApplies(SalaryAdjustApplyPageQuery query);

    SalaryAdjustApplyVO getSalaryAdjustApply(Long id);

    void createSalaryAdjustApply(SalaryAdjustApplyCreateRequest request);

    void updateSalaryAdjustApply(Long id, SalaryAdjustApplyUpdateRequest request);

    void submitSalaryAdjustApply(Long id);

    void approveSalaryAdjustApply(Long id, ApprovalOpinionRequest request);

    void rejectSalaryAdjustApply(Long id, ApprovalOpinionRequest request);

    List<ApprovalRecordVO> getApprovalRecords(String bizType, Long bizId);

    List<AttachmentVO> listWorkflowAttachments(String bizType, Long bizId);

    void deleteWorkflowAttachment(Long id, String bizType, Long bizId);

    AttachmentVO uploadWorkflowAttachment(String bizType, Long bizId, String fileName, long fileSize, String storedPath, String fileExt);

    PageResponse<ManagementRecordVO> pageManagementRecords(ManagementRecordPageQuery query);

    List<EmployeeVO> listEmployees(String keyword);
}
