package com.liuzozo.stepdemo.fragment;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.liuzozo.stepdemo.utils.MyDatabaseHelper;

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
        double distance = 0;
        long duration = 0;
        int count = 0;

        MyDatabaseHelper databaseHelper = new MyDatabaseHelper(
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
                count += 1;

                //查完一条之后调用cursor.moveToNext()把cursor的位置移动到下一条
            } while (cursor.moveToNext());

        }
        //全部查完后  把cursor关闭
        cursor.close();

        sportMile = (TextView) view.findViewById(R.id.tv_sport_mile);
        sportMile.setText(distance + "");

        sportCount = (TextView) view.findViewById(R.id.tv_sport_count);
        sportCount.setText(count + "");

        sportTime = (TextView) view.findViewById(R.id.tv_sport_time);
        sportTime.setText(duration + "");

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
