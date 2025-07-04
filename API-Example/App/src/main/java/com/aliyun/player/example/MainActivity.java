package com.aliyun.player.example;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.player.example.adapter.MenuAdapter;
import com.aliyun.player.example.config.MenuConfig;
import com.aliyun.player.example.model.MenuItem;

import java.util.List;

/**
 * @author keria
 * @date 2025/5/30
 * @brief 演示 APP 首页
 */
public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        loadMenuData();
    }

    private void initViews() {
        mRecyclerView = findViewById(R.id.rv_menu);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadMenuData() {
        List<MenuItem> menuItems = MenuConfig.getMenuItems(this);
        MenuAdapter adapter = new MenuAdapter(menuItems);
        mRecyclerView.setAdapter(adapter);
    }
}
