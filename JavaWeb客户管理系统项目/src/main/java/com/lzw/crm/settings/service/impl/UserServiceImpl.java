package com.lzw.crm.settings.service.impl;

import com.lzw.crm.exception.*;
import com.lzw.crm.settings.dao.*;
import com.lzw.crm.settings.domain.*;
import com.lzw.crm.settings.service.*;
import com.lzw.crm.utils.*;

import java.util.*;

public class UserServiceImpl implements UserService {
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    public User login(String username, String password, String ip) throws LoginException {
        //����������ʹ��MD5�㷨תΪ��������
        password = MD5Util.getMD5(password);

        //���û����������װ��һ��map���ϣ������ݸ�dao��
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", username);
        map.put("password", password);
        User user = userDao.login(map);

        //������ص�User������null���׳��쳣��������
        if (null == user) {
            throw new LoginException("�û��������벻���ڣ�");
        }

        //�������ִ�е��ˣ�˵���˺ź�������ȷ����Ϊ����ȷʱ�Ѿ��׳��쳣��������ִ�к������䣬��ʱ��֤ʧЧʱ��
        String expireTime = user.getExpireTime();
        String currentTime = DateTimeUtil.getSysTime();
        int compare = expireTime.compareTo(currentTime);
        if (compare <= 0) {
            throw new LoginException("���˺���ʧЧ���޷���¼��");
        }

        //�������ִ�е��ˣ�˵�����ϵ���֤��ͨ������Ϊ��ͨ��ʱ�Ѿ��׳��쳣��������ִ�к������䣬��ʱ��֤�˺�״̬
        String lockState = user.getLockState();
        if (Integer.parseInt(lockState) == 0) {
            throw new LoginException("�˺��ѱ��������޷���¼��");
        }

        //��֤�������IP��ַ
        String allowIps = user.getAllowIps();
        String[] ais = allowIps.split(",");

        //���ַ��ǽ�����˳������Ļ����ϵģ��ڽ��ж��ַ�����֮ǰ���������������
        Arrays.sort(ais);
        int index = Arrays.binarySearch(ais, ip);
        if (index < 0) {
            throw new LoginException("IP��ַ���ޣ���¼ʧ�ܣ�");
        }

        //������֤��ͨ��������User��������������浽session��
        return user;
    }

    @Override
    public List<User> getList() {
        return userDao.getList();
    }
}