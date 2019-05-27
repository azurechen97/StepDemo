package com.liuzozo.stepdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;

import com.amap.api.maps.model.MyLocationStyle;
import com.liuzozo.stepdemo.bean.PathRecord;
import com.liuzozo.stepdemo.fragment.MyCountTimer;
import com.liuzozo.stepdemo.ui.UIHelperUtil;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 开始跑步页面  ,分跑步模式和地图模式
 *
 *    当开始跑步时 倒计时3秒，
 *    同时利用 PathRecord 设置 startTime 、记录路径的经纬度，
 *    可以通过记录的两个经纬度的距离计算跑了多少公里
 *    数据库里面保存:
 *    当前时间的时间戳，  距离， 时长， 开始时间， 结束时间， 开始位置的经纬度， 结束位置的经纬度，路线的经纬度
 *    根据体重计算的卡路里， 平均时速（公里/小时） 平均配速（分钟/公里）
 *
 *  tips:
 *  onLocationChanged 可以每隔几秒钟获得一次当前位置的经纬度，你可以把它保存到List 里面，
 *  当跑完步把list 保存到PathRecord 类，从而保存到数据库
 */

public class SportMap_Activity extends AppCompatActivity implements
        LocationSource, AMapLocationListener, View.OnClickListener {


    private AMap aMap;  // 地图
    private MapView mapView;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;


    TextView tv_modeText; // 地图模式与跑步模式控件

    RelativeLayout rlMap; // 控制地图布局显示与否
    boolean mode = true; // true 跑步模式, false 地图模式

    PathRecord pathRecord; // PathRecord 类记录本次运行的所有路径

    TextView tvSportComplate;
    TextView tvSportPause;
    TextView tvSportContinue;

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    private static final int PERMISSON_REQUESTCODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_map);

        // 初始化地图组件
        mapView = findViewById(R.id.stepMap);
        mapView.onCreate(savedInstanceState);//

        Button btnCountTimer = (Button) findViewById(R.id.btnCountTimer);
        LinearLayout countBackground = (LinearLayout) findViewById(R.id.countBackground);
        //倒计时总时间为10S，时间防止从9s开始显示
        MyCountTimer myCountTimer = new MyCountTimer(4000, 1000,
                btnCountTimer, "End", countBackground);
        myCountTimer.start();

        initMap();
        initView();

    }

    private void initView() {
        // 点击时隐藏|显示地图
        tv_modeText = findViewById(R.id.tv_mode);
        tv_modeText.setOnClickListener(this);

        rlMap = findViewById(R.id.rlMap);
        rlMap.setVisibility(View.GONE); // 刚开始不现实地图

        tvSportComplate = findViewById(R.id.tv_sport_complate);
        tvSportComplate.setOnClickListener(this);
    }


    /**
     * 每个按钮的点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_sport_complate:
                Intent intent = new Intent(this, SportResult_Activity.class);
                startActivity(intent);
                break;
            case R.id.tv_mode:
                if (mode) {
                    rlMap.setVisibility(View.VISIBLE);
                    tv_modeText.setText("跑步模式");
                    UIHelperUtil.setLeftDrawable(tv_modeText, ContextCompat.getDrawable(this, R.mipmap.run_mode));
                    mode = false;
                } else {
                    rlMap.setVisibility(View.GONE);
                    tv_modeText.setText("地图模式");
                    UIHelperUtil.setLeftDrawable(tv_modeText, ContextCompat.getDrawable(this, R.mipmap.map_mode));
                    mode = true;
                }
            default:
                break;
        }
    }

    /**
     * 地图开始
     */
    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            // 缩放级别
            aMap.moveCamera(CameraUpdateFactory.zoomTo(20));
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.getUiSettings().setCompassEnabled(true);// 设置显示指南针
            setUpMap();
        }
    }


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 设置定位回调
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.strokeColor(Color.TRANSPARENT); // 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0)); // 设置圆形的填充颜色
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }


    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            // 初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听 属性
            // 可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
            // mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位间隔 5s
            mLocationOption.setInterval(5000);
            mLocationClient.setLocationOption(mLocationOption);
            // 设置定位监听
            mLocationClient.startLocation();
        }
    }

    // 停止定位
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;

    }

    // 定位成功后 会 每 5秒钟 回调函数
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            // code 12 is no premission
            Log.i("TAG", aMapLocation.getErrorCode() + "");
            if (aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);// 显示系统圆点

                // 每5秒打印一次 经度纬度 地MyLocationStyle 址 城市
                // pathRecord.addpoint(new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude() ));
                Log.i("TAG", aMapLocation.getLatitude() + "   :   " + aMapLocation.getLongitude());
                Log.i("TAG", aMapLocation.getAddress());
                Log.i("TAG", aMapLocation.getCity());
                // 根据这些经纬度可以在地图上每五秒画点线，最终画出轨迹
            }
        } else {
            String errText = "定位失败" + aMapLocation.getErrorCode() + ":" + aMapLocation.getErrorInfo();
            Log.e("AmapError", errText);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        setNeedCheckPermission();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出销毁Map
        mapView.onDestroy();
    }




    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    /**
     * 检查手机是否开启定位权限，没开启会提醒开启
     */
    private void setNeedCheckPermission() {

        if (Build.VERSION.SDK_INT >= 23
                && getApplicationInfo().targetSdkVersion >= 23) {
            if (isNeedCheck) {
                checkPermissions(needPermissions);
            }
        }
    }

    /**
     * 检查定位权限
     *
     * @param permissions
     * @since 2.5.0
     */
    private void checkPermissions(String... permissions) {
        try {
            if (Build.VERSION.SDK_INT >= 23
                    && getApplicationInfo().targetSdkVersion >= 23) {
                List<String> needRequestPermissonList = findDeniedPermissions(permissions);
                if (null != needRequestPermissonList
                        && needRequestPermissonList.size() > 0) {
                    String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
                    Method method = getClass().getMethod("requestPermissions", new Class[]{String[].class,
                            int.class});

                    method.invoke(this, array, PERMISSON_REQUESTCODE);
                }
            }
        } catch (Throwable e) {
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= 23
                && getApplicationInfo().targetSdkVersion >= 23) {
            try {
                for (String perm : permissions) {
                    Method checkSelfMethod = getClass().getMethod("checkSelfPermission", String.class);
                    Method shouldShowRequestPermissionRationaleMethod = getClass().getMethod("shouldShowRequestPermissionRationale",
                            String.class);
                    if ((Integer) checkSelfMethod.invoke(this, perm) != PackageManager.PERMISSION_GRANTED
                            || (Boolean) shouldShowRequestPermissionRationaleMethod.invoke(this, perm)) {
                        needRequestPermissonList.add(perm);
                    }
                }
            } catch (Throwable e) {

            }
        }
        return needRequestPermissonList;
    }


}
