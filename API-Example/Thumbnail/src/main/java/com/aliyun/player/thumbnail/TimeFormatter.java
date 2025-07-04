package com.aliyun.player.thumbnail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @author junhuiYe
 * @date 2025/6/10
 * @brief 时间格式化工具
 */

public class TimeFormatter {
    /**
     * 格式化毫秒数为 xx:xx:xx这样的时间格式。
     *
     * @param ms 毫秒数
     * @return 格式化后的字符串
     */
    public static String formatMs(long ms) {
        int seconds = (int) (ms / 1000);
        int finalSec = seconds % 60;
        int finalMin = seconds / 60 % 60;
        int finalHour = seconds / 3600;

        StringBuilder msBuilder = new StringBuilder();
        if (finalHour > 9) {
            msBuilder.append(finalHour).append(":");
        } else if (finalHour > 0) {
            msBuilder.append("0").append(finalHour).append(":");
        } else {
            msBuilder.append("00").append(":");
        }

        if (finalMin > 9) {
            msBuilder.append(finalMin).append(":");
        } else if (finalMin > 0) {
            msBuilder.append("0").append(finalMin).append(":");
        } else {
            msBuilder.append("00").append(":");
        }

        if (finalSec > 9) {
            msBuilder.append(finalSec);
        } else if (finalSec > 0) {
            msBuilder.append("0").append(finalSec);
        } else {
            msBuilder.append("00");
        }

        return msBuilder.toString();
    }


    public static String utcToLocal(long utc) {
        DateFormat showDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        TimeZone tz = TimeZone.getTimeZone("UTC");
        df.setTimeZone(tz);
        try {
            String format = df.format(utc);
            return showDF.format(df.parse(format));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "0";
    }
}
