package tv.hillsbrad.com.model;

import android.util.Log;

import java.util.ArrayList;

import tv.hillsbrad.com.Utils;

/**
 * Created by alex on 6/8/16.
 */
public class Channel {
    private String mTitle;
    private ArrayList<Program> mPrograms;

    public Channel(String title) {
        mTitle = title;
        mPrograms = new ArrayList<>();
    }

    public String getTitle() {
        return mTitle;
    }

    /**
     *  Add from parser.
     */
    public void addProgram(Program program) {
        if (mPrograms.size() != 0) {
            for (int i = mPrograms.size() -1; i >= 0; i--) {
                if (program.getStartDate().getTime() < mPrograms.get(i).getEndDate().getTime()) {
                    if (Utils.YAHOO_ERROR_DEBUG) {
                        Log.d(Utils.TAG, mPrograms.get(i).toString() + " addProgram HasYahooTimeProblem \n "
                            + program.toString() + " addProgram HasYahooTimeProblem");
                    }
                    mPrograms.get(i).setHasYahooTimeProblem(true);
                    program.setHasYahooTimeProblem(true);
                    break;
                }

                if (!mPrograms.get(i).hasYahooTimeProblem()) {
                    break;
                }
            }
        }
        mPrograms.add(program);
    }

    public ArrayList<Program> getPrograms() {
        return mPrograms;
    }

    public void attach(Channel channel, boolean forward) {
        if (mPrograms.size() == 0) {
            mPrograms.addAll(channel.getPrograms());
        } else if (channel.getPrograms().size() != 0) {
            int count = 0;
            for (Program program: channel.getPrograms()) {
                if (forward) {
                    // judge if program time is correct
                    for (int i = mPrograms.size() -1; i >= 0; i--) {
                        if (mPrograms.contains(program)) {break;}

                        if (program.getStartDate().getTime() < mPrograms.get(i).getEndDate().getTime()) {
                            if (Utils.YAHOO_ERROR_DEBUG) {
                                Log.d(Utils.TAG, program.toString() + " attach forward HasYahooTimeProblem\n "
                                    + mPrograms.get(i).toString());
                            }
                            mPrograms.get(i).setHasYahooTimeProblem(true);
                            program.setHasYahooTimeProblem(true);
                        }

                        if (!mPrograms.get(i).hasYahooTimeProblem()) {break;}
                    }

                    if (!mPrograms.contains(program)) {mPrograms.add(program);}
                } else {
                    // judge if program time is correct
                    for (int i = count; i < mPrograms.size(); i++) {
                        if (mPrograms.contains(program)) {break;}

                        if (program.getEndDate().getTime() > mPrograms.get(i).getStartDate().getTime()) {
                            if (Utils.YAHOO_ERROR_DEBUG) {
                                Log.d(Utils.TAG, program.toString() + " attach backward HasYahooTimeProblem \n "
                                    + mPrograms.get(i).toString());
                            }
                            mPrograms.get(i).setHasYahooTimeProblem(true);
                            program.setHasYahooTimeProblem(true);
                        }

                        if (!mPrograms.get(i).hasYahooTimeProblem()) {break;}
                    }

                    if (!mPrograms.contains(program)) {mPrograms.add(count++, program);}
                }
            }
        }
    }
}
