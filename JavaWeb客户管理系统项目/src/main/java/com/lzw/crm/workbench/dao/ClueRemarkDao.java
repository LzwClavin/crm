package com.lzw.crm.workbench.dao;

import com.lzw.crm.workbench.domain.*;

import java.util.*;

public interface ClueRemarkDao {
    List<ClueRemark> getListByCId(String cid);

    int deleteByCId(String cid);
}