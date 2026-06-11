package com.alivc.player.scenes.shortplaylist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.player.playerkits.shortvideolist.data.PlaylistInfo;
import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;
import com.alivc.player.playerkits.shortvideolist.data.VideoInfoExtension;
import com.alivc.player.playerkits.shortvideolist.utils.AUIShortVideoListUtil;
import com.alivc.player.scenes.shortplaylist.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class AUIShortPlaylistDetailAdapter extends RecyclerView.Adapter<AUIShortPlaylistDetailAdapter.ViewHolder> {
    private List<PlaylistInfo> mPlaylist = new ArrayList<>();
    private OnShortPlaylistEventListener mOnShortPlaylistEventListener;
    private Context mContext;
    private static final Gson mGSON = new Gson();

    public interface OnShortPlaylistEventListener {
        void onClickPosition(String playlistId);

        void onClickJson(String json);
    }

    public void setData(List<PlaylistInfo> playlistInfos) {
        mPlaylist.clear();
        if (playlistInfos != null) {
            mPlaylist.addAll(playlistInfos);
        }
        notifyDataSetChanged();
    }

    public AUIShortPlaylistDetailAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ilr_view_short_playlist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlaylistInfo playlistInfo = mPlaylist.get(position);
        holder.bind(playlistInfo);
    }

    @Override
    public int getItemCount() {
        return mPlaylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mCoverIv;
        private TextView mTitleTv;
        private TextView mTotalTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCoverIv = itemView.findViewById(R.id.v_item_cover);
            mTitleTv = itemView.findViewById(R.id.v_item_title);
            mTotalTv = itemView.findViewById(R.id.v_item_total);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnShortPlaylistEventListener != null) {
                        int position = getAdapterPosition();
                        if (position >= 0 && position < mPlaylist.size()) {
                            List<VideoInfo> videoInfoList = mPlaylist.get(getAdapterPosition()).playlistVideos;
                            String json = AUIShortVideoListUtil.serializeVideoInfoListToJson(videoInfoList);
                            mOnShortPlaylistEventListener.onClickPosition(mPlaylist.get(getAdapterPosition()).playlistId);
                            mOnShortPlaylistEventListener.onClickJson(json);
                        }
                    }
                }
            });
        }

        public void bind(PlaylistInfo mData) {
            if (null == mData) return;

            if (null != mData.playlistCoverUrl && !mData.playlistCoverUrl.isEmpty()) {
                loadCover(mData.playlistCoverUrl);
            }

            mTitleTv.setText(mData.playlistName);
            if (mData.playlistExtension == null || mData.playlistExtension.isEmpty()) {
                return;
            }
            VideoInfoExtension videoInfoExtension = mGSON.fromJson(mData.playlistExtension, VideoInfoExtension.class);
            String videoCount = "全" + videoInfoExtension.count + "集";
            mTotalTv.setText(videoCount);
        }

        private void loadCover(String cover) {
            if (null != cover && !cover.isEmpty()) {
                Glide.with(mContext).load(cover)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(15)))
                        .into(mCoverIv);
            }
        }
    }

    public void setShortPlaylistEventListener(OnShortPlaylistEventListener listener) {
        mOnShortPlaylistEventListener = listener;
    }
}
