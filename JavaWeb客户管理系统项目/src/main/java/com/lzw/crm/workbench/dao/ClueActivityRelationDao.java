package com.lzw.crm.workbench.dao;

import com.lzw.crm.workbench.domain.*;

import java.util.*;

public interface ClueActivityRelationDao {
    int unbund(String id);

    int bund(ClueActivityRelation clueActivityRelation);

    List<ClueActivityRelation> getListByCId(String cid);
}