package com.kart.support;

import android.content.Intent;

import com.kart.activity.LoginActivity;
import com.kart.activity.NotificationPostActivity;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class MyNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;

        System.out.println("DATA FROM ONE SIGNAL " + data);

        if (data != null) {
            try {
                System.out.println("LOG STATUS " + LoginSharedPreference.getLoggedStatus(App.getContext()));
                if (LoginSharedPreference.getLoggedStatus(App.getContext())) {
                    Intent intent = new Intent(App.getContext(), NotificationPostActivity.class);
                    intent.putExtra("key", "Shopping");
                    intent.putExtra("postIndexId", data.getString("postIndexId"));
                    intent.putExtra("shopIndexId", data.getString("shopIndexId"));
                    intent.putExtra("shopType", data.getString("shopType"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                    App.getContext().startActivity(intent);
                } else {
                    Intent intent = new Intent(App.getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                    App.getContext().startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //If we send notification with action buttons we need to specidy the button id's and retrieve it to
        //do the necessary operation.
        if (actionType == OSNotificationAction.ActionType.ActionTaken) {

        }
    }
}
