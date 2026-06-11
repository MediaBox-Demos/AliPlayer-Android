package com.alivc.player.playerkits.shortvideolist.utils;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.alivc.player.playerkits.shortvideolist.AUIShortVideoListConstants;
import com.alivc.player.playerkits.shortvideolist.data.BaseResponse;
import com.alivc.player.playerkits.shortvideolist.data.PlaylistInfo;
import com.alivc.player.playerkits.shortvideolist.data.PlayInfoListRequestParams;
import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author keria
 * @date 2024/8/7
 * @brief 短视频列表播放-工具类
 */
public class AUIShortVideoListUtil {
    private AUIShortVideoListUtil() {
    }

    /**
     * 从Assets获取内容
     *
     * @param context android context
     * @param path    Assets路径
     * @return Assets内容
     */
    public static String getContentFromAssets(Context context, String path) {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream assetsInputStream = context.getAssets().open(path); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetsInputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 从URI获取内容 (must be called in sub thread)
     *
     * @param uri URI
     * @return 页面内容
     */
    public static String getContentFromURI(String uri) {
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
                return convertStreamToString(in);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将输入流转换为字符串
     *
     * @param inputStream 输入流
     * @return 字符串
     */
    public static String convertStreamToString(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    /**
     * convert url from HTTPS to HTTP
     *
     * @param url HTTPS URL
     * @return HTTP URL
     * Currently, https URL can't be accessed with Glide, and a certificate for HTTPS needs to be configured;
     * Therefore, a temporary plan was adopted to avoid it
     */
    public static String convertURLFromHTTPS2HTTP(String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        if (!url.startsWith("https://")) {
            return url;
        }
        return url.replace("https://", "http://");
    }

    public interface OnNetworkCallBack<T> {
        void onResponse(T data);
    }

    /**
     * Sample code for requesting video info list
     *
     * @return video info list
     */
    public static void requestVideoInfoList(final PlayInfoListRequestParams requestParams, final OnNetworkCallBack<List<VideoInfo>> callBack) {
        Request request = NetWorkRequestUtils.postContentFromURI(requestParams);

        NetWorkRequestUtils.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                SLog.e(this, "请求失败: ", e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    Gson gson = new Gson();
                    Type type = new TypeToken<BaseResponse<List<PlaylistInfo>>>() {
                    }.getType();

                    BaseResponse<List<PlaylistInfo>> baseResponse = gson.fromJson(json, type);

                    if (baseResponse == null || baseResponse.data == null || baseResponse.data.isEmpty()) {
                        SLog.e(this, "返回数据为空");
                        return;
                    }

                    PlaylistInfo firstItem = baseResponse.data.get(0);

                    if (firstItem.playlistVideos == null) {
                        SLog.e(this, "playlistVideos数据为空");
                        return;
                    }

                    List<VideoInfo> playlistVideos = firstItem.playlistVideos;
                    if (callBack != null) {
                        callBack.onResponse(playlistVideos);
                    }
                }
            }
        });
    }

    public static void requestVideoInfoList(String playlist, final OnNetworkCallBack<List<VideoInfo>> callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(playlist)) {
                    return;
                }

                String content = AUIShortVideoListUtil.getContentFromURI(AUIShortVideoListConstants.DEFAULT_VIDEO_INFO_LIST_URL + playlist);

                SLog.d(this, "[VIDEO-ID]: ", content);
                Gson gson = new Gson();
                Type type = new TypeToken<BaseResponse<PlaylistInfo>>() {
                }.getType();
                BaseResponse<PlaylistInfo> response = gson.fromJson(content, type);

                if (response != null && response.data != null && response.data.playlistVideos != null && !response.data.playlistVideos.isEmpty()) {
                    callBack.onResponse(response.data.playlistVideos);
                }
            }
        }).start();
    }


    /**
     * Sample code for assembling video info list
     *
     * @return video info list
     */
    public static ArrayList<VideoInfo> assembleVideoInfoList() {
        ArrayList<VideoInfo> videoInfoList = new ArrayList<>();

        // TODO: Add video info into videoInfoList
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.playlistId = "1"; // TODO 视频列表的唯一标识 ID
        videoInfo.playAuth = ""; // TODO 播放凭证
        videoInfo.title = ""; // TODO 视频标题
        videoInfo.description = ""; // TODO 视频描述
        videoInfo.videoId = ""; // TODO 视频ID
        videoInfoList.add(videoInfo);

        // TODO: Add others video info into videoInfoList...

        return videoInfoList;
    }

    /**
     * Sample code for convert video info list to JSON
     *
     * @return video info list serializable JSON String
     */
    public static String serializeVideoInfoListToJson(List<VideoInfo> videoInfoList) {
        Gson gson = new Gson();
        return gson.toJson(videoInfoList);
    }

    /**
     * Sample code for deserialize video info list from JSON
     *
     * @param json video info list serializable JSON String
     * @return video info list
     */
    public static List<VideoInfo> deserializeVideoInfoListFromJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        List<VideoInfo> videoInfoList = null;
        try {
            Gson gson = new Gson();
            videoInfoList = gson.fromJson(json, new TypeToken<List<VideoInfo>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videoInfoList != null && videoInfoList.size() > 0 ? videoInfoList : null;
    }

    public static boolean isSameVideoInfo(VideoInfo selectVideoInfo, VideoInfo currentVideoInfo) {
        if (selectVideoInfo == null || currentVideoInfo == null) return false;

        return Objects.equals(selectVideoInfo.videoId, currentVideoInfo.videoId);
    }
}
