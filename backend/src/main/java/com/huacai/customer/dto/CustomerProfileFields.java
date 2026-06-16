package com.huacai.customer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 客户基础档案字段。{@link CustomerSaveRequest} 与 {@link CustomerArchiveSaveRequest} 共同实现，
 * 让服务层用同一个 fillCustomer 回填实体，避免两套重复的逐字段赋值随业务字段增减而漂移。
 */
public interface CustomerProfileFields {

    String customerName();

    String gender();

    String idCard();

    LocalDate birthday();

    String mobile();

    String companyName();

    String creditCode();

    LocalDate establishedDate();

    String industry();

    String businessAddress();

    String bankName();

    String bankAccount();

    String recommenderName();

    BigDecimal recommenderRate();

    BigDecimal serviceFee();

    String bizStatus();

    Boolean taxRegistrationNormal();
}
