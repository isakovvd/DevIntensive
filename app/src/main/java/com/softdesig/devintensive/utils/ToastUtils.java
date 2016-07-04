package com.softdesig.devintensive.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * @author IsakovVlad
 * @created on 28.06.16
 */

public class ToastUtils {

    public static void showError(final String message, final Context context) {
        getToast(message, context).show();
    }

    public static void showShortMessage(String message, Context context) {
        getToast(message, context, Toast.LENGTH_SHORT).show();
    }

    private static Toast getToast(String message, Context context) {
        return getToast(message, context, Toast.LENGTH_LONG);
    }

    private static Toast getToast(String message, Context context, int length) {
        return Toast.makeText(context, message, length);
    }

}
