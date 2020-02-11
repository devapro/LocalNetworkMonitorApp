package pro.devapp.networkwatcher.logic;

import java.util.LinkedList;

public class ProgressScanDispatcher {
    private final LinkedList<ProgressCallback> listeners =  new LinkedList<>();

    public void addListener(ProgressCallback listener){
        if(!listeners.contains(listener)){
            listeners.add(listener);
        }
    }

    public void removeListener(ProgressCallback listener){
        listeners.remove(listener);
    }

    public void dispatchStart(){
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
        for (ProgressCallback callback : listeners){
            callback.onEnd(success);
        }
    }

    public interface ProgressCallback{
        public void onStart();
        public void onProgress(int progress);
        public void onEnd(boolean success);
    }
}
