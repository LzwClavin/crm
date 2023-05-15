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
 * �û�ģ��
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
     * ��֤��¼
     *
     * @param request
     * @param response
     */
    private void login(HttpServletRequest request, HttpServletResponse response) {
        //��Ȼ��¼���������񣬵���¼����չҵ�����п���ʹ����־�������������ݿ���������־��¼����Ҫ�����񣬾��������࣬ʹ�ô�����ӿڶ���
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        /*
        ��ȡ�����IP��ַ���������ʱ��ַ���ϵı�����ַ����127.0.0.1����localhost�����ﷵ��0:0:0:0:0:0:0:1
        ����ʵ���в���ʹ��localhost
        */
        String ip = request.getRemoteAddr();
        System.out.println(ip);
        try {
            User user = userService.login(username, password, ip);

            /*
            ��ȡ�Ự����󲢽�User�������Ự�������¼ʧ�ܣ����û��������벻ƥ�����ݿ��¼������null���������null
            ���Ե�¼��֤ʧ�ܱ���User����ʧ�ܣ�Ҳû��Ҫ���棬���ｫ�����Ž�try�����ڵ�����֤��¼�����ĺ���
            һ����֤��¼ʧ�ܣ��ײ���׳��쳣������������ʱ�����Ͳ��ᱻִ�У�ֱ��ִ��catch�����쳣
            ����ʵ�ϲ�������ת��catch�򣬱�������һ�仹�ǻ�ִ�У��Ӷ����µ�¼��֤ʧ��Ҳ�ᱻ��Ϊ�ɹ�Ȼ����ת����ҳ
            �������������ԭ����userService��һ��������󣬵���login����ʱ�ȵ��ô������invoke����
            ��invoke�����е�����ʵ��ҵ�񷽷�login��login�׳��쳣��invoke�������գ�Ȼ��ִ�����е�catch�������쳣����ӡ�쳣��ջ��Ϣ
            �ٷ���User���������������Ϊ�Ѿ�û���쳣Ҫ�����ͼ�������ִ�У��洢User�����session����Ӧtrue��ǰ�˲���תҳ��
            */
            request.getSession().setAttribute("user", user);

            //��ӦJSON�ַ�����ǰ�ˣ�����ʹ��msg���ԣ���Ϊ��¼�ɹ�û�д�����Ϣ��ʹ��Jackson����ƴ��JSON�ַ���
            PrintJson.printJsonFlag(response, true);
        } catch (Exception e) {
            e.printStackTrace();

            //������Ϣ���ǵײ��쳣������쳣��Ϣ
            String msg = e.getMessage();

            /*
            ��������Ǻʹ�����Ϣ��װ��Map���ϵļ�ֵ�ԣ�������Դ���һ��vo������������Ǻʹ�����Ϣ��װ�ɶ���
            �����Ӧ����Ϣ����ʹ�ã�����vo����������Ӧ����Ϣ����ʹ�ã�ʹ��map���ϣ��������ã�����ʹ��map����
            */
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("success", false);
            map.put("msg", msg);

            //��ӦJSON�ַ�����ǰ��
            PrintJson.printJsonObj(response, map);
        }
    }

    private void save(HttpServletRequest request, HttpServletResponse response) {

    }
}