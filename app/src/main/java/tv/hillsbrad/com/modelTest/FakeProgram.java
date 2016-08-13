package tv.hillsbrad.com.modelTest;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import tv.hillsbrad.com.model.Channel;
import tv.hillsbrad.com.model.ChannelGroup;
import tv.hillsbrad.com.model.Program;
import tv.hillsbrad.com.yahoo.YahooTvTimeParser;

/**
 * Created by alex on 2016/7/25.
 */
public class FakeProgram {

    public static int forwardIndex;
    public static int backwardIndex;

    public static ArrayList<ChannelGroup> channelGroupList = new ArrayList<>();

    static {
        generateFakeData();
    }

    public static void clear() {
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
        calendar.set(2016, 6, 24, 22, 0);
        calendar.setTime(YahooTvTimeParser.convert2StartDate(calendar));

        fakeChannelGroup.setProcessDate(calendar.getTime());

        Channel channel = new Channel("緯來電影台");
        fakeChannelGroup.addChannel(channel);
        Program program = new Program("週日電癮院:屍憶(首)-輔", "09:00PM~10:50PM", calendar);
        channel.addProgram(program);
        program = new Program("爆漫王-普", "10:50PM~01:20AM", calendar);
        channel.addProgram(program);

        channel = new Channel("衛視電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("葉問3", "09:00PM~11:20PM", calendar);
        channel.addProgram(program);
        program = new Program("寒戰", "11:20PM~01:40AM", calendar);
        channel.addProgram(program);

        channelGroupList.add(fakeChannelGroup);
        //-----------------------------------------

        fakeChannelGroup = new ChannelGroup();
        calendar = Calendar.getInstance();
        calendar.set(2016, 6, 25, 0, 0, 0);
        calendar.setTime(YahooTvTimeParser.convert2StartDate(calendar));
        fakeChannelGroup.setProcessDate(calendar.getTime());

        channel = new Channel("緯來電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("爆漫王-普", "10:50PM~01:20AM", calendar);
        channel.addProgram(program);
        program = new Program("情慾王朝-輔", "01:20AM~03:35AM", calendar);
        channel.addProgram(program);

        channel = new Channel("衛視電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("寒戰", "11:20PM~01:40AM", calendar);
        channel.addProgram(program);
        program = new Program("B+偵探[輔]", "01:30AM~03:40AM", calendar);
        channel.addProgram(program);
        program = new Program("B+偵探", "01:40AM~03:50AM", calendar);
        channel.addProgram(program);

        channelGroupList.add(fakeChannelGroup);
        //-----------------------------------------

        fakeChannelGroup = new ChannelGroup();
        calendar = Calendar.getInstance();
        calendar.set(2016, 6, 25, 2, 0, 0);
        calendar.setTime(YahooTvTimeParser.convert2StartDate(calendar));
        fakeChannelGroup.setProcessDate(calendar.getTime());

        channel = new Channel("緯來電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("情慾王朝-輔", "01:20AM~03:35AM", calendar);
        channel.addProgram(program);
        program = new Program("神劍闖江湖-護", "03:35AM~06:20AM", calendar);
        channel.addProgram(program);

        channel = new Channel("衛視電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("B+偵探[輔]", "01:30AM~03:40AM", calendar);
        channel.addProgram(program);
        program = new Program("B+偵探", "01:40AM~03:50AM", calendar);
        channel.addProgram(program);
        program = new Program("想飛[普]", "03:40AM~06:50AM", calendar);
        channel.addProgram(program);
        program = new Program("冰裸殺", "03:50AM~05:50AM", calendar);
        channel.addProgram(program);

        channelGroupList.add(fakeChannelGroup);
        //-----------------------------------------

        fakeChannelGroup = new ChannelGroup();
        calendar = Calendar.getInstance();
        calendar.set(2016, 6, 25, 4, 0, 0);
        calendar.setTime(YahooTvTimeParser.convert2StartDate(calendar));
        fakeChannelGroup.setProcessDate(calendar.getTime());

        channel = new Channel("緯來電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("神劍闖江湖-護", "03:35AM~06:20AM", calendar);
        channel.addProgram(program);

        channel = new Channel("衛視電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("想飛[普]", "03:40AM~06:50AM", calendar);
        channel.addProgram(program);
        program = new Program("冰裸殺", "03:50AM~05:50AM", calendar);
        channel.addProgram(program);
        program = new Program("賭聖2之街頭賭聖", "05:50AM~08:00AM", calendar);
        channel.addProgram(program);

        channelGroupList.add(fakeChannelGroup);
        //-----------------------------------------

        fakeChannelGroup = new ChannelGroup();
        calendar = Calendar.getInstance();
        calendar.set(2016, 6, 25, 6, 0, 0);
        calendar.setTime(YahooTvTimeParser.convert2StartDate(calendar));
        fakeChannelGroup.setProcessDate(calendar.getTime());

        channel = new Channel("緯來電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("摩登如來神掌-普", "06:20AM~08:25AM", calendar);
        channel.addProgram(program);

        channel = new Channel("衛視電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("賭聖2之街頭賭聖", "05:50AM~08:00AM", calendar);
        channel.addProgram(program);
        program = new Program("賭聖2之街頭賭聖[護]", "06:50AM~09:00AM", calendar);
        channel.addProgram(program);

        channelGroupList.add(fakeChannelGroup);
        //-----------------------------------------

        fakeChannelGroup = new ChannelGroup();
        calendar = Calendar.getInstance();
        calendar.set(2016, 6, 25, 8, 0, 0);
        calendar.setTime(YahooTvTimeParser.convert2StartDate(calendar));
        fakeChannelGroup.setProcessDate(calendar.getTime());

        channel = new Channel("緯來電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("摩登如來神掌-普", "06:20AM~08:25AM", calendar);
        channel.addProgram(program);
        program = new Program("猛龍過江-護", "08:25AM~10:35AM", calendar);
        channel.addProgram(program);

        channel = new Channel("衛視電影台");
        fakeChannelGroup.addChannel(channel);
        program = new Program("賭聖2之街頭賭聖[護]", "06:50AM~09:00AM", calendar);
        channel.addProgram(program);
        program = new Program("名偵探柯南", "08:00AM~08:30AM", calendar);
        channel.addProgram(program);
        program = new Program("名偵探柯南", "08:30AM~09:00AM", calendar);
        channel.addProgram(program);
        program = new Program("航海王電視特別版6-2", "09:00AM~09:30AM", calendar);
        channel.addProgram(program);
        program = new Program("航海王電視特別版6-3", "09:30AM~10:00AM", calendar);
        channel.addProgram(program);

        channelGroupList.add(fakeChannelGroup);

        clear();
    }
}
