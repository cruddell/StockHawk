package com.sam_chordas.android.stockhawk.utils;

import android.content.Context;
import android.os.Handler;

/**
 * Created by ChristopherRuddell, Museum of the Bible, on 6/9/16.
 */

public class MiscUtils {

    public static void runOnUiThread(Context context, Runnable runnable) {
        Handler mainHandler = new Handler(context.getApplicationContext().getMainLooper());
        mainHandler.post(runnable);
    }
}
