package com.lzw.crm.vo;

import java.util.*;

public class PaginationVO<T> {
    private int total;
    private List<T> list;

    public PaginationVO() {

    }

    public PaginationVO(int total, List<T> list) {
        this.total = total;
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}