package com.sspu.search.pojo;

import lombok.Data;

import java.util.Map;

/**请求参数：json格式，目前只有一个属性：key-搜索关键字，但是搜索结果页一定是带有分页查询的，
 * 所以将来肯定会有page属性，因此我们可以用一个对象来接收请求的json数据：
 * @author hp
 */
@Data
public class SearchRequest  {
    private String key;//搜索条件

    private Integer page;//当前页

    private String  sortBy;//排序字段

    private Boolean descending;//是否降序
    private Map<String,Object> filter;
    private static final Integer DEFAULT_SIZE = 10;// 每页大小，不从页面接收，而是固定大小
    private static final Integer DEFAULT_PAGE = 1;// 默认页

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Boolean getDescending() {
        return descending;
    }

    public void setDescending(Boolean descending) {
        this.descending = descending;
    }

    public Map<String, Object> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, Object> filter) {
        this.filter = filter;
    }

    public SearchRequest(String key, Integer page) {
        this.key = key;
        this.page = page;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        if (page == null){
            return DEFAULT_PAGE;
        }
        // 获取页码时做一些校验，不能小于1
        return Math.max(DEFAULT_PAGE,page);
    }

    public void setPage(Integer page) {
        this.page = page;
    }
    public Integer getSize() {
        return DEFAULT_SIZE;
    }
}
