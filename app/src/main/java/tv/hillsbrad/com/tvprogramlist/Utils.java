package tv.hillsbrad.com.tvprogramlist;

import android.content.Context;
import android.util.Log;

import com.example.model.Program;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by alex on 6/17/16.
 */
public class Utils {

    public final static int MILLISECOND_IN_HOUR = 1000 * 60 * 60;

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

    public static int getProgramSliceColor(Context context, int count) {
        if (count % 2 == 0) {
            return context.getResources().getColor(R.color.colorProgramSlice1, null);
        } else {
            return context.getResources().getColor(R.color.colorProgramSlice2, null);
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

        Log.d("alexx", "runtime=" + relatedRuntime);
        if (program.getStartDate().getTime() < viewStartDate.getTime()) {
            long pre = viewStartDate.getTime() - program.getStartDate().getTime();
            relatedRuntime -= TimeUnit.MILLISECONDS.toMinutes(pre);
            Log.d("alexx", " -= " + TimeUnit.MILLISECONDS.toMinutes(pre));
        }

        if (program.getEndDate().getTime() > viewEndDate.getTime()) {
            long post = program.getEndDate().getTime() - viewEndDate.getTime();
            relatedRuntime -= TimeUnit.MILLISECONDS.toMinutes(post);
            Log.d("alexx", " -= " + TimeUnit.MILLISECONDS.toMinutes(post));
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
}
