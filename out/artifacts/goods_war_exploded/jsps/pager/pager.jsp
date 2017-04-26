<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
	function _go() {
		var pc = $("#pageCode").val();//获取文本框中的当前页码
		if(!/^[1-9]\d*$/.test(pc)) {//对当前页码进行整数校验
			alert('请输入正确的页码！');
			return;
		}
		if(pc > ${pageBean.tp}) {//判断当前页码是否大于最大页
			alert('请输入正确的页码！');
			return;
		}
		//跳转到这个url
		location = "${pageBean.url}&pc="+pc;
	}
</script>


<div class="divBody">
  <div class="divContent">
    <%--上一页 --%>
    <c:choose>
        <c:when test="${pageBean.pc eq 1}">
            <span class="spanBtnDisabled">上一页</span>
        </c:when>
        <c:otherwise>
            <a href="${pageBean.url}&pc=${pageBean.pc -1} " class="aBtn bold">上一页</a>
        </c:otherwise>
    </c:choose>


    
    <%-- 计算begin和end --%>
      <%-- 如果总页数<=6，那么显示所有页码，即begin=1 end=${pb.tp} --%>
        <%-- 设置begin=当前页码-2，end=当前页码+3 --%>
          <%-- 如果begin<1，那么让begin=1 end=6 --%>
          <%-- 如果end>最大页，那么begin=最大页-5 end=最大页 --%>

    
    <%-- 显示页码列表 --%>
        <c:choose>
            <%--当最大页小于等于6的时候--%>
            <c:when test="${pageBean.tp <= 6}">
                <%--begin等于1 ， end等于最大页--%>
                <c:set var="begin" value="1"/>
                <c:set var="end" value="${pageBean.tp}"/>
            </c:when>
            <c:otherwise>
                <%--设置begin=当前页-2，end=当前页+3--%>
                <c:set var="begin" value="${pageBean.pc - 2}"/>
                <c:set var="end" value="${pageBean.pc + 3}"/>

                <%--如果因此得到的begin < 1 的话--%>
                <c:if test="${begin < 1}">
                    <%-- 因此当前页面应该是小于3的，所以从1到6就可以 --%>
                    <c:set var="begin" value="1"/>
                    <c:set var="end" value="6"/>
                </c:if>
                <%-- 如果得到的end是大于最大页面的--%>
                <c:if test="${end > pageBean.tp }">
                    <%-- 因此当前页面+3后面是没有页面的，所以显示最大页面-5 和最大页面--%>
                    <c:set var="begin" value="${pageBean.tp - 5}"/>
                    <c:set var="end" value="${pageBean.tp}"/>
                </c:if>
            </c:otherwise>
        </c:choose>

        <%-- 开始循环--%>
        <c:forEach begin="${begin}" end="${end}" var="i">
            <c:choose>
                <%-- 如果循环到当前页面 --%>
                <c:when test="${i eq pageBean.pc}">
                    <%-- 设置不能点击的加亮按钮--%>
                    <span class="spanBtnSelect">${i}</span>
                </c:when>

                <c:otherwise>
                    <%-- 设置按钮，数值就是上面设置的，还有设置超链接=请求--%>
                    <a href="${pageBean.url}&pc=${i}" class="aBtn">${i}</a>
                </c:otherwise>
            </c:choose>

        </c:forEach>







      <%-- 显示点点点 --%>
        <c:if test="${end < pageBean.tp}">
            <span class="spanApostrophe">...</span>
        </c:if>

    
     <%--下一页 --%>
      <c:choose>
          <c:when test="${pageBean.pc eq pageBean.tp}">
              <span class="spanBtnDisabled">下一页</span>
          </c:when>
          <c:otherwise>
              <a href="${pageBean.url}&pc=${pageBean.pc + 1}" class="aBtn bold">下一页</a>
          </c:otherwise>

      </c:choose>
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    
    <%-- 共N页 到M页 --%>
    <span>共${pageBean.tp}页</span>
    <span>到</span>
    <input type="text" class="inputPageCode" id="pageCode" value="${pageBean.pc}"/>
    <span>页</span>
    <a href="javascript:_go();" class="aSubmit">确定</a>
  </div>
</div>