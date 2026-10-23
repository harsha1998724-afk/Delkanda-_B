package com.freshnessdesk.cafe;
import android.content.Context;
import android.content.SharedPreferences;
public class ReminderPrefs {
    static class Prefs { int hour, minute; String body; boolean enabled; }
    static void save(Context c, int h, int m, String body, boolean enabled) {
        c.getSharedPreferences("reminder", Context.MODE_PRIVATE).edit()
            .putInt("hour",h).putInt("minute",m).putString("body",body).putBoolean("enabled",enabled).apply();
    }
    static Prefs read(Context c) {
        SharedPreferences s=c.getSharedPreferences("reminder",Context.MODE_PRIVATE);
        Prefs p=new Prefs(); p.hour=s.getInt("hour",8); p.minute=s.getInt("minute",0);
        p.body=s.getString("body","Check cafe stock expiry dates."); p.enabled=s.getBoolean("enabled",false); return p;
    }
}
