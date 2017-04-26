
$(function(){
    //遍历每一个错误信息，如果是空的就隐藏
    $(".labelError").each(function () {
        showError($(this));
    })

    //鼠标经过事件，变图片效果
    $("#submit").hover(function(){
        $("#submit").attr("src","/goods/images/regist2.jpg");
    },function () {
        $("#submit").attr("src","/goods/images/regist1.jpg");
    })

    //获得焦点时，取消错误信息
    $(".input").focus(function () {
        var ErrorId = $(this).attr("id") + "Error";
        $("#"+ErrorId).text("");
        showError($("#"+ErrorId));
    });

    //失去焦点时，验证
    $(".input").blur(function () {

        var ErrorId = $(this).attr("id") + "Verify()";
        eval(ErrorId);
    })

    //表单验证
    $('#formregist').submit(function () {
        var bool = true;
        if(!loginnameVerify()){
            bool = false;
        }
        if(!loginpassVerify()){
            bool = false;
        }
        if(!reloginpassVerify()){
            bool = false;
        }
        if(!emailVerify()){
            bool = false;
        }
        if(!verifyCodeVerify()){
            bool = false;
        }
        return bool;

    })

})

/**
 * 换一张
 * @private
 */
function _change(){
    alert("12");
    $("#vCode").attr("src","/goods/VerifyCode?a="+new Date());
}


//登录名校验
function loginnameVerify(){

    var id = "loginname";
    var value = $("#"+id).val();
    //非空校验
    if(!value){
        $("#"+id+"Error").text("登录名不能为空");
        showError($("#"+id+"Error"));
        return false;
    }

    //长度校验
    if(value.length > 20 || value.length < 3){
        $("#"+id+"Error").text("长度必须在3-20");
        showError($("#"+id+"Error"));
        return false;
    }

    //是否注册校验
    $.ajax({
        url:"/goods/UserServlet",
        data:{method:"ajaxValidateLoginname",loginname:value},
        type:"POST",
        dataType:"json",
        async:false,//选择同步，这样就会执行完成之后再继续下面的代码
        cache:false,
        success:function (result){

            if(!result){//如果验证失败
                $("#"+id+"Error").text("用户名已存在");
                showError($("#"+id+"Error"));
                return false;
            }
        }
    });

    return true;
}

//登录密码校验
function loginpassVerify(){
    var id = "loginpass";
    var value = $("#"+id).val();
    //非空校验
    if(!value){
        $("#"+id+"Error").text("密码不能为空");
        showError($("#"+id+"Error"));
        return false;
    }
    //长度校验
    if(value.length > 20 || value.length < 3){
        $("#"+id+"Error").text("密码必须在3-20");
        showError($("#"+id+"Error"));
        return false;
    }



    return true;
}

//确认密码校验
function reloginpassVerify(){
    var id = "reloginpass";
    var value = $("#"+id).val();
    //非空校验
    if(!value){
        $("#"+id+"Error").text("确认密码不能为空");
        showError($("#"+id+"Error"));
        return false;
    }
    //两次密码输入
    if(value != $("#loginpass").val()){
        $("#"+id+"Error").text("两次输入不一致");
        showError($("#"+id+"Error"));
        return false;
    }



    return true;
}

//邮箱校验
function emailVerify(){
    var id = "email";
    var value = $("#"+id).val();
    //非空校验
    if(!value){
        $("#"+id+"Error").text("邮箱不能为空");
        showError($("#"+id+"Error"));
        return false;
    }
    //格式验证
    if(!/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/.test(value)){
        $("#"+id+"Error").text("请输入正确的邮箱格式");
        showError($("#"+id+"Error"));
        return false;
    }

    //是否注册校验
    $.ajax({
        url:"/goods/UserServlet",
        data:{method:"ajaxValidateemail",email:value},
        type:"POST",
        dataType:"json",
        async:false,//选择同步，这样就会执行完成之后再继续下面的代码
        cache:false,
        success:function (result){

            if(!result){//如果验证失败
                $("#"+id+"Error").text("邮箱已存在");
                showError($("#"+id+"Error"));
                return false;
            }
        }
    });

    return true;
}

//验证码校验
function verifyCodeVerify(){
    var id = "verifyCode";
    var value = $("#"+id).val();
    //非空校验
    if(!value){
        $("#"+id+"Error").text("验证码不能为空");
        showError($("#"+id+"Error"));
        return false;
    }
    //长度校验
    if(value.length != 4){
        $("#"+id+"Error").text("错误的验证码");
        showError($("#"+id+"Error"));
        return false;
    }

    //是否验证码是否错误
    $.ajax({
        url:"/goods/UserServlet",
        data:{method:"ajaxValidateVerifyCode",verifyCode:value},
        type:"POST",
        dataType:"json",
        async:false,
        cache:false,
        success:function (result) {
            if(!result){
                $("#" + id + "Error").text("验证码错误");
                showError($("#" + id + "Error"));
                return false;
            }
        }
    })

    return true;

}


//如果对象里面值为空的，就隐藏
function showError(arg){
    var text = arg.text();
    if(!text){
        arg.css("display","none");
    }else{
        arg.css("display","");
    }
}