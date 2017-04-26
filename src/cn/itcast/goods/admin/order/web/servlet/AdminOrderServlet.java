package cn.itcast.goods.admin.order.web.servlet;

import cn.itcast.goods.book.pager.PageBean;
import cn.itcast.goods.order.domain.Order;
import cn.itcast.goods.order.service.OrderService;
import cn.itcast.goods.order.web.servlet.OrderServlet;
import cn.itcast.servlet.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by znho_0 on 2017/3/24.
 */
public class AdminOrderServlet extends BaseServlet{
    private OrderService orderService = new OrderService();


    /**
     * 根据订单状态查询
     */
    public String findByStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        //得到pc
        int pc = getPc(request);
        //得到url
        String url = getUrl(request);
        //得到状态
        String status = request.getParameter("status");
        PageBean<Order> pageBean = orderService.findByStatus(status,pc);
        pageBean.setUrl(url);
        request.setAttribute("pageBean",pageBean);
        return "f:/adminjsps/admin/order/list.jsp";
    }

    /**
     * 取消订单
     */
    public String cancel(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String oid = request.getParameter("oid");
        boolean status = orderService.isStatus(oid,1);
        if(!status){
            request.setAttribute("msg","取消失败，当前状态错误");
            return "f:/adminjsps/msg.jsp";
        }
        orderService.updateStatus(oid,5);
        request.setAttribute("msg","取消成功");
        return "f:/adminjsps/msg.jsp";

    }

    /**
     * 取消订单
     */
    public String sendout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String oid = request.getParameter("oid");
        boolean status = orderService.isStatus(oid,2);
        if(!status){
            request.setAttribute("msg","发货失败，当前状态错误");
            return "f:/adminjsps/msg.jsp";
        }
        orderService.updateStatus(oid,3);
        request.setAttribute("msg","发货成功");
        return "f:/adminjsps/msg.jsp";

    }

    /**
     * 查看订单详情
     */
    public String desc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        //1.得到oid
        String oid = request.getParameter("oid");
        //2.得到btn  0:都不显示 1:发货 2：取消订单
        int btn = getBtn(request);

        //3.使用Service查询
        Order order = orderService.findByOid(oid);

        //4.保存信息
        request.setAttribute("order",order);
        request.setAttribute("btn",btn);

        return "f:/adminjsps/admin/order/desc.jsp";


    }

    private int getBtn(HttpServletRequest request) {
        String value = request.getParameter("btn");
        int btn = 0;
        if(value != null && !value.isEmpty()){
            btn = Integer.parseInt(value);
        }
        return btn;
    }


    /**
     *  查询所有人的订单
     */
    public String findAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        //得到pc
        int pc = getPc(request);
        //得到url
        String url = getUrl(request);

        PageBean<Order> pageBean = orderService.findAll(pc);
        pageBean.setUrl(url);
        request.setAttribute("pageBean",pageBean);

        return  "f:/adminjsps/admin/order/list.jsp";
    }
    //得到没有pc的url
    //localhost:8080/Servlet?method=xxx&pc=x
    private String getUrl(HttpServletRequest request) {
        String url = request.getRequestURI() + "?" + request.getQueryString();

        String[] value = url.split("&pc=");
        url = value[0];

        return url;
    }


    //得到pc
    //localhost:8080/Servlet?method=xxx&pc=x
    private int getPc(HttpServletRequest request) {
        String pcString = request.getParameter("pc");
        int pc = 1;
        if(pcString ==null || pcString.isEmpty()){
            pc = 1;
        }else {
            pc = Integer.parseInt(pcString);
        }
        return pc;

    }


}
