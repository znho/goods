package cn.itcast.goods.admin.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by znho_0 on 2017/3/24.
 */
public class AdminLoginFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        Object admin = request.getSession().getAttribute("admin");
        if (admin == null ){
            request.setAttribute("msg","您还没登陆");
            request.getRequestDispatcher("/adminjsps/login.jsp").forward(req,resp);
        }else {

            chain.doFilter(req, resp);
        }
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
