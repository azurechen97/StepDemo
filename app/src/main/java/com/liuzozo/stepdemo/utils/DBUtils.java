package com.liuzozo.stepdemo.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.liuzozo.stepdemo.bean.PathRecord;
import com.liuzozo.stepdemo.bean.SportRecord;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/**
 * 封装操作数据库的工具类 和相关常量
 */
public class DBUtils {

    // 表保存的db 文件
    public static final String SPORT_DB_STORE = "sport_record.db";


    /**
     * 这些init 应该放入具体的activity 页面 ，初始化db 后，可以通过DBUtils 操作这个 db
     */
//    private MyDatabaseHelper dbHelper;
//   获得一个数据库的引用，以便操作数据库
//    public void initDB() {
//        //initFruitList();
//        dbHelper = new MyDatabaseHelper(this, DBUtils.BOOK_DB_STORE, null, 1);
//        dbHelper.getWritableDatabase(); // 执行、真正创建数据库文件, 不会删除已有的数据
//        db = dbHelper.getWritableDatabase();
//    }

    // 添加
    public static void insert(SQLiteDatabase db, Double distance, Long duration,
                              String pathLine, String startPoint, String endPoint, Long startTime,
                              Long endTime, Double calorie, Double speed, Double distribution,
                              String dateTag) {
        db.execSQL("insert into sport_record(distance, duration, path_line," +
                "start_point, end_point, start_time, end_time, calorie, speed, " +
                "distribution, date_tag)" +
                "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{distance, duration,
                pathLine, startPoint, endPoint, startTime, endTime, calorie, speed, distribution,
                dateTag});

    }

    public static void insert(SQLiteDatabase db, SportRecord sportRecord) {
        insert(db, sportRecord.getDistance(), sportRecord.getDuration(), sportRecord.getPathLine(),
                sportRecord.getStartPoint(), sportRecord.getEndPoint(), sportRecord.getStartTime(),
                sportRecord.getEndTime(), sportRecord.getCalorie(), sportRecord.getSpeed(),
                sportRecord.getDistribution(), sportRecord.getDateTag());
    }

    public static void insert(SQLiteDatabase db, PathRecord pathRecord) {
        insert(db, StepUtils.parseSportRecord(pathRecord));
    }

    // 查询
    public static PathRecord getRecordById(SQLiteDatabase db, int id) {
        SportRecord sportRecord = new SportRecord();

        Cursor cursor = db.rawQuery("SELECT * FROM sport_record WHERE id = ?"
                , new String[]{String.valueOf(id)});

        boolean succeed = (cursor.moveToFirst());

        if (succeed) {
            sportRecord.setId(cursor.getLong(cursor.getColumnIndex("id")));
            sportRecord.setDistance(cursor.getDouble(cursor.getColumnIndex("distance")));
            sportRecord.setDuration(cursor.getLong(cursor.getColumnIndex("duration")));
            sportRecord.setPathLine(cursor.getString(cursor.getColumnIndex("path_line")));
            sportRecord.setStartPoint(cursor.getString(cursor.getColumnIndex("start_point")));
            sportRecord.setEndPoint(cursor.getString(cursor.getColumnIndex("end_point")));
            sportRecord.setStartTime(cursor.getLong(cursor.getColumnIndex("start_time")));
            sportRecord.setEndTime(cursor.getLong(cursor.getColumnIndex("end_time")));
            sportRecord.setCalorie(cursor.getDouble(cursor.getColumnIndex("calorie")));
            sportRecord.setSpeed(cursor.getDouble(cursor.getColumnIndex("speed")));
            sportRecord.setDistribution(cursor.getDouble(cursor.getColumnIndex("distribution")));
            sportRecord.setDateTag(cursor.getString(cursor.getColumnIndex("date_tag")));
        }
        cursor.close();

        PathRecord pathRecord = StepUtils.parsePathRecord(sportRecord);

        return pathRecord;
    }

    public static List<PathRecord> getRecordsByDate(SQLiteDatabase db, int year, int month, int day) {
        List<PathRecord> sportList = new ArrayList<>(0);

        Cursor cursor = db.rawQuery("SELECT * FROM sport_record WHERE date_tag = ?"
                , new String[]{String.format("%4d-%02d-%02d", year, month, day)});

        boolean succeed = (cursor.moveToFirst());

        if (succeed) {
            do {
                SportRecord sportRecord = new SportRecord();
                sportRecord.setId(cursor.getLong(cursor.getColumnIndex("id")));
                sportRecord.setDistance(cursor.getDouble(cursor.getColumnIndex("distance")));
                sportRecord.setDuration(cursor.getLong(cursor.getColumnIndex("duration")));
                sportRecord.setPathLine(cursor.getString(cursor.getColumnIndex("path_line")));
                sportRecord.setStartPoint(cursor.getString(cursor.getColumnIndex("start_point")));
                sportRecord.setEndPoint(cursor.getString(cursor.getColumnIndex("end_point")));
                sportRecord.setStartTime(cursor.getLong(cursor.getColumnIndex("start_time")));
                sportRecord.setEndTime(cursor.getLong(cursor.getColumnIndex("end_time")));
                sportRecord.setCalorie(cursor.getDouble(cursor.getColumnIndex("calorie")));
                sportRecord.setSpeed(cursor.getDouble(cursor.getColumnIndex("speed")));
                sportRecord.setDistribution(cursor.getDouble(cursor.getColumnIndex("distribution")));
                sportRecord.setDateTag(cursor.getString(cursor.getColumnIndex("date_tag")));

                sportList.add(StepUtils.parsePathRecord(sportRecord));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return sportList;
    }

    // 删除
//    public static void deleteRecord(SQLiteDatabase db, int id) {
//        db.execSQL("delete from sport_record where id = " + id);
////        db.execSQL("delete from book where author =  'ssasa' ");
//    }

    // 修改
//    public static void updateRecord(SQLiteDatabase db) {
//        db.execSQL("update book set pages = '120' where author = 'lu jia zui' ");
//    }

    /**
     *  根据日期  获取运动数
     */
//    List<Object> queryRecordList( int year , int month, int day){
//
//    }
}
