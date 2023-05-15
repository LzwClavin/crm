package com.lzw.crm.workbench.service.impl;

import com.lzw.crm.utils.*;
import com.lzw.crm.workbench.dao.*;
import com.lzw.crm.workbench.service.*;

import java.util.*;

public class CustomerServiceImpl implements CustomerService {
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public List<String> getCustomerNames(String name) {
        List<String> names = customerDao.getCustomerNames(name);
        return names;
    }
}