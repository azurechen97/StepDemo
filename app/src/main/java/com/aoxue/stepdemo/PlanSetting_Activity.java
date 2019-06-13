package com.aoxue.stepdemo;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 运动计划设置页面
 * 弹框  设置每天跑步目标公里， 是否提醒， 提醒时间
 */
public class PlanSetting_Activity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static Context sContext = null;
    private TextView time;
    private EditText mileage;
    private SwitchCompat alarmSwitch;
    private String mTime;
    private String mDate;
    private String mMileage;
    private RelativeLayout timeLayout;

    private SharedPreferences.Editor editor;

    private DecimalFormat format = new DecimalFormat("00");


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
        time = (TextView) findViewById(R.id.alarm_time);
        mileage = (EditText) findViewById(R.id.alarm_mileage);
        alarmSwitch = (SwitchCompat) findViewById(R.id.alarm_switch);
        timeLayout = (RelativeLayout) findViewById(R.id.time_layout);

        SharedPreferences preferences = getSharedPreferences(
                "plan-setting", MODE_PRIVATE);

        if (preferences.contains("checked"))
            alarmSwitch.setChecked(preferences.getBoolean("checked", false));
        if (preferences.contains("mileage"))
            mileage.setText(preferences.getString("mileage", null));
        if (preferences.contains("time"))
            time.setText(preferences.getString("time", null));

    }

    private void init() {
        //date.setOnClickListener(this);
        timeLayout.setOnClickListener(this);
        alarmSwitch.setOnCheckedChangeListener(this);

        editor = getSharedPreferences(
                "plan-setting", MODE_PRIVATE).edit();

        //当改变文字时取消原有设置
        mileage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                alarmSwitch.setChecked(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        time.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                alarmSwitch.setChecked(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
                    mMileage = mileage.getText().toString();

                    editor.putString("mileage", mMileage);
                    editor.putString("time", mTime);
                    editor.putBoolean("checked", true);
                    editor.apply();

                    Log.e("xx", "日期= " + mDate + "  时间= " + mTime);
//                    if (TextUtils.isEmpty(mTime)) {
//                        Toast.makeText(getApplicationContext(), "请选择提醒时间",
//                                Toast.LENGTH_SHORT).show();
//                        alarmSwitch.setChecked(false);
//                        break;
//                    }
                    if (TextUtils.isEmpty(mMileage)) {
                        Toast.makeText(getApplicationContext(), "请设置计划跑步距离",
                                Toast.LENGTH_SHORT).show();
                        alarmSwitch.setChecked(false);
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
                        Toast.makeText(getApplicationContext(), "今天该时间已过，从明天开始",
                                Toast.LENGTH_SHORT).show();
                    }
//                    int delayTime = (int) (value - value2);
                    AlarmService.addNotification(value,
                            "跑步时间到！今天您计划跑" + mMileage + "米",
                            "乐跑圈闹钟",
                            "跑步时间到！今天您计划跑" + mMileage + "米");
                } else {
                    editor.putBoolean("checked", false);
                    editor.apply();
                    AlarmService.cleanAllNotification();
                }

                break;

            default:
                throw new IllegalStateException("Unexpected value: " + buttonView.getId());

        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.time_layout:

                Calendar mCurrentTime = Calendar.getInstance();
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time.setText(format.format(selectedHour) + ":" + format.format(selectedMinute));
                    }
                }, hour, minute, true).show();

                break;


            default:
                break;
        }
    }
}

