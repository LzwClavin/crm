package com.lzw.crm.settings.dao;

import com.lzw.crm.settings.domain.*;

import java.util.*;

public interface UserDao {
    User login(Map<String, String> map);

    List<User> getList();
}