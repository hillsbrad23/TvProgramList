package tv.hillsbrad.com;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import tv.hillsbrad.com.model.Program;
import tv.hillsbrad.com.tvprogramlist.R;
import tv.hillsbrad.com.yahoo.YahooTvConstant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public static String TAG = "alexx";
    public static String TMP_TAG = "alexx";

    public static boolean PROGRAM_DEBUG = false;
    public static boolean YAHOO_ERROR_DEBUG = false;

    public static boolean USE_FAKE_DATE_DEBUG = false;
    public static boolean REFRESH_UI_ON_PARSE_NEW_CONTENT = true;


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

    public static int getProgramSliceWidth(long runtime) {  //width per hour
        return (int) (runtime * getSliceBacisWidth(App.getContext()) / 60.0f);
    }

    public static int getEmptySliceWidth(Date startDate, Date endDate) {
        long time = endDate.getTime() - startDate.getTime();
        return getProgramSliceWidth(TimeUnit.MILLISECONDS.toMinutes(time));
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

    public static String convertDayOfWeek(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return "(日)";
            case Calendar.MONDAY:
                return "(一)";
            case Calendar.TUESDAY:
                return "(二)";
            case Calendar.WEDNESDAY:
                return "(三)";
            case Calendar.THURSDAY:
                return "(四)";
            case Calendar.FRIDAY:
                return "(五)";
            case Calendar.SATURDAY:
                return "(六)";
            default:
                return "()";
        }
    }

    private static final String SHARED_PREFERENCES_NAME = "program_list_settings";
    private static final String KEY_CUSTOM_CHANNELS = "custom_channels";
    private static final String KEY_CURRENT_GROUP = "current_group";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static Set<String> loadCustomSelectedChannels(Context context) {
        SharedPreferences spref = getSharedPreferences(context);
        return spref.getStringSet(KEY_CUSTOM_CHANNELS, Collections.<String>emptySet());
    }

    public static void saveCustomSelectedChannels(Context context, Set<String> selected) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putStringSet(KEY_CUSTOM_CHANNELS, selected).apply();
    }

    public static YahooTvConstant.Group loadCurrentGroup(Context context) {
        SharedPreferences spref = getSharedPreferences(context);
        int defaultGroup = spref.getInt(KEY_CURRENT_GROUP, 1);
        return  YahooTvConstant.Group.convertToGroup(defaultGroup);
    }

    public static void saveCurrentGroup(Context context, YahooTvConstant.Group group) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(KEY_CURRENT_GROUP, group.getValue()).apply();
    }
}
