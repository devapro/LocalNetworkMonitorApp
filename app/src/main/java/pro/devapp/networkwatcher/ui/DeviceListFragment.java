package pro.devapp.networkwatcher.ui;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import pro.devapp.networkwatcher.App;
import pro.devapp.networkwatcher.R;
import pro.devapp.networkwatcher.databinding.FragmentDeviceListBinding;
import pro.devapp.networkwatcher.logic.ProgressScanDispatcher;
import pro.devapp.networkwatcher.logic.entity.DeviceEntity;
import pro.devapp.networkwatcher.logic.viewmodel.DeviceListViewModel;
import pro.devapp.networkwatcher.services.ScanForegroundService;

public class DeviceListFragment extends Fragment {

    private DeviceListViewModel mViewModel;
    private FragmentDeviceListBinding mBinding;
    private Snackbar snackbar;

    public static DeviceListFragment newInstance() {
        return new DeviceListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_device_list, container, false);
        mBinding.setLifecycleOwner(this);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, DeviceListViewModel.createFactory(getActivity().getApplication(), new DeviceListViewModel.ActionListener() {
            @Override
            public void startScan() {
                ContextCompat.startForegroundService(getContext(), new Intent(getContext(), ScanForegroundService.class));
            }
        })).get(DeviceListViewModel.class);
        mBinding.setModel(mViewModel);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((App)getActivity().getApplication()).getNetworkScanController().getProgressScanDispatcher().addListener(callback);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((App)getActivity().getApplication()).getNetworkScanController().getProgressScanDispatcher().removeListener(callback);
    }

    private final ProgressScanDispatcher.ProgressCallback callback = new ProgressScanDispatcher.ProgressCallback() {
        @Override
        public void onStart() {
            snackbar = Snackbar.make(mBinding.container, "", BaseTransientBottomBar.LENGTH_INDEFINITE)
                .setAction(R.string.stop_scan, v -> ((App)getActivity().getApplication()).getNetworkScanController().stopScan());
            snackbar.show();
        }

        @Override
        public void onProgress(int progress) {
            snackbar.setText(progress + " %");
        }

        @Override
        public void onEnd(boolean success) {
            snackbar.dismiss();
            snackbar = null;
        }

        @Override
        public void onNewDeviceDetected(DeviceEntity device) {

        }
    };
}
