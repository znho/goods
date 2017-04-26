package cn.itcast.goods.cart.domain;

import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.user.domain.User;

import java.math.BigDecimal;

/**
 * Created by znho_0 on 2017/3/16.
 */
public class CartItem {
    private String cartItemId;//主键
    private int quantity;//数量
    private Book book;//外键，图书
    private User user;//外键,用户

    //得到小计
    public double getTotal(){
        BigDecimal bigDecimal1 = new BigDecimal(book.getCurrPrice() + "");
        BigDecimal bigDecimal2 = new BigDecimal(quantity+ "");
        Number number = bigDecimal1.multiply(bigDecimal2);
        return number.doubleValue();
    }

    public String getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(String cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "cartItemId='" + cartItemId + '\'' +
                ", quantity=" + quantity +
                ", book=" + book +
                ", user=" + user +
                '}';
    }
}
