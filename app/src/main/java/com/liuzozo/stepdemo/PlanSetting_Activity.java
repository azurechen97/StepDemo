package com.liuzozo.stepdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 运动计划设置页面
 * 弹框  设置每天跑步目标公里， 是否提醒， 提醒时间
 */
public class PlanSetting_Activity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener {

    private static Context sContext = null;
    private EditText time;
    private Switch alarmSwitch;
    private String mTime;
    private String mDate;

    public static Context getContext() {
        return sContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_setting);
        sContext = this;
        initView();
        init();
    }

    private void initView() {
        time = (EditText) findViewById(R.id.alarm_time);
        alarmSwitch = (Switch) findViewById(R.id.alarm_switch);
    }

    private void init() {
        //date.setOnClickListener(this);
        //time.setOnClickListener(this);
        alarmSwitch.setOnCheckedChangeListener(this);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.alarm_switch:
                if (isChecked) {
                    Date curDate = new Date(System.currentTimeMillis());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    mDate = dateFormat.format(curDate); //当前日期

                    mTime = time.getText().toString();
                    Log.e("xx", "日期= " + mDate + "  时间= " + mTime);
                    if (TextUtils.isEmpty(mTime)) {
                        Toast.makeText(getApplicationContext(), "请选择提醒时间",
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date date;
                    long value = 0;
                    String str_date = mDate + " " + mTime;
                    try {
                        date = sdf.parse(str_date);
                        value = date.getTime();
                        System.out.println("当前设置时间:" + value);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Log.e("str_date=", str_date);
                    Log.e("value=", value + "");
                    long value2 = System.currentTimeMillis();
                    Log.e("Current Time", value2 + "");
                    if (value <= value2) {
                        value += 24 * 3600 * 1000;
                        Toast.makeText(getApplicationContext(), "今天该时间已过",
                                Toast.LENGTH_SHORT).show();
                    }
                    int delayTime = (int) (value - value2);
//                    AlarmService.addNotification(delayTime,
//                            "tick","title","text");
                    Intent intent = new Intent(PlanSetting_Activity.this,
                            AlarmService.class);
                    intent.putExtra("delayTime", delayTime);
                    intent.putExtra("tickerText", "tick");
                    intent.putExtra("contentTitle", "title");
                    intent.putExtra("contentText", "text");
                    startService(intent);
                    Log.e("Service", "started");
                } else {
                    AlarmService.cleanAllNotification();
                }

                break;

            default:
                throw new IllegalStateException("Unexpected value: " + buttonView.getId());

        }

    }


}
