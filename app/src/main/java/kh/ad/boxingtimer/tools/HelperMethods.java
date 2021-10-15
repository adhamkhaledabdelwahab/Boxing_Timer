package kh.ad.boxingtimer.tools;

import android.content.Context;
import android.content.SharedPreferences;

import kh.ad.boxingtimer.R;

public abstract class HelperMethods {

    public static long stringToMilliSeconds(String time){
        String[] data = time.split(":");
        long min = Long.parseLong(data[0]);
        long sec = Long.parseLong(data[1]);
        return min * 60000 + sec * 1000;
    }

    public static SharedPreferences getStorage(Context context){
        return context.getApplicationContext().
                getSharedPreferences(context.getString(R.string.SHARED_PREFRENCES_NAME),
                        Context.MODE_PRIVATE);
    }
}
