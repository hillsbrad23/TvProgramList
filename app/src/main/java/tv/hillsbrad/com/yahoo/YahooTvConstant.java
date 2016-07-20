package tv.hillsbrad.com.yahoo;

import tv.hillsbrad.com.tvprogramlist.R;

/**
 * Created by alex on 2016/4/14.
 */
public class YahooTvConstant {

    public final static int[] CHANNEL_TYPE = {
            R.string.channel_type_one, R.string.channel_type_two, R.string.channel_type_three,
            R.string.channel_type_four, R.string.channel_type_five, R.string.channel_type_six,
            R.string.channel_type_seven, R.string.channel_type_eight, R.string.channel_type_nine,
            R.string.channel_type_ten, R.string.channel_type_eleven, R.string.channel_type_twelve,
            R.string.channel_type_thirteen};

    public enum Group {
        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9),
        TEN(10),
        ELEVEN(11),
        TWELVE(12),
        THIRTEEN(13);   // custom

        private int value;

        private Group(int value) {
            this.value = value;
        }

        public int getStringRes() {
            return CHANNEL_TYPE[value-1];
        }

        public int getValue() {
            return value;
        }

        public static Group convertToGroup(int i) {
            Group groupDefault = ONE;
            switch (i) {
                case 1:
                    groupDefault = ONE;
                    break;
                case 2:
                    groupDefault = TWO;
                    break;
                case 3:
                    groupDefault = THREE;
                    break;
                case 4:
                    groupDefault = FOUR;
                    break;
                case 5:
                    groupDefault = FIVE;
                    break;
                case 6:
                    groupDefault = SIX;
                    break;
                case 7:
                    groupDefault = SEVEN;
                    break;
                case 8:
                    groupDefault = EIGHT;
                    break;
                case 9:
                    groupDefault = NINE;
                    break;
                case 10:
                    groupDefault = TEN;
                    break;
                case 11:
                    groupDefault = ELEVEN;
                    break;
                case 12:
                    groupDefault = TWELVE;
                    break;
                case 13:
                    groupDefault = THIRTEEN;
                    break;
            }
            return groupDefault;
        }

        public static Group convertResIdToGroup(int resId) {
            Group groupDefault = ONE;
            switch (resId) {
                case R.string.channel_type_one:
                    groupDefault = ONE;
                    break;
                case R.string.channel_type_two:
                    groupDefault = TWO;
                    break;
                case R.string.channel_type_three:
                    groupDefault = THREE;
                    break;
                case R.string.channel_type_four:
                    groupDefault = FOUR;
                    break;
                case R.string.channel_type_five:
                    groupDefault = FIVE;
                    break;
                case R.string.channel_type_six:
                    groupDefault = SIX;
                    break;
                case R.string.channel_type_seven:
                    groupDefault = SEVEN;
                    break;
                case R.string.channel_type_eight:
                    groupDefault = EIGHT;
                    break;
                case R.string.channel_type_nine:
                    groupDefault = NINE;
                    break;
                case R.string.channel_type_ten:
                    groupDefault = TEN;
                    break;
                case R.string.channel_type_eleven:
                    groupDefault = ELEVEN;
                    break;
                case R.string.channel_type_twelve:
                    groupDefault = TWELVE;
                    break;
            }
            return groupDefault;
        }
    };

    // https://tw.movies.yahoo.com/service/rest/?method=ymv.tv.getList&gid=1
    // https://tw.movies.yahoo.com/service/rest/?method=ymv.tv.getList&date=2016-06-08&h=16&gid=1
    public static String URL = "https://tw.movies.yahoo.com/service/rest/?method=ymv.tv.getList";

    public final static int YAHOO_SEARCH_HOUR = 2; //hour

    public final static int PREDICT_RUNTIME_NO_MARE_THAN = 10; //hour

    public static int[][] sChannelMapping = {
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
        if (channel < sChannelMapping[group-1].length) {
            return sChannelMapping[group-1][channel];
        }
        return 0;
    }
}
