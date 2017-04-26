package cn.itcast.goods.order.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.book.pager.PageBean;
import cn.itcast.goods.cart.domain.CartItem;
import cn.itcast.goods.cart.service.CartItemService;
import cn.itcast.goods.order.domain.Order;
import cn.itcast.goods.order.domain.OrderItem;
import cn.itcast.goods.order.service.OrderService;
import cn.itcast.goods.order.service.PaymentUtil;
import cn.itcast.goods.user.domain.User;
import cn.itcast.servlet.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by znho_0 on 2017/3/18.
 */
public class OrderServlet extends BaseServlet{
    OrderService orderService = new OrderService();
    CartItemService cartItemService = new CartItemService();

    /**
     * 支付成功后回馈页面
     * 易宝支付成功的时候，会访问这里
     * 两种方式访问（一起的）
     * 1.引导用户重定向到这里（如果关闭了浏览器就不能重定向了）
     * 2.易宝的服务器点对点的方式访问这个方法（我们必须回馈success，不然易宝服务器会一直调用这个方法一天）
     */
    public String back(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        System.out.println("回来访问了");
        //得到传过来的12个参数,必须得到这12个
        String p1_MerId = request.getParameter("p1_MerId");//商户在易宝支付系统的唯一身份标识.
        String r0_Cmd = request.getParameter("r0_Cmd");//业务类型,固定值Buy
        String r1_Code = request.getParameter("r1_Code");//支付结果,1表示成功
        String r2_TrxId = request.getParameter("r2_TrxId");//易宝支付交易流水号
        String r3_Amt = request.getParameter("r3_Amt");//支付金额
        String r4_Cur = request.getParameter("r4_Cur");//交易币种
        String r5_Pid = request.getParameter("r5_Pid");//商品名称
        String r6_Order = request.getParameter("r6_Order");//商品名称
        String r7_Uid = request.getParameter("r7_Uid");// 易宝支付会员ID
        String r8_MP = request.getParameter("r8_MP");//商户扩展信息
        String r9_BType = request.getParameter("r9_BType");//交易结果返回类型
        String hmac = request.getParameter("hmac");//签名数据

        //使用这11个参数加上key生成一个hmac，对比两个是否一样,返回一个布尔值
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("payment.properties"));
        String keyValue = properties.getProperty("keyValue");
        System.out.println("keyValue:"+keyValue);
        boolean bool = PaymentUtil.verifyCallback(hmac,p1_MerId,r0_Cmd,r1_Code,r2_TrxId,r3_Amt,r4_Cur,r5_Pid,r6_Order,r7_Uid,r8_MP,r9_BType,keyValue);
        //如果不一样
        System.out.println("检验布尔值");
        if(!bool){
            request.setAttribute("code","orror");
            request.setAttribute("msg","哥们，你问题大！重新支付吧");
            return "f:/jsps/msg.jsp";
        }

        System.out.println("是否成功"+bool);
        if(r1_Code.equals("1")){
            //改变状态为已支付
            System.out.println("开始改变状态");
            orderService.updateStatus(r6_Order,2);
            //1. 返回1为重定向过来的
            if(r9_BType.equals("1")){
                request.setAttribute("code","success");
                request.setAttribute("msg","支付成功");
                return "f:/jsps/msg.jsp";
            }else if(r9_BType.equals("2")){//2为服务器访问我们的方法，我们返回给他成功信息，否则他会一直访问
                response.getWriter().print("success");
            }
        }



        return null;
    }


    /**
     * 支付模块
     */
    public String payment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        //得到配置文件
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("payment.properties"));
        //1.准备13个参数
        String p0_Cmd = "Buy";//业务类型，固定Buy，大小写区分的！！
        String p1_MerId = properties.getProperty("p1_MerId");//商号编码，在易宝的唯一编号
        String p2_Order = request.getParameter("oid");//订单Id
        String p3_Amt = "0.01";//支付金额
        String p4_Cur = "CNY";//币种：人民币
        String p5_Pid = "";//商品名称
        String p6_Pcat = "";//商品种类.
        String p7_Pdesc = "";//商品描述
        String p8_Url = properties.getProperty("p8_Url");//支付成功后易宝支付会向该地址发送两次成功通知，该地址可以带参数
        String p9_SAF = "";//送货地址
        String pa_MP = "";//商户扩展信息
        String pd_FrpId = request.getParameter("yh");// 支付通道编码 ,哪个银行或支付宝
        String pr_NeedResponse = "1";//应答机制,固定值1

        //2.hmac要13个参数加上密钥一起生成
        String keyValue = properties.getProperty("keyValue");
        String hmac = PaymentUtil.buildHmac(p0_Cmd,p1_MerId,p2_Order,p3_Amt,p4_Cur,p5_Pid,p6_Pcat,
                p7_Pdesc,p8_Url,p9_SAF,pa_MP,pd_FrpId,pr_NeedResponse,keyValue);

        //保存信息
        StringBuilder value = new StringBuilder("https://www.yeepay.com/app-merchant-proxy/node");
        value.append("?").append("p0_Cmd=").append(p0_Cmd);
        value.append("&").append("p1_MerId=").append(p1_MerId);
        value.append("&").append("p2_Order=").append(p2_Order);
        value.append("&").append("p3_Amt=").append(p3_Amt);
        value.append("&").append("p4_Cur=").append(p4_Cur);
        value.append("&").append("p5_Pid=").append(p5_Pid);
        value.append("&").append("p6_Pcat=").append(p6_Pcat);
        value.append("&").append("p7_Pdesc=").append(p7_Pdesc);
        value.append("&").append("p8_Url=").append(p8_Url);
        value.append("&").append("p9_SAF=").append(p9_SAF);
        value.append("&").append("pa_MP=").append(pa_MP);
        value.append("&").append("pd_FrpId=").append(pd_FrpId);
        value.append("&").append("pr_NeedResponse=").append(pr_NeedResponse);
        value.append("&").append("hmac=").append(hmac);
        response.sendRedirect(value.toString());

        return null;

    }


    /**
     * 支付准备模块
     */
    public String payPlan(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        request.setAttribute("order",orderService.findByOid(request.getParameter("oid")));
        return "f:/jsps/order/pay.jsp";
    }



    /**
     *  取消订单模块
     */
    public String cancel(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String oid = request.getParameter("oid");
        boolean bool = orderService.isStatus(oid,1);
        //如果状态错误，就返回错误信息
        if(!bool){
            request.setAttribute("code","error");
            request.setAttribute("msg","取消失败，您已经付款或取消订单");
            return "f:/jsps/msg.jsp";
        }

        //修改验证码
        orderService.updateStatus(oid,5);
        request.setAttribute("code","success");
        request.setAttribute("msg","取消成功");
        return "f:/jsps/msg.jsp";




    }

    /**
     *  确认收货模块
     */
    public String confirm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String oid = request.getParameter("oid");
        boolean bool = orderService.isStatus(oid,3);
        //如果状态错误，就返回错误信息
        if(!bool){
            request.setAttribute("code","error");
            request.setAttribute("msg","确定收货失败");
            return "f:/jsps/msg.jsp";
        }

        //修改验证码
        orderService.updateStatus(oid,4);
        request.setAttribute("code","success");
        request.setAttribute("msg","确认收货成功");
        return "f:/jsps/msg.jsp";

    }


    /**
     * 查看订单详情
     */
    public String desc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        //1.得到oid
        String oid = request.getParameter("oid");
        //2.得到btn  0:都不显示 1:确认收货 2：取消订单
        int btn = getBtn(request);

        //3.使用Service查询
        Order order = orderService.findByOid(oid);

        //4.保存信息
        request.setAttribute("order",order);
        request.setAttribute("btn",btn);

        return "f:/jsps/order/desc.jsp";


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
     * 新建订单模块
     */
    public String addOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //1.得到地址、CartItemIds、User

//        request.setCharacterEncoding("UTF-8");
        String address = request.getParameter("address");
        address = new String(address.getBytes("ISO-8859-1"),"utf-8") ;

        String CartItemIds = request.getParameter("cartItemIds");
        User user = (User) request.getSession().getAttribute("sessionUser");

        //2.生成Order
            //使用传过来的CartItemIds得到所有CartItem的集合
        List<CartItem> cartItems = cartItemService.loadCartItems(CartItemIds);
        Order order = new Order();
        order.setOid(CommonUtils.uuid());
        order.setOrdertime(String.format("%tF %<tT",new Date()));
        order.setStatus(1);
        order.setAddress(address);
        order.setOwner(user);
        //得到Total需要循环所有购物车条目的小计加起来才能得到总计
        BigDecimal total = new BigDecimal("0");
        for(CartItem cartItem : cartItems){
            BigDecimal val = new BigDecimal(cartItem.getTotal()+"");
            //add 表示为 + ，所以还要赋值
            total = total.add(val);

        }

        order.setTotal(total.doubleValue());


        //3.得到OrderItem
        List<OrderItem> orderItems = new ArrayList<>();

        for(CartItem cartItem : cartItems){
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderItemId(CommonUtils.uuid());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(cartItem.getTotal());
            orderItem.setBook(cartItem.getBook());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);

        //4.使用service 增加新值
        orderService.addOrder(order);

        //5.删除购物车里面的条目
        cartItemService.batchDelete(CartItemIds);

        //保存值并返回
        request.setAttribute("order",order);
        return "f:/jsps/order/ordersucc.jsp";
    }


    /**
     * 我的购物车模块
     */
    public String MyOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.得到pc(当前页码)
        int pc = getPc(request);

        //2.得到Uid
        User user = (User) request.getSession().getAttribute("sessionUser");
        String uid = user.getUid();

        //3.得到url
        String url = getUrl(request);


        //4.通过uid和pc查询得到PageBean
        PageBean<Order> pageBean = orderService.MyOrder(uid,pc);

        //5.保存url到pageBean中
        pageBean.setUrl(url);

        //6.保存到请求中
        request.setAttribute("pageBean",pageBean);


        return "f:/jsps/order/list.jsp";

    }

    //得到pc
    private int getPc(HttpServletRequest request) {
        String pc = request.getParameter("pc");

        if(pc == null || pc.isEmpty()){
            return 1;
        }else{
            return Integer.parseInt(pc);
        }
    }

    //得到URl
    private String getUrl(HttpServletRequest request) {
        String url = request.getRequestURI()+ "?" +request.getQueryString();
        String[] urls = url.split("&pc=");
        url = urls[0];
        return url;

    }
}
