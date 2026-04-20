package com.huacai.hr.service.impl;

import com.huacai.file.mapper.FileMapper;
import com.huacai.file.service.FileService;
import com.huacai.hr.dto.EmployeeUpdateRequest;
import com.huacai.hr.entity.HrEmployee;
import com.huacai.hr.entity.HrManagementRecord;
import com.huacai.hr.mapper.*;
import com.huacai.security.AuthUser;
import com.huacai.security.CurrentUserProvider;
import com.huacai.system.entity.SysOrg;
import com.huacai.system.mapper.SysOrgMapper;
import com.huacai.system.mapper.SysUserMapper;
import com.huacai.workflow.mapper.FinReimbursementApplyMapper;
import com.huacai.workflow.mapper.WfProcessInstanceMapper;
import com.huacai.workflow.mapper.WfTaskMapper;
import com.huacai.workflow.mapper.WfActionLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HrManageServiceImplTest {

    @Mock private HrEmployeeMapper employeeMapper;
    @Mock private HrEmployeeJobInfoMapper jobInfoMapper;
    @Mock private HrEmployeeRemovalMapper removalMapper;
    @Mock private HrEmployeeCertificateMapper certificateMapper;
    @Mock private HrEmployeeAssessmentMapper assessmentMapper;
    @Mock private HrEmployeeGrowthMapper growthMapper;
    @Mock private HrEmployeeFamilyMapper familyMapper;
    @Mock private HrEmployeeChangeMapper changeMapper;
    @Mock private HrEmployeeContractMapper contractMapper;
    @Mock private HrSalaryStandardMapper salaryStandardMapper;
    @Mock private HrEmployeeSalaryMapper employeeSalaryMapper;
    @Mock private HrTransitionApplyMapper transitionApplyMapper;
    @Mock private HrSalaryAdjustApplyMapper salaryAdjustApplyMapper;
    @Mock private HrApprovalRecordMapper approvalRecordMapper;
    @Mock private HrManagementRecordMapper managementRecordMapper;
    @Mock private HrLeaveRecordMapper leaveRecordMapper;
    @Mock private SysOrgMapper orgMapper;
    @Mock private SysUserMapper userMapper;
    @Mock private FinReimbursementApplyMapper reimbursementApplyMapper;
    @Mock private WfProcessInstanceMapper processInstanceMapper;
    @Mock private WfTaskMapper taskMapper;
    @Mock private WfActionLogMapper actionLogMapper;
    @Mock private CurrentUserProvider currentUserProvider;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private FileMapper fileMapper;
    @Mock private FileService fileService;

    private HrManageServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new HrManageServiceImpl(
                employeeMapper,
                jobInfoMapper,
                removalMapper,
                certificateMapper,
                assessmentMapper,
                growthMapper,
                familyMapper,
                changeMapper,
                contractMapper,
                salaryStandardMapper,
                employeeSalaryMapper,
                transitionApplyMapper,
                salaryAdjustApplyMapper,
                approvalRecordMapper,
                managementRecordMapper,
                leaveRecordMapper,
                orgMapper,
                userMapper,
                reimbursementApplyMapper,
                processInstanceMapper,
                taskMapper,
                actionLogMapper,
                currentUserProvider,
                passwordEncoder,
                fileMapper,
                fileService
        );
    }

    @Test
    void updateEmployee_writesTransferRecordWhenOrgChanges() {
        HrEmployee employee = new HrEmployee();
        employee.setId(7L);
        employee.setRealName("张三");
        employee.setOrgId(10L);
        employee.setJobTitle("招商主管");

        AuthUser currentUser = new AuthUser(
                99L,
                "admin",
                "管理员",
                1L,
                "总部",
                "SUPER_ADMIN",
                "ADMIN",
                "管理员",
                List.of("ADMIN"),
                List.of(),
                List.of(),
                Map.of(),
                "ALL"
        );

        SysOrg oldOrg = new SysOrg();
        oldOrg.setId(10L);
        oldOrg.setOrgName("市场部");
        SysOrg newOrg = new SysOrg();
        newOrg.setId(20L);
        newOrg.setOrgName("销售部");

        when(employeeMapper.selectById(7L)).thenReturn(employee);
        when(currentUserProvider.getCurrentUser()).thenReturn(Optional.of(currentUser));
        when(orgMapper.selectById(10L)).thenReturn(oldOrg);
        when(orgMapper.selectById(20L)).thenReturn(newOrg);

        EmployeeUpdateRequest request = new EmployeeUpdateRequest(
                null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, 20L, null, null
        );

        service.updateEmployee(7L, request);

        ArgumentCaptor<HrManagementRecord> recordCaptor = ArgumentCaptor.forClass(HrManagementRecord.class);
        verify(managementRecordMapper, atLeastOnce()).insert(recordCaptor.capture());
        assertTrue(
                recordCaptor.getAllValues().stream().anyMatch(record ->
                        "TRANSFER".equals(record.getRecordType())
                                && record.getContent().contains("市场部")
                                && record.getContent().contains("销售部")
                )
        );
    }

    @Test
    void updateEmployee_doesNotWriteTransferRecordWhenOnlyRemarkChanges() {
        HrEmployee employee = new HrEmployee();
        employee.setId(8L);
        employee.setRealName("李四");
        employee.setOrgId(11L);
        employee.setJobTitle("招商主管");

        AuthUser currentUser = new AuthUser(
                99L,
                "admin",
                "管理员",
                1L,
                "总部",
                "SUPER_ADMIN",
                "ADMIN",
                "管理员",
                List.of("ADMIN"),
                List.of(),
                List.of(),
                Map.of(),
                "ALL"
        );

        when(employeeMapper.selectById(8L)).thenReturn(employee);
        when(currentUserProvider.getCurrentUser()).thenReturn(Optional.of(currentUser));

        EmployeeUpdateRequest request = new EmployeeUpdateRequest(
                null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, "只改备注"
        );

        service.updateEmployee(8L, request);

        ArgumentCaptor<HrManagementRecord> recordCaptor = ArgumentCaptor.forClass(HrManagementRecord.class);
        verify(managementRecordMapper, atLeastOnce()).insert(recordCaptor.capture());
        assertTrue(
                recordCaptor.getAllValues().stream().noneMatch(record -> "TRANSFER".equals(record.getRecordType()))
        );
    }
}
