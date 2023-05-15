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
     * ��ȡ�û��б���ת���������ҳ
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
     * ��ȡ�ͻ����б��Զ���ȫ
     *
     * @param request
     * @param response
     */
    private void getCustomerNames(HttpServletRequest request, HttpServletResponse response) {
        //��ȡǰ���ύ�Ĳ�ѯ�ؼ���
        String name = request.getParameter("name");

        //�����ǲ�ѯ�ͻ��б�������Ҫ���ÿͻ�ҵ��
        CustomerService customerService = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        //���ؿͻ����б�
        List<String> names = customerService.getCustomerNames(name);

        //���ڴ��ݵ���һ���ַ������ϣ����Բ�����ת����JSON��ֱ����Ӧ����
        PrintJson.printJsonObj(response, names);
    }

    /**
     * �������׼�¼
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
        String customerName = request.getParameter("customerName");//û�пͻ�id����ȡ�ͻ���
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

        //�������׶����װ���ݣ�����װ�ͻ���
        Tran tran = new Tran(id, owner, money, name, expectedDate, null, stage, type, source, activityId,
                contactsId, createBy, createTime, null, null, description, contactSummary, nextContactTime);
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());

        /*
        ���ݿͻ�����ҵ��㴦����ȡ��Ӧ�Ŀͻ�id��ҵ�����봴�����׼�¼�ͽ�����ʷ��¼���п�����Ҫ�����ͻ���¼
        ��Ҫʹ�ö��id�������ߺʹ���ʱ�䣬�����ﲻ��Ҫ������Щ������ȥ����Ϊid�Զ����ɡ�����ʱ��ʹ����߶�������tran�л�ȡ
        */
        boolean flag = tranService.save(tran, customerName);

        /*
        �����ӳɹ����ض��򵽽�����ҳ��ҳ���еĽ����б�ʹ��Ajax�ֲ����ɣ���Ϊ�漰���˶ദ��Ҫ�ֲ�ˢ�£����Բ�ʹ��ת��
        �������ʹ��ת������ַ����·�����ǵ�ǰServlet��ÿ��ˢ�¶����ظ���ӽ��׼�¼���ض����·����Ŀ��jsp·�����������ظ���ӵ�����
        */
        if (flag) {
            response.sendRedirect(request.getContextPath() + "/workbench/transaction/index.jsp");
        }
    }

    /**
     * ���ݽ���id��ȡ���׼�¼����ת����������ҳ
     *
     * @param request
     * @param response
     */
    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        Tran tran = tranService.detail(id);

        //��ȡ�׶�
        String stage = tran.getStage();

        //��ȡӦ�������
        ServletContext application = this.getServletContext();

        //��ȡӦ�����е�pMap�����׶κͿ����Ե�ӳ���ϵ
        Map<String, String> pMap = (Map<String, String>) application.getAttribute("pMap");

        //��ȡ������
        String possibility = pMap.get(stage);

        //����tran����Ŀ��������ԣ�Ȼ����Ա��浽�������У�������ҳʹ��
        tran.setPossibility(possibility);
        request.setAttribute("tran", tran);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request, response);
    }

    /**
     * ���ݽ���id��ȡ������ʷ�б�
     *
     * @param request
     * @param response
     */
    private void getHistorysByTId(HttpServletRequest request, HttpServletResponse response) {
        String tranId = request.getParameter("tranId");
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<TranHistory> histories = tranService.getHistorysByTId(tranId);

        //��ȡӦ�������
        ServletContext application = this.getServletContext();

        //��ȡӦ�����е�pMap�����׶κͿ����Ե�ӳ���ϵ�����������ѭ�����ⲿ��û��Ҫÿ��ѭ������ȡһ��
        Map<String, String> pMap = (Map<String, String>) application.getAttribute("pMap");

        //����������ʷ�б�
        for (TranHistory history : histories) {
            //��ȡÿ��������ʷ�����еĽ׶�
            String stage = history.getStage();

            //��ȡ������
            String possibility = pMap.get(stage);

            //����history����Ŀ��������ԣ�Ȼ�������Ӧ��ǰ�ˣ�������ҳʹ��
            history.setPossibility(possibility);
        }

        PrintJson.printJsonObj(response, histories);
    }

    /**
     * �޸Ľ��׽׶κͽ��׽׶�ͼ��
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

        //���������������װ��һ�����׶������´��ݣ������Ƿ����ɹ�
        boolean flag = tranService.changeStage(tran);

        //��ȡӦ�������
        ServletContext application = this.getServletContext();

        //��ȡӦ�����е�pMap�����׶κͿ����Ե�ӳ���ϵ
        Map<String, String> pMap = (Map<String, String>) application.getAttribute("pMap");

        //��ȡ������
        String possibility = pMap.get(stage);

        //����tran����Ŀ��������ԣ�Ȼ�������Ӧ��ǰ�ˣ�������ҳʹ��
        tran.setPossibility(possibility);

        //��������Ǻͽ��׶����װ��һ��map�в�ת��JSON����Ӧ��ǰ��
        Map<String, Object> map = new HashMap<>();
        map.put("success", flag);
        map.put("tran", tran);
        PrintJson.printJsonObj(response, map);
    }

    /**
     * ��ȡ���׽׶�����ͳ��ͼ��Ҫ������
     *
     * @param request
     * @param response
     */
    private void getCharts(HttpServletRequest request, HttpServletResponse response) {
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());

        /*
        ǰ����Ҫtotal��ͳ�����ݣ�����ʹ��map��װ�����ͳ��ͼ�϶࣬��ͳ��ͼ�ж���Ҫʹ�õ�name��value��ʹ��vo����
        ���⣬����Ҳ����ʹ�÷�ҳ����з�װ��vo����total�ͷ����б������ڷ���Ҫ���map��������鷳�����Բ�ʹ��
        */
        Map<String, Object> map = tranService.getCharts();
        PrintJson.printJsonObj(response, map);
    }
}