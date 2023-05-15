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
 * �г��ģ��
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
     * ��ȡ�û��б����û���صĲ������г��ģ��ʵ�ֵ�ԭ���������Ǵ��г��ģ�鷢���ģ���Ҫʹ���г��ģ��Ŀ��������ղ���������
     * ������ҵ�����˵�������û���ص�ҵ�����г���޹أ�������Ҫ���õ����û�ģ���ҵ���
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
     * ����г����¼
     *
     * @param request
     * @param response
     */
    private void save(HttpServletRequest request, HttpServletResponse response) {
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        //ʹ��UUID��������һ��32λ���������Ϊid
        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");

        //����ʱ���ǵ�ǰϵͳʱ�䣬ʹ�ù�������
        String createTime = DateTimeUtil.getSysTime();

        //�������ǵ�ǰ��¼���û�����ȡsession���е�User�����name���Լ��ɣ�������Ϊ����Ӽ�¼������û���޸�ʱ����޸���
        String createBy = ((User) request.getSession().getAttribute("user")).getName();

        //��װ���в������ݳ�һ��Activity����
        Activity activity = new Activity(id, owner, name, startDate, endDate, cost, description, createTime, createBy,
                null, null);

        //����ҵ��㷽�������г����¼�����ص����Ƿ���ӳɹ��������ÿ�����ݵ������ݣ�����鷳����Ҫ�ȷ�װ��һ��Activity�����ٴ���
        boolean flag = activityService.save(activity);

        //��Ӧ����
        PrintJson.printJsonFlag(response, flag);
    }

    /**
     * ��ȡ�г���б����������ѯ�ͷ�ҳ��ѯ
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

        //�����Թ��ļ�¼��
        int skipCount = (pageNo - 1) * pageSize;

        //ʹ��map��װ�����������´�����ΪDao��ֻ�ܽ���һ�����ݣ�����Щ���ݲ���ʹ��һ��domain��װ����Ҫ�õ�map
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("owner", owner);
        map.put("startDate", startDate);
        map.put("endDate", endDate);

        //������dao���ѯ���ݿ�ʱ��limit�Ĳ�����ʹ�õ������֣������ַ��������Ա��봫��������������ȥ
        map.put("skipCount", skipCount);
        map.put("pageSize", pageSize);

        /*
        ǰ����Ҫһ���г���б��һ���ܼ�¼������������Ҫ����map��vo��װ���������ݣ�����������ʵ�����Ǻ�Ƶ���ģ�����ʹ��vo��װ
        ��Ϊ�������г��ģ�飬�������ͻ�����ϵ�ˡ����׵�ģ�鶼��Ҫ��ҳ��ѯ��vo����int total��List<T> list��������
        ���������Activity������ģ���޷�ʹ�ø�vo����Ϊ������д���ˣ�ʹ����T�󣬴���vo����ʱ��T����ʲô���ͣ�T�ͱ��ʲô����
        vo������crm�£�����������Ŀ��ȫ��ͨ��
        */
        PaginationVO<Activity> vo = activityService.getActivitys(map);
        PrintJson.printJsonObj(response, vo);
    }

    /**
     * ɾ���г����¼
     *
     * @param request
     * @param response
     */
    private void delete(HttpServletRequest request, HttpServletResponse response) {
        //ǰ�˴���������ͬһ��name���value�����Ե���getParameterValues��ȡ������ʹ��һ���ַ����������
        String[] ids = request.getParameterValues("id");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        //����ҵ��㷽��ɾ���г����¼�����ص����Ƿ�ɾ���ɹ���ע�⴫�ݵ���һ�����飬����dao������Ҫʹ�ö�̬SQL�е�foreach
        boolean flag = activityService.delete(ids);
        PrintJson.printJsonFlag(response, flag);
    }

    /**
     * ��ȡ�û��б���г��������¼
     *
     * @param request
     * @param response
     */
    private void getUsersActivity(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        //��Ҫ����һ���û��б��һ���г������ʹ��map��װ����Ϊ�����������ʽϵ�
        Map<String, Object> map = activityService.getUsersActivity(id);
        PrintJson.printJsonObj(response, map);
    }

    /**
     * �޸��г����¼
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

        //�޸�ʱ���ǵ�ǰϵͳʱ�䣬ʹ�ù�������
        String editTime = DateTimeUtil.getSysTime();

        //�޸����ǵ�ǰ��¼���û�����ȡsession���е�User�����name���Լ��ɣ�������Ϊ���޸ļ�¼������û�д���ʱ��ʹ�����
        String editBy = ((User) request.getSession().getAttribute("user")).getName();

        //��װ���в������ݳ�һ��Activity����
        Activity activity = new Activity(id, owner, name, startDate, endDate, cost, description, null,
                null, editTime, editBy);

        //����ҵ��㷽���޸��г����¼�����ص����Ƿ��޸ĳɹ��������ÿ�����ݵ������ݣ�����鷳����Ҫ�ȷ�װ��һ��Activity�����ٴ���
        boolean flag = activityService.update(activity);

        //��Ӧ����
        PrintJson.printJsonFlag(response, flag);
    }

    /**
     * �����г������ҳ
     *
     * @param request
     * @param response
     */
    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        //�ײ����id�鵥�����᷵��һ����װ�õ�Activity����
        Activity activity = activityService.detail(id);

        //��Activity��������������
        request.setAttribute("activity", activity);

        //����ת����·�����ô���/��Ŀ����
        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request, response);
    }

    /**
     * �����г��id��ȡ�г����ע�б�
     *
     * @param request
     * @param response
     */
    private void getRemarksByAId(HttpServletRequest request, HttpServletResponse response) {
        String aid = request.getParameter("aid");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        //����һ����ע��Ϣ�б��ϣ��洢����ƥ��ı�ע��Ϣ
        List<ActivityRemark> activityRemarks = activityService.getRemarksByAId(aid);
        PrintJson.printJsonObj(response, activityRemarks);
    }

    /**
     * ɾ���г����ע
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
     * ����г����ע
     *
     * @param request
     * @param response
     */
    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {
        String noteContent = request.getParameter("noteContent");
        String aid = request.getParameter("aid");

        //���ɱ�ע��Ϣ��ID������ʱ�䡢�����ߺ��Ƿ��޸Ĺ����������ǵ�ǰ��¼���û�����ȡsession���е�User�����name���Լ���
        String id = UUIDUtil.getUUID();
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User) request.getSession().getAttribute("user")).getName();
        String editFlag = "0";//��ʾδ���޸Ĺ�

        //��װ������Ϣ����ע�����д��ݵ��ײ���ӣ����ز���ֵ��ʾ�ɹ���ʧ�ܣ�������ֵ�ͱ�ע�����������ݵķ�װ�ڿ��Ʋ����
        ActivityRemark activityRemark = new ActivityRemark(id, noteContent, createTime, createBy, null,
                null, editFlag, aid);
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.saveRemark(activityRemark);

        //ʹ��map��װ���ݼ��ɣ���Ϊ���ݵ�ʹ���ʲ���
        Map<String, Object> map = new HashMap<>();
        map.put("success", flag);
        map.put("r", activityRemark);
        PrintJson.printJsonObj(response, map);
    }

    /**
     * �޸��г����ע
     *
     * @param request
     * @param response
     */
    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");

        //�����޸�ʱ�䡢�޸��ߺ��Ƿ��޸Ĺ�
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User) request.getSession().getAttribute("user")).getName();
        String editFlag = "1";//��ʾ���޸Ĺ�

        //��װ������Ϣ����ע�����д��ݵ��ײ��޸ģ����ز���ֵ��ʾ�ɹ���ʧ�ܣ�������ֵ�ͱ�ע�����������ݵķ�װ�ڿ��Ʋ����
        ActivityRemark activityRemark = new ActivityRemark(id, noteContent, null, null, editTime,
                editBy, editFlag, null);
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.updateRemark(activityRemark);

        //ʹ��map��װ���ݼ��ɣ���Ϊ���ݵ�ʹ���ʲ���
        Map<String, Object> map = new HashMap<>();
        map.put("success", flag);
        map.put("r", activityRemark);
        PrintJson.printJsonObj(response, map);
    }
}