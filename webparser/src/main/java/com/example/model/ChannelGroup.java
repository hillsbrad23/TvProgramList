package com.example.model;

import com.example.YahooTvConstant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by alex on 6/16/16.
 */
public class ChannelGroup {
//    private ArrayList<Channel> mChannels = new ArrayList<Channel>();
    private HashMap<String, Channel> mChannels;

    private Date mSearchingStartDate;
    private Date mSearchingEndDate;

    public ChannelGroup() {
        mChannels = new HashMap<>();
    }

    public void addChannel(Channel channel) {
        if (!mChannels.containsKey(channel.getTitle())) {
            mChannels.put(channel.getTitle(), channel);
        }
    }

    public HashMap<String, Channel> getChannels() {
        return mChannels;
    }

    public Date getSearchingStartDate() {
        return mSearchingStartDate;
    }

    public Date getSearchingEndDate() {
        return mSearchingEndDate;
    }

    public void setProcessDate(Date startDate) {
        mSearchingStartDate = startDate;

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(startDate);
        endCal.add(Calendar.HOUR, YahooTvConstant.YAHOO_SEARCH_TIME);
        mSearchingEndDate = endCal.getTime();
    }

    public void attach(ChannelGroup channelGroup) {
        if (mSearchingStartDate == null ||
                channelGroup.getSearchingStartDate().getTime() < mSearchingStartDate.getTime()) {
            mSearchingStartDate = channelGroup.getSearchingStartDate();
        }

        if (mSearchingEndDate == null ||
                mSearchingEndDate.getTime() < channelGroup.getSearchingEndDate().getTime()) {
            mSearchingEndDate = channelGroup.getSearchingEndDate();
        }

        for (Channel channel: channelGroup.getChannels().values()) {
            if (mChannels.containsKey(channel.getTitle())) {
                mChannels.get(channel.getTitle()).attach(channel);
            } else {
                mChannels.put(channel.getTitle(), channel);
            }
        }
    }
}
