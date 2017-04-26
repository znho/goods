package cn.itcast.goods.admin.book.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.book.service.BookService;
import cn.itcast.goods.category.domain.Category;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by znho_0 on 2017/3/22.
 */
public class AdminAddBookServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        /**
         * commons-fileupload的上传三步
         */
        //1.创建工具，他是抽象类，所以要用子类创建
        //可以给参数，缓存大小和缓存地址
        FileItemFactory factory = new DiskFileItemFactory();

        //2. 创建解析器对象,传入factory
        ServletFileUpload sfu = new ServletFileUpload(factory);
        sfu.setFileSizeMax(80 * 1024);//设置单个上传的文件上限为80kb

        //3. 解析request得到List<FileItem>
        //FileItem:每一个代表的都是表单中传输过来的每一个数据
        List<FileItem> fileItemList = null;

        try {
            //开始解析数据
            fileItemList = sfu.parseRequest(request);
        } catch (FileUploadException e) {
            //这里如果会有异常，那应该就是文件大小超过80kb了
            //使用错误方法，转发到错误页面
            error("文件大小超过80kb",request,response);
            return;
        }

        //4.把List<FileItem>封装到Book对象中
        //4.1首先把普通字段放到一个map中，之后封装到book和category对象中，之后建立两者关系
        Map<String,Object> map = new HashMap<>();
        //遍历FileItem
        for(FileItem fileItem : fileItemList){
            //fileItem.isFormField()：如果你是普通表单字段
            if(fileItem.isFormField())
                //getFieldName：得到表单项的名字，fileItem.getString("utf-8")：得到表单项的值，设置编码
                map.put(fileItem.getFieldName(),fileItem.getString("utf-8"));
        }
        System.out.println(map);
        //把值映射到Book里面，除了Category和图片地址之外
        Book book = CommonUtils.toBean(map, Book.class);
        //把Cid映射到里面，表示这本书所属的分类
        Category category = CommonUtils.toBean(map,Category.class);
        //建立两者的关系
        book.setCategory(category);

        /**
         * 4.2把上传的图片保存起来
         *   -获取文件名，截取
         *   -给文件加前缀，加上uuid，避免同名
         *   -校验文件的扩展名，只能是jpg和png
         *   -校验图片的尺寸
         *   -指定图片的保存路径。这需要使用ServletContext#getRealPath()获取真实路径
         *   -保存
         *   -把图片的路径设置给book对象
         */
        //获取文件名
        FileItem fileItem = fileItemList.get(1);//获得大图
        String filename = fileItem.getName();
        System.out.println("测试是否是绝对路径:  " + filename);

        //获取文件名，因为部分浏览器上传的是绝对路径，不是单纯的文件名
        //用\\是因为一个\会出错，强行转义
        int index = filename.lastIndexOf("\\");
        //如果存在，就说明是绝对路径
        if(index != -1){
            //截取最后一个'/'后面的文件名字
            filename = filename.substring(index+1);
        }

        //给文件加前缀，加上uuid，避免同名
        filename = CommonUtils.uuid() + "_" + filename;

        //校验文件名的扩展名
        //如果不等于jpg或者png的话
        if( (!filename.toLowerCase().endsWith(".jpg")) && (!filename.toLowerCase().endsWith(".png")) ){
            //返回错误信息
            error("上传照片格式错误",request,response);
            return;
        }

        //校验图片尺寸
        //保存上传的图片，把图片new成图片对象：Image,Icon,ImageIcon,BufferedImage,ImageIO

        //保存图片
        //获取真实路径
        String savepath = this.getServletContext().getRealPath("/book_img");
        System.out.println("真实路径： " + savepath);

        //创建目标文件，传入路径和名字
        File destFile = new File(savepath,filename);

        //保存文件,把fileItem里面的临时文件（图片）用字节流写到destFile里面，之后再把临时文件删除
        try {
            fileItem.write(destFile);
        } catch (Exception e) {
            //会出错多半是第二次保存文件，但是临时文件已经删除了
            throw new RuntimeException(e);
        }

        //校验尺寸
        //创建ImageIcon,里面要传入图片的绝对路径：getAbsolutePath()
        ImageIcon imageIcon = new ImageIcon(destFile.getAbsolutePath());
        //得到Image对象
        Image image = imageIcon.getImage();
        //得到宽高
        if(image.getWidth(null) > 350 && image.getHeight(null) > 350){
            error("图片大于350*350",request,response);
            //图片不合标准，所以删了他
            destFile.delete();
            return;
        }
        //把图片路径设置给book
        book.setImage_w("book_img/" + filename);


        /**
         * 设置小图，代码和上面一模一样！！
         */

        //获取文件名
        fileItem = fileItemList.get(2);//获得小图
        filename = fileItem.getName();


        //获取文件名，因为部分浏览器上传的是绝对路径，不是单纯的文件名
        //用\\是因为一个\会出错，强行转义
        index = filename.lastIndexOf("\\");
        //如果存在，就说明是绝对路径
        if(index != -1){
            //截取最后一个'/'后面的文件名字
            filename = filename.substring(index+1);
        }

        //给文件加前缀，加上uuid，避免同名
        filename = CommonUtils.uuid() + "_" + filename;

        //校验文件名的扩展名
        //如果不等于jpg或者png的话
        if( (!filename.toLowerCase().endsWith(".jpg")) && (!filename.toLowerCase().endsWith(".png")) ){
            //返回错误信息
            error("上传照片格式错误",request,response);
            return;
        }

        //校验图片尺寸
        //保存上传的图片，把图片new成图片对象：Image,Icon,ImageIcon,BufferedImage,ImageIO

        //保存图片
        //获取真实路径
        savepath = this.getServletContext().getRealPath("/book_img");
        System.out.println("真实路径： " + savepath);

        //创建目标文件，传入路径和名字
        destFile = new File(savepath,filename);

        //保存文件,把fileItem里面的临时文件（图片）用字节流写到destFile里面，之后再把临时文件删除
        try {
            fileItem.write(destFile);
        } catch (Exception e) {
            //会出错多半是第二次保存文件，但是临时文件已经删除了
            throw new RuntimeException(e);
        }

        //校验尺寸
        //创建ImageIcon,里面要传入图片的绝对路径：getAbsolutePath()
        imageIcon = new ImageIcon(destFile.getAbsolutePath());
        //得到Image对象
        image = imageIcon.getImage();
        //得到宽高
        if(image.getWidth(null) > 250 && image.getHeight(null) > 250){
            error("图片大于250*250",request,response);
            //图片不合标准，所以删了他
            destFile.delete();
            return;
        }
        //把图片路径设置给book
        book.setImage_b("book_img/" + filename);

        //给book加入uuid
        book.setBid(CommonUtils.uuid());


        //调用Service进行保存
        BookService bookService = new BookService();
        bookService.add(book);


        request.setAttribute("msg","添加图书成功");
        request.getRequestDispatcher("/adminjsps/msg.jsp").forward(request,response);


    }

    //返回错误信息的方法
    private void error(String msg, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("msg",msg);
        request.getRequestDispatcher("/adminjsps/admin/book/add.jsp").forward(request,response);
    }


}
