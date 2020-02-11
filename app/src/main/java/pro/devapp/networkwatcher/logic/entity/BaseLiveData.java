package pro.devapp.networkwatcher.logic.entity;

import android.os.Looper;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.InvalidationTracker;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pro.devapp.networkwatcher.storage.AppDataBase;

public abstract class BaseLiveData<T> extends LiveData<T> {

    protected final ExecutorService executor;
    protected final AppDataBase appDataBase;

    public BaseLiveData(AppDataBase appDataBase){
        executor = Executors.newSingleThreadExecutor();
        this.appDataBase = appDataBase;
    }

    public abstract String[] observableTables();

    public abstract T getData();

    public void requestData(){
        if(Thread.currentThread() == Looper.getMainLooper().getThread()){
            if(!executor.isShutdown()){
                executor.execute(() -> { postValue(getData()); });
            }
        }
        else {
            postValue(getData());
        }
    }

    @Override
    protected void onActive() {
        super.onActive();
        appDataBase.getInvalidationTracker().addObserver(observer);
        requestData();
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        appDataBase.getInvalidationTracker().removeObserver(observer);
    }

    @CallSuper
    public void shutdown() {
        appDataBase.getInvalidationTracker().removeObserver(observer);
        executor.shutdown();
    }

    final InvalidationTracker.Observer observer = new InvalidationTracker.Observer(observableTables()){
        @Override
        public void onInvalidated(@NonNull Set<String> tables) {
            requestData();
        }
    };
}
