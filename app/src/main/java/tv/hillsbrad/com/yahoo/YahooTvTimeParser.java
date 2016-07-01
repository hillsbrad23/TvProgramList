package tv.hillsbrad.com.yahoo;

import android.content.Context;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import tv.hillsbrad.com.App;
import tv.hillsbrad.com.model.Channel;
import tv.hillsbrad.com.model.ChannelGroup;
import tv.hillsbrad.com.model.Program;


/**
 * Created by alex on 2016/4/14.
 */
public class YahooTvTimeParser {

    public static ChannelGroup parse(YahooTvConstant.Group group, Calendar queryTime) {
        try {
            queryTime.setTime(convert2StartDate(queryTime));
            // https://tw.movies.yahoo.com/service/rest/?method=ymv.tv.getList&date=2016-06-08&h=16&gid=1
            String url = YahooTvConstant.URL.concat("&gid=" + group.getValue() + "&date=" + getDate(queryTime) + "&h=" + queryTime.get(Calendar.HOUR_OF_DAY));
            Log.d("alexx", "parse url=" + url);
            Connection.Response response = Jsoup.connect(url).timeout(30000)
                                                            .method(Connection.Method.GET)
                                                            .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36").execute();
            Document doc = response.parse();
            Elements elements = doc.getElementsByClass("channel");

            ChannelGroup channelGroup = new ChannelGroup();
            channelGroup.setProcessDate(queryTime.getTime());
            int channelCount = 0;
            for (Element element: elements) {

                int resId = YahooTvConstant.getChannelNameRes(group.getValue(), channelCount++);
                if (resId == 0) {
                    continue;
                }

                String channelTitle = App.getContext().getString(resId);
                Channel channel = new Channel(channelTitle);
                channelGroup.addChannel(channel);

                Elements lis = element.getElementsByTag("li");
                for (Element li: lis) {
                    Program program = new Program(li.getElementsByClass("title").get(0).text(),
                                                  li.getElementsByClass("time").get(0).text(),
                                                  queryTime);
                    channel.addProgram(program);
                }
            }

            return channelGroup;
        } catch (IOException e) {
            Log.d("alexx", "", e);
        }

        return null;
    }

    public static String getDate(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendar.getTime());
    }

    public static Date convert2StartDate(Calendar calendar) {
        if (calendar.get(Calendar.HOUR_OF_DAY) % 2 == 1) {
            calendar.add(Calendar.HOUR, -1);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh aa");
        String tmp = sdf.format(calendar.getTime());
        Date date = null;
        try {
            date = sdf.parse(tmp);
        } catch (ParseException e) {}

        return date;
    }
}
