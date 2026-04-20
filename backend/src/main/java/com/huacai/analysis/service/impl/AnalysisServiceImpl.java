package com.huacai.analysis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huacai.analysis.dto.*;
import com.huacai.analysis.entity.*;
import com.huacai.analysis.mapper.*;
import com.huacai.analysis.query.*;
import com.huacai.analysis.service.AnalysisService;
import com.huacai.analysis.vo.*;
import com.huacai.common.exception.BusinessException;
import com.huacai.common.model.PageResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    private final IncrementSummaryMapper incrementSummaryMapper;
    private final IncrementDetailMapper incrementDetailMapper;
    private final DailyIncrementMapper dailyIncrementMapper;
    private final EmployeePerformanceMapper employeePerformanceMapper;

    public AnalysisServiceImpl(
            IncrementSummaryMapper incrementSummaryMapper,
            IncrementDetailMapper incrementDetailMapper,
            DailyIncrementMapper dailyIncrementMapper,
            EmployeePerformanceMapper employeePerformanceMapper) {
        this.incrementSummaryMapper = incrementSummaryMapper;
        this.incrementDetailMapper = incrementDetailMapper;
        this.dailyIncrementMapper = dailyIncrementMapper;
        this.employeePerformanceMapper = employeePerformanceMapper;
    }

    // ===================== Increment Summary =====================

    @Override
    public PageResponse<IncrementSummaryVO> pageIncrementSummary(IncrementSummaryQuery query) {
        LambdaQueryWrapper<IncrementSummary> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(IncrementSummary::getCompanyName, query.getKeyword());
        }
        wrapper.orderByDesc(IncrementSummary::getUpdatedAt);

        IPage<IncrementSummary> page = incrementSummaryMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);

        List<IncrementSummaryVO> voList = page.getRecords().stream()
                .map(this::toIncrementSummaryVO)
                .collect(Collectors.toList());

        return new PageResponse<>(voList, page.getTotal(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public IncrementSummaryVO getIncrementSummaryDetail(Long id) {
        IncrementSummary entity = incrementSummaryMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("增量总览记录不存在");
        }
        return toIncrementSummaryVO(entity);
    }

    @Override
    public void createIncrementSummary(IncrementSummarySaveDTO dto) {
        IncrementSummary entity = new IncrementSummary();
        copyIncrementSummaryToEntity(dto, entity);
        incrementSummaryMapper.insert(entity);
    }

    @Override
    public void updateIncrementSummary(Long id, IncrementSummarySaveDTO dto) {
        IncrementSummary existing = incrementSummaryMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("增量总览记录不存在");
        }
        copyIncrementSummaryToEntity(dto, existing);
        incrementSummaryMapper.updateById(existing);
    }

    @Override
    public void deleteIncrementSummary(Long id) {
        incrementSummaryMapper.deleteById(id);
    }

    private void copyIncrementSummaryToEntity(IncrementSummarySaveDTO dto, IncrementSummary entity) {
        entity.setCompanyName(dto.getCompanyName());
        entity.setBusinessAddress(dto.getBusinessAddress());
        entity.setIndustry(dto.getIndustry());
        entity.setStartDate(dto.getStartDate());
        entity.setJanAmount(dto.getJanAmount());
        entity.setFebAmount(dto.getFebAmount());
        entity.setMarAmount(dto.getMarAmount());
        entity.setAprAmount(dto.getAprAmount());
        entity.setMayAmount(dto.getMayAmount());
        entity.setJunAmount(dto.getJunAmount());
        entity.setJulAmount(dto.getJulAmount());
        entity.setAugAmount(dto.getAugAmount());
        entity.setSepAmount(dto.getSepAmount());
        entity.setOctAmount(dto.getOctAmount());
        entity.setNovAmount(dto.getNovAmount());
        entity.setDecAmount(dto.getDecAmount());
        entity.setTotalAmount(calculateTotal(dto));
        entity.setRemark(dto.getRemark());
    }

    private BigDecimal calculateTotal(IncrementSummarySaveDTO dto) {
        BigDecimal total = BigDecimal.ZERO;
        if (dto.getJanAmount() != null) total = total.add(dto.getJanAmount());
        if (dto.getFebAmount() != null) total = total.add(dto.getFebAmount());
        if (dto.getMarAmount() != null) total = total.add(dto.getMarAmount());
        if (dto.getAprAmount() != null) total = total.add(dto.getAprAmount());
        if (dto.getMayAmount() != null) total = total.add(dto.getMayAmount());
        if (dto.getJunAmount() != null) total = total.add(dto.getJunAmount());
        if (dto.getJulAmount() != null) total = total.add(dto.getJulAmount());
        if (dto.getAugAmount() != null) total = total.add(dto.getAugAmount());
        if (dto.getSepAmount() != null) total = total.add(dto.getSepAmount());
        if (dto.getOctAmount() != null) total = total.add(dto.getOctAmount());
        if (dto.getNovAmount() != null) total = total.add(dto.getNovAmount());
        if (dto.getDecAmount() != null) total = total.add(dto.getDecAmount());
        return total;
    }

    private IncrementSummaryVO toIncrementSummaryVO(IncrementSummary entity) {
        IncrementSummaryVO vo = new IncrementSummaryVO();
        vo.setId(entity.getId());
        vo.setCompanyName(entity.getCompanyName());
        vo.setBusinessAddress(entity.getBusinessAddress());
        vo.setIndustry(entity.getIndustry());
        vo.setStartDate(entity.getStartDate());
        vo.setJanAmount(entity.getJanAmount());
        vo.setFebAmount(entity.getFebAmount());
        vo.setMarAmount(entity.getMarAmount());
        vo.setAprAmount(entity.getAprAmount());
        vo.setMayAmount(entity.getMayAmount());
        vo.setJunAmount(entity.getJunAmount());
        vo.setJulAmount(entity.getJulAmount());
        vo.setAugAmount(entity.getAugAmount());
        vo.setSepAmount(entity.getSepAmount());
        vo.setOctAmount(entity.getOctAmount());
        vo.setNovAmount(entity.getNovAmount());
        vo.setDecAmount(entity.getDecAmount());
        vo.setTotalAmount(entity.getTotalAmount());
        vo.setRemark(entity.getRemark());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    // ===================== Increment Detail =====================

    @Override
    public PageResponse<IncrementDetailVO> pageIncrementDetail(IncrementDetailQuery query) {
        LambdaQueryWrapper<IncrementDetail> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(IncrementDetail::getCustomerName, query.getKeyword());
        }
        if (query.getCustomerId() != null) {
            wrapper.eq(IncrementDetail::getCustomerId, query.getCustomerId());
        }
        if (query.getIncrementDateFrom() != null) {
            wrapper.ge(IncrementDetail::getIncrementDate, query.getIncrementDateFrom());
        }
        if (query.getIncrementDateTo() != null) {
            wrapper.le(IncrementDetail::getIncrementDate, query.getIncrementDateTo());
        }
        wrapper.orderByDesc(IncrementDetail::getIncrementDate);

        IPage<IncrementDetail> page = incrementDetailMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);

        List<IncrementDetailVO> voList = page.getRecords().stream()
                .map(this::toIncrementDetailVO)
                .collect(Collectors.toList());

        return new PageResponse<>(voList, page.getTotal(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public IncrementDetailVO getIncrementDetailDetail(Long id) {
        IncrementDetail entity = incrementDetailMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("增量详情记录不存在");
        }
        return toIncrementDetailVO(entity);
    }

    @Override
    public void createIncrementDetail(IncrementDetailSaveDTO dto) {
        IncrementDetail entity = new IncrementDetail();
        copyIncrementDetailToEntity(dto, entity);
        incrementDetailMapper.insert(entity);
    }

    @Override
    public void updateIncrementDetail(Long id, IncrementDetailSaveDTO dto) {
        IncrementDetail existing = incrementDetailMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("增量详情记录不存在");
        }
        copyIncrementDetailToEntity(dto, existing);
        incrementDetailMapper.updateById(existing);
    }

    @Override
    public void deleteIncrementDetail(Long id) {
        // Delete associated daily increments first
        LambdaQueryWrapper<DailyIncrement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyIncrement::getDetailId, id);
        dailyIncrementMapper.delete(wrapper);
        // Delete the detail record
        incrementDetailMapper.deleteById(id);
    }

    private void copyIncrementDetailToEntity(IncrementDetailSaveDTO dto, IncrementDetail entity) {
        entity.setCustomerId(dto.getCustomerId());
        entity.setCustomerName(dto.getCustomerName());
        entity.setIncrementDate(dto.getIncrementDate());
        entity.setTotalAmount(dto.getTotalAmount());
        entity.setBusinessAddress(dto.getBusinessAddress());
        entity.setIndustry(dto.getIndustry());
        entity.setDailyCount(dto.getDailyCount());
        entity.setRemark(dto.getRemark());
    }

    private IncrementDetailVO toIncrementDetailVO(IncrementDetail entity) {
        IncrementDetailVO vo = new IncrementDetailVO();
        vo.setId(entity.getId());
        vo.setCustomerId(entity.getCustomerId());
        vo.setCustomerName(entity.getCustomerName());
        vo.setIncrementDate(entity.getIncrementDate());
        vo.setTotalAmount(entity.getTotalAmount());
        vo.setBusinessAddress(entity.getBusinessAddress());
        vo.setIndustry(entity.getIndustry());
        vo.setDailyCount(entity.getDailyCount());
        vo.setRemark(entity.getRemark());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    // ===================== Daily Increment =====================

    @Override
    public PageResponse<DailyIncrementVO> pageDailyIncrement(DailyIncrementQuery query) {
        LambdaQueryWrapper<DailyIncrement> wrapper = new LambdaQueryWrapper<>();
        if (query.getDetailId() != null) {
            wrapper.eq(DailyIncrement::getDetailId, query.getDetailId());
        }
        wrapper.orderByAsc(DailyIncrement::getSeqNo);

        IPage<DailyIncrement> page = dailyIncrementMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);

        List<DailyIncrementVO> voList = page.getRecords().stream()
                .map(this::toDailyIncrementVO)
                .collect(Collectors.toList());

        return new PageResponse<>(voList, page.getTotal(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public DailyIncrementVO getDailyIncrementDetail(Long id) {
        DailyIncrement entity = dailyIncrementMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("日增量记录不存在");
        }
        return toDailyIncrementVO(entity);
    }

    @Override
    public void createDailyIncrement(DailyIncrementSaveDTO dto) {
        IncrementDetail parent = getIncrementDetailOrThrow(dto.getDetailId());
        DailyIncrement entity = new DailyIncrement();
        copyDailyIncrementToEntity(dto, entity, parent);
        dailyIncrementMapper.insert(entity);
    }

    @Override
    public void updateDailyIncrement(Long id, DailyIncrementSaveDTO dto) {
        DailyIncrement existing = dailyIncrementMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("日增量记录不存在");
        }
        IncrementDetail parent = getIncrementDetailOrThrow(dto.getDetailId());
        copyDailyIncrementToEntity(dto, existing, parent);
        dailyIncrementMapper.updateById(existing);
    }

    @Override
    public void deleteDailyIncrement(Long id) {
        dailyIncrementMapper.deleteById(id);
    }

    private void copyDailyIncrementToEntity(DailyIncrementSaveDTO dto, DailyIncrement entity, IncrementDetail parent) {
        entity.setDetailId(dto.getDetailId());
        // 父级派生字段：信任父记录，不信任客户端传入值
        entity.setCustomerName(parent.getCustomerName());
        entity.setIncrementDate(parent.getIncrementDate());
        entity.setIncrementAmount(dto.getIncrementAmount());
        entity.setChannelRate(dto.getChannelRate());
        entity.setChannelFee(dto.getChannelFee());
        entity.setSeqNo(dto.getSeqNo());
        entity.setRemark(dto.getRemark());
    }

    private IncrementDetail getIncrementDetailOrThrow(Long detailId) {
        if (detailId == null) {
            throw new BusinessException("日增量记录缺少父级增量详情ID");
        }
        IncrementDetail parent = incrementDetailMapper.selectById(detailId);
        if (parent == null) {
            throw new BusinessException("父级增量详情记录不存在");
        }
        return parent;
    }

    private DailyIncrementVO toDailyIncrementVO(DailyIncrement entity) {
        DailyIncrementVO vo = new DailyIncrementVO();
        vo.setId(entity.getId());
        vo.setDetailId(entity.getDetailId());
        vo.setCustomerName(entity.getCustomerName());
        vo.setIncrementDate(entity.getIncrementDate());
        vo.setIncrementAmount(entity.getIncrementAmount());
        vo.setChannelRate(entity.getChannelRate());
        vo.setChannelFee(entity.getChannelFee());
        vo.setSeqNo(entity.getSeqNo());
        vo.setRemark(entity.getRemark());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    // ===================== Employee Performance =====================

    @Override
    public PageResponse<EmployeePerformanceVO> pageEmployeePerformance(EmployeePerformanceQuery query) {
        LambdaQueryWrapper<EmployeePerformance> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(EmployeePerformance::getEmployeeName, query.getKeyword());
        }
        if (query.getEmployeeId() != null) {
            wrapper.eq(EmployeePerformance::getEmployeeId, query.getEmployeeId());
        }
        if (StringUtils.hasText(query.getPeriodDate())) {
            wrapper.eq(EmployeePerformance::getPeriodDate, query.getPeriodDate());
        }
        wrapper.orderByDesc(EmployeePerformance::getUpdatedAt);

        IPage<EmployeePerformance> page = employeePerformanceMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);

        List<EmployeePerformanceVO> voList = page.getRecords().stream()
                .map(this::toEmployeePerformanceVO)
                .collect(Collectors.toList());

        return new PageResponse<>(voList, page.getTotal(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public EmployeePerformanceVO getEmployeePerformanceDetail(Long id) {
        EmployeePerformance entity = employeePerformanceMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("员工绩效记录不存在");
        }
        return toEmployeePerformanceVO(entity);
    }

    @Override
    public void createEmployeePerformance(EmployeePerformanceSaveDTO dto) {
        EmployeePerformance entity = new EmployeePerformance();
        copyEmployeePerformanceToEntity(dto, entity);
        employeePerformanceMapper.insert(entity);
    }

    @Override
    public void updateEmployeePerformance(Long id, EmployeePerformanceSaveDTO dto) {
        EmployeePerformance existing = employeePerformanceMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("员工绩效记录不存在");
        }
        copyEmployeePerformanceToEntity(dto, existing);
        employeePerformanceMapper.updateById(existing);
    }

    @Override
    public void deleteEmployeePerformance(Long id) {
        employeePerformanceMapper.deleteById(id);
    }

    private void copyEmployeePerformanceToEntity(EmployeePerformanceSaveDTO dto, EmployeePerformance entity) {
        entity.setEmployeeId(dto.getEmployeeId());
        entity.setEmployeeName(dto.getEmployeeName());
        entity.setOrgId(dto.getOrgId());
        entity.setOrgName(dto.getOrgName());
        entity.setPeriodDate(dto.getPeriodDate());
        entity.setTargetAmount(dto.getTargetAmount());
        entity.setActualAmount(dto.getActualAmount());
        entity.setDealAmount(dto.getDealAmount());
        entity.setDealCount(dto.getDealCount());
        entity.setRemark(dto.getRemark());
    }

    private EmployeePerformanceVO toEmployeePerformanceVO(EmployeePerformance entity) {
        EmployeePerformanceVO vo = new EmployeePerformanceVO();
        vo.setId(entity.getId());
        vo.setEmployeeId(entity.getEmployeeId());
        vo.setEmployeeName(entity.getEmployeeName());
        vo.setOrgId(entity.getOrgId());
        vo.setOrgName(entity.getOrgName());
        vo.setPeriodDate(entity.getPeriodDate());
        vo.setTargetAmount(entity.getTargetAmount());
        vo.setActualAmount(entity.getActualAmount());
        vo.setDealAmount(entity.getDealAmount());
        vo.setDealCount(entity.getDealCount());
        vo.setRemark(entity.getRemark());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}
