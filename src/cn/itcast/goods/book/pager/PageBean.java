package cn.itcast.goods.book.pager;

import java.util.List;

/**
 * 分页bean，会在各层传递
 * Created by znho_0 on 2017/3/12.
 */
public class PageBean<T> {
    private int pc;//当前页数
    private int tr;//总记录数
    private int ps;//每页记录数
    private String url;//请求链接
    private List<T> beanList;//当前页数据
//    private int tp;//总页数


    @Override
    public String toString() {
        return "PageBean{" +
                "pc=" + pc +
                ", tr=" + tr +
                ", ps=" + ps +
                ", url='" + url + '\'' +
                ", beanList=" + beanList +
                '}';
    }

    //计算总页数
    public int getTp() {
        int tp = tr/ps;

        return tr%ps == 0 ? tp : (tp+1);
    }

    public PageBean(){

    }


    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public int getTr() {
        return tr;
    }

    public void setTr(int tr) {
        this.tr = tr;
    }

    public int getPs() {
        return ps;
    }

    public void setPs(int ps) {
        this.ps = ps;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<T> getBeanList() {
        return beanList;
    }

    public void setBeanList(List<T> beanList) {
        this.beanList = beanList;
    }
}
