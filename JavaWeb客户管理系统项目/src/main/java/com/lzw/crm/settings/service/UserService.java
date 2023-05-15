package com.lzw.crm.settings.service;

import com.lzw.crm.exception.*;
import com.lzw.crm.settings.domain.*;

import java.util.List;

public interface UserService {
    User login(String username, String password, String ip) throws LoginException;

    List<User> getList();
}