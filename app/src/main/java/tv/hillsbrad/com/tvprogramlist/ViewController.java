package tv.hillsbrad.com.tvprogramlist;

import android.content.Context;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alex on 6/21/16.
 */
public class ViewController {

    public static int PROGRAM_UI_TOTAL_SHOWED = 1;
    public static int PROGRAM_UI_PARTIAL_HIDE = 2;

    private String mCurrentChannel;

    // channelContentLayout
    private HashMap<String, LinearLayout> mChannelLayoutMap;

    private HashMap<String, LinearLayout> mBackwardYahooErrorParentLayoutMap;
    private HashMap<String, ArrayList<LinearLayout>> mBackwardReusedYahooErrorLayoutMap;
    private HashMap<String, ArrayList<Long>> mBackwardReusedErrorLayoutEdgeTimeMap;

    private HashMap<String, LinearLayout> mForwardYahooErrorParentLayoutMap;
    private HashMap<String, ArrayList<LinearLayout>> mForwardReusedYahooErrorLayoutMap;
    private HashMap<String, ArrayList<Long>> mForwardReusedErrorLayoutEdgeTimeMap;

    public ViewController(Context context) {
        mChannelLayoutMap = new HashMap<>();

        mBackwardYahooErrorParentLayoutMap = new HashMap<>();
        mBackwardReusedYahooErrorLayoutMap = new HashMap<>();
        mBackwardReusedErrorLayoutEdgeTimeMap = new HashMap<>();

        mForwardYahooErrorParentLayoutMap = new HashMap<>();
        mForwardReusedYahooErrorLayoutMap = new HashMap<>();
        mForwardReusedErrorLayoutEdgeTimeMap = new HashMap<>();
    }

    public void setCurrentChannel(String channel) {
        mCurrentChannel = channel;
    }

    public boolean containsChannel(String channelTitle) {
        return mChannelLayoutMap.containsKey(channelTitle);
    }

    public LinearLayout getChannelLayout(String channelTitle) {
        return mChannelLayoutMap.get(channelTitle);
    }

    public void addChannelLayout(String channelTitle, LinearLayout channelContentLayout) {
        mChannelLayoutMap.put(channelTitle, channelContentLayout);
    }

    public LinearLayout getYahooErrorParentLayout(boolean forward) {
        if (forward) {
            return mForwardYahooErrorParentLayoutMap.get(mCurrentChannel);
        } else {
            return mBackwardYahooErrorParentLayoutMap.get(mCurrentChannel);
        }
    }

    public void putYahooErrorParentLayout(String channelTitle, boolean forward, LinearLayout layout) {
        if (forward) {
            mForwardYahooErrorParentLayoutMap.put(channelTitle, layout);
        } else {
            mBackwardYahooErrorParentLayoutMap.put(channelTitle, layout);
        }
    }

    public void removeYahooErrorParentLayout(boolean forward) {
        removeYahooErrorParentLayout(mCurrentChannel, forward);
    }

    public void removeYahooErrorParentLayout(String channelTitle, boolean forward) {
        if (forward) {
            mForwardYahooErrorParentLayoutMap.remove(channelTitle);
        } else {
            mBackwardYahooErrorParentLayoutMap.remove(channelTitle);
        }
    }

    public void putYahooErrorParentLayout(boolean forward, LinearLayout layout) {
        putYahooErrorParentLayout(mCurrentChannel, forward, layout);
    }

    public ArrayList<LinearLayout> getReusedErrorLayout(boolean forward) {
        if (forward) {
            return mForwardReusedYahooErrorLayoutMap.get(mCurrentChannel);
        } else {
            return mBackwardReusedYahooErrorLayoutMap.get(mCurrentChannel);
        }
    }

    public void putReusederrorLayout(String channelTitle, boolean forward, ArrayList<LinearLayout> list) {
        if (forward) {
            mForwardReusedYahooErrorLayoutMap.put(channelTitle, list);
        } else {
            mBackwardReusedYahooErrorLayoutMap.put(channelTitle, list);
        }
    }

    public void putReusederrorLayout(boolean forward, ArrayList<LinearLayout> list) {
        putReusederrorLayout(mCurrentChannel, forward, list);
    }

    public ArrayList<Long> getReusedErrorLayoutEdgeTime(boolean forward) {
        if (forward) {
            return mForwardReusedErrorLayoutEdgeTimeMap.get(mCurrentChannel);
        } else {
            return mBackwardReusedErrorLayoutEdgeTimeMap.get(mCurrentChannel);
        }
    }

    public void putReusedErrorLayoutEdgeTime(String channelTitle, boolean forward, ArrayList<Long> list) {
        if (forward) {
            mForwardReusedErrorLayoutEdgeTimeMap.put(channelTitle, list);
        } else {
            mBackwardReusedErrorLayoutEdgeTimeMap.put(channelTitle, list);
        }
    }

    public void putReusedErrorLayoutEdgeTime(boolean forward, ArrayList<Long> list) {
        putReusedErrorLayoutEdgeTime(mCurrentChannel, forward, list);
    }

    public void clear() {
        mChannelLayoutMap.clear();

        mBackwardYahooErrorParentLayoutMap.clear();
        mBackwardReusedYahooErrorLayoutMap.clear();
        mBackwardReusedErrorLayoutEdgeTimeMap.clear();

        mForwardYahooErrorParentLayoutMap.clear();
        mForwardReusedYahooErrorLayoutMap.clear();
        mForwardReusedErrorLayoutEdgeTimeMap.clear();
    }







    public void clearChannelLayout() {
        mChannelLayoutMap.clear();
    }
}
