<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>cartlist.jsp</title>

    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <!--
    <link rel="stylesheet" type="text/css" href="styles.css">
    -->
    <script src="<c:url value='/jquery/jquery-1.5.1.js'/>"></script>
    <script src="<c:url value='/js/round.js'/>"></script>

    <link rel="stylesheet" type="text/css" href="<c:url value='/jsps/css/cart/list.css'/>">
    <script type="text/javascript">
        $(function () {
            //计算总计
            total();
            //全选按钮被点击时的时间
            $("#selectAll").click(function () {
                //得到按钮是否被选择的布尔值
                var pool = $(this).attr("checked");
                //调用全选或全不选方法，传入布尔值
                CheckAll(pool);
                //刚好！如果boolean为true我们就显示亮按钮，反之暗按钮，所以可以传入同样的布尔值
                balance(pool);
            })
            //单击一个选项的事件
            clickOne();

            //给加号添加点击事件
            $(".jia").click(function () {

                //得到id
                var id = $(this).attr("id");

                var list = id.split("Jia");
                ajaxQuantity(list[0],1);
            })

            //给减号添加点击事件
            $(".jian").click(function () {
                //得到id id=CartItemId + Jian
                var id = $(this).attr("id");
                //去掉Jian
                var list = id.split("Jian");
                var num = $("#"+list[0]+"Quantity" ).val();
                if(0 === num-1){
                    if(confirm("要把他移除购物车吗？"))
                    {
                        location="/goods/CartItemServlet?method=batchDelete&cartItemIds="+list[0];
                    }
                    else
                    {
                        return false;
                    }
                }
                ajaxQuantity(list[0],-1);

            })



        });

        //单击一个选项的事件
        function clickOne() {
            //所有的选项循环
            $(":checkbox[name=checkboxBtn]").each(function () {
                //每个选项的点击事件
                $(this).click(function () {
                    //所有单选项的数量
                    var all = $(":checkbox[name=checkboxBtn]").length;
                    //被勾中的单选项的数量
                    var get = $(":checkbox[name=checkboxBtn][checked=true]").length;
                    //如果全勾中
                    if (all == get) {
                        //显示结算亮按钮
                        balance(true);
                        //设置全选勾中
                        $("#selectAll").attr("checked", true);
                    } else if (get == 0) {
                        //显示结算暗按钮
                        balance(false);
                        //设置全选不勾中
                        $("#selectAll").attr("checked", false);
                    } else {
                        //显示结算亮按钮
                        balance(true);
                        //设置全选不勾中
                        $("#selectAll").attr("checked", false);
                    }
                    //计算总计
                    total();
                })
            })
        }

        //设置结算按钮样式
        function balance(pool) {
            //如果为true
            if (pool) {
                //换样式（图片）
                $("#jiesuan").removeClass("kill").addClass("jiesuan");
                //取出绑定的click事件
                $("#jiesuan").unbind("click");

            } else {
                //换样式
                $("#jiesuan").removeClass("jiesuan").addClass("kill");
                //设置点击事件，点击无效
                $("#jiesuan").click(function () {
                    return false;
                })
            }
        }

        //全选事件
        function CheckAll(pool) {
            $(":checkbox[name=checkboxBtn]").attr("checked", pool);
            //计算总计
            total();
        }

        //计算总金额
        function total() {
            var total = 0;
            $(":checkbox[name=checkboxBtn][checked=true]").each(function () {
                var id = $(this).val();
                var money = $("#" + id + "Subtotal").text();
                //得到的是Number，所以我们要转换为number
                total += Number(money);
            })
            //round:四舍五入，第二个参数：保留两位小数
            $("#total").text(round(total, 2));
        }
        //批量删除
        function deleteAll() {
            var CartItemIds = "";
            //所有被选中的长度
            var num = $(":checkbox[name=checkboxBtn][checked=true]").length;
            //所有被选择的单选框事件
            var array = new Array();
            $(":checkbox[name=checkboxBtn][checked=true]").each(function (i) {
                //id = CartItemId
                var CartItemId = $(this).val();
                CartItemIds += CartItemId;
                //如果不是最后一个，都加上逗号
                if (i < num - 1) {
                    CartItemIds += ","
                }

            })
            //发送请求
            location = "/goods/CartItemServlet?method=batchDelete&cartItemIds=" + CartItemIds;

        }



        //增减数量的ajax请求
        function ajaxQuantity(CartItemId,num) {
            var url = "/goods/CartItemServlet?method=updateQuantity";

            var data = {"num":num,"cartItemId":CartItemId};
            $.getJSON(url,data,function (data) {
                $("#"+CartItemId+"Subtotal" ).text(data.total);
                $("#"+CartItemId+"Quantity" ).val(data.quantity);
                total();
            });
        }
        function _getIds() {
            var array = new Array();
            $(":checkbox[name=checkboxBtn][checked=true]").each(function () {
                array.push($(this).val());
            })

            $("#cartItemIds").val(array.toString());
            $("#allTotal").val($("#total").text());

            $("#form1").submit();
        }


    </script>
</head>
<body>

<c:choose>
<c:when test="${empty cartItems}">
    <table width="95%" align="center" cellpadding="0" cellspacing="0">
        <tr>
            <td align="right">
                <img align="top" src="<c:url value='/images/icon_empty.png'/>"/>
            </td>
            <td>
                <span class="spanEmpty">您的购物车中暂时没有商品</span>
            </td>
        </tr>
    </table>
</c:when>
<c:otherwise>


<br/>
<br/>


<table width="95%" align="center" cellpadding="0" cellspacing="0">
    <tr align="center" bgcolor="#efeae5">
        <td align="left" width="50px">
            <input type="checkbox" id="selectAll" checked="checked"/><label for="selectAll">全选</label>
        </td>
        <td colspan="2">商品名称</td>
        <td>单价</td>
        <td>数量</td>
        <td>小计</td>
        <td>操作</td>
    </tr>


    <c:forEach items="${cartItems}" var="cartItem">


        <tr align="center">
            <td align="left">
                <input value="${cartItem.cartItemId}" type="checkbox" name="checkboxBtn" checked="checked"
                       />
            </td>
            <td align="left" width="70px">
                <a class="linkImage" href="<c:url value='/jsps/book/desc.jsp'/>"><img border="0" width="54" align="top"
                                                                                      src="<c:url value='${cartItem.book.image_w}'/>"/></a>
            </td>
            <td align="left" width="400px">
                <a href="<c:url value='/jsps/book/desc.jsp'/>"><span>${cartItem.book.bname}</span></a>
            </td>
            <td><span>&yen;<span class="currPrice" id="${cartItem.cartItemId}CurrPrice">${cartItem.book.currPrice}</span></span>
            </td>
            <td>
                <a class="jian" id="${cartItem.cartItemId}Jian" ></a><input class="quantity" readonly="readonly"
                                                                         id="${cartItem.cartItemId}Quantity" type="text"
                                                                         value="${cartItem.quantity}"/><a class="jia"
                                                                                                          id="${cartItem.cartItemId}Jia" ></a>
            </td>
            <td width="100px">
                <span class="price_n">&yen;<span class="subTotal"
                                                 id="${cartItem.cartItemId}Subtotal">${cartItem.total}</span></span>
            </td>
            <td>
                <a href="<c:url value='/CartItemServlet?method=batchDelete&cartItemIds=${cartItem.cartItemId}'/>">删除</a>
            </td>
        </tr>

    </c:forEach>




    <tr>
        <td colspan="4" class="tdBatchDelete">
            <a href="javascript:deleteAll();">批量删除</a>
        </td>
        <td colspan="3" align="right" class="tdTotal">
            <span>总计：</span><span class="price_t">&yen;<span id="total"></span></span>
        </td>
    </tr>
    <tr>
        <td colspan="7" align="right">
            <a href="javascript:_getIds()" id="jiesuan" class="jiesuan"></a>
        </td>
    </tr>
</table>
<form id="form1" action="<c:url value='/CartItemServlet'/>" method="post">
    <input type="hidden" name="cartItemIds" id="cartItemIds" />
    <input type="hidden" name="allTotal" id="allTotal" />
    <input type="hidden" name="method" value="loadCartItems"/>
</form>

</c:otherwise>
</c:choose>
</body>
</html>
