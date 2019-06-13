package com.aoxue.stepdemo.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aoxue.stepdemo.MainActivity;
import com.aoxue.stepdemo.R;
import com.aoxue.stepdemo.bean.PathRecord;
import com.aoxue.stepdemo.utils.DBUtils;
import com.aoxue.stepdemo.utils.MyDatabaseHelper;
import com.aoxue.stepdemo.utils.StepUtils;

import java.text.DecimalFormat;

import static com.aoxue.stepdemo.SportRecordDetails_Activity.SPORT_DATA;

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

    private Button deleteRecord;

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private DecimalFormat intFormat = new DecimalFormat("#");

    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    PathRecord pathRecord;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sport_details, container,
                false);

        initDB();

        initView(view);

        Bundle bundle = this.getArguments();
        pathRecord = new PathRecord();
        if (bundle != null)
            pathRecord = bundle.getParcelable(SPORT_DATA);

        detailDistance.setText(decimalFormat.format(pathRecord.getDistance() / 1000));
        detailDuration.setText(StepUtils.formatMilliseconds(pathRecord.getDuration()));
        detailSpeed.setText(decimalFormat.format(pathRecord.getSpeed()));
        detailDistribution.setText(decimalFormat.format(pathRecord.getDistribution()));
        detailCalorie.setText(intFormat.format(pathRecord.getCalorie()));

        final Long id = pathRecord.getId();

        deleteRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("警告")
                        .setMessage("确定要删除这条记录吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DBUtils.deleteRecord(db, id);
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create().show();
            }
        });

        return view;
    }

    private void initView(View view) {
        detailDistance = (TextView) view.findViewById(R.id.detail_distance);
        detailDuration = (TextView) view.findViewById(R.id.detail_duration);
        detailSpeed = (TextView) view.findViewById(R.id.detail_speed);
        detailDistribution = (TextView) view.findViewById(R.id.detail_distribution);
        detailCalorie = (TextView) view.findViewById(R.id.detail_calorie);

        deleteRecord = view.findViewById(R.id.delete_record);
    }

    public void initDB() {
        dbHelper = new MyDatabaseHelper(getActivity(), DBUtils.SPORT_DB_STORE, null, 1);
        dbHelper.getWritableDatabase(); // 执行、真正创建数据库文件, 不会删除已有的数据
        db = dbHelper.getWritableDatabase();
    }
}
