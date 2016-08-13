package tv.hillsbrad.com.modelTest;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import tv.hillsbrad.com.model.Channel;
import tv.hillsbrad.com.model.ChannelGroup;
import tv.hillsbrad.com.model.Program;
import tv.hillsbrad.com.yahoo.YahooTvTimeParser;

/**
 * Created by alex on 7/26/16.
 */
public class FakeProgram3 {

    public static int forwardIndex;
    public static int backwardIndex;

    public static ArrayList<ChannelGroup> channelGroupList = new ArrayList<>();

    static {
        generateFakeData();
    }

    public static void clear() {
//        forwardIndex = channelGroupList.size() - 1;
//        backwardIndex = forwardIndex - 1;

        forwardIndex = 0;
        backwardIndex = 0;
    }

    public static ChannelGroup getFakeData(boolean forward) {
        int index = -1;
        if (forward && forwardIndex < channelGroupList.size()) {
            index = forwardIndex++;
        } else if (!forward && backwardIndex > 0) {
            index = backwardIndex--;
        }

        Log.d("alexx", "fakeChannel " + index);
        if (index != -1) {
            return channelGroupList.get(index);
        } else {
            return null;
        }
    }

    public static void generateFakeData() {
        channelGroupList.clear();

        ChannelGroup fakeChannelGroup = new ChannelGroup();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 6, 30, 22, 0);
        calendar.setTime(YahooTvTimeParser.convert2StartDate(calendar));

        fakeChannelGroup.setProcessDate(calendar.getTime());

        Channel channel = new Channel("AXN");
        fakeChannelGroup.addChannel(channel);
        Program program = new Program("麻辣公主", "09:00PM~11:00PM", calendar);
        channel.addProgram(program);
        program = new Program("功夫", "11:00PM~01:00AM", calendar);
        channel.addProgram(program);

        channel = new Channel("START MOVIES");
        fakeChannelGroup.addChannel(channel);
        program = new Program("復仇者聯盟2:奧創紀元", "09:00PM~11:50PM", calendar);
        channel.addProgram(program);
        program = new Program("換命法則", "11:50PM~08:00AM", calendar);
        channel.addProgram(program);

        channel = new Channel("好萊塢電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("酷斯拉(普)", "09:55PM~12:55AM", calendar);
        channel.addProgram(program);
        channelGroupList.add(fakeChannelGroup);
        //-----------------------------------------

        fakeChannelGroup = new ChannelGroup();
        calendar = Calendar.getInstance();
        calendar.set(2016, 6, 31, 0, 0);
        calendar.setTime(YahooTvTimeParser.convert2StartDate(calendar));

        fakeChannelGroup.setProcessDate(calendar.getTime());

        channel = new Channel("AXN");
        fakeChannelGroup.addChannel(channel);
        program = new Program("功夫", "11:00PM~01:00AM", calendar);
        channel.addProgram(program);
        program = new Program("電影幕後花絮:神鬼認證:傑森包恩", "01:00AM~01:30AM", calendar);
        channel.addProgram(program);
        program = new Program("電影幕後花絮:星際爭霸戰:浩瀚無垠", "01:30AM~02:00AM", calendar);
        channel.addProgram(program);

        channel = new Channel("START MOVIES");
        fakeChannelGroup.addChannel(channel);
        program = new Program("歡迎來到殺人勝地(輔)", "12:55AM~02:55AM", calendar);
        channel.addProgram(program);
        channelGroupList.add(fakeChannelGroup);

        fakeChannelGroup = new ChannelGroup();
        calendar = Calendar.getInstance();
        calendar.set(2016, 6, 31, 2, 0);
        calendar.setTime(YahooTvTimeParser.convert2StartDate(calendar));

        fakeChannelGroup.setProcessDate(calendar.getTime());

        channel = new Channel("AXN");
        fakeChannelGroup.addChannel(channel);
        program = new Program("國務卿女士 (第2季)#7", "02:00AM~03:00AM", calendar);
        channel.addProgram(program);
        program = new Program("國務卿女士 (第2季)#8", "03:00AM~04:00AM", calendar);
        channel.addProgram(program);

        channel = new Channel("START MOVIES");
        fakeChannelGroup.addChannel(channel);
        program = new Program("歡迎來到殺人勝地(輔)", "12:55AM~02:55AM", calendar);
        channel.addProgram(program);
        program = new Program("驚爆飛行(普)", "02:55AM~08:00AM", calendar);
        channel.addProgram(program);
        channelGroupList.add(fakeChannelGroup);

        clear();
    }
}
