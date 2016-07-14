package tv.hillsbrad.com.tvprogramlist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import tv.hillsbrad.com.Utils;
import tv.hillsbrad.com.yahoo.YahooTvConstant;
import tv.hillsbrad.com.model.Channel;
import tv.hillsbrad.com.model.ChannelGroup;
import tv.hillsbrad.com.model.Program;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static String REFRESH_UI_ACTION = "tv.hillsbrad.intent.action.REFRESH_UI";
    public static String SEARCH_NEXT = "search_next";

    private LinearLayout mChannelTitleLayout;
    private LinearLayout mProgramDurationLayout;
    private LinearLayout mTimeSliceLayout;
    private HorizontalScrollView mProgramScrollView;

    private Spinner mTypeSpinner;

    private ImageButton mPreviousButton;
    private ImageButton mNextButton;
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

        mModelController = ModelController.getInstance();

        mChannelTitleLayout = (LinearLayout) findViewById(R.id.channel_title_layout);
        mProgramDurationLayout = (LinearLayout) findViewById(R.id.program_duration_layout);
        mTimeSliceLayout = (LinearLayout) findViewById(R.id.time_slice_layout);
        mProgramScrollView = (HorizontalScrollView) findViewById(R.id.program_scroll_view);
        mProgramScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        int x = v.getScrollX();
                        int width = v.getWidth();
                        int measureWidth = mProgramScrollView.getChildAt(0).getMeasuredWidth();

                        if (mBasicMeasureViewWidth == -1) {
                            mBasicMeasureViewWidth = mTimeSliceLayout.getChildAt(0).getMeasuredWidth() * 2;
                        }

                        if (x == 0) {
                            searchMore(false);
                        } else if (x + width >= measureWidth) {
                            searchMore(true);
                        }
                        break;
                }
                return false;
            }
        });

        mTypeSpinner = (Spinner) findViewById(R.id.type_spinner);
        initSpinner();

        mPreviousButton = (ImageButton) findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchMore(false);
            }
        });
        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchMore(true);
            }
        });

        searchMore(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mIntentFilter);
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
        ArrayList<String> channels2 = new ArrayList<>();

        String[] channels = new String[YahooTvConstant.CHANNEL_TYPE.length];
        for (int i = 0; i < YahooTvConstant.CHANNEL_TYPE.length; i++) {
            channels[i] = getString(YahooTvConstant.CHANNEL_TYPE[i]);
            channels2.add(getString(YahooTvConstant.CHANNEL_TYPE[i]));
        }

        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(this, channels2);
        mTypeSpinner.setAdapter(customSpinnerAdapter);
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void search(final boolean next) {

        Log.d("alexx", "search(" + next + ")");

//        if (!mTimeController.isSearched()) {
//            new Thread() {
//                public void run() {
                    mModelController.parseMoreDataFromHttp(next);
//                    refreshUI();
//                }
//            }.start();



//                    if (channelGroup != null) {
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//
//                                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
//                                        Utils.getSliceBacisWidth(MainActivity.this), ViewGroup.LayoutParams.WRAP_CONTENT);
//                                int currentHour = mTimeController.getCurrentHourOfDay();
//
//                                mTimeController.resetSearchTime(channelGroup.getSearchingStartDate(), channelGroup.getSearchingEndDate());
//                                Log.d("alexx", "viewStartTime " + mTimeController.getViewStartDate());
//                                Log.d("alexx", "viewEndTime   " + mTimeController.getViewEndDate());
//
//                                TextView textView = new TextView(MainActivity.this);
//                                textView.setText(Utils.toHourOfDayString(currentHour));
//                                textView.setBackgroundColor(Utils.getHourOfDayColor(MainActivity.this, currentHour));
//                                textView.setLayoutParams(params);
//                                if (next) {
//                                    mTimeSliceLayout.addView(textView);
//                                } else {
//                                    mTimeSliceLayout.addView(textView, 0);
//                                }
//
//                                textView = new TextView(MainActivity.this);
//                                textView.setText(Utils.toHourOfDayString(currentHour + 1));
//                                textView.setBackgroundColor(Utils.getHourOfDayColor(MainActivity.this, currentHour + 1));
//                                textView.setLayoutParams(params);
//                                if (next) {
//                                    mTimeSliceLayout.addView(textView);
//                                } else {
//                                    mTimeSliceLayout.addView(textView, 1);
//                                }
//
//                                int channelCount = 0;
//                                for (Channel channel : channelGroup.getChannels().values()) {
//                                    int bg = (channelCount++ % 2 == 0) ? R.color.programBg1 : R.color.programBg2;
//                                    bg = getResources().getColor(bg, null);
//
//                                    LinearLayout sliceLayout = null;
//                                    // channel title
//                                    if (mViewController.containsChannel(channel.getTitle())) {
//                                        sliceLayout = mViewController.getLayout(channel.getTitle());
//                                        Log.d("alexx", "existed: " + channel.getTitle());
//                                    } else {
//                                        textView = new TextView(MainActivity.this);
//                                        textView.setText(channel.getTitle());
//                                        textView.setBackgroundColor(bg);
//                                        mChannelTitleLayout.addView(textView);
//
//                                        sliceLayout = new LinearLayout(MainActivity.this);
//                                        sliceLayout.setOrientation(LinearLayout.HORIZONTAL);
//                                        mProgramDurationLayout.addView(sliceLayout);
//
//                                        Log.d("alexx", "new: " + channel.getTitle());
//                                    }
//
//                                    // channel content
//                                    int count = 0;
//                                    int totalCount = channel.getPrograms().size();
//                                    for (Program program : channel.getPrograms()) {
//
//                                        if (count == 0 && next && sliceLayout.getChildCount() > 0 &&
//                                            program.getStartDate().getTime() < channelGroup.getSearchingStartDate().getTime()) {
//                                            // if need to merge privious item
//                                            textView = (TextView) sliceLayout.getChildAt(sliceLayout.getChildCount()-1);
//                                            params = textView.getLayoutParams();
//                                            params.width = Utils.getRelatedProgramSliceWidth(program,
//                                                    mTimeController.getViewStartDate(),
//                                                    mTimeController.getViewEndDate());
//                                            textView.requestLayout();
//                                            Log.d("alexx", "    next exist");
//                                            Log.d("alexx", "    " + params.width + " / " + program.toString());
//                                        } else if (count == (totalCount -1) && !next && sliceLayout.getChildCount() > count &&
//                                                program.getEndDate().getTime() >
//                                                        (channelGroup.getSearchingStartDate().getTime() + YahooTvConstant.YAHOO_SEARCH_TIME * Utils.MILLISECOND_IN_HOUR)) {
//                                            // if need to merge post item
//                                            textView = (TextView) sliceLayout.getChildAt(count);
//                                            params = textView.getLayoutParams();
//                                            params.width = Utils.getRelatedProgramSliceWidth(program,
//                                                    mTimeController.getViewStartDate(),
//                                                    mTimeController.getViewEndDate());
//                                            textView.requestLayout();
//
//                                            Log.d("alexx", "    previous exist");
//                                            Log.d("alexx", "    " + params.width + " / " + program.toString());
//                                        } else {
//                                            if (count == 0 && next && sliceLayout.getChildCount() > 0 &&
//                                                    program.getStartDate().getTime() > channelGroup.getSearchingStartDate().getTime()) {
//
//                                                // there is bug, adjust view must use correct program object
//                                                textView = (TextView) sliceLayout.getChildAt(sliceLayout.getChildCount()-1);
//                                                Program textViewProgram = (Program) textView.getTag();
//                                                params = textView.getLayoutParams();
//                                                params.width = Utils.getRelatedProgramSliceWidth(textViewProgram,
//                                                        mTimeController.getViewStartDate(),
//                                                        mTimeController.getViewEndDate());
//                                                textView.requestLayout();
//                                                //https://tw.movies.yahoo.com/service/rest/?method=ymv.tv.getList&gid=5&date=2016-06-24&h=18
//                                                //existed: 教育文化頻道
//                                                //120 / 華視新住民新聞-越南語(普) / 10 / Fri Jun 24 19:00:00 GMT+08:00 2016 / Fri Jun 24 19:10:00 GMT+08:00 2016
//                                                //300 / 動感科技-機器人(普) / 25 / Fri Jun 24 19:10:00 GMT+08:00 2016 / Fri Jun 24 19:35:00 GMT+08:00 2016
//                                                //300 / 動感科技-機器人(普) / 25 / Fri Jun 24 19:35:00 GMT+08:00 2016 / Fri Jun 24 20:00:00 GMT+08:00 2016
//                                            }
//
//                                            params = new ViewGroup.LayoutParams(
//                                                    Utils.getRelatedProgramSliceWidth(program,
//                                                            mTimeController.getViewStartDate(),
//                                                            mTimeController.getViewEndDate()),
//                                                    ViewGroup.LayoutParams.WRAP_CONTENT);
//                                            textView = new TextView(MainActivity.this);
//                                            textView.setText(program.getTitle() + "/" + program.getTime());
//                                            textView.setBackground(getResources().getDrawable(R.drawable.program_textview, null));
//                                            textView.setLayoutParams(params);
//                                            textView.setHorizontallyScrolling(true);
//                                            textView.setSingleLine(true);
//                                            textView.setTag(program);
//
//                                            if (next) {
//                                                sliceLayout.addView(textView);
//                                            } else {
//                                                sliceLayout.addView(textView, count);
//                                            }
//
//                                            Log.d("alexx", "    " + params.width + " / " + program.toString());
//                                        }
//                                        count++;
//                                    }
//                                    mViewController.add(channel.getTitle(), sliceLayout);
//                                }
//                                mIsProcessing = false;
//                                mPreviousButton.setEnabled(true);
//                                mNextButton.setEnabled(true);
//                            }
//                        });
//                    }
//                }
//            }.start();
//        } else {
//            Log.d("alexx", "already search / " + mTimeController.getCalendar().getTime());
//            mIsProcessing = false;
//            mPreviousButton.setEnabled(true);
//            mNextButton.setEnabled(true);
//        }
    }

    public void searchMore(boolean next) {
        synchronized (this) {
            if (!mIsProcessing) {
                mIsProcessing = true;
                mPreviousButton.setEnabled(false);
                mNextButton.setEnabled(false);
                mTypeSpinner.setEnabled(false);

                search(next);
            } else {
                Log.d("alexx", "search is processing");
            }
        }
    }

    public void refreshUI(final boolean next) {
        Log.d("alexx", "refreshUI " + next);

        runOnUiThread(new Runnable() {
            public void run() {
                //clear UI
                mChannelTitleLayout.removeAllViews();
                TextView textView = new TextView(MainActivity.this);
                textView.setPadding(10, 10, 10, 10);
                mChannelTitleLayout.addView(textView);
                mProgramDurationLayout.removeAllViews();
                mTimeSliceLayout.removeAllViews();

                ChannelGroup channelGroup = mModelController.getModel();

                if (channelGroup.isReadyToPresent()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(channelGroup.getSearchingStartDate());
                    int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                    long duration = channelGroup.getSearchingEndDate().getTime() -
                            channelGroup.getSearchingStartDate().getTime();
                    long hours = TimeUnit.MILLISECONDS.toHours(duration);

                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                            Utils.getSliceBacisWidth(MainActivity.this), ViewGroup.LayoutParams.WRAP_CONTENT);
                    for (int i = 0; i < hours; i++) {
                        textView = new TextView(MainActivity.this);
                        textView.setText(Utils.toHourOfDayString((currentHour + i) % 24));
                        textView.setBackgroundColor(Utils.getHourOfDayColor(MainActivity.this, (currentHour + i) % 24));
                        textView.setLayoutParams(params);
                        textView.setTextSize(14);
                        textView.setPadding(10, 10, 10, 10);
                        mTimeSliceLayout.addView(textView);
                    }

                    int channelCount = 0;
                    for (Channel channel : channelGroup.getChannels().values()) {
                        /** channel title **/
                        textView = new TextView(MainActivity.this);
                        textView.setText(channel.getTitle());
                        textView.setTextSize(14);
                        textView.setPadding(10, 10, 10, 10);
                        if (channelCount++ % 2 == 0) {
                            textView.setBackgroundColor(getResources().getColor(R.color.colorChannelTitleBg, null));
                        }
                        mChannelTitleLayout.addView(textView);

                        /** channel content **/
                        LinearLayout channelContentLayout = new LinearLayout(MainActivity.this);
                        channelContentLayout.setOrientation(LinearLayout.HORIZONTAL);
                        mProgramDurationLayout.addView(channelContentLayout);

                        // for time of yahoo program model error handle
                        ArrayList<LinearLayout> reusedErrorLayout = new ArrayList<LinearLayout>();
                        ArrayList<Long> reusedLayoutStartTime = new ArrayList<Long>();
                        ArrayList<Long> reusedLayoutEndTime = new ArrayList<Long>();

                        LinearLayout yahooTimeErrorLayout = null;
                        long errorStartTime = 0, errorEndTime = 0;

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

                            // fix Yahoo data is not well-formed
                            // query h=16
                            // 04:30PM~05:00PM xxx
                            // 05:00PM~07:00PM yyy
                            if (firstProgram) {
                                firstProgram = false;
                                if (program.getStartDate().getTime() >
                                        channelGroup.getSearchingStartDate().getTime()) {

                                    params = new ViewGroup.LayoutParams(
                                            Utils.getEmptySliceWidth(channelGroup.getSearchingStartDate(),
                                                    program.getStartDate()),
                                            ViewGroup.LayoutParams.WRAP_CONTENT);

                                    TextView emptyTextView = new TextView(MainActivity.this);
                                    emptyTextView.setBackground(getResources().getDrawable(R.drawable.program_textview, null));
                                    emptyTextView.setLayoutParams(params);
                                    emptyTextView.setPadding(10, 10, 10, 10);
                                    channelContentLayout.addView(emptyTextView);
                                }
                            }

                            if (program.hasYahooTimeProblem()) {
                                textView.setTextColor(Color.RED);

                                if (yahooTimeErrorLayout == null) {
                                    yahooTimeErrorLayout = new LinearLayout(MainActivity.this);
                                    yahooTimeErrorLayout.setOrientation(LinearLayout.VERTICAL);
                                    errorStartTime = 0;
                                    errorEndTime = 0;
                                }

                                if (errorStartTime == 0 || program.getStartDate().getTime() < errorStartTime) {
                                    if (program.getStartDate().getTime() < channelGroup.getSearchingStartDate().getTime()) {
                                        errorStartTime = channelGroup.getSearchingStartDate().getTime();
                                    } else {
                                        errorStartTime = program.getStartDate().getTime();
                                    }
                                }
                                if (errorEndTime == 0 || program.getEndDate().getTime() > errorEndTime) {
                                    errorEndTime = program.getEndDate().getTime();
                                }

                                if (program.getStartDate().getTime() > errorStartTime) {
                                    ((LinearLayout.LayoutParams) textView.getLayoutParams())
                                            .setMarginStart(Utils.getEmptySliceWidth(new Date(errorStartTime),
                                                    program.getStartDate()));
                                    Log.d("alexx", textView.getText() + " setMargin " + Utils.getEmptySliceWidth(new Date(errorStartTime),
                                            program.getStartDate()));
                                }

                                yahooTimeErrorLayout.addView(textView);
                            } else {
                                if (yahooTimeErrorLayout != null) {
                                    channelContentLayout.addView(yahooTimeErrorLayout);
                                    for (int i = 1; i < yahooTimeErrorLayout.getChildCount(); i++) {
                                        TextView emptyView = new TextView(MainActivity.this);
                                        emptyView.setTextSize(14);
                                        emptyView.setPadding(10, 10, 10, 10);
                                        mChannelTitleLayout.addView(emptyView);
                                    }

                                    yahooTimeErrorLayout = null;
                                }
                                channelContentLayout.addView(textView);
                            }
                        }

                        if (yahooTimeErrorLayout != null) {
                            channelContentLayout.addView(yahooTimeErrorLayout);

                            for (int i = 1; i < yahooTimeErrorLayout.getChildCount(); i++) {
                                textView = new TextView(MainActivity.this);
                                textView.setTextSize(14);
                                textView.setPadding(10, 10, 10, 10);
                                mChannelTitleLayout.addView(textView);
                            }
                            yahooTimeErrorLayout = null;
                        }
                    }
                }

                if (!next && mBasicMeasureViewWidth != -1) {
                    mProgramScrollView.setScrollX(mBasicMeasureViewWidth);
                }

                synchronized (MainActivity.this) {
                    mIsProcessing = false;
                }
                mPreviousButton.setEnabled(true);
                mNextButton.setEnabled(true);
                mTypeSpinner.setEnabled(true);
            }
        });
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
        private ArrayList<String> asr;

        public CustomSpinnerAdapter(Context context, ArrayList<String> asr) {
            this.asr = asr;
            activity = context;
        }

        public int getCount() {
            return asr.size();
        }

        public Object getItem(int i) {
            return asr.get(i);
        }

        public long getItemId(int i) {
            return (long)i;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(MainActivity.this);
            txt.setPadding(10, 10, 10, 10);
            txt.setTextSize(16);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(asr.get(position));
            txt.setTextColor(Color.parseColor("#000000"));
            return  txt;
        }

        public View getView(int i, View view, ViewGroup viewgroup) {
            TextView txt = new TextView(MainActivity.this);
            txt.setGravity(Gravity.CENTER);
            txt.setPadding(16, 5, 16, 5);
            txt.setTextSize(16);
            txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
            txt.setText(asr.get(i));
            txt.setTextColor(Color.parseColor("#000000"));
            return  txt;
        }

    }
}