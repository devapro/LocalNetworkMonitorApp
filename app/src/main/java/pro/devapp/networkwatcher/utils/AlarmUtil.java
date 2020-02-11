package pro.devapp.networkwatcher.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;
import pro.devapp.networkwatcher.AlarReceiver;

public class AlarmUtil {
    public static void setScheduledAlarm(AlarmManager manager, Context context){

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 101, new Intent(context, AlarReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 10);
        long time = calendar.getTimeInMillis();

        manager.setRepeating(AlarmManager.RTC_WAKEUP, time,
            AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
    }
}
