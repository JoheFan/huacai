package com.huacai.hr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huacai.common.exception.BusinessException;
import com.huacai.common.model.PageResponse;
import com.huacai.hr.dto.*;
import com.huacai.hr.entity.*;
import com.huacai.hr.mapper.*;
import com.huacai.hr.query.*;
import com.huacai.hr.service.HrManageService;
import com.huacai.hr.vo.*;
import com.huacai.file.mapper.FileMapper;
import com.huacai.file.service.FileService;
import com.huacai.security.AuthUser;
import com.huacai.security.CurrentUserProvider;
import com.huacai.system.entity.SysOrg;
import com.huacai.system.entity.SysUser;
import com.huacai.system.mapper.SysOrgMapper;
import com.huacai.system.mapper.SysUserMapper;
import com.huacai.workflow.entity.FinReimbursementApply;
import com.huacai.workflow.entity.WfActionLog;
import com.huacai.workflow.entity.WfProcessInstance;
import com.huacai.workflow.entity.WfTask;
import com.huacai.workflow.mapper.FinReimbursementApplyMapper;
import com.huacai.workflow.mapper.WfActionLogMapper;
import com.huacai.workflow.mapper.WfProcessInstanceMapper;
import com.huacai.workflow.mapper.WfTaskMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class HrManageServiceImpl implements HrManageService {

    private final HrEmployeeMapper employeeMapper;
    private final HrEmployeeJobInfoMapper jobInfoMapper;
    private final HrEmployeeRemovalMapper removalMapper;
    private final HrEmployeeCertificateMapper certificateMapper;
    private final HrEmployeeAssessmentMapper assessmentMapper;
    private final HrEmployeeGrowthMapper growthMapper;
    private final HrEmployeeFamilyMapper familyMapper;
    private final HrEmployeeChangeMapper changeMapper;
    private final HrEmployeeContractMapper contractMapper;
    private final HrSalaryStandardMapper salaryStandardMapper;
    private final HrEmployeeSalaryMapper employeeSalaryMapper;
    private final HrTransitionApplyMapper transitionApplyMapper;
    private final HrSalaryAdjustApplyMapper salaryAdjustApplyMapper;
    private final HrApprovalRecordMapper approvalRecordMapper;
    private final HrManagementRecordMapper managementRecordMapper;
    private final HrLeaveRecordMapper leaveRecordMapper;
    private final SysOrgMapper orgMapper;
    private final SysUserMapper userMapper;
    private final FinReimbursementApplyMapper reimbursementApplyMapper;
    private final WfProcessInstanceMapper processInstanceMapper;
    private final WfTaskMapper taskMapper;
    private final WfActionLogMapper actionLogMapper;
    private final CurrentUserProvider currentUserProvider;
    private final PasswordEncoder passwordEncoder;
    private final FileMapper fileMapper;
    private final FileService fileService;

    public HrManageServiceImpl(
            HrEmployeeMapper employeeMapper,
            HrEmployeeJobInfoMapper jobInfoMapper,
            HrEmployeeRemovalMapper removalMapper,
            HrEmployeeCertificateMapper certificateMapper,
            HrEmployeeAssessmentMapper assessmentMapper,
            HrEmployeeGrowthMapper growthMapper,
            HrEmployeeFamilyMapper familyMapper,
            HrEmployeeChangeMapper changeMapper,
            HrEmployeeContractMapper contractMapper,
            HrSalaryStandardMapper salaryStandardMapper,
            HrEmployeeSalaryMapper employeeSalaryMapper,
            HrTransitionApplyMapper transitionApplyMapper,
            HrSalaryAdjustApplyMapper salaryAdjustApplyMapper,
            HrApprovalRecordMapper approvalRecordMapper,
            HrManagementRecordMapper managementRecordMapper,
            HrLeaveRecordMapper leaveRecordMapper,
            SysOrgMapper orgMapper,
            SysUserMapper userMapper,
            FinReimbursementApplyMapper reimbursementApplyMapper,
            WfProcessInstanceMapper processInstanceMapper,
            WfTaskMapper taskMapper,
            WfActionLogMapper actionLogMapper,
            CurrentUserProvider currentUserProvider,
            PasswordEncoder passwordEncoder,
            FileMapper fileMapper,
            FileService fileService
    ) {
        this.employeeMapper = employeeMapper;
        this.jobInfoMapper = jobInfoMapper;
        this.removalMapper = removalMapper;
        this.certificateMapper = certificateMapper;
        this.assessmentMapper = assessmentMapper;
        this.growthMapper = growthMapper;
        this.familyMapper = familyMapper;
        this.changeMapper = changeMapper;
        this.contractMapper = contractMapper;
        this.salaryStandardMapper = salaryStandardMapper;
        this.employeeSalaryMapper = employeeSalaryMapper;
        this.transitionApplyMapper = transitionApplyMapper;
        this.salaryAdjustApplyMapper = salaryAdjustApplyMapper;
        this.approvalRecordMapper = approvalRecordMapper;
        this.managementRecordMapper = managementRecordMapper;
        this.leaveRecordMapper = leaveRecordMapper;
        this.orgMapper = orgMapper;
        this.userMapper = userMapper;
        this.reimbursementApplyMapper = reimbursementApplyMapper;
        this.processInstanceMapper = processInstanceMapper;
        this.taskMapper = taskMapper;
        this.actionLogMapper = actionLogMapper;
        this.currentUserProvider = currentUserProvider;
        this.passwordEncoder = passwordEncoder;
        this.fileMapper = fileMapper;
        this.fileService = fileService;
    }

    private AuthUser getCurrentAuthUser() {
        return currentUserProvider.getCurrentUser().orElseThrow(() -> new BusinessException("未登录"));
    }

    @Override
    public Long getCurrentEmployeeId() {
        AuthUser currentUser = getCurrentAuthUser();
        HrEmployee employee = findEmployeeByUserId(currentUser.getUserId());
        if (employee == null) {
            throw new BusinessException("当前用户未绑定员工档案");
        }
        return employee.getId();
    }

    @Override
    public PageResponse<EmployeeVO> pageEmployees(EmployeePageQuery query) {
        LambdaQueryWrapper<HrEmployee> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(HrEmployee::getRealName, query.getKeyword())
                    .or().like(HrEmployee::getEmployeeCode, query.getKeyword()));
        }
        wrapper.eq(StringUtils.hasText(query.getEmploymentStatus()), HrEmployee::getEmploymentStatus, query.getEmploymentStatus())
                .eq(query.getOrgId() != null, HrEmployee::getOrgId, query.getOrgId())
                .orderByDesc(HrEmployee::getCreatedAt);

        Page<HrEmployee> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<HrEmployee> result = employeeMapper.selectPage(page, wrapper);

        List<EmployeeVO> records = result.getRecords().stream().map(this::toEmployeeVO).collect(Collectors.toList());
        return new PageResponse<>(records, result.getTotal(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public EmployeeDetailVO getEmployeeDetail(Long id) {
        HrEmployee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }

        String orgName = null;
        if (employee.getOrgId() != null) {
            SysOrg org = orgMapper.selectById(employee.getOrgId());
            orgName = org != null ? org.getOrgName() : null;
        }

        JobInfoVO jobInfo = getJobInfoByEmployeeId(id);
        RemovalVO removal = getRemovalByEmployeeId(id);
        List<CertificateVO> certificates = getCertificatesByEmployeeId(id);
        List<AssessmentVO> assessments = getAssessmentsByEmployeeId(id);
        List<GrowthVO> growthRecords = getGrowthByEmployeeId(id);
        List<FamilyVO> familyMembers = getFamilyByEmployeeId(id);
        List<ChangeVO> changeRecords = getChangesByEmployeeId(id);
        List<ContractVO> contracts = getContractsByEmployeeId(id);
        List<EmployeeSalaryVO> salaryInfo = getSalaryInfoByEmployeeId(id);
        List<LeaveRecordVO> leaveRecords = getLeaveRecordsByEmployeeId(id);

        return new EmployeeDetailVO(
                employee.getId(), employee.getEmployeeCode(), employee.getRealName(),
                employee.getGender(), employee.getIdCardNo(), employee.getBirthday(),
                employee.getAge(), employee.getNation(), employee.getPoliticalStatus(),
                employee.getHometown(), employee.getMaritalStatus(), employee.getPhone(),
                employee.getEmail(), employee.getGraduateSchool(), employee.getHighestEducation(),
                employee.getWorkStartDate(), employee.getHomeAddress(),
                employee.getEmergencyContact(), employee.getEmergencyContactPhone(),
                employee.getBankCardNo(), employee.getIdPhotoUrl(),
                employee.getEmploymentStatus(), employee.getTalentFlag(),
                employee.getCreateSystemAccount(), employee.getSystemUsername(),
                employee.getOrgId(), orgName, employee.getJobTitle(),
                employee.getRemark(), employee.getCreatedAt(), employee.getUpdatedAt(),
                jobInfo, removal, certificates, assessments, growthRecords,
                familyMembers, changeRecords, contracts, salaryInfo, leaveRecords
        );
    }

    @Override
    @Transactional
    public void createEmployee(EmployeeCreateRequest request) {
        LambdaQueryWrapper<HrEmployee> codeCheck = Wrappers.lambdaQuery();
        codeCheck.eq(HrEmployee::getEmployeeCode, request.employeeCode());
        if (employeeMapper.selectCount(codeCheck).intValue() > 0) {
            throw new BusinessException("员工编号已存在");
        }

        HrEmployee employee = new HrEmployee();
        employee.setEmployeeCode(request.employeeCode());
        employee.setRealName(request.realName());
        employee.setGender(request.gender());
        employee.setIdCardNo(request.idCardNo());
        employee.setBirthday(request.birthday());
        employee.setAge(request.age());
        employee.setNation(request.nation());
        employee.setPoliticalStatus(request.politicalStatus());
        employee.setHometown(request.hometown());
        employee.setMaritalStatus(request.maritalStatus());
        employee.setPhone(request.phone());
        employee.setEmail(request.email());
        employee.setGraduateSchool(request.graduateSchool());
        employee.setHighestEducation(request.highestEducation());
        employee.setWorkStartDate(request.workStartDate());
        employee.setHomeAddress(request.homeAddress());
        employee.setEmergencyContact(request.emergencyContact());
        employee.setEmergencyContactPhone(request.emergencyContactPhone());
        employee.setBankCardNo(request.bankCardNo());
        employee.setIdPhotoUrl(request.idPhotoUrl());
        employee.setEmploymentStatus(request.employmentStatus());
        employee.setTalentFlag(request.talentFlag());
        employee.setCreateSystemAccount(request.createSystemAccount());
        employee.setSystemUsername(request.systemUsername());
        employee.setSystemPasswordPlain(request.systemPasswordPlain());
        employee.setOrgId(request.orgId());
        employee.setJobTitle(request.jobTitle());
        employee.setRemark(request.remark());

        if (request.birthday() != null && request.age() == null) {
            int age = LocalDate.now().getYear() - request.birthday().getYear();
            employee.setAge(age);
        }

        AuthUser currentUser = getCurrentAuthUser();
        employee.setCreatedBy(currentUser.getUserId());

        employeeMapper.insert(employee);

        if (request.createSystemAccount() != null && request.createSystemAccount() == 1) {
            createSystemAccount(employee, request.systemPasswordPlain());
        }

        writeManagementRecord(employee.getId(), employee.getRealName(), "EMPLOYEE_CREATE",
                "新增员工档案：" + employee.getRealName(), currentUser);
    }

    @Override
    @Transactional
    public void updateEmployee(Long id, EmployeeUpdateRequest request) {
        HrEmployee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }

        Long oldOrgId = employee.getOrgId();
        String oldOrgName = getOrgName(oldOrgId);
        String oldJobTitle = employee.getJobTitle();
        boolean orgChanged = request.orgId() != null && !Objects.equals(request.orgId(), oldOrgId);
        boolean jobTitleChanged = request.jobTitle() != null && !Objects.equals(request.jobTitle(), oldJobTitle);

        if (StringUtils.hasText(request.realName())) employee.setRealName(request.realName());
        if (request.gender() != null) employee.setGender(request.gender());
        if (request.idCardNo() != null) employee.setIdCardNo(request.idCardNo());
        if (request.birthday() != null) {
            employee.setBirthday(request.birthday());
            if (request.age() == null) {
                int age = LocalDate.now().getYear() - request.birthday().getYear();
                employee.setAge(age);
            }
        }
        if (request.age() != null) employee.setAge(request.age());
        if (request.nation() != null) employee.setNation(request.nation());
        if (request.politicalStatus() != null) employee.setPoliticalStatus(request.politicalStatus());
        if (request.hometown() != null) employee.setHometown(request.hometown());
        if (request.maritalStatus() != null) employee.setMaritalStatus(request.maritalStatus());
        if (request.phone() != null) employee.setPhone(request.phone());
        if (request.email() != null) employee.setEmail(request.email());
        if (request.graduateSchool() != null) employee.setGraduateSchool(request.graduateSchool());
        if (request.highestEducation() != null) employee.setHighestEducation(request.highestEducation());
        if (request.workStartDate() != null) employee.setWorkStartDate(request.workStartDate());
        if (request.homeAddress() != null) employee.setHomeAddress(request.homeAddress());
        if (request.emergencyContact() != null) employee.setEmergencyContact(request.emergencyContact());
        if (request.emergencyContactPhone() != null) employee.setEmergencyContactPhone(request.emergencyContactPhone());
        if (request.bankCardNo() != null) employee.setBankCardNo(request.bankCardNo());
        if (request.idPhotoUrl() != null) employee.setIdPhotoUrl(request.idPhotoUrl());
        if (request.employmentStatus() != null) employee.setEmploymentStatus(request.employmentStatus());
        if (request.talentFlag() != null) employee.setTalentFlag(request.talentFlag());
        if (request.createSystemAccount() != null) {
            employee.setCreateSystemAccount(request.createSystemAccount());
            if (request.createSystemAccount() == 1 && StringUtils.hasText(request.systemUsername())) {
                employee.setSystemUsername(request.systemUsername());
                if (StringUtils.hasText(request.systemPasswordPlain())) {
                    employee.setSystemPasswordPlain(request.systemPasswordPlain());
                }
            }
        }
        if (request.orgId() != null) employee.setOrgId(request.orgId());
        if (request.jobTitle() != null) employee.setJobTitle(request.jobTitle());
        if (request.remark() != null) employee.setRemark(request.remark());

        AuthUser currentUser = getCurrentAuthUser();

        employeeMapper.updateById(employee);

        if (orgChanged || jobTitleChanged) {
            String newOrg = getOrgName(employee.getOrgId());
            String content = "调岗：" + employee.getRealName();
            if (orgChanged) content += "，部门：" + (oldOrgName != null ? oldOrgName : "无") + " → " + (newOrg != null ? newOrg : "无");
            if (jobTitleChanged) content += "，岗位：" + (oldJobTitle != null ? oldJobTitle : "无") + " → " + (employee.getJobTitle() != null ? employee.getJobTitle() : "无");
            writeManagementRecord(id, employee.getRealName(), "TRANSFER", content, currentUser);
        }

        if (request.createSystemAccount() != null && request.createSystemAccount() == 1) {
            createSystemAccount(employee, request.systemPasswordPlain());
        }

        writeManagementRecord(id, employee.getRealName(), "EMPLOYEE_UPDATE",
                "更新员工档案：" + employee.getRealName(), currentUser);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        HrEmployee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }
        AuthUser currentUser = getCurrentAuthUser();
        writeManagementRecord(id, employee.getRealName(), "EMPLOYEE_UPDATE",
                "删除员工档案：" + employee.getRealName(), currentUser);
        employeeMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateMyProfile(MyProfileUpdateRequest request) {
        AuthUser currentUser = getCurrentAuthUser();
        HrEmployee employee = findEmployeeByUserId(currentUser.getUserId());
        if (employee == null) {
            throw new BusinessException("当前用户未绑定员工档案");
        }

        if (StringUtils.hasText(request.realName())) employee.setRealName(request.realName());
        if (request.gender() != null) employee.setGender(request.gender());
        if (request.idCardNo() != null) employee.setIdCardNo(request.idCardNo());
        if (request.birthday() != null) {
            employee.setBirthday(request.birthday());
            if (request.age() == null) {
                int age = LocalDate.now().getYear() - request.birthday().getYear();
                employee.setAge(age);
            }
        }
        if (request.age() != null) employee.setAge(request.age());
        if (request.nation() != null) employee.setNation(request.nation());
        if (request.politicalStatus() != null) employee.setPoliticalStatus(request.politicalStatus());
        if (request.hometown() != null) employee.setHometown(request.hometown());
        if (request.maritalStatus() != null) employee.setMaritalStatus(request.maritalStatus());
        if (request.phone() != null) employee.setPhone(request.phone());
        if (request.email() != null) employee.setEmail(request.email());
        if (request.graduateSchool() != null) employee.setGraduateSchool(request.graduateSchool());
        if (request.highestEducation() != null) employee.setHighestEducation(request.highestEducation());
        if (request.homeAddress() != null) employee.setHomeAddress(request.homeAddress());
        if (request.emergencyContact() != null) employee.setEmergencyContact(request.emergencyContact());
        if (request.emergencyContactPhone() != null) employee.setEmergencyContactPhone(request.emergencyContactPhone());

        employee.setUpdatedBy(currentUser.getUserId());
        employeeMapper.updateById(employee);
    }

    @Override
    public JobInfoVO getJobInfo(Long employeeId) {
        return getJobInfoByEmployeeId(employeeId);
    }

    @Override
    @Transactional
    public void saveJobInfo(JobInfoSaveRequest request) {
        LambdaQueryWrapper<HrEmployeeJobInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HrEmployeeJobInfo::getEmployeeId, request.employeeId());
        HrEmployeeJobInfo existing = jobInfoMapper.selectOne(wrapper);

        HrEmployeeJobInfo jobInfo = new HrEmployeeJobInfo();
        jobInfo.setEmployeeId(request.employeeId());
        jobInfo.setEmployeeCode(request.employeeCode());
        jobInfo.setJoinDate(request.joinDate());
        jobInfo.setFormalDate(request.formalDate());
        jobInfo.setWorkUnit(request.workUnit());
        jobInfo.setWorkMode(request.workMode());
        jobInfo.setBorrowDispatchDate(request.borrowDispatchDate());
        jobInfo.setDepartment(request.department());
        jobInfo.setRankLevel(request.rankLevel());
        jobInfo.setJobCategory(request.jobCategory());
        jobInfo.setPosition(request.position());
        jobInfo.setSortNo(request.sortNo());
        jobInfo.setIs编制(request.is编制());

        AuthUser currentUser = getCurrentAuthUser();
        jobInfo.setCreatedBy(currentUser.getUserId());

        if (existing != null) {
            jobInfo.setId(existing.getId());
            jobInfo.setUpdatedBy(currentUser.getUserId());
            jobInfoMapper.updateById(jobInfo);
        } else {
            jobInfoMapper.insert(jobInfo);
        }
    }

    @Override
    @Transactional
    public void saveRemoval(RemovalSaveRequest request) {
        HrEmployeeRemoval removal = new HrEmployeeRemoval();
        removal.setEmployeeId(request.employeeId());
        removal.setRemovalType(request.removalType());
        removal.setRemovalDate(request.removalDate());
        removal.setExpectedRetireDate(request.expectedRetireDate());
        removal.setReason(request.reason());

        AuthUser currentUser = getCurrentAuthUser();
        removal.setCreatedBy(currentUser.getUserId());
        removalMapper.insert(removal);

        HrEmployee employee = employeeMapper.selectById(request.employeeId());
        if (employee != null) {
            employee.setEmploymentStatus("OFFBOARD");
            employee.setUpdatedBy(currentUser.getUserId());
            employeeMapper.updateById(employee);
            writeManagementRecord(employee.getId(), employee.getRealName(), "TERMINATION",
                    "员工离职：" + employee.getRealName() + "，离职日期：" + request.removalDate(), currentUser);
        }
    }

    @Override
    @Transactional
    public void saveCertificate(CertificateSaveRequest request) {
        HrEmployeeCertificate certificate = new HrEmployeeCertificate();
        if (request.id() != null) {
            certificate.setId(request.id());
        }
        certificate.setEmployeeId(request.employeeId());
        certificate.setCertificateName(request.certificateName());
        certificate.setCertificateNo(request.certificateNo());
        certificate.setIssueDate(request.issueDate());
        certificate.setCertificateType(request.certificateType());
        certificate.setIssueOrg(request.issueOrg());
        certificate.setIsPermanent(request.isPermanent());
        certificate.setExpireDate(request.expireDate());
        certificate.setCertificateFileUrl(request.certificateFileUrl());

        AuthUser currentUser = getCurrentAuthUser();
        if (request.id() != null) {
            certificate.setUpdatedBy(currentUser.getUserId());
            certificateMapper.updateById(certificate);
        } else {
            certificate.setCreatedBy(currentUser.getUserId());
            certificateMapper.insert(certificate);
        }
    }

    @Override
    public void deleteCertificate(Long id) {
        certificateMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void saveAssessment(AssessmentSaveRequest request) {
        HrEmployeeAssessment assessment = new HrEmployeeAssessment();
        if (request.id() != null) {
            assessment.setId(request.id());
        }
        assessment.setEmployeeId(request.employeeId());
        assessment.setAssessmentMonth(request.assessmentMonth());
        assessment.setAssessmentScore(request.assessmentScore());
        assessment.setAssessmentGrade(request.assessmentGrade());
        assessment.setRemark(request.remark());

        AuthUser currentUser = getCurrentAuthUser();
        if (request.id() != null) {
            assessment.setUpdatedBy(currentUser.getUserId());
            assessmentMapper.updateById(assessment);
        } else {
            assessment.setCreatedBy(currentUser.getUserId());
            assessmentMapper.insert(assessment);
        }
    }

    @Override
    public void deleteAssessment(Long id) {
        assessmentMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void saveGrowth(GrowthSaveRequest request) {
        HrEmployeeGrowth growth = new HrEmployeeGrowth();
        if (request.id() != null) {
            growth.setId(request.id());
        }
        growth.setEmployeeId(request.employeeId());
        growth.setStartDate(request.startDate());
        growth.setEndDate(request.endDate());
        growth.setWorkName(request.workName());

        AuthUser currentUser = getCurrentAuthUser();
        if (request.id() != null) {
            growth.setUpdatedBy(currentUser.getUserId());
            growthMapper.updateById(growth);
        } else {
            growth.setCreatedBy(currentUser.getUserId());
            growthMapper.insert(growth);
        }
    }

    @Override
    public void deleteGrowth(Long id) {
        growthMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void saveFamily(FamilySaveRequest request) {
        HrEmployeeFamily family = new HrEmployeeFamily();
        if (request.id() != null) {
            family.setId(request.id());
        }
        family.setEmployeeId(request.employeeId());
        family.setRelation(request.relation());
        family.setName(request.name());
        family.setBirthday(request.birthday());
        family.setIdCardNo(request.idCardNo());
        family.setPoliticalStatus(request.politicalStatus());
        family.setWorkUnitPosition(request.workUnitPosition());

        AuthUser currentUser = getCurrentAuthUser();
        if (request.id() != null) {
            family.setUpdatedBy(currentUser.getUserId());
            familyMapper.updateById(family);
        } else {
            family.setCreatedBy(currentUser.getUserId());
            familyMapper.insert(family);
        }
    }

    @Override
    public void deleteFamily(Long id) {
        familyMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void saveChange(ChangeSaveRequest request) {
        HrEmployeeChange change = new HrEmployeeChange();
        if (request.id() != null) {
            change.setId(request.id());
        }
        change.setEmployeeId(request.employeeId());
        change.setCurrentDepartment(request.currentDepartment());
        change.setCurrentPosition(request.currentPosition());
        change.setCurrentRankLevel(request.currentRankLevel());
        change.setOriginalDepartment(request.originalDepartment());
        change.setOriginalPosition(request.originalPosition());
        change.setOriginalRankLevel(request.originalRankLevel());
        change.setReportDate(request.reportDate());
        change.setChangeType(request.changeType());
        change.setChangeReason(request.changeReason());

        AuthUser currentUser = getCurrentAuthUser();
        if (request.id() != null) {
            change.setUpdatedBy(currentUser.getUserId());
            changeMapper.updateById(change);
        } else {
            change.setCreatedBy(currentUser.getUserId());
            changeMapper.insert(change);
        }
    }

    @Override
    @Transactional
    public void saveContract(ContractSaveRequest request) {
        HrEmployeeContract contract = new HrEmployeeContract();
        if (request.id() != null) {
            contract.setId(request.id());
        }
        contract.setEmployeeId(request.employeeId());
        contract.setContractName(request.contractName());
        contract.setContractNo(request.contractNo());
        contract.setContractStartDate(request.contractStartDate());
        contract.setContractEndDate(request.contractEndDate());
        contract.setContractFileUrl(request.contractFileUrl());
        contract.setRemark(request.remark());

        AuthUser currentUser = getCurrentAuthUser();
        if (request.id() != null) {
            contract.setUpdatedBy(currentUser.getUserId());
            contractMapper.updateById(contract);
        } else {
            contract.setCreatedBy(currentUser.getUserId());
            contractMapper.insert(contract);
        }
    }

    @Override
    public void deleteContract(Long id) {
        contractMapper.deleteById(id);
    }

    @Override
    public PageResponse<SalaryStandardVO> pageSalaryStandards(SalaryStandardPageQuery query) {
        LambdaQueryWrapper<HrSalaryStandard> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StringUtils.hasText(query.getKeyword()), HrSalaryStandard::getSalaryName, query.getKeyword())
                .eq(StringUtils.hasText(query.getStatus()), HrSalaryStandard::getStatus, query.getStatus())
                .eq(query.getOrgId() != null, HrSalaryStandard::getOrgId, query.getOrgId())
                .orderByDesc(HrSalaryStandard::getSortNo, HrSalaryStandard::getCreatedAt);

        Page<HrSalaryStandard> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<HrSalaryStandard> result = salaryStandardMapper.selectPage(page, wrapper);

        List<SalaryStandardVO> records = result.getRecords().stream().map(this::toSalaryStandardVO).collect(Collectors.toList());
        return new PageResponse<>(records, result.getTotal(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public SalaryStandardVO getSalaryStandard(Long id) {
        HrSalaryStandard salary = salaryStandardMapper.selectById(id);
        if (salary == null) {
            throw new BusinessException("工资标准不存在");
        }
        return toSalaryStandardVO(salary);
    }

    @Override
    @Transactional
    public void createSalaryStandard(SalaryStandardCreateRequest request) {
        HrSalaryStandard salary = new HrSalaryStandard();
        salary.setSalaryName(request.salaryName());
        salary.setAmount(request.amount());
        salary.setJobTitle(request.jobTitle());
        salary.setOrgId(request.orgId());
        salary.setOrgName(request.orgName());
        salary.setDescription(request.description());
        salary.setSortNo(request.sortNo() != null ? request.sortNo() : 0);
        salary.setStatus(request.status());
        salary.setEffectiveDate(request.effectiveDate());
        salary.setExpireDate(request.expireDate());
        salary.setVersionNo(request.versionNo());
        salary.setApplicableScope(request.applicableScope());

        AuthUser currentUser = getCurrentAuthUser();
        salary.setCreatedBy(currentUser.getUserId());
        salaryStandardMapper.insert(salary);
    }

    @Override
    @Transactional
    public void updateSalaryStandard(Long id, SalaryStandardUpdateRequest request) {
        HrSalaryStandard salary = salaryStandardMapper.selectById(id);
        if (salary == null) {
            throw new BusinessException("工资标准不存在");
        }

        if (StringUtils.hasText(request.salaryName())) salary.setSalaryName(request.salaryName());
        if (request.amount() != null) salary.setAmount(request.amount());
        if (request.jobTitle() != null) salary.setJobTitle(request.jobTitle());
        if (request.orgId() != null) salary.setOrgId(request.orgId());
        if (request.orgName() != null) salary.setOrgName(request.orgName());
        if (request.description() != null) salary.setDescription(request.description());
        if (request.sortNo() != null) salary.setSortNo(request.sortNo());
        if (StringUtils.hasText(request.status())) salary.setStatus(request.status());
        if (request.effectiveDate() != null) salary.setEffectiveDate(request.effectiveDate());
        if (request.expireDate() != null) salary.setExpireDate(request.expireDate());
        if (request.versionNo() != null) salary.setVersionNo(request.versionNo());
        if (request.applicableScope() != null) salary.setApplicableScope(request.applicableScope());

        AuthUser currentUser = getCurrentAuthUser();
        salary.setUpdatedBy(currentUser.getUserId());
        salaryStandardMapper.updateById(salary);
    }

    @Override
    public void deleteSalaryStandard(Long id) {
        salaryStandardMapper.deleteById(id);
    }

    @Override
    public BigDecimal getEmployeeCurrentSalary(Long employeeId) {
        LambdaQueryWrapper<HrEmployeeSalary> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HrEmployeeSalary::getEmployeeId, employeeId)
                .and(w -> w.isNull(HrEmployeeSalary::getExpireDate)
                        .or().gt(HrEmployeeSalary::getExpireDate, LocalDate.now()))
                .orderByDesc(HrEmployeeSalary::getEffectiveDate)
                .last("LIMIT 1");

        HrEmployeeSalary salary = employeeSalaryMapper.selectOne(wrapper);
        return salary != null ? salary.getAmount() : null;
    }

    @Override
    public PageResponse<TransitionApplyVO> pageTransitionApplies(TransitionApplyPageQuery query) {
        AuthUser currentUser = getCurrentAuthUser();
        LambdaQueryWrapper<HrTransitionApply> wrapper = Wrappers.lambdaQuery();

        if ("my".equals(query.getScope())) {
            wrapper.eq(HrTransitionApply::getApplicantId, currentUser.getUserId());
        }

        wrapper.like(StringUtils.hasText(query.getKeyword()), HrTransitionApply::getEmployeeName, query.getKeyword())
                .eq(StringUtils.hasText(query.getProcessStatus()), HrTransitionApply::getProcessStatus, query.getProcessStatus())
                .orderByDesc(HrTransitionApply::getCreatedAt);

        Page<HrTransitionApply> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<HrTransitionApply> result = transitionApplyMapper.selectPage(page, wrapper);

        List<TransitionApplyVO> records = result.getRecords().stream().map(a -> toTransitionApplyVO(a, null)).collect(Collectors.toList());
        return new PageResponse<>(records, result.getTotal(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public TransitionApplyVO getTransitionApply(Long id) {
        HrTransitionApply apply = transitionApplyMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException("转正申请不存在");
        }
        return toTransitionApplyVO(apply, canCurrentUserApproveTransition(apply));
    }

    @Override
    @Transactional
    public void createTransitionApply(TransitionApplyCreateRequest request) {
        AuthUser currentUser = getCurrentAuthUser();
        HrEmployee employee = employeeMapper.selectById(request.employeeId());
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }

        String applyNo = generateApplyNo("ZZ");

        HrTransitionApply apply = new HrTransitionApply();
        apply.setApplyNo(applyNo);
        apply.setEmployeeId(request.employeeId());
        apply.setEmployeeCode(employee.getEmployeeCode());
        apply.setEmployeeName(employee.getRealName());
        apply.setPhone(employee.getPhone());
        apply.setDepartment(getOrgName(employee.getOrgId()));
        apply.setOrgId(employee.getOrgId());
        apply.setPosition(employee.getJobTitle());
        apply.setJoinDate(employee.getWorkStartDate());
        apply.setExpectedFormalDate(request.expectedFormalDate());
        apply.setApplyReason(request.applyReason());
        apply.setSelfEvaluation(request.selfEvaluation());
        apply.setGrowth(request.growth());
        apply.setShortcomings(request.shortcomings());
        apply.setDevelopmentSuggestion(request.developmentSuggestion());
        apply.setDraftOpinion(request.draftOpinion());
        apply.setProcessStatus("DRAFT");
        apply.setApplicantId(currentUser.getUserId());
        apply.setApplyTime(LocalDateTime.now());
        apply.setCreatedBy(currentUser.getUserId());

        transitionApplyMapper.insert(apply);
    }

    @Override
    @Transactional
    public void updateTransitionApply(Long id, TransitionApplyUpdateRequest request) {
        HrTransitionApply apply = transitionApplyMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException("转正申请不存在");
        }
        if (!"DRAFT".equals(apply.getProcessStatus()) && !"REJECTED".equals(apply.getProcessStatus())) {
            throw new BusinessException("只有草稿或驳回状态可以编辑");
        }

        if (request.expectedFormalDate() != null) apply.setExpectedFormalDate(request.expectedFormalDate());
        if (request.applyReason() != null) apply.setApplyReason(request.applyReason());
        if (request.selfEvaluation() != null) apply.setSelfEvaluation(request.selfEvaluation());
        if (request.growth() != null) apply.setGrowth(request.growth());
        if (request.shortcomings() != null) apply.setShortcomings(request.shortcomings());
        if (request.developmentSuggestion() != null) apply.setDevelopmentSuggestion(request.developmentSuggestion());
        if (request.draftOpinion() != null) apply.setDraftOpinion(request.draftOpinion());

        AuthUser currentUser = getCurrentAuthUser();
        apply.setUpdatedBy(currentUser.getUserId());
        transitionApplyMapper.updateById(apply);
    }

    @Override
    @Transactional
    public void submitTransitionApply(Long id) {
        HrTransitionApply apply = transitionApplyMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException("转正申请不存在");
        }
        if (!"DRAFT".equals(apply.getProcessStatus()) && !"REJECTED".equals(apply.getProcessStatus())) {
            throw new BusinessException("只有草稿或驳回状态可以提交");
        }

        apply.setProcessStatus("PENDING_DEPT");
        apply.setCurrentNode("部门审核");
        apply.setSubmitTime(LocalDateTime.now());

        AuthUser currentUser = getCurrentAuthUser();
        apply.setUpdatedBy(currentUser.getUserId());
        transitionApplyMapper.updateById(apply);

        WfProcessInstance process = new WfProcessInstance();
        process.setBizType("TRANSITION");
        process.setBizId(id);
        process.setBizNo(apply.getApplyNo());
        process.setTitle("转正申请：" + apply.getEmployeeName());
        process.setApplicantId(apply.getApplicantId());
        process.setApplicantName(currentUser.getRealName());
        process.setApplicantOrgId(apply.getOrgId());
        process.setApplicantOrgName(null);
        process.setOverallStatus("PENDING");
        process.setCurrentNodeKey("PENDING_DEPT");
        process.setCurrentNodeName("部门审核");
        process.setCurrentRound(1);
        process.setStartedAt(LocalDateTime.now());
        process.setLastActionAt(LocalDateTime.now());
        process.setCreatedBy(currentUser.getUserId());
        processInstanceMapper.insert(process);

        WfTask task = new WfTask();
        task.setProcessId(process.getId());
        task.setBizType("TRANSITION");
        task.setBizId(id);
        task.setRoundNo(1);
        task.setNodeKey("PENDING_DEPT");
        task.setNodeName("部门审核");
        task.setTaskStatus("PENDING");
        task.setCandidateRoleCode("DEPT_LEADER");
        task.setCandidateOrgId(apply.getOrgId());
        task.setSortNo(1);
        task.setCreatedBy(currentUser.getUserId());
        taskMapper.insert(task);

        writeApprovalRecord("TRANSITION", id, "部门审核", "DEPT", currentUser, null, null);
    }

    @Override
    @Transactional
    public void approveTransitionApply(Long id, ApprovalOpinionRequest request) {
        HrTransitionApply apply = transitionApplyMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException("转正申请不存在");
        }

        AuthUser currentUser = getCurrentAuthUser();
        String currentStatus = apply.getProcessStatus();
        String opinion = request.opinion();
        String result = request.result();

        requireCanApproveTransition(apply);

        String nextNodeKey = null;
        String nextNodeName = null;

        switch (currentStatus) {
            case "PENDING_DEPT" -> {
                apply.setDraftOpinion(opinion);
                apply.setProcessStatus("PENDING_HR");
                apply.setCurrentNode("人事审核");
                nextNodeKey = "PENDING_HR";
                nextNodeName = "人事审核";
                writeApprovalRecord("TRANSITION", id, "部门审核", "DEPT", currentUser, opinion, result);
            }
            case "PENDING_HR" -> {
                apply.setHrOpinion(opinion);
                apply.setProcessStatus("PENDING_COMPANY");
                apply.setCurrentNode("公司审批");
                nextNodeKey = "PENDING_COMPANY";
                nextNodeName = "公司审批";
                writeApprovalRecord("TRANSITION", id, "人事审核", "HR", currentUser, opinion, result);
            }
            case "PENDING_COMPANY" -> {
                apply.setCompanyOpinion(opinion);
                apply.setProcessStatus("PENDING_ADMIN");
                apply.setCurrentNode("行政审批");
                nextNodeKey = "PENDING_ADMIN";
                nextNodeName = "行政审批";
                writeApprovalRecord("TRANSITION", id, "公司审批", "COMPANY", currentUser, opinion, result);
            }
            case "PENDING_ADMIN" -> {
                apply.setAdminOpinion(opinion);
                apply.setProcessStatus("APPROVED");
                apply.setCurrentNode("已办结");
                apply.setCompleteTime(LocalDateTime.now());
                nextNodeKey = "APPROVED";
                writeApprovalRecord("TRANSITION", id, "行政审批", "ADMIN", currentUser, opinion, result);

                completeTransitionApply(apply, currentUser);
            }
            default -> throw new BusinessException("当前状态不允许审批");
        }

        apply.setUpdatedBy(currentUser.getUserId());
        transitionApplyMapper.updateById(apply);

        // Update WfTask and WfProcessInstance
        LambdaQueryWrapper<WfTask> taskWrapper = Wrappers.lambdaQuery();
        taskWrapper.eq(WfTask::getBizType, "TRANSITION")
                .eq(WfTask::getBizId, id)
                .eq(WfTask::getTaskStatus, "PENDING");
        WfTask currentTask = taskMapper.selectOne(taskWrapper);
        if (currentTask != null) {
            currentTask.setTaskStatus("DONE");
            currentTask.setAssigneeId(currentUser.getUserId());
            currentTask.setAssigneeName(currentUser.getRealName());
            currentTask.setActionResult(result);
            currentTask.setActionOpinion(opinion);
            currentTask.setActionTime(LocalDateTime.now());
            taskMapper.updateById(currentTask);
        }

        LambdaQueryWrapper<WfProcessInstance> piWrapper = Wrappers.lambdaQuery();
        piWrapper.eq(WfProcessInstance::getBizType, "TRANSITION")
                .eq(WfProcessInstance::getBizId, id);
        WfProcessInstance process = processInstanceMapper.selectOne(piWrapper);
        if (process != null) {
            if ("APPROVED".equals(nextNodeKey)) {
                process.setOverallStatus("APPROVED");
                process.setCompletedAt(LocalDateTime.now());
            } else if (nextNodeKey != null) {
                process.setCurrentNodeKey(nextNodeKey);
                process.setCurrentNodeName(nextNodeName);
                process.setLastActionAt(LocalDateTime.now());

                WfTask nextTask = new WfTask();
                nextTask.setProcessId(process.getId());
                nextTask.setBizType("TRANSITION");
                nextTask.setBizId(id);
                nextTask.setRoundNo(currentTask != null ? currentTask.getRoundNo() : 1);
                nextTask.setNodeKey(nextNodeKey);
                nextTask.setNodeName(nextNodeName);
                nextTask.setTaskStatus("PENDING");
                nextTask.setCandidateRoleCode(getNextRoleCode(nextNodeKey));
                nextTask.setCandidateOrgId(currentTask != null ? currentTask.getCandidateOrgId() : apply.getOrgId());
                nextTask.setSortNo(currentTask != null ? currentTask.getSortNo() + 1 : 1);
                nextTask.setCreatedBy(currentUser.getUserId());
                taskMapper.insert(nextTask);
            }
            process.setUpdatedBy(currentUser.getUserId());
            processInstanceMapper.updateById(process);
        }

        WfActionLog log = new WfActionLog();
        log.setProcessId(process != null ? process.getId() : null);
        log.setBizType("TRANSITION");
        log.setBizId(id);
        log.setRoundNo(currentTask != null ? currentTask.getRoundNo() : 1);
        log.setActionCode("APPROVE");
        log.setNodeKey(currentTask != null ? currentTask.getNodeKey() : currentStatus);
        log.setNodeName(currentTask != null ? currentTask.getNodeName() : apply.getCurrentNode());
        log.setOperatorId(currentUser.getUserId());
        log.setOperatorName(currentUser.getRealName());
        log.setActionOpinion(opinion);
        log.setActionResult(result);
        log.setActionTime(LocalDateTime.now());
        log.setCreatedBy(currentUser.getUserId());
        actionLogMapper.insert(log);
    }

    private String getNextRoleCode(String nodeKey) {
        return switch (nodeKey) {
            case "PENDING_HR" -> "HR";
            case "PENDING_COMPANY" -> "COMPANY_APPROVER";
            case "PENDING_ADMIN" -> "ADMIN";
            default -> "DEPT_LEADER";
        };
    }

    private boolean canCurrentUserApproveTransition(HrTransitionApply apply) {
        if (!apply.getProcessStatus().startsWith("PENDING")) {
            return false;
        }
        AuthUser currentUser;
        try {
            currentUser = getCurrentAuthUser();
        } catch (BusinessException e) {
            return false;
        }
        String status = apply.getProcessStatus();
        switch (status) {
            case "PENDING_DEPT" -> {
                return hasRole(currentUser, "HR") || hasRole(currentUser, "ADMIN") || isSameOrg(currentUser, apply.getOrgId());
            }
            case "PENDING_HR" -> {
                return hasRole(currentUser, "HR") || hasRole(currentUser, "ADMIN");
            }
            case "PENDING_COMPANY" -> {
                return hasRole(currentUser, "COMPANY_APPROVER") || hasRole(currentUser, "ADMIN");
            }
            case "PENDING_ADMIN" -> {
                return hasRole(currentUser, "ADMIN");
            }
            default -> {
                return false;
            }
        }
    }

    private boolean canCurrentUserApproveSalaryAdjust(HrSalaryAdjustApply apply) {
        if (!apply.getProcessStatus().startsWith("PENDING")) {
            return false;
        }
        AuthUser currentUser;
        try {
            currentUser = getCurrentAuthUser();
        } catch (BusinessException e) {
            return false;
        }
        String status = apply.getProcessStatus();
        switch (status) {
            case "PENDING_DEPT" -> {
                return hasRole(currentUser, "HR") || hasRole(currentUser, "ADMIN") || isSameOrg(currentUser, apply.getOrgId());
            }
            case "PENDING_HR" -> {
                return hasRole(currentUser, "HR") || hasRole(currentUser, "ADMIN");
            }
            case "PENDING_LEADER" -> {
                return hasRole(currentUser, "LEADER") || hasRole(currentUser, "ADMIN");
            }
            case "PENDING_SCHOOL" -> {
                return hasRole(currentUser, "SCHOOL_LEADER") || hasRole(currentUser, "ADMIN");
            }
            case "PENDING_FINANCE" -> {
                return hasRole(currentUser, "FINANCE") || hasRole(currentUser, "ADMIN");
            }
            case "PENDING_COMPANY" -> {
                return hasRole(currentUser, "COMPANY_APPROVER") || hasRole(currentUser, "ADMIN");
            }
            case "PENDING_ADMIN" -> {
                return hasRole(currentUser, "ADMIN");
            }
            default -> {
                return false;
            }
        }
    }

    private void requireCanApproveTransition(HrTransitionApply apply) {
        if (!canCurrentUserApproveTransition(apply)) {
            throw new BusinessException("无权审批此申请");
        }
    }

    private void requireCanApproveSalaryAdjust(HrSalaryAdjustApply apply) {
        if (!canCurrentUserApproveSalaryAdjust(apply)) {
            throw new BusinessException("无权审批此申请");
        }
    }

    private boolean hasRole(AuthUser user, String role) {
        if (user == null || user.getRoles() == null) {
            return false;
        }
        if (user.isSuperAdmin()) {
            return true;
        }
        return user.getRoles().contains(role);
    }

    private boolean isSameOrg(AuthUser user, Long applyOrgId) {
        if (user == null || applyOrgId == null) {
            return false;
        }
        return applyOrgId.equals(user.getOrgId());
    }

    @Override
    @Transactional
    public void rejectTransitionApply(Long id, ApprovalOpinionRequest request) {
        HrTransitionApply apply = transitionApplyMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException("转正申请不存在");
        }

        String currentStatus = apply.getProcessStatus();
        if (!currentStatus.startsWith("PENDING")) {
            throw new BusinessException("当前状态不允许驳回");
        }

        AuthUser currentUser = getCurrentAuthUser();

        // Validate approver has appropriate role/identity for reject as well
        requireCanApproveTransition(apply);

        switch (currentStatus) {
            case "PENDING_DEPT" -> {
                apply.setDraftOpinion(request.opinion());
                writeApprovalRecord("TRANSITION", id, "部门审核", "DEPT", currentUser, request.opinion(), "REJECT");
            }
            case "PENDING_HR" -> {
                apply.setHrOpinion(request.opinion());
                writeApprovalRecord("TRANSITION", id, "人事审核", "HR", currentUser, request.opinion(), "REJECT");
            }
            case "PENDING_COMPANY" -> {
                apply.setCompanyOpinion(request.opinion());
                writeApprovalRecord("TRANSITION", id, "公司审批", "COMPANY", currentUser, request.opinion(), "REJECT");
            }
            case "PENDING_ADMIN" -> {
                apply.setAdminOpinion(request.opinion());
                writeApprovalRecord("TRANSITION", id, "行政审批", "ADMIN", currentUser, request.opinion(), "REJECT");
            }
        }

        apply.setProcessStatus("REJECTED");
        apply.setUpdatedBy(currentUser.getUserId());
        transitionApplyMapper.updateById(apply);

        LambdaQueryWrapper<WfTask> taskWrapper = Wrappers.lambdaQuery();
        taskWrapper.eq(WfTask::getBizType, "TRANSITION")
                .eq(WfTask::getBizId, id)
                .eq(WfTask::getTaskStatus, "PENDING");
        WfTask currentTask = taskMapper.selectOne(taskWrapper);
        if (currentTask != null) {
            currentTask.setTaskStatus("REJECTED");
            currentTask.setAssigneeId(currentUser.getUserId());
            currentTask.setAssigneeName(currentUser.getRealName());
            currentTask.setActionResult("REJECT");
            currentTask.setActionOpinion(request.opinion());
            currentTask.setActionTime(LocalDateTime.now());
            taskMapper.updateById(currentTask);
        }

        LambdaQueryWrapper<WfProcessInstance> piWrapper = Wrappers.lambdaQuery();
        piWrapper.eq(WfProcessInstance::getBizType, "TRANSITION")
                .eq(WfProcessInstance::getBizId, id);
        WfProcessInstance process = processInstanceMapper.selectOne(piWrapper);
        if (process != null) {
            process.setOverallStatus("REJECTED");
            process.setCompletedAt(LocalDateTime.now());
            process.setUpdatedBy(currentUser.getUserId());
            processInstanceMapper.updateById(process);
        }
    }

    @Override
    public PageResponse<SalaryAdjustApplyVO> pageSalaryAdjustApplies(SalaryAdjustApplyPageQuery query) {
        AuthUser currentUser = getCurrentAuthUser();
        LambdaQueryWrapper<HrSalaryAdjustApply> wrapper = Wrappers.lambdaQuery();

        if ("my".equals(query.getScope())) {
            wrapper.eq(HrSalaryAdjustApply::getApplicantId, currentUser.getUserId());
        }

        wrapper.like(StringUtils.hasText(query.getKeyword()), HrSalaryAdjustApply::getEmployeeName, query.getKeyword())
                .eq(StringUtils.hasText(query.getProcessStatus()), HrSalaryAdjustApply::getProcessStatus, query.getProcessStatus())
                .orderByDesc(HrSalaryAdjustApply::getCreatedAt);

        Page<HrSalaryAdjustApply> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<HrSalaryAdjustApply> result = salaryAdjustApplyMapper.selectPage(page, wrapper);

        List<SalaryAdjustApplyVO> records = result.getRecords().stream().map(a -> toSalaryAdjustApplyVO(a, null)).collect(Collectors.toList());
        return new PageResponse<>(records, result.getTotal(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public SalaryAdjustApplyVO getSalaryAdjustApply(Long id) {
        HrSalaryAdjustApply apply = salaryAdjustApplyMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException("调薪申请不存在");
        }
        return toSalaryAdjustApplyVO(apply, canCurrentUserApproveSalaryAdjust(apply));
    }

    @Override
    @Transactional
    public void createSalaryAdjustApply(SalaryAdjustApplyCreateRequest request) {
        AuthUser currentUser = getCurrentAuthUser();
        HrEmployee employee = employeeMapper.selectById(request.employeeId());
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }

        BigDecimal currentSalary = getEmployeeCurrentSalary(request.employeeId());
        if (currentSalary == null) {
            throw new BusinessException("该员工没有工资标准，请先在工资管理中维护");
        }

        BigDecimal newSalary = currentSalary.add(request.adjustAmount());
        if (newSalary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("调薪后薪资不能为负数或零");
        }

        String applyNo = generateApplyNo("JX");

        HrSalaryAdjustApply apply = new HrSalaryAdjustApply();
        apply.setApplyNo(applyNo);
        apply.setEmployeeId(request.employeeId());
        apply.setEmployeeName(employee.getRealName());
        apply.setDepartment(getOrgName(employee.getOrgId()));
        apply.setOrgId(employee.getOrgId());
        apply.setCurrentSalary(currentSalary);
        apply.setAdjustAmount(request.adjustAmount());
        apply.setNewSalary(newSalary);
        apply.setApplyReason(request.applyReason());
        apply.setDraftOpinion(request.draftOpinion());
        apply.setProcessStatus("DRAFT");
        apply.setApplicantId(currentUser.getUserId());
        apply.setApplyTime(LocalDateTime.now());
        apply.setCreatedBy(currentUser.getUserId());

        salaryAdjustApplyMapper.insert(apply);
    }

    @Override
    @Transactional
    public void updateSalaryAdjustApply(Long id, SalaryAdjustApplyUpdateRequest request) {
        HrSalaryAdjustApply apply = salaryAdjustApplyMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException("调薪申请不存在");
        }
        if (!"DRAFT".equals(apply.getProcessStatus()) && !"REJECTED".equals(apply.getProcessStatus())) {
            throw new BusinessException("只有草稿或驳回状态可以编辑");
        }

        if (request.adjustAmount() != null) {
            apply.setAdjustAmount(request.adjustAmount());
            if (apply.getCurrentSalary() != null) {
                apply.setNewSalary(apply.getCurrentSalary().add(request.adjustAmount()));
            }
        }
        if (request.applyReason() != null) apply.setApplyReason(request.applyReason());
        if (request.draftOpinion() != null) apply.setDraftOpinion(request.draftOpinion());

        AuthUser currentUser = getCurrentAuthUser();
        apply.setUpdatedBy(currentUser.getUserId());
        salaryAdjustApplyMapper.updateById(apply);
    }

    @Override
    @Transactional
    public void submitSalaryAdjustApply(Long id) {
        HrSalaryAdjustApply apply = salaryAdjustApplyMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException("调薪申请不存在");
        }
        if (!"DRAFT".equals(apply.getProcessStatus()) && !"REJECTED".equals(apply.getProcessStatus())) {
            throw new BusinessException("只有草稿或驳回状态可以提交");
        }

        apply.setProcessStatus("PENDING_DEPT");
        apply.setCurrentNode("部门审核");
        apply.setSubmitTime(LocalDateTime.now());

        AuthUser currentUser = getCurrentAuthUser();
        apply.setUpdatedBy(currentUser.getUserId());
        salaryAdjustApplyMapper.updateById(apply);

        WfProcessInstance process = new WfProcessInstance();
        process.setBizType("SALARY_ADJUST");
        process.setBizId(id);
        process.setBizNo(apply.getApplyNo());
        process.setTitle("调薪申请：" + apply.getEmployeeName());
        process.setApplicantId(apply.getApplicantId());
        process.setApplicantName(currentUser.getRealName());
        process.setApplicantOrgId(apply.getOrgId());
        process.setApplicantOrgName(null);
        process.setOverallStatus("PENDING");
        process.setCurrentNodeKey("PENDING_DEPT");
        process.setCurrentNodeName("部门审核");
        process.setCurrentRound(1);
        process.setStartedAt(LocalDateTime.now());
        process.setLastActionAt(LocalDateTime.now());
        process.setCreatedBy(currentUser.getUserId());
        processInstanceMapper.insert(process);

        WfTask task = new WfTask();
        task.setProcessId(process.getId());
        task.setBizType("SALARY_ADJUST");
        task.setBizId(id);
        task.setRoundNo(1);
        task.setNodeKey("PENDING_DEPT");
        task.setNodeName("部门审核");
        task.setTaskStatus("PENDING");
        task.setCandidateRoleCode("DEPT_LEADER");
        task.setCandidateOrgId(apply.getOrgId());
        task.setSortNo(1);
        task.setCreatedBy(currentUser.getUserId());
        taskMapper.insert(task);

        writeApprovalRecord("SALARY_ADJUST", id, "部门审核", "DEPT", currentUser, null, null);
    }

    @Override
    @Transactional
    public void approveSalaryAdjustApply(Long id, ApprovalOpinionRequest request) {
        HrSalaryAdjustApply apply = salaryAdjustApplyMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException("调薪申请不存在");
        }

        AuthUser currentUser = getCurrentAuthUser();
        String currentStatus = apply.getProcessStatus();
        String opinion = request.opinion();
        String result = request.result();

        requireCanApproveSalaryAdjust(apply);

        String nextNodeKey = null;
        String nextNodeName = null;

        switch (currentStatus) {
            case "PENDING_DEPT" -> {
                apply.setDeptOpinion(opinion);
                apply.setProcessStatus("PENDING_HR");
                apply.setCurrentNode("人事审核");
                nextNodeKey = "PENDING_HR";
                nextNodeName = "人事审核";
                writeApprovalRecord("SALARY_ADJUST", id, "部门审核", "DEPT", currentUser, opinion, result);
            }
            case "PENDING_HR" -> {
                apply.setHrOpinion(opinion);
                apply.setProcessStatus("PENDING_LEADER");
                apply.setCurrentNode("分管领导");
                nextNodeKey = "PENDING_LEADER";
                nextNodeName = "分管领导";
                writeApprovalRecord("SALARY_ADJUST", id, "人事审核", "HR", currentUser, opinion, result);
            }
            case "PENDING_LEADER" -> {
                apply.setLeaderOpinion(opinion);
                apply.setProcessStatus("PENDING_SCHOOL");
                apply.setCurrentNode("校领导");
                nextNodeKey = "PENDING_SCHOOL";
                nextNodeName = "校领导";
                writeApprovalRecord("SALARY_ADJUST", id, "分管领导", "LEADER", currentUser, opinion, result);
            }
            case "PENDING_SCHOOL" -> {
                apply.setSchoolLeaderOpinion(opinion);
                apply.setProcessStatus("PENDING_FINANCE");
                apply.setCurrentNode("财务办理");
                nextNodeKey = "PENDING_FINANCE";
                nextNodeName = "财务办理";
                writeApprovalRecord("SALARY_ADJUST", id, "校领导", "SCHOOL", currentUser, opinion, result);
            }
            case "PENDING_FINANCE" -> {
                apply.setFinanceOpinion(opinion);
                apply.setProcessStatus("APPROVED");
                apply.setCurrentNode("已办结");
                apply.setEffectiveDate(LocalDate.now());
                apply.setCompleteTime(LocalDateTime.now());
                nextNodeKey = "APPROVED";
                writeApprovalRecord("SALARY_ADJUST", id, "财务办理", "FINANCE", currentUser, opinion, result);

                completeSalaryAdjustApply(apply, currentUser);
            }
            default -> throw new BusinessException("当前状态不允许审批");
        }

        apply.setUpdatedBy(currentUser.getUserId());
        salaryAdjustApplyMapper.updateById(apply);

        LambdaQueryWrapper<WfTask> taskWrapper = Wrappers.lambdaQuery();
        taskWrapper.eq(WfTask::getBizType, "SALARY_ADJUST")
                .eq(WfTask::getBizId, id)
                .eq(WfTask::getTaskStatus, "PENDING");
        WfTask currentTask = taskMapper.selectOne(taskWrapper);
        if (currentTask != null) {
            currentTask.setTaskStatus("DONE");
            currentTask.setAssigneeId(currentUser.getUserId());
            currentTask.setAssigneeName(currentUser.getRealName());
            currentTask.setActionResult(result);
            currentTask.setActionOpinion(opinion);
            currentTask.setActionTime(LocalDateTime.now());
            taskMapper.updateById(currentTask);
        }

        LambdaQueryWrapper<WfProcessInstance> piWrapper = Wrappers.lambdaQuery();
        piWrapper.eq(WfProcessInstance::getBizType, "SALARY_ADJUST")
                .eq(WfProcessInstance::getBizId, id);
        WfProcessInstance process = processInstanceMapper.selectOne(piWrapper);
        if (process != null) {
            if ("APPROVED".equals(nextNodeKey)) {
                process.setOverallStatus("APPROVED");
                process.setCompletedAt(LocalDateTime.now());
            } else if (nextNodeKey != null) {
                process.setCurrentNodeKey(nextNodeKey);
                process.setCurrentNodeName(nextNodeName);
                process.setLastActionAt(LocalDateTime.now());

                WfTask nextTask = new WfTask();
                nextTask.setProcessId(process.getId());
                nextTask.setBizType("SALARY_ADJUST");
                nextTask.setBizId(id);
                nextTask.setRoundNo(currentTask != null ? currentTask.getRoundNo() : 1);
                nextTask.setNodeKey(nextNodeKey);
                nextTask.setNodeName(nextNodeName);
                nextTask.setTaskStatus("PENDING");
                nextTask.setCandidateRoleCode(getSalaryNextRoleCode(nextNodeKey));
                nextTask.setCandidateOrgId(currentTask != null ? currentTask.getCandidateOrgId() : apply.getOrgId());
                nextTask.setSortNo(currentTask != null ? currentTask.getSortNo() + 1 : 1);
                nextTask.setCreatedBy(currentUser.getUserId());
                taskMapper.insert(nextTask);
            }
            process.setUpdatedBy(currentUser.getUserId());
            processInstanceMapper.updateById(process);
        }

        WfActionLog log = new WfActionLog();
        log.setProcessId(process != null ? process.getId() : null);
        log.setBizType("SALARY_ADJUST");
        log.setBizId(id);
        log.setRoundNo(currentTask != null ? currentTask.getRoundNo() : 1);
        log.setActionCode("APPROVE");
        log.setNodeKey(currentTask != null ? currentTask.getNodeKey() : currentStatus);
        log.setNodeName(currentTask != null ? currentTask.getNodeName() : apply.getCurrentNode());
        log.setOperatorId(currentUser.getUserId());
        log.setOperatorName(currentUser.getRealName());
        log.setActionOpinion(opinion);
        log.setActionResult(result);
        log.setActionTime(LocalDateTime.now());
        log.setCreatedBy(currentUser.getUserId());
        actionLogMapper.insert(log);
    }

    private String getSalaryNextRoleCode(String nodeKey) {
        return switch (nodeKey) {
            case "PENDING_HR" -> "HR";
            case "PENDING_LEADER" -> "LEADER";
            case "PENDING_SCHOOL" -> "SCHOOL_LEADER";
            case "PENDING_FINANCE" -> "FINANCE";
            default -> "DEPT_LEADER";
        };
    }

    @Override
    @Transactional
    public void rejectSalaryAdjustApply(Long id, ApprovalOpinionRequest request) {
        HrSalaryAdjustApply apply = salaryAdjustApplyMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException("调薪申请不存在");
        }

        String currentStatus = apply.getProcessStatus();
        if (!currentStatus.startsWith("PENDING")) {
            throw new BusinessException("当前状态不允许驳回");
        }

        AuthUser currentUser = getCurrentAuthUser();

        // Validate approver has appropriate role/identity for reject as well
        requireCanApproveSalaryAdjust(apply);

        switch (currentStatus) {
            case "PENDING_DEPT" -> {
                apply.setDraftOpinion(request.opinion());
                writeApprovalRecord("SALARY_ADJUST", id, "部门审核", "DEPT", currentUser, request.opinion(), "REJECT");
            }
            case "PENDING_HR" -> {
                apply.setHrOpinion(request.opinion());
                writeApprovalRecord("SALARY_ADJUST", id, "人事审核", "HR", currentUser, request.opinion(), "REJECT");
            }
            case "PENDING_LEADER" -> {
                apply.setLeaderOpinion(request.opinion());
                writeApprovalRecord("SALARY_ADJUST", id, "分管领导", "LEADER", currentUser, request.opinion(), "REJECT");
            }
            case "PENDING_SCHOOL" -> {
                apply.setSchoolLeaderOpinion(request.opinion());
                writeApprovalRecord("SALARY_ADJUST", id, "校领导", "SCHOOL", currentUser, request.opinion(), "REJECT");
            }
            case "PENDING_FINANCE" -> {
                apply.setFinanceOpinion(request.opinion());
                writeApprovalRecord("SALARY_ADJUST", id, "财务办理", "FINANCE", currentUser, request.opinion(), "REJECT");
            }
        }

        apply.setProcessStatus("REJECTED");
        apply.setUpdatedBy(currentUser.getUserId());
        salaryAdjustApplyMapper.updateById(apply);

        LambdaQueryWrapper<WfTask> taskWrapper = Wrappers.lambdaQuery();
        taskWrapper.eq(WfTask::getBizType, "SALARY_ADJUST")
                .eq(WfTask::getBizId, id)
                .eq(WfTask::getTaskStatus, "PENDING");
        WfTask currentTask = taskMapper.selectOne(taskWrapper);
        if (currentTask != null) {
            currentTask.setTaskStatus("REJECTED");
            currentTask.setAssigneeId(currentUser.getUserId());
            currentTask.setAssigneeName(currentUser.getRealName());
            currentTask.setActionResult("REJECT");
            currentTask.setActionOpinion(request.opinion());
            currentTask.setActionTime(LocalDateTime.now());
            taskMapper.updateById(currentTask);
        }

        LambdaQueryWrapper<WfProcessInstance> piWrapper = Wrappers.lambdaQuery();
        piWrapper.eq(WfProcessInstance::getBizType, "SALARY_ADJUST")
                .eq(WfProcessInstance::getBizId, id);
        WfProcessInstance process = processInstanceMapper.selectOne(piWrapper);
        if (process != null) {
            process.setOverallStatus("REJECTED");
            process.setCompletedAt(LocalDateTime.now());
            process.setUpdatedBy(currentUser.getUserId());
            processInstanceMapper.updateById(process);
        }
    }

    @Override
    public List<ApprovalRecordVO> getApprovalRecords(String bizType, Long bizId) {
        LambdaQueryWrapper<HrApprovalRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HrApprovalRecord::getBizType, bizType)
                .eq(HrApprovalRecord::getBizId, bizId)
                .orderByAsc(HrApprovalRecord::getCreatedAt);

        return approvalRecordMapper.selectList(wrapper).stream()
                .map(r -> new ApprovalRecordVO(r.getId(), r.getBizType(), r.getBizId(),
                        r.getNodeName(), r.getNodeKey(), r.getOperatorId(),
                        r.getOperatorName(), r.getOperateTime(), r.getOpinion(), r.getResult()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AttachmentVO> listWorkflowAttachments(String bizType, Long bizId) {
        validateUserCanAccessWorkflow(bizType, bizId);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.huacai.file.entity.SysFile> wrapper =
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("biz_type", bizType.trim());
        wrapper.eq("biz_id", bizId);
        wrapper.orderByDesc("created_at");
        List<com.huacai.file.entity.SysFile> fileList = fileMapper.selectList(wrapper);
        return fileList.stream().map(f -> {
            String uploaderName = null;
            if (f.getUploaderId() != null) {
                com.huacai.system.entity.SysUser uploader = userMapper.selectById(f.getUploaderId());
                if (uploader != null) uploaderName = uploader.getRealName();
            }
            return new AttachmentVO(
                f.getId(), f.getBizType(), f.getBizId(), f.getFileName(),
                f.getFileSize(), null, f.getFileExt(),
                f.getUploaderId(), uploaderName, f.getCreatedAt()
            );
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteWorkflowAttachment(Long id, String bizType, Long bizId) {
        validateUserCanAccessWorkflow(bizType, bizId);
        com.huacai.file.entity.SysFile file = fileMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.huacai.file.entity.SysFile>()
                .eq("id", id).eq("biz_type", bizType.trim()).eq("biz_id", bizId));
        if (file == null) {
            throw new BusinessException("附件不存在或无权删除");
        }
        AuthUser currentUser = getCurrentAuthUser();
        if (!currentUser.getUserId().equals(file.getUploaderId())) {
            throw new BusinessException("无权删除此附件");
        }
        fileMapper.deleteById(id);
    }

    @Override
    public AttachmentVO uploadWorkflowAttachment(String bizType, Long bizId, String fileName, long fileSize, String storedPath, String fileExt) {
        validateUserCanAccessWorkflow(bizType, bizId);
        AuthUser currentUser = getCurrentAuthUser();
        com.huacai.file.entity.SysFile entity = new com.huacai.file.entity.SysFile();
        entity.setBizType(bizType.trim());
        entity.setBizId(bizId);
        entity.setFileName(fileName);
        entity.setFilePath(storedPath);
        entity.setFileExt(fileExt);
        entity.setFileSize(fileSize);
        entity.setStorageType("LOCAL");
        entity.setUploaderId(currentUser.getUserId());
        fileMapper.insert(entity);
        return new AttachmentVO(
            entity.getId(), entity.getBizType(), entity.getBizId(), entity.getFileName(),
            entity.getFileSize(), null, entity.getFileExt(),
            entity.getUploaderId(), currentUser.getRealName(), entity.getCreatedAt()
        );
    }

    private void validateUserCanAccessWorkflow(String bizType, Long bizId) {
        if (bizId == null || !StringUtils.hasText(bizType)) {
            throw new BusinessException("参数无效");
        }
        AuthUser currentUser = getCurrentAuthUser();
        if ("TRANSITION".equals(bizType)) {
            HrTransitionApply apply = transitionApplyMapper.selectById(bizId);
            if (apply == null) throw new BusinessException("转正申请不存在");
            if (!hasAccessToTransition(apply, currentUser)) {
                throw new BusinessException("无权访问此转正申请");
            }
        } else if ("SALARY_ADJUST".equals(bizType)) {
            HrSalaryAdjustApply apply = salaryAdjustApplyMapper.selectById(bizId);
            if (apply == null) throw new BusinessException("调薪申请不存在");
            if (!hasAccessToSalaryAdjust(apply, currentUser)) {
                throw new BusinessException("无权访问此调薪申请");
            }
        } else if ("REIMBURSEMENT".equals(bizType)) {
            com.huacai.workflow.entity.FinReimbursementApply apply =
                reimbursementApplyMapper.selectById(bizId);
            if (apply == null) throw new BusinessException("报销申请不存在");
            if (!hasAccessToReimbursement(apply, currentUser)) {
                throw new BusinessException("无权访问此报销申请");
            }
        } else {
            throw new BusinessException("不支持的业务类型");
        }
    }

    private boolean hasAccessToReimbursement(com.huacai.workflow.entity.FinReimbursementApply apply, AuthUser user) {
        if (user.isSuperAdmin()) return true;
        if (apply.getApplicantId() != null && apply.getApplicantId().equals(user.getUserId())) return true;
        if (apply.getApplicantOrgId() != null && apply.getApplicantOrgId().equals(user.getOrgId())) return true;
        return false;
    }

    private boolean hasAccessToTransition(HrTransitionApply apply, AuthUser user) {
        if (user.isSuperAdmin()) return true;
        if (apply.getApplicantId() != null && apply.getApplicantId().equals(user.getUserId())) return true;
        if (apply.getEmployeeId() != null) {
            HrEmployee emp = employeeMapper.selectById(apply.getEmployeeId());
            if (emp != null && emp.getOrgId() != null && emp.getOrgId().equals(user.getOrgId())) return true;
        }
        return false;
    }

    private boolean hasAccessToSalaryAdjust(HrSalaryAdjustApply apply, AuthUser user) {
        if (user.isSuperAdmin()) return true;
        if (apply.getApplicantId() != null && apply.getApplicantId().equals(user.getUserId())) return true;
        if (apply.getEmployeeId() != null) {
            HrEmployee emp = employeeMapper.selectById(apply.getEmployeeId());
            if (emp != null && emp.getOrgId() != null && emp.getOrgId().equals(user.getOrgId())) return true;
        }
        return false;
    }

    @Override
    public PageResponse<ManagementRecordVO> pageManagementRecords(ManagementRecordPageQuery query) {
        AuthUser currentUser = getCurrentAuthUser();
        if (!currentUser.isSuperAdmin()) {
            throw new BusinessException("无权访问管理记录");
        }
        LambdaQueryWrapper<HrManagementRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(query.getEmployeeId() != null, HrManagementRecord::getEmployeeId, query.getEmployeeId())
                .eq(StringUtils.hasText(query.getRecordType()), HrManagementRecord::getRecordType, query.getRecordType())
                .orderByDesc(HrManagementRecord::getOperatedAt);

        Page<HrManagementRecord> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<HrManagementRecord> result = managementRecordMapper.selectPage(page, wrapper);

        List<ManagementRecordVO> records = result.getRecords().stream()
                .map(r -> new ManagementRecordVO(r.getId(), r.getEmployeeId(), r.getEmployeeName(),
                        r.getRecordType(), r.getContent(), r.getOperatorId(),
                        r.getOperatorName(), r.getOperatedAt(), r.getRemark()))
                .collect(Collectors.toList());
        return new PageResponse<>(records, result.getTotal(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public List<EmployeeVO> listEmployees(String keyword) {
        LambdaQueryWrapper<HrEmployee> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StringUtils.hasText(keyword), HrEmployee::getRealName, keyword)
                .or(StringUtils.hasText(keyword))
                .like(HrEmployee::getEmployeeCode, keyword)
                .eq(HrEmployee::getEmploymentStatus, "ONBOARD")
                .orderByDesc(HrEmployee::getCreatedAt)
                .last("LIMIT 50");

        return employeeMapper.selectList(wrapper).stream().map(this::toEmployeeVO).collect(Collectors.toList());
    }

    private EmployeeVO toEmployeeVO(HrEmployee e) {
        String orgName = null;
        if (e.getOrgId() != null) {
            SysOrg org = orgMapper.selectById(e.getOrgId());
            orgName = org != null ? org.getOrgName() : null;
        }
        return new EmployeeVO(e.getId(), e.getEmployeeCode(), e.getRealName(), e.getGender(),
                e.getIdCardNo(), e.getBirthday(), e.getAge(), e.getNation(), e.getPoliticalStatus(),
                e.getHometown(), e.getMaritalStatus(), e.getPhone(), e.getEmail(),
                e.getGraduateSchool(), e.getHighestEducation(), e.getWorkStartDate(),
                e.getHomeAddress(), e.getEmergencyContact(), e.getEmergencyContactPhone(),
                e.getBankCardNo(), e.getIdPhotoUrl(), e.getEmploymentStatus(), e.getTalentFlag(),
                e.getCreateSystemAccount(), e.getSystemUsername(), e.getOrgId(), orgName,
                e.getJobTitle(), e.getRemark(), e.getCreatedAt(), e.getUpdatedAt());
    }

    private SalaryStandardVO toSalaryStandardVO(HrSalaryStandard s) {
        return new SalaryStandardVO(s.getId(), s.getSalaryName(), s.getAmount(), s.getJobTitle(),
                s.getOrgId(), s.getOrgName(), s.getDescription(), s.getSortNo(), s.getStatus(),
                s.getEffectiveDate(), s.getExpireDate(), s.getVersionNo(), s.getApplicableScope(),
                s.getCreatedAt(), s.getUpdatedAt());
    }

    private TransitionApplyVO toTransitionApplyVO(HrTransitionApply a, Boolean canApprove) {
        return new TransitionApplyVO(a.getId(), a.getApplyNo(), a.getEmployeeId(), a.getEmployeeCode(),
                a.getEmployeeName(), a.getPhone(), a.getDepartment(), a.getOrgId(), a.getPosition(),
                a.getJoinDate(), a.getExpectedFormalDate(), a.getApplyReason(), a.getSelfEvaluation(),
                a.getGrowth(), a.getShortcomings(), a.getDevelopmentSuggestion(), a.getDraftOpinion(),
                a.getHrOpinion(), a.getCompanyOpinion(), a.getAdminOpinion(), a.getProcessStatus(),
                a.getCurrentNode(), a.getApplicantId(), getApplicantName(a.getApplicantId()),
                a.getApplyTime(), a.getSubmitTime(), a.getCompleteTime(), a.getCreatedAt(), canApprove);
    }

    private SalaryAdjustApplyVO toSalaryAdjustApplyVO(HrSalaryAdjustApply a, Boolean canApprove) {
        return new SalaryAdjustApplyVO(a.getId(), a.getApplyNo(), a.getEmployeeId(), a.getEmployeeName(),
                a.getDepartment(), a.getOrgId(), a.getCurrentSalary(), a.getAdjustAmount(), a.getNewSalary(),
                a.getApplyReason(), a.getDraftOpinion(), a.getDeptOpinion(), a.getHrOpinion(),
                a.getLeaderOpinion(), a.getSchoolLeaderOpinion(), a.getFinanceOpinion(), a.getProcessStatus(),
                a.getCurrentNode(), a.getApplicantId(), getApplicantName(a.getApplicantId()),
                a.getApplyTime(), a.getSubmitTime(), a.getEffectiveDate(), a.getCompleteTime(), a.getCreatedAt(), canApprove);
    }

    private JobInfoVO getJobInfoByEmployeeId(Long employeeId) {
        LambdaQueryWrapper<HrEmployeeJobInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HrEmployeeJobInfo::getEmployeeId, employeeId).last("LIMIT 1");
        HrEmployeeJobInfo j = jobInfoMapper.selectOne(wrapper);
        if (j == null) return null;
        return new JobInfoVO(j.getId(), j.getEmployeeId(), j.getEmployeeCode(), j.getJoinDate(),
                j.getFormalDate(), j.getWorkUnit(), j.getWorkMode(), j.getBorrowDispatchDate(),
                j.getDepartment(), j.getRankLevel(), j.getJobCategory(), j.getPosition(),
                j.getSortNo(), j.getIs编制());
    }

    private RemovalVO getRemovalByEmployeeId(Long employeeId) {
        LambdaQueryWrapper<HrEmployeeRemoval> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HrEmployeeRemoval::getEmployeeId, employeeId).orderByDesc(HrEmployeeRemoval::getCreatedAt).last("LIMIT 1");
        HrEmployeeRemoval r = removalMapper.selectOne(wrapper);
        if (r == null) return null;
        return new RemovalVO(r.getId(), r.getEmployeeId(), r.getRemovalType(), r.getRemovalDate(),
                r.getExpectedRetireDate(), r.getReason());
    }

    private List<CertificateVO> getCertificatesByEmployeeId(Long employeeId) {
        LambdaQueryWrapper<HrEmployeeCertificate> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HrEmployeeCertificate::getEmployeeId, employeeId).orderByDesc(HrEmployeeCertificate::getCreatedAt);
        return certificateMapper.selectList(wrapper).stream()
                .map(c -> new CertificateVO(c.getId(), c.getEmployeeId(), c.getCertificateName(),
                        c.getCertificateNo(), c.getIssueDate(), c.getCertificateType(), c.getIssueOrg(),
                        c.getIsPermanent(), c.getExpireDate(), c.getCertificateFileUrl()))
                .collect(Collectors.toList());
    }

    private List<AssessmentVO> getAssessmentsByEmployeeId(Long employeeId) {
        LambdaQueryWrapper<HrEmployeeAssessment> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HrEmployeeAssessment::getEmployeeId, employeeId).orderByDesc(HrEmployeeAssessment::getAssessmentMonth);
        return assessmentMapper.selectList(wrapper).stream()
                .map(a -> new AssessmentVO(a.getId(), a.getEmployeeId(), a.getAssessmentMonth(),
                        a.getAssessmentScore(), a.getAssessmentGrade(), a.getRemark()))
                .collect(Collectors.toList());
    }

    private List<GrowthVO> getGrowthByEmployeeId(Long employeeId) {
        LambdaQueryWrapper<HrEmployeeGrowth> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HrEmployeeGrowth::getEmployeeId, employeeId).orderByAsc(HrEmployeeGrowth::getStartDate);
        return growthMapper.selectList(wrapper).stream()
                .map(g -> new GrowthVO(g.getId(), g.getEmployeeId(), g.getStartDate(), g.getEndDate(), g.getWorkName()))
                .collect(Collectors.toList());
    }

    private List<FamilyVO> getFamilyByEmployeeId(Long employeeId) {
        LambdaQueryWrapper<HrEmployeeFamily> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HrEmployeeFamily::getEmployeeId, employeeId).orderByAsc(HrEmployeeFamily::getCreatedAt);
        return familyMapper.selectList(wrapper).stream()
                .map(f -> new FamilyVO(f.getId(), f.getEmployeeId(), f.getRelation(), f.getName(),
                        f.getBirthday(), f.getIdCardNo(), f.getPoliticalStatus(), f.getWorkUnitPosition()))
                .collect(Collectors.toList());
    }

    private List<ChangeVO> getChangesByEmployeeId(Long employeeId) {
        LambdaQueryWrapper<HrEmployeeChange> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HrEmployeeChange::getEmployeeId, employeeId).orderByDesc(HrEmployeeChange::getCreatedAt);
        return changeMapper.selectList(wrapper).stream()
                .map(c -> new ChangeVO(c.getId(), c.getEmployeeId(), c.getCurrentDepartment(),
                        c.getCurrentPosition(), c.getCurrentRankLevel(), c.getOriginalDepartment(),
                        c.getOriginalPosition(), c.getOriginalRankLevel(), c.getReportDate(),
                        c.getChangeType(), c.getChangeReason()))
                .collect(Collectors.toList());
    }

    private List<ContractVO> getContractsByEmployeeId(Long employeeId) {
        LambdaQueryWrapper<HrEmployeeContract> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HrEmployeeContract::getEmployeeId, employeeId).orderByDesc(HrEmployeeContract::getCreatedAt);
        return contractMapper.selectList(wrapper).stream()
                .map(c -> new ContractVO(c.getId(), c.getEmployeeId(), c.getContractName(),
                        c.getContractNo(), c.getContractStartDate(), c.getContractEndDate(),
                        c.getContractFileUrl(), c.getRemark()))
                .collect(Collectors.toList());
    }

    private List<EmployeeSalaryVO> getSalaryInfoByEmployeeId(Long employeeId) {
        LambdaQueryWrapper<HrEmployeeSalary> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HrEmployeeSalary::getEmployeeId, employeeId).orderByDesc(HrEmployeeSalary::getEffectiveDate);
        return employeeSalaryMapper.selectList(wrapper).stream()
                .map(s -> new EmployeeSalaryVO(s.getId(), s.getEmployeeId(), s.getSalaryStandardId(),
                        s.getSalaryName(), s.getAmount(), s.getEffectiveDate(), s.getExpireDate(), s.getChangeReason()))
                .collect(Collectors.toList());
    }

    private List<LeaveRecordVO> getLeaveRecordsByEmployeeId(Long employeeId) {
        LambdaQueryWrapper<HrLeaveRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HrLeaveRecord::getEmployeeId, employeeId).orderByDesc(HrLeaveRecord::getCreatedAt);
        return leaveRecordMapper.selectList(wrapper).stream()
                .map(l -> new LeaveRecordVO(l.getId(), l.getEmployeeId(), l.getLeaveType(),
                        l.getStartDate(), l.getEndDate(), l.getDays(), l.getReason(),
                        l.getStatus(), l.getApplicantId(), l.getApplicantName(), l.getApplyTime()))
                .collect(Collectors.toList());
    }

    private String getOrgName(Long orgId) {
        if (orgId == null) return null;
        SysOrg org = orgMapper.selectById(orgId);
        return org != null ? org.getOrgName() : null;
    }

    private String getApplicantName(Long applicantId) {
        if (applicantId == null) return null;
        SysUser user = userMapper.selectById(applicantId);
        return user != null ? user.getRealName() : null;
    }

    private HrEmployee findEmployeeByUserId(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null || !StringUtils.hasText(user.getEmployeeCode())) {
            return null;
        }
        LambdaQueryWrapper<HrEmployee> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HrEmployee::getEmployeeCode, user.getEmployeeCode()).last("LIMIT 1");
        return employeeMapper.selectOne(wrapper);
    }

    private void createSystemAccount(HrEmployee employee, String plainPassword) {
        LambdaQueryWrapper<SysUser> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysUser::getUsername, employee.getSystemUsername()).last("LIMIT 1");
        SysUser existingUser = userMapper.selectOne(wrapper);

        AuthUser currentUser = getCurrentAuthUser();

        if (existingUser != null) {
            existingUser.setRealName(employee.getRealName());
            existingUser.setPhone(employee.getPhone());
            existingUser.setOrgId(employee.getOrgId());
            existingUser.setEmploymentStatus(employee.getEmploymentStatus());
            existingUser.setUpdatedBy(currentUser.getUserId());
            if (StringUtils.hasText(plainPassword)) {
                existingUser.setPasswordHash(hashPassword(plainPassword));
            }
            userMapper.updateById(existingUser);
        } else {
            if (!StringUtils.hasText(plainPassword)) {
                throw new BusinessException("创建系统账号必须设置密码");
            }
            SysUser newUser = new SysUser();
            newUser.setUsername(employee.getSystemUsername());
            newUser.setRealName(employee.getRealName());
            newUser.setPhone(employee.getPhone());
            newUser.setEmployeeCode(employee.getEmployeeCode());
            newUser.setOrgId(employee.getOrgId());
            newUser.setJobTitle(employee.getJobTitle());
            newUser.setEmploymentStatus(employee.getEmploymentStatus());
            newUser.setAccountStatus("ENABLE");
            newUser.setIdentityType("NORMAL_USER");
            newUser.setCreatedBy(currentUser.getUserId());
            newUser.setPasswordHash(hashPassword(plainPassword));
            userMapper.insert(newUser);
        }
    }

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void writeManagementRecord(Long employeeId, String employeeName, String recordType, String content, AuthUser operator) {
        HrManagementRecord record = new HrManagementRecord();
        record.setEmployeeId(employeeId);
        record.setEmployeeName(employeeName);
        record.setRecordType(recordType);
        record.setContent(content);
        record.setOperatorId(operator.getUserId());
        record.setOperatorName(operator.getRealName());
        record.setOperatedAt(LocalDateTime.now());
        record.setCreatedBy(operator.getUserId());
        managementRecordMapper.insert(record);
    }

    private void writeApprovalRecord(String bizType, Long bizId, String nodeName, String nodeKey,
                                     AuthUser operator, String opinion, String result) {
        HrApprovalRecord record = new HrApprovalRecord();
        record.setBizType(bizType);
        record.setBizId(bizId);
        record.setNodeName(nodeName);
        record.setNodeKey(nodeKey);
        record.setOperatorId(operator.getUserId());
        record.setOperatorName(operator.getRealName());
        record.setOperateTime(LocalDateTime.now());
        record.setOpinion(opinion);
        record.setResult(result);
        record.setCreatedBy(operator.getUserId());
        approvalRecordMapper.insert(record);
    }

    private void completeTransitionApply(HrTransitionApply apply, AuthUser operator) {
        HrEmployee employee = employeeMapper.selectById(apply.getEmployeeId());
        if (employee != null) {
            if (apply.getJoinDate() != null) {
                employee.setWorkStartDate(apply.getJoinDate());
            }
            employee.setUpdatedBy(operator.getUserId());
            employeeMapper.updateById(employee);
        }

        writeManagementRecord(apply.getEmployeeId(), apply.getEmployeeName(), "TRANSITION",
                "转正申请通过，预计转正日期：" + apply.getExpectedFormalDate(), operator);
    }

    private void completeSalaryAdjustApply(HrSalaryAdjustApply apply, AuthUser operator) {
        HrEmployeeSalary salaryRecord = new HrEmployeeSalary();
        salaryRecord.setEmployeeId(apply.getEmployeeId());
        salaryRecord.setSalaryName("调薪-" + apply.getApplyNo());
        salaryRecord.setAmount(apply.getNewSalary());
        salaryRecord.setEffectiveDate(apply.getEffectiveDate());
        salaryRecord.setChangeReason("调薪申请通过：" + apply.getApplyNo());
        salaryRecord.setCreatedBy(operator.getUserId());
        employeeSalaryMapper.insert(salaryRecord);

        HrEmployee employee = employeeMapper.selectById(apply.getEmployeeId());
        // 调薪通过后更新员工薪资记录，已在 HrEmployeeSalary 表写入，此处不重复写管理记录
    }

    private String generateApplyNo(String prefix) {
        return prefix + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
