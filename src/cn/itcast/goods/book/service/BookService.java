package cn.itcast.goods.book.service;

import cn.itcast.goods.book.dao.BookDao;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.book.pager.PageBean;

import java.sql.SQLException;

/**
 * Created by znho_0 on 2017/3/12.
 */
public class BookService {
    private BookDao bookDao = new BookDao();

    /**
     * 删除图书
     */
    public void deleteBook(String bid){
        try {
            bookDao.deleteBook(bid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 根据Bid查找单本图书详细信息
     */
    public Book load(String bid){
        try {
            return bookDao.load(bid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 根据分类查找信息
     */
    public PageBean<Book> findByCategory(String cid, int pc){
        try {
            return bookDao.findByCategory(cid,pc);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 按照作者查询
     */
    public PageBean<Book> findByAuthor(String author,int pc){
        try {
            return  bookDao.findByAuthor(author,pc);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 按照出版社查询
     */
    public PageBean<Book> findByPress(String press,int pc){
        try {
            return  bookDao.findByPress(press,pc);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 按照书名查询
     */
    public PageBean<Book> findByBname(String cname,int pc) {
        try {
            return  bookDao.findByBname(cname,pc);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 多条件查询
     */
    public PageBean<Book> findByCondition(Book condition,int pc){
        try {
            return  bookDao.findByCondition(condition,pc);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *添加图书
     */
    public void add(Book book) {
        try {
            bookDao.add(book);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改图书
     */
    public void updateBook(Book book){
        try {
            bookDao.updateBook(book);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
