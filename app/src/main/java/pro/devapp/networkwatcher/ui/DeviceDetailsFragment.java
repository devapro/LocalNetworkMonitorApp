package pro.devapp.networkwatcher.ui;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pro.devapp.networkwatcher.R;
import pro.devapp.networkwatcher.logic.viewmodel.DeviceDetailsViewModel;

public class DeviceDetailsFragment extends Fragment {

    private DeviceDetailsViewModel mViewModel;

    public static DeviceDetailsFragment newInstance() {
        return new DeviceDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.device_details_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(DeviceDetailsViewModel.class);
        // TODO: Use the ViewModel
    }

}
