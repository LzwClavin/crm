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
 * 线索模块
 */
@WebServlet({"/workbench/clue/getUsers.do", "/workbench/clue/save.do", "/workbench/clue/detail.do",
        "/workbench/clue/getActivitysByCId.do", "/workbench/clue/unbund.do", "/workbench/clue/getActivitysNNCI.do",
        "/workbench/clue/bund.do", "/workbench/clue/getActivitysByName.do", "/workbench/clue/convert.do"})
public class ClueController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/workbench/clue/getUsers.do":
                getUsers(request, response);
                break;
            case "/workbench/clue/save.do":
                save(request, response);
                break;
            case "/workbench/clue/detail.do":
                detail(request, response);
                break;
            case "/workbench/clue/getActivitysByCId.do":
                getActivitysByCId(request, response);
                break;
            case "/workbench/clue/unbund.do":
                unbund(request, response);
                break;
            case "/workbench/clue/getActivitysNNCI.do":
                getActivitysNNCI(request, response);
                break;
            case "/workbench/clue/bund.do":
                bund(request, response);
                break;
            case "/workbench/clue/getActivitysByName.do":
                getActivitysByName(request, response);
                break;
            case "/workbench/clue/convert.do":
                convert(request, response);
                break;
        }
    }

    /**
     * 获取用户列表，不要复用市场活动模块的该功能，因为该请求是从线索模块发出的，应该由线索模块的控制器处理
     * 而且如果以后线索模块的该功能进行扩展，此时与市场活动模块的该功能产生了区别，所以这里就不能直接调用市场活动模块的该功能
     *
     * @param request
     * @param response
     */
    private void getUsers(HttpServletRequest request, HttpServletResponse response) {
        //因为是处理用户业务，使用UserService即可
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> users = userService.getList();
        PrintJson.printJsonObj(response, users);
    }

    /**
     * 添加线索记录
     *
     * @param request
     * @param response
     */
    private void save(HttpServletRequest request, HttpServletResponse response) {
        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String company = request.getParameter("company");
        String appellation = request.getParameter("appellation");
        String fullname = request.getParameter("fullname");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String source = request.getParameter("source");
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User) request.getSession().getAttribute("user")).getName();//创建者是当前登录的用户名
        Clue clue = new Clue(id, fullname, appellation, owner, company, job, email, phone, website, mphone, state,
                source, createBy, createTime, null, null, description, contactSummary, nextContactTime,
                address);
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.save(clue);
        PrintJson.printJsonFlag(response, flag);
    }

    /**
     * 进入到线索详情页
     *
     * @param request
     * @param response
     */
    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Clue clue = clueService.detail(id);
        request.setAttribute("clue", clue);
        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request, response);
    }

    /**
     * 获取某条线索关联的市场活动列表
     *
     * @param request
     * @param response
     */
    private void getActivitysByCId(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");

        //因为是获取市场活动列表，应该使用市场活动业务
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activities = activityService.getActivitysByCId(id);
        PrintJson.printJsonObj(response, activities);
    }

    /**
     * 解除当前线索与某个市场活动的关联
     *
     * @param request
     * @param response
     */
    private void unbund(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");

        //解除关联操作可以是线索业务，也可以是市场活动业务，但该操作是从线索模块中发出的请求，接收的控制器必须是线索模块的
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.unbund(id);
        PrintJson.printJsonFlag(response, flag);
    }

    /**
     * 根据市场活动名模糊查询非某条线索关联的市场活动列表
     *
     * @param request
     * @param response
     */
    private void getActivitysNNCI(HttpServletRequest request, HttpServletResponse response) {
        String aname = request.getParameter("aname");
        String cid = request.getParameter("cid");

        //将两个数据封装成map往下传递，因为SQL中需要使用到这两个数据
        Map<String, String> map = new HashMap<>();
        map.put("aname", aname);
        map.put("cid", cid);
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activities = activityService.getActivitysNNCI(map);
        PrintJson.printJsonObj(response, activities);
    }

    /**
     * 关联当前线索与指定的一个或多个市场活动
     *
     * @param request
     * @param response
     */
    private void bund(HttpServletRequest request, HttpServletResponse response) {
        String cid = request.getParameter("cid");

        //传递过来的市场活动id是一个数组，使用数组接收
        String[] aids = request.getParameterValues("aid");
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.bund(cid, aids);
        PrintJson.printJsonFlag(response, flag);
    }

    /**
     * 根据市场活动名模糊查询市场活动列表
     *
     * @param request
     * @param response
     */
    private void getActivitysByName(HttpServletRequest request, HttpServletResponse response) {
        String aname = request.getParameter("aname");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activities = activityService.getActivitysByName(aname);
        PrintJson.printJsonObj(response, activities);
    }

    /**
     * 线索转换
     *
     * @param request
     * @param response
     */
    private void convert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //获取需要转换的线索的id
        String cid = request.getParameter("cid");

        //接收创建交易记录的标记
        String flag = request.getParameter("flag");

        //声明交易表的domain对象
        Tran tran = null;
        String createBy = ((User) request.getSession().getAttribute("user")).getName();

        //如果标记不是null，说明需要创建交易记录，获取交易表单中的参数
        if (null != flag) {
            String money = request.getParameter("money");
            String name = request.getParameter("name");
            String expectedDate = request.getParameter("expectedDate");
            String stage = request.getParameter("stage");
            String activityId = request.getParameter("activityId");
            String id = UUIDUtil.getUUID();
            String createTime = DateTimeUtil.getSysTime();

            //初始化交易表对象
            tran = new Tran();
            tran.setMoney(money);
            tran.setName(name);
            tran.setExpectedDate(expectedDate);
            tran.setStage(stage);
            tran.setActivityId(activityId);
            tran.setId(id);
            tran.setCreateTime(createTime);
            tran.setCreateBy(createBy);
        }

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        /*
        传递线索id给业务层，根据id找到线索并转换；另外传递tran给业务层，如果tran是null，说明不需要创建交易；否则需要创建交易
        另外业务层需要执行添加操作，使用到创建者，而创建者需要使用request从session域中获取，所以需要使用到request
        但传递request给业务层是大材小用，而且该对象规定只能在控制层使用，所以直接生成好创建者再传递即可
        虽然tran里面有创建者，但如果tran是null，不创建交易，则获取不到里面的创建者，所以必须传递创建者
        另外UUID和创建时间都可以使用工具生成，不用传递任何参数；返回布尔值表示转换成功或失败
        */
        boolean f = clueService.convert(cid, tran, createBy);

        //当转换成功，进行响应；前端发送的是传统请求，这里使用重定向响应到线索主页，因为不需要传参，不使用转发；使用动态项目名拼接路径
        if (f) {
            response.sendRedirect(request.getContextPath() + "/workbench/clue/index.jsp");
        }
    }
}