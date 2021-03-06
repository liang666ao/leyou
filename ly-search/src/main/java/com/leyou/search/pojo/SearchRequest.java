package com.leyou.search.pojo;

import java.util.Map;

public class SearchRequest {

    private String key; //搜索字段
    private Integer page; // 当前页
    private Map<String,Object> filter; //过滤字段

    private static final int DEFAULT_SIZE = 20; // 每页大小,不从页面接受,而是固定大小
    private static final  int DEFAULT_PAGE =1;// 默认页

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        if(page == null){
            return DEFAULT_PAGE;
        }
        //获取页码时做一些效验,不能小于1
        return Math.max(DEFAULT_PAGE,page);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return DEFAULT_SIZE;
    }
    public Map<String, Object> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, Object> filter) {
        this.filter = filter;
    }
}
