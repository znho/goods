package cn.itcast.goods.order.domain;

import cn.itcast.goods.user.domain.User;

import java.util.List;

/**
 * 图书类
 * Created by znho_0 on 2017/3/18.
 */

public class Order {
    private String oid;//id
    private String ordertime;//订单时间
    private double total;//总计
    private int status;//状态 1.未付款 2.已付款但是没发货 3.已到货没确定收货 4.已经确定收货 5.已取消(未付款才能取消)
    private String address;//地址
    private User owner;//用户
    private List<OrderItem> orderItems;//图书条目类的集合

    @Override
    public String toString() {
        return "Order{" +
                "oid='" + oid + '\'' +
                ", ordertime='" + ordertime + '\'' +
                ", total=" + total +
                ", status=" + status +
                ", address='" + address + '\'' +
                ", owner=" + owner +
                ", orderItems=" + orderItems +
                '}';
    }

    public Order() {
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(String ordertime) {
        this.ordertime = ordertime;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
