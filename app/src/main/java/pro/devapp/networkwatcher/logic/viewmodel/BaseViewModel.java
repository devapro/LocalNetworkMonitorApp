package pro.devapp.networkwatcher.logic.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BaseViewModel extends AndroidViewModel {

    protected final Executor uiThreadExecutor;
    protected final ExecutorService workThreadExecutor;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        uiThreadExecutor = ContextCompat.getMainExecutor(application);
        workThreadExecutor = Executors.newFixedThreadPool(2);
    }


}
