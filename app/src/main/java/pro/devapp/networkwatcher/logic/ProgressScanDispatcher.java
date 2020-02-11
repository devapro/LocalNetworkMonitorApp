package pro.devapp.networkwatcher.logic;

import java.util.LinkedList;

import pro.devapp.networkwatcher.logic.entity.DeviceEntity;

public class ProgressScanDispatcher {
    private final LinkedList<ProgressCallback> listeners =  new LinkedList<>();
    private State state = State.NONE;

    public void addListener(ProgressCallback listener){
        if(!listeners.contains(listener)){
            listeners.add(listener);
            if(state == State.SCAN){
                listener.onStart();
            }
        }
    }

    public void removeListener(ProgressCallback listener){
        listeners.remove(listener);
    }

    public void dispatchStart(){
        state = State.SCAN;
        for (ProgressCallback callback : listeners){
            callback.onStart();
        }
    }

    public void dispatchProgress(int progress){
        for (ProgressCallback callback : listeners){
            callback.onProgress(progress);
        }
    }

    public void dispatchEnd(boolean success){
        state = State.NONE;
        for (ProgressCallback callback : listeners){
            callback.onEnd(success);
        }
    }

    public void dispatchNewDeviceFound(DeviceEntity device){
        for (ProgressCallback callback : listeners){
            callback.onNewDeviceDetected(device);
        }
    }

    public interface ProgressCallback{
        void onStart();
        void onProgress(int progress);
        void onEnd(boolean success);
        void onNewDeviceDetected(DeviceEntity device);
    }

    public enum State{
        NONE,
        SCAN
    }
}
