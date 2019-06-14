package com.aoxue.stepdemo.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aoxue.stepdemo.utils.ConstantUtils;
import com.aoxue.stepdemo.utils.MyDatabaseHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.aoxue.stepdemo.PlanSetting_Activity;
import com.aoxue.stepdemo.R;
import com.aoxue.stepdemo.Settings_Activity;
import com.aoxue.stepdemo.TuLinTalk_Activity;
import com.aoxue.stepdemo.WeekRecord_Activity;
import com.aoxue.stepdemo.utils.PermissionUtils;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.text.DecimalFormat;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
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
    LinearLayout sloganLayout;
    TextView tvHeight;
    TextView tvWeight;
    TextView tvBMI;
    TextView accountDistance;
    TextView username;
    TextView slogan;


//    LayoutInflater layoutInflater;
//    View dialogView;
//    TextView unit;
//    EditText editText;

    SharedPreferences.Editor editor;
    SharedPreferences.Editor editorSettings;

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
        accountDistance = view.findViewById(R.id.account_distance);
        username = view.findViewById(R.id.username);
        slogan = view.findViewById(R.id.slogan);
        sloganLayout = view.findViewById(R.id.slogan_layout);

        // 设置对应的监听事件
        planSettingLayout.setOnClickListener(this);
        weekRecordLayout.setOnClickListener(this);
        talkLayout.setOnClickListener(this);
        tvHeight.setOnClickListener(this);
        tvWeight.setOnClickListener(this);
        tvBMI.setOnClickListener(this);
        icon.setOnClickListener(this);
        username.setOnClickListener(this);
        sloganLayout.setOnClickListener(this);


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

        if (settings.contains("username")) {
            String user = settings.getString("username", null);
            username.setText(user);
        }

        if (settings.contains("slogan")) {
            String slog = settings.getString("slogan", null);
            slogan.setText(slog);
        }

        editorSettings = getActivity().getSharedPreferences("settings", MODE_PRIVATE).edit();
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
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View dialogView = layoutInflater.inflate(R.layout.dialog_parameters, null);
        TextView unit = (TextView) dialogView.findViewById(R.id.unit);
        final EditText editText = (EditText) dialogView.findViewById(R.id.editText);

        View dialogView2 = layoutInflater.inflate(R.layout.dialog_no_parameters, null);
        final EditText editText2 = (EditText) dialogView2.findViewById(R.id.editText2);

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
                                String heightStr = editText.getText().toString();
                                if (heightStr.length() != 0) {
                                    editor.putFloat("height",
                                            Float.parseFloat(heightStr));
                                    editor.apply();
                                }

                                initShared();

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
                                String weightStr = editText.getText().toString();
                                if (weightStr.length() != 0) {
                                    editor.putFloat("weight",
                                            Float.parseFloat(weightStr));
                                    editor.apply();
                                }

                                initShared();
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

                                initShared();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;

            case R.id.iv_icon:
                setAvatar();
                break;

            case R.id.username:
                editText2.setMaxLines(1);
                editText2.getLayoutParams().width = 250;
                new AlertDialog.Builder(getActivity())
                        .setTitle("请设置一个用户名吧!")
                        .setView(dialogView2)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String usernameStr = editText2.getText().toString();
                                if (usernameStr.length() != 0) {
                                    editorSettings.putString("username",
                                            usernameStr);
                                }

                                editorSettings.apply();

                                initShared();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;

            case R.id.slogan_layout:
                new AlertDialog.Builder(getActivity())
                        .setTitle("请设置一句话的签名吧!")
                        .setView(dialogView2)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String sloganStr = editText2.getText().toString();
                                if (sloganStr.length() != 0) {
                                    editorSettings.putString("slogan",
                                            sloganStr);
                                }

                                editorSettings.apply();

                                initShared();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;

            default:
                break;
        }
    }

    private void setAvatar() {
        Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent1.setType("image/*");

        Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = new File(Environment.getExternalStorageDirectory(), ConstantUtils.HEAD_PICTURE);
        Uri uri = Uri.fromFile(file);

        intent2.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        Intent chooser = Intent.createChooser(intent1, "选择头像");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent2});
        startActivityForResult(chooser, 101);// --------------->101
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == 101) {
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                } else {
                    // 相机拍照
                    uri = Uri.fromFile(ConstantUtils.bgFile);
                }

                RequestListener mRequestListener = new RequestListener() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e,
                                                Object model, Target target, boolean isFirstResource) {
                        Log.e("Glide", "onException: " + e.toString()
                                + "  model:" + model + " isFirstResource: " + isFirstResource);
                        icon.setImageResource(R.mipmap.icon_app);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Object resource, Object model,
                                                   Target target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        Log.e("Glide", "model:" + model + " isFirstResource: " + isFirstResource);
                        return false;
                    }
                };

                if (uri != null) {
                    editorSettings.putString("portrait", uri.toString());
                    editorSettings.apply();
                    Glide.with(this)
                            .load(uri)
                            .placeholder(R.mipmap.akkarin)
                            .error(R.mipmap.icon_app)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .override(300, 300)//指定图片大小
                            .addListener(mRequestListener)
                            .into(icon);
                }

            }
        }

    }

}

