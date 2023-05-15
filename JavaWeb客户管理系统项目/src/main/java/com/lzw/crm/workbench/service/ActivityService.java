package com.lzw.crm.workbench.service;

import com.lzw.crm.vo.*;
import com.lzw.crm.workbench.domain.*;

import java.util.*;

public interface ActivityService {
    boolean save(Activity activity);

    PaginationVO<Activity> getActivitys(Map<String, Object> map);

    boolean delete(String[] ids);

    Map<String, Object> getUsersActivity(String id);

    boolean update(Activity activity);

    Activity detail(String id);

    List<ActivityRemark> getRemarksByAId(String aid);

    boolean deleteRemark(String id);

    boolean saveRemark(ActivityRemark activityRemark);

    boolean updateRemark(ActivityRemark activityRemark);

    List<Activity> getActivitysByCId(String id);

    List<Activity> getActivitysNNCI(Map<String, String> map);

    List<Activity> getActivitysByName(String aname);
}