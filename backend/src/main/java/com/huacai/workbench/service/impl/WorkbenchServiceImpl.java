package com.huacai.workbench.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huacai.customer.entity.CustCustomer;
import com.huacai.customer.mapper.CustomerMapper;
import com.huacai.loan.entity.LoanOrder;
import com.huacai.loan.entity.LoanRepayment;
import com.huacai.loan.mapper.LoanOrderMapper;
import com.huacai.loan.mapper.LoanRepaymentMapper;
import com.huacai.workbench.query.WorkbenchQuery;
import com.huacai.workbench.service.WorkbenchService;
import com.huacai.workbench.vo.WorkbenchMetricVO;
import com.huacai.workbench.vo.WorkbenchOverviewVO;
import com.huacai.workbench.vo.WorkbenchRecordVO;
import com.huacai.workbench.vo.WorkbenchReminderVO;
import com.huacai.workbench.vo.WorkbenchTodoVO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class WorkbenchServiceImpl implements WorkbenchService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");

    private final CustomerMapper customerMapper;
    private final LoanOrderMapper loanOrderMapper;
    private final LoanRepaymentMapper loanRepaymentMapper;

    public WorkbenchServiceImpl(
            CustomerMapper customerMapper,
            LoanOrderMapper loanOrderMapper,
            LoanRepaymentMapper loanRepaymentMapper
    ) {
        this.customerMapper = customerMapper;
        this.loanOrderMapper = loanOrderMapper;
        this.loanRepaymentMapper = loanRepaymentMapper;
    }

    @Override
    public WorkbenchOverviewVO overview(WorkbenchQuery query) {
        Set<Long> customerIds = findCustomerIdsByKeyword(query.getKeyword());
        long customerTotal = customerMapper.selectCount(new LambdaQueryWrapper<CustCustomer>());
        long activeLoanCount = loanOrderMapper.selectCount(new LambdaQueryWrapper<LoanOrder>().ne(LoanOrder::getStatus, "SETTLED"));
        long todayRepaymentCount = loanRepaymentMapper.selectCount(new LambdaQueryWrapper<LoanRepayment>()
                .eq(LoanRepayment::getRepaymentDate, LocalDate.now()));
        long missingInfoCount = customerMapper.selectCount(new LambdaQueryWrapper<CustCustomer>()
                .and(wrapper -> wrapper.isNull(CustCustomer::getCreditCode)
                        .or()
                        .eq(CustCustomer::getCreditCode, "")
                        .or()
                        .isNull(CustCustomer::getMobile)
                        .or()
                        .eq(CustCustomer::getMobile, "")));

        List<WorkbenchMetricVO> metrics = List.of(
                new WorkbenchMetricVO("客户总数", String.valueOf(customerTotal), "客户档案", "", true),
                new WorkbenchMetricVO("运行中借贷单", String.valueOf(activeLoanCount), "未结清", "", false),
                new WorkbenchMetricVO("今日还款登记", String.valueOf(todayRepaymentCount), "今日回款", "", false),
                new WorkbenchMetricVO("资料待补客户", String.valueOf(missingInfoCount), "资料缺口", "", false)
        );

        List<WorkbenchRecordVO> focusRows = buildRecentRecords(customerIds);
        List<WorkbenchTodoVO> todoItems = buildTodoItems(activeLoanCount, todayRepaymentCount, missingInfoCount);
        List<WorkbenchReminderVO> reminderItems = buildReminderItems(activeLoanCount, todayRepaymentCount, missingInfoCount);
        return new WorkbenchOverviewVO(metrics, focusRows, todoItems, reminderItems);
    }

    private List<WorkbenchRecordVO> buildRecentRecords(Set<Long> customerIds) {
        List<RecentWorkbenchRecord> rows = new ArrayList<>();

        LambdaQueryWrapper<CustCustomer> customerWrapper = new LambdaQueryWrapper<CustCustomer>()
                .orderByDesc(CustCustomer::getUpdatedAt)
                .last("LIMIT 4");
        if (!customerIds.isEmpty()) {
            customerWrapper.in(CustCustomer::getId, customerIds);
        }
        for (CustCustomer customer : customerMapper.selectList(customerWrapper)) {
            LocalDateTime recordTime = resolveRecordTime(customer.getUpdatedAt(), customer.getCreatedAt());
            rows.add(new RecentWorkbenchRecord(
                    new WorkbenchRecordVO(
                            customer.getId(),
                            customer.getCustomerName(),
                            "客户档案",
                            blankToDash(customer.getCompanyName()),
                            formatDate(recordTime),
                            blankToDash(customer.getLoanStatus()),
                            priorityByMissingInfo(customer),
                            "/customers"
                    ),
                    recordTime
            ));
        }

        LambdaQueryWrapper<LoanOrder> loanWrapper = new LambdaQueryWrapper<LoanOrder>()
                .orderByDesc(LoanOrder::getUpdatedAt)
                .last("LIMIT 4");
        if (!customerIds.isEmpty()) {
            loanWrapper.in(LoanOrder::getCustomerId, customerIds);
        }
        for (LoanOrder order : loanOrderMapper.selectList(loanWrapper)) {
            CustCustomer customer = customerMapper.selectById(order.getCustomerId());
            LocalDateTime recordTime = resolveRecordTime(order.getUpdatedAt(), order.getCreatedAt());
            rows.add(new RecentWorkbenchRecord(
                    new WorkbenchRecordVO(
                            order.getId(),
                            customer == null ? "未知客户" : customer.getCustomerName(),
                            "借贷主单",
                            blankToDash(order.getCapitalSourceType()),
                            formatDate(recordTime),
                            blankToDash(order.getStatus()),
                            order.getBalanceAmount() != null && order.getBalanceAmount().signum() > 0 ? "高" : "中",
                            "/loan-orders"
                    ),
                    recordTime
            ));
        }

        LambdaQueryWrapper<LoanRepayment> repaymentWrapper = new LambdaQueryWrapper<LoanRepayment>()
                .orderByDesc(LoanRepayment::getUpdatedAt)
                .last("LIMIT 4");
        if (!customerIds.isEmpty()) {
            repaymentWrapper.in(LoanRepayment::getCustomerId, customerIds);
        }
        for (LoanRepayment repayment : loanRepaymentMapper.selectList(repaymentWrapper)) {
            CustCustomer customer = customerMapper.selectById(repayment.getCustomerId());
            LocalDateTime recordTime = resolveRecordTime(repayment.getUpdatedAt(), repayment.getCreatedAt());
            rows.add(new RecentWorkbenchRecord(
                    new WorkbenchRecordVO(
                            repayment.getId(),
                            customer == null ? "未知客户" : customer.getCustomerName(),
                            "还款记录",
                            "借贷单 #" + repayment.getLoanOrderId(),
                            formatDate(recordTime),
                            blankToDash(repayment.getCapitalSourceType()),
                            "中",
                            "/repayments"
                    ),
                    recordTime
            ));
        }

        return rows.stream()
                .sorted(Comparator.comparing(RecentWorkbenchRecord::sortAt, Comparator.nullsLast(LocalDateTime::compareTo))
                        .reversed())
                .map(RecentWorkbenchRecord::view)
                .limit(8)
                .toList();
    }

    private List<WorkbenchTodoVO> buildTodoItems(long activeLoanCount, long todayRepaymentCount, long missingInfoCount) {
        return List.of(
                new WorkbenchTodoVO("检查运行中借贷单余额", "高", activeLoanCount + " 单"),
                new WorkbenchTodoVO("核对今日还款登记", todayRepaymentCount > 0 ? "中" : "高", todayRepaymentCount + " 条"),
                new WorkbenchTodoVO("补齐客户关键资料", missingInfoCount > 0 ? "中" : "低", missingInfoCount + " 家")
        );
    }

    private List<WorkbenchReminderVO> buildReminderItems(long activeLoanCount, long todayRepaymentCount, long missingInfoCount) {
        List<WorkbenchReminderVO> reminders = new ArrayList<>();
        reminders.add(new WorkbenchReminderVO("运行中借贷单 " + activeLoanCount + " 笔", "持续跟进", activeLoanCount > 0 ? "warning" : "success"));
        reminders.add(new WorkbenchReminderVO("今日还款记录 " + todayRepaymentCount + " 条", "当日登记", todayRepaymentCount > 0 ? "success" : "warning"));
        reminders.add(new WorkbenchReminderVO("资料缺口客户 " + missingInfoCount + " 家", "资料补齐", missingInfoCount > 0 ? "danger" : "success"));
        return reminders;
    }

    private Set<Long> findCustomerIdsByKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return java.util.Collections.emptySet();
        }
        String trimmed = keyword.trim();
        return customerMapper.selectList(new LambdaQueryWrapper<CustCustomer>()
                        .select(CustCustomer::getId)
                        .and(w -> w.like(CustCustomer::getCustomerName, trimmed)
                                .or()
                                .like(CustCustomer::getCustomerNo, trimmed)
                                .or()
                                .like(CustCustomer::getCompanyName, trimmed)
                                .or()
                                .like(CustCustomer::getMobile, trimmed)))
                .stream()
                .map(CustCustomer::getId)
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());
    }

    private String priorityByMissingInfo(CustCustomer customer) {
        if (!StringUtils.hasText(customer.getCreditCode()) || !StringUtils.hasText(customer.getMobile())) {
            return "高";
        }
        return "中";
    }

    private String formatDate(java.time.LocalDateTime dateTime) {
        return dateTime == null ? "-" : dateTime.toLocalDate().format(DATE_FORMATTER);
    }

    private String formatDate(LocalDate date) {
        return date == null ? "-" : date.format(DATE_FORMATTER);
    }

    private String blankToDash(String value) {
        return StringUtils.hasText(value) ? value : "-";
    }

    private LocalDateTime resolveRecordTime(LocalDateTime updatedAt, LocalDateTime createdAt) {
        return updatedAt != null ? updatedAt : createdAt;
    }

    private record RecentWorkbenchRecord(WorkbenchRecordVO view, LocalDateTime sortAt) {
    }
}
