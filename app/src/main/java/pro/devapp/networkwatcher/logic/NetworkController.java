package pro.devapp.networkwatcher.logic;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import pro.devapp.networkwatcher.logic.entity.DeviceEntity;
import pro.devapp.networkwatcher.storage.AppDataBase;
import pro.devapp.networkwatcher.storage.entity.NetworkDeviceEntity;
import pro.devapp.networkwatcher.storage.entity.NetworkDeviceHistoryEntity;

import static android.content.Context.WIFI_SERVICE;

/**
 * Private IP addresses start with 10, 172, or 192 which are Class A, B, and C respectively. The subnet masks are 255.0.0.0, 255.255.0.0, and 255.255.255.0 respectively, /8, /16, and /24 in CIDR notation.
 */

/**
 * WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
 * WifiInfo wifiInf = wifiMan.getConnectionInfo();
 * int ipAddress = wifiInf.getIpAddress();
 * String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
 */

// https://stackoverflow.com/questions/6064510/how-to-get-ip-address-of-the-device-from-code
    //https://www.android-examples.com/get-display-ip-address-of-android-phone-device-programmatically/
public class NetworkController {

    private final Context context;
    private final AppDataBase dataBase;

    private final Executor uiThreadExecutor;
    private final ExecutorService workThreadExecutor;

    private final ProgressScanDispatcher progressScanDispatcher = new ProgressScanDispatcher();

    private final AtomicBoolean inProgress = new AtomicBoolean(false);
    private final AtomicBoolean forceStop = new AtomicBoolean(false);

    public NetworkController(Context context, AppDataBase dataBase) {
        this.context = context;
        this.dataBase = dataBase;
        uiThreadExecutor = ContextCompat.getMainExecutor(context);
        workThreadExecutor = Executors.newFixedThreadPool(2);
    }

    public ProgressScanDispatcher getProgressScanDispatcher() {
        return progressScanDispatcher;
    }

    public void scan(){
        if(inProgress.compareAndSet(false, true)){
            progressScanDispatcher.dispatchStart();
            workThreadExecutor.execute(() -> {
                final boolean success = scanP();
                uiThreadExecutor.execute(() -> {
                    progressScanDispatcher.dispatchEnd(success);
                });
            });
        }
    }

    public void stopScan(){
        forceStop.compareAndSet(false, true);
    }

    private boolean scanP(){
        int maxIp = 255;
        String ip = getMyWiFiIp();
        try {
            NetworkInterface iFace = NetworkInterface
                .getByInetAddress(InetAddress.getByName(ip));

            for (int i = 0; i <= maxIp; i++) {

                if (forceStop.compareAndSet(true, false)){
                    return true;
                }

                final int progress = (i/(maxIp/100));
                uiThreadExecutor.execute(() -> {
                    progressScanDispatcher.dispatchProgress(progress);
                });

                // build the next IP address
                ip = ip.substring(0, ip.lastIndexOf('.') + 1) + i;
                InetAddress pingAddr = InetAddress.getByName(ip);

                if (pingAddr.isReachable(1000)) {
                    Log.d("PING", pingAddr.getHostAddress());
//                    Log.d("PING", pingAddr.getHostName());
//                    Log.d("PING", pingAddr.getCanonicalHostName());
                    Log.d("PING", pingAddr.getCanonicalHostName());

                    String mac = getMacAddress(pingAddr.getHostAddress());
                    Log.d("PING", "- " + mac);
                    String info = "";
                    if(mac != null){
                        info = getInfo(mac);
                    }
                    setDeviceOnline(ip, mac, info);
                }
                else {
                    setDeviceOffline(ip);
                }

//                // 50ms Timeout for the "ping"
//                if (pingAddr.isReachable(iFace, 200, 50)) {
//                    Log.d("PING", pingAddr.getHostAddress());
//                    Log.d("PING", pingAddr.getHostName());
//                    Log.d("PING", pingAddr.getCanonicalHostName());
//                }
//                else {
//                    Log.d("PING FAIL", pingAddr.getHostAddress());
//                }

            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    private void setDeviceOffline(String ip){
        NetworkDeviceEntity deviceEntity = dataBase.networkDeviceDao().findByIp(ip);
        if(deviceEntity != null){
            NetworkDeviceHistoryEntity lastState = dataBase.networkDeviceHistoryDao().getLastState(deviceEntity.getId());
            if(lastState == null || lastState.getStateType() != NetworkDeviceHistoryEntity.State.OFFLINE){
                dataBase.networkDeviceHistoryDao().add(new NetworkDeviceHistoryEntity(
                    0,
                    deviceEntity.getId(),
                    ip,
                    NetworkDeviceHistoryEntity.State.OFFLINE,
                    new Date().getTime()
                ));
            }
        }
    }

    private void setDeviceOnline(String ip, String mac, String info){
        NetworkDeviceEntity deviceEntity = null;
        if(mac != null) {
            deviceEntity = dataBase.networkDeviceDao().findByMac(mac);
        }
        else {
            deviceEntity = dataBase.networkDeviceDao().findByIp(ip);
            if(deviceEntity != null && !TextUtils.isEmpty(deviceEntity.getMac())){
                deviceEntity = null;
            }
        }
        if(deviceEntity == null){
            long newDeviceId = dataBase.networkDeviceDao().add(new NetworkDeviceEntity(
                0,
                info,
                mac,
                ip,
                new Date().getTime(),
                new Date().getTime()
            ));
            deviceEntity = dataBase.networkDeviceDao().findById(newDeviceId);
        }
        if(deviceEntity != null){
            NetworkDeviceHistoryEntity lastState = dataBase.networkDeviceHistoryDao().getLastState(deviceEntity.getId());
            if(lastState == null || lastState.getStateType() != NetworkDeviceHistoryEntity.State.ONLINE){
                progressScanDispatcher.dispatchNewDeviceFound(new DeviceEntity(
                    deviceEntity.getId(),
                    deviceEntity.getName(),
                    deviceEntity.getIp(),
                    deviceEntity.getMac(),
                    deviceEntity.getCreatedAt(),
                    deviceEntity.getUpdatedAt(),
                    NetworkDeviceHistoryEntity.State.ONLINE
                ));
                dataBase.networkDeviceHistoryDao().add(new NetworkDeviceHistoryEntity(
                    0,
                    deviceEntity.getId(),
                    ip,
                    NetworkDeviceHistoryEntity.State.ONLINE,
                    new Date().getTime()
                ));
            }
        }
    }

    private String getMyWiFiIp(){
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    @Nullable
    private String getMacAddress(@NonNull String ipAddress) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("/proc/net/arp")));
            String line;
            while((line = br.readLine()) != null) {
                if(line.contains(ipAddress)) {
                    /* this string still would need to be sanitized */
                    //return line;
                    String[] splitted = line.split(" +");
                    if (splitted.length >= 4 && ipAddress.equals(splitted[0])) {
                        // Basic sanity check
                        String mac = splitted[3];
                        if (mac.matches("..:..:..:..:..:..")) {
                            return mac;
                        } else {
                            return null;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    private String getInfo(@NonNull String macAdress){
        String dataUrl = "http://api.macvendors.com/" + macAdress;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(dataUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String responseStr = bufferedReader.readLine();
            Log.d("PING Server response", responseStr);
            return responseStr;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = inetAddress.getHostAddress();
                        Log.i("PING", "***** IP="+ ip);
                        // return ip;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("PING", ex.toString());
        }
        return null;
    }

}
