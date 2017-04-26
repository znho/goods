package cn.itcast.goods.category.service;

import cn.itcast.goods.category.dao.CategoryDao;
import cn.itcast.goods.category.domain.Category;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by znho_0 on 2017/3/11.
 * 分类服务层
 */
public class CategoryService {
    CategoryDao categoryDao = new CategoryDao();

    /**
     * 通过一级分类的cid查找所有二级分类
     */
    public List<Category> findCategoryChild(String pid)  {
        try {
            return categoryDao.findCategoryChild(pid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询是否分类下存在图书
     */
    public int findBookIsNull(String cid){
        try {
            return categoryDao.findBookIsNull(cid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询是否存在二级分类
     *
     */
    public int findChildrenIsNull(String cid){
        try {
            return categoryDao.findChildrenIsNull(cid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除分类
     */
    public void deleteCategory(String cid) {
        try {
            categoryDao.deleteCategory(cid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 修改分类
     */
    public void alterCategory(Category category){
        try {
            categoryDao.alterCategory(category);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * 通过cid找到分类
     */
    public Category findCatgory(String cid){
        try {
            return categoryDao.findCatgory(cid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * 查找所有一级分类
     */
    public List<Category> findParent(){
        try {
            return categoryDao.findParent();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 找到所有分类
     * @return
     */
    public List<Category> findAll(){
        try {
            List<Category> categories = categoryDao.findAll();
            return categories;
        } catch (SQLException e) {
            throw  new RuntimeException(e);
        }
    }

    /**
     *  增加一级分类
     * @param category
     */
    public void addParent(Category category){
        try {
            categoryDao.addParent(category);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

