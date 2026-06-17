package com.huacai.common.util;

import org.springframework.util.StringUtils;

/**
 * PII 脱敏工具：仅用于"广暴露的列表/显示面"，不要用在详情/编辑回填的 VO 上
 * （否则脱敏值会被表单原样保存回库，污染真实数据）。
 */
public final class PiiMaskUtil {

    private PiiMaskUtil() {
    }

    /** 身份证：保留前 6 后 4，中间打码。例 4401**********1234 */
    public static String maskIdCard(String idCard) {
        if (!StringUtils.hasText(idCard)) {
            return idCard;
        }
        String v = idCard.trim();
        if (v.length() <= 10) {
            return maskMiddle(v, 1, 1);
        }
        return v.substring(0, 6) + "*".repeat(v.length() - 10) + v.substring(v.length() - 4);
    }

    /** 银行账号/银行卡：仅保留后 4 位。例 **** **** 1234 → ************1234 */
    public static String maskBankAccount(String account) {
        if (!StringUtils.hasText(account)) {
            return account;
        }
        String v = account.trim().replace(" ", "");
        if (v.length() <= 4) {
            return v;
        }
        return "*".repeat(v.length() - 4) + v.substring(v.length() - 4);
    }

    /** 手机号：保留前 3 后 4。例 138****1234 */
    public static String maskPhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return phone;
        }
        String v = phone.trim();
        if (v.length() < 7) {
            return maskMiddle(v, 1, 1);
        }
        return v.substring(0, 3) + "*".repeat(v.length() - 7) + v.substring(v.length() - 4);
    }

    private static String maskMiddle(String v, int head, int tail) {
        if (v.length() <= head + tail) {
            return v.isEmpty() ? v : "*".repeat(v.length());
        }
        return v.substring(0, head) + "*".repeat(v.length() - head - tail) + v.substring(v.length() - tail);
    }
}
