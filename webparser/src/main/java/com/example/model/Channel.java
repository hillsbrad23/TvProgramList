package com.example.model;

import java.util.ArrayList;
import java.util.Date;

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

    public void addProgram(Program program) {
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

            for (Program program: channel.getPrograms()) {
                int count = 0;

                if (mPrograms.contains(program)) {
                    continue;
                }

                if (backward) {
                    if (program.getEndDate().getTime() > baseTime) {
                        program.setHasTimeProblem(true);
                    }
                    mPrograms.add(count++, program);
                } else {
                    if (program.getStartDate().getTime() < baseTime) {
                        program.setHasTimeProblem(true);
                    }
                    mPrograms.add(program);
                }
            }
        }
    }
}
