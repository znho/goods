package cn.itcast.goods.admin.admin.dao;

import cn.itcast.goods.admin.admin.domain.Admin;
import cn.itcast.jdbc.TxQueryRunner;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;

/**
 * Created by znho_0 on 2017/3/20.
 */
public class AdminDao {
    QueryRunner queryRunner = new TxQueryRunner();

    public Admin login(String adminname,String adminpwd) throws SQLException {
        String sql = "select * from t_admin where adminname = ? and adminpwd = ?";
        Admin admin = queryRunner.query(sql,new BeanHandler<Admin>(Admin.class),adminname,adminpwd);
        return  admin;

    }


}
