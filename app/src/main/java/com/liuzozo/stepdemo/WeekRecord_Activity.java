package com.liuzozo.stepdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * 一周跑步记录
 * 按天显示 柱状图、折线图等，这个页面大家可以自定义， 不需要非按照我的那个图片的样式
 * （比如一条折线显示每天跑步的公里，一条显示每天跑步的时间，一条显示跑步的卡路里 等等，）
 * 要求：只要显示出数据库里跑步记录的一些分析即可
 */
public class WeekRecord_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_record);
    }
}
