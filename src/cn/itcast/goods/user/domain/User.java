package cn.itcast.goods.user.domain;

/**
 * 用户模块实体类
 * Created by znho_0 on 2017/3/6.
 */
public class User {
    //对应数据库表
    private String uid;//主键
    private String loginname;//用户名
    private String loginpass;//密码
    private String email;//邮箱
    private boolean status;//状态:是否已激活
    private String activationCode;//激活码

    //注册表单
    private String reloginpass;//确认密码
    private String verifyCode;//验证码


    //修改密码表单
    private String newpass;//新密码

    public User() {
    }

    public User(String uid, String loginname, String loginpass, String email, boolean status, String activationCode, String reloginpass, String verifyCode, String newpass) {
        this.uid = uid;
        this.loginname = loginname;
        this.loginpass = loginpass;
        this.email = email;
        this.status = status;
        this.activationCode = activationCode;
        this.reloginpass = reloginpass;
        this.verifyCode = verifyCode;
        this.newpass = newpass;
    }

    public String getReloginpass() {
        return reloginpass;
    }

    public void setReloginpass(String reloginpass) {
        this.reloginpass = reloginpass;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getNewpass() {
        return newpass;
    }

    public void setNewpass(String newpass) {
        this.newpass = newpass;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getLoginpass() {
        return loginpass;
    }

    public void setLoginpass(String loginpass) {
        this.loginpass = loginpass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }


}
