package com.maker.intelligentdustbin;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


/**
 * Created by sweets on 17/2/24.
 * 利用Alarm进行后台服务，一分钟刷新一次
 */

public class BackgroundService extends Service {

    private String able,disable,backup,others;
    private boolean ableChangeFlag = true,disableChangeFlag = true,backupChangeFlag = true,ohtersChangeFlag = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getInformation();
        AlarmInit();
        return super.onStartCommand(intent, flags, startId);
    }

    //WebService线程
    private void getInformation() {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                try {
                    //组装反向地理编码的接口地址
                    StringBuilder url = new StringBuilder();
                    url.append("http://jasperwong.cn:8082/SmartBicycle_Server/trash/search?id=1");
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(url.toString());
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    HttpEntity entity = httpResponse.getEntity();

                    String response = EntityUtils.toString(entity);
                    response = response.substring(6);

                    JSONObject jsonObject = new JSONObject(response);
                    able = jsonObject.getString("able");
                    disable = jsonObject.getString("disable");
                    backup = jsonObject.getString("backup");
                    others = jsonObject.getString("others");
                    if (able.equals("100")){
                        if (ableChangeFlag){
                            NotificationSend("可回收压缩桶",1);
                            ableChangeFlag = false;
                        }
                    }
                    else ableChangeFlag = true;

                    if (disable.equals("100")){
                        if (disableChangeFlag){
                            NotificationSend("可回收不可压缩桶",2);
                            disableChangeFlag = false;
                        }
                    }
                    else disableChangeFlag = true;

                    if (backup.equals("100")){
                        if (backupChangeFlag){
                            NotificationSend("其他桶",3);
                            backupChangeFlag = false;
                        }
                    }
                    else backupChangeFlag = true;

                    if (others.equals("100")){
                        if (ohtersChangeFlag){
                            NotificationSend("备用桶",4);
                            ohtersChangeFlag = false;
                        }
                    }
                    else ohtersChangeFlag = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void AlarmInit(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int oneMinute = 60*1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + oneMinute;
        Intent i = new Intent(this,AlarmReceiver.class);
        PendingIntent pi =  PendingIntent.getBroadcast(this,0,i,0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void NotificationSend(String message,int number){
        Intent intent = new Intent(this, DustbinContentActivity.class);
        intent.putExtra("text", "1号垃圾桶");
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(this)
                .setTicker("You have a new message")//在状态栏显示的标题
                .setContentTitle("垃圾桶已装满")//设置标题
                .setContentText("1号垃圾桶中"+message+"已经装满! 请及时处理！")//设置内容
                .setWhen(System.currentTimeMillis())//设置显示的时间，默认就是currentTimeMillis()
                .setSmallIcon(R.drawable.dustbin_icon1)//设置状态栏显示时的图标
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))//设置点击时的意图
//                .setDeleteIntent(PendingIntent.getActivity(this, 0, intent, 0))//设置删除时的意图
                .setAutoCancel(true)//设置是否自动按下过后取消
                .setOngoing(false)//设置为true时就不能删除  除非使用notificationManager.cancel(1)方法
                .setDefaults(Notification.DEFAULT_ALL)
                .build();//创建Notification
        notificationManager.notify(number,notification);
    }

}
