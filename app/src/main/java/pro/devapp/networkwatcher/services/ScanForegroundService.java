package pro.devapp.networkwatcher.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import pro.devapp.networkwatcher.App;
import pro.devapp.networkwatcher.MainActivity;
import pro.devapp.networkwatcher.R;
import pro.devapp.networkwatcher.logic.ProgressScanDispatcher;

public class ScanForegroundService extends Service {

    private final int NOTIFICATION_ID = 234;

    @Override
    public void onCreate() {
        super.onCreate();
        ((App)getApplication()).getNetworkController().getProgressScanDispatcher().addListener(callback);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, createNotification(0));
        ((App)getApplication()).getNetworkController().scan();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((App)getApplication()).getNetworkController().getProgressScanDispatcher().removeListener(callback);
    }

    private Notification createNotification(int progress) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //TODO
        Intent stopIntent = new Intent();
        stopIntent.setAction(ScanForegroundService.class.getName() + "stop");
        PendingIntent pendingIntentStop = PendingIntent.getBroadcast(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews customRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_scanner);
        customRemoteViews.setTextViewText(R.id.progress, progress + " %");

        customRemoteViews.setOnClickPendingIntent(R.id.stop, pendingIntentStop);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(App.CHANNEL_ID);
        }
        return builder
            .setContentTitle(getString(R.string.app_name))
            .setContentText(progress + " %")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCustomContentView(customRemoteViews)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setAutoCancel(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setSound(null)
            .build();
    }

    private void updateNotification(int progress) {
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, createNotification(progress));
    }

    public final class LocalBinder extends Binder {
        public ScanForegroundService getService(){
            return ScanForegroundService.this;
        }
    }

    private final ProgressScanDispatcher.ProgressCallback callback = new ProgressScanDispatcher.ProgressCallback() {
        @Override
        public void onStart() {

        }

        @Override
        public void onProgress(int progress) {
            updateNotification(progress);
        }

        @Override
        public void onEnd(boolean success) {
            stopForeground(true);
            stopSelf();
        }
    };
}
