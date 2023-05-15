package com.lzw.crm.settings.test;

import com.lzw.crm.utils.*;

import java.text.*;
import java.util.*;

/**
 * 验证账号是否失效或锁定、验证IP地址是否受限、md5加密密码
 */
public class test1 {
    public static void main(String[] args) {
        //账号失效时间，实际为数据表中的字段值或User对象中的属性值
        String expireTime = "2024-05-01 08:00:00";

        //获取当前系统时间
        Date date = new Date();

        //格式化当前系统时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(date);
        System.out.println(currentTime);

        //使用工具类获取格式化当前系统时间
        currentTime = DateTimeUtil.getSysTime();
        System.out.println(currentTime);

        /*
        调用compareTo方法比较失效时间和当前系统时间
        在格式相同的前提下，将两个字符串从高位到低位逐个字符比较，能实现字符串形式的时间比较
        返回一个整数，如果大于0，表示调用者大，账号未失效；如果小于0，表示参数大，账号已失效；如果等于0，两者相等，账号已失效
        */
        int compare = expireTime.compareTo(currentTime);
        System.out.println(compare);
        expireTime = "2022-05-01 08:00:00";
        compare = expireTime.compareTo(currentTime);
        System.out.println(compare);

        //账号状态，设为锁定
        String lockState = "0";

        //必须使用字符串常量调用equals，不能使用lockState调用equals，因为如果lockState从数据库中查询返回null，会出现空指针异常
        if ("0".equals(lockState)) {
            System.out.println("账号已被锁定，无法登录！");
        }

        //发送请求的浏览器端的IP地址
        String ip = "192.168.1.1";

        //数据表中允许访问的IP地址群，多个IP地址之间使用逗号分隔
        String allowIps = "192.168.1.10,127.0.0.1";

        //将字符串使用逗号切割成一个字符串数组，存放每个有效IP
        String[] ais = allowIps.split(",");

        //二分法查找字符串数组中是否有浏览器的IP地址，如果有，返回元素的下标
        int index = Arrays.binarySearch(ais, ip);

        //如果返回的下标是负数，说明浏览器的IP地址不在白名单内，无法登录
        if (index < 0) {
            System.out.println("IP地址受限，登录失败！");
        }

        //密码
        String pwd = "123";

        //md5加密
        String md5Pwd = MD5Util.getMD5(pwd);
        System.out.println(md5Pwd);//202cb962ac59075b964b07152d234b70
        System.out.println(MD5Util.getMD5("lzw695"));//f187acdcde072ff7a551908f127ef985
    }
}