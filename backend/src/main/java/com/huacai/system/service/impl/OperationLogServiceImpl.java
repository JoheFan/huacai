package com.huacai.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huacai.common.model.PageResponse;
import com.huacai.security.CurrentUserProvider;
import com.huacai.system.entity.SysOperationLog;
import com.huacai.system.entity.SysUser;
import com.huacai.system.mapper.OperationLogMapper;
import com.huacai.system.mapper.UserMapper;
import com.huacai.system.query.OperationLogPageQuery;
import com.huacai.system.service.OperationLogService;
import com.huacai.system.vo.OperationLogVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;
    private final UserMapper userMapper;

    public OperationLogServiceImpl(OperationLogMapper operationLogMapper, UserMapper userMapper) {
        this.operationLogMapper = operationLogMapper;
        this.userMapper = userMapper;
    }

    @Override
    public PageResponse<OperationLogVO> page(OperationLogPageQuery query) {
        Page<SysOperationLog> page = new Page<>(normalizePageNum(query.getPageNum()), normalizePageSize(query.getPageSize()));
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<SysOperationLog>()
                .orderByDesc(SysOperationLog::getCreatedAt);

        if (StringUtils.hasText(query.getModuleCode())) {
            wrapper.eq(SysOperationLog::getModuleCode, query.getModuleCode());
        }
        if (StringUtils.hasText(query.getBizType())) {
            wrapper.eq(SysOperationLog::getBizType, query.getBizType());
        }
        if (query.getUserId() != null) {
            wrapper.eq(SysOperationLog::getUserId, query.getUserId());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = query.getKeyword().trim();
            wrapper.and(w -> w
                    .like(SysOperationLog::getActionDesc, keyword)
                    .or()
                    .like(SysOperationLog::getTargetType, keyword)
                    .or()
                    .like(SysOperationLog::getResultMsg, keyword));
        }
        if (StringUtils.hasText(query.getStartDate())) {
            wrapper.ge(SysOperationLog::getCreatedAt, query.getStartDate() + " 00:00:00");
        }
        if (StringUtils.hasText(query.getEndDate())) {
            wrapper.le(SysOperationLog::getCreatedAt, query.getEndDate() + " 23:59:59");
        }

        Page<SysOperationLog> result = operationLogMapper.selectPage(page, wrapper);
        List<Long> userIds = result.getRecords().stream()
                .map(SysOperationLog::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, String> userNameMap = loadUserNameMap(userIds);

        List<OperationLogVO> records = result.getRecords().stream()
                .map(log -> toVO(log, userNameMap.get(log.getUserId())))
                .toList();
        return new PageResponse<>(records, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }

    @Override
    public OperationLogVO detail(Long id) {
        SysOperationLog log = operationLogMapper.selectById(id);
        if (log == null) {
            return null;
        }
        String userName = log.getUserId() != null ? userMapper.selectById(log.getUserId()) != null
                ? userMapper.selectById(log.getUserId()).getRealName() : null : null;
        return toVO(log, userName);
    }

    @Override
    @Transactional
    public void writeLog(OperationLogContent content) {
        SysOperationLog log = new SysOperationLog();
        log.setUserId(content.userId());
        log.setModuleCode(content.moduleCode());
        log.setBizType(content.bizType());
        log.setActionCode(content.actionCode());
        log.setActionDesc(content.actionDesc());
        log.setTargetType(content.targetType());
        log.setTargetId(content.targetId());
        log.setRequestUri(content.requestUri());
        log.setRequestMethod(content.requestMethod());
        log.setResultCode(content.resultCode());
        log.setResultMsg(content.resultMsg());
        log.setIp(content.ip());
        log.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(log);
    }

    private Map<Long, String> loadUserNameMap(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName));
    }

    private OperationLogVO toVO(SysOperationLog log, String userName) {
        return new OperationLogVO(
                log.getId(),
                log.getUserId(),
                userName,
                log.getModuleCode(),
                log.getBizType(),
                log.getActionCode(),
                log.getActionDesc(),
                log.getTargetType(),
                log.getTargetId(),
                log.getRequestUri(),
                log.getRequestMethod(),
                log.getResultCode(),
                log.getResultMsg(),
                log.getIp(),
                log.getCreatedAt()
        );
    }

    private int normalizePageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private int normalizePageSize(Integer pageSize) {
        return pageSize == null || pageSize < 1 ? 20 : Math.min(pageSize, 200);
    }
}