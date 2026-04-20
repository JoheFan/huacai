package com.huacai.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huacai.common.exception.BusinessException;
import com.huacai.common.model.PageResponse;
import com.huacai.finance.dto.PayeeSaveRequest;
import com.huacai.finance.entity.FinPayee;
import com.huacai.finance.mapper.FinPayeeMapper;
import com.huacai.finance.service.FinPayeeService;
import com.huacai.finance.vo.PayeeVO;
import com.huacai.security.AuthUser;
import com.huacai.security.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class FinPayeeServiceImpl implements FinPayeeService {

    private final FinPayeeMapper payeeMapper;
    private final CurrentUserProvider currentUserProvider;

    public FinPayeeServiceImpl(FinPayeeMapper payeeMapper, CurrentUserProvider currentUserProvider) {
        this.payeeMapper = payeeMapper;
        this.currentUserProvider = currentUserProvider;
    }

    private AuthUser getCurrentAuthUser() {
        return currentUserProvider.getCurrentUser().orElseThrow(() -> new BusinessException("未登录"));
    }

    @Override
    public PageResponse<PayeeVO> pagePayees(String keyword, String status, int pageNum, int pageSize) {
        LambdaQueryWrapper<FinPayee> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StringUtils.hasText(keyword), FinPayee::getPayeeName, keyword)
                .eq(StringUtils.hasText(status), FinPayee::getStatus, status)
                .orderByDesc(FinPayee::getId);
        Page<FinPayee> page = new Page<>(pageNum, pageSize);
        Page<FinPayee> result = payeeMapper.selectPage(page, wrapper);
        return new PageResponse<>(
                result.getRecords().stream().map(this::toVO).toList(),
                result.getTotal(),
                pageNum,
                pageSize
        );
    }

    @Override
    public PayeeVO getPayee(Long id) {
        FinPayee payee = payeeMapper.selectById(id);
        if (payee == null) throw new BusinessException("收款方不存在");
        return toVO(payee);
    }

    @Override
    public void createPayee(PayeeSaveRequest request) {
        FinPayee payee = new FinPayee();
        payee.setPayeeName(request.payeeName());
        payee.setPayeeType(request.payeeType());
        payee.setBankName(request.bankName());
        payee.setBankAccount(request.bankAccount());
        payee.setContactName(request.contactName());
        payee.setContactPhone(request.contactPhone());
        payee.setStatus(request.status() != null ? request.status() : "ENABLE");
        payee.setRemark(request.remark());
        AuthUser user = getCurrentAuthUser();
        payee.setCreatedBy(user.getUserId());
        payeeMapper.insert(payee);
    }

    @Override
    public void updatePayee(Long id, PayeeSaveRequest request) {
        FinPayee payee = payeeMapper.selectById(id);
        if (payee == null) throw new BusinessException("收款方不存在");
        if (StringUtils.hasText(request.payeeName())) payee.setPayeeName(request.payeeName());
        if (request.payeeType() != null) payee.setPayeeType(request.payeeType());
        if (request.bankName() != null) payee.setBankName(request.bankName());
        if (request.bankAccount() != null) payee.setBankAccount(request.bankAccount());
        if (request.contactName() != null) payee.setContactName(request.contactName());
        if (request.contactPhone() != null) payee.setContactPhone(request.contactPhone());
        if (StringUtils.hasText(request.status())) payee.setStatus(request.status());
        if (request.remark() != null) payee.setRemark(request.remark());
        AuthUser user = getCurrentAuthUser();
        payee.setUpdatedBy(user.getUserId());
        payeeMapper.updateById(payee);
    }

    @Override
    public void deletePayee(Long id) {
        FinPayee payee = payeeMapper.selectById(id);
        if (payee == null) throw new BusinessException("收款方不存在");
        payeeMapper.deleteById(id);
    }

    @Override
    public void updatePayeeStatus(Long id, String status) {
        FinPayee payee = payeeMapper.selectById(id);
        if (payee == null) throw new BusinessException("收款方不存在");
        AuthUser user = getCurrentAuthUser();
        payee.setStatus(status);
        payee.setUpdatedBy(user.getUserId());
        payeeMapper.updateById(payee);
    }

    private PayeeVO toVO(FinPayee p) {
        return new PayeeVO(
                p.getId(), p.getPayeeName(), p.getPayeeType(),
                p.getBankName(), p.getBankAccount(), p.getContactName(),
                p.getContactPhone(), p.getStatus(), p.getRemark(), p.getCreatedAt()
        );
    }
}
