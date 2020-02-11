package pro.devapp.networkwatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pro.devapp.networkwatcher.databinding.ActivityMainBinding;
import pro.devapp.networkwatcher.logic.MainNavigationController;
import pro.devapp.networkwatcher.logic.NetworkController;
import pro.devapp.networkwatcher.logic.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;
    private NavController controller;
    private MainNavigationController mainNavigationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.setLifecycleOwner(this);
        mainBinding.setModel(ViewModelProviders.of(this).get(MainViewModel.class));

        controller = Navigation.findNavController(this, R.id.nav_host_fragment);

        mainNavigationController = new MainNavigationController(this, controller);

        setSupportActionBar(mainBinding.toolbar);


        //TODO test
//        Executors.newCachedThreadPool().execute(() -> {
//            NetworkController.getLocalIpAddress();
//            String ip = NetworkController.getMyIp(this);
//            NetworkController.scan(ip);
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        controller.addOnDestinationChangedListener(mainNavigationController.getNavigationListener());

        new Handler().postDelayed(() -> { mainBinding.splash.setVisibility(View.GONE); }, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        controller.removeOnDestinationChangedListener(mainNavigationController.getNavigationListener());
    }
}
