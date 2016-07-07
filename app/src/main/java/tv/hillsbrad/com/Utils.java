package tv.hillsbrad.com;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import tv.hillsbrad.com.model.Program;
import tv.hillsbrad.com.tvprogramlist.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by alex on 6/17/16.
 */
public class Utils {

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static String toHourOfDayString(int hour) {
        return (hour % 24) + ":00";
    }

    public static int getHourOfDayColor(Context context, int hour) {
        if (hour % 2 == 0) {
            return context.getResources().getColor(R.color.colorTimeSlice1, null);
        } else {
            return context.getResources().getColor(R.color.colorTimeSlice2, null);
        }
    }

    private static int sSliceBasicWidth = -1;
    public static int getSliceBacisWidth(Context context) {
        if (sSliceBasicWidth == -1)
            sSliceBasicWidth = context.getResources().getDimensionPixelSize(R.dimen.time_slice_length);
        return sSliceBasicWidth;
    }

    public static int getProgramSliceWidth(int runtime) {  //width per hour
        return (int) (runtime * sSliceBasicWidth / 60.0f);
    }

    public static int getRelatedProgramSliceWidth(Program program, Date viewStartDate, Date viewEndDate) {
        int relatedRuntime = program.getRuntime();

        if (program.getStartDate().getTime() < viewStartDate.getTime()) {
            long pre = viewStartDate.getTime() - program.getStartDate().getTime();
            relatedRuntime -= TimeUnit.MILLISECONDS.toMinutes(pre);
        }

        if (program.getEndDate().getTime() > viewEndDate.getTime()) {
            long post = program.getEndDate().getTime() - viewEndDate.getTime();
            relatedRuntime -= TimeUnit.MILLISECONDS.toMinutes(post);
//            Log.d("alexx", " -= " + TimeUnit.MILLISECONDS.toMinutes(post));
        }

        return getProgramSliceWidth(relatedRuntime);
    }

    private static String sDateFormat = "yyyy/MM/dd hh:mm";
    public static Date convert2Date(int year, int month, int day, int hour, int minute) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(sDateFormat, Locale.US);
            date = sdf.parse("" + year + "/" + month + "/" + day + " " + hour + ":" + minute);
        } catch (ParseException e) {}

        return date;
    }

    private static final String SHARED_PREFERENCES_NAME = "program_list_settings";
    private static final String KEY_CUSTOM_CHANNELS = "custom_channels";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static void saveCustomSelectedChannels(Context context, Set<String> selected) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putStringSet(KEY_CUSTOM_CHANNELS, selected).apply();
    }

    public static Set<String> loadCustomSelectedChannels(Context context) {
        SharedPreferences spref = getSharedPreferences(context);
        return spref.getStringSet(KEY_CUSTOM_CHANNELS, Collections.<String>emptySet());
    }
}
