package cn.itcast.goods.category.dao;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.category.domain.Category;
import cn.itcast.jdbc.TxQueryRunner;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by znho_0 on 2017/3/11.
 * 分类工具层
 */
public class CategoryDao {
    QueryRunner queryRunner = new TxQueryRunner();

    /**
     * 查询是否分类下存在图书
     */
    public int findBookIsNull(String cid) throws SQLException {
        String sql = "select count(*) from t_book where cid = ?";
        Number number = (Number) queryRunner.query(sql,new ScalarHandler(),cid);
        return number == null ? 0 : number.intValue();
    }

    /**
     * 查询是否存在二级分类
     *
     */
    public int findChildrenIsNull(String cid) throws SQLException {
        String sql = "select count(*) from t_category where pid = ?";
        Number number = (Number) queryRunner.query(sql,new ScalarHandler(),cid);
        return number == null ? 0 : number.intValue();
    }

    /**
     * 删除分类
     */
    public void deleteCategory(String cid) throws SQLException {
        String sql = "delete from t_category where cid = ?";
        queryRunner.update(sql,cid);
    }


    /**
     * 修改分类
     */
    public void alterCategory(Category category) throws SQLException {
        String sql  = "update t_category set cname = ? , `desc`=? , pid = ? where cid = ? ";
        String pid = null;

        if(category.getParent() != null){
            pid = category.getParent().getCid();
        }
        queryRunner.update(sql,category.getCname(),category.getDesc(),pid,category.getCid());
    }


    /**
     * 通过cid找到分类
     */
    public Category findCatgory(String cid) throws SQLException {
        String sql = "select * from t_category where cid = ?";
        Map<String,Object> map = queryRunner.query(sql,new MapHandler(),cid);
        return toCategory(map);
    }


    /**
     * 找到一级分类
     */
    public List<Category> findParent() throws SQLException {
        String sql = "select * from t_category where pid is null";
        List<Map<String,Object>> mapList = queryRunner.query(sql,new MapListHandler());
        List<Category> categories = toCategoryList(mapList);
        return  categories;
    }

    /**
     *找到所有分类
     */
    public List<Category> findAll() throws SQLException {
        //查找一级分类
        String sql = "select * from t_category where pid is null order by orderby";
        List<Map<String,Object>> categorys = queryRunner.query(sql,new MapListHandler());

        //把多个map转换为多个Category
        List<Category> categories = toCategoryList(categorys);

        //查找每个一级分类的子类
        for(Category category : categories){
            //查找这个一个分类的子类（一级分类的cid=二级分类的pid）
            //并返回所有的二级分类集合
            List<Category> child = findCategoryChild(category.getCid());
            //把二级分类放入一级分类的属性中
            category.setChildren(child);


        }

        return categories;
    }

    /**
     * 通过一级分类的cid查找所有二级分类
     */
    public List<Category> findCategoryChild(String pid) throws SQLException {
        //找到所有二级分类
        String sql = "select * from t_category where pid = ? order by orderby";
        List<Map<String,Object>> listCategory = queryRunner.query(sql,new MapListHandler(),pid);
        //把二级分类转换为Category集合，并且为他们设置父类
        List<Category> categories = toCategoryList(listCategory);
        return categories;
    }


    /**
     * 把多个map(List<Map>)转换为多个category(List<Category>)
     */
    private List<Category> toCategoryList(List<Map<String,Object>> list){

        List<Category> categories = new ArrayList<>();
        //把list里面的所以map拿出来循环
        for(Map<String ,Object> map : list){
            //把每个map都转换为Category
            Category category = toCategory(map);
            categories.add(category);
        }

        return categories;
    }


    /**
     * 把map转换为category
     */
    private Category toCategory(Map<String,Object> map){
        //把map中的数据传入Category中
        Category category = CommonUtils.toBean(map,Category.class);
        //得到map中的pid
        String pid = (String) map.get("pid");
        //如果pid有值
        if( pid != null){
            //创建一个对象
            Category parent = new Category();
            //因为一个对象的pid有值，表示pid对应的就是上一级分类的cid，所以
            //创建一个类，设置他的cid为我们的pid，作为我们category类的父类
            parent.setCid(pid);
            //把他设置为我们的父类属性中
            category.setParent(parent);
        }

        return category;

    }

    /**
     * 增加一级分类
     * @param category
     * @throws SQLException
     */
    public void addParent(Category category) throws SQLException {
        String sql = "insert into t_category(cid,cname,pid,`desc`) values(?,?,?,?)";
        String pid = null;
        //这样做最大的理由是如果category.getParent()是空的，我们使用category.getParent().getCid()会报空指针异常
        if(category.getParent() == null){
            pid = null;
        }else{
            pid = category.getParent().getCid();
        }
        queryRunner.update(sql,category.getCid(),category.getCname(),pid,category.getDesc());
    }




}
