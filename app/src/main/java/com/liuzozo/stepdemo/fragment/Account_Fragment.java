package com.liuzozo.stepdemo.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liuzozo.stepdemo.PlanSetting_Activity;
import com.liuzozo.stepdemo.R;
import com.liuzozo.stepdemo.TuLinTalk_Activity;
import com.liuzozo.stepdemo.WeekRecord_Activity;

import static android.content.Context.MODE_PRIVATE;

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
    TextView tvHeight;
    TextView tvWeight;
    TextView tvBMI;


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
        tvHeight = view.findViewById(R.id.tv_height);
        tvWeight = view.findViewById(R.id.tv_weight);
        tvBMI = view.findViewById(R.id.tv_bmi);
        // 设置对应的监听事件
        planSettingLayout.setOnClickListener(this);
        weekRecordLayout.setOnClickListener(this);
        talkLayout.setOnClickListener(this);
        tvHeight.setOnClickListener(this);
        tvWeight.setOnClickListener(this);
        tvBMI.setOnClickListener(this);

        SharedPreferences preferences = getActivity().getSharedPreferences(
                "BMI-data", MODE_PRIVATE);
        float height = preferences.getFloat("height", (float) 175.);
        float weight = preferences.getFloat("weight", (float) 50.);
        tvHeight.setText(getString(R.string.xliff_height, height));
        tvWeight.setText(getString(R.string.xliff_weight, weight));
        tvBMI.setText(getString(R.string.xliff_bmi, weight / height / height * 10000));

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

            case R.id.tv_height:
                final SharedPreferences.Editor editor1 = getActivity().getSharedPreferences(
                        "BMI-data", MODE_PRIVATE).edit();
                final EditText inputServer1 = new EditText(getActivity());

                new AlertDialog.Builder(getActivity())
                        .setTitle("请输入您的身高")
                        .setView(inputServer1)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor1.putFloat("height",
                                        Float.parseFloat(inputServer1.getText().toString()));
                                editor1.apply();

                                getFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.id_nav_table_content, new Account_Fragment())
                                        .commitNow();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;

            case R.id.tv_weight:
                final SharedPreferences.Editor editor2 = getActivity().getSharedPreferences(
                        "BMI-data", MODE_PRIVATE).edit();
                final EditText inputServer2 = new EditText(getActivity());

                new AlertDialog.Builder(getActivity()).setTitle("请输入您的体重")
                        .setView(inputServer2)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor2.putFloat("weight",
                                        Float.parseFloat(inputServer2.getText().toString()));
                                editor2.apply();

                                getFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.id_nav_table_content, new Account_Fragment())
                                        .commitNow();

                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;

            case R.id.tv_bmi:
                final SharedPreferences.Editor editor3 = getActivity().getSharedPreferences(
                        "BMI-data", MODE_PRIVATE).edit();

                new AlertDialog.Builder(getActivity()).setTitle("确定要清除数据吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor3.remove("height");
                                editor3.remove("weight");
                                editor3.apply();

                                getFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.id_nav_table_content, new Account_Fragment())
                                        .commitNow();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;

            default:
                break;
        }
    }


}
