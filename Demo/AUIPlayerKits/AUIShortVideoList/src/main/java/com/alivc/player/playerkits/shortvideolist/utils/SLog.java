package com.alivc.player.playerkits.shortvideolist.utils;

import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author keria
 * @date 2024/9/25
 * @brief log util
 * @note Android Studio logcat filter:
 * package:mine tag:AUIShortVideoList AliPlayerPool
 */
public class SLog {
    private static final String TAG = "AUIShortVideoList";
    public static boolean ENABLE_LOG = true;
    public static final int BUFFER_SIZE = 3000;

    private SLog() {
    }

    public static void log(Object o, String method, String s) {
        if (!ENABLE_LOG) {
            return;
        }

        int length = s.length();
        int startIndex = 0;

        while (startIndex < length) {
            int endIndex = Math.min(length, startIndex + BUFFER_SIZE);
            SLog.v(o, method, s.substring(startIndex, endIndex));
            startIndex = endIndex;
        }
    }

    public static void l(int level, Object o, String method, Object... messages) {
        if (ENABLE_LOG) {
            Log.println(level, TAG, createLog(o, method, messages));
        }
    }

    public static void v(Object o, String method, Object... messages) {
        if (ENABLE_LOG) {
            Log.v(TAG, createLog(o, method, messages));
        }
    }

    public static void v(Object o, String method, Throwable throwable, Object... messages) {
        if (ENABLE_LOG) {
            Log.v(TAG, createLog(o, method, messages), throwable);
        }
    }

    public static void d(Object o, String method, Object... messages) {
        if (ENABLE_LOG) {
            Log.d(TAG, createLog(o, method, messages));
        }
    }

    public static void d(Object o, String method, Throwable throwable, Object... messages) {
        if (ENABLE_LOG) {
            Log.d(TAG, createLog(o, method, messages), throwable);
        }
    }

    public static void i(Object o, String method, Object... messages) {
        if (ENABLE_LOG) {
            Log.i(TAG, createLog(o, method, messages));
        }
    }

    public static void i(Object o, String method, Throwable throwable, Object... messages) {
        if (ENABLE_LOG) {
            Log.i(TAG, createLog(o, method, messages), throwable);
        }
    }

    public static void e(Object o, String method, Object... messages) {
        if (ENABLE_LOG) {
            Log.e(TAG, createLog(o, method, messages));
        }
    }

    public static void e(Object o, String method, Throwable throwable, Object... messages) {
        if (ENABLE_LOG) {
            Log.e(TAG, createLog(o, method, messages), throwable);
        }
    }

    public static void w(Object o, String method, Object... messages) {
        if (ENABLE_LOG) {
            Log.w(TAG, createLog(o, method, messages));
        }
    }

    public static void w(Object o, String method, Throwable throwable, Object... messages) {
        if (ENABLE_LOG) {
            Log.w(TAG, createLog(o, method, messages), throwable);
        }
    }

    public static void t(String method, long costTime) {
        if (ENABLE_LOG) {
            String threadName = Looper.myLooper() == Looper.getMainLooper() ? "MAIN" : "SUB";
            Log.d("TimeProfiler", createLog(null, method, new Object[]{threadName, "costTime: " + costTime}));
        }
    }

    private static String createLog(Object o, String method, Object[] messages) {
        StringBuilder msg = new StringBuilder();
        if (o != null) {
            msg.append("[").append(slimObj(o)).append("]");
        }
        msg.append("[").append(method).append("]");

        if (messages != null && messages.length > 0) {
            msg.append(" -> ");
            for (Object message : messages) {
                String str = slimObj(message);
                if (!TextUtils.isEmpty(str) && str.startsWith("[") && str.endsWith("]")) {
                    msg.append(str);
                } else {
                    msg.append("[").append(str).append("]");
                }
            }
        }
        return msg.toString();
    }

    public static String string(Object o) {
        if (o == null) {
            return "null";
        }
        return ENABLE_LOG ? o.toString() : "";
    }

    public static String slimObj(Object o) {
        if (o == null) {
            return "null";
        } else if (o instanceof String) {
            return (String) o;
        } else if (o instanceof Boolean) {
            return String.valueOf(o);
        } else if (o instanceof Number) {
            return String.valueOf(o);
        } else if (o instanceof Iterable<?>) {
            return String.valueOf(o);
        } else if (o.getClass().isAnonymousClass()) {
            String s = o.toString();
            return s.substring(s.lastIndexOf('.') + 1);
        } else if (o instanceof Class<?>) {
            return ((Class<?>) o).getSimpleName();
        } else {
            return o.getClass().getSimpleName() + '@' + Integer.toHexString(o.hashCode());
        }
    }
}
