package com.example;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alex on 2016/4/14.
 */
public class YahooTvConstant {

    public enum Group {
        ONE(1),      //���
        TWO(2),     //���
        THREE(3),   //���@
        FOUR(4),    //��|
        FIVE(5),     //��X
        SIX(6),      //����
        SEVEN(7),   //�饻
        EIGHT(8),   //����
        NINE(9),   //�d�q
        TEN(10),     //�s�D
        ELEVEN(11),  //�]�g
        TWELVE(12); //�v��

        private int value;

        private Group(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    };

    // https://tw.movies.yahoo.com/service/rest/?method=ymv.tv.getList&gid=1
    // https://tw.movies.yahoo.com/service/rest/?method=ymv.tv.getList&date=2016-06-08&h=16&gid=1
    public static String URL = "https://tw.movies.yahoo.com/service/rest/?method=ymv.tv.getList";

    public final static int YAHOO_SEARCH_TIME = 2; //hour

    public final static int PREDICT_RUNTIME_NO_MARE_THAN = 10; //hour

    private static int[][] mChannelMapping = {
            {},
            {R.string.one_one, R.string.one_two, R.string.one_three, R.string.one_four,
                R.string.one_five, R.string.one_six, R.string.one_seven},
            {R.string.two_one, R.string.two_two, R.string.two_three, R.string.two_four},
            {R.string.three_one, R.string.three_two, R.string.three_three, R.string.three_four},
            {R.string.four_one, R.string.four_two, R.string.four_three},
            {R.string.five_one, R.string.five_two, R.string.five_three, R.string.five_four,
                    R.string.five_five, R.string.five_six, R.string.five_seven, R.string.five_eight,
                    R.string.five_nine, R.string.five_ten, R.string.five_eleven, R.string.five_twelve,
                    R.string.five_thirteen, R.string.five_fourteen, R.string.five_fifteen, R.string.five_sixteen,
                    R.string.five_seventeen, R.string.five_eighteen, R.string.five_nineteen, R.string.five_twenty,
                    R.string.five_twenty_one, R.string.five_twenty_two, R.string.five_twenty_three, R.string.five_twenty_four,
                    R.string.five_twenty_five, R.string.five_twenty_six, R.string.five_twenty_seven, R.string.five_twenty_eight,
                    R.string.five_twenty_nine},
            {R.string.six_one, R.string.six_two, R.string.six_three, R.string.six_four},
            {R.string.seven_one, R.string.seven_two, R.string.seven_three, R.string.seven_four},
            {R.string.eight_one},
            {R.string.nine_one, R.string.nine_two, R.string.nine_three, R.string.nine_four, R.string.nine_five},
            {R.string.ten_one, R.string.ten_two, R.string.ten_three, R.string.ten_four,
                    R.string.ten_five, R.string.ten_six, R.string.ten_seven, R.string.ten_eight},
            {R.string.eleven_one, R.string.eleven_two, R.string.eleven_three},
            {R.string.twelve_one, R.string.twelve_two, R.string.twelve_three}};

    public static int getChannelNameRes(int group, int channel) {
        if (channel < mChannelMapping[group].length) {
            return mChannelMapping[group][channel];
        }
        return 0;
    }
}
