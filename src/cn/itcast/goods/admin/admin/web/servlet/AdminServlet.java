package cn.itcast.goods.admin.admin.web.servlet;

import cn.itcast.goods.admin.admin.domain.Admin;
import cn.itcast.goods.admin.admin.service.AdminService;
import cn.itcast.servlet.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by znho_0 on 2017/3/20.
 */
public class AdminServlet extends BaseServlet{
    AdminService adminService = new AdminService();



    /**
     * 后台登录模块
     */
    public String login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String adminname = request.getParameter("adminname");
        String adminpwd = request.getParameter("adminpwd");

        Admin admin = adminService.login(adminname,adminpwd);
        if(admin == null){
            request.setAttribute("msg","账号或密码错误");
            return "f:/adminjsps/login.jsp";
        }else{

            request.getSession().setAttribute("admin",admin);
            return "r:/adminjsps/admin/index.jsp";
        }

    }
}
