package com.liuzozo.stepdemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liuzozo.stepdemo.R;

/***
 *  运动记录详情页 --- 不带地图的页面
 *  1. 根据传来的PathRecord 值的显示一些信息，见PPT 该页面
 */
public class SportRecordDetails_Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sport_details, container,
                false);

        // initSendData();
        return view;
    }
}
