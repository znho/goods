package cn.itcast.goods.admin.admin.service;

import cn.itcast.goods.admin.admin.dao.AdminDao;
import cn.itcast.goods.admin.admin.domain.Admin;

import java.sql.SQLException;

/**
 * Created by znho_0 on 2017/3/20.
 */
public class AdminService {
    AdminDao adminDao = new AdminDao();

    public Admin login(String adminname , String adminpwd){
        try {
            return adminDao.login(adminname,adminpwd);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
