package com.aoxue.stepdemo.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aoxue.stepdemo.utils.MyDatabaseHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.aoxue.stepdemo.PlanSetting_Activity;
import com.aoxue.stepdemo.R;
import com.aoxue.stepdemo.Settings_Activity;
import com.aoxue.stepdemo.TuLinTalk_Activity;
import com.aoxue.stepdemo.WeekRecord_Activity;
import com.aoxue.stepdemo.utils.PermissionUtils;

import java.text.DecimalFormat;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * 个人信息页面
 * 当用户首次点击跑步的时侯，需要检测SharedPreference 里面是否存储了 运动标语， 身高、体重，并计算BMI，
 * 没有存储这些值的时侯，不能让他跳转到跑步页面
 * <p>
 * 点击头像区域，设置头像
 * 点击头像左边区域，设置运动标语， 身高，体重 ， 保存SharedPreference 里面后，计算BMI 值显示到界面
 * <p>
 * 点击跑步设置，一周记录，悦聊 分别跳转到对应页面
 */

public class Account_Fragment extends Fragment implements View.OnClickListener {

    // 三个Layout
    LinearLayout planSettingLayout;
    LinearLayout weekRecordLayout;
    LinearLayout talkLayout;
    TextView tvHeight;
    TextView tvWeight;
    TextView tvBMI;
    TextView settingBtn;
    TextView accountDistance;

    LayoutInflater layoutInflater;
    View dialogView;
    TextView unit;
    EditText editText;

    SharedPreferences.Editor editor;

    private CircleImageView icon;

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private DecimalFormat oneDecimal = new DecimalFormat("0.0");
    private DecimalFormat intFormat = new DecimalFormat("#");

    private MyDatabaseHelper databaseHelper;
    private double totalDistance = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container,
                false);
        PermissionUtils.verifyStoragePermissions(getActivity());
        initView(view);
        initShared();
        initDB();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initShared();
    }

    private void initView(View view) {
        planSettingLayout = view.findViewById(R.id.plan_setting_id);
        weekRecordLayout = view.findViewById(R.id.week_record_id);
        talkLayout = view.findViewById(R.id.talk_id);
        tvHeight = view.findViewById(R.id.tv_height);
        tvWeight = view.findViewById(R.id.tv_weight);
        tvBMI = view.findViewById(R.id.tv_bmi);
        icon = (CircleImageView) view.findViewById(R.id.iv_icon);
        settingBtn = view.findViewById(R.id.setting_btn);
        accountDistance = view.findViewById(R.id.account_distance);

        // 设置对应的监听事件
        planSettingLayout.setOnClickListener(this);
        weekRecordLayout.setOnClickListener(this);
        talkLayout.setOnClickListener(this);
        tvHeight.setOnClickListener(this);
        tvWeight.setOnClickListener(this);
        tvBMI.setOnClickListener(this);
        icon.setOnClickListener(this);
        settingBtn.setOnClickListener(this);

        layoutInflater = LayoutInflater.from(getActivity());
        dialogView = layoutInflater.inflate(R.layout.dialog_parameters, null);
        unit = (TextView) dialogView.findViewById(R.id.unit);
        editText = (EditText) dialogView.findViewById(R.id.editText);

    }

    private void initShared() {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                "BMI-data", MODE_PRIVATE);
        float height = preferences.getFloat("height", (float) 175.);
        float weight = preferences.getFloat("weight", (float) 50.);
        if (preferences.contains("height"))
            tvHeight.setText(intFormat.format(height));
        if (preferences.contains("weight"))
            tvWeight.setText(intFormat.format(weight));
        if (preferences.contains("height") && preferences.contains("weight"))
            tvBMI.setText(oneDecimal.format(weight / height / height * 10000));

        editor = getActivity().getSharedPreferences(
                "BMI-data", MODE_PRIVATE).edit();

        SharedPreferences settings = getActivity().getSharedPreferences(
                "settings", MODE_PRIVATE);
        if (settings.contains("portrait")) {
            String path = settings.getString("portrait", null);
            Uri uri = Uri.parse(path);

            Glide.with(this)
                    .load(uri)
                    .placeholder(R.mipmap.akkarin)
                    .error(R.mipmap.icon_app)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .override(300, 300)//指定图片大小
                    .into(icon);
        }
    }

    private void initDB() {

        totalDistance = 0.;

        databaseHelper = new MyDatabaseHelper(
                getActivity(), "sport_record.db", null, 1);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        Cursor cursor = db.query("sport_record",
                null, null, null,
                null, null, null);

        boolean succeed = (cursor.moveToFirst());

        if (succeed) {

            do {
                totalDistance += cursor.getDouble(cursor.getColumnIndex("distance"));
            } while (cursor.moveToNext());

        }
        cursor.close();

        accountDistance.setText(decimalFormat.format(totalDistance / 1000));
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
                unit.setText(" cm");

                new AlertDialog.Builder(getActivity())
                        .setTitle("请输入您的身高")
                        .setView(dialogView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor.putFloat("height",
                                        Float.parseFloat(editText.getText().toString()));
                                editor.apply();

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
                unit.setText(" kg");

                new AlertDialog.Builder(getActivity())
                        .setTitle("请输入您的体重")
                        .setView(dialogView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor.putFloat("weight",
                                        Float.parseFloat(editText.getText().toString()));
                                editor.apply();

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
                new AlertDialog.Builder(getActivity()).setTitle("确定要清除身体数据吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor.remove("height");
                                editor.remove("weight");
                                editor.apply();

                                getFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.id_nav_table_content, new Account_Fragment())
                                        .commitNow();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;

            case R.id.setting_btn:
                Intent intent = new Intent(getActivity(),
                        Settings_Activity.class);
                startActivity(intent);
                break;

            case R.id.iv_icon:

            default:
                break;
        }
    }

}

