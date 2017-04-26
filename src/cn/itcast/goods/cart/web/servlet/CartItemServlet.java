package cn.itcast.goods.cart.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.cart.domain.CartItem;
import cn.itcast.goods.cart.service.CartItemService;
import cn.itcast.goods.user.domain.User;
import cn.itcast.servlet.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by znho_0 on 2017/3/16.
 */
public class CartItemServlet extends BaseServlet {
    CartItemService cartItemService = new CartItemService();

    /**
     * 得到被勾选的CartItem
     */
    public String loadCartItems(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String allTotal = request.getParameter("allTotal");
        String sql = request.getParameter("cartItemIds");

        List<CartItem> CartItems = cartItemService.loadCartItems(sql);
        request.setAttribute("CartItems",CartItems);
        request.setAttribute("Total",allTotal);
        return "f:/jsps/cart/showitem.jsp";
    }



    /**
     * 增加减少数量
     */
    public String updateQuantity(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String num = request.getParameter("num");
        String CartItemId = request.getParameter("cartItemId");
        CartItem cartItem = cartItemService.updateQuantity(CartItemId,num);
        int quantity = cartItem.getQuantity();
        double total = cartItem.getTotal();
        String json = "{\"quantity\":\""+quantity+"\",\"total\":\""+total+"\"}";
        response.getWriter().print(json);
        return null;
    }

    /**
     * 删除功能
     */
    public String batchDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String CartItems = request.getParameter("cartItemIds");
        cartItemService.batchDelete(CartItems);
        return getMyCart(request,response);
    }



    /**
     * 添加到购物车
     */
    public String addCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        //得到参数bid，quantity
        CartItem cartItem = CommonUtils.toBean(request.getParameterMap(),CartItem.class);//只加入了quantity
        Book book = CommonUtils.toBean(request.getParameterMap(), Book.class);
        User user = (User) request.getSession().getAttribute("sessionUser");
        cartItem.setUser(user);
        cartItem.setBook(book);

        cartItemService.addCart(cartItem);

        return getMyCart(request,response);
    }


    /**
     *  查询购物车条目
     */
    public String getMyCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("sessionUser");
        List<CartItem> cartItems = cartItemService.getMyCart(user.getUid());
        request.setAttribute("cartItems",cartItems);
        return "f:/jsps/cart/list.jsp";

    }
}
