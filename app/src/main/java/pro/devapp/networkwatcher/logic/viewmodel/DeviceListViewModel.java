package pro.devapp.networkwatcher.logic.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import pro.devapp.networkwatcher.App;
import pro.devapp.networkwatcher.logic.NetworkController;
import pro.devapp.networkwatcher.logic.ProgressScanDispatcher;
import pro.devapp.networkwatcher.logic.viewmodel.livedata.DevicesLiveData;
import pro.devapp.networkwatcher.storage.AppDataBase;

public class DeviceListViewModel extends BaseViewModel {

    private final AppDataBase dataBase;
    private final NetworkController networkController;
    private final DevicesLiveData devicesLiveData;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isRefreshing = new MutableLiveData<>(false);
    private final ActionListener listener;

    public DeviceListViewModel(@NonNull Application application, @NonNull ActionListener listener) {
        super(application);
        this.listener = listener;
        dataBase = ((App)application).getAppDataBase();
        networkController = ((App)application).getNetworkController();
        devicesLiveData = new DevicesLiveData(dataBase);
        networkController.getProgressScanDispatcher().addListener(callback);
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

    @Override
    protected void onCleared() {
        super.onCleared();
        devicesLiveData.shutdown();
        networkController.getProgressScanDispatcher().removeListener(callback);
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
    }
}
