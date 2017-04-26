package cn.itcast.goods.admin.book.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.book.pager.PageBean;
import cn.itcast.goods.book.service.BookService;
import cn.itcast.goods.category.domain.Category;
import cn.itcast.goods.category.service.CategoryService;
import cn.itcast.servlet.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by znho_0 on 2017/3/22.
 */
public class AdminBookServlet  extends BaseServlet{
    BookService bookService = new BookService();
    CategoryService categoryService = new CategoryService();

    /**
     * 删除图书
     */
    public String deleteBook(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
        String bid = request.getParameter("bid");
        bookService.deleteBook(bid);
        request.setAttribute("msg","删除成功");
        return "f:/adminjsps/msg.jsp";
    }



    /**
     * 修改图书
      */

    public String updateBook(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
        //这个方法保存进了所有数据，除了category

        Book book = CommonUtils.toBean(request.getParameterMap(),Book.class);
        book.setBname(new String(request.getParameter("bname").getBytes("ISO-8859-1"),"utf-8"));
        book.setAuthor(new String(request.getParameter("author").getBytes("ISO-8859-1"),"utf-8"));
        book.setPress(new String(request.getParameter("press").getBytes("ISO-8859-1"),"utf-8"));
        book.setPaper(new String(request.getParameter("paper").getBytes("ISO-8859-1"),"utf-8"));
        //把cid映射到Category里的Cid
        Category category = CommonUtils.toBean(request.getParameterMap(),Category.class);
        book.setCategory(category);

        System.out.println(book);
        bookService.updateBook(book);
        request.setAttribute("msg","修改成功");
        return "f:/adminjsps/msg.jsp";
    }

    /**
     * 使用ajax的方式通过Cid得到二级分类
     */
    public String ajaxFindCategoryByCid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        //得到pid，通过pid查找这个分类下面的二级分类
        String pid = request.getParameter("pid");
        List<Category> child = categoryService.findCategoryChild(pid);

        //把数据转换为json字符串
        String json = toJson(child);
        response.getWriter().print(json);
        return null;

    }

    //把多个Category转为字符串 [{"cid":"xxx","cname":"xxx"},{"cid":"xxx","cname":"xxx"}]
    private String toJson(List<Category> child) {
        StringBuilder json = new StringBuilder("[");
        for(int i = 0 ; i< child.size();i++){
            json.append(toJson(child.get(i)));
            if(i < child.size()-1){
                json.append(",");
            }
        }
        json.append("]");

        return json.toString();
    }

    //把Category转为字符串:{"cid":"xxx","cname":"xxx"}
    private String toJson(Category category){
        StringBuilder json = new StringBuilder("{");
        json.append("\"cid\"").append(":").append("\"").append(category.getCid()).append("\",");
        json.append("\"cname\"").append(":").append("\"").append(category.getCname()).append("\"");
        json.append("}");
        return json.toString();
    }

    /**
     * 寻找所有一级分类
     */
    public String findParent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        List<Category> categories = categoryService.findParent();
        request.setAttribute("categories",categories);
        return "f:/adminjsps/admin/book/add.jsp";
    }

    /**
     * 根据Bid查找单本图书详细信息
     */
    public String findByBid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        //1.得到bid
        String bid = request.getParameter("bid");

        //2.使用service查找
        Book book = bookService.load(bid);
        //得到一级分类的集合
        List<Category> parent = categoryService.findParent();
        //通过得到的Book得到这本书的二级分类
        List<Category> children = categoryService.findCategoryChild(book.getCategory().getParent().getCid());
        //3.保存图书信息，转发到desc.jsp中
        request.setAttribute("book",book);
        request.setAttribute("parent",parent);
        request.setAttribute("children",children);

        return "f:/adminjsps/admin/book/desc.jsp";
    }

    /**
     * 多条件查询
     */
    public String findByCondition(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.得到pc,如果不存在。pc=1
        int pc = getPc(request);

        //2.得到URL
        String url = getUrl(request);


        //3.获取参数，使用参数得到service得到PageBean
        Book condition = CommonUtils.toBean(request.getParameterMap(),Book.class);
        PageBean<Book> pageBean = bookService.findByCondition(condition,pc);

        //4.将url加入到PageBean
        pageBean.setUrl(url);

        //5.保存PageBean，转发到list.jsp
        request.setAttribute("pageBean",pageBean);
        return "f:/adminjsps/admin/book/list.jsp";
    }

    /**
     * 根据按照书名查询
     */
    public String findByBname(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.得到pc,如果不存在。pc=1
        int pc = getPc(request);

        //2.得到URL
        String url = getUrl(request);

        //3.获取参数，使用参数得到service得到PageBean
        String bname = request.getParameter("bname");
        PageBean<Book> pageBean = bookService.findByBname(bname,pc);

        //4.将url加入到PageBean
        pageBean.setUrl(url);

        //5.保存PageBean，转发到list.jsp
        request.setAttribute("pageBean",pageBean);
        return "f:/adminjsps/admin/book/list.jsp";
    }

    /**
     * 根据按照出版社查询
     */
    public String findByPress(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.得到pc,如果不存在。pc=1
        int pc = getPc(request);

        //2.得到URL
        String url = getUrl(request);

        //3.获取参数，使用参数得到service得到PageBean
        String press = request.getParameter("press");
        PageBean<Book> pageBean = bookService.findByPress(press,pc);

        //4.将url加入到PageBean
        pageBean.setUrl(url);

        //5.保存PageBean，转发到list.jsp
        request.setAttribute("pageBean",pageBean);
        return "f:/adminjsps/admin/book/list.jsp";
    }

    /**
     * 根据作者查询
     */
    public String findByAuthor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.得到pc,如果不存在。pc=1

        int pc = getPc(request);

        //2.得到URL
        String url = getUrl(request);

        //3.获取参数，使用参数得到service得到PageBean
        String author = request.getParameter("author");
        PageBean<Book> pageBean = bookService.findByAuthor(author,pc);

        //4.将url加入到PageBean
        pageBean.setUrl(url);

        //5.保存PageBean，转发到list.jsp
        request.setAttribute("pageBean",pageBean);

        return "f:/adminjsps/admin/book/list.jsp";
    }


    /**
     *  找到二级分类里面的书
     */
    public String findBookFromCategory(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        //1.得到pc,cid,url
        int pc = getPc(request);
        String cid = request.getParameter("cid");
        String url = getUrl(request);
        //2.得到PageBean
        PageBean<Book> pageBean = bookService.findByCategory(cid,pc);
        pageBean.setUrl(url);
        //3.保存并返回
        request.setAttribute("pageBean",pageBean);
        return "f:/adminjsps/admin/book/list.jsp";
    }
    /**
     *找到所有分类
     */
    public String findAllCategory(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        List<Category> categories = categoryService.findAll();
        request.setAttribute("categories",categories);
        return "f:/adminjsps/admin/book/left.jsp";
    }

    //得到URl
    //http://localhost:8080/goods/BookServlet?methed=findByXXX&cid=xxx&pc=3
    private String getUrl(HttpServletRequest request){
        //getRequestURI:/goods/BookServlet
        String requsturl = request.getRequestURI();

        //getQueryString:methed=findByXXX&cid=xxx&pc=3
        String value = request.getQueryString();


        //由于没有带‘？’，所以我们要自己拼接上
        String url = requsturl + "?" +value;
        //我们不需要pc属性，所以截取掉
        //返回一个字符串最后出现的位置，0开始，没有的话为-1
        int index = url.lastIndexOf("&pc=");
        if(index != -1){
            //从0开始，到index-1结束
            url = url.substring(0,index);
        }



        return url;
    }

    //从请求中得到pc(当前页码)
    private int getPc(HttpServletRequest request){
        //pc等于1
        int pc = 1;
        //在请求在得到pc
        String stringpc = request.getParameter("pc");
        //如果stringpc不为空
        if(stringpc != null && !stringpc.isEmpty()){

            try{
                //把pc转换为int类型，如果是字符串就会抛出异常，我们不去处理，那么pc就会是默认值1
                pc = Integer.parseInt(stringpc);
            }catch(RuntimeException e){}
        }
        return pc;
    }

}
