package com.dieam.reactnativepushnotification.modules;

import android.content.Intent;
import android.os.Bundle;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.util.Log;

import java.util.List;
import java.util.Random;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONObject;

public class RNPushNotificationListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle bundle) {
        JSONObject data = getPushData(bundle.getString("data"));
        if (data != null) {
            if (!bundle.containsKey("message")) {
                bundle.putString("message", data.optString("alert", "Notification received"));
            }
            if (!bundle.containsKey("title")) {
                bundle.putString("title", data.optString("title", null));
            }
        }

        sendNotification(bundle);
    }

    private JSONObject getPushData(String dataString) {
        try {
            return new JSONObject(dataString);
        } catch (Exception e) {
            return null;
        }
    }

    private void sendNotification(Bundle bundle) {

        // If notification ID is not provided by the user for push notification, generate one at random
        if ( bundle.getString("id") == null ) {
            Random randomNumberGenerator = new Random(System.currentTimeMillis());
            bundle.putString("id", String.valueOf(randomNumberGenerator.nextInt()));
        }

        // If the application is running, then emit the notification received event
        // Else, show the notification and emit the remote fetch event
        Boolean isRunning = isApplicationRunning();

        if (!isRunning) {
            RNPushNotificationHelper rnPushNotificationHelper = new RNPushNotificationHelper(getApplication());
            rnPushNotificationHelper.sendNotification(bundle);
            // If contentAvailable is set to true, then send out a remote fetch event
            if(bundle.getString("contentAvailable", "false").equalsIgnoreCase("true")) {
                Log.d(bundle.toString(), "Received a notification with remote fetch enabled");
                Intent remoteFetchIntent = new Intent(this.getPackageName() + ".RNPushNotificationRemoteFetch");
                remoteFetchIntent.putExtra("notification", bundle);
                sendBroadcast(remoteFetchIntent);
            }
        } else {
            Intent intent = new Intent(this.getPackageName() + ".RNPushNotificationReceiveNotification");
            bundle.putBoolean("foreground", true);
            bundle.putBoolean("userInteraction", false);
            intent.putExtra("notification", bundle);
            sendBroadcast(intent);
        }
    }

    // Method copied to RNPushNotificationListenerService. Keep in sync
    private boolean isApplicationRunning() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            if (processInfo.processName.equals(getApplication().getPackageName())) {
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
