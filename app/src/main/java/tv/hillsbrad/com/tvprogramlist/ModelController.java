package tv.hillsbrad.com.tvprogramlist;

import tv.hillsbrad.com.yahoo.YahooTvConstant;
import tv.hillsbrad.com.model.ChannelGroup;
import tv.hillsbrad.com.yahoo.YahooTvTimeParser;

import java.util.Calendar;

/**
 * Created by alex on 6/27/16.
 */
public class ModelController {
    private YahooTvConstant.Group mCurrentGroup;

    private ChannelGroup[] mChannelsGroup;

    public ModelController() {
        mCurrentGroup = YahooTvConstant.Group.ONE;
        mChannelsGroup = new ChannelGroup[YahooTvConstant.Group.values().length];
    }

    private void attach(ChannelGroup group) {
        if (mChannelsGroup[mCurrentGroup.getValue()-1] == null) {
            mChannelsGroup[mCurrentGroup.getValue()-1] = new ChannelGroup();
        }
        mChannelsGroup[mCurrentGroup.getValue()-1].attach(group);
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

    public void parseMoreDataFromHttp(boolean next) {
        if (mCurrentGroup == YahooTvConstant.Group.THIRTEEN) {
            parseMoreCustomizeDataFromHttp(next);
            return;
        }

        ChannelGroup channelGroup = YahooTvTimeParser.parse(mCurrentGroup, getQueryDate(next));
        attach(channelGroup);
    }

    private void parseMoreCustomizeDataFromHttp(boolean next) {
        YahooTvConstant.Group group = mCurrentGroup;
        Calendar calendar = getQueryDate(next);
        ChannelGroup channelGroup = YahooTvTimeParser.parse(YahooTvConstant.Group.ONE, calendar);
        attach(channelGroup);

        channelGroup = YahooTvTimeParser.parse(YahooTvConstant.Group.TWO, calendar);
        attach(channelGroup);
    }
}
