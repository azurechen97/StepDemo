package com.aoxue.stepdemo.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aoxue.stepdemo.MainActivity;
import com.aoxue.stepdemo.R;
import com.aoxue.stepdemo.SportMap_Activity;
import com.aoxue.stepdemo.utils.MyDatabaseHelper;

import java.text.DecimalFormat;

import static android.content.Context.MODE_PRIVATE;

/**
 * 点击开发运动的 界面
 * 需要 查询数据库，查询出所有的跑步次数，跑步总公里，总时间
 */
public class Sport_Fragment extends Fragment {

    // 开始运动按钮
    private Button startBtn;
    private TextView sportMile;
    private TextView sportCount;
    private TextView sportTime;
    private MyDatabaseHelper databaseHelper;

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater
                .inflate(R.layout.fragment_sport, container, false);
        initView(view);
        return view;
    }

    public void initView(View view) {
        //首先查询获得主界面所用数据
        double distance = 0;
        long duration = 0;
        int count = 0;

        databaseHelper = new MyDatabaseHelper(
                getActivity(), "sport_record.db", null, 1);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        //查询 用db.query  返回一个Cursor 里面有数据
        Cursor cursor = db.query("sport_record",
                null, null, null,
                null, null, null);

        //查之前先把Cursor位置移到第一   从第一条开始查
        boolean succeed = (cursor.moveToFirst());

        if (succeed) {

            do {
                //先判断字段的数据类型 如果是String 就用 cursor.getString
                // 是Double 就用 cursor.getDouble  以此类推
                //cursor.getString函数需要传入 ColumnIndex
                //用 cursor.getColumnIndex 传入字段名

                distance += cursor.getDouble(cursor.getColumnIndex("distance"));
                duration += cursor.getLong(cursor.getColumnIndex("duration"));
                count = cursor.getCount();

                //查完一条之后调用cursor.moveToNext()把cursor的位置移动到下一条
            } while (cursor.moveToNext());

        }
        //全部查完后  把cursor关闭
        cursor.close();

        sportMile = (TextView) view.findViewById(R.id.tv_sport_mile);
        sportMile.setText(decimalFormat.format(distance / 1000));

        sportCount = (TextView) view.findViewById(R.id.tv_sport_count);
        sportCount.setText(count + "");

        sportTime = (TextView) view.findViewById(R.id.tv_sport_time);
        sportTime.setText(decimalFormat.format((double) duration / 60000));

        //开始按钮相关操作
        startBtn = (Button) view.findViewById(R.id.btnStart);

        startBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                final MainActivity mainActivity = (MainActivity) getActivity();

                SharedPreferences preferences = getActivity().getSharedPreferences(
                        "BMI-data", MODE_PRIVATE);
                if (preferences.contains("height") & preferences.contains("weight")) {
                    Intent goSportMapIntent = new Intent(getActivity(),
                            SportMap_Activity.class);
                    startActivity(goSportMapIntent);
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("检测到首次使用")
                            .setMessage("第一次使用需要设置身高及体重以便计算卡路里消耗。现在就去设置吗？")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mainActivity.setTab(2);
                                }
                            })
                            .setNegativeButton("否", null)
                            .show();
                }
            }
        });
    }

}
