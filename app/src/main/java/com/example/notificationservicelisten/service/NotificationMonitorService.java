package com.example.notificationservicelisten.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.notificationservicelisten.MyTableTextView;
import com.example.notificationservicelisten.R;

import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("NewApi")
public class NotificationMonitorService extends NotificationListenerService {

    private RelativeLayout relativeLayout;
    private LinearLayout mainLinerLayout;

    // 在收到消息时触发
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // TODO Auto-generated method stub
        Bundle extras = sbn.getNotification().extras;
        // 获取接收消息APP的包名
        String notificationPkg = sbn.getPackageName();
        // 获取接收消息的抬头
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        // 获取接收消息的内容
        String notificationText = extras.getString(Notification.EXTRA_TEXT);
        // 获取接收消息的 时间
        long when=sbn.getNotification().when;
        Date date=new Date(when);
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String notificationtime=format.format(date);
        Toast.makeText(getApplicationContext(), "Notification posted " + notificationTitle + " & " + notificationText, Toast.LENGTH_SHORT).show(); // Toast 是一个 View 视图，快速的为用户显示少量的信息
        Log.i("XSL_Test", "Notification posted " + notificationTitle + " & " + notificationText+"&"+notificationtime);

//        indata(notificationTitle, notificationText);
    }

    private void indata(String notificationTitle, String notificationText) {
        relativeLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.table, null);
        MyTableTextView txt = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_1);
//        txt.setText(1);
        txt = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_2);
        txt.setText(notificationText);
        txt = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_3);
        txt.setText(notificationTitle);
        txt = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_4);
        txt.setText("1992/04/21");
        txt = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_5);
        txt.setText("150");
        txt = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_6);
        txt.setText("200");
        txt = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_7);
        txt.setText("120");
        mainLinerLayout.addView(relativeLayout);
    }

    // 在删除消息时触发
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // TODO Auto-generated method stub
        Bundle extras = sbn.getNotification().extras;
        // 获取接收消息APP的包名
        String notificationPkg = sbn.getPackageName();
        // 获取接收消息的抬头
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        // 获取接收消息的内容
        String notificationText = extras.getString(Notification.EXTRA_TEXT);
        Log.i("XSL_Test", "Notification removed " + notificationTitle + " & " + notificationText);
        Toast.makeText(getApplicationContext(), "Notification posted " + notificationTitle + " & " + notificationText, Toast.LENGTH_SHORT).show(); // Toast 是一个 View 视图，快速的为用户显示少量的信息

    }


}
