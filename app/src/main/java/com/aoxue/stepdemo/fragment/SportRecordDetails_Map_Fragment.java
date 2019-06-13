package com.aoxue.stepdemo.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.aoxue.stepdemo.R;
import com.aoxue.stepdemo.bean.PathRecord;
import com.aoxue.stepdemo.utils.PathSmoothTool;
import com.aoxue.stepdemo.utils.StepUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.aoxue.stepdemo.SportRecordDetails_Activity.SPORT_DATA;

/***
 *  运动地图记录详情页
 *  1. 在地图上根据传来的PathRecord 值的经纬度等信息，画出轨迹等
 */
public class SportRecordDetails_Map_Fragment extends Fragment
        implements LocationSource, CompoundButton.OnCheckedChangeListener {

    // 地图
    private AMap aMap;
    private MapView mapView;
    private List<LatLng> pathLine = new ArrayList<LatLng>();

    TextView detailMapDistance;
    TextView detailMapDuration;

    private Polyline mOriginPolyline, mKalmanPolyline;
    private CheckBox mKalmanBtn;
    private PathSmoothTool mPathSmoothTool;

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sport_details_map, container,
                false);

        init(view);

        // 初始化地图组件
        mapView = view.findViewById(R.id.detalsMap);
        mapView.onCreate(savedInstanceState);

        initData();
        initMap();
        mPathSmoothTool = new PathSmoothTool();
        mPathSmoothTool.setIntensity(4);
        addPathLine();

        return view;
    }

    public void init(View view) {
//        mOriginBtn = (CheckBox) view.findViewById(R.id.record_show_activity_origin_button);
        mKalmanBtn = (CheckBox) view.findViewById(R.id.detail_kalman);
//        mOriginBtn.setOnCheckedChangeListener(this);
        mKalmanBtn.setOnCheckedChangeListener(this);
        detailMapDistance = (TextView) view.findViewById(R.id.detail_map_distance);
        detailMapDuration = (TextView) view.findViewById(R.id.detail_map_duration);
    }

    private void initData() {
        Bundle bundle = this.getArguments();
        PathRecord pathRecord = new PathRecord();
        if (bundle != null)
            pathRecord = bundle.getParcelable(SPORT_DATA);

        pathLine = pathRecord.getPathLine();
        detailMapDistance.setText(decimalFormat.format(
                pathRecord.getDistance() / 1000) + " 公里");
        detailMapDuration.setText(StepUtils.formatMilliseconds(pathRecord.getDuration()));
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
            aMap.getUiSettings().setMyLocationButtonEnabled(false);
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

    private void addPathLine() {

        if (pathLine != null && pathLine.size() > 0) {
            mOriginPolyline = aMap.addPolyline(new PolylineOptions().addAll(pathLine).color(Color.GREEN));
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBounds(pathLine), 200));
//            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mOriginList.get(0),15));
        }
        pathOptimize(pathLine);
        mOriginPolyline.setVisible(true);
        mKalmanPolyline.setVisible(false);
    }

    private LatLngBounds getBounds(List<LatLng> pointlist) {
        LatLngBounds.Builder b = LatLngBounds.builder();
        if (pointlist == null) {
            return b.build();
        }
        for (int i = 0; i < pointlist.size(); i++) {
            b.include(pointlist.get(i));
        }
        return b.build();

    }

    public List<LatLng> pathOptimize(List<LatLng> originList) {
        List<LatLng> pathOptimizeList = mPathSmoothTool.pathOptimize(originList);
        mKalmanPolyline = aMap.addPolyline(new PolylineOptions().addAll(pathOptimizeList)
                .color(Color.parseColor("#FFC125")));
        return pathOptimizeList;
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {


    }

    @Override
    public void deactivate() {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id) {
            case R.id.detail_kalman:
                if (mOriginPolyline != null) {
                    mOriginPolyline.setVisible(!isChecked);
                }
                if (mKalmanPolyline != null) {
                    mKalmanPolyline.setVisible(isChecked);
                }
                break;
        }

    }
}
