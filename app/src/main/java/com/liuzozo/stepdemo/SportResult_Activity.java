package com.liuzozo.stepdemo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.liuzozo.stepdemo.utils.DBUtils;
import com.liuzozo.stepdemo.utils.MyDatabaseHelper;
import com.liuzozo.stepdemo.utils.PathSmoothTool;
import com.liuzozo.stepdemo.utils.ScreenShotHelper;
import com.liuzozo.stepdemo.utils.StepUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 运动结果页面，点击完成时跳到该页面
 * 评分运行规则：依次判断 距离大于0 ★；运动时间大于40分钟 ★★；速度在3~6km/h之间 ★★★
 */
public class SportResult_Activity extends AppCompatActivity implements
        View.OnClickListener, CompoundButton.OnCheckedChangeListener,
        AMap.OnMapLoadedListener, AMap.OnMapScreenShotListener {

    private LinearLayout share;
    private ImageView star1;
    private ImageView star2;
    private ImageView star3;
    private LinearLayout returnMain;
    private TextView comments;
    private TextView distanceText;
    private TextView durationText;
    private TextView calorieText;
    private CardView hintCard;
    private TextView hint;
    private LinearLayout hintIcon;
    private boolean hintShown = false;

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private DecimalFormat intFormat = new DecimalFormat("0");

    private List<LatLng> pathLine = new ArrayList<LatLng>();
    private double distance = 0.;
    private double duration = 0.;
    //    private int durationHour = 0;
//    private int durationMin = 0;
//    private int durationSec = 0;
    private double calorie = 0.;
    private double speed = 0.;

    private Double distanceM;
    private Long durationMs;

    private MapView mapView = null;
    private AMap amap = null;
    private Polyline mOriginPolyline, mKalmanPolyline;
    private CheckBox mKalmanBtn;
    private PathSmoothTool mPathSmoothTool;

    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private ViewGroup mViewGroupContainer;
    private View mScreenShotView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_result);

        init();

        initDB();


        if (distance > 0) {
            star1.setVisibility(View.VISIBLE);
            comments.setText("跑步效果一般");
            hint.setText("跑步时间不足40分钟，试着跑久一点吧");
            if (duration > 40) {
                star2.setVisibility(View.VISIBLE);
                comments.setText("跑步效果不错");
                hint.setText("尝试把速度控制在3-6公里每小时会得到更好的锻炼效果");
                if (speed >= 3 & speed <= 6) {
                    star3.setVisibility(View.VISIBLE);
                    comments.setText("跑步效果很OK");
                    hint.setText("您的跑步技术已经登峰造极");
                }
            }
        }

        distanceText.setText(decimalFormat.format(distance));
        durationText.setText(StepUtils.formatMilliseconds(durationMs));
        calorieText.setText(intFormat.format(calorie));

        mapView = (MapView) findViewById(R.id.map_result);
        mapView.onCreate(savedInstanceState);
        initMap();
        mPathSmoothTool = new PathSmoothTool();
        mPathSmoothTool.setIntensity(4);
        addPathLine();
    }

    private void init() {
        share = findViewById(R.id.tv_shared);
        share.setOnClickListener(this);

        mKalmanBtn = (CheckBox) findViewById(R.id.record_show_activity_kalman_button);

        mKalmanBtn.setOnCheckedChangeListener(this);

        star1 = (ImageView) findViewById(R.id.star_1);
        star1.setVisibility(View.INVISIBLE);
        star2 = (ImageView) findViewById(R.id.star_2);
        star2.setVisibility(View.INVISIBLE);
        star3 = (ImageView) findViewById(R.id.star_3);
        star3.setVisibility(View.INVISIBLE);

        comments = findViewById(R.id.comments);
        distanceText = findViewById(R.id.result_distance);
        durationText = findViewById(R.id.result_duration);
        calorieText = findViewById(R.id.result_calorie);

        returnMain = (LinearLayout) findViewById(R.id.return_result);
        returnMain.setOnClickListener(this);

        mViewGroupContainer = (ViewGroup) findViewById(R.id.root_pic_id);
        mScreenShotView = (View) findViewById(R.id.screenshot_view);

        hintCard = findViewById(R.id.hint_card);
        hint = findViewById(R.id.hint);
        hintIcon = findViewById(R.id.hint_icon);
        hintIcon.setOnClickListener(this);
    }

    private void initMap() {
        if (amap == null) {
            amap = mapView.getMap();
        }
        amap.setOnMapLoadedListener(this);
    }

    public void initDB() {
        dbHelper = new MyDatabaseHelper(this, DBUtils.SPORT_DB_STORE, null, 1);
        dbHelper.getWritableDatabase(); // 执行、真正创建数据库文件, 不会删除已有的数据
        db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query("sport_record",
                null, null, null,
                null, null, null);

        boolean succeed = (cursor.moveToLast()); //查询最近的记录

        if (succeed) {
            String pathLineStr = cursor.getString(cursor.getColumnIndex("path_line"));
            pathLine = StepUtils.parseLatLngLocations(pathLineStr);


            distanceM = cursor.getDouble(cursor.getColumnIndex("distance"));
            distance = distanceM / 1000;

            durationMs = cursor.getLong(cursor.getColumnIndex("duration"));
            duration = (double) durationMs / 1000 / 60;
//            durationHour = (int) (durationMs / 1000 / 3600);
//            durationMin = (int) (durationMs / 1000 / 60) % 60;
//            durationSec = (int) (durationMs / 1000) % 60;

            calorie = cursor.getDouble(cursor.getColumnIndex("calorie"));

            speed = cursor.getDouble(cursor.getColumnIndex("speed"));

        }
        cursor.close();
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();
        switch (id) {
            case R.id.record_show_activity_kalman_button:
                if (mOriginPolyline != null) {
                    mOriginPolyline.setVisible(!b);
                }
                if (mKalmanPolyline != null) {
                    mKalmanPolyline.setVisible(b);
                }
                break;
        }
    }

    @Override
    public void onMapLoaded() {
    }

    private void addPathLine() {

        if (pathLine != null && pathLine.size() > 0) {
            mOriginPolyline = amap.addPolyline(new PolylineOptions().addAll(pathLine).color(Color.GREEN));
            amap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBounds(pathLine), 200));
//            amap.moveCamera(CameraUpdateFactory.newLatLngZoom(mOriginList.get(0),15));
        }
        pathOptimize(pathLine);
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
        mKalmanPolyline = amap.addPolyline(new PolylineOptions().addAll(pathOptimizeList)
                .color(Color.parseColor("#FFC125")));
        return pathOptimizeList;
    }

    /**
     * 下面代码为点击分享朋友圈
     * 1 .首先根据页面布局，选择特定区域的View的id (root view id) , 转bitmap
     * 2. 压缩图片，以免图片过大，内存溢出
     * 3. 把图片保存成手机上的文件，得到Uri 路径，
     * 4. 利用安卓自带的应用程序之间的分享功能，进行分享到朋友圈，微信等
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_shared:
                amap.getMapScreenShot(this);

//                final View textView = LayoutInflater.from(this).inflate(
//                        R.layout.activity_sport_result, null);
//                View rootView = textView.findViewById(R.id.root_pic_id);
//                Bitmap bitmap = getBitmapByView(rootView);
//                Bitmap img = compressImage(bitmap);
//                File file = bitMap2File(img);
                File file = new File(Environment.getExternalStorageDirectory().
                        getAbsolutePath() + File.separator + "share.png");
                Log.e("File", file.getAbsolutePath() + " exist = " + file.exists());
                if (file != null && file.exists() && file.isFile()) {
                    //由文件得到uri
                    Uri imageUri = FileProvider.getUriForFile(
                            this, "com.liuzozo.stepdemo.fileprovider", file);
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    shareIntent.setType("image/*");
                    startActivity(Intent.createChooser(shareIntent, "分享图片"));
                }
                break;

            case R.id.return_result:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;

            case R.id.hint_icon:
                if (hintShown) {
                    hintCard.setVisibility(View.INVISIBLE);
                    hintShown = false;
                } else {
                    hintCard.setVisibility(View.VISIBLE);
                    hintShown = true;
                }
                break;

            default:
                break;
        }
    }


    /**
     * 将布局转化为bitmap这里传入的是你要截的布局的根View
     */
    public Bitmap getBitmapByView(View view) {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        return view.getDrawingCache(true);
    }

    /**
     * 压缩图片
     */

    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 10, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 400) {  //循环判断如果压缩后图片是否大于400kb,大于继续压缩（这里可以设置大些）
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 把bitmap转化为file
     */
    public File bitMap2File(Bitmap bitmap) {
        String path = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = Environment.getExternalStorageDirectory() + File.separator;//保存到sd根目录下
        }

        File f = new File(path, "share" + ".jpg");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            bitmap.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return f;
        }
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onMapScreenShot(Bitmap bitmap) {
        ScreenShotHelper.saveScreenShot(bitmap, mViewGroupContainer, mapView, mScreenShotView);
        Toast.makeText(getApplicationContext(), "已截图", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onMapScreenShot(Bitmap bitmap, int i) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
