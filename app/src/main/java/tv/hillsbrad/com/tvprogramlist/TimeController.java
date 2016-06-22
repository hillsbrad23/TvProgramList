package tv.hillsbrad.com.tvprogramlist;

import android.util.Log;

import com.example.YahooTvConstant;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by alex on 6/21/16.
 */
public class TimeController {
    private Calendar mCalendar;
    private Date mViewStartDate;
    private Date mViewEndDate;

    public TimeController() {
        mCalendar = Calendar.getInstance();
    }

    public void setTime(Date date) {
        mCalendar.setTime(date);
        mViewStartDate = mCalendar.getTime();
    }

    public Calendar getCalendar() {
        return mCalendar;
    }

    public int getCurrentHourOfDay() {
        return mCalendar.get(Calendar.HOUR_OF_DAY);
    }

    public void addTime(int hour) {
        mCalendar.add(Calendar.HOUR_OF_DAY, hour);
    }

    public boolean isSearched() {
        Date currentDate = mCalendar.getTime();
        if (mViewEndDate != null &&
                mViewStartDate.getTime() <= currentDate.getTime() &&
                currentDate.getTime() < mViewEndDate.getTime()) {
            return true;
        }

        return false;
    }

    public void resetSearchTime() {
        if (mViewEndDate == null) {
            resetEndDate();
        }

        if (mCalendar.getTime().getTime() < mViewStartDate.getTime()) {
            mViewStartDate = mCalendar.getTime();
        } else if ((mCalendar.getTime().getTime() + YahooTvConstant.YAHOO_SEARCH_TIME *
                Utils.MILLISECOND_IN_HOUR) > mViewEndDate.getTime()) {
            resetEndDate();
        }
    }

    private void resetEndDate() {
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(mCalendar.getTime());
        endCal.add(Calendar.HOUR, YahooTvConstant.YAHOO_SEARCH_TIME);
        mViewEndDate = endCal.getTime();
    }

    public Date getViewStartDate() {
        return mViewStartDate;
    }

    public Date getViewEndDate() {
        return mViewEndDate;
    }
}
