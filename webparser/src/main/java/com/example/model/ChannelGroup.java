package com.example.model;

import com.example.YahooTvConstant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by alex on 6/16/16.
 */
public class ChannelGroup {
    private ArrayList<Channel> channels = new ArrayList<Channel>();
    private Date mStartDate;
    private Date mEndDate;

    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    public ArrayList<Channel> getChannels() {
        return channels;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public Date getEndDate() {
        return mEndDate;
    }

    public void setProcessDate(Date startDate) {
        mStartDate = startDate;

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(startDate);
        endCal.add(Calendar.HOUR, YahooTvConstant.YAHOO_SEARCH_TIME);
        mEndDate = endCal.getTime();
    }
}
