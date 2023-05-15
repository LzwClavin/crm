package com.lzw.crm.workbench.dao;

import com.lzw.crm.workbench.domain.*;

import java.util.*;

public interface TranHistoryDao {
    int save(TranHistory tranHistory);

    List<TranHistory> getHistorysByTId(String tranId);
}