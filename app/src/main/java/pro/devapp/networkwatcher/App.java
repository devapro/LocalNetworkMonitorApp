package pro.devapp.networkwatcher;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import pro.devapp.networkwatcher.logic.controllers.NetworkScanController;
import pro.devapp.networkwatcher.storage.AppDataBase;
import pro.devapp.networkwatcher.utils.AlarmUtil;

public class App extends Application {

    public static final String SERVICE_NOTIFICATION_CHANNEL_ID = "ScannerNotification";
    public static final String NOTIFICATION_CHANNEL_ID = "NewDeviceNotification";
    private AppDataBase appDataBase;
    private NetworkScanController networkScanController;

    @Override
    public void onCreate() {
        super.onCreate();
        appDataBase = AppDataBase.init(getApplicationContext());
        networkScanController = new NetworkScanController(getApplicationContext(), appDataBase);
        createNotificationChanel();

        AlarmManager manager = (AlarmManager)getSystemService(
            Context.ALARM_SERVICE);
        if (manager != null) {
            AlarmUtil.setScheduledAlarm(manager, this);
        }
    }

    public AppDataBase getAppDataBase() {
        return appDataBase;
    }

    public NetworkScanController getNetworkScanController() {
        return networkScanController;
    }

    private void createNotificationChanel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel foregroundNotificationChannel = new NotificationChannel(
                SERVICE_NOTIFICATION_CHANNEL_ID,
                "Foreground scan service",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            foregroundNotificationChannel.setSound(null, null);
            foregroundNotificationChannel.setShowBadge(false);
            getSystemService(NotificationManager.class).createNotificationChannel(foregroundNotificationChannel);

            NotificationChannel notificationChannel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "New device detected",
                NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.setShowBadge(true);
            getSystemService(NotificationManager.class).createNotificationChannel(notificationChannel);
        }
    }
}
