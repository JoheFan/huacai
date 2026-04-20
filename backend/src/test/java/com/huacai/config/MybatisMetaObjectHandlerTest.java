package com.huacai.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.huacai.loan.entity.LoanOrder;
import java.time.LocalDateTime;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.junit.jupiter.api.Test;

class MybatisMetaObjectHandlerTest {

    private final MybatisMetaObjectHandler handler = new MybatisMetaObjectHandler();

    @Test
    void updateFillOverwritesExistingUpdatedAtValue() {
        LoanOrder order = new LoanOrder();
        LocalDateTime original = LocalDateTime.of(2026, 1, 15, 8, 30);
        order.setUpdatedAt(original);

        MetaObject metaObject = SystemMetaObject.forObject(order);
        handler.updateFill(metaObject);

        assertThat(order.getUpdatedAt()).isAfter(original);
    }
}
