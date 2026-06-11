package com.alivc.player.playerkits.shortvideolist.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alivc.player.playerkits.shortvideolist.R;

/**
 * @author keria
 * @date 2023/8/29
 * @brief 短视频列表播放-交互组件（点赞、评论、分享）
 */

/****
 * @author keria
 * @date 2023/8/29
 * @brief short video list page interactive components (likes, comments, shares)
 */
public class AUIShortVideoInteractiveComponent extends FrameLayout {
    private LinearLayout mLikeLayout;
    private LinearLayout mCommentLayout;
    private LinearLayout mShareLayout;

    private ImageView mLikeImageView;

    private TextView mLikeTextview;
    private TextView mCommentTextview;
    private TextView mShareTextview;

    public AUIShortVideoInteractiveComponent(@NonNull Context context) {
        super(context);
        initViews(context);
    }

    public AUIShortVideoInteractiveComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public AUIShortVideoInteractiveComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.ilr_view_short_video_interactive_panel, null);

        mLikeLayout = view.findViewById(R.id.layout_like);
        mCommentLayout = view.findViewById(R.id.layout_comment);
        mShareLayout = view.findViewById(R.id.layout_share);

        mLikeImageView = view.findViewById(R.id.iv_like);

        mLikeTextview = view.findViewById(R.id.tv_like);
        mCommentTextview = view.findViewById(R.id.tv_comment);
        mShareTextview = view.findViewById(R.id.tv_share);
        addView(view);
    }
}
