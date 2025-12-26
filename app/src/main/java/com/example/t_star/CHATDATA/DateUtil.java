package com.example.t_star.CHATDATA;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static String getDateLabel(long time) {
        Calendar msgCal = Calendar.getInstance();
        msgCal.setTimeInMillis(time);

        Calendar now = Calendar.getInstance();

        if (isSameDay(msgCal, now)) {
            return "Today";
        }

        now.add(Calendar.DAY_OF_YEAR, -1);
        if (isSameDay(msgCal, now)) {
            return "Yesterday";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(new Date(time));
    }

    private static boolean isSameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }
}
