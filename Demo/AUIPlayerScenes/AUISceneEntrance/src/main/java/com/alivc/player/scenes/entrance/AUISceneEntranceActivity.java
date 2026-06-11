package com.alivc.player.scenes.entrance;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.player.scenes.entrance.adapter.EntranceAdapter;
import com.alivc.player.scenes.entrance.data.EntranceData;

import java.util.ArrayList;
import java.util.List;

public class AUISceneEntranceActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private EntranceAdapter mAdapter;
    private final static int AUI_SCENE_SHORT_VIDEO_LIST = 1;
    private final static int AUI_INDEX_TAB_LAYOUT_FEED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aui_scene_entrance);
        initView();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.aui_scene_entrance_recycler);
        mAdapter = new EntranceAdapter();
        mAdapter.setData(insertSceneEntrance());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private List<EntranceData> insertSceneEntrance() {
        List<EntranceData> entranceData = new ArrayList<>();
        entranceData.add(new EntranceData(AUI_SCENE_SHORT_VIDEO_LIST, R.drawable.aui_scene_entrance_icon, getResources().getString(R.string.player_episode), getResources().getString(R.string.player_short_playlist_theater_msg)));
        entranceData.add(new EntranceData(AUI_INDEX_TAB_LAYOUT_FEED, R.drawable.aui_scene_entrance_icon, getResources().getString(R.string.player_tab_layout), getResources().getString(R.string.player_tablayout_feed_msg)));
        return entranceData;
    }
}