package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyClass {
    public static void main(String[] args) {
//        try {
//            URL url = new URL("https://tw.movies.yahoo.com/service/rest/?method=ymv.tv.getList&date=2016-04-12&h=08&gid=1");
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestProperty("User-agent", "IE/6.0");
//            connection.setReadTimeout(30000);
//            connection.connect();
//
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String line;
//            // read from the urlconnection via the bufferedreader
//            while ((line = bufferedReader.readLine()) != null) {
//                System.out.println(line);
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        System.out.println(YahooTvConstant.test);
//        try {
//            String output = YahooTvTimeParser.parse(YahooTvConstant.Group.ONE);
//            System.out.println(output);
//        }catch (Exception e) {
//            e.printStackTrace();
//        }

//        String yahooUrl = "https://tw.movies.yahoo.com/service/rest/?method=ymv.tv.getList&date=2016-04-12&h=08&gid=1";
//        YahooTvTimeParser.parse(yahooUrl);

        try {
//            Calendar cal = Calendar.getInstance();
//            cal.set(Calendar.YEAR, 1988);
//            cal.set(Calendar.MONTH, Calendar.JANUARY);
//            cal.set(Calendar.DAY_OF_MONTH, 1);
//            cal.
//            Date dateRepresentation = cal.getTime();
//            System.out.println(dateRepresentation);


//            String time = "04:00PM~04:58PM";
//            String duration[] = time.split("~");
//
//            SimpleDateFormat sdf = new SimpleDateFormat("hh:mmaa");
//
//            sdf.setLenient(false);
//            Date d1 = sdf.parse(duration[0]);
//            System.out.println(d1.getHours() + ":" + d1.getMinutes());
//            Date d2 = sdf.parse(duration[1]);
//            System.out.println(d2.getHours() + ":" + d2.getMinutes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.get(Calendar.HOUR_OF_DAY));
        System.out.println(sdf.format(calendar.getTime()));

    }
}
