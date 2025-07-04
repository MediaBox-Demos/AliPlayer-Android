package com.aliyun.player.example.router;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.aliyun.player.example.R;

/**
 * Schema-based navigation router
 * 基于Schema的导航路由器
 *
 * @author keria
 * @date 2025/5/31
 * @brief Router utility for schema-based navigation
 */
public class SchemaRouter {
    @SuppressLint("QueryPermissionsNeeded")
    public static boolean navigate(Context context, String schema) {
        if (TextUtils.isEmpty(schema)) {
            return false;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(schema));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
                return true;
            } else {
                showSchemaNotSupportedMessage(context, schema);
                return false;
            }
        } catch (Exception e) {
            showSchemaNotSupportedMessage(context, schema);
            return false;
        }
    }

    private static void showSchemaNotSupportedMessage(Context context, String schema) {
        String message = context.getString(R.string.schema_not_supported, schema);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
