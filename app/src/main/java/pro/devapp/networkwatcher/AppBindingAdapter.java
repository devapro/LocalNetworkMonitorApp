package pro.devapp.networkwatcher;

import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import pro.devapp.networkwatcher.logic.entity.DeviceEntity;
import pro.devapp.networkwatcher.logic.viewmodel.livedata.DevicesLiveData;
import pro.devapp.networkwatcher.ui.customviews.DeviceListView;

public class AppBindingAdapter {
    @BindingAdapter("items")
    public static void setItems(DeviceListView view, DevicesLiveData data){
        List<DeviceEntity> items = data.getValue();
        if(items!= null){
            view.submitList(items);
        }
    }

    @BindingAdapter("isLoading")
    public static void setLoading(ContentLoadingProgressBar view, LiveData<Boolean> isLoading){
        if(isLoading.getValue() != null && isLoading.getValue()){
            view.show();
        }
        else {
            view.hide();
        }
    }

    @BindingAdapter("listener")
    public static void setSwipeRefreshLayoutListener(SwipeRefreshLayout view, SwipeRefreshLayout.OnRefreshListener listener){
        view.setOnRefreshListener(listener);
    }

    @BindingAdapter("refreshing")
    public static void setSwipeRefreshLayoutRefreshing(SwipeRefreshLayout view, LiveData<Boolean> isLoading){
        if(isLoading.getValue() != null && isLoading.getValue()){
            view.setRefreshing(true);
        }
        else {
            view.setRefreshing(false);
        }
    }


    @BindingAdapter("imageResource")
    public static void setImageViewDrawableResourse(ImageView view, @DrawableRes int idRes){
        view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), idRes));
    }
}
