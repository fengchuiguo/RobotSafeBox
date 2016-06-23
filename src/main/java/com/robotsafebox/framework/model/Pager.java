package com.robotsafebox.framework.model;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pager<T> {

    private Integer pageNo = 1;// 页码，默认是第一页
    private Integer pageSize = 15;// 每页显示的记录数，默认是15
    private Integer totalRecord;// 总记录数
    private Integer totalPage;// 总页数
    private List<T> results;// 对应的当前页记录
    private Map<String, Object> customparams = new HashMap<String, Object>();// 其他的参数我们把它分装成一个Map对象
    private String othersql;
    private Boolean isReturnPager = true;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(Integer totalRecord) {
        this.totalRecord = totalRecord;
        // 在设置总页数的时候计算出对应的总页数，在下面的三目运算中加法拥有更高的优先级，所以最后可以不加括号。
        int totalPage = totalRecord % pageSize == 0 ? totalRecord / pageSize
                : totalRecord / pageSize + 1;
        this.setTotalPage(totalPage);
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public Map<String, Object> getCustomparams() {
        return customparams;
    }

    public void setCustomparams(Map<String, Object> customparams) {
        this.customparams = customparams;
    }


    public String getOthersql() {
        return othersql;
    }

    public void setOthersql(String othersql) {
        this.othersql = othersql;
    }

    public Boolean getReturnPager() {
        return isReturnPager;
    }

    public void setReturnPager(Boolean returnPager) {
        isReturnPager = returnPager;
    }

    @Override
    public String toString() {
        return "Pager [pageNo=" + pageNo + ", pageSize=" + pageSize
                + ", totalRecord=" + totalRecord + ", totalPage=" + totalPage
                + ", results=" + results + ", customparams=" + customparams
                + "]";
    }

}
