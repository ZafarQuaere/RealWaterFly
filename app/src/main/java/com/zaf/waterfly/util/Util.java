package com.zaf.waterfly.util;

import android.util.Log;

public class Util {
    public static final String TAG = "PubNub>> ";
    public static void DEBUG(String sb) {
        //To print the log on debug mode only

            if (sb.length() > 4000) {
                int chunkCount = sb.length() / 4000;
                for (int i = 0; i <= chunkCount; i++) {
                    int max = 4000 * (i + 1);
                    if (max >= sb.length()) {
                        Log.d(TAG, " >> " + sb.substring(4000 * i));
                    } else {
                        Log.d(TAG, " >> " + sb.substring(4000 * i, max));
                    }
                }
            } else {
                Log.d(TAG, " >> " + sb.toString());
            }
        }

}
