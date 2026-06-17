package com.huacai.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PiiMaskUtilTest {

    @Test
    void maskIdCardKeepsHead6Tail4() {
        assertThat(PiiMaskUtil.maskIdCard("440100199001011234"))
                .isEqualTo("440100********1234");
    }

    @Test
    void maskBankAccountKeepsTail4() {
        assertThat(PiiMaskUtil.maskBankAccount("6222000000001234"))
                .isEqualTo("************1234");
        assertThat(PiiMaskUtil.maskBankAccount("6222 0000 0000 1234"))
                .isEqualTo("************1234");
    }

    @Test
    void maskPhoneKeepsHead3Tail4() {
        assertThat(PiiMaskUtil.maskPhone("13800001234")).isEqualTo("138****1234");
    }

    @Test
    void blankAndNullAreReturnedAsIs() {
        assertThat(PiiMaskUtil.maskIdCard(null)).isNull();
        assertThat(PiiMaskUtil.maskBankAccount("")).isEqualTo("");
        assertThat(PiiMaskUtil.maskPhone("  ")).isEqualTo("  ");
    }

    @Test
    void shortValuesDoNotThrow() {
        assertThat(PiiMaskUtil.maskBankAccount("12")).isEqualTo("12");
        assertThat(PiiMaskUtil.maskIdCard("123")).isEqualTo("1*3");
    }
}