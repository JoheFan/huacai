package com.huacai.opportunity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huacai.common.exception.BusinessException;
import com.huacai.common.model.PageResponse;
import com.huacai.customer.entity.CustCustomer;
import com.huacai.customer.mapper.CustomerMapper;
import com.huacai.opportunity.dto.FollowRecordSaveRequest;
import com.huacai.opportunity.dto.OpportunitySaveRequest;
import com.huacai.opportunity.entity.OppFollowRecord;
import com.huacai.opportunity.entity.OppOpportunity;
import com.huacai.opportunity.mapper.FollowRecordMapper;
import com.huacai.opportunity.mapper.OpportunityMapper;
import com.huacai.opportunity.query.FollowRecordPageQuery;
import com.huacai.opportunity.query.OpportunityPageQuery;
import com.huacai.opportunity.service.OpportunityService;
import com.huacai.opportunity.vo.FollowRecordVO;
import com.huacai.opportunity.vo.OpportunityVO;
import com.huacai.security.CurrentUserProvider;
import com.huacai.system.entity.SysUser;
import com.huacai.system.mapper.UserMapper;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class OpportunityServiceImpl implements OpportunityService {

    private final OpportunityMapper opportunityMapper;
    private final CustomerMapper customerMapper;
    private final FollowRecordMapper followRecordMapper;
    private final UserMapper userMapper;
    private final CurrentUserProvider currentUserProvider;

    public OpportunityServiceImpl(
            OpportunityMapper opportunityMapper,
            CustomerMapper customerMapper,
            FollowRecordMapper followRecordMapper,
            UserMapper userMapper,
            CurrentUserProvider currentUserProvider
    ) {
        this.opportunityMapper = opportunityMapper;
        this.customerMapper = customerMapper;
        this.followRecordMapper = followRecordMapper;
        this.userMapper = userMapper;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public PageResponse<OpportunityVO> pageOpportunities(OpportunityPageQuery query) {
        Page<OppOpportunity> page = new Page<>(
                normalizePageNum(query.getPageNum()),
                normalizePageSize(query.getPageSize())
        );

        LambdaQueryWrapper<OppOpportunity> wrapper = new LambdaQueryWrapper<OppOpportunity>()
                .orderByDesc(OppOpportunity::getCreatedAt);

        if (query.getCustomerId() != null) {
            wrapper.eq(OppOpportunity::getCustomerId, query.getCustomerId());
        }
        if (StringUtils.hasText(query.getStageCode())) {
            wrapper.eq(OppOpportunity::getStageCode, query.getStageCode());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(OppOpportunity::getStatus, query.getStatus());
        }
        if (query.getOwnerUserId() != null) {
            wrapper.eq(OppOpportunity::getOwnerUserId, query.getOwnerUserId());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            List<Long> customerIds = findCustomerIdsByKeyword(query.getKeyword());
            if (!customerIds.isEmpty()) {
                wrapper.in(OppOpportunity::getCustomerId, customerIds);
            } else {
                return new PageResponse<>(Collections.emptyList(), 0L, query.getPageNum(), query.getPageSize());
            }
        }

        Page<OppOpportunity> result = opportunityMapper.selectPage(page, wrapper);

        Map<Long, CustCustomer> customerMap = loadCustomerMap(result.getRecords().stream()
                .map(OppOpportunity::getCustomerId)
                .filter(Objects::nonNull)
                .toList());
        Map<Long, OppFollowRecord> latestFollowMap = loadLatestFollowRecordMap(result.getRecords().stream()
                .map(OppOpportunity::getId)
                .filter(Objects::nonNull)
                .toList());

        List<OpportunityVO> records = result.getRecords().stream()
                .map(opp -> toOpportunityVO(
                        opp,
                        customerMap.get(opp.getCustomerId()),
                        latestFollowMap.get(opp.getId())
                ))
                .toList();

        return new PageResponse<>(records, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }

    @Override
    public OpportunityVO getOpportunityDetail(Long id) {
        OppOpportunity opportunity = getOpportunityOrThrow(id);
        CustCustomer customer = opportunity.getCustomerId() != null ?
                customerMapper.selectById(opportunity.getCustomerId()) : null;
        OppFollowRecord latestFollowRecord = loadLatestFollowRecordMap(List.of(opportunity.getId())).get(opportunity.getId());
        return toOpportunityVO(opportunity, customer, latestFollowRecord);
    }

    @Override
    @Transactional
    public void createOpportunity(OpportunitySaveRequest request) {
        if (request.customerId() != null) {
            getCustomerOrThrow(request.customerId());
        }

        OppOpportunity opportunity = new OppOpportunity();
        opportunity.setCustomerId(request.customerId());
        opportunity.setPriorityLevel(request.priorityLevel());
        opportunity.setStageCode(request.stageCode() != null ? request.stageCode() : "NEW");
        opportunity.setOwnerUserId(request.ownerUserId());
        opportunity.setEstimatedAmount(request.estimatedAmount());
        opportunity.setIntentLevel(request.intentLevel());
        opportunity.setStatus(request.status() != null ? request.status() : "OPEN");
        opportunity.setNextFollowTime(request.nextFollowTime());
        opportunity.setRemark(request.remark());
        opportunity.setCreatedBy(currentUserProvider.getCurrentUserId());
        opportunity.setUpdatedBy(currentUserProvider.getCurrentUserId());
        opportunityMapper.insert(opportunity);
    }

    @Override
    @Transactional
    public void updateOpportunity(Long id, OpportunitySaveRequest request) {
        OppOpportunity opportunity = getOpportunityOrThrow(id);

        if (request.customerId() != null && !request.customerId().equals(opportunity.getCustomerId())) {
            getCustomerOrThrow(request.customerId());
            opportunity.setCustomerId(request.customerId());
        }
        if (request.priorityLevel() != null) {
            opportunity.setPriorityLevel(request.priorityLevel());
        }
        if (request.stageCode() != null) {
            opportunity.setStageCode(request.stageCode());
        }
        if (request.ownerUserId() != null) {
            opportunity.setOwnerUserId(request.ownerUserId());
        }
        if (request.estimatedAmount() != null) {
            opportunity.setEstimatedAmount(request.estimatedAmount());
        }
        if (request.intentLevel() != null) {
            opportunity.setIntentLevel(request.intentLevel());
        }
        if (request.nextFollowTime() != null) {
            opportunity.setNextFollowTime(request.nextFollowTime());
        }
        if (request.remark() != null) {
            opportunity.setRemark(request.remark());
        }
        opportunity.setUpdatedBy(currentUserProvider.getCurrentUserId());
        opportunityMapper.updateById(opportunity);
    }

    @Override
    @Transactional
    public void deleteOpportunity(Long id) {
        getOpportunityOrThrow(id);
        // Delete associated follow records first
        followRecordMapper.delete(new LambdaQueryWrapper<OppFollowRecord>()
                .eq(OppFollowRecord::getOpportunityId, id));
        opportunityMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateOpportunityStatus(Long id, String status) {
        OppOpportunity opportunity = getOpportunityOrThrow(id);
        opportunity.setStatus(status);
        opportunity.setUpdatedBy(currentUserProvider.getCurrentUserId());
        opportunityMapper.updateById(opportunity);
    }

    // ===================== 跟进记录 =====================

    @Override
    public PageResponse<FollowRecordVO> pageFollowRecords(FollowRecordPageQuery query) {
        Page<OppFollowRecord> page = new Page<>(
                normalizePageNum(query.getPageNum()),
                normalizePageSize(query.getPageSize())
        );

        LambdaQueryWrapper<OppFollowRecord> wrapper = new LambdaQueryWrapper<OppFollowRecord>()
                .orderByDesc(OppFollowRecord::getFollowTime)
                .orderByDesc(OppFollowRecord::getId);

        if (query.getOpportunityId() != null) {
            wrapper.eq(OppFollowRecord::getOpportunityId, query.getOpportunityId());
        }

        Page<OppFollowRecord> result = followRecordMapper.selectPage(page, wrapper);

        List<FollowRecordVO> records = result.getRecords().stream()
                .map(this::toFollowRecordVO)
                .toList();

        return new PageResponse<>(records, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }

    @Override
    public FollowRecordVO getFollowRecordDetail(Long id) {
        OppFollowRecord record = getFollowRecordOrThrow(id);
        return toFollowRecordVO(record);
    }

    @Override
    @Transactional
    public void createFollowRecord(FollowRecordSaveRequest request) {
        OppOpportunity opportunity = getOpportunityOrThrow(request.opportunityId());

        OppFollowRecord record = new OppFollowRecord();
        record.setOpportunityId(request.opportunityId());
        record.setCustomerId(opportunity.getCustomerId());
        record.setFollowTime(request.followTime());
        record.setFollowerName(request.followerName());
        record.setFollowContent(request.followContent());
        record.setNextAction(request.nextAction());
        record.setRemark(request.remark());
        record.setCreatedBy(currentUserProvider.getCurrentUserId());
        record.setUpdatedBy(currentUserProvider.getCurrentUserId());
        followRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void updateFollowRecord(Long id, FollowRecordSaveRequest request) {
        OppFollowRecord record = getFollowRecordOrThrow(id);

        if (request.followTime() != null) {
            record.setFollowTime(request.followTime());
        }
        if (request.followerName() != null) {
            record.setFollowerName(request.followerName());
        }
        if (request.followContent() != null) {
            record.setFollowContent(request.followContent());
        }
        if (request.nextAction() != null) {
            record.setNextAction(request.nextAction());
        }
        if (request.remark() != null) {
            record.setRemark(request.remark());
        }
        record.setUpdatedBy(currentUserProvider.getCurrentUserId());
        followRecordMapper.updateById(record);
    }

    @Override
    @Transactional
    public void deleteFollowRecord(Long id) {
        getFollowRecordOrThrow(id);
        followRecordMapper.deleteById(id);
    }

    // ===================== Helper Methods =====================

    private OppOpportunity getOpportunityOrThrow(Long id) {
        OppOpportunity opportunity = opportunityMapper.selectById(id);
        if (opportunity == null) {
            throw new BusinessException("商机不存在");
        }
        return opportunity;
    }

    private OppFollowRecord getFollowRecordOrThrow(Long id) {
        OppFollowRecord record = followRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("跟进记录不存在");
        }
        return record;
    }

    private CustCustomer getCustomerOrThrow(Long customerId) {
        CustCustomer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        return customer;
    }

    private List<Long> findCustomerIdsByKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return Collections.emptyList();
        }
        String trimmed = keyword.trim();
        return customerMapper.selectList(new LambdaQueryWrapper<CustCustomer>()
                        .select(CustCustomer::getId)
                        .and(w -> w
                                .like(CustCustomer::getCustomerName, trimmed)
                                .or().like(CustCustomer::getCustomerNo, trimmed)
                                .or().like(CustCustomer::getCompanyName, trimmed)
                                .or().like(CustCustomer::getMobile, trimmed)))
                .stream()
                .map(CustCustomer::getId)
                .toList();
    }

    private Map<Long, CustCustomer> loadCustomerMap(List<Long> customerIds) {
        if (customerIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return customerMapper.selectBatchIds(customerIds).stream()
                .collect(Collectors.toMap(CustCustomer::getId, c -> c));
    }

    private Map<Long, OppFollowRecord> loadLatestFollowRecordMap(List<Long> opportunityIds) {
        if (opportunityIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return followRecordMapper.selectList(new LambdaQueryWrapper<OppFollowRecord>()
                        .in(OppFollowRecord::getOpportunityId, opportunityIds)
                        .orderByDesc(OppFollowRecord::getFollowTime)
                        .orderByDesc(OppFollowRecord::getId))
                .stream()
                .filter(record -> record.getOpportunityId() != null)
                .collect(Collectors.toMap(
                        OppFollowRecord::getOpportunityId,
                        record -> record,
                        (first, ignored) -> first
                ));
    }

    private OpportunityVO toOpportunityVO(
            OppOpportunity opp,
            CustCustomer customer,
            OppFollowRecord latestFollowRecord
    ) {
        return new OpportunityVO(
                opp.getId(),
                opp.getCustomerId(),
                customer == null ? "" : customer.getCustomerName(),
                customer == null ? "" : customer.getCustomerNo(),
                customer == null ? "" : customer.getMobile(),
                customer == null ? "" : customer.getCompanyName(),
                customer == null ? "" : customer.getCreditCode(),
                opp.getPriorityLevel(),
                opp.getStageCode(),
                opp.getOwnerUserId(),
                "", // ownerUserName - not used in this view
                opp.getEstimatedAmount(),
                opp.getIntentLevel(),
                opp.getStatus(),
                opp.getNextFollowTime(),
                latestFollowRecord == null ? null : latestFollowRecord.getFollowTime(),
                latestFollowRecord == null ? "" : latestFollowRecord.getFollowerName(),
                latestFollowRecord == null ? "" : latestFollowRecord.getFollowContent(),
                opp.getRemark(),
                opp.getCreatedAt(),
                opp.getUpdatedAt()
        );
    }

    private FollowRecordVO toFollowRecordVO(OppFollowRecord record) {
        return new FollowRecordVO(
                record.getId(),
                record.getOpportunityId(),
                record.getCustomerId(),
                "", // customerName not needed for follow record
                record.getFollowTime(),
                record.getFollowerName(),
                record.getFollowContent(),
                record.getNextAction(),
                record.getRemark(),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }

    private int normalizePageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private int normalizePageSize(Integer pageSize) {
        return pageSize == null || pageSize < 1 ? 20 : Math.min(pageSize, 200);
    }
}
