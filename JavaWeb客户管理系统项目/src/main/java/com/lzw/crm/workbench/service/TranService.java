package com.lzw.crm.workbench.service;

import com.lzw.crm.workbench.domain.*;

import java.util.*;

public interface TranService {
    boolean save(Tran tran, String customerName);

    Tran detail(String id);

    List<TranHistory> getHistorysByTId(String tranId);

    boolean changeStage(Tran tran);

    Map<String, Object> getCharts();
}