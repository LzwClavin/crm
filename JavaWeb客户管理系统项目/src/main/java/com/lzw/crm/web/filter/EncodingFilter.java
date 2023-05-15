package com.lzw.crm.web.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * �ַ����������
 */
public class EncodingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        //����post�������Ĳ������������
        request.setCharacterEncoding("UTF-8");

        //������Ӧ����Ӧ�������������
        response.setContentType("text/html;charset=UTF-8");

        //����������ת��Ŀ��Servlet
        chain.doFilter(request, response);
    }
}