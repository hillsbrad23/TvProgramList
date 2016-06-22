package com.example.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alex on 6/16/16.
 */
public class ChannelGroup {
    private ArrayList<Channel> channels = new ArrayList<Channel>();
    private Date mStartDate;

    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    public ArrayList<Channel> getChannels() {
        return channels;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date date) {
        mStartDate = date;
    }
}
