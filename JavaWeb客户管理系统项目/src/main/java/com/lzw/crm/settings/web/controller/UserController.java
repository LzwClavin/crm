package com.lzw.crm.settings.web.controller;

import com.lzw.crm.settings.domain.*;
import com.lzw.crm.settings.service.*;
import com.lzw.crm.settings.service.impl.*;
import com.lzw.crm.utils.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

/**
 * 用户模块
 */
@WebServlet({"/settings/user/login.do", "/settings/user/save.do"})
public class UserController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/settings/user/login.do":
                login(request, response);
                break;
            case "/settings/user/save.do":
                save(request, response);
                break;
        }
    }

    /**
     * 验证登录
     *
     * @param request
     * @param response
     */
    private void login(HttpServletRequest request, HttpServletResponse response) {
        //虽然登录不用走事务，但登录的扩展业务中有可能使用日志操作，即往数据库中新增日志记录，需要走事务，经过代理类，使用代理类接口对象
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        /*
        获取浏览器IP地址，如果请求时地址栏上的本机地址不是127.0.0.1，是localhost，这里返回0:0:0:0:0:0:0:1
        所以实际中不能使用localhost
        */
        String ip = request.getRemoteAddr();
        System.out.println(ip);
        try {
            User user = userService.login(username, password, ip);

            /*
            获取会话域对象并将User对象存进会话域；如果登录失败，如用户名或密码不匹配数据库记录，返回null，保存的是null
            所以登录验证失败保存User对象失败，也没必要保存，这里将本语句放进try域且在调用验证登录方法的后面
            一旦验证登录失败，底层会抛出异常给控制器，此时本语句就不会被执行，直接执行catch域处理异常
            但事实上并不会跳转到catch域，本语句和下一句还是会执行，从而导致登录验证失败也会被认为成功然后跳转到首页
            发生这种情况的原因是userService是一个代理对象，调用login方法时先调用代理类的invoke方法
            在invoke方法中调用真实的业务方法login，login抛出异常给invoke方法接收，然后执行其中的catch域处理了异常，打印异常堆栈信息
            再返回User对象给控制器，因为已经没有异常要处理，就继续往下执行，存储User对象进session域、响应true给前端并跳转页面
            */
            request.getSession().setAttribute("user", user);

            //响应JSON字符串给前端，不用使用msg属性，因为登录成功没有错误信息；使用Jackson工具拼接JSON字符串
            PrintJson.printJsonFlag(response, true);
        } catch (Exception e) {
            e.printStackTrace();

            //错误信息就是底层异常对象的异常信息
            String msg = e.getMessage();

            /*
            将布尔标记和错误信息包装成Map集合的键值对；另外可以创建一个vo，即将布尔标记和错误信息封装成对象
            如果响应的信息经常使用，创建vo类对象；如果响应的信息很少使用，使用map集合；这里少用，所以使用map集合
            */
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("success", false);
            map.put("msg", msg);

            //响应JSON字符串给前端
            PrintJson.printJsonObj(response, map);
        }
    }

    private void save(HttpServletRequest request, HttpServletResponse response) {

    }
}