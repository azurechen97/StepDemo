package com.liuzozo.stepdemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liuzozo.stepdemo.R;
import com.liuzozo.stepdemo.bean.PathRecord;
import com.liuzozo.stepdemo.utils.StepUtils;

import java.nio.file.Path;
import java.text.DecimalFormat;

import static com.liuzozo.stepdemo.SportRecordDetails_Activity.SPORT_DATA;

/***
 *  运动记录详情页 --- 不带地图的页面
 *  1. 根据传来的PathRecord 值的显示一些信息，见PPT 该页面
 */
public class SportRecordDetails_Fragment extends Fragment {

    TextView detailDistance;
    TextView detailDuration;
    TextView detailSpeed;
    TextView detailDistribution;
    TextView detailCalorie;

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private DecimalFormat intFormat = new DecimalFormat("#");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sport_details, container,
                false);

        initView(view);

        Bundle bundle = this.getArguments();
        PathRecord pathRecord = new PathRecord();
        if (bundle != null)
            pathRecord = bundle.getParcelable(SPORT_DATA);

        detailDistance.setText(decimalFormat.format(pathRecord.getDistance() / 1000));
        detailDuration.setText(StepUtils.formatMilliseconds(pathRecord.getDuration()));
        detailSpeed.setText(decimalFormat.format(pathRecord.getSpeed()));
        detailDistribution.setText(decimalFormat.format(pathRecord.getDistribution()));
        detailCalorie.setText(intFormat.format(pathRecord.getCalorie()));

        return view;
    }

    private void initView(View view) {
        detailDistance = view.findViewById(R.id.detail_distance);
        detailDuration = view.findViewById(R.id.detail_duration);
        detailSpeed = view.findViewById(R.id.detail_speed);
        detailDistribution = view.findViewById(R.id.detail_distribution);
        detailCalorie = view.findViewById(R.id.detail_calorie);
    }
}
