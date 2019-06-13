package com.liuzozo.stepdemo;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("NewApi")
public class AlarmService extends Service {

    static Timer timer = null;

    // 清除通知
    public static void cleanAllNotification() {
        NotificationManager mn = (NotificationManager) PlanSetting_Activity
                .getContext().getSystemService(NOTIFICATION_SERVICE);
        mn.cancelAll();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        //停止整个服务
        Intent stopIntent = new Intent(PlanSetting_Activity.getContext(),
                AlarmService.class);
        stopIntent.putExtra("endKey", 1);
        PlanSetting_Activity.getContext().startForegroundService(stopIntent);
    }

    // 添加通知
    public static void addNotification(long alarmTime, String tickerText,
                                       String contentTitle, String contentText) {
        Intent startIntent = new Intent(PlanSetting_Activity.getContext(),
                AlarmService.class);
        startIntent.putExtra("alarmTime", alarmTime);
        startIntent.putExtra("tickerText", tickerText);
        startIntent.putExtra("contentTitle", contentTitle);
        startIntent.putExtra("contentText", contentText);
        PlanSetting_Activity.getContext().startForegroundService(startIntent);
        Log.e("Service", "started");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String id = "alert_service_01";
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel mChannel = new NotificationChannel(id, "提醒服务", NotificationManager.IMPORTANCE_HIGH);
        mChannel.setShowBadge(true);//显示logo
        mChannel.setDescription("可以在指定时间提醒");//设置描述
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC); //设置锁屏可见 VISIBILITY_PUBLIC=可见
        manager.createNotificationChannel(mChannel);

        Notification notification = new Notification.Builder(this)
                .setChannelId(id)
                .setContentTitle("乐跑圈闹钟服务")//标题
                .setContentText("运行中...")//内容
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.icon_app_round)
                //小图标一定需要设置,否则会报错(如果不设置它启动服务前台化不会报错,
                // 但是你会发现这个通知不会启动),如果是普通通知,不设置必然报错
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_app_round))
                .build();
        startForeground(1, notification);
        //服务前台化只能使用startForeground()方法,
        // 不能使用 notificationManager.notify(1,notification);
        // 这个只是启动通知使用的,使用这个方法你只需要等待几秒就会发现报错了
        Log.e("addNotification", "===========create=======");

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (intent.hasExtra("endKey")) {
            stopForeground(true);
            stopSelf(); //停止服务
        } else {
            long period = 24 * 60 * 60 * 1000; // 24小时一个周期
            long alarmTime = intent.getLongExtra("alarmTime", 0);
            Log.e("Alarm", "alarmTime = " + alarmTime);
            int delay = (int) (alarmTime - System.currentTimeMillis());
            Log.e("Alarm", "delay = " + delay);
            if (delay < 0)
                delay += period;

            if (null == timer) {
                timer = new Timer();
            }
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    Log.e("Alarm", "TimeUp");
                    NotificationManager notificationManager = (NotificationManager) AlarmService.this
                            .getSystemService(NOTIFICATION_SERVICE);

                    String id = "step_02";
                    String name = "乐跑圈闹钟";

                    //适配安卓8.0+
                    NotificationChannel mChannel = new NotificationChannel(id,
                            name, NotificationManager.IMPORTANCE_HIGH);
                    mChannel.enableVibration(true);
                    mChannel.setVibrationPattern(new long[]{
                            100, 200, 300, 400, 500, 400, 300, 200, 400});
                    mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

                    notificationManager.createNotificationChannel(mChannel);
                    Log.i("Alarm", mChannel.toString());

                    Notification.Builder builder = new Notification.Builder(
                            AlarmService.this);
                    builder.setChannelId(id);

                    Intent notificationIntent = new Intent(AlarmService.this,
                            MainActivity.class);// 点击跳转位置
                    PendingIntent contentIntent = PendingIntent.getActivity(
                            AlarmService.this, 0, notificationIntent, 0);

                    builder.setContentIntent(contentIntent);

                    builder.setSmallIcon(R.mipmap.icon_app_round);// 设置通知图标
                    builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_app_round));
                    builder.setTicker(intent.getStringExtra("tickerText")); // 测试通知栏标题
                    builder.setContentText(intent.getStringExtra("contentText")); // 下拉通知啦内容
                    builder.setContentTitle(intent.getStringExtra("contentTitle"));// 下拉通知栏标题
                    builder.setAutoCancel(true);// 点击弹出的通知后,让通知将自动取消
//                builder.setVibrate(new long[]{0, 2000, 1000, 4000}); // 震动需要真机测试-延迟0秒震动2秒延迟1秒震动4秒
                    // builder.setSound(Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI,
                    // "5"));//获取Android多媒体库内的铃声
                    // builder.setSound(Uri.parse("file:///sdcard/xx/xx.mp3"))
                    // ;//自定义铃声
                    builder.setDefaults(Notification.DEFAULT_ALL);// 设置使用系统默认声音
                    // builder.addAction("图标", title, intent); //此处可设置点击后 打开某个页面
                    Notification notification = builder.build();
                    notification.flags = notification.FLAG_AUTO_CANCEL;// 声音无限循环

                    notificationManager.notify((int) System.currentTimeMillis(), notification);
                }
            }, delay, period);
        }


        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Log.e("addNotification", "===========destroy=======");
        super.onDestroy();
    }
}