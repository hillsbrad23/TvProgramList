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
public class FakeProgram2 {

    public static int forwardIndex;
    public static int backwardIndex;

    public static ArrayList<ChannelGroup> channelGroupList = new ArrayList<>();

    static {
        generateFakeData();
    }

    public static void clear() {
        forwardIndex = channelGroupList.size() - 1;
        backwardIndex = forwardIndex - 1;

        forwardIndex = 8;
        backwardIndex = 7;
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
        calendar.set(2016, 6, 26, 8, 0);
        calendar.setTime(YahooTvTimeParser.convert2StartDate(calendar));

        fakeChannelGroup.setProcessDate(calendar.getTime());

        Channel channel = new Channel("緯來電影台");
        fakeChannelGroup.addChannel(channel);
        Program program = new Program("家有(喜喜)事2009-普", "08:25AM~10:35AM", calendar);
        channel.addProgram(program);

        channel = new Channel("衛視電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("九品芝麻官[護]", "06:45AM~09:00AM", calendar);
        channel.addProgram(program);
        program = new Program("名偵探柯南", "08:00AM~08:30AM", calendar);
        channel.addProgram(program);
        program = new Program("名偵探柯南", "08:30AM~09:00AM", calendar);
        channel.addProgram(program);
        program = new Program("航海王電視特別版6-4", "09:00AM~09:30AM", calendar);
        channel.addProgram(program);
        program = new Program("航海王電視特別版6-5", "09:30AM~10:00AM", calendar);
        channel.addProgram(program);

        channelGroupList.add(fakeChannelGroup);
        //-----------------------------------------

        fakeChannelGroup = new ChannelGroup();
        calendar = Calendar.getInstance();
        calendar.set(2016, 6, 26, 10, 0);
        calendar.setTime(YahooTvTimeParser.convert2StartDate(calendar));

        fakeChannelGroup.setProcessDate(calendar.getTime());

        channel = new Channel("緯來電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("家有(喜喜)事2009-普", "08:25AM~10:35AM", calendar);
        channel.addProgram(program);
        program = new Program("中華丈夫-普", "10:35AM~12:45PM", calendar);
        channel.addProgram(program);

        channel = new Channel("衛視電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("十二生肖", "10:00AM~12:40PM", calendar);
        channel.addProgram(program);
        program = new Program("十二生肖[護]", "11:00AM~01:40PM", calendar);
        channel.addProgram(program);

        channel = new Channel("神奇電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("二十四生肖", "10:00AM~22:40PM", calendar);
        channel.addProgram(program);


        channelGroupList.add(fakeChannelGroup);

        fakeChannelGroup = new ChannelGroup();
        calendar = Calendar.getInstance();
        calendar.set(2016, 6, 26, 12, 0);
        calendar.setTime(YahooTvTimeParser.convert2StartDate(calendar));

        fakeChannelGroup.setProcessDate(calendar.getTime());

        channel = new Channel("緯來電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("中華丈夫-普", "10:35AM~12:45PM", calendar);
        channel.addProgram(program);
        program = new Program("美味賭王-普", "12:45PM~02:55PM", calendar);
        channel.addProgram(program);

        channel = new Channel("衛視電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("十二生肖[護]", "11:00AM~01:40PM", calendar);
        channel.addProgram(program);
        program = new Program("一眉道人", "12:40PM~02:25PM", calendar);
        channel.addProgram(program);
        program = new Program("聽見下雨的聲音[普]", "01:40PM~04:15PM", calendar);
        channel.addProgram(program);

        channelGroupList.add(fakeChannelGroup);

        fakeChannelGroup = new ChannelGroup();
        calendar = Calendar.getInstance();
        calendar.set(2016, 6, 26, 14, 0);
        calendar.setTime(YahooTvTimeParser.convert2StartDate(calendar));

        fakeChannelGroup.setProcessDate(calendar.getTime());

        channel = new Channel("緯來電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("美味賭王-普", "12:45PM~02:55PM", calendar);
        channel.addProgram(program);
        program = new Program("有情飲水飽-普", "02:55PM~05:00PM", calendar);
        channel.addProgram(program);

        channel = new Channel("衛視電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("一眉道人", "12:40PM~02:25PM", calendar);
        channel.addProgram(program);
        program = new Program("聽見下雨的聲音[普]", "01:40PM~04:15PM", calendar);
        channel.addProgram(program);
        program = new Program("A 計劃續集", "02:25PM~04:35PM", calendar);
        channel.addProgram(program);

        channelGroupList.add(fakeChannelGroup);

        fakeChannelGroup = new ChannelGroup();
        calendar = Calendar.getInstance();
        calendar.set(2016, 6, 26, 16, 0);
        calendar.setTime(YahooTvTimeParser.convert2StartDate(calendar));

        fakeChannelGroup.setProcessDate(calendar.getTime());

        channel = new Channel("緯來電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("有情飲水飽-普", "02:55PM~05:00PM", calendar);
        channel.addProgram(program);
        program = new Program("新兵日記之特戰英雄#19 #20-普", "05:00PM~07:00PM", calendar);
        channel.addProgram(program);

        channel = new Channel("衛視電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("A 計劃續集", "02:25PM~04:35PM", calendar);
        channel.addProgram(program);
        program = new Program("雞排英雄[普]", "04:15PM~07:00PM", calendar);
        channel.addProgram(program);
        program = new Program("與龍共舞", "04:35PM~06:50PM", calendar);
        channel.addProgram(program);

        channelGroupList.add(fakeChannelGroup);

        fakeChannelGroup = new ChannelGroup();
        calendar = Calendar.getInstance();
        calendar.set(2016, 6, 26, 18, 0, 0);
        calendar.setTime(YahooTvTimeParser.convert2StartDate(calendar));
        fakeChannelGroup.setProcessDate(calendar.getTime());

        channel = new Channel("緯來電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("新兵日記之特戰英雄#19 #20-普", "05:00PM~07:00PM", calendar);
        channel.addProgram(program);
        program = new Program("鹿鼎記#20 #21-普", "07:00PM~09:00PM", calendar);
        channel.addProgram(program);

        channel = new Channel("衛視電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("雞排英雄[普]", "04:15PM~07:00PM", calendar);
        channel.addProgram(program);
        program = new Program("與龍共舞", "04:35PM~06:50PM", calendar);
        channel.addProgram(program);
        program = new Program("名偵探柯南", "06:50PM~07:20PM", calendar);
        channel.addProgram(program);
        program = new Program("名偵探柯南[護]", "07:00PM~07:30PM", calendar);
        channel.addProgram(program);
        program = new Program("名偵探柯南", "07:20PM~07:50PM", calendar);
        channel.addProgram(program);
        program = new Program("名偵探柯南[護]", "07:30PM~08:00PM", calendar);
        channel.addProgram(program);
        program = new Program("航海王電視特別版", "07:50PM~09:00PM", calendar);
        channel.addProgram(program);

        channelGroupList.add(fakeChannelGroup);

        fakeChannelGroup = new ChannelGroup();
        calendar = Calendar.getInstance();
        calendar.set(2016, 6, 26, 20, 0, 0);
        calendar.setTime(YahooTvTimeParser.convert2StartDate(calendar));
        fakeChannelGroup.setProcessDate(calendar.getTime());

        channel = new Channel("緯來電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("鹿鼎記#20 #21-普", "07:00PM~09:00PM", calendar);
        channel.addProgram(program);
        program = new Program("乾隆下揚州-普", "09:00PM~11:00PM", calendar);
        channel.addProgram(program);

        channel = new Channel("衛視電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("航海王電視特別版", "07:50PM~09:00PM", calendar);
        channel.addProgram(program);
        program = new Program("烏龍派出所特別篇[普]", "08:00PM~09:00PM", calendar);
        channel.addProgram(program);
        program = new Program("食神", "09:00PM~11:05PM", calendar);
        channel.addProgram(program);

        channelGroupList.add(fakeChannelGroup);

        fakeChannelGroup = new ChannelGroup();
        calendar = Calendar.getInstance();
        calendar.set(2016, 6, 26, 22, 0, 0);
        calendar.setTime(YahooTvTimeParser.convert2StartDate(calendar));
        fakeChannelGroup.setProcessDate(calendar.getTime());

        channel = new Channel("緯來電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("乾隆下揚州-普", "09:00PM~11:00PM", calendar);
        channel.addProgram(program);
        program = new Program("賭城大亨II之至尊無敵-護", "11:00PM~01:20AM", calendar);
        channel.addProgram(program);

        channel = new Channel("衛視電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("食神", "09:00PM~11:05PM", calendar);
        channel.addProgram(program);
        program = new Program("唐伯虎衝上雲霄", "11:05PM~01:10AM", calendar);
        channel.addProgram(program);

        channelGroupList.add(fakeChannelGroup);

        fakeChannelGroup = new ChannelGroup();
        calendar = Calendar.getInstance();
        calendar.set(2016, 6, 27, 0, 0, 0);
        calendar.setTime(YahooTvTimeParser.convert2StartDate(calendar));
        fakeChannelGroup.setProcessDate(calendar.getTime());

        channel = new Channel("緯來電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("賭城大亨II之至尊無敵-護", "11:00PM~01:20AM", calendar);
        channel.addProgram(program);
        program = new Program("怨鬼-輔", "01:20AM~03:25AM", calendar);
        channel.addProgram(program);

        channel = new Channel("衛視電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("唐伯虎衝上雲霄", "11:05PM~01:10AM", calendar);
        channel.addProgram(program);
        program = new Program("BBS鄉民的正義", "01:10AM~03:25AM", calendar);
        channel.addProgram(program);

        channelGroupList.add(fakeChannelGroup);

        fakeChannelGroup = new ChannelGroup();
        calendar = Calendar.getInstance();
        calendar.set(2016, 6, 27, 2, 0, 0);
        calendar.setTime(YahooTvTimeParser.convert2StartDate(calendar));
        fakeChannelGroup.setProcessDate(calendar.getTime());

        channel = new Channel("緯來電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("怨鬼-輔", "01:20AM~03:25AM", calendar);
        channel.addProgram(program);
        program = new Program("香港地下司令-護", "03:25AM~05:50AM", calendar);
        channel.addProgram(program);

        channel = new Channel("衛視電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("BBS鄉民的正義", "01:10AM~03:25AM", calendar);
        channel.addProgram(program);
        program = new Program("盜馬記", "03:25AM~05:30AM", calendar);
        channel.addProgram(program);

        channelGroupList.add(fakeChannelGroup);

        fakeChannelGroup = new ChannelGroup();
        calendar = Calendar.getInstance();
        calendar.set(2016, 6, 27, 4, 0, 0);
        calendar.setTime(YahooTvTimeParser.convert2StartDate(calendar));
        fakeChannelGroup.setProcessDate(calendar.getTime());

        channel = new Channel("緯來電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("香港地下司令-護", "03:25AM~05:50AM", calendar);
        channel.addProgram(program);
        program = new Program("黃飛鴻之鐵雞鬥蜈蚣-普", "05:50AM~08:15AM", calendar);
        channel.addProgram(program);

        channel = new Channel("衛視電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("盜馬記", "03:25AM~05:30AM", calendar);
        channel.addProgram(program);
        program = new Program("愛的麵包魂", "05:30AM~08:00AM", calendar);
        channel.addProgram(program);

        channelGroupList.add(fakeChannelGroup);

        fakeChannelGroup = new ChannelGroup();
        calendar = Calendar.getInstance();
        calendar.set(2016, 6, 27, 6, 0, 0);
        calendar.setTime(YahooTvTimeParser.convert2StartDate(calendar));
        fakeChannelGroup.setProcessDate(calendar.getTime());

        channel = new Channel("緯來電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("黃飛鴻之鐵雞鬥蜈蚣-普", "05:50AM~08:15AM", calendar);
        channel.addProgram(program);

        channel = new Channel("衛視電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("愛的麵包魂", "05:30AM~08:00AM", calendar);
        channel.addProgram(program);

        channelGroupList.add(fakeChannelGroup);

        clear();
    }
}
