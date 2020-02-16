package pro.devapp.networkwatcher.logic.viewmodel;

import android.app.Application;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import pro.devapp.networkwatcher.App;
import pro.devapp.networkwatcher.logic.controllers.NetworkScanController;
import pro.devapp.networkwatcher.logic.ProgressScanDispatcher;
import pro.devapp.networkwatcher.logic.entity.DeviceEntity;
import pro.devapp.networkwatcher.logic.viewmodel.livedata.DevicesLiveData;
import pro.devapp.networkwatcher.storage.AppDataBase;
import pro.devapp.networkwatcher.ui.customviews.DeviceListView;

public class DeviceListViewModel extends BaseViewModel {

    private final AppDataBase dataBase;
    private final NetworkScanController networkScanController;
    private final DevicesLiveData devicesLiveData;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isRefreshing = new MutableLiveData<>(false);
    private final ActionListener listener;
    private final GestureDetector mGestureDetector;

    public DeviceListViewModel(@NonNull Application application, @NonNull ActionListener listener) {
        super(application);
        this.listener = listener;
        dataBase = ((App)application).getAppDataBase();
        networkScanController = ((App)application).getNetworkScanController();
        devicesLiveData = new DevicesLiveData(dataBase);
        networkScanController.getProgressScanDispatcher().addListener(callback);
        mGestureDetector = new GestureDetector(application.getApplicationContext(), gestureListener);
    }

    public static ViewModelProvider.Factory  createFactory(@NonNull Application application, @NonNull ActionListener listener) {
        return new ViewModelFactory(application, listener);
    }

    public DevicesLiveData getDevicesLiveData() {
        return devicesLiveData;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<Boolean> getIsRefreshing() {
        return isRefreshing;
    }

    public SwipeRefreshLayout.OnRefreshListener getRefreshListener() {
        return refreshListener;
    }

    public RecyclerView.SimpleOnItemTouchListener getTouchListener() {
        return touchListener;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        devicesLiveData.shutdown();
        networkScanController.getProgressScanDispatcher().removeListener(callback);
    }

    private final SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener(){
        @Override
        public void onRefresh() {
            listener.startScan();
        }
    };

    private final ProgressScanDispatcher.ProgressCallback callback = new ProgressScanDispatcher.ProgressCallback() {
        @Override
        public void onStart() {
            workThreadExecutor.execute(() -> {
                int devicesCount = dataBase.networkDeviceDao().devicesCount();
                if(devicesCount == 0){
                    isLoading.postValue(true);
                }
                else {
                    isRefreshing.postValue(true);
                }
            });
        }

        @Override
        public void onProgress(int progress) {

        }

        @Override
        public void onEnd(boolean success) {
            isLoading.setValue(false);
            isRefreshing.setValue(false);
        }

        @Override
        public void onNewDeviceDetected(DeviceEntity device) {

        }
    };

    private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }
    };

    private final RecyclerView.SimpleOnItemTouchListener touchListener = new RecyclerView.SimpleOnItemTouchListener(){
        @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mGestureDetector.onTouchEvent(e)) {
                DeviceEntity entity = ((DeviceListView)view).getItem(view.getChildAdapterPosition(childView));
                if(entity != null){
                    listener.openDeviceDetails(entity);
                }
                return true;
            }
            return false;
        }
    };

    static class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

        private final Application application;
        private final ActionListener listener;

        public ViewModelFactory(@NonNull Application application, @NonNull ActionListener listener){
           this.application = application;
           this.listener = listener;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            ViewModel viewModel = new DeviceListViewModel(application, listener);
            return (T)viewModel;
        }
    }

    public interface ActionListener{
        void startScan();
        void openDeviceDetails(DeviceEntity device);
    }
}
