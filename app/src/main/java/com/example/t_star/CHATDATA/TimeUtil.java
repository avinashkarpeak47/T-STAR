package com.example.t_star.CHATDATA;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {

    public static String format(long ts) {
        if (ts <= 0) return "";
        return new SimpleDateFormat("hh:mm a", Locale.getDefault())
                .format(new Date(ts));
    }
}
