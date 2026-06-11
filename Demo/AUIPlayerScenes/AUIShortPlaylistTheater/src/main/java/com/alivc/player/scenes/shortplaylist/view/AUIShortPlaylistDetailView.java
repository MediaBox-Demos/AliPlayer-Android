package com.alivc.player.scenes.shortplaylist.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alivc.player.scenes.shortplaylist.R;

public class AUIShortPlaylistDetailView extends FrameLayout {

    public AUIShortPlaylistDetailView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AUIShortPlaylistDetailView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AUIShortPlaylistDetailView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.ilr_view_short_playlist_details, null);
        addView(view);
    }
}
