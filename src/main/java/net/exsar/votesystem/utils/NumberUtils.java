package net.exsar.votesystem.utils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class NumberUtils {
    public static String timeFromLong(Long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        return dateFormat.format(timestamp);
    }

    public static String format(int tokens) {
        return NumberFormat.getInstance(Locale.GERMANY).format(tokens).replace(",00", "");
    }
}
