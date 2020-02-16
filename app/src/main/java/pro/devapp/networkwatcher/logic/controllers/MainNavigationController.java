package pro.devapp.networkwatcher.logic.controllers;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;

public class MainNavigationController {

    private final Activity activity;
    private final NavController controller;

    private final NavController.OnDestinationChangedListener navigationListener = new NavController.OnDestinationChangedListener(){
        @Override
        public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {

        }
    };

    public MainNavigationController(Activity activity, NavController controller){
        this.activity = activity;
        this.controller = controller;
    }

    public NavController.OnDestinationChangedListener getNavigationListener() {
        return navigationListener;
    }
}
