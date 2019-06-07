package com.liuzozo.stepdemo.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.liuzozo.stepdemo.R;
import com.liuzozo.stepdemo.SportRecordDetails_Activity;
import com.liuzozo.stepdemo.adapter.SportCalendarAdapter;
import com.liuzozo.stepdemo.bean.PathRecord;
import com.liuzozo.stepdemo.calendarview.custom.FullyLinearLayoutManager;
import com.liuzozo.stepdemo.utils.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * sport data  page
 * 1. 根据日期 查询数据库， 在日历上标记，下方显示 日历某一天的跑步记录
 * 2. 点击某一条记录，跳转到跑步详情页面
 */
public class StepData_Fragment extends Fragment {


    TextView mTextYear;
    TextView mTextMonthDay;
    TextView mTextLunar;

    TextView mTextCurrentDay;

    // 日历控件
    CalendarView mCalendarView;
    RecyclerView mRecycleView;

    CalendarLayout calendarLayout;

    private SportCalendarAdapter sportCalendarAdapter;

    LinearLayout sport_record_listLayout;

    private int mYear;
    private List<PathRecord> sportList = new ArrayList<>(0);

    private MyDatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stepdata, container,
                false);

        initView(view);

        databaseHelper = new MyDatabaseHelper(
                getActivity(), "sport_record.db", null, 1);
        db = databaseHelper.getWritableDatabase();

        return view;
    }

    private void initView(View view) {
        mCalendarView = view.findViewById(R.id.calendarView);
        mYear = mCalendarView.getCurYear();

        mTextYear = view.findViewById(R.id.tv_year);
        mTextYear.setText(String.valueOf(mYear));

        String monthAndDay = mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日";
        mTextMonthDay = view.findViewById(R.id.tv_month_day);
        mTextMonthDay.setText(monthAndDay);

        mTextLunar = view.findViewById(R.id.tv_lunar);
        mTextLunar.setText("今日");

        mTextCurrentDay = view.findViewById(R.id.tv_current_day);
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));

        sport_record_listLayout = view.findViewById(R.id.sport_record_list);

        mCalendarView.setWeekStarWithSun();

        mRecycleView = view.findViewById(R.id.recyclerView);

        mRecycleView.setLayoutManager(new FullyLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mRecycleView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.line)));


        sportCalendarAdapter = new SportCalendarAdapter(R.layout.item_sport_calendar, sportList);
        mRecycleView.setAdapter(sportCalendarAdapter);


        // 加载数据
        loadSportData();

        // 每一条跑步记录的点击事件
        sportCalendarAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent();
                intent.setClass(getContext(), SportRecordDetails_Activity.class);
                startActivity(intent);
            }
        });
        // 日历点击事件
        mCalendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {

            @Override
            public void onCalendarOutOfRange(Calendar calendar) {

            }

            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                // 点击时加载数据
                loadSportData();
            }
        });
    }

    /**
     * 查询数据库里当天的跑步记录
     * 1. 查询所有跑步数据，设置日历标记
     * 2  根据年月日参数，查询某一天的数据，设置recycleview 的条目
     */
    private void loadSportData() {
        setCalendarSportData();
        setRecycleViewSportData(mCalendarView.getCurYear(), mCalendarView.getCurMonth(), mCalendarView.getCurDay());
    }

    public void setCalendarSportData() {
        // 给日历做有记录的标记, 需要查询出有跑步记录的年月日
        String date;
        int year;
        int month;
        int day;

        Map<String, Calendar> map = new HashMap<>();

        Cursor cursor = db.query("sport_record",
                null, null, null,
                null, null, null);

        boolean succeed = (cursor.moveToFirst());

        if (succeed) {
            do {
                date = cursor.getString(cursor.getColumnIndex("date_tag"));
                year = Integer.parseInt(date.substring(0, 4));
                month = Integer.parseInt(date.substring(5, 7));
                day = Integer.parseInt(date.substring(8));
                map.put(getSchemeCalendar(year, month, day, 0xFFCC0000, "记").toString(),
                        getSchemeCalendar(year, month, day, 0xFFCC0000, "记"));
            } while (cursor.moveToNext());

        }
        cursor.close();

        //此方法在巨大的数据量上不影响遍历性能，推荐使用
        mCalendarView.setSchemeDate(map);
    }

    public void setRecycleViewSportData(int year, int month, int day) {
        // todo 本应该查询数据库
        sportList.clear(); // 清除之前的数据
        sportCalendarAdapter.notifyDataSetChanged();// 更新显示
        PathRecord pathRecord = new PathRecord();

        Cursor cursor = db.query("sport_record",
                null, null, null,
                null, null, null);



        sportList.add(pathRecord);

        if (sportList.size() > 0) {
            sport_record_listLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 对日历进行设置，当然也可以操作其它属性
     *
     * @param year
     * @param month
     * @param day
     * @param color
     * @param text
     * @return
     */
    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        calendar.addScheme(new Calendar.Scheme());
        calendar.addScheme(0xFF008800, text);
        return calendar;
    }


    //recyclerView设置间距
    protected class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int mSpace;

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.right = mSpace;
            outRect.left = mSpace;
            outRect.bottom = mSpace;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = mSpace;
            } else {
                outRect.top = 0;
            }
        }

        SpaceItemDecoration(int space) {
            this.mSpace = space;
        }
    }


}
