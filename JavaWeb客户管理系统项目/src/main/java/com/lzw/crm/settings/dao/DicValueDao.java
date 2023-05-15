package com.lzw.crm.settings.dao;

import com.lzw.crm.settings.domain.*;

import java.util.*;

public interface DicValueDao {
    List<DicValue> getValues(String code);
}