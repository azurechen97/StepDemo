package com.liuzozo.stepdemo.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.liuzozo.stepdemo.bean.PathRecord;
import com.liuzozo.stepdemo.bean.SportRecord;


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
    public static void getBook(SQLiteDatabase db) {
        // select * from book where name='xxx'
        Cursor cursor = db.rawQuery("select * from book", null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("author"));
            int pages = cursor.getInt(cursor.getColumnIndex("pages"));
            System.out.println(name + "======" + pages);
        }
        cursor.close();
    }

    // 删除
    public static void deleteBook(SQLiteDatabase db, String authorName) {
        db.execSQL("delete from book where author = " + authorName);
        db.execSQL("delete from book where author =  'ssasa' ");
    }

    // 修改
    public static void updateBook(SQLiteDatabase db) {
        db.execSQL("update book set pages = '120' where author = 'lu jia zui' ");
    }


    /**
     *  根据日期  获取运动数
     */
//    List<Object> queryRecordList( int year , int month, int day){
//
//    }
}
