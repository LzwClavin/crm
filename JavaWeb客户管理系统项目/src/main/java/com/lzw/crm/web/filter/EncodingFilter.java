package com.lzw.crm.web.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * 字符编码过滤器
 */
public class EncodingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        //过滤post请求中文参数乱码的问题
        request.setCharacterEncoding("UTF-8");

        //过滤响应流响应中文乱码的问题
        response.setContentType("text/html;charset=UTF-8");

        //放行请求，跳转到目标Servlet
        chain.doFilter(request, response);
    }
}