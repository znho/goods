package cn.itcast.goods.cart.dao;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.cart.domain.CartItem;
import cn.itcast.goods.user.domain.User;
import cn.itcast.jdbc.TxQueryRunner;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by znho_0 on 2017/3/16.
 */

public class CartItemDao {
    QueryRunner queryRunner = new TxQueryRunner();

    /**
     * 得到被勾选的CartItem
     */
    public List<CartItem> loadCartItems(String CartItems) throws SQLException {
        //拼接字符串

        Object[] values = CartItems.split(",");
        StringBuilder sql = new StringBuilder("SELECT * FROM t_cartitem c ,t_book b WHERE b.bid = c.bid AND c.cartItemId IN(");
        for(int i = 0 ; i < values.length ; i++){
            if(i< values.length - 1){
                sql.append("?,");
            }else {
                sql.append("?");
            }
        }
        sql.append(")");


        //查询后得到多个map
        List<Map<String,Object>> listMap = queryRunner.query(sql.toString(),new MapListHandler(),values);
        //返回多个CartItem
        List<CartItem> cartItems = getCartItemList(listMap);

        return cartItems;
    }

//    @Test
//    public void test(){

//    }


    /**
     *  修改数量
     */
     public void updateOneQuantity(String CartItemId,int num) throws SQLException {
        String sql = "update t_cartitem set quantity = quantity + ? where cartItemId = ?";
        queryRunner.update(sql,num,CartItemId);
    }

    /**
     * 使用CartItemId查询
     */
    public CartItem findByCartItemId(String CartItemId) throws SQLException {
        String sql = "select * from t_cartitem c,t_book b  where c.bid = b.bid and cartItemId = ? ";

        Map<String,Object> map = queryRunner.query(sql,new MapHandler(),CartItemId);
        CartItem cartItem = getCartItem(map);
        return cartItem;



    }


    /**
     *   删除购物车条目
     */
    public void batchDelete(String CartItemIds) throws SQLException {
        Object[] CartItem = CartItemIds.split(",");
        //只有一个删除
        if(CartItem.length == 1){
            String sql = "delete from t_cartitem where cartItemId = ?";
            queryRunner.update(sql,CartItem[0]);
        }else{//批量删除
            StringBuilder sql = new StringBuilder("delete from t_cartitem where cartItemId in(?");
            for(int i = 1;i < CartItem.length ; i++){
                sql.append(",?");
            }
            sql.append(")");

            queryRunner.update(sql.toString(),CartItem);

        }
    }






    /**
     * 使用bid和uid查询
     */
    public CartItem findByBidAndUid(String bid,String uid) throws SQLException {
        String sql = "select * from t_cartitem where bid = ? and uid = ?";
        Map<String,Object> cartItemMap = queryRunner.query(sql, new MapHandler(),bid,uid);

        CartItem cartItem = getCartItem(cartItemMap);
        return cartItem;
    }

    /**
     * 更改已有图书数量
     */
    public void updateQuantity(CartItem cartItem,int quantity) throws SQLException {
        String sql = "update t_cartitem set quantity = ? where cartItemId = ?";
        queryRunner.update(sql,quantity,cartItem.getCartItemId());

    }

    /**
     * 增加图书到购物车
     */
    public void insertCart(CartItem cartItem) throws SQLException {

        String sql = "insert into t_cartitem(cartItemId,quantity,bid,uid) values(?,?,?,?)";
        queryRunner.update(sql,cartItem.getCartItemId(),cartItem.getQuantity(),cartItem.getBook().getBid(),cartItem.getUser().getUid());
    }




    /**
     * 得到购物车列表
     */
    public List<CartItem> getMyCart(String uid) throws SQLException {
        //多表查询，这样子就可以得到书的信息和购物车里面的信息
        String sql = "select * from t_cartitem c,t_book b  where c.bid = b.bid and c.uid = ? order by c.orderBy";
        List<Map<String,Object>> cartList = queryRunner.query(sql,new MapListHandler(),uid);
        if(cartList == null)return null;

        return getCartItemList(cartList);


    }

    /**
     * 把map转换为CartItem
     */

    private CartItem getCartItem(Map<String,Object> cartMap){
        if(cartMap == null) return null;
        CartItem cartItem = CommonUtils.toBean(cartMap,CartItem.class);
        Book book = CommonUtils.toBean(cartMap,Book.class);
        User user = CommonUtils.toBean(cartMap,User.class);
        cartItem.setBook(book);
        cartItem.setUser(user);
        return cartItem;
    }

    /**
     * 将多个map(List<Map<String,Object>>)转换为多个CartItem(List<CartItem>)
     */
    private List<CartItem> getCartItemList(List<Map<String,Object>> cartItemList){
        List<CartItem> cartItems = new ArrayList<>();
        for(Map<String,Object> cartItemMap :cartItemList){
            CartItem cartItem = getCartItem(cartItemMap);
            cartItems.add(cartItem);
        }
        return cartItems;
    }



}
