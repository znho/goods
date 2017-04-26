package cn.itcast.goods.admin.category.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.category.domain.Category;
import cn.itcast.goods.category.service.CategoryService;
import cn.itcast.servlet.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by znho_0 on 2017/3/20.
 */
public class AdminCategoryServlet extends BaseServlet {
    CategoryService categoryService = new CategoryService();

    public String deleteChildren(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
        String cid = request.getParameter("cid");
        int i = categoryService.findBookIsNull(cid);
        System.out.println(i);
        if (i > 0 ){
            request.setAttribute("msg","删除失败，请删除此分类全部的图书");
            return "f:/adminjsps/admin/msg.jsp";
        }
        categoryService.deleteCategory(cid);
        return findAll(request,response);


    }

    public String deleteParent(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
        String cid = request.getParameter("cid");
        int i = categoryService.findChildrenIsNull(cid);

        if(i > 0){
            request.setAttribute("msg","删除失败，请删除此分类全部的子分类");
            return "f:/adminjsps/admin/msg.jsp";
        }
        categoryService.deleteCategory(cid);
        return findAll(request,response);
    }

    public String alterChildren(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.得到一级分类的cid
        String pid = request.getParameter("pid");
        //得到Cname，desc,cid
        String cid = request.getParameter("cid");
        String cname = new String(request.getParameter("cname").getBytes("ISO-8859-1"),"utf-8");
        String desc = new String(request.getParameter("desc").getBytes("ISO-8859-1"),"utf-8");

        //封装进Category
        Category category = new Category();
        category.setCid(cid);
        category.setCname(cname);
        category.setDesc(desc);

        Category parent = new Category();
        parent.setCid(pid);
        category.setParent(parent);

        //修改
        categoryService.alterCategory(category);
        return findAll(request,response);
    }

    /**
     *  修改二级分类的页面数据处理
     */
    public String alterChildrenPre(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String cid = request.getParameter("cid");
        String parent = request.getParameter("parent");
        //得到二级分类
        Category category = categoryService.findCatgory(cid);
        //得到所有一级分类
        List<Category> categories = categoryService.findParent();
        request.setAttribute("categories",categories);
        request.setAttribute("category",category);
        request.setAttribute("cid",parent);


        return "f:/adminjsps/admin/category/edit2.jsp";
    }

    /**
     * 修改一级分类
     */
    public String alterParent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String cid = request.getParameter("cid");
        String cname = new String(request.getParameter("cname").getBytes("ISO-8859-1"),"utf-8");
        String desc = new String(request.getParameter("desc").getBytes("ISO-8859-1"),"utf-8");
        Category category = new Category();
        category.setCid(cid);
        category.setCname(cname);
        category.setDesc(desc);
        categoryService.alterCategory(category);
        return findAll(request,response);
    }

    /**
     *  得到修改一级分类页面的参数
     */
    public String alterParentPre(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String cid = request.getParameter("cid");
        Category category = categoryService.findCatgory(cid);
        request.setAttribute("category",category);
        return "f:/adminjsps/admin/category/edit.jsp";
    }


    /**
     *  增加第二分类
     */
    public String addChildren(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        //得到pid ,cname,desc
        String pid = request.getParameter("pid");
        String cname = new String(request.getParameter("cname").getBytes("ISO-8859-1"),"utf-8");
        String desc = new String(request.getParameter("desc").getBytes("ISO-8859-1"),"utf-8");
        System.out.println(cname+"  "+desc);
        Category category = new Category();
        category.setCname(cname);
        category.setDesc(desc);
        Category parent = new Category();
        parent.setCid(pid);
        category.setParent(parent);
        category.setCid(CommonUtils.uuid());
        categoryService.addParent(category);

        return findAll(request,response);
    }


    /**
     * 跳转到二级分类页面,找到所有一级分类
     */
    public String findParent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String cid = request.getParameter("cid");
        List<Category> categories = categoryService.findParent();
        request.setAttribute("cid",cid);
        request.setAttribute("categories",categories);
        return "f:/adminjsps/admin/category/add2.jsp";
    }


    /**
     * 增加一级分类
     */
    public String addParent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        Category category = new Category();
        String cname = new String(request.getParameter("cname").getBytes("ISO-8859-1"),"utf-8") ;
        String desc = new String(request.getParameter("desc").getBytes("ISO-8859-1"),"utf-8") ;

        category.setCname(cname);
        category.setDesc(desc);
        if(category.getCname() == null || category.getCname().isEmpty() || category.getDesc().isEmpty() || category.getDesc() == null){
            request.setAttribute("msg","不能为空");
            return "f:/adminjsps/admin/category/add.jsp";
        }
        category.setCid(CommonUtils.uuid());
        categoryService.addParent(category);
        return findAll(request,response);


    }


    /**
     * 找到所有分类
     */
    public String findAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Category> categories = categoryService.findAll();
        request.setAttribute("categories",categories);
        return  "f:/adminjsps/admin/category/list.jsp";
    }
}
