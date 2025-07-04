package com.aliyun.player.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        } else if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).bind(item);
        }
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
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
}
