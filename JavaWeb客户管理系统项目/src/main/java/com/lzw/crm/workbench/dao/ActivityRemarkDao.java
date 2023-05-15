package com.lzw.crm.workbench.dao;

import com.lzw.crm.workbench.domain.*;

import java.util.*;

public interface ActivityRemarkDao {
    int getCountByAIds(String[] ids);

    int deleteByAIds(String[] ids);

    List<ActivityRemark> getRemarksByAId(String aid);

    int deleteById(String id);

    int saveRemark(ActivityRemark activityRemark);

    int updateRemark(ActivityRemark activityRemark);
}