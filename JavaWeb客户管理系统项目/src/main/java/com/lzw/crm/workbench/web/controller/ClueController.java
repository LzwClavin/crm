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
 * ����ģ��
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
     * ��ȡ�û��б���Ҫ�����г��ģ��ĸù��ܣ���Ϊ�������Ǵ�����ģ�鷢���ģ�Ӧ��������ģ��Ŀ���������
     * ��������Ժ�����ģ��ĸù��ܽ�����չ����ʱ���г��ģ��ĸù��ܲ�����������������Ͳ���ֱ�ӵ����г��ģ��ĸù���
     *
     * @param request
     * @param response
     */
    private void getUsers(HttpServletRequest request, HttpServletResponse response) {
        //��Ϊ�Ǵ����û�ҵ��ʹ��UserService����
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> users = userService.getList();
        PrintJson.printJsonObj(response, users);
    }

    /**
     * ���������¼
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
        String createBy = ((User) request.getSession().getAttribute("user")).getName();//�������ǵ�ǰ��¼���û���
        Clue clue = new Clue(id, fullname, appellation, owner, company, job, email, phone, website, mphone, state,
                source, createBy, createTime, null, null, description, contactSummary, nextContactTime,
                address);
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.save(clue);
        PrintJson.printJsonFlag(response, flag);
    }

    /**
     * ���뵽��������ҳ
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
     * ��ȡĳ�������������г���б�
     *
     * @param request
     * @param response
     */
    private void getActivitysByCId(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");

        //��Ϊ�ǻ�ȡ�г���б�Ӧ��ʹ���г��ҵ��
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activities = activityService.getActivitysByCId(id);
        PrintJson.printJsonObj(response, activities);
    }

    /**
     * �����ǰ������ĳ���г���Ĺ���
     *
     * @param request
     * @param response
     */
    private void unbund(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");

        //���������������������ҵ��Ҳ�������г��ҵ�񣬵��ò����Ǵ�����ģ���з��������󣬽��յĿ���������������ģ���
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.unbund(id);
        PrintJson.printJsonFlag(response, flag);
    }

    /**
     * �����г����ģ����ѯ��ĳ�������������г���б�
     *
     * @param request
     * @param response
     */
    private void getActivitysNNCI(HttpServletRequest request, HttpServletResponse response) {
        String aname = request.getParameter("aname");
        String cid = request.getParameter("cid");

        //���������ݷ�װ��map���´��ݣ���ΪSQL����Ҫʹ�õ�����������
        Map<String, String> map = new HashMap<>();
        map.put("aname", aname);
        map.put("cid", cid);
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activities = activityService.getActivitysNNCI(map);
        PrintJson.printJsonObj(response, activities);
    }

    /**
     * ������ǰ������ָ����һ�������г��
     *
     * @param request
     * @param response
     */
    private void bund(HttpServletRequest request, HttpServletResponse response) {
        String cid = request.getParameter("cid");

        //���ݹ������г��id��һ�����飬ʹ���������
        String[] aids = request.getParameterValues("aid");
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.bund(cid, aids);
        PrintJson.printJsonFlag(response, flag);
    }

    /**
     * �����г����ģ����ѯ�г���б�
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
     * ����ת��
     *
     * @param request
     * @param response
     */
    private void convert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //��ȡ��Ҫת����������id
        String cid = request.getParameter("cid");

        //���մ������׼�¼�ı��
        String flag = request.getParameter("flag");

        //�������ױ��domain����
        Tran tran = null;
        String createBy = ((User) request.getSession().getAttribute("user")).getName();

        //�����ǲ���null��˵����Ҫ�������׼�¼����ȡ���ױ��еĲ���
        if (null != flag) {
            String money = request.getParameter("money");
            String name = request.getParameter("name");
            String expectedDate = request.getParameter("expectedDate");
            String stage = request.getParameter("stage");
            String activityId = request.getParameter("activityId");
            String id = UUIDUtil.getUUID();
            String createTime = DateTimeUtil.getSysTime();

            //��ʼ�����ױ����
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
        ��������id��ҵ��㣬����id�ҵ�������ת�������⴫��tran��ҵ��㣬���tran��null��˵������Ҫ�������ף�������Ҫ��������
        ����ҵ�����Ҫִ����Ӳ�����ʹ�õ������ߣ�����������Ҫʹ��request��session���л�ȡ��������Ҫʹ�õ�request
        ������request��ҵ����Ǵ��С�ã����Ҹö���涨ֻ���ڿ��Ʋ�ʹ�ã�����ֱ�����ɺô������ٴ��ݼ���
        ��Ȼtran�����д����ߣ������tran��null�����������ף����ȡ��������Ĵ����ߣ����Ա��봫�ݴ�����
        ����UUID�ʹ���ʱ�䶼����ʹ�ù������ɣ����ô����κβ��������ز���ֵ��ʾת���ɹ���ʧ��
        */
        boolean f = clueService.convert(cid, tran, createBy);

        //��ת���ɹ���������Ӧ��ǰ�˷��͵��Ǵ�ͳ��������ʹ���ض�����Ӧ��������ҳ����Ϊ����Ҫ���Σ���ʹ��ת����ʹ�ö�̬��Ŀ��ƴ��·��
        if (f) {
            response.sendRedirect(request.getContextPath() + "/workbench/clue/index.jsp");
        }
    }
}