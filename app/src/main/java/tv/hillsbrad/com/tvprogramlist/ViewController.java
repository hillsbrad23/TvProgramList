package tv.hillsbrad.com.tvprogramlist;

import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alex on 6/21/16.
 */
public class ViewController {
    private HashMap<String, LinearLayout> mChannelMap;


    public ViewController() {
        mChannelMap = new HashMap<String, LinearLayout>();
    }

    public void add(String channelTitle, LinearLayout layout) {
        if (!mChannelMap.containsKey(channelTitle)) {
            mChannelMap.put(channelTitle, layout);
        }
    }

    public boolean containsChannel(String channelTitle) {
        return mChannelMap.containsKey(channelTitle);
    }

    public LinearLayout getLayout(String channelTitle) {
        return mChannelMap.get(channelTitle);
    }
}
