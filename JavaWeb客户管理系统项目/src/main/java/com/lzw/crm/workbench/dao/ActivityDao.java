package com.lzw.crm.workbench.dao;

import com.lzw.crm.workbench.domain.*;

import java.util.*;

public interface ActivityDao {
    int save(Activity activity);

    int getTotalByCondition(Map<String, Object> map);

    List<Activity> getActivitysByCondition(Map<String, Object> map);

    int delete(String[] ids);

    Activity getById(String id);

    int update(Activity activity);

    Activity detail(String id);

    List<Activity> getActivitysByCId(String id);

    List<Activity> getActivitysNNCI(Map<String, String> map);

    List<Activity> getActivitysByName(String aname);
}