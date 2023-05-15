package com.lzw.crm.web.filter;

import com.lzw.crm.settings.domain.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

/**
 * ��¼��֤������
 */
public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException,
            ServletException {
        /*
        ��Ϊǰ�˷�������󵽹�����ʱ���������϶���HttpServletRequest�������Կ��Խ�������ServletRequest����ǿת�ɸö���
        ͬ���������һ��HttpServletResponse���󽫲�����ServletResponse����ǿת�ɸö���
        */
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        //��ȡsession�����
        HttpSession session = request.getSession();

        //��ȡsession���е�User����
        User user = (User) session.getAttribute("user");

        //��ȡServlet��jsp·��
        String path = request.getServletPath();

        /*
        ���User������ڣ�˵����¼�������У������ض��򵽵�¼ҳ���������������session�е�User�������٣��޷�ֱ�ӷ�����Դ
        ����ʹ���ض������ʹ��ת������Ϊ��ת�����ַ����·������ԭ·����������ת������Դ��¼ҳ��·��
        �����ת���¼ҳ�����������Ե��ˢ�°�ť���¼���ҳ��
        */
        if (null != user || "/login.jsp".equals(path) || "/settings/user/login.do".equals(path)) {
            chain.doFilter(request, response);
        } else {
            //�ض�����Ҫ����/��Ŀ�����������ȡ��̬��Ŀ��
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }
}