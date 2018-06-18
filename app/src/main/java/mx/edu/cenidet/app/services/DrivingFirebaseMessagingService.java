
package mx.edu.cenidet.app.services;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.graphics.Color;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import mx.edu.cenidet.app.activities.AlertHistoryActivity;
import mx.edu.cenidet.app.activities.SplashActivity;
import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.app.R;
import mx.edu.cenidet.app.activities.HomeActivity;

public class DrivingFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "Alertas";
    private Response response = new Response();
    private String severity = "";
    private String location = "";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */

    // [START receive_message]

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData().get("alert"));
            JSONObject jsonObject = response.parseJsonObject(remoteMessage.getData().get("alert").toString());
            try {
                severity = jsonObject.getString("severity");
                location = jsonObject.getString("location");
                Log.d(TAG, "SEVERITY: " + severity+ " LOCATION: "+location);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body-------------: " + remoteMessage.getNotification().getBody());
            showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(), severity);
            newAlertMessage(remoteMessage.getNotification().getTitle());
        }
    }

    private void showNotification(String messageTitle ,String messageBody, String severity) {
        Intent intent = new Intent(this, AlertHistoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
         //       PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = null;

        int notificationId = new Random().nextInt(60000);

        switch (severity){
            case "informational":
                notificationBuilder = new NotificationCompat.Builder(this)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setColorized(true)
                        .setColor(Color.parseColor("#3498db"))
                        .setSmallIcon(R.drawable.ic_car)
                        .setVibrate(new long[] {100, 250, 100, 500})
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
                break;
            case "low":
                notificationBuilder = new NotificationCompat.Builder(this)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setColorized(true)
                        .setColor(Color.parseColor("#2c3e50"))
                        .setSmallIcon(R.drawable.ic_car)
                        .setVibrate(new long[] {100, 250, 100, 500})
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
                break;
            case "medium":
                notificationBuilder = new NotificationCompat.Builder(this)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setColorized(true)
                        .setColor(Color.parseColor("#f1c40f"))
                        .setSmallIcon(R.drawable.ic_car)
                        .setVibrate(new long[] {100, 250, 100, 500})
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
                break;
            case "high":
                notificationBuilder = new NotificationCompat.Builder(this)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setColorized(true)
                        .setColor(Color.parseColor("#e67e22"))
                        .setSmallIcon(R.drawable.ic_car)
                        .setVibrate(new long[] {100, 250, 100, 500})
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
                break;
            case "critical":
                notificationBuilder = new NotificationCompat.Builder(this)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setColorized(true)
                        .setColor(Color.parseColor("#c0392b"))
                        .setSmallIcon(R.drawable.ic_car)
                        .setVibrate(new long[] {100, 250, 100, 500})
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
                break;
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId , notificationBuilder.build());
    }

    private void newAlertMessage(String messageTitle) {

        /*Context context = getApplicationContext();
        Intent serviceIntent = new Intent(context, EntringAlert.class);
        serviceIntent.putExtra("EntringAlert", messageTitle);
        context.startService(serviceIntent);
        HeadlessJsTaskService.acquireWakeLockNow(context);*/

    }

    // [END receive_message]

}