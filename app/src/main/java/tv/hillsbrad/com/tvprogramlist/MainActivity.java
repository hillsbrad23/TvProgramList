package tv.hillsbrad.com.tvprogramlist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.YahooTvConstant;
import com.example.YahooTvTimeParser;
import com.example.model.Channel;
import com.example.model.ChannelGroup;
import com.example.model.Program;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mChannelLayout;
    private LinearLayout mProgramLayout;
    private LinearLayout mTimeSliceLayout;

    private ImageButton mPreviousButton;
    private ImageButton mNextButton;

    private TimeController mTimeController;
    private ViewController mViewController;

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

        final Calendar calendar = Calendar.getInstance();
        mTimeController = new TimeController();
        mTimeController.setTime(YahooTvTimeParser.convert2StartDate(calendar));

        mViewController = new ViewController();

        mChannelLayout = (LinearLayout) findViewById(R.id.channel_layout);
        TextView textView = new TextView(MainActivity.this);
        mChannelLayout.addView(textView);
        mProgramLayout = (LinearLayout) findViewById(R.id.programLayout);
        mTimeSliceLayout = (LinearLayout) findViewById(R.id.timeSliceLayout);

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

        search(true);

    }

    public void search(final boolean next) {
        if (!mTimeController.isSearched()) {
            new Thread() {
                public void run() {
                    final ChannelGroup channelGroup = YahooTvTimeParser.parse(MainActivity.this,
                            YahooTvConstant.Group.FIVE, mTimeController.getCalendar());

                    if (channelGroup != null) {
                        runOnUiThread(new Runnable() {
                            public void run() {

                                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                                        Utils.getSliceBacisWidth(MainActivity.this), ViewGroup.LayoutParams.WRAP_CONTENT);
                                int currentHour = mTimeController.getCurrentHourOfDay();

                                mTimeController.resetSearchTime();
                                Log.d("alexx", "viewStartTime " + mTimeController.getViewStartDate());
                                Log.d("alexx", "viewEndTime   " + mTimeController.getViewEndDate());

                                TextView textView = new TextView(MainActivity.this);
                                textView.setText(Utils.toHourOfDayString(currentHour));
                                textView.setBackgroundColor(Utils.getHourOfDayColor(MainActivity.this, currentHour));
                                textView.setLayoutParams(params);
                                if (next) {
                                    mTimeSliceLayout.addView(textView);
                                } else {
                                    mTimeSliceLayout.addView(textView, 0);
                                }

                                textView = new TextView(MainActivity.this);
                                textView.setText(Utils.toHourOfDayString(currentHour + 1));
                                textView.setBackgroundColor(Utils.getHourOfDayColor(MainActivity.this, currentHour + 1));
                                textView.setLayoutParams(params);
                                if (next) {
                                    mTimeSliceLayout.addView(textView);
                                } else {
                                    mTimeSliceLayout.addView(textView, 1);
                                }

                                int channelCount = 0;
                                for (Channel channel : channelGroup.getChannels()) {
                                    int bg = (channelCount++ % 2 == 0) ? R.color.programBg1 : R.color.programBg2;
                                    bg = getResources().getColor(bg, null);

                                    LinearLayout sliceLayout = null;
                                    // channel title
                                    if (mViewController.containsChannel(channel.getTitle())) {
                                        sliceLayout = mViewController.getLayout(channel.getTitle());
                                        Log.d("alexx", "existed: " + channel.getTitle());
                                    } else {
                                        textView = new TextView(MainActivity.this);
                                        textView.setText(channel.getTitle());
                                        textView.setBackgroundColor(bg);
                                        mChannelLayout.addView(textView);

                                        sliceLayout = new LinearLayout(MainActivity.this);
                                        sliceLayout.setOrientation(LinearLayout.HORIZONTAL);
                                        mProgramLayout.addView(sliceLayout);

                                        Log.d("alexx", "new: " + channel.getTitle());
                                    }

                                    // channel content
                                    int count = 0;
                                    int totalCount = channel.getPrograms().size();
                                    for (Program program : channel.getPrograms()) {

                                        if (count == 0 && next && sliceLayout.getChildCount() > 0 &&
                                                program.getStartDate().getTime() < channelGroup.getStartDate().getTime()) {
                                            // if need to merge privious item
                                            textView = (TextView) sliceLayout.getChildAt(sliceLayout.getChildCount()-1);
                                            params = textView.getLayoutParams();
                                            params.width = Utils.getRelatedProgramSliceWidth(program,
                                                    mTimeController.getViewStartDate(),
                                                    mTimeController.getViewEndDate());
                                            textView.requestLayout();
                                            Log.d("alexx", "\tnext exist");
                                            Log.d("alexx", "\t" + params.width + " / " + program.toString());
                                        } else if (count == (totalCount -1) && !next && sliceLayout.getChildCount() > count &&
                                                program.getEndDate().getTime() >
                                                        (channelGroup.getStartDate().getTime() + YahooTvConstant.YAHOO_SEARCH_TIME * Utils.MILLISECOND_IN_HOUR)) {
                                            // if need to merge post item
                                            textView = (TextView) sliceLayout.getChildAt(count);
                                            params = textView.getLayoutParams();
                                            params.width = Utils.getRelatedProgramSliceWidth(program,
                                                    mTimeController.getViewStartDate(),
                                                    mTimeController.getViewEndDate());
                                            textView.requestLayout();

                                            Log.d("alexx", "\tprevious exist");
                                            Log.d("alexx", "\t" + params.width + " / " + program.toString());
                                        } else {
                                            params = new ViewGroup.LayoutParams(
                                                    Utils.getRelatedProgramSliceWidth(program,
                                                            mTimeController.getViewStartDate(),
                                                            mTimeController.getViewEndDate()),
                                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                                            textView = new TextView(MainActivity.this);
                                            textView.setText(program.getTitle());
                                            textView.setBackground(getResources().getDrawable(R.drawable.program_textview, null));
//                                            textView.setBackgroundColor(Utils.getProgramSliceColor(MainActivity.this, sliceLayout.getChildCount()));
                                            textView.setLayoutParams(params);
                                            textView.setHorizontallyScrolling(true);
                                            textView.setSingleLine(true);

                                            if (next) {
                                                sliceLayout.addView(textView);
                                            } else {
                                                sliceLayout.addView(textView, count);
                                            }

                                            Log.d("alexx", "\t" + params.width + " / " + program.toString());
                                        }
                                        count++;
                                    }
                                    mViewController.add(channel.getTitle(), sliceLayout);
                                }
                            }
                        });
                    }
                }
            }.start();
        } else {
            Log.d("alexx", "already search / " + mTimeController.getCalendar().getTime());
        }


    }

    public void searchMore(boolean next) {
        if (next) {
            mTimeController.addTime(YahooTvConstant.YAHOO_SEARCH_TIME);
        } else {
            mTimeController.addTime(YahooTvConstant.YAHOO_SEARCH_TIME * -1);
        }
        search(next);
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
