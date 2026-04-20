package com.huacai.workbench.service;

import com.huacai.workbench.query.WorkbenchQuery;
import com.huacai.workbench.vo.WorkbenchOverviewVO;

public interface WorkbenchService {

    WorkbenchOverviewVO overview(WorkbenchQuery query);
}
