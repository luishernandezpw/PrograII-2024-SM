package com.ugb.controlesbasicos;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyAndroidFCMservice";
    private static final String ADMIN_CHANNEL_ID = "";
    public static final String DISPLAY_MESSAGE_ACTION = "enviarMsg";
    NotificationManager notificationManager;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        crearNotificacionPush(remoteMessage);
        sendNewMsgBroadcast(remoteMessage);
    }
    private void crearNotificacionPush(RemoteMessage remoteMessage){
        Intent intent = new Intent( this, chats.class );
        intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("msg", remoteMessage.getData().get("msg"));
        intent.putExtra("to", remoteMessage.getData().get("para"));
        intent.putExtra("from", remoteMessage.getData().get("de"));
        intent.putExtra("user", remoteMessage.getData().get("user"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels();
        }
        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder( this, ADMIN_CHANNEL_ID)                .
                setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Has recibido un mensaje de "+ remoteMessage.getData().get("user"))
                .setContentText(remoteMessage.getData().get("msg"))
                .setAutoCancel( true )
                .setSound(notificationSoundURI)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mNotificationBuilder.build());
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(){
        CharSequence adminChannelName = "miCanal";
        String adminChannelDescription = "Esta es mi cana de comunicacion";
        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }
    private void sendNewMsgBroadcast(RemoteMessage remoteMessage) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra("msg", remoteMessage.getData().get("msg"));
        intent.putExtra("to", remoteMessage.getData().get("para"));
        intent.putExtra("from", remoteMessage.getData().get("de"));
        intent.putExtra("user", remoteMessage.getData().get("user"));
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}
