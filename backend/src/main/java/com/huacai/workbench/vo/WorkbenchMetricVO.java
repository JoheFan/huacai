package com.huacai.workbench.vo;

public record WorkbenchMetricVO(
        String title,
        String value,
        String helper,
        String note,
        boolean emphasized
) {
}
