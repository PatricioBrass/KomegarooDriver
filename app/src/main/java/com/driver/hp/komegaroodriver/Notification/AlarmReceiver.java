package com.driver.hp.komegaroodriver.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.driver.hp.komegaroodriver.MainActivity;
import com.driver.hp.komegaroodriver.R;

/**
 * Created by Patricio on 29-08-2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //PendingIntent pendingIntent = stackBuilder.getPendingIntent(100, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = builder
                .setContentTitle("KomeGaroo Riders")
                .setContentText("Están pidiendo un Komegaroo")
                .setAutoCancel(true)
                .setSound(sound)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setSmallIcon(R.drawable.ic_riders)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);

    }
}
