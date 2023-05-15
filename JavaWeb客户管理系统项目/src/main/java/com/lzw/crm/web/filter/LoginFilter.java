package com.lzw.crm.web.filter;

import com.lzw.crm.settings.domain.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

/**
 * 登录验证拦截器
 */
public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException,
            ServletException {
        /*
        因为前端发送请求后到过滤器时该请求对象肯定是HttpServletRequest对象，所以可以将参数的ServletRequest对象强转成该对象
        同理可以声明一个HttpServletResponse对象将参数的ServletResponse对象强转成该对象
        */
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        //获取session域对象
        HttpSession session = request.getSession();

        //获取session域中的User对象
        User user = (User) session.getAttribute("user");

        //获取Servlet或jsp路径
        String path = request.getServletPath();

        /*
        如果User对象存在，说明登录过，放行，否则重定向到登录页；当重启浏览器后，session中的User对象被销毁，无法直接访问资源
        这里使用重定向而不使用转发是因为，转发后地址栏的路径还是原路径，不是跳转到新资源登录页的路径
        如果跳转后登录页加载慢，可以点击刷新按钮重新加载页面
        */
        if (null != user || "/login.jsp".equals(path) || "/settings/user/login.do".equals(path)) {
            chain.doFilter(request, response);
        } else {
            //重定向需要带“/项目名”，这里获取动态项目名
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }
}