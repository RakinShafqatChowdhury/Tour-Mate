package com.example.tourmatebatch14;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

public class GeofenceReceiver extends BroadcastReceiver {
    private Context context;
    private final String CHANNEL = "myChannel";
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        int transition = event.getGeofenceTransition();
        String transitionString = "";
        switch (transition){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                transitionString = "You've entered!";
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                transitionString = "You've exited!";
                break;
        }

        List<Geofence> triggeringGeofences = event.getTriggeringGeofences();
        List<String> names = new ArrayList<>();
        for(Geofence g : triggeringGeofences){
            names.add(g.getRequestId());
        }

        String notification = transitionString+" "+"to"+" "+ TextUtils.join(",", names);
        setNotification(notification);
    }

    private void setNotification(String notification) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL);
        builder.setSmallIcon(R.drawable.ic_baseline_notifications_active_24);
        builder.setContentTitle("Geofence detected");
        builder.setContentText(notification);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL,"Notified channels",NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        manager.notify(69,builder.build());
    }
}
