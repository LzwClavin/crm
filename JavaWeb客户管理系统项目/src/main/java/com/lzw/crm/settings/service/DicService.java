package com.lzw.crm.settings.service;

import com.lzw.crm.settings.domain.*;

import java.util.*;

public interface DicService {
    Map<String, List<DicValue>> getAll();
}