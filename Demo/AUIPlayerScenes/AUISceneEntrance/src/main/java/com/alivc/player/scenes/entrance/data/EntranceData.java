package com.alivc.player.scenes.entrance.data;

/**
 * @author junhuiYe
 * @date 2024/9/25
 * @brief Item的实体类
 */
public class EntranceData {
    public int id;
    public int icon;
    public String title;
    public String desc;

    public EntranceData(int id, int icon, String title, String desc) {
        this.id = id;
        this.icon = icon;
        this.title = title;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "EntranceData{" +
                "id=" + id +
                ", icon=" + icon +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
