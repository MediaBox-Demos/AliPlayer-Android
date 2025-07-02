package com.aliyun.player.alivcplayerexpand.util;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import static androidx.core.app.NotificationCompat.PRIORITY_DEFAULT;
import static androidx.core.app.NotificationCompat.VISIBILITY_SECRET;

public class NotificationUtils extends ContextWrapper {

    public static final String CHANNEL_ID = "default";
    private static final String CHANNEL_NAME = "Default_Channel";
    private NotificationManager mManager;
    private int[] flags;

    public NotificationUtils(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //android 8.0以上需要特殊处理，也就是targetSDKVersion为26以上
            //android 8.0+ requires special handling, i.e. targetSDKVersion of 26 or more
            createNotificationChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        //第一个参数：channel_id
        //First parameter: channel_id
        //第二个参数：channel_name
        //Second parameter: channel_name
        //第三个参数：设置通知重要性级别
        //Third parameter: set notification importance level
        //注意：该级别必须要在 NotificationChannel 的构造函数中指定，总共要五个级别；
        //Note: This level must be specified in the NotificationChannel's constructor, for a total of five levels;
        //范围是从 NotificationManager.IMPORTANCE_NONE(0) ~ NotificationManager.IMPORTANCE_HIGH(4)
        //Range is from NotificationManager.IMPORTANCE_NONE(0) ~ NotificationManager.IMPORTANCE_HIGH(4)
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.canBypassDnd();//是否绕过请勿打扰模式 Whether to bypass Do Not Disturb mode
        channel.enableLights(true);//闪光灯 flash bulb (photography)
        channel.setLockscreenVisibility(VISIBILITY_SECRET);//锁屏显示通知 Lock screen display notification
        channel.setLightColor(Color.RED);//闪关灯的灯光颜色 flash bulb light color
        channel.canShowBadge();//桌面launcher的消息角标 Desktop launcher's message corner
        channel.enableVibration(true);//是否允许震动 Whether to allow vibration
        channel.getAudioAttributes();//获取系统通知响铃声音的配置 Get system notification ringtone configuration
        channel.getGroup();//获取通知取到组 Get notification group
        channel.setBypassDnd(true);//设置可绕过 请勿打扰模式 Set can bypass Do Not Disturb mode
        channel.setVibrationPattern(new long[] {100, 100, 200});//设置震动模式 Set vibration mode
        channel.shouldShowLights();//是否会有灯光 Whether there will be a light
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(channel);
    }

    /**
     * 获取创建一个NotificationManager的对象
     *
     * @return NotificationManager对象
     */
    /****
     * Get the object that creates a NotificationManager
     *
     * @return NotificationManager object
     */
    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    /**
     * 清空所有的通知
     */
    /****
     * Clear all notifications
     */
    public void clearNotification() {
        getManager().cancelAll();
    }

    /**
     * 获取Notification
     *
     * @param title   title
     * @param content content
     */
    /****
     * Get Notification
     *Notification
     * @param title title
     * @param content content
     */
    public Notification getNotification(Context context, String title, String content, int icon) {
        Notification build;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //android 8.0以上需要特殊处理，也就是targetSDKVersion为26以上
            //android 8.0 and above requires special handling, i.e. targetSDKVersion of 26 and above
            //通知用到NotificationCompat()这个V4库中的方法。但是在实际使用时发现书上的代码已经过时并且Android8.0已经不支持这种写法
            //Notifications use NotificationCompat(), a method in the V4 library. However, in actual use, I found the code in the book has already expired and Android 8.0 does not support this
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), icon);
            Notification.Builder builder = getChannelNotification(title, content, icon, bitmap);
            build = builder.build();
        } else {
            NotificationCompat.Builder builder = getNotificationCompat(title, content, icon);
            build = builder.build();
        }
        if (flags != null && flags.length > 0) {
            for (int a = 0; a < flags.length; a++) {
                build.flags |= flags[a];
            }
        }
        return build;
    }

    /**
     * 建议使用这个发送通知
     * 调用该方法可以发送通知
     *
     * @param notifyId notifyId
     * @param title    title
     * @param content  content
     */
    /****
     * Recommended to use this send notification
     * Call this method to send notification
     *
     * @param notifyId notifyId
     * @param title    title
     * @param content  content
     */
    public void sendNotification(int notifyId, String title, String content, int icon) {
        Notification build;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //android 8.0以上需要特殊处理，也就是targetSDKVersion为26以上
            //android 8.0 and above requires special handling, i.e. targetSDKVersion of 26 and above
            //通知用到NotificationCompat()这个V4库中的方法。但是在实际使用时发现书上的代码已经过时并且Android8.0已经不支持这种写法
            //Notifications use NotificationCompat(), a method in the V4 library. However, in actual use, I found the code in the book has already expired and Android 8.0 does not support this
            Notification.Builder builder = getChannelNotification(title, content, icon, null);
            build = builder.build();
        } else {
            NotificationCompat.Builder builder = getNotificationCompat(title, content, icon);
            build = builder.build();
        }
        if (flags != null && flags.length > 0) {
            for (int a = 0; a < flags.length; a++) {
                build.flags |= flags[a];
            }
        }
        getManager().notify(notifyId, build);
    }

    /**
     * 调用该方法可以发送通知
     *
     * @param notifyId notifyId
     * @param title    title
     * @param content  content
     */
    /****
     * Call this method to send notification
     *
     * @param notifyId notifyId
     * @param title    title
     * @param content  content
     */
    public void sendNotificationCompat(int notifyId, String title, String content, int icon) {
        NotificationCompat.Builder builder = getNotificationCompat(title, content, icon);
        Notification build = builder.build();
        if (flags != null && flags.length > 0) {
            for (int a = 0; a < flags.length; a++) {
                build.flags |= flags[a];
            }
        }
        getManager().notify(notifyId, build);
    }

    private NotificationCompat.Builder getNotificationCompat(String title, String content, int icon) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        } else {
            //注意用下面这个方法，在8.0以上无法出现通知栏。8.0之前是正常的。这里需要增强判断逻辑
            //Note: Use this method, which cannot appear in the notification bar on Android 8.0 and above. Before Android 8.0, it was normal. Here needs to enhance the judgment logic
            builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setPriority(PRIORITY_DEFAULT);
        }
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(icon);
        builder.setPriority(priority);
        builder.setOnlyAlertOnce(onlyAlertOnce);
        builder.setOngoing(ongoing);
        if (remoteViews != null) {
            builder.setContent(remoteViews);
            builder.setCustomBigContentView(remoteViews);
        }
        if (intent != null) {
            builder.setContentIntent(intent);
        }
        if (ticker != null && ticker.length() > 0) {
            builder.setTicker(ticker);
        }
        if (when != 0) {
            builder.setWhen(when);
        }
        if (sound != null) {
            builder.setSound(sound);
        }
        if (defaults != 0) {
            builder.setDefaults(defaults);
        }
        //点击自动删除通知
        //Click automatically delete notification
        builder.setAutoCancel(true);
        return builder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification.Builder getChannelNotification(String title, String content, int icon, Bitmap largeIcon) {
        Notification.Builder builder = new Notification.Builder(getApplicationContext(), CHANNEL_ID);
        Notification.Builder notificationBuilder = builder
                //设置标题
                //Title
                .setContentTitle(title)
                //消息内容
                //Message content
                .setContentText(content)
                //设置通知的图标
                //Setting the icon for notifications
                .setSmallIcon(icon)
                .setLargeIcon(largeIcon)
                //让通知左右滑的时候是否可以取消通知
                //Let notifications be canceled when sliding
                .setOngoing(ongoing)
                //设置优先级
                //Setting priority
                .setPriority(priority)
                //是否提示一次.true - 如果Notification已经存在状态栏即使在调用notify函数也不会更新
                //Whether or not to prompt once.true - if the Notification already exists the status bar will not be updated even if the notify function is called
                .setOnlyAlertOnce(onlyAlertOnce)
                .setAutoCancel(true);
        if (remoteViews != null) {
            //设置自定义view通知栏
            //Custom view notification bar
            notificationBuilder.setContent(remoteViews);
            notificationBuilder.setCustomBigContentView(remoteViews);
        }
        if (intent != null) {
            notificationBuilder.setContentIntent(intent);
        }
        if (ticker != null && ticker.length() > 0) {
            //设置状态栏的标题
            //Setting the title of the status bar
            notificationBuilder.setTicker(ticker);
        }
        if (when != 0) {
            //设置通知时间，默认为系统发出通知的时间，通常不用设置
            //Setting the notification time, which is the default time of the system, usually not to set
            notificationBuilder.setWhen(when);
        }
        if (sound != null) {
            //设置sound
            //Setting sound
            notificationBuilder.setSound(sound);
        }
        if (defaults != 0) {
            //设置默认的提示音
            //Setting the default prompt sound
            notificationBuilder.setDefaults(defaults);
        }
        if (pattern != null) {
            //自定义震动效果
            //Custom vibration effect
            notificationBuilder.setVibrate(pattern);
        }
        return notificationBuilder;
    }

    private boolean ongoing = false;
    private RemoteViews remoteViews = null;
    private PendingIntent intent = null;
    private String ticker = "";
    private int priority = Notification.PRIORITY_DEFAULT;
    private boolean onlyAlertOnce = false;
    private long when = 0;
    private Uri sound = null;
    private int defaults = 0;
    private long[] pattern = null;

    /**
     * 让通知左右滑的时候是否可以取消通知
     *
     * @param ongoing 是否可以取消通知
     * @return
     */
    /****
     * Let notifications be canceled when sliding
     *
     * @param ongoing Can the notification be canceled when sliding
     * @return
     */
    public NotificationUtils setOngoing(boolean ongoing) {
        this.ongoing = ongoing;
        return this;
    }

    /**
     * 设置自定义view通知栏布局
     *
     * @param remoteViews view
     * @return
     */
    /****
     * Set custom view notification bar layout
     *
     * @param remoteViews view
     * @return
     */
    public NotificationUtils setContent(RemoteViews remoteViews) {
        this.remoteViews = remoteViews;
        return this;
    }

    /**
     * 设置内容点击
     *
     * @param intent intent
     * @return
     */
    /****
     * Set content click
     *
     * @param intent intent
     * @return
     */
    public NotificationUtils setContentIntent(PendingIntent intent) {
        this.intent = intent;
        return this;
    }

    /**
     * 设置状态栏的标题
     *
     * @param ticker 状态栏的标题
     * @return
     */
    /****
     * Set the title of the status bar
     *
     * @param ticker Status bar title
     * @return
     */
    public NotificationUtils setTicker(String ticker) {
        this.ticker = ticker;
        return this;
    }

    /**
     * 设置优先级
     * 注意：
     * Android 8.0以及上，在 NotificationChannel 的构造函数中指定，总共要五个级别；
     * Android 7.1（API 25）及以下的设备，还得调用NotificationCompat 的 setPriority方法来设置
     *
     * @param priority 优先级，默认是Notification.PRIORITY_DEFAULT
     * @return
     */
    /****
     * Set priority
     * Note:
     * On Android 8.0 and above, in the NotificationChannel constructor, specify five levels;
     * On devices with Android 7.1 (API 25) and below, you must also call NotificationCompat setPriority to set
     *
     * @param priority Priority, the default is Notification.PRIORITY_DEFAULT
     * @return
     */
    public NotificationUtils setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    /**
     * 是否提示一次.true - 如果Notification已经存在状态栏即使在调用notify函数也不会更新
     *
     * @param onlyAlertOnce 是否只提示一次，默认是false
     * @return
     */
    /****
     * Whether or not to prompt once.true - if the Notification already exists the status bar will not be updated even if the notify function is called
     *
     * @param onlyAlertOnce Is it only prompted once, the default is false
     * @return
     */
    public NotificationUtils setOnlyAlertOnce(boolean onlyAlertOnce) {
        this.onlyAlertOnce = onlyAlertOnce;
        return this;
    }

    /**
     * 设置通知时间，默认为系统发出通知的时间，通常不用设置
     *
     * @param when when
     * @return
     */
    /****
     * Set the notification time, which is the default time of the system, usually not to set
     *
     * @param when When
     * @return
     */
    public NotificationUtils setWhen(long when) {
        this.when = when;
        return this;
    }

    /**
     * 设置sound
     *
     * @param sound sound
     * @return
     */
    /****
     * Set sound
     *
     * @param sound Sound
     * @return
     */
    public NotificationUtils setSound(Uri sound) {
        this.sound = sound;
        return this;
    }

    /**
     * 设置默认的提示音
     *
     * @param defaults defaults
     * @return
     */
    /****
     * Set the default prompt sound
     *
     * @param defaults Defaults
     * @return
     */
    public NotificationUtils setDefaults(int defaults) {
        this.defaults = defaults;
        return this;
    }

    /**
     * 自定义震动效果
     *
     * @param pattern pattern
     * @return
     */
    /****
     * Custom vibration effect
     *
     * @param pattern Pattern
     * @return
     */
    public NotificationUtils setVibrate(long[] pattern) {
        this.pattern = pattern;
        return this;
    }

    /**
     * 设置flag标签
     *
     * @param flags flags
     * @return
     */
    /****
     * Set flag label
     *
     * @param flags Flags
     * @return
     */
    public NotificationUtils setFlags(int... flags) {
        this.flags = flags;
        return this;
    }

}
