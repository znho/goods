package cn.itcast.goods.user.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.user.domain.User;
import cn.itcast.goods.user.service.UserService;
import cn.itcast.goods.user.service.exception.UserException;
import cn.itcast.servlet.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by znho_0 on 2017/3/6.
 */
public class UserServlet extends BaseServlet {
    private UserService userService = new UserService();

    /**
     * 退出功能
     */
    public String quit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getSession().removeAttribute("sessionUser");
        return "r:/jsps/user/login.jsp";
    }

    /**
     *  修改密码页面
     */
    public String updatePass(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //封装数据
        System.out.println("开始");
        User user = CommonUtils.toBean(request.getParameterMap(),User.class);

        HttpSession httpSession = request.getSession();
        User sessionuser = (User) httpSession.getAttribute("sessionUser");
        if(sessionuser == null){
            request.setAttribute("msg","您还没有登录");
            return "f:/jsps/user/login.jsp";
        }

        //验证数据
        Map<String,String> errors = virifyUpdatePass(user,request.getSession());
        if(!errors.isEmpty()){
            System.out.println(errors);

            request.setAttribute("errors",errors);
            return "f:/jsps/user/pwd.jsp";
        }

        //使用Session中的得到的数据和传入的密码，新密码，执行验证用户是否存在与修改密码业务

        try {
            userService.updatePass(sessionuser.getLoginname(),user.getLoginpass(),user.getNewpass());
            request.setAttribute("code","success");
            request.setAttribute("msg","修改密码成功");
            request.getSession().invalidate();
            return "f:/jsps/msg.jsp";
        } catch (UserException e) {
            request.setAttribute("msg",e.getMessage());
            return "f:/jsps/user/pwd.jsp";
        }



    }

    /**
     *  登录页面
     */
    public String login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //把表单数据封装到User中
        User fromuser = CommonUtils.toBean(request.getParameterMap(),User.class);



        //验证数据
        Map<String,String> errors = verify(fromuser,request.getSession());

        //如果错误，返回错误信息
        if(errors.size() > 0 ){
            request.setAttribute("errors",errors);
            request.setAttribute("user",fromuser.getLoginname());
            return "f:/jsps/user/login.jsp";
        }

        //通过帐号密码在数据库查找用户
        User user = userService.queryWithNameAndPass(fromuser.getLoginname(),fromuser.getLoginpass());

        //如果为空
        if(user == null){
            request.setAttribute("hint","账号或密码错误");
            request.setAttribute("user",fromuser.getLoginname());
            return "f:/jsps/user/login.jsp";
        }else{//如果不为空
            //如果已经激活
            if(user.isStatus()){
                request.getSession().setAttribute("sessionUser",user);
                String loginname = user.getLoginname();
                loginname = URLEncoder.encode(loginname,"utf-8");
                Cookie cookie = new Cookie("loginname",loginname);
                cookie.setMaxAge(1 * 60 * 60 * 24 * 10);//十天,秒
                response.addCookie(cookie);
                return "r:/index.jsp";//重定向
            }else{//如果没有激活
                request.setAttribute("hint","您还未激活，请前往邮箱激活");
                request.setAttribute("user",fromuser.getLoginname());
                return "f:/jsps/user/login.jsp";
            }
        }



    }

    /**
     * 注册页面
     */
    public String regist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //封装数据到javaBean
//        String loginname =request.getParameter("loginname");
//        String loginpass =request.getParameter("loginpass");
//        String email =request.getParameter("email");
//
//        User user = new User();
//        user.setLoginname(loginname);
//        user.setLoginpass(loginpass);
//        user.setEmail(email);

        //功能和上面的代码一样，变得很方便
        User fromuser = CommonUtils.toBean(request.getParameterMap(),User.class);


        //校验数据，错误的话，保存成功信息，返回到regist.jsp显示
        Map<String,String> map = validateRegist(fromuser,request.getSession());
        if(map.size() > 0){
            request.setAttribute("from",fromuser);
            request.setAttribute("errors",map);
            return "f:/jsps/user/regist.jsp";
        }


        //使用service完成业务
        userService.regist(fromuser);

        //保存成功信息，转发到msg.jsp显示
        //因为转发过去的页面有两个要得到的请求，就是code和msg，一个是传成功或失败，一个是传信息过去
        request.setAttribute("code","success");
        request.setAttribute("msg","注册成功，请尽快到邮箱进行激活");

        return "f:/jsps/msg.jsp";
    }


    /*
     *ajax用户名是否正确
     */
    public String ajaxValidateLoginname(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String loginname = request.getParameter("loginname");
        boolean bool = userService.ajaxValidateLoginname(loginname);

        response.getWriter().print(bool);

        return null;
    }


    /*
 *ajax邮箱是否正确
 */
    public String ajaxValidateemail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //得到传入email
        String email = request.getParameter("email");
        //验证是否存在
        boolean bool = userService.ajaxValidateemail(email);
        //返回布尔值
        response.getWriter().print(bool);

        return null;
    }

    /**
     *   ajax验证码是否正确
    */
    public String ajaxValidateVerifyCode(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //得到传入的验证码
        String email = request.getParameter("verifyCode");
        //获得真实验证码
        String vCode = (String) request.getSession().getAttribute("vCode");

        boolean bool = vCode.equalsIgnoreCase(email);

        response.getWriter().print(bool);

        return null;
    }

    /**
     * 激活功能
     */
    public String activation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //激活
        String code = request.getParameter("activationCode");
        System.out.println(code);
        try {
            userService.activationCode(code);
            request.setAttribute("code","success");
            request.setAttribute("msg","账户激活成功");
        } catch (UserException e) {
            //说明抛出了异常
            request.setAttribute("code","error");
            request.setAttribute("msg",e.getMessage());

        }

        //保存成功状态

        return "f:/jsps/msg.jsp";
    }

    /**
     * 检验表单的每个数据,如果有错误，使用字段名作为key，错误信息作为value保存到map返回
     */
    private Map<String,String> validateRegist(User user , HttpSession session){
        Map<String,String> map = new HashMap<>();

        /**
         * 检验用户名
         */
        String loginname = user.getLoginname();
        if(loginname == null || loginname.trim().isEmpty()){
            map.put("loginname","用户名不能为空");
        }else if(loginname.length() > 20 || loginname.length() < 3){
            map.put("loginname","长度必须在3-20");
        }else if(!userService.ajaxValidateLoginname(loginname)){
            map.put("loginname","用户名已存在");
        }


        /**
         * 检验密码
         */
        String loginpass = user.getLoginpass();
        if(loginpass == null || loginpass.trim().isEmpty()){
            map.put("loginpass","密码不能为空");
        }else if(loginpass.length() > 20 || loginpass.length() < 3){
            map.put("loginpass","密码必须在3-20");
        }

        /**
         * 检验确认密码
         */
        String reloginpass = user.getReloginpass();
        if(reloginpass == null || reloginpass.trim().isEmpty()){
            map.put("reloginpass","确认密码不能为空");
        }else if(!loginpass.equals(reloginpass)){
            map.put("reloginpass","两次输出不一致");
        }

        /**
         * 检验邮箱
         */
        String email = user.getEmail();
        if(email == null || email.trim().isEmpty()){
            map.put("email","邮箱不能为空");
            //email.matches :这个方法里面放正则运算，可以检验格式
        }else if(!email.matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$")){
            map.put("email","请输入正确的邮箱格式");
        }else if(!userService.ajaxValidateemail(email)){
            map.put("email","邮箱已存在");
        }

        String vCode = (String) session.getAttribute("vCode");
        String verifyCode = user.getVerifyCode();
        if(verifyCode == null || verifyCode.trim().isEmpty()){
            map.put("verifyCode","验证码不能为空");
        }else if(!vCode.equalsIgnoreCase(verifyCode)){
            map.put("verifyCode","验证码错误");
        }

        return map;



    }

    //检验登录信息
    private Map<String,String> verify(User user,HttpSession session){
        Map<String,String> errors = new HashMap<String,String>();

        //检验用户名
        String loginname = user.getLoginname();
        if(loginname == null){
            errors.put("loginname","用户名不能为空");
        }
        if(loginname.length() > 20 || loginname.length() < 3){
            errors.put("loginname","用户名长度必须在3-20");
        }


        //检验密码
        String loginpass = user.getLoginpass();
        if(loginpass == null){
            errors.put("loginpass","密码不能为空");
        }else if(loginpass.length() > 20 || loginpass.length() < 3){
            errors.put("loginpass","密码长度必须在3-20");
        }

        String verifyCode = user.getVerifyCode();
        String vCode = (String) session.getAttribute("vCode");


        if(verifyCode == null || verifyCode.trim().isEmpty()){
            errors.put("verifyCode","验证码不能为空");
        }else if(!vCode.equalsIgnoreCase(verifyCode)){
            errors.put("verifyCode","验证码错误");
        }

        return errors;
    }

    /**
     *  检验修改密码信息
     */

    private Map<String,String> virifyUpdatePass(User user,HttpSession session){
        Map<String,String> error = new HashMap<>();

        //验证密码
        String loginpass = user.getLoginpass();
        if(loginpass == null || loginpass.trim().isEmpty()){
            error.put("loginpass","密码不能为空");
        }else if(loginpass.length() > 20 || loginpass.length() < 3){
            error.put("loginpass","密码长度必须在3-20");
        }

        //验证新密码
        String newpass = user.getNewpass();
        if(newpass == null || newpass.trim().isEmpty()){
            error.put("newpass","密码不能为空");
        }else if(newpass.length() > 20 || newpass.length() < 3){
            error.put("newpass","密码长度必须在3-20");
        }

        //验证确认密码
        String reloginpass = user.getReloginpass();
        if(reloginpass == null || reloginpass.trim().isEmpty()){
            error.put("reloginpass","密码不能为空");
        }else if(!reloginpass.equals(newpass)){
            error.put("reloginpass","两次密码输入不一样");
        }

        //验证验证码
        String vCode = (String) session.getAttribute("vCode");
        System.out.println(vCode);
        String verifycode = user.getVerifyCode();
        if(verifycode == null || verifycode.trim().isEmpty()){
            error.put("verifycode","验证码不能为空");
        }else if(!vCode.equalsIgnoreCase(verifycode)){
            error.put("verifycode","验证码错误");
        }

        return error;
    }

}
