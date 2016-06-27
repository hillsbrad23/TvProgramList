package tv.hillsbrad.com.tvprogramlist;

import com.example.model.ChannelGroup;

/**
 * Created by alex on 6/27/16.
 */
public class ModelController {
    private ChannelGroup mChannelGroup;

    public ModelController() {
        mChannelGroup = new ChannelGroup();
    }

    public void attach(ChannelGroup group) {
        mChannelGroup.attach(group);
    }

    public ChannelGroup getModel() {
        return mChannelGroup;
    }
}
