package cn.itcast.goods.book.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.book.pager.PageBean;
import cn.itcast.goods.book.service.BookService;
import cn.itcast.servlet.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by znho_0 on 2017/3/12.
 */
public class BookServlet extends BaseServlet{
    private BookService bookService = new BookService();


    /**
     * 根据Bid查找单本图书详细信息
     */
    public String findByBid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        //1.得到bid
        String bid = request.getParameter("bid");

        //2.使用service查找
        Book book = bookService.load(bid);

        //3.保存图书信息，转发到desc.jsp中
        request.setAttribute("book",book);
        return "f:/jsps/book/desc.jsp";
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
        return "f:/jsps/book/list.jsp";
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
                return "f:/jsps/book/list.jsp";
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

        //使用了临时参数代替
        PageBean<Book> pageBean = bookService.findByPress(press,pc);

        //4.将url加入到PageBean
        pageBean.setUrl(url);

        //5.保存PageBean，转发到list.jsp
        request.setAttribute("pageBean",pageBean);
        return "f:/jsps/book/list.jsp";
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

        //临时使用temp代替
        PageBean<Book> pageBean = bookService.findByAuthor(author,pc);

        //4.将url加入到PageBean
        pageBean.setUrl(url);

        //5.保存PageBean，转发到list.jsp
        request.setAttribute("pageBean",pageBean);

        return "f:/jsps/book/list.jsp";
    }

    /**\
     * 按照类型查找
     */
    public String findByCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //1.得到pc,如果不存在。pc=1
        int pc = getPc(request);

        //2.得到URL
        String url = getUrl(request);





        //3.获取参数，使用参数得到service得到PageBean
        String cid = request.getParameter("cid");
        PageBean<Book> pageBean = bookService.findByCategory(cid,pc);

        //4.将url加入到PageBean
        pageBean.setUrl(url);

        //5.保存PageBean，转发到list.jsp
        request.setAttribute("pageBean",pageBean);
        return "f:/jsps/book/list.jsp";
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
