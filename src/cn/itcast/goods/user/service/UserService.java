package cn.itcast.goods.user.service;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.user.dao.UserDao;
import cn.itcast.goods.user.domain.User;
import cn.itcast.goods.user.service.exception.UserException;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;

import javax.mail.MessagingException;
import javax.mail.Session;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * 用户模块业务层
 * Created by znho_0 on 2017/3/6.
 */
public class UserService {
    private UserDao userDao = new UserDao();

    /**
     * 验证用户是否存在并修改密码
     */
    public void updatePass(String loginname,String loginpass,String newpass) throws UserException {
        try {
            boolean bool = userDao.UserIsExist(loginname,loginpass);
            if(bool){
                throw new UserException("密码错误");
            }
            userDao.updatePass(loginname,loginpass,newpass);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * 验证用户名
     * @param loginname
     * @return
     */
    public boolean ajaxValidateLoginname(String loginname){
        try {
             return userDao.ajaxValidateLoginname(loginname);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 验证邮箱
     * @param email
     * @return
     */
    public boolean ajaxValidateemail(String email){
        try {
            return userDao.ajaxValidateemail(email);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 添加用户
     */
    public void regist(User user){
        //增加UID
        user.setUid(CommonUtils.uuid());
        user.setStatus(false);
        user.setActivationCode(CommonUtils.uuid() + CommonUtils.uuid());

        //向数据库插入
        try {
            userDao.add(user);
        } catch (SQLException e) {
            throw new RuntimeException();
        }

//        发送邮件
//        1.得到properties文件
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("email_template.properties"));
        } catch (IOException e) {
            throw new RuntimeException();
        }

//        2.登录邮箱服务器（服务器，用户名，密码）
        Session session = MailUtils.createSession(properties.getProperty("host"),properties.getProperty("username"),properties.getProperty("password"));

//        3.创建邮箱对象（发送人，接收人，主题，正文）
        //MessageFormat.format方法会用第二个参数来代替第一个参数中的占位符｛0｝（从0开始）
        String content = MessageFormat.format(properties.getProperty("content"),user.getActivationCode());
        System.out.println(content);
        Mail mail = new Mail(properties.getProperty("from"),user.getEmail(),properties.getProperty("subject"),content);

//        4.发送邮件
        try {
            MailUtils.send(session,mail);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 激活功能
     */
    public void activationCode(String code) throws UserException {
        User user = null;
        try {
            //根据验证码查询有没有User
            user = userDao.queryCode(code);
            if(user == null){//如果没有，用自定义异常抛出，由我们的上一层调用者处理
               throw new UserException("无效的激活码");
            }else if(user.isStatus() == true){//如果已经激活，也抛出我们的自定义异常
               throw new UserException("你已经激活");
            }else if(user.isStatus() == false){//没激活，就激活
                userDao.updateStatus(code,true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 通过用户名密码访问数据库，返回User
     */
    public User queryWithNameAndPass(String loginname,String loginpass){
        try {
            User user = userDao.queryWithNameAndPass(loginname,loginpass);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

}
