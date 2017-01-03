package com.dieam.reactnativepushnotification.modules;

import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class RNPushNotificationPublisher extends BroadcastReceiver {
    final static String NOTIFICATION_ID = "notificationId";

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        long currentTime = System.currentTimeMillis();
        Log.i("RNPushNotification", "NotificationPublisher: Prepare To Publish: " + id + ", Now Time: " + currentTime);
        Boolean isRunning = isApplicationRunning(context);
        if(!isRunning) {
            new RNPushNotificationHelper((Application) context.getApplicationContext()).sendNotification(intent.getExtras());
        } else {
            Intent notificationIntent = new Intent(context.getPackageName() + ".RNPushNotificationReceiveNotification");
            Bundle bundle = intent.getExtras();
            bundle.putBoolean("foreground", true);
            bundle.putBoolean("userInteraction", false);
            notificationIntent.putExtra("notification", bundle);
            context.sendBroadcast(notificationIntent);
        }
    }

    // Method copied from RNPushNotificationListenerService. Keep in sync
    private boolean isApplicationRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            if (processInfo.processName.equals(context.getPackageName())) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String d : processInfo.pkgList) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
