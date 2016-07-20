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
        mModelController.parseMoreDataFromHttp(next);
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

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
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
                    int channelHeight = 0;
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
                        mProgramDurationLayout.addView(channelContentLayout);

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
                                    }
                                }

                                // adjust margin if program list is not continuous
                                if (index < reusedErrorLayoutEndTime.size()
                                    && reusedErrorLayoutEndTime.get(index) < program.getStartDate().getTime()) {

                                    params = (LinearLayout.LayoutParams) textView.getLayoutParams();
                                    params.setMarginStart(Utils.getEmptySliceWidth(
                                            new Date(reusedErrorLayoutEndTime.get(index)), program.getStartDate()));
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