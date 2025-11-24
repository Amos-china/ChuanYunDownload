package com.chuanyun.downloader.tabbar.me.model;

import java.util.List;

public class VipGoodsData {
    private List<VipGoodsModel> list;
    private int dataTotal;
    private int pageTotal;
    private int prePage;
    private int nextPage;
    private int minPage;
    private int maxPage;
    private int currentPage;

    public List<VipGoodsModel> getList() {
        return list;
    }

    public void setList(List<VipGoodsModel> list) {
        this.list = list;
    }

    public int getDataTotal() {
        return dataTotal;
    }

    public void setDataTotal(int dataTotal) {
        this.dataTotal = dataTotal;
    }

    public int getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }

    public int getPrePage() {
        return prePage;
    }

    public void setPrePage(int prePage) {
        this.prePage = prePage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getMinPage() {
        return minPage;
    }

    public void setMinPage(int minPage) {
        this.minPage = minPage;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
