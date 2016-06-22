package com.example.model;

import java.util.ArrayList;

/**
 * Created by alex on 6/8/16.
 */
public class Channel {
    private String mTitle;
    private ArrayList<Program> mPrograms;

    public Channel(String title) {
        mTitle = title;
        mPrograms = new ArrayList<Program>();
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
}
