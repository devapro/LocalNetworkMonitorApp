package pro.devapp.networkwatcher;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import pro.devapp.networkwatcher.logic.NetworkController;
import pro.devapp.networkwatcher.storage.AppDataBase;

public class App extends Application {

    public static final String CHANNEL_ID = "ScannerNotification";
    private AppDataBase appDataBase;
    private NetworkController networkController;

    @Override
    public void onCreate() {
        super.onCreate();
        appDataBase = AppDataBase.init(getApplicationContext());
        networkController = new NetworkController(getApplicationContext(), appDataBase);
        createNotificationChanel();
    }

    public AppDataBase getAppDataBase() {
        return appDataBase;
    }

    public NetworkController getNetworkController() {
        return networkController;
    }

    private void createNotificationChanel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,
                "Timer",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationChannel.setSound(null, null);
            notificationChannel.setShowBadge(false);
            getSystemService(NotificationManager.class).createNotificationChannel(notificationChannel);
        }
    }
}
