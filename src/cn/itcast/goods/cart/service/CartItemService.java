package cn.itcast.goods.cart.service;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.cart.dao.CartItemDao;
import cn.itcast.goods.cart.domain.CartItem;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by znho_0 on 2017/3/16.
 */
public class CartItemService {
    CartItemDao cartItemDao = new CartItemDao();


    /**
     * 得到被勾选的CartItem
     */
    public List<CartItem> loadCartItems(String CartItems){
        try {
            return cartItemDao.loadCartItems(CartItems);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 增加减少数量
     */
    public CartItem updateQuantity(String CartItemId,String num){
        int intNum = Integer.parseInt(num);
        try {
            //修改数据
            cartItemDao.updateOneQuantity(CartItemId,intNum);
            //使用CartItemId进行查询
            CartItem cartItem = cartItemDao.findByCartItemId(CartItemId);
            return  cartItem;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }




    }


    /**
     * 删除功能
     *
     */
    public void batchDelete(String CartItemIds){
        try {
            cartItemDao.batchDelete(CartItemIds);
        } catch (SQLException e) {
            throw  new RuntimeException(e);
        }
    }


    /**
     * 加入购物车功能
     */
    public void addCart(CartItem cartItem){
        try {
            //通过bid和uid查询购物车中是否已存在这本书

            CartItem reCartItem = cartItemDao.findByBidAndUid(cartItem.getBook().getBid(),cartItem.getUser().getUid());
            //如果已存在
            if(reCartItem != null){
                //购物车里面的数量加上我们新加的数量
                int quantity = reCartItem.getQuantity() + cartItem.getQuantity();
                cartItemDao.updateQuantity(reCartItem,quantity);
            }else{//如果不存在，就添加一条新记录
                cartItem.setCartItemId(CommonUtils.uuid());
                cartItemDao.insertCart(cartItem);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     *  查询购物车条目
     */
    public List<CartItem> getMyCart(String uid){
        try {
            return cartItemDao.getMyCart(uid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
