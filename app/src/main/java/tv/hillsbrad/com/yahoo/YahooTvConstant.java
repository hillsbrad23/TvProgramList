package tv.hillsbrad.com.yahoo;

/**
 * Created by alex on 2016/4/14.
 */
public class YahooTvConstant {

    public final static int[] CHANNEL_TYPE = {
            com.example.R.string.channel_type_one, com.example.R.string.channel_type_two, com.example.R.string.channel_type_three,
            com.example.R.string.channel_type_four, com.example.R.string.channel_type_five, com.example.R.string.channel_type_six,
            com.example.R.string.channel_type_seven, com.example.R.string.channel_type_eight, com.example.R.string.channel_type_nine,
            com.example.R.string.channel_type_ten, com.example.R.string.channel_type_eleven, com.example.R.string.channel_type_twelve,
            com.example.R.string.channel_type_thirteen};

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
        THIRTEEN(13);

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
    };

    // https://tw.movies.yahoo.com/service/rest/?method=ymv.tv.getList&gid=1
    // https://tw.movies.yahoo.com/service/rest/?method=ymv.tv.getList&date=2016-06-08&h=16&gid=1
    public static String URL = "https://tw.movies.yahoo.com/service/rest/?method=ymv.tv.getList";

    public final static int YAHOO_SEARCH_TIME = 2; //hour

    public final static int PREDICT_RUNTIME_NO_MARE_THAN = 10; //hour

    public static int[][] sChannelMapping = {
            {com.example.R.string.one_one, com.example.R.string.one_two, com.example.R.string.one_three, com.example.R.string.one_four,
                com.example.R.string.one_five, com.example.R.string.one_six, com.example.R.string.one_seven},
            {com.example.R.string.two_one, com.example.R.string.two_two, com.example.R.string.two_three, com.example.R.string.two_four},
            {com.example.R.string.three_one, com.example.R.string.three_two, com.example.R.string.three_three, com.example.R.string.three_four},
            {com.example.R.string.four_one, com.example.R.string.four_two, com.example.R.string.four_three},
            {com.example.R.string.five_one, com.example.R.string.five_two, com.example.R.string.five_three, com.example.R.string.five_four,
                    com.example.R.string.five_five, com.example.R.string.five_six, com.example.R.string.five_seven, com.example.R.string.five_eight,
                    com.example.R.string.five_nine, com.example.R.string.five_ten, com.example.R.string.five_eleven, com.example.R.string.five_twelve,
                    com.example.R.string.five_thirteen, com.example.R.string.five_fourteen, com.example.R.string.five_fifteen, com.example.R.string.five_sixteen,
                    com.example.R.string.five_seventeen, com.example.R.string.five_eighteen, com.example.R.string.five_nineteen, com.example.R.string.five_twenty,
                    com.example.R.string.five_twenty_one, com.example.R.string.five_twenty_two, com.example.R.string.five_twenty_three, com.example.R.string.five_twenty_four,
                    com.example.R.string.five_twenty_five, com.example.R.string.five_twenty_six, com.example.R.string.five_twenty_seven, com.example.R.string.five_twenty_eight,
                    com.example.R.string.five_twenty_nine},
            {com.example.R.string.six_one, com.example.R.string.six_two, com.example.R.string.six_three, com.example.R.string.six_four},
            {com.example.R.string.seven_one, com.example.R.string.seven_two, com.example.R.string.seven_three, com.example.R.string.seven_four},
            {com.example.R.string.eight_one},
            {com.example.R.string.nine_one, com.example.R.string.nine_two, com.example.R.string.nine_three, com.example.R.string.nine_four, com.example.R.string.nine_five},
            {com.example.R.string.ten_one, com.example.R.string.ten_two, com.example.R.string.ten_three, com.example.R.string.ten_four,
                    com.example.R.string.ten_five, com.example.R.string.ten_six, com.example.R.string.ten_seven, com.example.R.string.ten_eight},
            {com.example.R.string.eleven_one, com.example.R.string.eleven_two, com.example.R.string.eleven_three},
            {com.example.R.string.twelve_one, com.example.R.string.twelve_two, com.example.R.string.twelve_three}};

    public static int getChannelNameRes(int group, int channel) {
        if (channel < sChannelMapping[group-1].length) {
            return sChannelMapping[group-1][channel];
        }
        return 0;
    }
}
