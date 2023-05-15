package com.lzw.crm.web.listener;

import com.lzw.crm.settings.domain.*;
import com.lzw.crm.settings.service.*;
import com.lzw.crm.settings.service.impl.*;
import com.lzw.crm.utils.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import java.util.*;

/**
 * ϵͳ��ʼ������������ʼ�������ֵ䵽������
 * ʵ�������ļ���������д��ʼ�������ٷ����������Ĳ������������¼�����
 * ע�����ü�������˵��������һ��������
 */
@WebListener
public class SysInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("���������汣�������ֵ俪ʼ");

        //ͨ���¼�����ȡ�ñ������������������
        ServletContext application = sce.getServletContext();

        /*
        ����ҵ���������ֵ����������ݿ��ȡ�����ֵ䣬��Ϊ���ض��list���ϣ�����Щlist�ٴ����һ��map�ٷ��ظ�������
        map�е�key���ֵ����ͣ�value��list���ϴ�����ֵ����Ͷ�Ӧ�������ֵ�ֵ�б�
        */
        DicService dicService = (DicService) ServiceFactory.getService(new DicServiceImpl());
        Map<String, List<DicValue>> dictionary = dicService.getAll();

        //��map�ļ�ֵ�Խ���ΪӦ��������б���ļ�ֵ��
        Set<String> types = dictionary.keySet();//��ȡmap�е�����key���������ֵ�����
        for (String type : types) {
            //���������ֵ䵽Ӧ�����У�ÿѭ��һ�α���һ��map��key��map��value
            application.setAttribute(type, dictionary.get(type));
        }

        System.out.println("���������汣�������ֵ����");

        //�����ļ�Stage2Possibility.properties�������еļ�ֵ�Դ����Java�еļ�ֵ�ԣ���map������map���浽Ӧ������
        //��Ϊ��ǰ��Maven��Ŀ�����������ļ��Ž���ָ��Ŀ¼resources�У�·��ֱ��д�ļ������ɣ�����main/resources/�ļ���
        ResourceBundle rb = ResourceBundle.getBundle("Stage2Possibility");

        //ʹ����Դ��������getKeys�������Ի�ȡ�ļ��е�����key�������н��׽׶Σ�����һ��ö����
        Enumeration<String> stages = rb.getKeys();

        //����Ҫ�洢���ݵ�map
        Map<String, String> pMap = new HashMap<>();

        //����ö���࣬��ȡ���е�ÿ�����׽׶�
        while (stages.hasMoreElements()) {
            String stage = stages.nextElement();

            //ͨ��ÿ��key��ȡÿ��value����ÿ��������
            String possibility = rb.getString(stage);

            //��ÿ���׶κͿ����Է�װ��map��
            pMap.put(stage, possibility);
        }

        //��map���浽Ӧ������
        application.setAttribute("pMap", pMap);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}