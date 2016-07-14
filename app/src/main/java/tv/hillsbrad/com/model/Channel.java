package tv.hillsbrad.com.model;

import java.util.ArrayList;

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
            int lastIndex = mPrograms.size() -1;
            for (int i = lastIndex; i >= 0; i--) {
                Program compareProgram = mPrograms.get(i);

                if (program.getStartDate().getTime() < compareProgram.getEndDate().getTime()) {
                    compareProgram.setHasYahooTimeProblem(true);
                    program.setHasYahooTimeProblem(true);
                    break;
                }

                if (!compareProgram.hasYahooTimeProblem()) {
                    break;
                }
            }
        }
        mPrograms.add(program);
    }

    public ArrayList<Program> getPrograms() {
        return mPrograms;
    }

    public void attach(Channel channel) {
        if (mPrograms.size() == 0) {
            mPrograms.addAll(channel.getPrograms());
        } else if (channel.getPrograms().size() != 0) {
            long baseTime;
            Boolean backward = false;
            if (mPrograms.get(0).getStartDate().getTime() > channel.getPrograms().get(0).getStartDate().getTime()) {
                baseTime = mPrograms.get(0).getStartDate().getTime();
                backward = true;
            } else {
                baseTime = mPrograms.get(mPrograms.size()-1).getEndDate().getTime();
            }

            int count = 0;
            for (Program program: channel.getPrograms()) {
                if (mPrograms.contains(program)) {
                    continue;
                }

                if (backward) {
//                    if (program.getEndDate().getTime() > baseTime) {
//                        program.setHasYahooTimeProblem(true);
//                    }
                    mPrograms.add(count++, program);
                } else {
//                    if (program.getStartDate().getTime() < baseTime) {
//                        program.setHasYahooTimeProblem(true);
//                    }
                    mPrograms.add(program);
                }
            }
        }
    }
}
