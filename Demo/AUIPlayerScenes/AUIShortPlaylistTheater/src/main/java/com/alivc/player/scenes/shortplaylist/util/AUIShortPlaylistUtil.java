package com.alivc.player.scenes.shortplaylist.util;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.alivc.player.playerkits.shortvideolist.data.BaseResponse;
import com.alivc.player.playerkits.shortvideolist.data.PlaylistInfo;
import com.alivc.player.playerkits.shortvideolist.data.PlayInfoListRequestParams;
import com.alivc.player.playerkits.shortvideolist.utils.AUIShortVideoListUtil;
import com.alivc.player.playerkits.shortvideolist.utils.NetWorkRequestUtils;
import com.alivc.player.playerkits.shortvideolist.utils.SLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author keria
 * @date 2024/9/24
 * @brief 短剧剧场场景-工具类
 */
public class AUIShortPlaylistUtil {
    private AUIShortPlaylistUtil() {
    }

    /**
     * Sample code for requesting playlist info list
     *
     * @param callBack network response callback
     */
    public static void requestPlaylistInfoList(PlayInfoListRequestParams requestParams, final AUIShortVideoListUtil.OnNetworkCallBack<List<PlaylistInfo>> callBack) {
        OkHttpClient okHttpClient = NetWorkRequestUtils.getOkHttpClient();
        Request request = NetWorkRequestUtils.postContentFromURI(requestParams);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                SLog.e("[AUIShortPlayListUtil]: ", e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    Gson gson = new Gson();
                    Type type = new TypeToken<BaseResponse<List<PlaylistInfo>>>() {
                    }.getType();
                    BaseResponse<List<PlaylistInfo>> baseResponse = gson.fromJson(json, type);
                    if (baseResponse.success && baseResponse.data != null && !baseResponse.data.isEmpty()) {
                        callBack.onResponse(baseResponse.data);
                    }
                }
            }
        });
    }

    /**
     * Sample code for assembling playlist info list
     *
     * @return playlist info list
     */
    public static ArrayList<PlaylistInfo> assemblePlaylistInfoList() {
        ArrayList<PlaylistInfo> playlist = new ArrayList<>();
        // Add playlist info into playListInfoList
        PlaylistInfo playlistInfo = new PlaylistInfo();
        playlistInfo.playlistId = "1"; // TODO 剧集id
        playlistInfo.playlistName = ""; // TODO 剧集名称
        playlistInfo.playlistDescription = ""; // TODO 剧集描述
        playlistInfo.playlistStatus = ""; // TODO 列表状态
        playlistInfo.playlistTags = ""; // TODO 列表表现
        playlistInfo.playlistCoverUrl = ""; // TODO 剧集封面图
        playlistInfo.playlistOrderBy = ""; // TODO 排序规则
        playlistInfo.playlistExtension = ""; // TODO 列表拓展项
        playlistInfo.createTime = ""; // TODO 创建时间
        playlistInfo.modifyTime = ""; // TODO 最后修改时间
        playlistInfo.requestId = ""; // TODO 本次请求id
        // Add others playlist info into playListInfoList...

        playlist.add(playlistInfo);
        return playlist;
    }

    /**
     * Sample code for convert video info list to JSON
     *
     * @return video info list serializable JSON String
     */
    public static String serializePlaylistInfoListToJson(List<PlaylistInfo> playlist) {
        Gson gson = new Gson();
        return gson.toJson(playlist);
    }

    /**
     * Sample code for deserialize playlist info list from JSON
     *
     * @param json playlist info list serializable JSON String
     * @return playlist info list
     */
    public static List<PlaylistInfo> deserializePlaylistInfoListFromJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        List<PlaylistInfo> playlist = null;
        try {
            Gson gson = new Gson();
            playlist = gson.fromJson(json, new TypeToken<List<PlaylistInfo>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playlist != null && playlist.size() > 0 ? playlist : null;
    }
}
