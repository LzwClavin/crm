package com.lzw.crm.workbench.dao;

import com.lzw.crm.workbench.domain.*;

public interface ClueDao {
    int save(Clue clue);

    Clue detail(String id);

    Clue getById(String cid);

    int delete(String cid);
}