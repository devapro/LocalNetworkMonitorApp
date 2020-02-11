package pro.devapp.networkwatcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import pro.devapp.networkwatcher.services.ScanForegroundService;

public class AlarReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ContextCompat.startForegroundService(context, new Intent(context, ScanForegroundService.class));
    }
}
