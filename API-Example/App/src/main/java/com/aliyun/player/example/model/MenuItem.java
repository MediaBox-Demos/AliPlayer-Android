package com.aliyun.player.example.model;

import java.util.List;

/**
 * Data model for menu items
 * 菜单项的数据模型
 *
 * @author keria
 * @date 2025/5/31
 * @brief Menu item data model
 */
public class MenuItem {
    private final MenuType type;
    private final String title;
    private final String schema;
    private final String description;

    private final List<MenuItem> subItems;
    private boolean isExpanded;

    public MenuItem(MenuType type, String title, String schema, String description, List<MenuItem> subItems) {
        this.type = type;
        this.title = title;
        this.schema = schema;
        this.description = description;
        this.subItems = subItems;
        this.isExpanded = false;
    }

    public static MenuItem createHeader(String title) {
        return new MenuItem(MenuType.HEADER, title, null, null, null);
    }

    public static MenuItem createItem(String title, String schema, String description) {
        return new MenuItem(MenuType.ITEM, title, schema, description, null);
    }

    public static MenuItem createExpandableItem(String title, String description, List<MenuItem> subItems) {
        return new MenuItem(MenuType.EXPANDABLE_ITEM, title, null, description, subItems);
    }

    public MenuType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getSchema() {
        return schema;
    }

    public String getDescription() {
        return description;
    }

    public List<MenuItem> getSubItems() {
        return subItems;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public boolean hasSubItems() {
        return subItems != null && !subItems.isEmpty();
    }

    public enum MenuType {
        HEADER(0),

        ITEM(1),

        EXPANDABLE_ITEM(2),

        ;

        private final int value;

        MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}