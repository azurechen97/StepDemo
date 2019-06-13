package com.aoxue.stepdemo.bean;

import com.aoxue.stepdemo.utils.TuLinConstants;

public class HomeListBean {
    //讲话人
    private int teller = TuLinConstants.APP;
    //内容
    private String content;
    //日期
    private String data;

    public int getTeller() {
        return teller;
    }

    public void setTeller(int teller) {
        this.teller = teller;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

