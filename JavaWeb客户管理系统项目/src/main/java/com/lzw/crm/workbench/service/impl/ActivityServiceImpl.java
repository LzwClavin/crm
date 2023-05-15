package com.lzw.crm.workbench.service.impl;

import com.lzw.crm.settings.dao.*;
import com.lzw.crm.settings.domain.*;
import com.lzw.crm.utils.*;
import com.lzw.crm.vo.*;
import com.lzw.crm.workbench.dao.*;
import com.lzw.crm.workbench.domain.*;
import com.lzw.crm.workbench.service.*;

import java.util.*;

public class ActivityServiceImpl implements ActivityService {
    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public boolean save(Activity activity) {
        /*
        调用dao层的保存方法，返回的是影响记录条数
        如果添加过程中出现异常，这里需要抛出自定义异常，但如果每个需求都抛出自定义异常，需求多了会很麻烦，这里只使用返回值表示成功或失败
        */
        int count = activityDao.save(activity);

        //如果添加成功，count肯定是1，返回true，否则添加失败，返回false
        return count == 1;
    }

    @Override
    public PaginationVO<Activity> getActivitys(Map<String, Object> map) {
        //获取total，根据条件获取总记录条数，条件在map中
        int total = activityDao.getTotalByCondition(map);

        //获取市场活动列表，根据条件获取列表，条件在map中
        List<Activity> list = activityDao.getActivitysByCondition(map);

        //封装两个数据到vo中并返回
        PaginationVO<Activity> vo = new PaginationVO<>(total, list);
        return vo;
    }

    @Override
    public boolean delete(String[] ids) {
        /*
        在删除市场活动之前，需要判断市场活动有没有关联到市场活动备注，如果关联，涉及到级联删除
        需要先将市场活动下的所有备注删除再删除该市场活动；先查询出需要删除的备注数量，因为不清楚关联的备注有多少条
        且返回的是一个影响记录条数，即实际删除的数量，要确保实际删除的数量与实际的备注数量相等才能判断为删除成功
        删除市场活动时同理，获取参数数组的长度即可得到需要删除的市场活动数量
        */
        boolean flag = true;

        //根据备注表的外键id表示的市场活动id查询出所有匹配的备注的数量
        int count1 = activityRemarkDao.getCountByAIds(ids);

        //根据市场活动id删除对应的备注
        int count2 = activityRemarkDao.deleteByAIds(ids);

        //判断是否删除成功
        if (count1 != count2) {
            flag = false;
        }

        //删除市场活动
        int count3 = activityDao.delete(ids);

        //判断是否删除成功
        if (count3 != ids.length) {
            flag = false;
        }

        return flag;
    }

    @Override
    public Map<String, Object> getUsersActivity(String id) {
        //获取用户列表
        List<User> users = userDao.getList();

        //获取市场活动对象
        Activity activity = activityDao.getById(id);

        //将两个数据打包到map中并返回
        Map<String, Object> map = new HashMap<>();
        map.put("users", users);
        map.put("activity", activity);
        return map;
    }

    @Override
    public boolean update(Activity activity) {
        //调用dao层的保存方法，返回的是影响记录条数
        int count = activityDao.update(activity);

        //如果修改成功，count肯定是1，返回true，否则修改失败，返回false
        return count == 1;
    }

    @Override
    public Activity detail(String id) {
        /*
        不能复用getById方法，因为页面上显示的所有者是用户姓名，而表中查询出来的是用户id，需要先将id经过处理变成姓名才能返回
        写一个新方法处理id为姓名
        */
        Activity activity = activityDao.detail(id);
        return activity;
    }

    @Override
    public List<ActivityRemark> getRemarksByAId(String aid) {
        List<ActivityRemark> activityRemarks = activityRemarkDao.getRemarksByAId(aid);
        return activityRemarks;
    }

    @Override
    public boolean deleteRemark(String id) {
        int count = activityRemarkDao.deleteById(id);
        return count == 1;
    }

    @Override
    public boolean saveRemark(ActivityRemark activityRemark) {
        int count = activityRemarkDao.saveRemark(activityRemark);
        return count == 1;
    }

    @Override
    public boolean updateRemark(ActivityRemark activityRemark) {
        int count = activityRemarkDao.updateRemark(activityRemark);
        return count == 1;
    }

    @Override
    public List<Activity> getActivitysByCId(String id) {
        List<Activity> activities = activityDao.getActivitysByCId(id);
        return activities;
    }

    @Override
    public List<Activity> getActivitysNNCI(Map<String, String> map) {
        List<Activity> activities = activityDao.getActivitysNNCI(map);
        return activities;
    }

    @Override
    public List<Activity> getActivitysByName(String aname) {
        List<Activity> activities = activityDao.getActivitysByName(aname);
        return activities;
    }
}