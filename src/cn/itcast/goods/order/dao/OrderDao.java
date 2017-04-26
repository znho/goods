package cn.itcast.goods.order.dao;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.book.pager.Expression;
import cn.itcast.goods.book.pager.PageBean;
import cn.itcast.goods.book.pager.PageConstants;
import cn.itcast.goods.order.domain.Order;
import cn.itcast.goods.order.domain.OrderItem;
import cn.itcast.jdbc.TxQueryRunner;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.*;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by znho_0 on 2017/3/18.
 */
public class OrderDao {
    QueryRunner queryRunner = new TxQueryRunner();

    /**
     * 根据订单状态查询
     */
    public PageBean<Order> findByStatus(String status,int pc) throws SQLException {
        Expression expression = new Expression("status","=",status);
        List<Expression> list = new ArrayList<>();
        list.add(expression);
        return findByCriteria(list,pc);

    }

    /**
     * 找到所有订单
     */
    public PageBean<Order> findAll(int pc) throws SQLException {
//        //1.给要查询的类赋值
//        Expression expression = new Expression();
//        List<Expression> expressions = new ArrayList<>();
//        expressions.add(expression);
        //2.使用查询方法
        return findByCriteria(null,pc);


    }

    /**
     *传入oid查询状态
     */
    public int queryStatus(String oid) throws SQLException {
        String sql = "select status from t_order where oid = ?";
        Number number = (Number) queryRunner.query(sql,new ScalarHandler(),oid);
        return number.intValue();
    }

    /**
     * 修改状态码
     */
    public void updateStatus(String oid,int status) throws SQLException {
        String sql = "update t_order set status = ? where oid = ?";
        queryRunner.update(sql,status,oid);
    }

    /**
     * 使用oid查询
     */
    public Order findByOid(String oid) throws SQLException {
        //得到order
        String sql = "select * from t_order where oid = ?";
        Order order = queryRunner.query(sql,new BeanHandler<Order>(Order.class),oid);

        //得到orderItem
        List<OrderItem> orderItem = getOrderItemList(oid);
        order.setOrderItems(orderItem);

        return order;
    }


    /**
     * 增加订单
     */
    public void addOrder(Order order) throws SQLException {
        //新建order订单
        String sql = "INSERT INTO t_order VALUES(?,?,?,?,?,?)";
        Object[] values = {order.getOid(),order.getOrdertime(),order.getTotal(),order.getStatus()
        ,order.getAddress(),order.getOwner().getUid()};
        queryRunner.update(sql,values);


        //新建OrderItem订单
        sql = "insert into t_orderitem values(?,?,?,?,?,?,?,?)";
        //得到List的长度
        int len = order.getOrderItems().size();
        //新建一个二维数组，用于批处理
        Object[][] args = new Object[len][];
        List<OrderItem> orderItems = order.getOrderItems();
        for(int i = 0 ; i < len ; i ++){
            //取出每一个OrderItem，对应一个args[i]
            OrderItem orderItem = orderItems.get(i);
            args[i] = new Object[]{orderItem.getOrderItemId(), orderItem.getQuantity(), orderItem.getSubtotal(), orderItem.getBook().getBid(),
                    orderItem.getBook().getBname(), orderItem.getBook().getCurrPrice(), orderItem.getBook().getImage_b(), orderItem.getOrder().getOid()};

        }
        //批处理
        queryRunner.batch(sql,args);

    }


    /**
     * 我的订单模块
     */
    public PageBean<Order> MyOrder(String uid,int pc) throws SQLException {
        //1.给要查询的类赋值
        Expression expression = new Expression("uid","=",uid);
        List<Expression> expressions = new ArrayList<>();
        expressions.add(expression);
        //2.使用查询方法
        return findByCriteria(expressions,pc);


    }

    /**
     *  通用查询方法
     */
    private PageBean<Order> findByCriteria(List<Expression> expressionList, int pc) throws SQLException {
        //1.得到ps（每页记录数）
        int ps = PageConstants.ORDER_PAGE_SIZE;
        //2.拼凑字符串
        StringBuilder condition = new StringBuilder(" where 1 = 1");
        List<Object> values = new ArrayList<>();
        //循环
        if(expressionList != null){
            for (Expression expression : expressionList){
                //拼凑后半段的条件
                condition.append(" and ").append(expression.getName()+" ").append(expression.getOperator()+" ");
                //只要不是is null ，就在后面加占位符，之后把条件放进数组
                if (!expression.getOperator().equalsIgnoreCase("is null")){
                    condition.append("?");

                    values.add(expression.getValue());
                }
            }
        }



        //得到总记录数
        String sql = "select count(*) from t_order" + condition.toString();

        Number number = (Number) queryRunner.query(sql,new ScalarHandler(),values.toArray());

        int tr = number.intValue();

        //得到当前页数据
        sql = "select * from t_order" + condition.toString()+ " order by ordertime desc limit ?,?";
        values.add((pc-1) * ps  );
        values.add(ps);
        //通过查询得到list<Order> ,我们查询的数据和Order类中的数据都可以一一对应，
        // 除了User，但是我们不用使用到他，所以不用使用Map，直接Bean映射就可以
        List<Order> orders =queryRunner.query(sql,new BeanListHandler<Order>(Order.class),values.toArray());
        //因为里面没有OrderItem，所以我们要给他加上
        for(Order order : orders){
            //通过oid可以查询到有哪些图书目录类，把这些类集合返回
            List<OrderItem> orderItems = getOrderItemList(order.getOid());
            //设置进我们的属性
            order.setOrderItems(orderItems);
        }

        //创建PageBean
        PageBean<Order> pageBean = new PageBean<>();
        pageBean.setTr(tr);
        pageBean.setPc(pc);
        pageBean.setPs(ps);
        pageBean.setBeanList(orders);



        return  pageBean;
    }

    //通过Oid查询所有OrderItem
    private List<OrderItem> getOrderItemList(String oid) throws SQLException {
        //查询语句
        String sql = "select * from t_orderitem where oid = ?";
        //因为类和数据库列不吻合，所以要使用Map取出，后一一对应加入
        List<Map<String,Object>> mapList = queryRunner.query(sql,new MapListHandler(),oid);
        List<OrderItem> orderItems = new ArrayList<>();
        //把多个map转换为多个OrderItem
        for(Map<String,Object> map:mapList){
            OrderItem orderItem = getOrderItem(map);

            orderItems.add(orderItem);
        }
        return orderItems;

    }

    private OrderItem getOrderItem(Map<String,Object> map){
        OrderItem orderItem = CommonUtils.toBean(map,OrderItem.class);
        Book book = CommonUtils.toBean(map, Book.class);
        Order order = CommonUtils.toBean(map,Order.class);
        orderItem.setBook(book);
        orderItem.setOrder(order);
        return orderItem;
    }

//    @Test
//    public void test(){
//        System.out.println(String.format("%tF %<tT",new Date()));
//    }
}
