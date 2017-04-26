package cn.itcast.goods.Filter;

import cn.itcast.goods.user.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by znho_0 on 2017/3/20.
 */
public class MyFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        Object user = (User) request.getSession().getAttribute("sessionUser");

        if(user == null){
            request.setAttribute("code","error");
            request.setAttribute("msg","您还未登陆");
            request.getRequestDispatcher("/jsps/msg.jsp").forward(request,resp);
        }else{

            chain.doFilter(req, resp);
        }
    }

    @Override
    public void destroy() {

    }


}
