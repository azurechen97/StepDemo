package com.liuzozo.stepdemo.fragment;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.liuzozo.stepdemo.PlanSetting_Activity;
import com.liuzozo.stepdemo.R;
import com.liuzozo.stepdemo.TuLinTalk_Activity;
import com.liuzozo.stepdemo.WeekRecord_Activity;

/**
 * 个人信息页面
 *  当用户首次点击跑步的时侯，需要检测SharedPreference 里面是否存储了 运动标语， 身高、体重，并计算BMI，
 *   没有存储这些值的时侯，不能让他跳转到跑步页面
 *
 *   点击头像区域，设置头像
 *   点击头像左边区域，设置运动标语， 身高，体重 ， 保存SharedPreference 里面后，计算BMI 值显示到界面
 *
 *   点击跑步设置，一周记录，悦聊 分别跳转到对应页面
 */

public class Account_Fragment extends Fragment implements View.OnClickListener {

    // 三个Layout
    LinearLayout planSettingLayout;
    LinearLayout weekRecordLayout;
    LinearLayout talkLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container,
                false);


        initView(view);
        return view;
    }

    private void initView(View view) {
        planSettingLayout = view.findViewById(R.id.plan_setting_id);
        weekRecordLayout = view.findViewById(R.id.week_record_id);
        talkLayout = view.findViewById(R.id.talk_id);
        // 设置对应的监听事件
        planSettingLayout.setOnClickListener(this);
        weekRecordLayout.setOnClickListener(this);
        talkLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.plan_setting_id:
                Intent goPlan = new Intent(getContext(), PlanSetting_Activity.class);
                startActivity(goPlan);
                break;
            case R.id.week_record_id:
                Intent goWeek = new Intent(getContext(), WeekRecord_Activity.class);
                startActivity(goWeek);
                break;
            case R.id.talk_id:
                Intent goTalk = new Intent(getContext(), TuLinTalk_Activity.class);
                startActivity(goTalk);
                break;

            default:
                break;
        }
    }


}
