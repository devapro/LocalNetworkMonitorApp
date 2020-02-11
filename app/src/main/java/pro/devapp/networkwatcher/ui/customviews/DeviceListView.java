package pro.devapp.networkwatcher.ui.customviews;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DimenRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pro.devapp.networkwatcher.R;
import pro.devapp.networkwatcher.databinding.ViewDeviceItemBinding;
import pro.devapp.networkwatcher.logic.entity.DeviceEntity;
import pro.devapp.networkwatcher.logic.viewmodel.DeviceItemViewModel;

public class DeviceListView extends RecyclerView {

    public DeviceListView(@NonNull Context context) {
        this(context, null, 0);
    }

    public DeviceListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeviceListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutManager(new LinearLayoutManager(context));
        addItemDecoration(new SpaceItemDecoration(getContext(), R.dimen.d_common_2dp));
        setAdapter(new CustomAdapter());
    }

    public void submitList(@NonNull List<DeviceEntity> items) {
        ((CustomAdapter)getAdapter()).submitList(items);
    }

    private static class CustomAdapter extends ListAdapter<DeviceEntity, CustomViewHolder>{

        CustomAdapter() {
            super(new Diff());
            setHasStableIds(true);
        }

        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
            ViewDeviceItemBinding itemBinding =
                ViewDeviceItemBinding.inflate(layoutInflater, parent, false);
            return new CustomViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
            DeviceEntity item = getItem(position);
            holder.bind(item);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        @Override
        public void onViewRecycled(@NonNull CustomViewHolder holder) {
            holder.unind();
        }
    }

    private static class Diff extends DiffUtil.ItemCallback<DeviceEntity> {

        @Override
        public boolean areItemsTheSame(@NonNull DeviceEntity oldItem, @NonNull DeviceEntity newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull DeviceEntity oldItem, @NonNull DeviceEntity newItem) {
            return oldItem.equals(newItem);
        }
    }

    private static class CustomViewHolder extends RecyclerView.ViewHolder{
        private final ViewDeviceItemBinding binding;

        @LayoutRes
        private static final int LAYOUT = R.layout.view_device_item;

        public CustomViewHolder(@NonNull ViewDeviceItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DeviceEntity item){
            binding.setModel(new DeviceItemViewModel(item.getIp(), item.getMac(), item.getName(), item.isConnected()));
            binding.executePendingBindings();
        }

        public void unind(){

        }
    }

    private final static class SpaceItemDecoration extends ItemDecoration{
        private final int offset;

        SpaceItemDecoration(@NonNull Context context, @DimenRes int offsetId) {
            offset = context.getResources().getDimensionPixelSize(offsetId);
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull State state) {
            RecyclerView.Adapter adapter = parent.getAdapter();
            if (adapter == null) {
                outRect.setEmpty();
                return;
            }

            int itemsCount = adapter.getItemCount();
            if (itemsCount == 0 || itemsCount == 1) {
                outRect.setEmpty();
                return;
            }

            int position = parent.getChildAdapterPosition(view);
            if (position == RecyclerView.NO_POSITION) {
                outRect.setEmpty();
                return;
            }

            if (position % 2 == 0) {
                outRect.right = offset;

                if (hasBeforeRow(position)) {
                    outRect.top = offset;
                }

                if (hasAfterRow(position, itemsCount)) {
                    outRect.bottom = offset;
                }
            } else if (position % 2 == 1) {
                outRect.left = offset;

                if (hasBeforeRow(position)) {
                    outRect.top = offset;
                }

                if (hasAfterRow(position, itemsCount)) {
                    outRect.bottom = offset;
                }
            } else {
                outRect.setEmpty();
            }
        }

        private boolean hasAfterRow(int position, int itemsCount) {
            int lastPosition = itemsCount - 1;
            if (position % 2 == 0) {
                int afterRowPosition = Math.min(lastPosition, position + 2);
                return afterRowPosition <= lastPosition;
            } else if (position % 2 == 1) {
                int afterRowPosition = Math.min(lastPosition, position + 1);
                return afterRowPosition <= lastPosition;
            }
            return false;
        }

        private boolean hasBeforeRow(int position) {
            if (position % 2 == 0) {
                int beforeRowPosition = Math.max(0, position - 1);
                return beforeRowPosition != 0;
            } else if (position % 2 == 1) {
                int beforeRowPosition = Math.max(0, position - 2);
                return beforeRowPosition != 0;
            }
            return false;
        }
    }
}
