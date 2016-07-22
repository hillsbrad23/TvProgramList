package tv.hillsbrad.com.tvprogramlist;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import tv.hillsbrad.com.App;
import tv.hillsbrad.com.Utils;
import tv.hillsbrad.com.model.Channel;
import tv.hillsbrad.com.model.Program;
import tv.hillsbrad.com.yahoo.YahooTvConstant;
import tv.hillsbrad.com.model.ChannelGroup;
import tv.hillsbrad.com.yahoo.YahooTvTimeParser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * Created by alex on 6/27/16.
 */
public class ModelController {

    private static ModelController sInstance;

    private YahooTvConstant.Group mCurrentGroup;

    private ChannelGroup[] mChannelsGroup;

    private Date mYahooLimitationStartDate;
    private Date mYahooLimitationEndDate;

    private int mSpecificMonth = -1, mSpecificDay = -1;

    /** User select custom channel **/
    private HashSet<String> mCustomSelectedChannels;
    private ArrayList<YahooTvConstant.Group> mCustomSelectedTypes;

    private int mWaitToParseCount;

    public static ModelController getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ModelController(context);
        }
        return sInstance;
    }

    private ModelController(Context context) {
        mCurrentGroup = Utils.loadCurrentGroup(context);

        mChannelsGroup = new ChannelGroup[YahooTvConstant.Group.values().length];
        mCustomSelectedChannels = new LinkedHashSet<>();
        mCustomSelectedTypes = new ArrayList<>();

        mCustomSelectedChannels.addAll(Utils.loadCustomSelectedChannels(App.getContext()));
        for (int i = 0; i < YahooTvConstant.sChannelMapping.length; i++) {
            for (int j = 0; j < YahooTvConstant.sChannelMapping[i].length; j++) {
                if (mCustomSelectedChannels.contains(App.getContext()
                        .getString(YahooTvConstant.sChannelMapping[i][j]))) {
                    mCustomSelectedTypes.add(YahooTvConstant.Group.convertToGroup(i+1));
                    break;
                }
            }
        }
    }

    private void attach(ChannelGroup channelGroup, boolean forward) {
        attach(mCurrentGroup, channelGroup, forward);
    }

    private void attach(YahooTvConstant.Group group, ChannelGroup channelGroup, boolean forward) {
        if (mChannelsGroup[group.getValue()-1] == null) {
            mChannelsGroup[group.getValue()-1] = new ChannelGroup();
        }
        mChannelsGroup[group.getValue()-1].attach(channelGroup, forward);
    }
    public ChannelGroup getModel() {
        if (mChannelsGroup[mCurrentGroup.getValue()-1] == null) {
            mChannelsGroup[mCurrentGroup.getValue()-1] = new ChannelGroup();
        }
        return mChannelsGroup[mCurrentGroup.getValue()-1];
    }

    public YahooTvConstant.Group getCurrentGroup() {
        return mCurrentGroup;
    }

    public void setCurrentGroup(YahooTvConstant.Group group) {
        Utils.saveCurrentGroup(App.getContext(), group);
        mCurrentGroup = group;
    }

    public void setYahooLimitationDate(Date startDate, Date endDate) {
        mYahooLimitationStartDate = startDate;
        mYahooLimitationEndDate = endDate;
    }

    public void setSearchDate(int month, int day) {
        mSpecificMonth = month;
        mSpecificDay = day;
        mChannelsGroup = new ChannelGroup[YahooTvConstant.Group.values().length];
    }

    private Calendar getQueryDate(boolean forward) {
        Calendar calendar = Calendar.getInstance();

        if (mSpecificMonth != -1 && mSpecificDay != -1) {
            calendar.set(Calendar.MONTH, mSpecificMonth - 1);
            calendar.set(Calendar.DAY_OF_MONTH, mSpecificDay);
        }

        if (mChannelsGroup[mCurrentGroup.getValue()-1] != null &&
                mChannelsGroup[mCurrentGroup.getValue()-1].getSearchingStartDate() != null &&
                mChannelsGroup[mCurrentGroup.getValue()-1].getSearchingEndDate() != null) {
            if (forward) {
                calendar.setTime(mChannelsGroup[mCurrentGroup.getValue() - 1].getSearchingEndDate());
            } else {
                calendar.setTime(mChannelsGroup[mCurrentGroup.getValue() - 1].getSearchingStartDate());
                calendar.add(Calendar.HOUR, YahooTvConstant.YAHOO_SEARCH_HOUR * -1);
            }
        }

        return calendar;
    }

    public boolean parseMoreDataFromHttp(final boolean forward) {
        if (mCurrentGroup == YahooTvConstant.Group.THIRTEEN) {  // custom type
            return parseMoreCustomDataFromHttp(forward);
        }

        final Calendar calendar = getQueryDate(forward);
        if (calendar.getTime().getTime() < mYahooLimitationStartDate.getTime() ||
                calendar.getTime().getTime() >= mYahooLimitationEndDate.getTime()) {
            return false;
        }

        mWaitToParseCount = 0;
        new Thread() {
            public void run() {
                mWaitToParseCount++;

                ChannelGroup channelGroup = YahooTvTimeParser.parse(mCurrentGroup, calendar);
                attach(channelGroup, forward);
                notifyUpdate(forward);

                if (Utils.PROGRAM_DEBUG) {
                    for (Channel channel : channelGroup.getChannels().values()) {
                        Log.d(Utils.TAG, channel.getTitle());
                        for (Program program : channel.getPrograms()) {
                            Log.d(Utils.TAG, program.getTitle() + " / " + program.getStartDate()
                                    + " / " + program.getEndDate());
                        }
                    }
                }

            }
        }.start();

        return true;
    }

    private boolean parseMoreCustomDataFromHttp(final boolean next) {
        final Calendar calendar = getQueryDate(next);
        mWaitToParseCount = 0;

        if (mCustomSelectedTypes.size() == 0) {
            notifyUpdate(next);
            return false;
        }

        if (calendar.getTime().getTime() < mYahooLimitationStartDate.getTime() ||
                calendar.getTime().getTime() >= mYahooLimitationEndDate.getTime()) {
            return false;
        }

        for (final YahooTvConstant.Group group: mCustomSelectedTypes) {

            new Thread() {
                public void run() {
                    synchronized (this) {
                        mWaitToParseCount++;
                    }

                    HashSet<String> notSelected = new HashSet<>();
                    ChannelGroup channelGroup = YahooTvTimeParser.parse(group, calendar);
                    attach(group, channelGroup, next);

                    for (String channelTitle: channelGroup.getChannels().keySet()) {
                        if (!mCustomSelectedChannels.contains(channelTitle)) {
                            Log.d(Utils.TAG, "not selected:" + channelTitle);

                            notSelected.add(channelTitle);
                        }
                    }
                    channelGroup.removeChannels(notSelected);
                    attach(channelGroup, next);
                    notifyUpdate(next);
                }
            }.start();
        }

        return true;
    }

    private void notifyUpdate(boolean next) {
        synchronized (this) {
            if (mWaitToParseCount > 0) mWaitToParseCount--;
            if (mWaitToParseCount == 0) {
                Intent in = new Intent(MainActivity.REFRESH_UI_ACTION);
                in.putExtra(MainActivity.SEARCH_NEXT, next);
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(in);
            } else {
                // FIXME add timer to trigger update
            }
        }
    }

    public HashSet<String> getCustomSelectedChannels() {
        return mCustomSelectedChannels;
    }

    public void setCustomSelectedChannels(HashSet<String> channels, int[] channelTypeCount) {
        // clear all for custom type
        mChannelsGroup[YahooTvConstant.Group.THIRTEEN.getValue()-1] = null;
        mCustomSelectedChannels.clear();
        mCustomSelectedTypes.clear();
        mCustomSelectedChannels.addAll(channels);
        updateCustomTypes(channelTypeCount);
    }

    private void updateCustomTypes(int[] channelTypeCount) {
        for (int i = 0; i < YahooTvConstant.sChannelMapping.length; i++) {
            if (channelTypeCount[i] > 0) {
                mCustomSelectedTypes.add(YahooTvConstant.Group.convertToGroup(i+1));
            }
        }
    }

    public void clear() {
        mCurrentGroup = null;
        mChannelsGroup = null;
        mChannelsGroup = new ChannelGroup[YahooTvConstant.Group.values().length];
        sInstance = null;
    }
}
