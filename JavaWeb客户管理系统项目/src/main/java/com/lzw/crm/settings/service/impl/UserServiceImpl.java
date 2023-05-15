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
        //将明文密码使用MD5算法转为密文密码
        password = MD5Util.getMD5(password);

        //将用户名和密码封装成一个map集合，并传递给dao层
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", username);
        map.put("password", password);
        User user = userDao.login(map);

        //如果返回的User对象是null，抛出异常给控制器
        if (null == user) {
            throw new LoginException("用户名或密码不存在！");
        }

        //如果程序执行到此，说明账号和密码正确，因为不正确时已经抛出异常，不会再执行后面的语句，此时验证失效时间
        String expireTime = user.getExpireTime();
        String currentTime = DateTimeUtil.getSysTime();
        int compare = expireTime.compareTo(currentTime);
        if (compare <= 0) {
            throw new LoginException("该账号已失效，无法登录！");
        }

        //如果程序执行到此，说明以上的验证都通过，因为不通过时已经抛出异常，不会再执行后面的语句，此时验证账号状态
        String lockState = user.getLockState();
        if (Integer.parseInt(lockState) == 0) {
            throw new LoginException("账号已被锁定，无法登录！");
        }

        //验证浏览器的IP地址
        String allowIps = user.getAllowIps();
        String[] ais = allowIps.split(",");

        //二分法是建立在顺序数组的基础上的，在进行二分法查找之前，必须对数组排序
        Arrays.sort(ais);
        int index = Arrays.binarySearch(ais, ip);
        if (index < 0) {
            throw new LoginException("IP地址受限，登录失败！");
        }

        //所有验证读通过，返回User对象给控制器保存到session域
        return user;
    }

    @Override
    public List<User> getList() {
        return userDao.getList();
    }
}