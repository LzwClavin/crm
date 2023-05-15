package com.lzw.crm.workbench.dao;

import com.lzw.crm.workbench.domain.*;

import java.util.*;

public interface TranDao {
    int save(Tran tran);

    Tran detail(String id);

    int changeStage(Tran tran);

    int getTotal();

    List<Map<String, Object>> getCharts();
}