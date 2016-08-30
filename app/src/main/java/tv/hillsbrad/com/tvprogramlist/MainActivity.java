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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements ModelController.ChannelContentChangedListener {

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
    private Button mVideoButton;

    private ModelController mModelController;

    private ViewController mViewController;

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
                mViewController.clear();
                searchMore(true, false);
            }
        });

        // for test
        mVideoButton = (Button) findViewById(R.id.launch_video_view_button);
        mVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, VideoActivity.class);
                startActivity(intent);
            }
        });

        mIntentFilter = new IntentFilter(REFRESH_UI_ACTION);

        mModelController = ModelController.getInstance(this);
        mViewController = new ViewController(this);

        mChannelTitleLayout = (LinearLayout) findViewById(R.id.channel_title_layout);
        mChannelGroupContentLayout = (LinearLayout) findViewById(R.id.channel_group_content_layout);
        mTimeSliceLayout = (LinearLayout) findViewById(R.id.time_slice_layout);
        mProgramScrollView = (HorizontalScrollView) findViewById(R.id.program_scroll_view);
//
//        mProgramScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//            @Override
//            public void onScrollChanged() {
//                int x = mProgramScrollView.getScrollX();
//
//                Log.d("alexx", "" + x);
//
//                if (!mIsProcessing) {
//                    if (x > (mTimeSliceLayout.getMeasuredWidth() * 0.9)) {
//                        Log.d("alexx", x + " > " + mTimeSliceLayout.getMeasuredWidth() * 0.9);
//                        searchMore(true);
//                    } else if (x < (mTimeSliceLayout.getMeasuredWidth() * 0.1)) {
//                        Log.d("alexx", x + " < " + mTimeSliceLayout.getMeasuredWidth() * 0.1);
//                        searchMore(false);
//                    }
//                }
//            }
//        });

        mProgramScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        Log.d("alexx", "ACTION_DWON");
//                        if (mBasicMeasureViewWidth == -1) {
//                            mBasicMeasureViewWidth = mTimeSliceLayout.getChildAt(0).getMeasuredWidth() * 2;
//                        }
//                        break;
                    case MotionEvent.ACTION_UP:
                        if (mBasicMeasureViewWidth == -1) {
                            mBasicMeasureViewWidth = mTimeSliceLayout.getChildAt(0).getMeasuredWidth() * 2;
                        }

//                        Log.d("alexx", "ACTION_UP: " + v.getScrollX() + ", " + v.getScrollY());

                        int x = v.getScrollX();
                        int width = v.getWidth();
                        int measureWidth = mProgramScrollView.getChildAt(0).getMeasuredWidth();
                        if (x == 0) {
                            searchMore(false, true);
                        } else if (x + width >= measureWidth) {
                            searchMore(true, true);
                        }
                        break;
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

        mModelController.addContentChangedListener(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mIntentFilter);
        searchMore(true, false);

    }

    @Override
    public void onPause() {
        super.onPause();

        mModelController.removeContentChangedListener(this);
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
                        searchMore(true, false);
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
                    searchMore(true, false);
                } else {
                    Log.d("alexx", "tag is null");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void search(final boolean next, boolean isFromSlide) {
        boolean querySuccess = mModelController.parseMoreDataFromHttp(next, isFromSlide);
        if (!querySuccess) {
            Log.d("alexx", "out of range");
        }
        lockUI(false);
    }

    public void searchMore(boolean next, boolean isFromSlide) {
        synchronized (this) {
            if (!mIsProcessing) {
                lockUI(true);
                search(next, isFromSlide);
            } else {
                // FIXME need to add progress bar
                Log.d(Utils.TAG, "search is processing");
            }
        }
    }

    @Override
    public void onUpdate(final ChannelGroup channelGroup, final boolean forward) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /** time mark **/
                addTimeMark(channelGroup, forward);

                Date currentTime = Calendar.getInstance().getTime();

                for (Channel channel : channelGroup.getChannels().values()) {
                    /** channel title **/
                    if (!mViewController.containsChannel(channel.getTitle())) {
                        addChannelTitle(channel.getTitle());
                    }

                    /** channel content **/
                    LinearLayout channelContentLayout = mViewController.getChannelLayout(channel.getTitle());
                    if (channelContentLayout == null) {
                        channelContentLayout = new LinearLayout(MainActivity.this);
                        channelContentLayout.setOrientation(LinearLayout.HORIZONTAL);
                        mChannelGroupContentLayout.addView(channelContentLayout);
                        mViewController.addChannelLayout(channel.getTitle(), channelContentLayout);
                    }

                    mViewController.setCurrentChannel(channel.getTitle());
                    if (!forward) Collections.reverse(channel.getPrograms());

                    for (Program program : channel.getPrograms()) {

                        View neighborView = null;
                        if (channelContentLayout.getChildCount() != 0) {
                            if (forward) {
                                neighborView = channelContentLayout.getChildAt(channelContentLayout.getChildCount() - 1);
                            } else {
                                neighborView = channelContentLayout.getChildAt(0);
                            }
                        }

                        boolean lastProgram = channel.getPrograms().indexOf(program) == channel.getPrograms().size() - 1;

                        View view = createView(program, forward, neighborView, currentTime, lastProgram);
                        if (view != null) {
                            if (forward) {
                                channelContentLayout.addView(view);
                            } else {
                                channelContentLayout.addView(view, 0);
                            }
                        }
                    }

                }

                if (!forward && mBasicMeasureViewWidth != -1) {
                    mProgramScrollView.setScrollX(mBasicMeasureViewWidth * 2);
                }
                lockUI(false);
            }
        });
    }

    public View createView(Program program, boolean forward, View neighborView, Date currentTime, boolean lastProgram) {
        View view = null;

        if (neighborView != null && neighborView instanceof TextView) {
            TextView neighborTextView = (TextView) neighborView;

            Log.d(Utils.TMP_TAG, "" + neighborTextView.getTag(R.id.ui_showed_level) + "/" +program.equals(neighborTextView.getTag(R.id.program_data)));

            if ((int) neighborTextView.getTag(R.id.ui_showed_level) == ViewController.PROGRAM_UI_PARTIAL_HIDE &&
                    program.equals(neighborTextView.getTag(R.id.program_data))) {
                if (!program.hasYahooTimeProblem()) {
                    // update existed program view
                    // resize width
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) neighborTextView.getLayoutParams();
                    params.width = Utils.getRelatedProgramSliceWidth(program,
                            mModelController.getModel().getSearchingStartDate(),
                            mModelController.getModel().getSearchingEndDate());
                    // reset if view is total showed
                    if (program.getStartDate().getTime() >=
                            mModelController.getModel().getSearchingStartDate().getTime() &&
                            program.getEndDate().getTime() <=
                                    mModelController.getModel().getSearchingEndDate().getTime()) {
                        neighborTextView.setTag(R.id.ui_showed_level, ViewController.PROGRAM_UI_TOTAL_SHOWED);
                    }
                    // add margin of leftmost program in backward mode
                    if (lastProgram && !forward) {
                        if (program.getStartDate().getTime() > mModelController.getModel().getSearchingStartDate().getTime()) {
                            params.setMarginStart(Utils.getEmptySliceWidth(
                                    mModelController.getModel().getSearchingStartDate(), program.getStartDate()));
                        }
                    }

                    neighborTextView.setLayoutParams(params);
                } else {
                    // program existed but status change from normal to error program
                    TextView textView = createProgramTextView(program,
                            mModelController.getModel().getSearchingStartDate(),
                            mModelController.getModel().getSearchingEndDate(),
                            currentTime);

                    LinearLayout yahooErrorParentLayout = new LinearLayout(MainActivity.this);
                    yahooErrorParentLayout.setOrientation(LinearLayout.VERTICAL);
                    yahooErrorParentLayout.setBackgroundResource(R.drawable.error_program_textview_layout);
                    if (forward) {
                        yahooErrorParentLayout.setTag(R.id.layout_start_date, program.getStartDate());
                    } else {
                        if (program.getStartDate().getTime() < mModelController.getModel().getSearchingStartDate().getTime()) {

                            yahooErrorParentLayout.setTag(R.id.layout_start_date, mModelController.getModel().getSearchingStartDate());

                            Log.d(Utils.TMP_TAG, "setTag A " + yahooErrorParentLayout.getTag(R.id.layout_start_date));
                        } else {
                            yahooErrorParentLayout.setTag(R.id.layout_start_date, program.getStartDate());

                            Log.d(Utils.TMP_TAG, "setTag B " + yahooErrorParentLayout.getTag(R.id.layout_start_date));
                        }
                    }
                    ArrayList<LinearLayout> reusedYahooErrorLayout = new ArrayList<>();
                    ArrayList<Long> reusedErrorLayoutEdgeTime = new ArrayList<>();
                    mViewController.putReusederrorLayout(forward, reusedYahooErrorLayout);
                    mViewController.putReusedErrorLayoutEdgeTime(forward, reusedErrorLayoutEdgeTime);
                    mViewController.putYahooErrorParentLayout(forward, yahooErrorParentLayout);
                    view = yahooErrorParentLayout;

                    LinearLayout errorInRowLayout = new LinearLayout(MainActivity.this);
                    errorInRowLayout.setOrientation(LinearLayout.HORIZONTAL);
                    yahooErrorParentLayout.addView(errorInRowLayout);
                    reusedYahooErrorLayout.add(errorInRowLayout);
                    if (forward) {
                        reusedErrorLayoutEdgeTime.add(program.getEndDate().getTime());
                        errorInRowLayout.addView(textView);
                    } else {
                        reusedErrorLayoutEdgeTime.add(program.getStartDate().getTime());
                        errorInRowLayout.addView(textView, 0);
                    }


                    // FIXME need to test change margin of TextView or LinearLayout
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
                    params.setMarginStart(((LinearLayout.LayoutParams) neighborTextView.getLayoutParams()).getMarginStart());
                    textView.setLayoutParams(params );

                    ((ViewGroup) neighborTextView.getParent()).removeView(neighborTextView);
                }
            } else {
                if (!program.hasYahooTimeProblem()) {
                    // add new
                    TextView textView = createProgramTextView(program,
                            mModelController.getModel().getSearchingStartDate(),
                            mModelController.getModel().getSearchingEndDate(),
                            currentTime);

                    if (!forward) {
                        // need to remove margin
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) neighborTextView.getLayoutParams();
                        params.setMarginStart(0);
                        neighborTextView.setLayoutParams(params);

                        // add margin of leftmost program in backward mode
                        if (lastProgram) {
                            if (program.getStartDate().getTime() > mModelController.getModel().getSearchingStartDate().getTime()) {
                                params = (LinearLayout.LayoutParams) textView.getLayoutParams();
                                params.setMarginStart(Utils.getEmptySliceWidth(
                                        mModelController.getModel().getSearchingStartDate(), program.getStartDate()));
                                textView.setLayoutParams(params);
                            }
                        }
                    }

                    // add margin of leftmost program in backward mode
                    if (lastProgram && !forward) {
                        if (program.getStartDate().getTime() > mModelController.getModel().getSearchingStartDate().getTime()) {
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
                            params.setMarginStart(Utils.getEmptySliceWidth(
                                    mModelController.getModel().getSearchingStartDate(), program.getStartDate()));
                            textView.setLayoutParams(params);
                        }
                    }

                    view = textView;
                } else {
                    TextView textView = createProgramTextView(program,
                            mModelController.getModel().getSearchingStartDate(),
                            mModelController.getModel().getSearchingEndDate(),
                            currentTime);

                    LinearLayout yahooErrorParentLayout = new LinearLayout(MainActivity.this);
                    yahooErrorParentLayout.setOrientation(LinearLayout.VERTICAL);
                    yahooErrorParentLayout.setBackgroundResource(R.drawable.error_program_textview_layout);
                    yahooErrorParentLayout.setTag(R.id.layout_start_date, program.getStartDate());
                    ArrayList<LinearLayout> reusedYahooErrorLayout = new ArrayList<>();
                    ArrayList<Long> reusedErrorLayoutEdgeTime = new ArrayList<>();
                    mViewController.putReusederrorLayout(forward, reusedYahooErrorLayout);
                    mViewController.putReusedErrorLayoutEdgeTime(forward, reusedErrorLayoutEdgeTime);
                    mViewController.putYahooErrorParentLayout(forward, yahooErrorParentLayout);
                    view = yahooErrorParentLayout;

                    LinearLayout errorInRowLayout = new LinearLayout(MainActivity.this);
                    errorInRowLayout.setOrientation(LinearLayout.HORIZONTAL);
                    yahooErrorParentLayout.addView(errorInRowLayout);
                    reusedYahooErrorLayout.add(errorInRowLayout);
                    if (forward) {
                        reusedErrorLayoutEdgeTime.add(program.getEndDate().getTime());
                        errorInRowLayout.addView(textView);
                    } else {
                        reusedErrorLayoutEdgeTime.add(program.getStartDate().getTime());
                        errorInRowLayout.addView(textView, 0);
                    }


                    // FIXME need to test change margin of TextView or LinearLayout
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
                    params.setMarginStart(((LinearLayout.LayoutParams) neighborTextView.getLayoutParams()).getMarginStart());
                    textView.setLayoutParams(params );
                }
            }
        } else if (neighborView != null && neighborView instanceof LinearLayout) {
            LinearLayout neighborLayout = (LinearLayout) neighborView;
            if (!program.hasYahooTimeProblem()) {
                TextView textView = createProgramTextView(program,
                        mModelController.getModel().getSearchingStartDate(),
                        mModelController.getModel().getSearchingEndDate(),
                        currentTime);

                // add margin of leftmost program in backward mode
                if (lastProgram && !forward) {
                    if (program.getStartDate().getTime() > mModelController.getModel().getSearchingStartDate().getTime()) {
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
                        params.setMarginStart(Utils.getEmptySliceWidth(
                                mModelController.getModel().getSearchingStartDate(), program.getStartDate()));
                        textView.setLayoutParams(params);
                    }
                }

                mViewController.removeYahooErrorParentLayout(forward);

                view = textView;
            } else {
                LinearLayout yahooErrorParentLayout = mViewController.getYahooErrorParentLayout(forward);
                ArrayList<LinearLayout> reusedYahooErrorLayout = mViewController.getReusedErrorLayout(forward);
                ArrayList<Long> reusedErrorLayoutEdgeTime = mViewController.getReusedErrorLayoutEdgeTime(forward);


                Log.d(Utils.TMP_TAG, "yahoo error " + program.toString());


                TextView existTextView = null;
                // check if program existed
                for (int i = 0; i < yahooErrorParentLayout.getChildCount(); i++) {
                    LinearLayout childLayout;
                    if (forward) {
                        childLayout =(LinearLayout) yahooErrorParentLayout.getChildAt(i);
                    } else {
                        childLayout =(LinearLayout) yahooErrorParentLayout.getChildAt(yahooErrorParentLayout.getChildCount() - i - 1);
                    }

                    if (childLayout.getChildCount() > 0) {
                        TextView textView;
                        if (forward) {
                            textView = (TextView) childLayout.getChildAt(childLayout.getChildCount() - 1);
                        } else {
                            textView = (TextView) childLayout.getChildAt(0);
                        }
                        if (program.equals(textView.getTag(R.id.program_data))) {
                            existTextView = textView;
                            Log.d("alexx", "existed view " + existTextView.getText());
                            break;
                        }
                    }
                }

                if (existTextView != null) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) existTextView.getLayoutParams();
                    params.width = Utils.getRelatedProgramSliceWidth(program,
                            mModelController.getModel().getSearchingStartDate(),
                            mModelController.getModel().getSearchingEndDate());
                    existTextView.setLayoutParams(params);

                    if (program.getStartDate().getTime() > mModelController.getModel().getSearchingStartDate().getTime()) {
                        if (program.getStartDate().getTime() < ((Date) yahooErrorParentLayout.getTag(R.id.layout_start_date)).getTime()) {
                            yahooErrorParentLayout.setTag(R.id.layout_start_date, program.getStartDate());

                            Log.d(Utils.TMP_TAG, "setTag C " + yahooErrorParentLayout.getTag(R.id.layout_start_date));
                        }
                    } else {
                        if (mModelController.getModel().getSearchingStartDate().getTime() <
                                ((Date) yahooErrorParentLayout.getTag(R.id.layout_start_date)).getTime()) {
                            yahooErrorParentLayout.setTag(R.id.layout_start_date, mModelController.getModel().getSearchingStartDate());

                            Log.d(Utils.TMP_TAG, "setTag D " + yahooErrorParentLayout.getTag(R.id.layout_start_date));
                        }
                    }
                } else {
                    TextView textView = createProgramTextView(program,
                            mModelController.getModel().getSearchingStartDate(),
                            mModelController.getModel().getSearchingEndDate(),
                            currentTime);

                    LinearLayout errorInRowLayout = null;
                    int index;
                    // search suitable layout to add view
                    for (index = 0; index < reusedYahooErrorLayout.size(); index++) {
                        if (forward) {
                            if (reusedErrorLayoutEdgeTime.get(index) <= program.getStartDate().getTime()) {
                                errorInRowLayout = reusedYahooErrorLayout.get(index);
                                break;
                            }
                        } else {
                            int backwardIndex = reusedYahooErrorLayout.size() - index - 1;
                            if (reusedErrorLayoutEdgeTime.get(backwardIndex) >= program.getEndDate().getTime()) {
                                errorInRowLayout = reusedYahooErrorLayout.get(backwardIndex);


                                Log.d(Utils.TMP_TAG, "insert in " + backwardIndex + ", " + program.toString() + " / " + new Date(reusedErrorLayoutEdgeTime.get(backwardIndex)));

                                break;
                            }
                        }
                    }

                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
                    if (errorInRowLayout == null) {
                        errorInRowLayout = new LinearLayout(MainActivity.this);
                        errorInRowLayout.setOrientation(LinearLayout.HORIZONTAL);
                        yahooErrorParentLayout.addView(errorInRowLayout);
                        reusedYahooErrorLayout.add(errorInRowLayout);
                        if (forward) {
                            reusedErrorLayoutEdgeTime.add(program.getEndDate().getTime());
                        } else {
                            reusedErrorLayoutEdgeTime.add(program.getStartDate().getTime());
                        }
                    } else {
                        // adjust width of previous textView
                        TextView previousTextView;
                        if (forward) {
                            previousTextView = (TextView) errorInRowLayout.getChildAt(
                                    errorInRowLayout.getChildCount() - 1);
                        } else {
                            previousTextView = (TextView) errorInRowLayout.getChildAt(0);
                        }
                        LinearLayout.LayoutParams previousParams =
                                (LinearLayout.LayoutParams) previousTextView.getLayoutParams();
                        previousParams.width = Utils.getRelatedProgramSliceWidth(
                                (Program) previousTextView.getTag(R.id.program_data),
                                mModelController.getModel().getSearchingStartDate(),
                                mModelController.getModel().getSearchingEndDate());
                        if (forward) {
                            reusedErrorLayoutEdgeTime.set(index, program.getEndDate().getTime());
                        } else {


                            reusedErrorLayoutEdgeTime.set(reusedYahooErrorLayout.size() - index - 1, program.getStartDate().getTime());

                            Log.d(Utils.TMP_TAG, "update reused edge time " + new Date(reusedErrorLayoutEdgeTime.get(index)));

                            previousParams.setMarginStart(0);
                        }

                        previousTextView.setLayoutParams(previousParams);
                    }

                    if (program.getStartDate().getTime() > mModelController.getModel().getSearchingStartDate().getTime()) {
                        if (program.getStartDate().getTime() < ((Date) yahooErrorParentLayout.getTag(R.id.layout_start_date)).getTime()) {
                            yahooErrorParentLayout.setTag(R.id.layout_start_date, program.getStartDate());

                            Log.d(Utils.TMP_TAG, "setTag E " + yahooErrorParentLayout.getTag(R.id.layout_start_date));
                        }
                    } else {
                        if (mModelController.getModel().getSearchingStartDate().getTime() <
                                ((Date) yahooErrorParentLayout.getTag(R.id.layout_start_date)).getTime()) {
                            yahooErrorParentLayout.setTag(R.id.layout_start_date, mModelController.getModel().getSearchingStartDate());

                            Log.d(Utils.TMP_TAG, "setTag F " + yahooErrorParentLayout.getTag(R.id.layout_start_date));
                        }
                    }

//                    if (index < reusedErrorLayoutEdgeTime.size()) {
//                        if (!forward) {
//                            params.setMarginStart(Utils.getEmptySliceWidth(
//                                    new Date(reusedErrorLayoutEdgeTime.get(index)), program.getStartDate()));
//                            reusedErrorLayoutEdgeTime.set(index, program.getEndDate().getTime());
//                        }
//                    } else {
//                        // ***
////                        if (program.getStartDate().getTime() > ((Date) yahooErrorParentLayout.getTag()).getTime()) {
////                            params.setMarginStart(Utils.getEmptySliceWidth(
////                                    (Date) yahooErrorParentLayout.getTag(), program.getStartDate()));
////                        }
////                        reusedErrorLayoutEdgeTime.add(program.getEndDate().getTime());
//
//                        Log.d(Utils.TMP_TAG, program.getTitle() + " " + params.getMarginStart());
//                    }

                    if (!forward) {
                        if (program.getStartDate().getTime() > ((Date) yahooErrorParentLayout.getTag(R.id.layout_start_date)).getTime()) {
                            params.setMarginStart(Utils.getEmptySliceWidth(
                                    (Date) yahooErrorParentLayout.getTag(R.id.layout_start_date), program.getStartDate()));
                        }
                    }

                    textView.setLayoutParams(params);
                    if (forward) {
                        errorInRowLayout.addView(textView);
                    } else {
                        errorInRowLayout.addView(textView, 0);
                    }
                }
            }
        } else {
            TextView textView = createProgramTextView(program,
                    mModelController.getModel().getSearchingStartDate(),
                    mModelController.getModel().getSearchingEndDate(),
                    currentTime);

            // add margin of leftmost program in backward mode
            if (lastProgram && !forward) {
                if (program.getStartDate().getTime() > mModelController.getModel().getSearchingStartDate().getTime()) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
                    params.setMarginStart(Utils.getEmptySliceWidth(
                            mModelController.getModel().getSearchingStartDate(), program.getStartDate()));
                    textView.setLayoutParams(params);
                }
            }

            view = textView;
        }




//
//
//        if (!program.hasYahooTimeProblem()) {
//            if (neighborView != null && neighborView instanceof TextView &&
//                    (int) neighborView.getTag() == ViewController.PROGRAM_UI_PARTIAL_HIDE &&
//                    ((TextView) neighborView).getText().equals(program.getTitle() + "/" + program.getTime())) {
//                /** update existed program view **/
//                // resize width
//                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) neighborView.getLayoutParams();
//                params.width = Utils.getRelatedProgramSliceWidth(program,
//                        mModelController.getModel().getSearchingStartDate(),
//                        mModelController.getModel().getSearchingEndDate());
//
//                // reset if view is total showed
//                if (program.getStartDate().getTime() >=
//                        mModelController.getModel().getSearchingStartDate().getTime() &&
//                        program.getEndDate().getTime() <=
//                            mModelController.getModel().getSearchingEndDate().getTime()) {
//                    neighborView.setTag(ViewController.PROGRAM_UI_TOTAL_SHOWED);
//                }
//
//                if (!forward && lastProgram) {
//                    // backward first program add margin
//                    if (program.getStartDate().getTime() > mModelController.getModel().getSearchingStartDate().getTime()) {
//                        params.setMarginStart(Utils.getEmptySliceWidth(
//                                mModelController.getModel().getSearchingStartDate(), program.getStartDate()));
//                    }
//                }
//
//                neighborView.setLayoutParams(params);
//            } else {
//                /** create new program view **/
//                TextView textView = createProgramTextView(program,
//                        mModelController.getModel().getSearchingStartDate(),
//                        mModelController.getModel().getSearchingEndDate(),
//                        currentTime);
//
//                if (!forward) {
//                    if (neighborView != null) {
//                        // need to remove margin
//                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) neighborView.getLayoutParams();
//                        params.setMarginStart(0);
//                        neighborView.setLayoutParams(params);
//                    }
//
//                    if (lastProgram) {
//                        if (program.getStartDate().getTime() > mModelController.getModel().getSearchingStartDate().getTime()) {
//                            // backward first program add margin
//                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
//                            params.setMarginStart(Utils.getEmptySliceWidth(
//                                    mModelController.getModel().getSearchingStartDate(), program.getStartDate()));
//                            textView.setLayoutParams(params);
//                        }
//                    }
//                }
//
//                if (neighborView != null && neighborView instanceof TextView &&
//                        (int) neighborView.getTag() == ViewController.PROGRAM_UI_PARTIAL_HIDE) {
//                    // resize previous program width
//                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) neighborView.getLayoutParams();
//                    Program previousProgram = (Program) neighborView.getTag(R.id.program_data);
//                    params.width = Utils.getRelatedProgramSliceWidth(previousProgram,
//                            mModelController.getModel().getSearchingStartDate(),
//                            mModelController.getModel().getSearchingEndDate());
//                    neighborView.setLayoutParams(params);
//                }
//
//                view = textView;
//            }
//
//            mViewController.removeYahooErrorParentLayout(forward);
//
//        } else { // has Yahoo Problem
//            LinearLayout yahooErrorParentLayout = mViewController.getYahooErrorParentLayout(forward);
//            ArrayList<LinearLayout> reusedYahooErrorLayout = mViewController.getReusedErrorLayout(forward);
//            ArrayList<Long> reusedErrorLayoutEndTime = mViewController.getReusedErrorLayoutEdgeTime(forward);
//
//            Log.d(Utils.TMP_TAG, program.getTitle() + " " + (yahooErrorParentLayout == null));
//
//            if (yahooErrorParentLayout == null) {
//                yahooErrorParentLayout = new LinearLayout(MainActivity.this);
//                yahooErrorParentLayout.setOrientation(LinearLayout.VERTICAL);
//                yahooErrorParentLayout.setBackgroundResource(R.drawable.error_program_textview_layout);
//                yahooErrorParentLayout.setTag(program.getStartDate());
//
//                reusedYahooErrorLayout = new ArrayList<>();
//                reusedErrorLayoutEndTime = new ArrayList<>();
//                mViewController.putReusederrorLayout(forward, reusedYahooErrorLayout);
//                mViewController.putReusedErrorLayoutEdgeTime(forward, reusedErrorLayoutEndTime);
//                mViewController.putYahooErrorParentLayout(forward, yahooErrorParentLayout);
//
//                view = yahooErrorParentLayout;
//            }
//
//            if (neighborView != null && neighborView instanceof TextView &&
//                    (int) neighborView.getTag() == ViewController.PROGRAM_UI_PARTIAL_HIDE &&
//                    ((TextView) neighborView).getText().equals(program.getTitle() + "/" + program.getTime())) {
//                // case 1
//                // program existed but status change from normal to error program
//                ((ViewGroup) neighborView.getParent()).removeView(neighborView);
//
//                /** create new program view **/
//                TextView textView = createProgramTextView(program,
//                        mModelController.getModel().getSearchingStartDate(),
//                        mModelController.getModel().getSearchingEndDate(),
//                        currentTime);
//                textView.setTextColor(Color.RED);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                        Utils.getRelatedProgramSliceWidth(program,
//                                mModelController.getModel().getSearchingStartDate(),
//                                mModelController.getModel().getSearchingEndDate()),
//                        ViewGroup.LayoutParams.WRAP_CONTENT
//                );
//                textView.setLayoutParams(params);
//
//                LinearLayout errorInRowLayout = null;
//                int index;
//                // search suitable layout to add view
//                // maybe can skip this b/c neighborView is textview
//                for (index = 0; index < reusedYahooErrorLayout.size(); index++) {
//                    if (reusedErrorLayoutEndTime.get(index) <= program.getStartDate().getTime()) {
//                        errorInRowLayout = reusedYahooErrorLayout.get(index);
//                        break;
//                    }
//                }
//
//                if (errorInRowLayout == null) {
//                    errorInRowLayout = new LinearLayout(MainActivity.this);
//                    errorInRowLayout.setOrientation(LinearLayout.HORIZONTAL);
//                    yahooErrorParentLayout.addView(errorInRowLayout);
//                    reusedYahooErrorLayout.add(errorInRowLayout);
//
//                    // add channel title height if there are multiple error linearLayout
////                    if (channelHeight < reusedYahooErrorLayout.size()) {
////                        for (int i = channelHeight; i < reusedYahooErrorLayout.size(); i++) {
////                            TextView emptyChannelTitleTextView = new TextView(MainActivity.this);
////                            emptyChannelTitleTextView.setPadding(10, 10, 10, 10);
////                            if (channelCount % 2 == 1) {
////                                emptyChannelTitleTextView.setBackgroundColor(getResources().getColor(R.color.colorChannelTitleBg, null));
////                            }
////                            mChannelTitleLayout.addView(emptyChannelTitleTextView);
////                        }
////
////                        channelHeight = reusedYahooErrorLayout.size();
////                    }
//
//
//                    // adjust margin if program list is not continuous for first initial
////                    if (program.getStartDate().getTime() > currentNormalBlockEndTime
////                            && program.getStartDate().getTime() > channelGroup.getSearchingStartDate().getTime()) {
////                        if (currentNormalBlockEndTime == 0L) {
////                            currentNormalBlockEndTime = channelGroup.getSearchingStartDate().getTime();
////                        }
////
////                        params = (LinearLayout.LayoutParams) textView.getLayoutParams();
////                        params.setMarginStart(Utils.getEmptySliceWidth(
////                                new Date(currentNormalBlockEndTime), program.getStartDate()));
////                        textView.setLayoutParams(params);
////                    }
//
//
//                    // adjust margin if program list is not continuous
////                    if (index < reusedErrorLayoutEndTime.size() &&
////                            reusedErrorLayoutEndTime.get(index) < program.getStartDate().getTime()) {
////
////                        params = (LinearLayout.LayoutParams) textView.getLayoutParams();
////                        params.setMarginStart(Utils.getEmptySliceWidth(
////                                new Date(reusedErrorLayoutEndTime.get(index)), program.getStartDate()));
////                        textView.setLayoutParams(params);
//                }
//
//                errorInRowLayout.addView(textView);
//                if (index < reusedErrorLayoutEndTime.size()) {
//                    reusedErrorLayoutEndTime.set(index, program.getEndDate().getTime());
//                } else {
//                    reusedErrorLayoutEndTime.add(program.getEndDate().getTime());
//                }
//            } else if (neighborView != null && neighborView instanceof LinearLayout) {
//                // case 2
////                yahooErrorParentLayout = ((LinearLayout) neighborView);
//                TextView existTextView = null;
//                // check if program existed
//                for (int i = 0; i < yahooErrorParentLayout.getChildCount(); i++) {
//                    LinearLayout childLayout = (LinearLayout) yahooErrorParentLayout.getChildAt(i);
//
//                    if (childLayout.getChildCount() > 0) {
//                        TextView textView = (TextView) childLayout.getChildAt(childLayout.getChildCount() - 1);
//                        if (textView.getText().equals(program.getTitle() + "/" + program.getTime())) {
//                            existTextView = textView;
//
//                            Log.d("alexx", "existed view " + existTextView.getText());
//
//                            break;
//                        }
//                    }
//                }
//
//                if (existTextView != null) {
//                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) existTextView.getLayoutParams();
//                    params.width = Utils.getRelatedProgramSliceWidth(program,
//                            mModelController.getModel().getSearchingStartDate(),
//                                    mModelController.getModel().getSearchingEndDate());
//                    existTextView.setLayoutParams(params);
//                } else {
//
////                    Log.d("alexx", "new " + program.getTitle());
//                    TextView textView = createProgramTextView(program,
//                            mModelController.getModel().getSearchingStartDate(),
//                            mModelController.getModel().getSearchingEndDate(),
//                            currentTime);
//                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                            Utils.getRelatedProgramSliceWidth(program,
//                                    mModelController.getModel().getSearchingStartDate(),
//                                    mModelController.getModel().getSearchingEndDate()),
//                            ViewGroup.LayoutParams.WRAP_CONTENT
//                    );
//
//
//                    LinearLayout errorInRowLayout = null;
//                    int index;
//                    // search suitable layout to add view
//                    for (index = 0; index < reusedYahooErrorLayout.size(); index++) {
//                        if (forward) {
//                            if (reusedErrorLayoutEndTime.get(index) <= program.getStartDate().getTime()) {
//                                errorInRowLayout = reusedYahooErrorLayout.get(index);
//                                break;
//                            }
//                        } else {
//                            if (reusedErrorLayoutEndTime.get(index) >= program.getEndDate().getTime()) {
//                                errorInRowLayout = reusedYahooErrorLayout.get(index);
//                                break;
//                            }
//                        }
//                    }
//
//                    if (errorInRowLayout == null) {
//                        errorInRowLayout = new LinearLayout(MainActivity.this);
//                        errorInRowLayout.setOrientation(LinearLayout.HORIZONTAL);
//                        yahooErrorParentLayout.addView(errorInRowLayout);
//                        reusedYahooErrorLayout.add(errorInRowLayout);
//                    } else {
//                        // adjust width of previous textView
//                        TextView previousTextView;
//                        if (forward) {
//                            previousTextView = (TextView) errorInRowLayout.getChildAt(
//                                    errorInRowLayout.getChildCount() - 1);
//                        } else {
//                            previousTextView = (TextView) errorInRowLayout.getChildAt(0);
//                        }
//                        LinearLayout.LayoutParams previousParams =
//                                (LinearLayout.LayoutParams) previousTextView.getLayoutParams();
//                        previousParams.width = Utils.getRelatedProgramSliceWidth(
//                                                (Program) previousTextView.getTag(R.id.program_data),
//                                                mModelController.getModel().getSearchingStartDate(),
//                                                mModelController.getModel().getSearchingEndDate());
//                        previousTextView.setLayoutParams(previousParams);
//                    }
//
//                    if (index < reusedErrorLayoutEndTime.size()) {
//                        params.setMarginStart(Utils.getEmptySliceWidth(
//                                new Date(reusedErrorLayoutEndTime.get(index)), program.getStartDate()));
//                        reusedErrorLayoutEndTime.set(index, program.getEndDate().getTime());
//                    } else {
//                        // ***
////                        if (program.getStartDate().getTime() > ((Date) yahooErrorParentLayout.getTag()).getTime()) {
////                            params.setMarginStart(Utils.getEmptySliceWidth(
////                                    (Date) yahooErrorParentLayout.getTag(), program.getStartDate()));
////                        }
//                        reusedErrorLayoutEndTime.add(program.getEndDate().getTime());
//
//                        Log.d(Utils.TMP_TAG, program.getTitle() + " " + params.getMarginStart());
//                    }
//
//
//                    textView.setLayoutParams(params);
//                    errorInRowLayout.addView(textView);
//
//                }
//
//            } else {
//                TextView textView = createProgramTextView(program,
//                        mModelController.getModel().getSearchingStartDate(),
//                        mModelController.getModel().getSearchingEndDate(),
//                        currentTime);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                        Utils.getRelatedProgramSliceWidth(program,
//                                mModelController.getModel().getSearchingStartDate(),
//                                mModelController.getModel().getSearchingEndDate()),
//                        ViewGroup.LayoutParams.WRAP_CONTENT
//                );
//
//                LinearLayout errorInRowLayout = null;
//                errorInRowLayout = new LinearLayout(MainActivity.this);
//                errorInRowLayout.setOrientation(LinearLayout.HORIZONTAL);
//                yahooErrorParentLayout.addView(errorInRowLayout);
//                reusedYahooErrorLayout.add(errorInRowLayout);
//
//                textView.setLayoutParams(params);
//                errorInRowLayout.addView(textView);
//                reusedErrorLayoutEndTime.add(program.getEndDate().getTime());
//            }
//
//        }

        return view;
    }

    public void adjustView() {

    }

    public void addTimeMark(ChannelGroup channelGroup, boolean forward) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(channelGroup.getSearchingStartDate());
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        long duration = channelGroup.getSearchingEndDate().getTime() -
                channelGroup.getSearchingStartDate().getTime();
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                Utils.getSliceBacisWidth(MainActivity.this), ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < hours; i++) {
            TextView textView = new TextView(MainActivity.this);
            textView.setText(sdf.format(calendar.getTime()));
            textView.setTypeface(null, Typeface.BOLD);
            textView.setBackgroundColor(Utils.getHourOfDayColor(MainActivity.this, (currentHour + i) % 24));
            textView.setLayoutParams(params);
            textView.setTextSize(14);
            textView.setPadding(10, 10, 10, 10);
            if (forward) {
                mTimeSliceLayout.addView(textView);
            } else {
                mTimeSliceLayout.addView(textView, i);
            }

            calendar.add(Calendar.HOUR_OF_DAY, 1);
        }
    }

    public void addChannelTitle(String channelTitle) {
        TextView textView = new TextView(MainActivity.this);
        textView.setText(channelTitle);
        textView.setTextSize(14);
        textView.setPadding(10, 10, 10, 10);
//        if (channelCount % 2 == 1) {
//            textView.setBackgroundColor(getResources().getColor(R.color.colorChannelTitleBg, null));
//        }
        mChannelTitleLayout.addView(textView);
    }

    public TextView createProgramTextView(Program program, Date channelGroupStart, Date channelGroupEnd, Date currentTime) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                Utils.getRelatedProgramSliceWidth(program, channelGroupStart, channelGroupEnd),
                ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView textView = new TextView(MainActivity.this);
        textView.setText(program.getTitle() + "/" + program.getTime());
        textView.setBackgroundResource(R.drawable.program_textview);
        textView.setLayoutParams(params);
        textView.setHorizontallyScrolling(true);
        textView.setSingleLine(true);
        textView.setTextSize(14);
        textView.setPadding(10, 10, 10, 10);
        textView.setTag(R.id.program_data, program);

        if (program.getStartDate().getTime() < channelGroupStart.getTime() ||
                program.getEndDate().getTime() > channelGroupEnd.getTime()) {
            textView.setTag(R.id.ui_showed_level, ViewController.PROGRAM_UI_PARTIAL_HIDE);
        } else {
            textView.setTag(R.id.ui_showed_level, ViewController.PROGRAM_UI_TOTAL_SHOWED);
        }

        if (program.hasYahooTimeProblem()) {
            textView.setTextColor(Color.RED);
        }

        if (currentTime.getTime() >= program.getStartDate().getTime() &&
                currentTime.getTime() < program.getEndDate().getTime()) {
            textView.setTextColor(Color.BLUE);
        }

        return textView;
    }

    public void refreshUI(final boolean forward) {
        runOnUiThread(new Runnable() {
            public void run() {
                //clear UI
                mChannelTitleLayout.removeAllViews();
                TextView textView = new TextView(MainActivity.this);
                textView.setPadding(10, 10, 10, 10);
                mChannelTitleLayout.addView(textView);
                mChannelGroupContentLayout.removeAllViews();
                mTimeSliceLayout.removeAllViews();
                mViewController.clearChannelLayout();

                ChannelGroup channelGroup = mModelController.getModel();

                if (channelGroup.isReadyToPresent()) {
                    /** time mark **/
                    addTimeMark(channelGroup, true);

                    LinearLayout.LayoutParams params = null;

                    int channelCount = 0;
                    int channelHeight = 0;
                    Date currentTime = Calendar.getInstance().getTime();
                    for (Channel channel: channelGroup.getChannels().values()) {
                        channelCount++;
                        channelHeight = 1;

                        /** channel title **/
                        addChannelTitle(channel.getTitle());

                        /** channel content **/
                        LinearLayout channelContentLayout = new LinearLayout(MainActivity.this);
                        channelContentLayout.setOrientation(LinearLayout.HORIZONTAL);
                        mChannelGroupContentLayout.addView(channelContentLayout);
                        mViewController.addChannelLayout(channel.getTitle(), channelContentLayout);

                        // handle time error of yahoo program model
                        LinearLayout yahooErrorParentLayout = null;
                        ArrayList<LinearLayout> reusedYahooErrorLayout = new ArrayList<>();
                        ArrayList<Long> reusedErrorLayoutEndTime = new ArrayList<>();
                        Long currentNormalBlockEndTime = 0L;

                        boolean firstProgram = true;
                        for (Program program : channel.getPrograms()) {
                            textView = createProgramTextView(program,
                                           channelGroup.getSearchingStartDate(),
                                           channelGroup.getSearchingEndDate(),
                                           currentTime);

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
                                    yahooErrorParentLayout.setBackgroundResource(R.drawable.error_program_textview_layout);
                                    yahooErrorParentLayout.setTag(program.getStartDate());
                                    reusedYahooErrorLayout.clear();
                                    reusedErrorLayoutEndTime.clear();
                                    channelContentLayout.addView(yahooErrorParentLayout);

                                    mViewController.putReusederrorLayout(channel.getTitle(), forward, reusedYahooErrorLayout);
                                    mViewController.putReusedErrorLayoutEdgeTime(channel.getTitle(), forward, reusedErrorLayoutEndTime);
                                    mViewController.putYahooErrorParentLayout(channel.getTitle(), forward, yahooErrorParentLayout);
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
                                                emptyChannelTitleTextView.setBackgroundColor(getResources().getColor(R.color.colorChannelTitleBg));
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

                                mViewController.removeYahooErrorParentLayout(channel.getTitle(), forward);
                            }
                        }
                    }

                    if (currentTime.getTime() >= channelGroup.getSearchingStartDate().getTime() &&
                            currentTime.getTime() <= channelGroup.getSearchingEndDate().getTime()) {
                        /** time indicator (vertical divider) **/
                        // FIXME the added view can be drawn with frame layout have at least 3 child
                        if (mTimeIndicatorFrameLayout.getChildCount() > 2) {
                            mTimeIndicatorFrameLayout.removeViewAt(0);
                        }

                        int marginStart = Utils.getEmptySliceWidth(
                                channelGroup.getSearchingStartDate(), currentTime);
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

                if (!forward && mBasicMeasureViewWidth != -1) {
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

        MenuItem menuItem = menu.findItem(R.id.use_fake_data);
        if (menuItem != null) {
            if (Utils.USE_FAKE_DATE_DEBUG) {
                menuItem.setTitle("use fake data: on");
            } else {
                menuItem.setTitle("use fake data: off");
            }
        }

        menuItem = menu.findItem(R.id.refresh_on_parse);
        if (menuItem != null) {
            if (Utils.REFRESH_UI_ON_PARSE_NEW_CONTENT) {
                menuItem.setTitle("refresh on update: on");
            } else {
                menuItem.setTitle("refresh on update: off");
            }
        }

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
        } else if (id == R.id.use_fake_data) {
            if (Utils.USE_FAKE_DATE_DEBUG) {
                Utils.USE_FAKE_DATE_DEBUG = false;
                Log.d(Utils.TMP_TAG, "set fake data: off");
                item.setTitle("use fake data: off");
            } else {
                Utils.USE_FAKE_DATE_DEBUG = true;
                Log.d(Utils.TMP_TAG, "set fake data: on");
                item.setTitle("use fake data: on");
            }
        } else if (id == R.id.refresh_on_parse) {
            if (Utils.REFRESH_UI_ON_PARSE_NEW_CONTENT) {
                item.setTitle("refresh on update: off");
            } else {
                item.setTitle("refresh on update: on");
            }

            Utils.REFRESH_UI_ON_PARSE_NEW_CONTENT = !Utils.REFRESH_UI_ON_PARSE_NEW_CONTENT;
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