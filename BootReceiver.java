package com.freshnessdesk.cafe;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import java.util.Calendar;
public class BootReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        ReminderPrefs.Prefs p=ReminderPrefs.read(context); if(!p.enabled)return;
        Intent i=new Intent(context,ReminderReceiver.class); i.setAction(ReminderReceiver.ACTION_DAILY); i.putExtra("body",p.body);
        PendingIntent pi=PendingIntent.getBroadcast(context,1207,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Calendar c=Calendar.getInstance();c.set(Calendar.HOUR_OF_DAY,p.hour);c.set(Calendar.MINUTE,p.minute);c.set(Calendar.SECOND,0);c.set(Calendar.MILLISECOND,0);
        if(c.getTimeInMillis()<=System.currentTimeMillis())c.add(Calendar.DAY_OF_YEAR,1);
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(Build.VERSION.SDK_INT>=23)am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pi);else am.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pi);
    }
}
