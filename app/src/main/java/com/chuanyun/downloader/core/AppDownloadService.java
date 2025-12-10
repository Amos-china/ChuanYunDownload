package com.chuanyun.downloader.core;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.chuanyun.downloader.R;
import com.chuanyun.downloader.models.AppSettingsModel;

public class AppDownloadService extends Service {
    public class AppDownloadBinder extends Binder {
        AppDownloadService getService() {
            return AppDownloadService.this;
        }
    }

    private final IBinder binder = new AppDownloadBinder();
    private TTDownloadTask downloadTask;

    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_LOW.equals(action)) {
                AppSettingsModel settingsModel = AppSettingsModel.getSettingsModel();
                if (!settingsModel.isLowBatteryDownload()) {
                    if (downloadTask != null) {
                        downloadTask.stopAllTask();
                    }
                    Toast.makeText(context, "电量过低，已暂停所有下载任务", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    public TTDownloadTask createDownloadTask() {
        if (downloadTask == null) {
            downloadTask = new TTDownloadTask();
        }
        return downloadTask;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        // 创建通知频道（适用于 Android 8.0 及以上）
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("download_service_id",
                    "高速下载引擎运行中", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
        // 前台服务通知
        Notification notification = new NotificationCompat.Builder(this, "download_service_id")
                .setContentTitle("穿云下载")
                .setContentText("高速下载引擎运行中")
                .setSmallIcon(R.mipmap.logo)
                .build();
        startForeground(1, notification);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        registerReceiver(batteryReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(batteryReceiver);
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
