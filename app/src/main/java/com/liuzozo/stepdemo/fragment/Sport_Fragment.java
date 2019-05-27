package com.liuzozo.stepdemo.fragment;


import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.liuzozo.stepdemo.R;
import com.liuzozo.stepdemo.SportMap_Activity;

/**
 *  点击开发运动的 界面
 *  需要 查询数据库，查询出所有的跑步次数，跑步总公里，总时间
 */
public class Sport_Fragment extends Fragment {

    // 开始运动按钮
    Button startBtn;
    TextView sportMile;
    TextView sportCount;
    TextView sportTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater
                .inflate(R.layout.fragment_sport, container, false);
        initView(view);
        return view;
    }

    public void initView(View view) {

        sportMile = (TextView) view.findViewById(R.id.tv_sport_mile);
        sportMile.setText("2.78");

        sportCount = (TextView) view.findViewById(R.id.tv_sport_count);
        sportCount.setText("15");

        sportTime = (TextView) view.findViewById(R.id.tv_sport_time);
        sportTime.setText("1000.67");

        startBtn = (Button) view.findViewById(R.id.btnStart);
        startBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent goSportMapIntent = new Intent(getActivity(),
                        SportMap_Activity.class);
                startActivity(goSportMapIntent);
            }
        });
    }
}
