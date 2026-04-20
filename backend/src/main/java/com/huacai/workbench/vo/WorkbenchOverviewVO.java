package com.huacai.workbench.vo;

import java.util.List;

public record WorkbenchOverviewVO(
        List<WorkbenchMetricVO> metricCards,
        List<WorkbenchRecordVO> focusRows,
        List<WorkbenchTodoVO> todoItems,
        List<WorkbenchReminderVO> reminderItems
) {
}
