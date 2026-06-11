package com.alivc.player.playerkits.shortvideolist.utils;

import com.alivc.player.playerkits.shortvideolist.AUIShortVideoListConstants;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class NetWorkRequestUtils {
    public static OkHttpClient getOkHttpClient() {
        return new OkHttpClient()
                .newBuilder()
                .readTimeout(5L, TimeUnit.SECONDS)
                .writeTimeout(5L, TimeUnit.SECONDS)
                .connectTimeout(5L, TimeUnit.SECONDS)
                .build();
    }

    public static <T> Request postContentFromURI(T requestBody) {
        Gson gson = new Gson();
        String json = gson.toJson(requestBody);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json;charset=utf-8"));
        return new Request.Builder()
                .url(AUIShortVideoListConstants.DEFAULT_PLAYLIST_INFO_LIST_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
    }
}
