package tv.hillsbrad.com.tvprogramlist;

import com.example.YahooTvConstant;
import com.example.model.ChannelGroup;

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

    public void attach(ChannelGroup group) {
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
}
