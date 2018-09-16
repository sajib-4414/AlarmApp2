package gnc.alarmapp2;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;



import java.util.concurrent.atomic.AtomicInteger;

public class NotificationHandler {

    public static class NotificationMessage{
        String body;
        String title;
        public NotificationMessage(String title, String body){
            this.title = title;
            this.body = body;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body=body;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title=title;
        }
    }

    private final static AtomicInteger c = new AtomicInteger(0);
    private final static String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";


    /**
     * Shows a single notification without pending intent
     * @param context
     * @param remoteMessage
     * @param notificationIconResId
     */
    public static void showNotificationWithoutPendingIntent(Context context, final NotificationMessage remoteMessage, int notificationIconResId) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), notificationIconResId);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(notificationIconResId)
                .setLargeIcon(largeIcon)
                .setTicker(remoteMessage.getBody())
                .setContentTitle(remoteMessage.getTitle());

        notificationManager.notify(getID(), notificationBuilder.build());
    }


    /**
     * Shows a single notification with pending intent
     * @param context
     * @param remoteMessage
     * @param cls
     * @param notificationIconResId
     */
    public static void showNotificationWithPendingIntent(Context context, final NotificationMessage remoteMessage, Class<?> cls, int notificationIconResId) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

        Intent gcmintent = new Intent(context, cls);
        gcmintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int requestID = (int) System.currentTimeMillis();


        PendingIntent contentIntent = PendingIntent.getActivity(context, requestID,
                gcmintent, PendingIntent.FLAG_UPDATE_CURRENT);


        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), notificationIconResId);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(notificationIconResId)
                .setLargeIcon(largeIcon)
                .setTicker(remoteMessage.getBody())
                .setContentTitle(remoteMessage.getTitle())
                .setContentIntent(contentIntent);

        notificationManager.notify(getID(), notificationBuilder.build());
    }


    /**
     * Returns a unique integer id to generate notification builder
     * @return
     */
    private static int getID() {
        return c.incrementAndGet();
    }

}
