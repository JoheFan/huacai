package com.huacai.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huacai.common.exception.BusinessException;
import com.huacai.common.model.PageResponse;
import com.huacai.hr.entity.HrEmployee;
import com.huacai.hr.entity.HrSalaryAdjustApply;
import com.huacai.hr.entity.HrTransitionApply;
import com.huacai.hr.mapper.HrEmployeeMapper;
import com.huacai.hr.mapper.HrSalaryAdjustApplyMapper;
import com.huacai.hr.mapper.HrTransitionApplyMapper;
import com.huacai.security.AuthUser;
import com.huacai.security.CurrentUserProvider;
import com.huacai.workflow.entity.FinReimbursementApply;
import com.huacai.workflow.entity.WfActionLog;
import com.huacai.workflow.entity.WfProcessInstance;
import com.huacai.workflow.entity.WfTask;
import com.huacai.workflow.mapper.FinReimbursementApplyMapper;
import com.huacai.workflow.mapper.WfActionLogMapper;
import com.huacai.workflow.mapper.WfProcessInstanceMapper;
import com.huacai.workflow.mapper.WfTaskMapper;
import com.huacai.workflow.query.MyApprovalPageQuery;
import com.huacai.workflow.service.MyApprovalService;
import com.huacai.workflow.vo.ActionLogVO;
import com.huacai.workflow.vo.MyApprovalTaskVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MyApprovalServiceImpl implements MyApprovalService {

    private final WfTaskMapper taskMapper;
    private final WfProcessInstanceMapper processInstanceMapper;
    private final WfActionLogMapper actionLogMapper;
    private final HrTransitionApplyMapper transitionApplyMapper;
    private final HrSalaryAdjustApplyMapper salaryAdjustApplyMapper;
    private final FinReimbursementApplyMapper reimbursementApplyMapper;
    private final HrEmployeeMapper employeeMapper;
    private final CurrentUserProvider currentUserProvider;

    public MyApprovalServiceImpl(
            WfTaskMapper taskMapper,
            WfProcessInstanceMapper processInstanceMapper,
            WfActionLogMapper actionLogMapper,
            HrTransitionApplyMapper transitionApplyMapper,
            HrSalaryAdjustApplyMapper salaryAdjustApplyMapper,
            FinReimbursementApplyMapper reimbursementApplyMapper,
            HrEmployeeMapper employeeMapper,
            CurrentUserProvider currentUserProvider
    ) {
        this.taskMapper = taskMapper;
        this.processInstanceMapper = processInstanceMapper;
        this.actionLogMapper = actionLogMapper;
        this.transitionApplyMapper = transitionApplyMapper;
        this.salaryAdjustApplyMapper = salaryAdjustApplyMapper;
        this.reimbursementApplyMapper = reimbursementApplyMapper;
        this.employeeMapper = employeeMapper;
        this.currentUserProvider = currentUserProvider;
    }

    private AuthUser getCurrentAuthUser() {
        return currentUserProvider.getCurrentUser().orElseThrow(() -> new BusinessException("未登录"));
    }

    @Override
    public PageResponse<MyApprovalTaskVO> pageTodos(MyApprovalPageQuery query) {
        AuthUser currentUser = getCurrentAuthUser();
        Long userId = currentUser.getUserId();
        Long orgId = currentUser.getOrgId();
        List<String> roleCodes = getUserRoleCodes(currentUser);

        LambdaQueryWrapper<WfTask> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(WfTask::getTaskStatus, "PENDING")
                .eq(WfTask::getDeletedFlag, 0);

        if (StringUtils.hasText(query.getBizType())) {
            wrapper.eq(WfTask::getBizType, query.getBizType());
        }

        // OR: 非DEPT_LEADER角色直接匹配角色码 OR DEPT_LEADER必须同部门
        boolean hasDeptLeader = roleCodes.contains("DEPT_LEADER");
        List<String> otherRoles = roleCodes.stream().filter(r -> !"DEPT_LEADER".equals(r)).toList();

        wrapper.and(w -> {
            if (!otherRoles.isEmpty()) {
                w.in(WfTask::getCandidateRoleCode, otherRoles);
            }
            if (hasDeptLeader) {
                w.or(o -> o.eq(WfTask::getCandidateRoleCode, "DEPT_LEADER")
                        .eq(WfTask::getCandidateOrgId, orgId));
            } else if (otherRoles.isEmpty()) {
                w.eq(WfTask::getId, -1L); // 无有效角色则无结果
            }
        });

        wrapper.orderByDesc(WfTask::getCreatedAt);

        Page<WfTask> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<WfTask> result = taskMapper.selectPage(page, wrapper);

        List<MyApprovalTaskVO> records = result.getRecords().stream()
                .map(this::toTaskVO)
                .toList();
        return new PageResponse<>(records, result.getTotal(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public PageResponse<MyApprovalTaskVO> pageInitiated(MyApprovalPageQuery query) {
        AuthUser currentUser = getCurrentAuthUser();
        Long userId = currentUser.getUserId();

        LambdaQueryWrapper<WfProcessInstance> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(WfProcessInstance::getApplicantId, userId)
                .eq(WfProcessInstance::getDeletedFlag, 0);

        if (StringUtils.hasText(query.getBizType())) {
            wrapper.eq(WfProcessInstance::getBizType, query.getBizType());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(WfProcessInstance::getBizNo, query.getKeyword());
        }
        wrapper.orderByDesc(WfProcessInstance::getCreatedAt);

        Page<WfProcessInstance> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<WfProcessInstance> result = processInstanceMapper.selectPage(page, wrapper);

        List<MyApprovalTaskVO> records = result.getRecords().stream()
                .map(this::toInitiatedVO)
                .toList();
        return new PageResponse<>(records, result.getTotal(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public PageResponse<MyApprovalTaskVO> pageProcessed(MyApprovalPageQuery query) {
        AuthUser currentUser = getCurrentAuthUser();
        Long userId = currentUser.getUserId();

        LambdaQueryWrapper<WfTask> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(WfTask::getAssigneeId, userId)
                .in(WfTask::getTaskStatus, List.of("DONE", "REJECTED"))
                .eq(WfTask::getDeletedFlag, 0);

        if (StringUtils.hasText(query.getBizType())) {
            wrapper.eq(WfTask::getBizType, query.getBizType());
        }
        wrapper.orderByDesc(WfTask::getActionTime);

        Page<WfTask> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<WfTask> result = taskMapper.selectPage(page, wrapper);

        List<MyApprovalTaskVO> records = result.getRecords().stream()
                .map(this::toProcessedVO)
                .toList();
        return new PageResponse<>(records, result.getTotal(), query.getPageNum(), query.getPageSize());
    }

    @Override
    @Transactional
    public void approveTask(Long taskId, String opinion) {
        WfTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        if (!"PENDING".equals(task.getTaskStatus())) {
            throw new BusinessException("任务已处理");
        }

        AuthUser currentUser = getCurrentAuthUser();
        validateTaskAssignee(task, currentUser);

        task.setTaskStatus("DONE");
        task.setActionResult("APPROVE");
        task.setActionOpinion(opinion);
        task.setActionTime(LocalDateTime.now());
        task.setAssigneeId(currentUser.getUserId());
        task.setAssigneeName(currentUser.getRealName());
        taskMapper.updateById(task);

        advanceProcess(task, currentUser, opinion, "APPROVE");
    }

    @Override
    @Transactional
    public void rejectTask(Long taskId, String opinion) {
        WfTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        if (!"PENDING".equals(task.getTaskStatus())) {
            throw new BusinessException("任务已处理");
        }

        AuthUser currentUser = getCurrentAuthUser();
        validateTaskAssignee(task, currentUser);

        task.setTaskStatus("DONE");
        task.setActionResult("REJECT");
        task.setActionOpinion(opinion);
        task.setActionTime(LocalDateTime.now());
        task.setAssigneeId(currentUser.getUserId());
        task.setAssigneeName(currentUser.getRealName());
        taskMapper.updateById(task);

        rejectProcess(task, currentUser, opinion);
    }

    @Override
    public List<ActionLogVO> getProcessTimeline(String bizType, Long bizId) {
        AuthUser currentUser = getCurrentAuthUser();
        if (!canViewTimeline(bizType, bizId, currentUser)) {
            throw new BusinessException("无权查看此审批历史");
        }

        LambdaQueryWrapper<WfActionLog> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(WfActionLog::getBizType, bizType)
                .eq(WfActionLog::getBizId, bizId)
                .orderByAsc(WfActionLog::getCreatedAt);

        return actionLogMapper.selectList(wrapper).stream()
                .map(log -> new ActionLogVO(
                        log.getId(), log.getProcessId(), log.getBizType(), log.getBizId(),
                        log.getRoundNo(), log.getActionCode(), log.getNodeKey(), log.getNodeName(),
                        log.getOperatorId(), log.getOperatorName(), log.getActionOpinion(),
                        log.getActionResult(), log.getActionTime()
                ))
                .toList();
    }

    private boolean canViewTimeline(String bizType, Long bizId, AuthUser user) {
        if (user.isSuperAdmin()) return true;
        switch (bizType) {
            case "REIMBURSEMENT" -> {
                FinReimbursementApply a = reimbursementApplyMapper.selectById(bizId);
                if (a == null) return false;
                if (a.getApplicantId().equals(user.getUserId())) return true;
                if (a.getApplicantOrgId() != null && a.getApplicantOrgId().equals(user.getOrgId())) return true;
                return false;
            }
            case "TRANSITION" -> {
                HrTransitionApply a = transitionApplyMapper.selectById(bizId);
                if (a == null) return false;
                if (a.getApplicantId() != null && a.getApplicantId().equals(user.getUserId())) return true;
                if (a.getEmployeeId() != null) {
                    HrEmployee emp = employeeMapper.selectById(a.getEmployeeId());
                    if (emp != null && emp.getOrgId() != null && emp.getOrgId().equals(user.getOrgId())) return true;
                }
                return false;
            }
            case "SALARY_ADJUST" -> {
                HrSalaryAdjustApply a = salaryAdjustApplyMapper.selectById(bizId);
                if (a == null) return false;
                if (a.getApplicantId() != null && a.getApplicantId().equals(user.getUserId())) return true;
                if (a.getEmployeeId() != null) {
                    HrEmployee emp = employeeMapper.selectById(a.getEmployeeId());
                    if (emp != null && emp.getOrgId() != null && emp.getOrgId().equals(user.getOrgId())) return true;
                }
                return false;
            }
            default -> {
                return false;
            }
        }
    }

    private void advanceProcess(WfTask task, AuthUser currentUser, String opinion, String result) {
        String bizType = task.getBizType();
        Long bizId = task.getBizId();
        String nextNodeKey;
        String nextNodeName;

        switch (bizType) {
            case "REIMBURSEMENT" -> {
                FinReimbursementApply apply = reimbursementApplyMapper.selectById(bizId);
                if (apply == null) return;

                String currentStatus = apply.getProcessStatus();
                switch (currentStatus) {
                    case "PENDING_DEPT" -> {
                        nextNodeKey = "PENDING_HR";
                        nextNodeName = "人事审核";
                        apply.setProcessStatus("PENDING_HR");
                    }
                    case "PENDING_HR" -> {
                        nextNodeKey = "PENDING_LEADER";
                        nextNodeName = "分管领导";
                        apply.setProcessStatus("PENDING_LEADER");
                    }
                    case "PENDING_LEADER" -> {
                        nextNodeKey = "PENDING_FINANCE";
                        nextNodeName = "财务审核";
                        apply.setProcessStatus("PENDING_FINANCE");
                    }
                    case "PENDING_FINANCE" -> {
                        nextNodeKey = "APPROVED";
                        nextNodeName = "已通过";
                        apply.setProcessStatus("APPROVED");
                        apply.setCompleteTime(LocalDateTime.now());
                    }
                    default -> {
                        return;
                    }
                }

                apply.setCurrentNodeKey(nextNodeKey);
                apply.setCurrentNodeName(nextNodeName);
                apply.setUpdatedBy(currentUser.getUserId());
                reimbursementApplyMapper.updateById(apply);

                if (!"APPROVED".equals(nextNodeKey)) {
                    WfTask nextTask = new WfTask();
                    nextTask.setProcessId(task.getProcessId());
                    nextTask.setBizType(bizType);
                    nextTask.setBizId(bizId);
                    nextTask.setRoundNo(task.getRoundNo());
                    nextTask.setNodeKey(nextNodeKey);
                    nextTask.setNodeName(nextNodeName);
                    nextTask.setTaskStatus("PENDING");
                    nextTask.setCandidateRoleCode(getNextRoleCode(nextNodeKey));
                    nextTask.setCandidateOrgId(task.getCandidateOrgId());
                    nextTask.setSortNo(task.getSortNo() + 1);
                    nextTask.setCreatedBy(currentUser.getUserId());
                    taskMapper.insert(nextTask);
                }

                WfActionLog log = new WfActionLog();
                log.setProcessId(task.getProcessId());
                log.setBizType(bizType);
                log.setBizId(bizId);
                log.setRoundNo(task.getRoundNo());
                log.setActionCode("APPROVE");
                log.setNodeKey(task.getNodeKey());
                log.setNodeName(task.getNodeName());
                log.setOperatorId(currentUser.getUserId());
                log.setOperatorName(currentUser.getRealName());
                log.setActionOpinion(opinion);
                log.setActionResult(result);
                log.setActionTime(LocalDateTime.now());
                log.setCreatedBy(currentUser.getUserId());
                actionLogMapper.insert(log);

                LambdaQueryWrapper<WfProcessInstance> piWrapper = Wrappers.lambdaQuery();
                piWrapper.eq(WfProcessInstance::getBizType, bizType).eq(WfProcessInstance::getBizId, bizId);
                WfProcessInstance process = processInstanceMapper.selectOne(piWrapper);
                if (process != null) {
                    process.setCurrentNodeKey(nextNodeKey);
                    process.setCurrentNodeName(nextNodeName);
                    process.setLastActionAt(LocalDateTime.now());
                    if ("APPROVED".equals(nextNodeKey)) {
                        process.setOverallStatus("APPROVED");
                        process.setCompletedAt(LocalDateTime.now());
                    }
                    process.setUpdatedBy(currentUser.getUserId());
                    processInstanceMapper.updateById(process);
                }
            }
            case "TRANSITION" -> {
                HrTransitionApply apply = transitionApplyMapper.selectById(bizId);
                if (apply == null) return;

                String currentStatus = apply.getProcessStatus();
                switch (currentStatus) {
                    case "PENDING_DEPT" -> {
                        nextNodeKey = "PENDING_HR";
                        nextNodeName = "人事审核";
                        apply.setProcessStatus("PENDING_HR");
                    }
                    case "PENDING_HR" -> {
                        nextNodeKey = "PENDING_COMPANY";
                        nextNodeName = "公司审批";
                        apply.setProcessStatus("PENDING_COMPANY");
                    }
                    case "PENDING_COMPANY" -> {
                        nextNodeKey = "PENDING_ADMIN";
                        nextNodeName = "行政审批";
                        apply.setProcessStatus("PENDING_ADMIN");
                    }
                    case "PENDING_ADMIN" -> {
                        nextNodeKey = "APPROVED";
                        nextNodeName = "已通过";
                        apply.setProcessStatus("APPROVED");
                        apply.setCompleteTime(LocalDateTime.now());
                    }
                    default -> {
                        return;
                    }
                }

                apply.setCurrentNode(nextNodeName);
                apply.setUpdatedBy(currentUser.getUserId());
                transitionApplyMapper.updateById(apply);

                if (!"APPROVED".equals(nextNodeKey)) {
                    WfTask nextTask = new WfTask();
                    nextTask.setProcessId(task.getProcessId());
                    nextTask.setBizType(bizType);
                    nextTask.setBizId(bizId);
                    nextTask.setRoundNo(task.getRoundNo());
                    nextTask.setNodeKey(nextNodeKey);
                    nextTask.setNodeName(nextNodeName);
                    nextTask.setTaskStatus("PENDING");
                    nextTask.setCandidateRoleCode(getNextRoleCode(nextNodeKey));
                    nextTask.setCandidateOrgId(task.getCandidateOrgId());
                    nextTask.setSortNo(task.getSortNo() + 1);
                    nextTask.setCreatedBy(currentUser.getUserId());
                    taskMapper.insert(nextTask);
                }

                WfActionLog log = new WfActionLog();
                log.setProcessId(task.getProcessId());
                log.setBizType(bizType);
                log.setBizId(bizId);
                log.setRoundNo(task.getRoundNo());
                log.setActionCode("APPROVE");
                log.setNodeKey(task.getNodeKey());
                log.setNodeName(task.getNodeName());
                log.setOperatorId(currentUser.getUserId());
                log.setOperatorName(currentUser.getRealName());
                log.setActionOpinion(opinion);
                log.setActionResult(result);
                log.setActionTime(LocalDateTime.now());
                log.setCreatedBy(currentUser.getUserId());
                actionLogMapper.insert(log);
            }
            case "SALARY_ADJUST" -> {
                HrSalaryAdjustApply apply = salaryAdjustApplyMapper.selectById(bizId);
                if (apply == null) return;

                String currentStatus = apply.getProcessStatus();
                switch (currentStatus) {
                    case "PENDING_DEPT" -> {
                        nextNodeKey = "PENDING_HR";
                        nextNodeName = "人事审核";
                        apply.setProcessStatus("PENDING_HR");
                    }
                    case "PENDING_HR" -> {
                        nextNodeKey = "PENDING_LEADER";
                        nextNodeName = "分管领导";
                        apply.setProcessStatus("PENDING_LEADER");
                    }
                    case "PENDING_LEADER" -> {
                        nextNodeKey = "PENDING_SCHOOL";
                        nextNodeName = "校领导";
                        apply.setProcessStatus("PENDING_SCHOOL");
                    }
                    case "PENDING_SCHOOL" -> {
                        nextNodeKey = "PENDING_FINANCE";
                        nextNodeName = "财务办理";
                        apply.setProcessStatus("PENDING_FINANCE");
                    }
                    case "PENDING_FINANCE" -> {
                        nextNodeKey = "APPROVED";
                        nextNodeName = "已通过";
                        apply.setProcessStatus("APPROVED");
                        apply.setCompleteTime(LocalDateTime.now());
                    }
                    default -> {
                        return;
                    }
                }

                apply.setCurrentNode(nextNodeName);
                apply.setUpdatedBy(currentUser.getUserId());
                salaryAdjustApplyMapper.updateById(apply);

                if (!"APPROVED".equals(nextNodeKey)) {
                    WfTask nextTask = new WfTask();
                    nextTask.setProcessId(task.getProcessId());
                    nextTask.setBizType(bizType);
                    nextTask.setBizId(bizId);
                    nextTask.setRoundNo(task.getRoundNo());
                    nextTask.setNodeKey(nextNodeKey);
                    nextTask.setNodeName(nextNodeName);
                    nextTask.setTaskStatus("PENDING");
                    nextTask.setCandidateRoleCode(getNextRoleCode(nextNodeKey));
                    nextTask.setCandidateOrgId(task.getCandidateOrgId());
                    nextTask.setSortNo(task.getSortNo() + 1);
                    nextTask.setCreatedBy(currentUser.getUserId());
                    taskMapper.insert(nextTask);
                }

                WfActionLog log = new WfActionLog();
                log.setProcessId(task.getProcessId());
                log.setBizType(bizType);
                log.setBizId(bizId);
                log.setRoundNo(task.getRoundNo());
                log.setActionCode("APPROVE");
                log.setNodeKey(task.getNodeKey());
                log.setNodeName(task.getNodeName());
                log.setOperatorId(currentUser.getUserId());
                log.setOperatorName(currentUser.getRealName());
                log.setActionOpinion(opinion);
                log.setActionResult(result);
                log.setActionTime(LocalDateTime.now());
                log.setCreatedBy(currentUser.getUserId());
                actionLogMapper.insert(log);
            }
        }
    }

    private void rejectProcess(WfTask task, AuthUser currentUser, String opinion) {
        String bizType = task.getBizType();
        Long bizId = task.getBizId();

        switch (bizType) {
            case "REIMBURSEMENT" -> {
                FinReimbursementApply apply = reimbursementApplyMapper.selectById(bizId);
                if (apply == null) return;

                apply.setProcessStatus("REJECTED");
                apply.setCurrentNodeKey("REJECTED");
                apply.setCurrentNodeName("已驳回");
                apply.setUpdatedBy(currentUser.getUserId());
                reimbursementApplyMapper.updateById(apply);

                WfActionLog log = new WfActionLog();
                log.setProcessId(task.getProcessId());
                log.setBizType(bizType);
                log.setBizId(bizId);
                log.setRoundNo(task.getRoundNo());
                log.setActionCode("REJECT");
                log.setNodeKey(task.getNodeKey());
                log.setNodeName(task.getNodeName());
                log.setOperatorId(currentUser.getUserId());
                log.setOperatorName(currentUser.getRealName());
                log.setActionOpinion(opinion);
                log.setActionResult("REJECT");
                log.setActionTime(LocalDateTime.now());
                log.setCreatedBy(currentUser.getUserId());
                actionLogMapper.insert(log);

                LambdaQueryWrapper<WfProcessInstance> piWrapper = Wrappers.lambdaQuery();
                piWrapper.eq(WfProcessInstance::getBizType, bizType).eq(WfProcessInstance::getBizId, bizId);
                WfProcessInstance process = processInstanceMapper.selectOne(piWrapper);
                if (process != null) {
                    process.setOverallStatus("REJECTED");
                    process.setCompletedAt(LocalDateTime.now());
                    process.setUpdatedBy(currentUser.getUserId());
                    processInstanceMapper.updateById(process);
                }
            }
            case "TRANSITION" -> {
                HrTransitionApply apply = transitionApplyMapper.selectById(bizId);
                if (apply == null) return;

                apply.setProcessStatus("REJECTED");
                apply.setUpdatedBy(currentUser.getUserId());
                transitionApplyMapper.updateById(apply);

                WfActionLog log = new WfActionLog();
                log.setProcessId(task.getProcessId());
                log.setBizType(bizType);
                log.setBizId(bizId);
                log.setRoundNo(task.getRoundNo());
                log.setActionCode("REJECT");
                log.setNodeKey(task.getNodeKey());
                log.setNodeName(task.getNodeName());
                log.setOperatorId(currentUser.getUserId());
                log.setOperatorName(currentUser.getRealName());
                log.setActionOpinion(opinion);
                log.setActionResult("REJECT");
                log.setActionTime(LocalDateTime.now());
                log.setCreatedBy(currentUser.getUserId());
                actionLogMapper.insert(log);
            }
            case "SALARY_ADJUST" -> {
                HrSalaryAdjustApply apply = salaryAdjustApplyMapper.selectById(bizId);
                if (apply == null) return;

                apply.setProcessStatus("REJECTED");
                apply.setUpdatedBy(currentUser.getUserId());
                salaryAdjustApplyMapper.updateById(apply);

                WfActionLog log = new WfActionLog();
                log.setProcessId(task.getProcessId());
                log.setBizType(bizType);
                log.setBizId(bizId);
                log.setRoundNo(task.getRoundNo());
                log.setActionCode("REJECT");
                log.setNodeKey(task.getNodeKey());
                log.setNodeName(task.getNodeName());
                log.setOperatorId(currentUser.getUserId());
                log.setOperatorName(currentUser.getRealName());
                log.setActionOpinion(opinion);
                log.setActionResult("REJECT");
                log.setActionTime(LocalDateTime.now());
                log.setCreatedBy(currentUser.getUserId());
                actionLogMapper.insert(log);
            }
        }
    }

    private MyApprovalTaskVO toTaskVO(WfTask task) {
        String title = getBizTitle(task.getBizType(), task.getBizId());
        WfProcessInstance process = getProcessInstance(task.getProcessId());
        return new MyApprovalTaskVO(
                task.getId(),
                task.getProcessId(),
                task.getBizType(),
                task.getBizId(),
                process != null ? process.getBizNo() : null,
                title,
                task.getNodeKey(),
                task.getNodeName(),
                task.getTaskStatus(),
                process != null ? process.getApplicantName() : null,
                process != null ? process.getApplicantOrgName() : null,
                process != null ? process.getStartedAt() : null,
                task.getCreatedAt()
        );
    }

    private MyApprovalTaskVO toInitiatedVO(WfProcessInstance process) {
        String title = getBizTitle(process.getBizType(), process.getBizId());
        return new MyApprovalTaskVO(
                null,
                process.getId(),
                process.getBizType(),
                process.getBizId(),
                process.getBizNo(),
                title,
                process.getCurrentNodeKey(),
                process.getCurrentNodeName(),
                process.getOverallStatus(),
                process.getApplicantName(),
                process.getApplicantOrgName(),
                process.getStartedAt(),
                null
        );
    }

    private MyApprovalTaskVO toProcessedVO(WfTask task) {
        String title = getBizTitle(task.getBizType(), task.getBizId());
        WfProcessInstance process = getProcessInstance(task.getProcessId());
        return new MyApprovalTaskVO(
                task.getId(),
                task.getProcessId(),
                task.getBizType(),
                task.getBizId(),
                process != null ? process.getBizNo() : null,
                title,
                task.getNodeKey(),
                task.getNodeName(),
                task.getActionResult(),
                process != null ? process.getApplicantName() : null,
                process != null ? process.getApplicantOrgName() : null,
                process != null ? process.getStartedAt() : null,
                task.getActionTime()
        );
    }

    private String getBizTitle(String bizType, Long bizId) {
        switch (bizType) {
            case "TRANSITION" -> {
                HrTransitionApply apply = transitionApplyMapper.selectById(bizId);
                return apply != null ? "转正申请：" + apply.getEmployeeName() : "转正申请";
            }
            case "SALARY_ADJUST" -> {
                HrSalaryAdjustApply apply = salaryAdjustApplyMapper.selectById(bizId);
                return apply != null ? "调薪申请：" + apply.getEmployeeName() : "调薪申请";
            }
            case "REIMBURSEMENT" -> {
                FinReimbursementApply apply = reimbursementApplyMapper.selectById(bizId);
                return apply != null ? "报销申请：" + apply.getApplicantName() + " - " + apply.getAmount() + "元" : "报销申请";
            }
            default -> {
                return "审批申请";
            }
        }
    }

    private WfProcessInstance getProcessInstance(Long processId) {
        if (processId == null) return null;
        return processInstanceMapper.selectById(processId);
    }

    private List<String> getUserRoleCodes(AuthUser user) {
        if (user == null || user.getRoles() == null) {
            return List.of();
        }
        List<String> codes = new ArrayList<>(user.getRoles());
        if (user.isSuperAdmin()) {
            codes.addAll(List.of("HR", "ADMIN", "DEPT_LEADER", "LEADER", "FINANCE", "COMPANY_APPROVER", "SCHOOL_LEADER"));
        }
        return codes;
    }

    private String getNextRoleCode(String nodeKey) {
        return switch (nodeKey) {
            case "PENDING_HR" -> "HR";
            case "PENDING_LEADER" -> "LEADER";
            case "PENDING_FINANCE" -> "FINANCE";
            case "PENDING_COMPANY" -> "COMPANY_APPROVER";
            case "PENDING_SCHOOL" -> "SCHOOL_LEADER";
            default -> "ADMIN";
        };
    }

    boolean validateTaskAssignee(WfTask task, AuthUser user) {
        if (user.isSuperAdmin()) {
            return true;
        }
        if (task.getCandidateRoleCode() != null) {
            if (!user.getRoles().contains(task.getCandidateRoleCode())) {
                return false;
            }
            // DEPT_LEADER 必须同部门
            if ("DEPT_LEADER".equals(task.getCandidateRoleCode())) {
                if (task.getCandidateOrgId() != null && !task.getCandidateOrgId().equals(user.getOrgId())) {
                    return false;
                }
            }
        }
        return true;
    }
}
