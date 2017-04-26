package cn.itcast.goods.category.web.servlet;

import cn.itcast.goods.category.domain.Category;
import cn.itcast.goods.category.service.CategoryService;
import cn.itcast.servlet.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by znho_0 on 2017/3/11.
 * 分类web层
 */
public class CategoryServlet extends BaseServlet {
    CategoryService categoryService = new CategoryService();

    public String findAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Category> categories =  categoryService.findAll();
        request.setAttribute("categories",categories);
        return "f:/jsps/left.jsp";

    }
}
