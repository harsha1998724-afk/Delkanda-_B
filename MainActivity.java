package com.freshnessdesk.cafe;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.util.Calendar;

public class MainActivity extends Activity {
    private WebView webView;
    private static final int NOTIFICATION_PERMISSION_REQUEST = 44;
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createChannel();
        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new AndroidNotifications(this), "AndroidNotifications");
        setContentView(webView);
        webView.loadUrl("file:///android_asset/index.html");
    }
    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("freshness_reminders",
                "Expiry reminders", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Daily cafe stock expiry reminders");
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }
    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack(); else super.onBackPressed();
    }
    public class AndroidNotifications {
        private final Context context;
        AndroidNotifications(Context c) { context = c; }
        @JavascriptInterface public void requestPermission() {
            runOnUiThread(() -> {
                if (Build.VERSION.SDK_INT >= 33 &&
                    checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST);
                }
            });
        }
        @JavascriptInterface public void showNotification(String title, String body) {
            requestPermission();
            runOnUiThread(() -> {
                if (Build.VERSION.SDK_INT >= 33 &&
                    checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Allow notifications to receive expiry reminders.", Toast.LENGTH_LONG).show();
                    return;
                }
                ReminderReceiver.show(context, title, body);
            });
        }
        @JavascriptInterface public void scheduleDailyReminder(String time, String body) {
            String[] parts = time.split(":");
            if (parts.length != 2) return;
            try {
                int hour = Integer.parseInt(parts[0]), minute = Integer.parseInt(parts[1]);
                Intent intent = new Intent(context, ReminderReceiver.class);
                intent.setAction(ReminderReceiver.ACTION_DAILY);
                intent.putExtra("body", body);
                PendingIntent pi = PendingIntent.getBroadcast(context, 1207, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, hour); c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
                if (c.getTimeInMillis() <= System.currentTimeMillis()) c.add(Calendar.DAY_OF_YEAR, 1);
                if (Build.VERSION.SDK_INT >= 23) am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
                else am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
                ReminderPrefs.save(context, hour, minute, body, true);
                requestPermission();
            } catch (Exception ignored) {}
        }
        @JavascriptInterface public void cancelDailyReminder() {
            Intent intent = new Intent(context, ReminderReceiver.class);
            intent.setAction(ReminderReceiver.ACTION_DAILY);
            PendingIntent pi = PendingIntent.getBroadcast(context, 1207, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            ((AlarmManager)context.getSystemService(ALARM_SERVICE)).cancel(pi);
            ReminderPrefs.save(context, 8, 0, "", false);
        }
    }
}
