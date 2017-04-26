package cn.itcast.goods.book.dao;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.book.pager.Expression;
import cn.itcast.goods.book.pager.PageBean;
import cn.itcast.goods.book.pager.PageConstants;
import cn.itcast.goods.category.domain.Category;
import cn.itcast.jdbc.TxQueryRunner;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by znho_0 on 2017/3/12.
 */
public class BookDao {
    private QueryRunner queryRunner = new TxQueryRunner();



    //封装book对象，把一级分类和二级分类的cid也放进去
    private Book getBookFromMap(Map<String,Object> map){
        //把图书的大部分信息赋值给book
        Book book = CommonUtils.toBean(map,Book.class);
        //把book属于的分类（二级分类）的cid赋值给Category
        Category category = CommonUtils.toBean(map,Category.class);
        //这个对象表示book属于的分类的父分类（一级分类）
        Category parent = new Category();
        //他的cid就是二级分类的pid
        parent.setCid((String) map.get("pid"));
        //设置关联
        category.setParent(parent);
        book.setCategory(category);
        return  book;
    }


    /**
     * 根据bid查询图书
     */
    public Book load(String bid) throws SQLException {
        //多表查询，因为我们需要category里面的cid和pid（一级分类和二级分类）
        String sql = "SELECT * FROM t_book b,t_category c WHERE b.cid = c.`cid` AND bid = ?";
        //得到map，因为里面的cid是我们Book里面没有的属性，但是我们需要他
        Map<String,Object> map = queryRunner.query(sql,new MapHandler(),bid);

        //得到完整的Book（里面有外链Category属性）
        Book book = getBookFromMap(map);

        return book;

    }


    /**
     * 根据分类查找信息
     */
    public PageBean<Book> findByCategory(String cid,int pc) throws SQLException {
        List<Expression> list = new ArrayList<>();
        Expression expression = new Expression("cid","=",cid);
        list.add(expression);
        PageBean<Book> pageBean = findByCriteria(list,pc);
        return pageBean;
    }

    /**
     * 按照作者查询
     */
    public PageBean<Book> findByAuthor(String author,int pc) throws SQLException {
        List<Expression> list = new ArrayList<>();
        Expression expression = new Expression("author","like","%"+author+"%");
        list.add(expression);
        PageBean<Book> pageBean = findByCriteria(list,pc);
        return pageBean;
    }

    /**
     * 按照出版社查询
     */
    public PageBean<Book> findByPress(String press,int pc) throws SQLException {
        List<Expression> list = new ArrayList<>();
        Expression expression = new Expression("press","like","%"+press+"%");
        list.add(expression);
        PageBean<Book> pageBean = findByCriteria(list,pc);
        return pageBean;
    }

    /**
     * 按照书名查询
     */
    public PageBean<Book> findByBname(String bname,int pc) throws SQLException {
        List<Expression> list = new ArrayList<>();
        Expression expression = new Expression("bname","like","%"+bname+"%");
        list.add(expression);
        PageBean<Book> pageBean = findByCriteria(list,pc);
        return pageBean;
    }

    /**
     * 多条件查询
     */
    public PageBean<Book> findByCondition(Book condition,int pc) throws SQLException {
        List<Expression> list = new ArrayList<>();
        Expression expression1 = new Expression("author","like","%"+condition.getAuthor()+"%");
        Expression expression2 = new Expression("bname","like","%"+condition.getBname()+"%");
        Expression expression3 = new Expression("press","like","%"+condition.getPress()+"%");
        list.add(expression1);
        list.add(expression2);
        list.add(expression3);
        PageBean<Book> pageBean = findByCriteria(list,pc);
        return pageBean;
    }





    /**
     * 通用的查询方法
     * ExcList：查询条件的集合
     * pc：当前页数
     */
    private PageBean<Book> findByCriteria(List<Expression> ExcList, int pc) throws SQLException {

        //1.得到ps（每页记录数）
        int ps = PageConstants.BOOK_PAGE_SIZE;//每页记录数


        //2.通过参数查找总记录数
        StringBuilder where = new StringBuilder(" where 1 = 1");
        //新建一个list，存储代替占位符的值
        List<Object> values = new ArrayList<>();
        //以and开头
        //运算符可以是= > < like 等 其中如果是is null的话，就没有值
        for(Expression expression : ExcList){
            where.append(" and").append(" ").append(expression.getName()).append(" ").append(expression.getOperator()).append(" ");
            //如果是is null，就不赋值
            if(!expression.getOperator().equalsIgnoreCase("is null")){
                where.append("?");
                values.add(expression.getValue());
            }
        }

        String sql = "select count(*) from t_book" + where.toString();
        //查询后返回数据数量
        Number number = (Number) queryRunner.query(sql,new ScalarHandler(),values.toArray());
        int tr = number.intValue();

        //3.当前页的数据(limit : 两个数据，一个是从第几个开始，一个是要多少个)
        sql = "select * from t_book "+ where.toString() +" order by orderBy limit ?,?";
        values.add((pc-1) * ps);
        values.add(ps);
        //里面没有category属性，但是我们没有子类，所以不需要
        List<Book> BeanList = queryRunner.query(sql,new BeanListHandler<Book>(Book.class),values.toArray());
        //给PageBean赋值。但是url没有，因为他要交给servlet赋值
        PageBean<Book> pageBean = new PageBean<>();
        pageBean.setPc(pc);
        pageBean.setPs(ps);
        pageBean.setTr(tr);
        pageBean.setBeanList(BeanList);

        return pageBean;
    }


    /**
     * 添加图书
     */
    public void add(Book book) throws SQLException {
        String sql = "insert into t_book(bid,bname,author,price,currPrice," +
                "discount,press,publishtime,edition,pageNum,wordNum,printtime," +
                "booksize,paper,cid,image_w,image_b)" +
                "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] params = {book.getBid(),book.getBname(),book.getAuthor(),book.getPrice(),book.getCurrPrice(),
                            book.getDiscount(),book.getPress(),book.getPublishtime(),book.getEdition(),book.getPageNum(),
                            book.getWordNum(),book.getPrinttime(),book.getBooksize(),book.getPaper(),book.getCategory().getCid(),
                            book.getImage_w(),book.getImage_b()};
        queryRunner.update(sql,params);

    }


    /**
     * 修改图书
     */
    public void updateBook(Book book) throws SQLException {
        String sql ="update t_book set bname=?,author=?,price=?,currPrice=?," +
                "discount=?,press=?,publishtime=?,edition=?,pageNum=?,wordNum=?,printtime=?," +
                "booksize=?,paper=?,cid=?,image_w=?,image_b=? where bid = ?";

        Object[] params = {book.getBname(),book.getAuthor(),book.getPrice(),book.getCurrPrice(),
                book.getDiscount(),book.getPress(),book.getPublishtime(),book.getEdition(),book.getPageNum(),
                book.getWordNum(),book.getPrinttime(),book.getBooksize(),book.getPaper(),book.getCategory().getCid(),
                book.getImage_w(),book.getImage_b(),book.getBid()};
        queryRunner.update(sql,params);

    }

    /**
     * 删除图书
     */
    public void deleteBook(String bid) throws SQLException {
        String  sql  = "delete from t_book where bid = ?";
        queryRunner.update(sql,bid);
    }


}
