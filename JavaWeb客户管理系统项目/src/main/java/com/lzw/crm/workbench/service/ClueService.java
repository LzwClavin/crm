package com.lzw.crm.workbench.service;

import com.lzw.crm.workbench.domain.*;

public interface ClueService {
    boolean save(Clue clue);

    Clue detail(String id);

    boolean unbund(String id);

    boolean bund(String cid, String[] aids);

    boolean convert(String cid, Tran tran, String createBy);
}