package pro.devapp.networkwatcher.logic.viewmodel.livedata;


import java.util.ArrayList;
import java.util.List;

import pro.devapp.networkwatcher.logic.entity.BaseLiveData;
import pro.devapp.networkwatcher.logic.entity.DeviceEntity;
import pro.devapp.networkwatcher.storage.AppDataBase;
import pro.devapp.networkwatcher.storage.entity.NetworkDeviceEntity;
import pro.devapp.networkwatcher.storage.entity.NetworkDeviceHistoryEntity;

public class DevicesLiveData extends BaseLiveData<List<DeviceEntity>> {

    private static final String[] TABLES = {
        "NetworkDevice",
        "NetworkDeviceHistory"
    };

    public DevicesLiveData(AppDataBase appDataBase) {
        super(appDataBase);
    }

    @Override
    public String[] observableTables() {
        return TABLES;
    }

    @Override
    public List<DeviceEntity> getData() {
        List<DeviceEntity> result = new ArrayList<>();
        List<NetworkDeviceEntity> items = appDataBase.networkDeviceDao().devices();
        for (NetworkDeviceEntity item : items){
            NetworkDeviceHistoryEntity lastState = appDataBase.networkDeviceHistoryDao().getLastState(item.getId());
            result.add(new DeviceEntity(
                item.getId(),
                item.getName(),
                item.getIp(),
                item.getMac(),
                item.getCreatedAt(),
                item.getUpdatedAt(),
                lastState != null ? lastState.getStateType() : NetworkDeviceHistoryEntity.State.OFFLINE
            ));
        }
        return result;
    }
}
