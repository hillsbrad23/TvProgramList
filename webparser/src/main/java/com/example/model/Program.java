package com.example.model;

import android.util.Log;

import com.example.YahooTvConstant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by alex on 2016/4/14.
 */
public class Program {
    private String mTitle;
    private String mTime;

    private Date mStartDate;
    private Date mEndDate;

    public Program(String title, String time, Calendar calendar) {
        this.mTitle = title;
        this.mTime = time;

        convertTimezone(time, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY));
    }

    private void convertTimezone(String time, int year, int month, int day, int hour) {
        Calendar calendar = Calendar.getInstance();

        String[] duration = time.split("~");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mmaa", Locale.US);
            if (duration.length >= 2) {
                Date start = sdf.parse(duration[0]);
                int startHour = start.getHours();
                calendar.setTime(start);
                calendar.set(year, month, day);
                if ((startHour - YahooTvConstant.YAHOO_SEARCH_TIME) > hour) {
                    calendar.add(Calendar.DATE, -1);
                }
                if (startHour < hour &&
                    (hour - startHour) > YahooTvConstant.PREDICT_RUNTIME_NO_MARE_THAN) {
                    calendar.add(Calendar.DATE, 1);
                }
                mStartDate = calendar.getTime();

                Date end = sdf.parse(duration[1]);
                int endHour = end.getHours();
                calendar.setTime(end);
                calendar.set(year, month, day);
                if (endHour < hour) {
                    calendar.add(Calendar.DATE, 1);
                }
                mEndDate = calendar.getTime();
            }
        } catch (ParseException e) {}
    }

    public String getTitle() {
        return mTitle;
    }

    public String getTime() {
        return mTime;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public Date getEndDate() {
        return mEndDate;
    }

    public int getRuntime() {
        long duration  = mEndDate.getTime() - mStartDate.getTime();
        return (int) TimeUnit.MILLISECONDS.toMinutes(duration);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(mTitle + " / " + getRuntime() + " / " + mStartDate + " / " + mEndDate);
        return builder.toString();
    }
}
