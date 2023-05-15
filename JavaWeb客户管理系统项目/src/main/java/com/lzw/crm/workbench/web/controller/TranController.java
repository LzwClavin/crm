package com.lzw.crm.workbench.web.controller;

import com.lzw.crm.settings.domain.*;
import com.lzw.crm.settings.service.*;
import com.lzw.crm.settings.service.impl.*;
import com.lzw.crm.utils.*;
import com.lzw.crm.workbench.domain.*;
import com.lzw.crm.workbench.service.*;
import com.lzw.crm.workbench.service.impl.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

/**
 * 交易模块
 */
@WebServlet({"/workbench/transaction/add.do", "/workbench/transaction/getCustomerNames.do",
        "/workbench/transaction/save.do", "/workbench/transaction/detail.do",
        "/workbench/transaction/getHistorysByTId.do", "/workbench/transaction/changeStage.do"
        , "/workbench/transaction/getCharts.do"})
public class TranController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/workbench/transaction/add.do":
                add(request, response);
                break;
            case "/workbench/transaction/getCustomerNames.do":
                getCustomerNames(request, response);
                break;
            case "/workbench/transaction/save.do":
                save(request, response);
                break;
            case "/workbench/transaction/detail.do":
                detail(request, response);
                break;
            case "/workbench/transaction/getHistorysByTId.do":
                getHistorysByTId(request, response);
                break;
            case "/workbench/transaction/changeStage.do":
                changeStage(request, response);
                break;
            case "/workbench/transaction/getCharts.do":
                getCharts(request, response);
                break;
        }
    }

    /**
     * 获取用户列表并跳转到交易添加页
     *
     * @param request
     * @param response
     */
    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> users = userService.getList();
        request.setAttribute("users", users);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request, response);
    }

    /**
     * 获取客户名列表并自动补全
     *
     * @param request
     * @param response
     */
    private void getCustomerNames(HttpServletRequest request, HttpServletResponse response) {
        //获取前端提交的查询关键字
        String name = request.getParameter("name");

        //由于是查询客户列表，所以需要调用客户业务
        CustomerService customerService = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        //返回客户名列表
        List<String> names = customerService.getCustomerNames(name);

        //由于传递的是一个字符串集合，所以不进行转换成JSON，直接响应集合
        PrintJson.printJsonObj(response, names);
    }

    /**
     * 创建交易记录
     *
     * @param request
     * @param response
     */
    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String customerName = request.getParameter("customerName");//没有客户id，获取客户名
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        String createBy = ((User) request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        //创建交易对象封装数据，不封装客户名
        Tran tran = new Tran(id, owner, money, name, expectedDate, null, stage, type, source, activityId,
                contactsId, createBy, createTime, null, null, description, contactSummary, nextContactTime);
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());

        /*
        传递客户名给业务层处理，获取对应的客户id，业务层必须创建交易记录和交易历史记录，有可能需要创建客户记录
        需要使用多个id、创建者和创建时间，但这里不需要传递这些参数过去，因为id自动生成、创建时间和创建者都可以在tran中获取
        */
        boolean flag = tranService.save(tran, customerName);

        /*
        如果添加成功，重定向到交易主页，页面中的交易列表使用Ajax局部生成，因为涉及到了多处需要局部刷新，所以不使用转发
        而且如果使用转发，地址栏的路径还是当前Servlet，每次刷新都会重复添加交易记录；重定向的路径是目标jsp路径，不存在重复添加的问题
        */
        if (flag) {
            response.sendRedirect(request.getContextPath() + "/workbench/transaction/index.jsp");
        }
    }

    /**
     * 根据交易id获取交易记录，跳转到交易详情页
     *
     * @param request
     * @param response
     */
    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        Tran tran = tranService.detail(id);

        //获取阶段
        String stage = tran.getStage();

        //获取应用域对象
        ServletContext application = this.getServletContext();

        //获取应用域中的pMap，即阶段和可能性的映射关系
        Map<String, String> pMap = (Map<String, String>) application.getAttribute("pMap");

        //获取可能性
        String possibility = pMap.get(stage);

        //设置tran对象的可能性属性，然后可以保存到请求域中，供详情页使用
        tran.setPossibility(possibility);
        request.setAttribute("tran", tran);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request, response);
    }

    /**
     * 根据交易id获取交易历史列表
     *
     * @param request
     * @param response
     */
    private void getHistorysByTId(HttpServletRequest request, HttpServletResponse response) {
        String tranId = request.getParameter("tranId");
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<TranHistory> histories = tranService.getHistorysByTId(tranId);

        //获取应用域对象
        ServletContext application = this.getServletContext();

        //获取应用域中的pMap，即阶段和可能性的映射关系；这两句放在循环体外部，没必要每次循环都获取一次
        Map<String, String> pMap = (Map<String, String>) application.getAttribute("pMap");

        //遍历交易历史列表
        for (TranHistory history : histories) {
            //获取每个交易历史对象中的阶段
            String stage = history.getStage();

            //获取可能性
            String possibility = pMap.get(stage);

            //设置history对象的可能性属性，然后可以响应到前端，供详情页使用
            history.setPossibility(possibility);
        }

        PrintJson.printJsonObj(response, histories);
    }

    /**
     * 修改交易阶段和交易阶段图标
     *
     * @param request
     * @param response
     */
    private void changeStage(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");
        String editBy = ((User) request.getSession().getAttribute("user")).getName();
        String editTime = DateTimeUtil.getSysTime();
        Tran tran = new Tran();
        tran.setId(id);
        tran.setStage(stage);
        tran.setMoney(money);
        tran.setExpectedDate(expectedDate);
        tran.setEditBy(editBy);
        tran.setEditTime(editTime);
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());

        //将所有请求参数封装成一个交易对象并往下传递，返回是否变更成功
        boolean flag = tranService.changeStage(tran);

        //获取应用域对象
        ServletContext application = this.getServletContext();

        //获取应用域中的pMap，即阶段和可能性的映射关系
        Map<String, String> pMap = (Map<String, String>) application.getAttribute("pMap");

        //获取可能性
        String possibility = pMap.get(stage);

        //设置tran对象的可能性属性，然后可以响应到前端，供详情页使用
        tran.setPossibility(possibility);

        //将布尔标记和交易对象封装到一个map中并转成JSON再响应给前端
        Map<String, Object> map = new HashMap<>();
        map.put("success", flag);
        map.put("tran", tran);
        PrintJson.printJsonObj(response, map);
    }

    /**
     * 获取交易阶段数量统计图需要的数据
     *
     * @param request
     * @param response
     */
    private void getCharts(HttpServletRequest request, HttpServletResponse response) {
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());

        /*
        前端需要total和统计数据，这里使用map封装；如果统计图较多，且统计图中都需要使用到name和value，使用vo方便
        另外，这里也可以使用分页插件中封装的vo，即total和泛型列表，但由于泛型要变成map，处理较麻烦，所以不使用
        */
        Map<String, Object> map = tranService.getCharts();
        PrintJson.printJsonObj(response, map);
    }
}