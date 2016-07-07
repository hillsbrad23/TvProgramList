package tv.hillsbrad.com.tvprogramlist;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import tv.hillsbrad.com.App;
import tv.hillsbrad.com.Utils;
import tv.hillsbrad.com.yahoo.YahooTvConstant;
import tv.hillsbrad.com.model.ChannelGroup;
import tv.hillsbrad.com.yahoo.YahooTvTimeParser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * Created by alex on 6/27/16.
 */
public class ModelController {

    private static ModelController sInstance;

    private YahooTvConstant.Group mCurrentGroup;

    private ChannelGroup[] mChannelsGroup;

    /** User select custom channel **/
    private HashSet<String> mCustomSelectedChannels;
    private ArrayList<YahooTvConstant.Group> mCustomSelectedTypes;

    private int mWaitToParseCount;

    public static ModelController getInstance() {
        if (sInstance == null) {
            sInstance = new ModelController();
        }
        return sInstance;
    }

    private ModelController() {
        mCurrentGroup = YahooTvConstant.Group.ONE;
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

    private void attach(ChannelGroup channelGroup) {
        attach(mCurrentGroup, channelGroup);
    }

    private void attach(YahooTvConstant.Group group, ChannelGroup channelGroup) {
        if (mChannelsGroup[group.getValue()-1] == null) {
            mChannelsGroup[group.getValue()-1] = new ChannelGroup();
        }
        mChannelsGroup[group.getValue()-1].attach(channelGroup);
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
        mCurrentGroup = group;
    }

    private Calendar getQueryDate(boolean forward) {
        Calendar calendar = Calendar.getInstance();
        if (mChannelsGroup[mCurrentGroup.getValue()-1] != null &&
                mChannelsGroup[mCurrentGroup.getValue()-1].getSearchingStartDate() != null &&
                mChannelsGroup[mCurrentGroup.getValue()-1].getSearchingEndDate() != null) {
            if (forward) {
                calendar.setTime(mChannelsGroup[mCurrentGroup.getValue() - 1].getSearchingEndDate());
            } else {
                calendar.setTime(mChannelsGroup[mCurrentGroup.getValue() - 1].getSearchingStartDate());
                calendar.add(Calendar.HOUR, YahooTvConstant.YAHOO_SEARCH_TIME * -1);
            }
        }

        return calendar;
    }

    public void parseMoreDataFromHttp(final boolean next) {
        Log.d("alexx", "parseMoreDataFromHttp");

        if (mCurrentGroup == YahooTvConstant.Group.THIRTEEN) {  // custom type
            parseMoreCustomDataFromHttp(next);
            return;
        }

        mWaitToParseCount = 0;
        new Thread() {
            public void run() {
                mWaitToParseCount++;

                ChannelGroup channelGroup = YahooTvTimeParser.parse(mCurrentGroup, getQueryDate(next));
                attach(channelGroup);
                notifyUpdate();
            }
        }.start();


//        ChannelGroup channelGroup = YahooTvTimeParser.parse(mCurrentGroup, getQueryDate(next));
//        attach(channelGroup);
    }

    private void parseMoreCustomDataFromHttp(boolean next) {
        Log.d("alexx", "parseMoreCustomDataFromHttp - " + mCustomSelectedTypes.size());

        final Calendar calendar = getQueryDate(next);
        mWaitToParseCount = 0;

        if (mCustomSelectedTypes.size() == 0) {
            notifyUpdate();
            return;
        }

        for (final YahooTvConstant.Group group: mCustomSelectedTypes) {

            new Thread() {
                public void run() {
                    synchronized (this) {
                        mWaitToParseCount++;
                    }

                    HashSet<String> notSelected = new HashSet<>();
                    ChannelGroup channelGroup = YahooTvTimeParser.parse(group, calendar);
                    attach(group, channelGroup);

                    for (String channelTitle: channelGroup.getChannels().keySet()) {
                        if (!mCustomSelectedChannels.contains(channelTitle)) {
                            Log.d("alexx", "not selected:" + channelTitle);

                            notSelected.add(channelTitle);
                        }
                    }
                    channelGroup.removeChannels(notSelected);
                    attach(channelGroup);
                    notifyUpdate();
                }
            }.start();
        }
    }

    public void notifyUpdate() {
        synchronized (this) {
            if (mWaitToParseCount > 0) mWaitToParseCount--;
            Log.d("alexx", "notifyUpdate mWaitToParseCount=" + mWaitToParseCount);
            if (mWaitToParseCount == 0) {
                Log.d("alexx", "broadcast REFRESH_UI");
                Intent in = new Intent(MainActivity.REFRESH_UI_ACTION);
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
        mCustomSelectedChannels.addAll(channels);
        updateCustomTypes(channelTypeCount);
    }

    private void updateCustomTypes(int[] channelTypeCount) {
        mCustomSelectedTypes.clear();
        for (int i = 0; i < YahooTvConstant.sChannelMapping.length; i++) {
            if (channelTypeCount[i] > 0) {
                mCustomSelectedTypes.add(YahooTvConstant.Group.convertToGroup(i+1));
            }
        }
    }

    public void clear() {
        mCurrentGroup = null;
        mChannelsGroup = null;
        mCustomSelectedChannels.clear();
        mCustomSelectedChannels = null;
        mCustomSelectedTypes.clear();
        mCustomSelectedTypes = null;
        sInstance = null;
    }
}
