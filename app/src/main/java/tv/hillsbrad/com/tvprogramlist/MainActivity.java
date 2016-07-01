package tv.hillsbrad.com.tvprogramlist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import tv.hillsbrad.com.Utils;
import tv.hillsbrad.com.yahoo.YahooTvConstant;
import tv.hillsbrad.com.yahoo.YahooTvTimeParser;
import tv.hillsbrad.com.model.Channel;
import tv.hillsbrad.com.model.ChannelGroup;
import tv.hillsbrad.com.model.Program;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mChannelTitleLayout;
    private LinearLayout mProgramDurationLayout;
    private LinearLayout mTimeSliceLayout;

    private Spinner mTypeSpinner;

    private ImageButton mPreviousButton;
    private ImageButton mNextButton;
    private boolean mIsProcessing;

//    private ViewController mViewController;
    private ModelController mModelController;

    ArrayAdapter<String> mSpinnerAdapter;

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


//        mViewController = new ViewController();
        mModelController = new ModelController();

        mChannelTitleLayout = (LinearLayout) findViewById(R.id.channel_title_layout);
        TextView textView = new TextView(MainActivity.this);
        mChannelTitleLayout.addView(textView);
        mProgramDurationLayout = (LinearLayout) findViewById(R.id.program_duration_layout);
        mTimeSliceLayout = (LinearLayout) findViewById(R.id.time_slice_layout);

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

        CustomChannelSettings channelSettings = new CustomChannelSettings();
        channelSettings.show(getSupportFragmentManager(),
                CustomChannelSettings.class.getSimpleName());
    }

    public void initSpinner() {
        String[] channels = new String[YahooTvConstant.CHANNEL_TYPE.length];
        for (int i = 0; i < YahooTvConstant.CHANNEL_TYPE.length; i++) {
            channels[i] = getString(YahooTvConstant.CHANNEL_TYPE[i]);
        }
        mSpinnerAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, channels);
        mTypeSpinner.setAdapter(mSpinnerAdapter);
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!mIsProcessing) {
                    Log.d("alexx", "item selected: " + parent.getItemAtPosition(position));
                    mModelController.setCurrentGroup(YahooTvConstant.Group.convertToGroup(position + 1));


                    Log.d("alexx", "currentGroup=" + mModelController.getCurrentGroup().getValue());
                    if (mModelController.getModel().getSearchingStartDate() == null &&
                            mModelController.getModel().getSearchingEndDate() == null) {
                        searchMore(true);
                    } else {
                        Log.d("alexx", "data already existed");
                        Log.d("alexx", mModelController.getModel().getSearchingStartDate().toString() + "/" +
                                mModelController.getModel().getSearchingEndDate());
                        refreshUI();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void search(final boolean next) {
//        if (!mTimeController.isSearched()) {
            new Thread() {
                public void run() {
                    mModelController.parseMoreDataFromHttp(next);
                    refreshUI();
                }
            }.start();

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
        if (!mIsProcessing) {
            mIsProcessing = true;
            mPreviousButton.setEnabled(false);
            mNextButton.setEnabled(false);
            mTypeSpinner.setEnabled(false);

            search(next);
        }
    }

    public void refreshUI() {
        Log.d("alexx", "refreshUI");

        runOnUiThread(new Runnable() {
            public void run() {

                //clear UI
                mChannelTitleLayout.removeAllViews();
                TextView textView = new TextView(MainActivity.this);
                mChannelTitleLayout.addView(textView);
                mProgramDurationLayout.removeAllViews();
                mTimeSliceLayout.removeAllViews();

                ChannelGroup channelGroup = mModelController.getModel();

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
                    mTimeSliceLayout.addView(textView);
                }


                int channelCount = 0;
                for (Channel channel : channelGroup.getChannels().values()) {
                    // channel title
                    textView = new TextView(MainActivity.this);
                    textView.setText(channel.getTitle());
                    if (channelCount++ % 2 == 0) {
                        textView.setBackgroundColor(getResources().getColor(R.color.colorChannelTitleBg, null));
                    }
                    mChannelTitleLayout.addView(textView);
                    Log.d("alexx", channel.getTitle());

                    // channel content
                    LinearLayout channelSliceLayout = new LinearLayout(MainActivity.this);
                    channelSliceLayout.setOrientation(LinearLayout.HORIZONTAL);
                    mProgramDurationLayout.addView(channelSliceLayout);
                    for (Program program : channel.getPrograms()) {
                        params = new ViewGroup.LayoutParams(
                                Utils.getRelatedProgramSliceWidth(program,
                                        mModelController.getModel().getSearchingStartDate(),
                                        mModelController.getModel().getSearchingEndDate()),
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        textView = new TextView(MainActivity.this);
                        textView.setText(program.getTitle() + "/" + program.getTime());
                        textView.setBackground(getResources().getDrawable(R.drawable.program_textview, null));
                        textView.setLayoutParams(params);
                        textView.setHorizontallyScrolling(true);
                        textView.setSingleLine(true);
                        channelSliceLayout.addView(textView);

                        Log.d("alexx", "____" + params.width + " / " + program.toString());
                    }
//                    mViewController.add(channel.getTitle(), channelSliceLayout);
                }
                mIsProcessing = false;
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
