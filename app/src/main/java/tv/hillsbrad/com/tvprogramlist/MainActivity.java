package tv.hillsbrad.com.tvprogramlist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import tv.hillsbrad.com.Utils;
import tv.hillsbrad.com.yahoo.YahooTvConstant;
import tv.hillsbrad.com.model.Channel;
import tv.hillsbrad.com.model.ChannelGroup;
import tv.hillsbrad.com.model.Program;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static String REFRESH_UI_ACTION = "tv.hillsbrad.intent.action.REFRESH_UI";
    public static String SEARCH_NEXT = "search_next";

    private LinearLayout mChannelTitleLayout;
    private LinearLayout mChannelGroupContentLayout;
    private LinearLayout mTimeSliceLayout;
    private HorizontalScrollView mProgramScrollView;
    private FrameLayout mTimeIndicatorFrameLayout;

    private Spinner mChannelGroupTypeSpinner;
    private Spinner mDaySpinner;
    private TextView mTimeIndicatorTextView;

    private boolean mIsProcessing;

    // for test
    private Button mClearButton;


    private ModelController mModelController;

    private int mBasicMeasureViewWidth = -1;

    public class UIRefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean next = intent.getBooleanExtra(SEARCH_NEXT, true);
            refreshUI(next);
        }
    }

    private final UIRefreshReceiver mReceiver = new UIRefreshReceiver();

    private IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.content_main);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


        // for test
        mClearButton = (Button) findViewById(R.id.clear_model_data_button);
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YahooTvConstant.Group current = mModelController.getCurrentGroup();
                mModelController.clear();
                mModelController.setCurrentGroup(current);
                searchMore(true);            }
        });

        mIntentFilter = new IntentFilter(REFRESH_UI_ACTION);

        mModelController = ModelController.getInstance(this);

        mChannelTitleLayout = (LinearLayout) findViewById(R.id.channel_title_layout);
        mChannelGroupContentLayout = (LinearLayout) findViewById(R.id.channel_group_content_layout);
        mTimeSliceLayout = (LinearLayout) findViewById(R.id.time_slice_layout);
        mProgramScrollView = (HorizontalScrollView) findViewById(R.id.program_scroll_view);

        mProgramScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int x = mProgramScrollView.getScrollX();

                if (!mIsProcessing) {
                    if (x > (mTimeSliceLayout.getMeasuredWidth() * 0.9)) {
                        Log.d("alexx", x + " > " + mTimeSliceLayout.getMeasuredWidth() * 0.9);
                        searchMore(true);
                    } else if (x < (mTimeSliceLayout.getMeasuredWidth() * 0.1)) {
                        Log.d("alexx", x + " < " + mTimeSliceLayout.getMeasuredWidth() * 0.1);
                        searchMore(false);
                    }
                }
            }
        });

        mProgramScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("alexx", "ACTION_DWON");
                        if (mBasicMeasureViewWidth == -1) {
                            mBasicMeasureViewWidth = mTimeSliceLayout.getChildAt(0).getMeasuredWidth() * 2;
                        }
                        break;
//                    case MotionEvent.ACTION_UP:
//                        if (mBasicMeasureViewWidth == -1) {
//                            mBasicMeasureViewWidth = mTimeSliceLayout.getChildAt(0).getMeasuredWidth() * 2;
//                        }
//
//                        Log.d("alexx", "ACTION_UP: " + v.getScrollX() + ", " + v.getScrollY());
//
//                        int x = v.getScrollX();
//                        int width = v.getWidth();
//                        int measureWidth = mProgramScrollView.getChildAt(0).getMeasuredWidth();
//                        if (x == 0) {
//                            Log.d("alexx", "search backward");
//                            searchMore(false);
//                        } else if (x + width >= measureWidth) {
//                            Log.d("alexx", "search forward");
//                            searchMore(true);
//                        }
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        Log.d("alexx", "current location: " + v.getScrollX() + ", " + v.getScrollY());
//                        break;
                }
                return false;
            }
        });
        mTimeIndicatorFrameLayout = (FrameLayout) findViewById(R.id.time_indicator_framelayout);
        mTimeIndicatorTextView = (TextView) findViewById(R.id.time_indicator_textview);

        mChannelGroupTypeSpinner = (Spinner) findViewById(R.id.channel_group_type_spinner);
        mDaySpinner = (Spinner) findViewById(R.id.day_spinner);
        initSpinner();
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mIntentFilter);
        searchMore(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    public void onDestroy() {
        mModelController.clear();
        super.onDestroy();
    }

    public void initSpinner() {
        // channel group
        ArrayList<String> channels = new ArrayList<>();
        for (int i = 0; i < YahooTvConstant.CHANNEL_TYPE.length; i++) {
            channels.add(getString(YahooTvConstant.CHANNEL_TYPE[i]));
        }

        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(this, channels);
        mChannelGroupTypeSpinner.setAdapter(customSpinnerAdapter);
        mChannelGroupTypeSpinner.setSelection(mModelController.getCurrentGroup().getValue() - 1, true);
        mChannelGroupTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!mIsProcessing) {
                    mModelController.setCurrentGroup(YahooTvConstant.Group.convertToGroup(position + 1));

                    if (!mModelController.getModel().isReadyToPresent()) {
                        searchMore(true);
                    } else {
                        refreshUI(true);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // day
        ArrayList<String> days = new ArrayList<>();
        ArrayList<String> daysOfWeek = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        GregorianCalendar yahooLimitation = new GregorianCalendar(calendar.get(Calendar.YEAR),
                                                                  calendar.get(Calendar.MONTH),
                                                                  calendar.get(Calendar.DAY_OF_MONTH));
        Date limitationStart = yahooLimitation.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
        for (int i = 0; i < 6; i++) {
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            days.add(sdf.format(calendar.getTime()));
            daysOfWeek.add(Utils.convertDayOfWeek(day));
            calendar.add(Calendar.DATE, 1);
        }

        yahooLimitation.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        yahooLimitation.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        yahooLimitation.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
        mModelController.setYahooLimitationDate(limitationStart, yahooLimitation.getTime());

        CustomSpinnerAdapter customSpinnerAdapter2 = new CustomSpinnerAdapter(this, days, daysOfWeek);
        mDaySpinner.setAdapter(customSpinnerAdapter2);
        mDaySpinner.setSelection(1, true);
        mDaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view.getTag() != null) {
                    String[] date = view.getTag().toString().split("/");
                    mModelController.setSearchDate(Integer.valueOf(date[0]), Integer.valueOf(date[1]));
                    searchMore(true);
                } else {
                    Log.d("alexx", "tag is null");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void search(final boolean next) {
        boolean querySuccess = mModelController.parseMoreDataFromHttp(next);
        if (!querySuccess) {
            Log.d("alexx", "out of range");

            lockUI(false);
        }
    }

    public void searchMore(boolean next) {
        synchronized (this) {
            if (!mIsProcessing) {
                lockUI(true);
                search(next);
            } else {
                // FIXME need to add progress bar
                Log.d(Utils.TAG, "search is processing");
            }
        }
    }

    public void refreshUI(final boolean next) {
        runOnUiThread(new Runnable() {
            public void run() {
                //clear UI
                mChannelTitleLayout.removeAllViews();
                TextView textView = new TextView(MainActivity.this);
                textView.setPadding(10, 10, 10, 10);
                mChannelTitleLayout.addView(textView);
                mChannelGroupContentLayout.removeAllViews();
                mTimeSliceLayout.removeAllViews();

                ChannelGroup channelGroup = mModelController.getModel();

                if (channelGroup.isReadyToPresent()) {
                    Calendar calendar = Calendar.getInstance();

                    /** time mark **/
                    calendar.setTime(channelGroup.getSearchingStartDate());
                    int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                    long duration = channelGroup.getSearchingEndDate().getTime() -
                            channelGroup.getSearchingStartDate().getTime();
                    long hours = TimeUnit.MILLISECONDS.toHours(duration);
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            Utils.getSliceBacisWidth(MainActivity.this), ViewGroup.LayoutParams.WRAP_CONTENT);
                    for (int i = 0; i < hours; i++) {
                        textView = new TextView(MainActivity.this);
                        textView.setText(sdf.format(calendar.getTime()));
                        textView.setTypeface(null, Typeface.BOLD);
                        textView.setBackgroundColor(Utils.getHourOfDayColor(MainActivity.this, (currentHour + i) % 24));
                        textView.setLayoutParams(params);
                        textView.setTextSize(14);
                        textView.setPadding(10, 10, 10, 10);
                        mTimeSliceLayout.addView(textView);

                        calendar.add(Calendar.HOUR_OF_DAY, 1);
                    }

                    int channelCount = 0;
                    int channelHeight = 0;
                    Date currentTime = Calendar.getInstance().getTime();
                    for (Channel channel : channelGroup.getChannels().values()) {
                        channelCount++;
                        channelHeight = 1;

                        /** channel title **/
                        textView = new TextView(MainActivity.this);
                        textView.setText(channel.getTitle());
                        textView.setTextSize(14);
                        textView.setPadding(10, 10, 10, 10);
                        if (channelCount % 2 == 1) {
                            textView.setBackgroundColor(getResources().getColor(R.color.colorChannelTitleBg, null));
                        }
                        mChannelTitleLayout.addView(textView);

                        /** channel content **/
                        LinearLayout channelContentLayout = new LinearLayout(MainActivity.this);
                        channelContentLayout.setOrientation(LinearLayout.HORIZONTAL);
                        mChannelGroupContentLayout.addView(channelContentLayout);

                        // handle time error of yahoo program model
                        LinearLayout yahooErrorParentLayout = null;
                        ArrayList<LinearLayout> reusedYahooErrorLayout = new ArrayList<>();
                        ArrayList<Long> reusedErrorLayoutEndTime = new ArrayList<>();
                        Long currentNormalBlockEndTime = 0L;

                        boolean firstProgram = true;
                        for (Program program : channel.getPrograms()) {
                            params = new LinearLayout.LayoutParams(
                                    Utils.getRelatedProgramSliceWidth(program,
                                            channelGroup.getSearchingStartDate(),
                                            channelGroup.getSearchingEndDate()),
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            textView = new TextView(MainActivity.this);
                            textView.setText(program.getTitle() + "/" + program.getTime());
                            textView.setBackground(getResources().getDrawable(R.drawable.program_textview, null));
                            textView.setLayoutParams(params);
                            textView.setHorizontallyScrolling(true);
                            textView.setSingleLine(true);
                            textView.setTextSize(14);
                            textView.setPadding(10, 10, 10, 10);
                            if (currentTime.getTime() >= program.getStartDate().getTime() &&
                                    currentTime.getTime() < program.getEndDate().getTime()) {
                                textView.setTextColor(Color.BLUE);
                            }

                            // fix Yahoo data is not well-formed
                            // query h=16, no data during 04:00~04:30
                            // 04:30PM~05:00PM xxx
                            // 05:00PM~07:00PM yyy
                            if (firstProgram) {
                                firstProgram = false;
                                if (program.getStartDate().getTime() >
                                        channelGroup.getSearchingStartDate().getTime()) {
                                    params = (LinearLayout.LayoutParams) textView.getLayoutParams();
                                    params.setMarginStart(Utils.getEmptySliceWidth(
                                            channelGroup.getSearchingStartDate(), program.getStartDate()));
                                    textView.setLayoutParams(params);
                                }
                            }

                            if (program.hasYahooTimeProblem()) {
                                textView.setTextColor(Color.RED);

                                if (yahooErrorParentLayout == null) {
                                    yahooErrorParentLayout = new LinearLayout(MainActivity.this);
                                    yahooErrorParentLayout.setOrientation(LinearLayout.VERTICAL);
                                    yahooErrorParentLayout.setBackground(getResources().getDrawable(R.drawable.error_program_textview_layout, null));
                                    reusedYahooErrorLayout.clear();
                                    reusedErrorLayoutEndTime.clear();
                                    channelContentLayout.addView(yahooErrorParentLayout);
                                }

                                LinearLayout errorInRowLayout = null;
                                int index;
                                // search suitable layout to add view
                                for (index = 0; index < reusedYahooErrorLayout.size(); index++) {
                                    if (reusedErrorLayoutEndTime.get(index) <= program.getStartDate().getTime()) {
                                        errorInRowLayout = reusedYahooErrorLayout.get(index);
                                        break;
                                    }
                                }

                                if (errorInRowLayout == null) {
                                    errorInRowLayout = new LinearLayout(MainActivity.this);
                                    errorInRowLayout.setOrientation(LinearLayout.HORIZONTAL);
                                    yahooErrorParentLayout.addView(errorInRowLayout);
                                    reusedYahooErrorLayout.add(errorInRowLayout);

                                    // add channel title height if there are multiple error linearLayout
                                    if (channelHeight < reusedYahooErrorLayout.size()) {
                                        for (int i = channelHeight; i < reusedYahooErrorLayout.size(); i++) {
                                            TextView emptyChannelTitleTextView = new TextView(MainActivity.this);
                                            emptyChannelTitleTextView.setPadding(10, 10, 10, 10);
                                            if (channelCount % 2 == 1) {
                                                emptyChannelTitleTextView.setBackgroundColor(getResources().getColor(R.color.colorChannelTitleBg, null));
                                            }
                                            mChannelTitleLayout.addView(emptyChannelTitleTextView);
                                        }

                                        channelHeight = reusedYahooErrorLayout.size();
                                    }

                                    // adjust margin if program list is not continuous for first initial
                                    if (program.getStartDate().getTime() > currentNormalBlockEndTime
                                            && program.getStartDate().getTime() > channelGroup.getSearchingStartDate().getTime()) {
                                        if (currentNormalBlockEndTime == 0L) {
                                            currentNormalBlockEndTime = channelGroup.getSearchingStartDate().getTime();
                                        }

                                        params = (LinearLayout.LayoutParams) textView.getLayoutParams();
                                        params.setMarginStart(Utils.getEmptySliceWidth(
                                                new Date(currentNormalBlockEndTime), program.getStartDate()));
                                        textView.setLayoutParams(params);
                                    }
                                }

                                // adjust margin if program list is not continuous
                                if (index < reusedErrorLayoutEndTime.size() &&
                                        reusedErrorLayoutEndTime.get(index) < program.getStartDate().getTime()) {

                                    params = (LinearLayout.LayoutParams) textView.getLayoutParams();
                                    params.setMarginStart(Utils.getEmptySliceWidth(
                                            new Date(reusedErrorLayoutEndTime.get(index)), program.getStartDate()));
                                    textView.setLayoutParams(params);
                                }

                                errorInRowLayout.addView(textView);
                                if (index < reusedErrorLayoutEndTime.size()) {
                                    reusedErrorLayoutEndTime.set(index, program.getEndDate().getTime());
                                } else {
                                    reusedErrorLayoutEndTime.add(program.getEndDate().getTime());
                                }
                            } else {
                                // normal program time, just add it
                                channelContentLayout.addView(textView);
                                currentNormalBlockEndTime = program.getEndDate().getTime();
                                yahooErrorParentLayout = null;
                            }
                        }
                    }

                    calendar = Calendar.getInstance();
                    Date current = calendar.getTime();
                    if (current.getTime() >= channelGroup.getSearchingStartDate().getTime() &&
                            current.getTime() <= channelGroup.getSearchingEndDate().getTime()) {
                        /** time indicator (vertical divider) **/
                        // FIXME the added view can be drawn with frame layout have at least 3 child
                        if (mTimeIndicatorFrameLayout.getChildCount() > 2) {
                            mTimeIndicatorFrameLayout.removeViewAt(0);
                        }

                        int marginStart = Utils.getEmptySliceWidth(
                                channelGroup.getSearchingStartDate(), current);
                        View view = new View(MainActivity.this);
                        FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(
                                5, ViewGroup.LayoutParams.MATCH_PARENT);
                        param.setMarginStart(marginStart);
                        view.setLayoutParams(param);
                        view.setBackgroundColor(Color.rgb(0x66, 0x66, 0x66));
                        mTimeIndicatorFrameLayout.addView(view, 0);

                        /** time indicator (current time mark) **/
                        LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) mTimeIndicatorTextView.getLayoutParams();
                        params2.setMarginStart(0);
                        mTimeIndicatorTextView.setVisibility(View.VISIBLE);
                        mTimeIndicatorTextView.setLayoutParams(params2);
                        mTimeIndicatorTextView.setTag(marginStart);
                        ViewTreeObserver vto2 = mTimeIndicatorTextView.getViewTreeObserver();
                        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                mTimeIndicatorTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                int marginStart = (int) mTimeIndicatorTextView.getTag() - (mTimeIndicatorTextView.getMeasuredWidth() / 2);
                                if (marginStart < 0) {
                                    marginStart = 0;
                                } else if ((marginStart + mTimeIndicatorTextView.getMeasuredWidth())
                                        > mTimeSliceLayout.getMeasuredWidth()) {
                                    marginStart = mTimeSliceLayout.getMeasuredWidth() - mTimeIndicatorTextView.getMeasuredWidth();
                                }
                                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mTimeIndicatorTextView.getLayoutParams();
                                params.setMarginStart(marginStart);
                                mTimeIndicatorTextView.setLayoutParams(params);
                            }
                        });
                    } else {
                        // out of range
                        if (mTimeIndicatorFrameLayout.getChildCount() > 2) {
                            mTimeIndicatorFrameLayout.removeViewAt(0);
                        }
                        mTimeIndicatorTextView.setVisibility(View.GONE);
                    }
                }

                if (!next && mBasicMeasureViewWidth != -1) {
                    mProgramScrollView.setScrollX(mBasicMeasureViewWidth);
                }

                lockUI(false);
            }
        });
    }

    private synchronized void lockUI(boolean enable) {
        mIsProcessing = enable;
        mChannelGroupTypeSpinner.setEnabled(!enable);
        mDaySpinner.setEnabled(!enable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_custom_channel_settings) {
            CustomChannelSettings channelSettings = new CustomChannelSettings();

            channelSettings.show(getSupportFragmentManager(),
                    CustomChannelSettings.class.getSimpleName());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

        private final Context activity;
        private ArrayList<String> mFirstTexts;
        private ArrayList<String> mSecondTexts;

        public CustomSpinnerAdapter(Context context, ArrayList<String> firstTexts) {
            mFirstTexts = firstTexts;
            activity = context;
        }

        public CustomSpinnerAdapter(Context context, ArrayList<String> firstTexts, ArrayList<String> secondTexts) {
            this(context, firstTexts);
            mSecondTexts = secondTexts;
        }

        public int getCount() {
            return mFirstTexts.size();
        }

        public Object getItem(int i) {
            return mFirstTexts.get(i);
        }

        public long getItemId(int i) {
            return (long)i;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextView(MainActivity.this);
                convertView.setPadding(0, 10, 0, 10);
                ((TextView) convertView).setTextSize(16);
                ((TextView) convertView).setGravity(Gravity.CENTER);
                ((TextView) convertView).setTextColor(Color.BLACK);
            }
            if (mSecondTexts == null) {
                ((TextView) convertView).setText(mFirstTexts.get(position));
            } else {
                ((TextView) convertView).setText(mFirstTexts.get(position) + mSecondTexts.get(position));
                convertView.setTag(mFirstTexts.get(position));
            }

            return convertView;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextView(MainActivity.this);
                convertView.setPadding(15, 5, 0, 5);
                ((TextView) convertView).setTextSize(16);
                ((TextView) convertView).setGravity(Gravity.CENTER);
                ((TextView) convertView).setTextColor(Color.BLACK);
                ((TextView) convertView).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
            }
            if (mSecondTexts == null) {
                ((TextView) convertView).setText(mFirstTexts.get(position));
            } else {
                ((TextView) convertView).setText(mFirstTexts.get(position) + " " + mSecondTexts.get(position));
                convertView.setTag(mFirstTexts.get(position));
            }

            return convertView;
        }
    }
}