package com.example.notificationservicelisten;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.notificationservicelisten.util.PreferenceUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;


public class MainActivity extends AppCompatActivity {

    private Intent intent;

    //通知  start
    private Context mContext;

    private Button send_notification;

    private Button notification_listen_off;

    private Button notification_listen_on;

    public PreferenceUtil preference ;

    private final static int GRAY_SERVICE_ID = 1001;

    //表格
    private LinearLayout mainLinerLayout;
    private RelativeLayout relativeLayout;
    private String[] name={"序号","考号","姓名","出生年月","语文","数学","英语"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext= MainActivity.this;


        //初始化实例
        initView();
        //检测通知是否开启
        checkNotification(mContext);
//        Log.e("onCreate", "进入checkLockAndShowNotification事件");
//        checkLockAndShowNotification("支付宝收款0.01元");
//
        //通知按钮监听
        send_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e("send_notification onClick", "进入send_notification点击事件");
                checkLockAndShowNotification("支付宝收款0.01元");
            }
        });

        //监听通知开启

        notification_listen_on.setOnClickListener(new View.OnClickListener() {

            boolean isAuthor = isNotificationServiceEnable();
            @Override
            public void onClick(View v) {
                //
                if (!isEnabled()) {
                    //直接跳转通知授权界面
                    //android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS是API 22才加入到Settings里，这里直接写死
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "监控器开关已开启", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        // 通知栏监控器关
        notification_listen_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if (isEnabled()) {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "监控器开关已关闭", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });


        //表格

        initData();
    }



    /**
     * 是否已授权
     *
     * @return
     */
    private boolean isNotificationServiceEnable() {
        return NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName());
    }






    /**
     * 初始化实例
     */
    private void initView() {
        send_notification = findViewById(R.id.send_notification);
        notification_listen_off = findViewById(R.id.notification_listen_off);
        notification_listen_on = findViewById(R.id.notification_listen_on);

        mainLinerLayout = (LinearLayout) this.findViewById(R.id.MyTable);
    }


    //    -----------------------------------消息通知--------------------------------------------------------

    /**
     * 检查锁屏状态，如果锁屏先点亮屏幕
     *
     * @param content
     */
    private void checkLockAndShowNotification(String content) {
        //管理锁屏的一个服务
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode()) {//锁屏
            //获取电源管理器对象
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            if (!pm.isScreenOn()) {
                @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
                wl.acquire();  //点亮屏幕
                wl.release();  //任务结束后释放
            }
            NotificationShow(content);
//            sendNotification(content);
        } else {
            NotificationShow(content);
//            sendNotification(content);
        }
    }


    /**
     * 发送通知
     *
     * @param
     */
    private void NotificationShow(String content) {
        //1.获取通知管理器类
        Intent intent = new Intent(this,MainActivity.class); //通知跳转
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        /**
         * 兼容Android版本8.0系统
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //第三个参数表示通知的重要程度，默认则只在通知栏闪烁一下
            NotificationChannel channel = new NotificationChannel("NotificationID", "NotificationName", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);         // 开启指示灯，如果设备有的话
//            channel.setLightColor(Color.RED);   // 设置指示灯颜色
            channel.setShowBadge(true);         // 检测是否显示角标
            // 注册通道
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId("NotificationID");
        }
        //2.构建通知类
        builder.setSmallIcon(R.mipmap.ic_image);//设置图标
        builder.setContentTitle("支付宝通知");//标题
//        builder.setContentText("您有一条未读消息!");//内容
        builder.setContentText(content);//内容

//        builder.setWhen(System.currentTimeMillis());    //时间
        //builder.setDefaults(Notification.DEFAULT_ALL);
        //SetDefaults 这个方法可选值如下：
        // Notification.DEFAULT_VIBRATE ：震动提示,Notification.DEFAULT_SOUND：提示音,Notification.DEFAULT_LIGHTS：三色灯,Notification.DEFAULT_ALL：以上三种全部

        // 设置该通知优先级
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setVisibility(VISIBILITY_PUBLIC);
        builder.setWhen(System.currentTimeMillis());
        // 向通知添加声音、闪灯和振动效果
        builder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_ALL | Notification.DEFAULT_SOUND);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        //3.获取通知
        Notification notification = builder.build();


        //设置跳转后  通知消失
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        //4.发送通知
        notificationManager.notify(1, notification);
    }


    /**
     *  通知
     * 检测是否开启通知
     *
     * @param context
     */
    private void checkNotification(final Context context) {
        Log.e("checkNotification", "检测通道是否开通");
        if (!isNotificationEnabled(context)) {
            new AlertDialog.Builder(context).setTitle("温馨提示")
                    .setMessage("你还未开启系统通知，将影响消息的接收，要去开启吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setNotification(context);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
    }
    /**
     * 如果没有开启通知，跳转至设置界面
     *
     * @param context
     */
    private void setNotification(Context context) {
        Intent localIntent = new Intent();
        //直接跳转到应用通知设置的代码：
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            localIntent.putExtra("app_package", context.getPackageName());
            localIntent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            localIntent.addCategory(Intent.CATEGORY_DEFAULT);
            localIntent.setData(Uri.parse("package:" + context.getPackageName()));
        } else {
            //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                localIntent.setAction(Intent.ACTION_VIEW);
                localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
            }
        }
        context.startActivity(localIntent);
    }

    /**
     * 获取通知权限,监测是否开启了系统通知
     *
     * @param context
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean isNotificationEnabled(Context context) {

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /*                                               消息通知结束                                  */




     /*                                                  消息监听开始                                 */

    // 判断是否打开了通知监听权限
    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }



    //用户协议  通知
     private void showAgreeMentDialog() {
         AlertDialog.Builder normalDialog =
                 new AlertDialog.Builder(MainActivity.this);

         normalDialog.setTitle("用户协议").setMessage("您必须同意该用户协议才能使用该应用。点击下方的按钮以查看完整的用户协议");
         normalDialog.setPositiveButton("查看用户协议",
                 new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         // ...To-do
                         Toast.makeText(getApplicationContext(), "用户协议还没有公布", Toast.LENGTH_SHORT).show(); // Toast 是一个 View 视图，快速的为用户显示少量的信息
                     }
                 });
         normalDialog.setNeutralButton("同意",
                 new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         // ...To-do
                         preference.setAgreeUserAgreement(true);
                     }
                 });
         normalDialog.setNegativeButton("退出", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 // ...To-do
                 finish();
             }
         });
         // 创建实例并显示
         normalDialog.show();
     }

    /*                                                  消息监听结束                            */


/*    表格显示开始            */
//绑定数据
private void initData() {
    //初始化标题
    relativeLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.table, null);
    MyTableTextView title = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_1);
    title.setText(name[0]);
    title.setTextColor(Color.BLUE);

    title = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_2);
    title.setText(name[1]);
    title.setTextColor(Color.BLUE);
    title = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_3);
    title.setText(name[2]);
    title.setTextColor(Color.BLUE);
    title = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_4);
    title.setText(name[3]);
    title.setTextColor(Color.BLUE);
    title = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_5);
    title.setText(name[4]);
    title.setTextColor(Color.BLUE);
    title = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_6);
    title.setText(name[5]);
    title.setTextColor(Color.BLUE);
    title = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_7);
    title.setText(name[6]);
    title.setTextColor(Color.BLUE);
    mainLinerLayout.addView(relativeLayout);

    //初始化内容
    int number = 1;
    for (int i=0;i<10;i++) {
        relativeLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.table, null);
        MyTableTextView txt = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_1);
        txt.setText(String.valueOf(number));

        txt = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_2);
        txt.setText("320321**********35");
        txt = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_3);
        txt.setText("张三");
        txt = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_4);
        txt.setText("1992/04/21");
        txt = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_5);
        txt.setText("150");
        txt = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_6);
        txt.setText("200");
        txt = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_7);
        txt.setText("120");
        mainLinerLayout.addView(relativeLayout);
        number++;
    }
}
    /*表格显示结束*/


//    //返回按钮使用
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if(myWebView.canGoBack()){
//                myWebView.goBack();
//                return true;
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}

//}
