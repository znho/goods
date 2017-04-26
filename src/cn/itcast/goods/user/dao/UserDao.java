package cn.itcast.goods.user.dao;

import cn.itcast.goods.user.domain.User;
import cn.itcast.jdbc.TxQueryRunner;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;

/**
 * 用户持久层
 * Created by znho_0 on 2017/3/6.
 */
public class UserDao {
    private QueryRunner queryRunner = new TxQueryRunner();




    //修改密码
    public void updatePass(String loginname,String loginpass,String newpass) throws SQLException {
        String sql = "update t_user set loginpass = ? where loginname = ? and loginpass = ?";
        queryRunner.update(sql,newpass,loginname,loginpass);
    }


    //验证用户是否存在,true为不存在
    public boolean UserIsExist(String loginname,String loginpass) throws SQLException {
        String sql = "select count(1) from t_user where loginname = ? and loginpass = ?";
        Number number  = (Number) queryRunner.query(sql,new ScalarHandler(),loginname,loginpass);
        return number.intValue() == 0;
    }

    //验证用户名是否注册过
    public boolean ajaxValidateLoginname(String loginname) throws SQLException {
        String sql = "select count(1) from t_user where loginname = ?";
        Number number = (Number) queryRunner.query(sql,new ScalarHandler(),loginname);

        return number.intValue() == 0;
    }

    //验证邮箱是否注册过
    public boolean ajaxValidateemail(String email) throws SQLException {
        String sql = "select count(1) from t_user where email = ?";
        Number number = (Number) queryRunner.query(sql,new ScalarHandler(),email);
        return number.intValue() == 0;
    }

    /**
     * 添加用户
     */
    public void add(User user) throws SQLException {
        String sql = "insert into t_user values(?,?,?,?,?,?)";
        Object[] args = {user.getUid(),user.getLoginname(),user.getLoginpass(),user.getEmail(),user.isStatus(),user.getActivationCode()};
        queryRunner.update(sql,args);
    }

    /**
     * 通过激活码返回User
     */
    public User queryCode(String code) throws SQLException {
        String sql = "select * from t_user where activationCode=?";
        User user = queryRunner.query(sql,new BeanHandler<>(User.class),code);
        System.out.println("查询成功");
        return user;
    }

    /**
     * 修改状态码
     */
    public void updateStatus(String code,boolean bool) throws SQLException {
        String sql = "update t_user set status = ? where activationCode = ?";
        queryRunner.update(sql,bool,code);
        System.out.println("修改成功");
    }

    /**
     * 通过用户名密码访问数据库，返回User
     */
    public User queryWithNameAndPass(String loginname,String loginpass) throws SQLException {
        String sql = "select * from t_user where loginname = ? and loginpass = ?";
        User user = queryRunner.query(sql,new BeanHandler<User>(User.class),loginname,loginpass);
        return user;
    }

}
