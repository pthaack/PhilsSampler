package ca.philipyoung.philssampler.util;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.Date;
import java.util.Locale;

import ca.philipyoung.philssampler.ActivityDashboard;
import ca.philipyoung.philssampler.R;

public class AlertReceiver extends BroadcastReceiver {
    private static final String TAG = "AlertReceiver";
    public static final int ACTIVITY_RECEIVER = 16;
    public static final String EXTRA_ACTIVITY = "extra_activity";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Started");
        createNotification(context,
                context.getString(R.string.notify_title_alert_message),
                String.format(Locale.US, context.getString(R.string.notify_description_alert_message), new Date()),
                String.format(Locale.US, context.getString(R.string.notify_ticker_alert_message), new Date()),
                (intent.hasExtra(EXTRA_ACTIVITY) ? intent.getStringExtra(EXTRA_ACTIVITY) : null)
        );
    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert, String strActivity) {
        Log.d(TAG, "createNotification: Started");
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(msg)
                .setContentText(msgText)
                .setTicker(msgAlert)
                .setSmallIcon(R.drawable.ic_menu_notification);
        Intent intent;
        if (strActivity != null)
            intent = new Intent(context, ActivityDashboard.class)
                    .putExtra(EXTRA_ACTIVITY, strActivity);
        else
            intent = new Intent(context, ActivityDashboard.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ActivityDashboard.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(ACTIVITY_RECEIVER, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(ACTIVITY_RECEIVER, notificationBuilder.build());
        }
    }
}
