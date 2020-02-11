package pro.devapp.networkwatcher.logic.viewmodel;

import pro.devapp.networkwatcher.R;

public class DeviceItemViewModel {
    private final String ip;
    private final String mac;
    private final String name;
    private final Boolean isConnected;

    public DeviceItemViewModel(String ip, String mac, String name, Boolean isConnected) {
        this.ip = ip;
        this.mac = mac;
        this.name = name;
        this.isConnected = isConnected;
    }

    public String getIp() {
        return ip;
    }

    public String getMac() {
        return mac;
    }

    public String getName() {
        return name;
    }

    public Boolean getConnected() {
        return isConnected;
    }

    public int getConnectedImg() {
        return isConnected ? R.drawable.ic_online : R.drawable.ic_offline;
    }

    public String getInfo() {
        return name + " (" + mac + ")";
    }
}
