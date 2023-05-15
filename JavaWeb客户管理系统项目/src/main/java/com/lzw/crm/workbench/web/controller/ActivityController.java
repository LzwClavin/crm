package com.lzw.crm.workbench.web.controller;

import com.lzw.crm.settings.domain.*;
import com.lzw.crm.settings.service.*;
import com.lzw.crm.settings.service.impl.*;
import com.lzw.crm.utils.*;
import com.lzw.crm.vo.*;
import com.lzw.crm.workbench.domain.*;
import com.lzw.crm.workbench.service.*;
import com.lzw.crm.workbench.service.impl.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

/**
 * 市场活动模块
 */
@WebServlet({"/workbench/activity/getUsers.do", "/workbench/activity/save.do", "/workbench/activity/getActivitys.do",
        "/workbench/activity/delete.do", "/workbench/activity/getUsersActivity.do", "/workbench/activity/update.do",
        "/workbench/activity/detail.do", "/workbench/activity/getRemarksByAId.do", "/workbench/activity/deleteRemark.do",
        "/workbench/activity/saveRemark.do", "/workbench/activity/updateRemark.do"})
public class ActivityController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/workbench/activity/getUsers.do":
                getUsers(request, response);
                break;
            case "/workbench/activity/save.do":
                save(request, response);
                break;
            case "/workbench/activity/getActivitys.do":
                getActivitys(request, response);
                break;
            case "/workbench/activity/delete.do":
                delete(request, response);
                break;
            case "/workbench/activity/getUsersActivity.do":
                getUsersActivity(request, response);
                break;
            case "/workbench/activity/update.do":
                update(request, response);
                break;
            case "/workbench/activity/detail.do":
                detail(request, response);
                break;
            case "/workbench/activity/getRemarksByAId.do":
                getRemarksByAId(request, response);
                break;
            case "/workbench/activity/deleteRemark.do":
                deleteRemark(request, response);
                break;
            case "/workbench/activity/saveRemark.do":
                saveRemark(request, response);
                break;
            case "/workbench/activity/updateRemark.do":
                updateRemark(request, response);
                break;
        }
    }

    /**
     * 获取用户列表，与用户相关的操作在市场活动模块实现的原因是请求是从市场活动模块发出的，需要使用市场活动模块的控制器接收并处理请求
     * 但对于业务层来说，这是用户相关的业务，与市场活动无关，所以需要调用的是用户模块的业务层
     *
     * @param request
     * @param response
     */
    private void getUsers(HttpServletRequest request, HttpServletResponse response) {
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> users = userService.getList();
        PrintJson.printJsonObj(response, users);
    }

    /**
     * 添加市场活动记录
     *
     * @param request
     * @param response
     */
    private void save(HttpServletRequest request, HttpServletResponse response) {
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        //使用UUID工具生成一个32位的随机串作为id
        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");

        //创建时间是当前系统时间，使用工具生成
        String createTime = DateTimeUtil.getSysTime();

        //创建者是当前登录的用户，获取session域中的User对象的name属性即可；另外因为是添加记录，所以没有修改时间和修改者
        String createBy = ((User) request.getSession().getAttribute("user")).getName();

        //封装所有参数数据成一个Activity对象
        Activity activity = new Activity(id, owner, name, startDate, endDate, cost, description, createTime, createBy,
                null, null);

        //调用业务层方法保存市场活动记录，返回的是是否添加成功；如果将每个数据单独传递，会很麻烦，需要先封装成一个Activity对象再传递
        boolean flag = activityService.save(activity);

        //响应数据
        PrintJson.printJsonFlag(response, flag);
    }

    /**
     * 获取市场活动列表，结合条件查询和分页查询
     *
     * @param request
     * @param response
     */
    private void getActivitys(HttpServletRequest request, HttpServletResponse response) {
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String pageNos = request.getParameter("pageNo");
        String pageSizes = request.getParameter("pageSize");
        int pageNo = Integer.parseInt(pageNos);
        int pageSize = Integer.parseInt(pageSizes);

        //计算略过的记录数
        int skipCount = (pageNo - 1) * pageSize;

        //使用map封装所有数据往下传，因为Dao层只能接收一个数据，且这些数据不能使用一个domain封装，需要用到map
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("owner", owner);
        map.put("startDate", startDate);
        map.put("endDate", endDate);

        //由于在dao层查询数据库时，limit的参数中使用的是数字，不是字符串，所以必须传递数字型数据下去
        map.put("skipCount", skipCount);
        map.put("pageSize", pageSize);

        /*
        前端需要一个市场活动列表和一个总记录条数，所以需要返回map或vo封装这两个数据，而该需求在实际中是很频繁的，所以使用vo封装
        因为不仅是市场活动模块，线索、客户、联系人、交易等模块都需要分页查询；vo中有int total和List<T> list两个属性
        如果泛型是Activity，其它模块无法使用该vo，因为将泛型写死了；使用了T后，创建vo对象时对T传递什么类型，T就变成什么类型
        vo创建在crm下，属于整个项目，全局通用
        */
        PaginationVO<Activity> vo = activityService.getActivitys(map);
        PrintJson.printJsonObj(response, vo);
    }

    /**
     * 删除市场活动记录
     *
     * @param request
     * @param response
     */
    private void delete(HttpServletRequest request, HttpServletResponse response) {
        //前端传过来的是同一个name多个value，所以调用getParameterValues获取参数，使用一个字符串数组接收
        String[] ids = request.getParameterValues("id");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        //调用业务层方法删除市场活动记录，返回的是是否删除成功，注意传递的是一个数组，所以dao层中需要使用动态SQL中的foreach
        boolean flag = activityService.delete(ids);
        PrintJson.printJsonFlag(response, flag);
    }

    /**
     * 获取用户列表和市场活动单条记录
     *
     * @param request
     * @param response
     */
    private void getUsersActivity(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        //需要返回一个用户列表和一个市场活动对象，使用map封装，因为这种需求复用率较低
        Map<String, Object> map = activityService.getUsersActivity(id);
        PrintJson.printJsonObj(response, map);
    }

    /**
     * 修改市场活动记录
     *
     * @param request
     * @param response
     */
    private void update(HttpServletRequest request, HttpServletResponse response) {
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");

        //修改时间是当前系统时间，使用工具生成
        String editTime = DateTimeUtil.getSysTime();

        //修改者是当前登录的用户，获取session域中的User对象的name属性即可；另外因为是修改记录，所以没有创建时间和创建者
        String editBy = ((User) request.getSession().getAttribute("user")).getName();

        //封装所有参数数据成一个Activity对象
        Activity activity = new Activity(id, owner, name, startDate, endDate, cost, description, null,
                null, editTime, editBy);

        //调用业务层方法修改市场活动记录，返回的是是否修改成功；如果将每个数据单独传递，会很麻烦，需要先封装成一个Activity对象再传递
        boolean flag = activityService.update(activity);

        //响应数据
        PrintJson.printJsonFlag(response, flag);
    }

    /**
     * 进入市场活动详情页
     *
     * @param request
     * @param response
     */
    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        //底层根据id查单条，会返回一个封装好的Activity对象
        Activity activity = activityService.detail(id);

        //将Activity对象存进请求域中
        request.setAttribute("activity", activity);

        //请求转发，路径不用带“/项目名”
        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request, response);
    }

    /**
     * 根据市场活动id获取市场活动备注列表
     *
     * @param request
     * @param response
     */
    private void getRemarksByAId(HttpServletRequest request, HttpServletResponse response) {
        String aid = request.getParameter("aid");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        //返回一个备注信息列表集合，存储所有匹配的备注信息
        List<ActivityRemark> activityRemarks = activityService.getRemarksByAId(aid);
        PrintJson.printJsonObj(response, activityRemarks);
    }

    /**
     * 删除市场活动备注
     *
     * @param request
     * @param response
     */
    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.deleteRemark(id);
        PrintJson.printJsonFlag(response, flag);
    }

    /**
     * 添加市场活动备注
     *
     * @param request
     * @param response
     */
    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {
        String noteContent = request.getParameter("noteContent");
        String aid = request.getParameter("aid");

        //生成备注信息的ID、创建时间、创建者和是否被修改过，创建者是当前登录的用户，获取session域中的User对象的name属性即可
        String id = UUIDUtil.getUUID();
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User) request.getSession().getAttribute("user")).getName();
        String editFlag = "0";//表示未被修改过

        //封装所有信息到备注对象中传递到底层添加，返回布尔值表示成功或失败，而布尔值和备注对象两个数据的封装在控制层完成
        ActivityRemark activityRemark = new ActivityRemark(id, noteContent, createTime, createBy, null,
                null, editFlag, aid);
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.saveRemark(activityRemark);

        //使用map封装数据即可，因为数据的使用率不高
        Map<String, Object> map = new HashMap<>();
        map.put("success", flag);
        map.put("r", activityRemark);
        PrintJson.printJsonObj(response, map);
    }

    /**
     * 修改市场活动备注
     *
     * @param request
     * @param response
     */
    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");

        //生成修改时间、修改者和是否被修改过
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User) request.getSession().getAttribute("user")).getName();
        String editFlag = "1";//表示被修改过

        //封装所有信息到备注对象中传递到底层修改，返回布尔值表示成功或失败，而布尔值和备注对象两个数据的封装在控制层完成
        ActivityRemark activityRemark = new ActivityRemark(id, noteContent, null, null, editTime,
                editBy, editFlag, null);
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.updateRemark(activityRemark);

        //使用map封装数据即可，因为数据的使用率不高
        Map<String, Object> map = new HashMap<>();
        map.put("success", flag);
        map.put("r", activityRemark);
        PrintJson.printJsonObj(response, map);
    }
}