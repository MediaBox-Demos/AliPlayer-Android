package com.aliyun.player.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.player.example.R;
import com.aliyun.player.example.model.MenuItem;
import com.aliyun.player.example.router.SchemaRouter;

import java.util.List;

/**
 * Menu adapter for RecyclerView to display menu items with headers and clickable items
 * 菜单适配器，用于在RecyclerView中显示包含页眉和可点击项的菜单
 *
 * @author keria
 * @date 2025/5/31
 * @brief RecyclerView adapter for menu display with header and item support
 */
public class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<MenuItem> menuItems;

    public MenuAdapter(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @Override
    public int getItemViewType(int position) {
        return menuItems.get(position).getType().getValue();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == MenuItem.MenuType.HEADER.getValue()) {
            View view = inflater.inflate(R.layout.item_menu_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == MenuItem.MenuType.EXPANDABLE_ITEM.getValue()) {
            View view = inflater.inflate(R.layout.item_menu_expandable, parent, false);
            return new ExpandableItemViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_menu_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MenuItem item = menuItems.get(position);

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(item);
        } else if (holder instanceof ExpandableItemViewHolder) {
            ((ExpandableItemViewHolder) holder).bind(item, this::toggleExpansion);
        } else if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).bind(item);
        }
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    private void toggleExpansion(MenuItem item) {
        int position = menuItems.indexOf(item);
        if (position == -1) return;

        item.setExpanded(!item.isExpanded());

        if (item.isExpanded()) {
            // 展开：在当前位置后插入子项
            menuItems.addAll(position + 1, item.getSubItems());
            notifyItemRangeInserted(position + 1, item.getSubItems().size());
        } else {
            // 收起：移除子项
            int subItemCount = item.getSubItems().size();
            for (int i = 0; i < subItemCount; i++) {
                menuItems.remove(position + 1);
            }
            notifyItemRangeRemoved(position + 1, subItemCount);
        }

        notifyItemChanged(position); // 更新箭头状态
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTv;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.tv_header_title);
        }

        public void bind(MenuItem item) {
            titleTv.setText(item.getTitle());
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTv;
        private final TextView descriptionTv;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.tv_item_title);
            descriptionTv = itemView.findViewById(R.id.tv_item_description);
        }

        public void bind(MenuItem item) {
            titleTv.setText(item.getTitle());
            descriptionTv.setText(item.getDescription());

            itemView.setOnClickListener(v -> {
                SchemaRouter.navigate(v.getContext(), item.getSchema());
            });
        }
    }

    static class ExpandableItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTv;
        private final TextView descriptionTv;
        private final ImageView arrowIv;

        public ExpandableItemViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.tv_item_title);
            descriptionTv = itemView.findViewById(R.id.tv_item_description);
            arrowIv = itemView.findViewById(R.id.iv_arrow);
        }

        public void bind(MenuItem item, OnExpandToggleListener listener) {
            titleTv.setText(item.getTitle());
            descriptionTv.setText(item.getDescription());

            // 设置箭头状态
            arrowIv.setRotation(item.isExpanded() ? 90f : 0f);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onToggle(item);
                }
            });
        }
    }

    interface OnExpandToggleListener {
        void onToggle(MenuItem item);
    }
}
