package com.lzw.crm.workbench.dao;

import com.lzw.crm.workbench.domain.*;

import java.util.*;

public interface CustomerDao {
    Customer getCustomerByName(String company);

    int save(Customer customer);

    List<String> getCustomerNames(String name);
}