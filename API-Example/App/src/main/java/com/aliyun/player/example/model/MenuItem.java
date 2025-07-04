package com.aliyun.player.example.model;

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

    public MenuItem(MenuType type, String title, String schema, String description) {
        this.type = type;
        this.title = title;
        this.schema = schema;
        this.description = description;
    }

    public static MenuItem createHeader(String title) {
        return new MenuItem(MenuType.HEADER, title, null, null);
    }

    public static MenuItem createItem(String title, String schema, String description) {
        return new MenuItem(MenuType.ITEM, title, schema, description);
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

    public enum MenuType {
        HEADER(0),

        ITEM(1);

        private final int value;

        MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}