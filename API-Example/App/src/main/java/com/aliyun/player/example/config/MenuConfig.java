package com.aliyun.player.example.config;

import android.content.Context;

import com.aliyun.player.common.Constants;
import com.aliyun.player.common.R;
import com.aliyun.player.example.model.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Menu configuration manager for loading and managing menu items
 * 菜单配置管理，用于加载和管理菜单项
 *
 * @author keria
 * @date 2025/5/31
 * @brief Menu configuration manager
 */
public class MenuConfig {
    public static List<MenuItem> getMenuItems(Context context) {
        List<MenuItem> items = new ArrayList<>();

        // 基础功能区
        items.add(MenuItem.createHeader(context.getString(R.string.menu_header_basic)));
        items.add(MenuItem.createItem(context.getString(R.string.menu_basic_demo_title), Constants.Schema.BASIC_PLAYBACK, context.getString(R.string.menu_basic_demo_desc)));
        items.add(MenuItem.createItem(context.getString(R.string.menu_rts_demo_title), Constants.Schema.RTS_LIVE_STREAM, context.getString(R.string.menu_rts_demo_desc)));

        // 进阶功能区
        items.add(MenuItem.createHeader(context.getString(R.string.menu_header_advanced)));
        items.add(MenuItem.createItem(context.getString(R.string.menu_pip_demo_title), Constants.Schema.PICTURE_IN_PICTURE, context.getString(R.string.menu_pip_demo_desc)));
        items.add(MenuItem.createItem(context.getString(R.string.menu_thumbnail_title), Constants.Schema.THUMBNAIL, context.getString(R.string.menu_basic_demo_desc)));


        return items;
    }
}