package com.freshnessdesk.cafe;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import java.util.Calendar;

public class ReminderReceiver extends BroadcastReceiver {
    public static final String ACTION_DAILY = "com.freshnessdesk.cafe.DAILY_REMINDER";
    @Override public void onReceive(Context context, Intent intent) {
        ReminderPrefs.Prefs p = ReminderPrefs.read(context);
        if (!p.enabled) return;
        show(context, "Cafe stock expiry reminder", p.body);
        Intent next = new Intent(context, ReminderReceiver.class);
        next.setAction(ACTION_DAILY);
        next.putExtra("body", p.body);
        PendingIntent pi = PendingIntent.getBroadcast(context, 1207, next,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, p.hour); c.set(Calendar.MINUTE, p.minute);
        c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DAY_OF_YEAR, 1);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
        else am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
    }
    public static void show(Context context, String title, String body) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel("freshness_reminders", "Expiry reminders", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        }
        Intent launch = new Intent(context, MainActivity.class);
        PendingIntent content = PendingIntent.getActivity(context, 1, launch,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder builder = Build.VERSION.SDK_INT >= 26
            ? new Notification.Builder(context, "freshness_reminders")
            : new Notification.Builder(context);
        Notification n = builder.setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title).setContentText(body.replace('\n',' '))
            .setStyle(new Notification.BigTextStyle().bigText(body))
            .setAutoCancel(true).setContentIntent(content).build();
        ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(1207, n);
    }
}
