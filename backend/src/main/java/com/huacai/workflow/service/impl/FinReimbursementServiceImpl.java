package com.huacai.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huacai.common.exception.BusinessException;
import com.huacai.common.model.PageResponse;
import com.huacai.hr.entity.HrEmployee;
import com.huacai.hr.mapper.HrEmployeeMapper;
import com.huacai.security.AuthUser;
import com.huacai.security.CurrentUserProvider;
import com.huacai.system.entity.SysOrg;
import com.huacai.system.entity.SysUser;
import com.huacai.system.mapper.SysOrgMapper;
import com.huacai.system.mapper.SysUserMapper;
import com.huacai.workflow.dto.ReimbursementCreateRequest;
import com.huacai.workflow.dto.ReimbursementSubmitRequest;
import com.huacai.workflow.dto.ReimbursementUpdateRequest;
import com.huacai.workflow.entity.FinReimbursementApply;
import com.huacai.workflow.entity.WfActionLog;
import com.huacai.workflow.entity.WfProcessInstance;
import com.huacai.workflow.entity.WfTask;
import com.huacai.workflow.mapper.FinReimbursementApplyMapper;
import com.huacai.workflow.mapper.WfActionLogMapper;
import com.huacai.workflow.mapper.WfProcessInstanceMapper;
import com.huacai.workflow.mapper.WfTaskMapper;
import com.huacai.workflow.query.ReimbursementPageQuery;
import com.huacai.workflow.service.FinReimbursementService;
import com.huacai.workflow.vo.ReimbursementVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FinReimbursementServiceImpl implements FinReimbursementService {

    private final FinReimbursementApplyMapper reimbursementMapper;
    private final WfProcessInstanceMapper processInstanceMapper;
    private final WfTaskMapper taskMapper;
    private final WfActionLogMapper actionLogMapper;
    private final HrEmployeeMapper employeeMapper;
    private final SysOrgMapper orgMapper;
    private final SysUserMapper userMapper;
    private final CurrentUserProvider currentUserProvider;

    public FinReimbursementServiceImpl(
            FinReimbursementApplyMapper reimbursementMapper,
            WfProcessInstanceMapper processInstanceMapper,
            WfTaskMapper taskMapper,
            WfActionLogMapper actionLogMapper,
            HrEmployeeMapper employeeMapper,
            SysOrgMapper orgMapper,
            SysUserMapper userMapper,
            CurrentUserProvider currentUserProvider
    ) {
        this.reimbursementMapper = reimbursementMapper;
        this.processInstanceMapper = processInstanceMapper;
        this.taskMapper = taskMapper;
        this.actionLogMapper = actionLogMapper;
        this.employeeMapper = employeeMapper;
        this.orgMapper = orgMapper;
        this.userMapper = userMapper;
        this.currentUserProvider = currentUserProvider;
    }

    private AuthUser getCurrentAuthUser() {
        return currentUserProvider.getCurrentUser().orElseThrow(() -> new BusinessException("未登录"));
    }

    @Override
    public PageResponse<ReimbursementVO> pageReimbursements(ReimbursementPageQuery query) {
        AuthUser currentUser = getCurrentAuthUser();
        LambdaQueryWrapper<FinReimbursementApply> wrapper = Wrappers.lambdaQuery();

        // 列表可见范围与单条 canViewReimbursement 对齐：
        // FINANCE/ADMIN/超管 可看全部；其余用户即便选"全部"也只能看本人提交或本部门的，避免越权列出全公司报销
        boolean canViewAll = currentUser.isSuperAdmin()
                || (currentUser.getRoles() != null
                        && (currentUser.getRoles().contains("FINANCE") || currentUser.getRoles().contains("ADMIN")));
        if ("my".equals(query.getScope())) {
            wrapper.eq(FinReimbursementApply::getApplicantId, currentUser.getUserId());
        } else if (!canViewAll) {
            Long myOrgId = currentUser.getOrgId();
            wrapper.and(w -> {
                w.eq(FinReimbursementApply::getApplicantId, currentUser.getUserId());
                if (myOrgId != null) {
                    w.or().eq(FinReimbursementApply::getApplicantOrgId, myOrgId);
                }
            });
        }

        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(FinReimbursementApply::getApplicantName, query.getKeyword())
                    .or().like(FinReimbursementApply::getApplyNo, query.getKeyword()));
        }
        if (StringUtils.hasText(query.getProcessStatus())) {
            wrapper.eq(FinReimbursementApply::getProcessStatus, query.getProcessStatus());
        }
        wrapper.orderByDesc(FinReimbursementApply::getCreatedAt);

        Page<FinReimbursementApply> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<FinReimbursementApply> result = reimbursementMapper.selectPage(page, wrapper);

        List<ReimbursementVO> records = result.getRecords().stream()
                .map(a -> toVO(a, currentUser))
                .toList();
        return new PageResponse<>(records, result.getTotal(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public ReimbursementVO getReimbursement(Long id) {
        FinReimbursementApply apply = reimbursementMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException("报销申请不存在");
        }
        AuthUser currentUser = getCurrentAuthUser();
        if (!canViewReimbursement(apply, currentUser)) {
            throw new BusinessException("无权查看此报销申请");
        }
        return toVO(apply, currentUser);
    }

    @Override
    @Transactional
    public void createReimbursement(ReimbursementCreateRequest request) {
        AuthUser currentUser = getCurrentAuthUser();

        HrEmployee employee = findEmployeeByUserId(currentUser.getUserId());
        if (employee == null) {
            throw new BusinessException("当前用户未绑定员工档案");
        }

        String applyNo = generateApplyNo("BX");

        FinReimbursementApply apply = new FinReimbursementApply();
        apply.setApplyNo(applyNo);
        apply.setApplicantId(currentUser.getUserId());
        apply.setApplicantName(currentUser.getRealName());
        apply.setApplicantOrgId(currentUser.getOrgId());
        apply.setApplicantOrgName(getOrgName(currentUser.getOrgId()));
        apply.setReimbursementUserId(employee.getId());
        apply.setReimbursementUserName(employee.getRealName());
        apply.setAmount(request.amount());
        apply.setReason(request.reason());
        apply.setProcessStatus("DRAFT");
        apply.setCurrentRound(1);
        apply.setResubmitCount(0);
        apply.setCreatedBy(currentUser.getUserId());
        reimbursementMapper.insert(apply);
    }

    @Override
    @Transactional
    public void updateReimbursement(Long id, ReimbursementUpdateRequest request) {
        FinReimbursementApply apply = reimbursementMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException("报销申请不存在");
        }
        AuthUser currentUser = getCurrentAuthUser();
        if (!apply.getApplicantId().equals(currentUser.getUserId())) {
            throw new BusinessException("只有申请人可以编辑");
        }
        if (!"DRAFT".equals(apply.getProcessStatus()) && !"REJECTED".equals(apply.getProcessStatus())) {
            throw new BusinessException("只有草稿或驳回状态可以编辑");
        }

        if (request.amount() != null) {
            validateAmount(request.amount());
            apply.setAmount(request.amount());
        }
        if (request.reason() != null) {
            apply.setReason(request.reason());
        }
        apply.setUpdatedBy(currentUser.getUserId());
        reimbursementMapper.updateById(apply);
    }

    @Override
    @Transactional
    public void submitReimbursement(Long id, ReimbursementSubmitRequest request) {
        FinReimbursementApply apply = reimbursementMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException("报销申请不存在");
        }
        AuthUser currentUser = getCurrentAuthUser();
        if (!apply.getApplicantId().equals(currentUser.getUserId())) {
            throw new BusinessException("只有申请人可以提交");
        }
        if (!"DRAFT".equals(apply.getProcessStatus())) {
            throw new BusinessException("只有草稿状态可以提交");
        }

        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("报销金额必须大于0");
        }
        if (!StringUtils.hasText(request.reason())) {
            throw new BusinessException("申请事由不能为空");
        }

        apply.setAmount(request.amount());
        apply.setReason(request.reason());
        apply.setProcessStatus("PENDING_DEPT");
        apply.setCurrentNodeKey("PENDING_DEPT");
        apply.setCurrentNodeName("部门审核");
        apply.setSubmitTime(LocalDateTime.now());
        apply.setUpdatedBy(currentUser.getUserId());
        reimbursementMapper.updateById(apply);

        createProcessInstance(apply, currentUser);
        writeActionLog(apply.getId(), "REIMBURSEMENT", apply.getId(), apply.getCurrentRound(),
                "SUBMIT", "PENDING_DEPT", "部门审核", currentUser, null, null);
    }

    @Override
    @Transactional
    public void withdrawReimbursement(Long id) {
        FinReimbursementApply apply = reimbursementMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException("报销申请不存在");
        }

        AuthUser currentUser = getCurrentAuthUser();
        if (!apply.getApplicantId().equals(currentUser.getUserId())) {
            throw new BusinessException("只有申请人可以撤回");
        }

        String status = apply.getProcessStatus();
        if (!"PENDING_DEPT".equals(status) && !"PENDING_HR".equals(status) && !"PENDING_LEADER".equals(status)) {
            throw new BusinessException("当前状态不允许撤回");
        }

        apply.setProcessStatus("WITHDRAWN");
        apply.setCurrentNodeKey("WITHDRAWN");
        apply.setCurrentNodeName("已撤回");
        apply.setWithdrawTime(LocalDateTime.now());
        apply.setUpdatedBy(currentUser.getUserId());
        reimbursementMapper.updateById(apply);

        LambdaQueryWrapper<WfTask> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(WfTask::getBizType, "REIMBURSEMENT")
                .eq(WfTask::getBizId, id)
                .eq(WfTask::getTaskStatus, "PENDING");
        List<WfTask> pendingTasks = taskMapper.selectList(wrapper);
        for (WfTask task : pendingTasks) {
            task.setTaskStatus("WITHDRAWN");
            task.setActionResult("WITHDRAW");
            task.setActionTime(LocalDateTime.now());
            taskMapper.updateById(task);
        }

        LambdaQueryWrapper<WfProcessInstance> piWrapper = Wrappers.lambdaQuery();
        piWrapper.eq(WfProcessInstance::getBizType, "REIMBURSEMENT")
                .eq(WfProcessInstance::getBizId, id);
        WfProcessInstance process = processInstanceMapper.selectOne(piWrapper);
        if (process != null) {
            process.setOverallStatus("WITHDRAWN");
            process.setCompletedAt(LocalDateTime.now());
            process.setUpdatedBy(currentUser.getUserId());
            processInstanceMapper.updateById(process);
        }

        writeActionLog(apply.getId(), "REIMBURSEMENT", apply.getId(), apply.getCurrentRound(),
                "WITHDRAW", apply.getCurrentNodeKey(), apply.getCurrentNodeName(), currentUser, null, "撤回申请");
    }

    @Override
    @Transactional
    public void resubmitReimbursement(Long id, ReimbursementSubmitRequest request) {
        FinReimbursementApply apply = reimbursementMapper.selectById(id);
        if (apply == null) {
            throw new BusinessException("报销申请不存在");
        }
        AuthUser currentUser = getCurrentAuthUser();
        if (!apply.getApplicantId().equals(currentUser.getUserId())) {
            throw new BusinessException("只有申请人可以重提");
        }
        if (!"REJECTED".equals(apply.getProcessStatus())) {
            throw new BusinessException("只有驳回状态可以重提");
        }

        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("报销金额必须大于0");
        }
        if (!StringUtils.hasText(request.reason())) {
            throw new BusinessException("申请事由不能为空");
        }

        int newRound = apply.getCurrentRound() + 1;
        apply.setAmount(request.amount());
        apply.setReason(request.reason());
        apply.setProcessStatus("PENDING_DEPT");
        apply.setCurrentNodeKey("PENDING_DEPT");
        apply.setCurrentNodeName("部门审核");
        apply.setCurrentRound(newRound);
        apply.setResubmitCount(apply.getResubmitCount() + 1);
        apply.setSubmitTime(LocalDateTime.now());
        apply.setUpdatedBy(currentUser.getUserId());
        reimbursementMapper.updateById(apply);

        // 复用原 process instance，更新状态和当前节点
        LambdaQueryWrapper<WfProcessInstance> piWrapper = Wrappers.lambdaQuery();
        piWrapper.eq(WfProcessInstance::getBizType, "REIMBURSEMENT")
                .eq(WfProcessInstance::getBizId, id);
        WfProcessInstance process = processInstanceMapper.selectOne(piWrapper);
        if (process != null) {
            process.setOverallStatus("PENDING");
            process.setCurrentNodeKey("PENDING_DEPT");
            process.setCurrentNodeName("部门审核");
            process.setCurrentRound(newRound);
            process.setLastActionAt(LocalDateTime.now());
            process.setCompletedAt(null);
            process.setUpdatedBy(currentUser.getUserId());
            processInstanceMapper.updateById(process);
        }

        // 为新轮次创建待审批任务
        if (process != null) {
            WfTask newTask = new WfTask();
            newTask.setProcessId(process.getId());
            newTask.setBizType("REIMBURSEMENT");
            newTask.setBizId(id);
            newTask.setRoundNo(newRound);
            newTask.setNodeKey("PENDING_DEPT");
            newTask.setNodeName("部门审核");
            newTask.setTaskStatus("PENDING");
            newTask.setCandidateRoleCode("DEPT_LEADER");
            newTask.setCandidateOrgId(apply.getApplicantOrgId());
            newTask.setSortNo(1);
            newTask.setCreatedBy(currentUser.getUserId());
            taskMapper.insert(newTask);
        }

        writeActionLog(id, "REIMBURSEMENT", id, newRound,
                "RESUBMIT", "PENDING_DEPT", "部门审核", currentUser, null, null);
    }

    private void createProcessInstance(FinReimbursementApply apply, AuthUser currentUser) {
        WfProcessInstance process = new WfProcessInstance();
        process.setBizType("REIMBURSEMENT");
        process.setBizId(apply.getId());
        process.setBizNo(apply.getApplyNo());
        process.setTitle("报销申请：" + apply.getApplicantName() + " - " + apply.getAmount() + "元");
        process.setApplicantId(apply.getApplicantId());
        process.setApplicantName(apply.getApplicantName());
        process.setApplicantOrgId(apply.getApplicantOrgId());
        process.setApplicantOrgName(apply.getApplicantOrgName());
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
        task.setBizType("REIMBURSEMENT");
        task.setBizId(apply.getId());
        task.setRoundNo(1);
        task.setNodeKey("PENDING_DEPT");
        task.setNodeName("部门审核");
        task.setTaskStatus("PENDING");
        task.setCandidateRoleCode("DEPT_LEADER");
        task.setCandidateOrgId(apply.getApplicantOrgId());
        task.setSortNo(1);
        task.setCreatedBy(currentUser.getUserId());
        taskMapper.insert(task);
    }

    private void writeActionLog(Long processId, String bizType, Long bizId, Integer roundNo,
                                String actionCode, String nodeKey, String nodeName,
                                AuthUser operator, String opinion, String result) {
        WfActionLog log = new WfActionLog();
        log.setProcessId(processId);
        log.setBizType(bizType);
        log.setBizId(bizId);
        log.setRoundNo(roundNo);
        log.setActionCode(actionCode);
        log.setNodeKey(nodeKey);
        log.setNodeName(nodeName);
        log.setOperatorId(operator.getUserId());
        log.setOperatorName(operator.getRealName());
        log.setActionOpinion(opinion);
        log.setActionResult(result);
        log.setActionTime(LocalDateTime.now());
        log.setCreatedBy(operator.getUserId());
        actionLogMapper.insert(log);
    }

    private ReimbursementVO toVO(FinReimbursementApply a, AuthUser currentUser) {
        String status = a.getProcessStatus();
        boolean canApprove = false;
        boolean canWithdraw = false;
        boolean canEdit = false;
        boolean canResubmit = false;

        if ("PENDING_DEPT".equals(status) || "PENDING_HR".equals(status) || "PENDING_LEADER".equals(status)) {
            canApprove = hasApproverRole(currentUser, status);
        }
        if ("PENDING_DEPT".equals(status) || "PENDING_HR".equals(status) || "PENDING_LEADER".equals(status)) {
            if (a.getApplicantId().equals(currentUser.getUserId())) {
                canWithdraw = true;
            }
        }
        if ("DRAFT".equals(status) || "REJECTED".equals(status)) {
            if (a.getApplicantId().equals(currentUser.getUserId())) {
                canEdit = true;
            }
        }
        if ("REJECTED".equals(status)) {
            if (a.getApplicantId().equals(currentUser.getUserId())) {
                canResubmit = true;
            }
        }

        return new ReimbursementVO(
                a.getId(), a.getApplyNo(), a.getApplicantId(), a.getApplicantName(),
                a.getApplicantOrgId(), a.getApplicantOrgName(), a.getReimbursementUserId(),
                a.getReimbursementUserName(), a.getAmount(), a.getReason(), a.getProcessStatus(),
                a.getCurrentNodeKey(), a.getCurrentNodeName(), a.getCurrentRound(), a.getResubmitCount(),
                a.getSubmitTime(), a.getCompleteTime(), a.getWithdrawTime(), a.getCreatedAt(),
                canApprove, canWithdraw, canEdit, canResubmit
        );
    }

    private boolean hasApproverRole(AuthUser user, String status) {
        if (user == null || user.getRoles() == null) {
            return false;
        }
        if (user.isSuperAdmin()) {
            return true;
        }
        switch (status) {
            case "PENDING_DEPT" -> {
                return user.getRoles().contains("HR") || user.getRoles().contains("ADMIN")
                        || user.getRoles().contains("DEPT_LEADER");
            }
            case "PENDING_HR" -> {
                return user.getRoles().contains("HR") || user.getRoles().contains("ADMIN");
            }
            case "PENDING_LEADER" -> {
                return user.getRoles().contains("LEADER") || user.getRoles().contains("ADMIN");
            }
            case "PENDING_FINANCE" -> {
                return user.getRoles().contains("FINANCE") || user.getRoles().contains("ADMIN");
            }
            default -> {
                return false;
            }
        }
    }

    private boolean canViewReimbursement(FinReimbursementApply apply, AuthUser user) {
        if (user.isSuperAdmin()) return true;
        if (apply.getApplicantId().equals(user.getUserId())) return true;
        if (apply.getApplicantOrgId() != null && apply.getApplicantOrgId().equals(user.getOrgId())) return true;
        return hasApproverRole(user, apply.getProcessStatus());
    }

    private HrEmployee findEmployeeByUserId(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null || !StringUtils.hasText(user.getEmployeeCode())) {
            return null;
        }
        LambdaQueryWrapper<HrEmployee> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HrEmployee::getEmployeeCode, user.getEmployeeCode());
        return employeeMapper.selectOne(wrapper);
    }

    private String getOrgName(Long orgId) {
        if (orgId == null) return null;
        SysOrg org = orgMapper.selectById(orgId);
        return org != null ? org.getOrgName() : null;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("金额必须大于0");
        }
        if (amount.scale() > 2) {
            throw new BusinessException("金额最多保留两位小数");
        }
    }

    private String generateApplyNo(String prefix) {
        return prefix + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
