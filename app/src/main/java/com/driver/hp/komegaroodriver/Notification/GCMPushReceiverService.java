package com.driver.hp.komegaroodriver.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.driver.hp.komegaroodriver.Fragment.MapsFragment;
import com.driver.hp.komegaroodriver.MainActivity;
import com.driver.hp.komegaroodriver.R;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Pato on 27/03/2017.
 */
public class GCMPushReceiverService extends GcmListenerService {

    private String notifi;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        notifi = data.getString("notId");
        Log.v("Data notificaciones",data.toString());
        sendNotification(message);
    }
    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = Integer.parseInt(notifi);//Your request code
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        //Setup notification
        //Sound
        //MapsFragment.newInstance("ImgTwoFragment, Instance 2").send();
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Build notification
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("KomeGaroo Riders")
                .setSound(sound)
                .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent))
                .setSmallIcon(R.drawable.ic_riders)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Integer.parseInt(notifi), noBuilder.build()); //0 = ID of notification
    }
}