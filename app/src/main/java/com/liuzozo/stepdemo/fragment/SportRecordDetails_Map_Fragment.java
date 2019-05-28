package com.liuzozo.stepdemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.liuzozo.stepdemo.R;

/***
 *  运动地图记录详情页
 *  1. 在地图上根据传来的PathRecord 值的经纬度等信息，画出轨迹等
 */
public class SportRecordDetails_Map_Fragment extends Fragment implements LocationSource {

    // 地图
    private AMap aMap;
    private MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sport_details_map, container,
                false);

        // 初始化地图组件
        mapView = view.findViewById(R.id.detalsMap);
        mapView.onCreate(savedInstanceState);

        initSendData();
        initMap();
        return view;
    }

    private void initSendData() {

        // 得到转到这个 fragmnet 的值
//        Bundle receiverBundle = getArguments();
//        if (receiverBundle != null) {
//            pathRecord = receiverBundle.getParcelable("SPORT_DATA");
//        }
    }

    /**
     * 地图开始
     */
    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            // 缩放级别
            aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
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
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {


    }

    @Override
    public void deactivate() {

    }
}
