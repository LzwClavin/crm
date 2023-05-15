package com.lzw.crm.web.listener;

import com.lzw.crm.settings.domain.*;
import com.lzw.crm.settings.service.*;
import com.lzw.crm.settings.service.impl.*;
import com.lzw.crm.utils.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import java.util.*;

/**
 * 系统初始化监听器，初始化数据字典到缓存中
 * 实现上下文监听器，重写初始化和销毁方法，方法的参数是上下文事件对象
 * 注解配置监听器，说明本类是一个监听器
 */
@WebListener
public class SysInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("服务器缓存保存数据字典开始");

        //通过事件对象取得被监听的上下文域对象
        ServletContext application = sce.getServletContext();

        /*
        调用业务层的数据字典类连接数据库获取数据字典，因为返回多个list集合，将这些list再打包成一个map再返回给监听器
        map中的key是字典类型，value是list集合代表的字典类型对应的整个字典值列表
        */
        DicService dicService = (DicService) ServiceFactory.getService(new DicServiceImpl());
        Map<String, List<DicValue>> dictionary = dicService.getAll();

        //将map的键值对解析为应用域对象中保存的键值对
        Set<String> types = dictionary.keySet();//获取map中的所有key，即所有字典类型
        for (String type : types) {
            //保存数据字典到应用域中，每循环一次保存一个map的key和map的value
            application.setAttribute(type, dictionary.get(type));
        }

        System.out.println("服务器缓存保存数据字典结束");

        //解析文件Stage2Possibility.properties，将其中的键值对处理成Java中的键值对，即map，并将map保存到应用域中
        //因为当前是Maven项目，属性配置文件放进了指定目录resources中，路径直接写文件名即可，不用main/resources/文件名
        ResourceBundle rb = ResourceBundle.getBundle("Stage2Possibility");

        //使用资源绑定器访问getKeys方法可以获取文件中的所有key，即所有交易阶段，返回一个枚举类
        Enumeration<String> stages = rb.getKeys();

        //定义要存储数据的map
        Map<String, String> pMap = new HashMap<>();

        //遍历枚举类，获取其中的每个交易阶段
        while (stages.hasMoreElements()) {
            String stage = stages.nextElement();

            //通过每个key获取每个value，即每个可能性
            String possibility = rb.getString(stage);

            //将每个阶段和可能性封装到map中
            pMap.put(stage, possibility);
        }

        //将map保存到应用域中
        application.setAttribute("pMap", pMap);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}