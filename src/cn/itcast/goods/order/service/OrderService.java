package cn.itcast.goods.order.service;

import cn.itcast.goods.book.pager.PageBean;
import cn.itcast.goods.order.dao.OrderDao;
import cn.itcast.goods.order.domain.Order;
import cn.itcast.jdbc.JdbcUtils;

import java.sql.SQLException;

/**
 * Created by znho_0 on 2017/3/18.
 */
public class OrderService {
    OrderDao orderDao = new OrderDao();

    /**
     * 找到所有订单
     */
    public PageBean<Order> findAll(int pc){
        try {
            return orderDao.findAll(pc);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 验证 订单状态
     */
    public boolean isStatus(String oid,int status){
        try {
            int restatus = orderDao.queryStatus(oid);
            if(restatus == status){
                return true;
            }else {
                return false;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *  修改订单状态
     */
    public void updateStatus(String oid,int status){
        try {
            orderDao.updateStatus(oid,status);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




    /**
     * 使用oid查询
     */
    public Order findByOid(String oid){
        try {
            JdbcUtils.beginTransaction();
            Order order = orderDao.findByOid(oid);
            JdbcUtils.commitTransaction();
            return  order;
        } catch (SQLException e) {
            try {
                JdbcUtils.rollbackTransaction();
            } catch (SQLException e1) { }
            throw new RuntimeException(e);
        }
    }


    /**
     * 新建订单模块
     */
    public void addOrder(Order order){
        try {
            JdbcUtils.beginTransaction();
            orderDao.addOrder(order);
            JdbcUtils.commitTransaction();
        } catch (SQLException e) {
            try {
                JdbcUtils.rollbackTransaction();
            } catch (SQLException e1) {}
            throw new RuntimeException(e);
        }

    }


    /**
     * 我的购物车模块
     */
    public PageBean<Order> MyOrder(String uid ,int pc){
        try {
            JdbcUtils.beginTransaction();
            PageBean<Order> pageBean = orderDao.MyOrder(uid,pc);
            JdbcUtils.commitTransaction();
            return pageBean;
        } catch (SQLException e) {
            try {
                JdbcUtils.rollbackTransaction();
            } catch (SQLException e1) { }
            throw new RuntimeException(e);
        }

    }


    /**
     * 根据订单状态查询
     */
    public PageBean<Order> findByStatus(String status,int pc) {
        try {
            return orderDao.findByStatus(status,pc);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
