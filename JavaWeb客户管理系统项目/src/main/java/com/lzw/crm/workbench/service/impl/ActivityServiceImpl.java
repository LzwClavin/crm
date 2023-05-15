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
        ����dao��ı��淽�������ص���Ӱ���¼����
        �����ӹ����г����쳣��������Ҫ�׳��Զ����쳣�������ÿ�������׳��Զ����쳣��������˻���鷳������ֻʹ�÷���ֵ��ʾ�ɹ���ʧ��
        */
        int count = activityDao.save(activity);

        //�����ӳɹ���count�϶���1������true���������ʧ�ܣ�����false
        return count == 1;
    }

    @Override
    public PaginationVO<Activity> getActivitys(Map<String, Object> map) {
        //��ȡtotal������������ȡ�ܼ�¼������������map��
        int total = activityDao.getTotalByCondition(map);

        //��ȡ�г���б�����������ȡ�б�������map��
        List<Activity> list = activityDao.getActivitysByCondition(map);

        //��װ�������ݵ�vo�в�����
        PaginationVO<Activity> vo = new PaginationVO<>(total, list);
        return vo;
    }

    @Override
    public boolean delete(String[] ids) {
        /*
        ��ɾ���г��֮ǰ����Ҫ�ж��г����û�й������г����ע������������漰������ɾ��
        ��Ҫ�Ƚ��г���µ����б�עɾ����ɾ�����г�����Ȳ�ѯ����Ҫɾ���ı�ע��������Ϊ����������ı�ע�ж�����
        �ҷ��ص���һ��Ӱ���¼��������ʵ��ɾ����������Ҫȷ��ʵ��ɾ����������ʵ�ʵı�ע������Ȳ����ж�Ϊɾ���ɹ�
        ɾ���г��ʱͬ����ȡ��������ĳ��ȼ��ɵõ���Ҫɾ�����г������
        */
        boolean flag = true;

        //���ݱ�ע������id��ʾ���г��id��ѯ������ƥ��ı�ע������
        int count1 = activityRemarkDao.getCountByAIds(ids);

        //�����г��idɾ����Ӧ�ı�ע
        int count2 = activityRemarkDao.deleteByAIds(ids);

        //�ж��Ƿ�ɾ���ɹ�
        if (count1 != count2) {
            flag = false;
        }

        //ɾ���г��
        int count3 = activityDao.delete(ids);

        //�ж��Ƿ�ɾ���ɹ�
        if (count3 != ids.length) {
            flag = false;
        }

        return flag;
    }

    @Override
    public Map<String, Object> getUsersActivity(String id) {
        //��ȡ�û��б�
        List<User> users = userDao.getList();

        //��ȡ�г������
        Activity activity = activityDao.getById(id);

        //���������ݴ����map�в�����
        Map<String, Object> map = new HashMap<>();
        map.put("users", users);
        map.put("activity", activity);
        return map;
    }

    @Override
    public boolean update(Activity activity) {
        //����dao��ı��淽�������ص���Ӱ���¼����
        int count = activityDao.update(activity);

        //����޸ĳɹ���count�϶���1������true�������޸�ʧ�ܣ�����false
        return count == 1;
    }

    @Override
    public Activity detail(String id) {
        /*
        ���ܸ���getById��������Ϊҳ������ʾ�����������û������������в�ѯ���������û�id����Ҫ�Ƚ�id�����������������ܷ���
        дһ���·�������idΪ����
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