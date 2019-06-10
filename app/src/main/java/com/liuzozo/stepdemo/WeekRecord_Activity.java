package com.liuzozo.stepdemo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.liuzozo.stepdemo.utils.MyDatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * 一周跑步记录
 * 按天显示 柱状图、折线图等，这个页面大家可以自定义， 不需要非按照我的那个图片的样式
 * （比如一条折线显示每天跑步的公里，一条显示每天跑步的时间，一条显示跑步的卡路里 等等，）
 * 要求：只要显示出数据库里跑步记录的一些分析即可
 */
public class WeekRecord_Activity extends AppCompatActivity {

    private LineChartView lineChart1;
    private LineChartView lineChart2;

    private MyDatabaseHelper databaseHelper;
    private SQLiteDatabase db;

//    String[] date = {"10-22","11-22","12-22","1-22","6-22","5-23","5-22","6-22","5-23","5-22"};//X轴的标注
//    int[] score= {50,42,90,33,10,74,22,18,79,20};//图表的数据点

    private ArrayList<String> date = new ArrayList<>();
    private ArrayList<Double> distance = new ArrayList<>();
    private ArrayList<Double> calorie = new ArrayList<>();

    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();

    private SimpleDateFormat dateTagFormat = new SimpleDateFormat("yyyy-MM-dd");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_record);

        initDB();

        lineChart1 = (LineChartView) findViewById(R.id.line_chart_1);
        lineChart2 = (LineChartView) findViewById(R.id.line_chart_2);

        getAxisXLabels();//获取x轴的标注

        getAxisPoints(distance);//获取坐标点
        initLineChart(lineChart1);//初始化

        getAxisPoints(calorie);//获取坐标点
        initLineChart(lineChart2);//初始化
    }

    private void initDB() {
        databaseHelper = new MyDatabaseHelper(
                this, "sport_record.db", null, 1);
        db = databaseHelper.getWritableDatabase();

        Cursor cursor = db.query("sport_record",
                null, null, null,
                null, null, null);

        String lastDateTag = new String();

        boolean succeed = (cursor.moveToFirst());
        if (succeed) {
            do {
                String aDate = cursor.getString(cursor.getColumnIndex("date_tag"));
                double aDistance = cursor.getDouble(cursor.getColumnIndex("distance"));
                double aCalorie = cursor.getDouble(cursor.getColumnIndex("calorie"));
                if (date.contains(aDate)) {
                    int i = date.indexOf(aDate);
                    distance.set(i, distance.get(i) + aDistance);
                    calorie.set(i, calorie.get(i) + aCalorie);
                    Log.e("date", date.get(i));
                    Log.e("distance", distance.get(i) + "");
                } else {
                    //填充中间日期
                    long gap = 0L;
                    long value1 = 0L;
                    long value2 = 0L;
                    try {
                        value1 = dateTagFormat.parse(lastDateTag).getTime();
                        value2 = dateTagFormat.parse(aDate).getTime();
                        gap = value2 - value1;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    while (gap > 24 * 3600 * 1000L) {
                        value1 += 24 * 3600 * 1000L;
                        String interval = dateTagFormat.format(new Date(value1));
                        date.add(interval);
                        distance.add(0.);
                        calorie.add(0.);
                        gap = value2 - value1;
                        Log.e("date", interval);
                        Log.e("distance", "0");
                    }

                    date.add(aDate);
                    distance.add(aDistance);
                    calorie.add(aCalorie);
                    int i = date.indexOf(aDate);
                    Log.e("date", date.get(i));
                    Log.e("distance", distance.get(i) + "");
                }
                lastDateTag = aDate;
            } while (cursor.moveToNext());
        }
        cursor.close();

        //填充到今天为止
        long gap = 0L;
        long value1 = 0L;
        String today = dateTagFormat.format(new Date());
        long value2 = 0L;

        try {
            value1 = dateTagFormat.parse(lastDateTag).getTime();
            value2 = dateTagFormat.parse(today).getTime();
            gap = value2 - value1;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        while (gap > 0) {
            value1 += 24 * 3600 * 1000L;
            String interval = dateTagFormat.format(new Date(value1));
            date.add(interval);
            distance.add(0.);
            calorie.add(0.);
            gap = value2 - value1;
            Log.e("date", interval);
            Log.e("distance", "0");
        }

        for (int i = 0; i < date.size(); i++)
            date.set(i, date.get(i).substring(5));
    }

    /**
     * 设置X 轴的显示
     */
    private void getAxisXLabels() {
        for (int i = 0; i < date.size(); i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(date.get(i)));
        }
    }

    /**
     * 图表的每个点的显示
     */
    private void getAxisPoints(ArrayList<Double> list) {
        mPointValues = new ArrayList<PointValue>();
        for (int i = 0; i < list.size(); i++) {
            mPointValues.add(new PointValue(i, Math.round(list.get(i))));
        }
    }

    private void initLineChart(LineChartView lineChart) {
        Line line = new Line(mPointValues).setColor(Color.parseColor("#FFCD41"));  //折线的颜色（橙色）
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line.setFilled(true);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(true);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.GRAY);  //设置字体颜色
        //axisX.setName("date");  //表格名称
        axisX.setTextSize(10);//设置字体大小
        axisX.setMaxLabelChars(8); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        //data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis();  //Y轴
        axisY.setName("");//y轴标注
        axisY.setTextSize(10);//设置字体大小
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边


        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 2);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);


        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.left = v.right - 6;//从右端开始，显示最近七天
        lineChart.setCurrentViewportWithAnimation(v);
    }
}
