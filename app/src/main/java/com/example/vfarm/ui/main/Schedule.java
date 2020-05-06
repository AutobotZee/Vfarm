package com.example.vfarm.ui.main;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;

public class Schedule implements Parcelable {
    public String CMD;
    public String NAME;
    public String ADDRESS;
    public String START_TIME;
    public String END1_TIME;
    public Schedule(String cmd, String add, String start_time, String end_time){
        this.CMD = cmd;
        this.ADDRESS = add;
        this.START_TIME = start_time;
        this.END1_TIME = end_time;
        this.NAME = "SCHEDULE";
    };
    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    public void sorter(ArrayList<Schedule> sch){
        int i, j, min_idx;

        // One by one move boundary of unsorted subarray
        for (i = 0; i < sch.size()-1; i++)
        {
            // Find the minimum element in unsorted array
            min_idx = i;
            for (j = i+1; j < sch.size(); j++)
                if (Integer.parseInt(sch.get(j).START_TIME )< Integer.parseInt(sch.get(i).START_TIME))
                    min_idx = j;

            // Swap the found minimum element with the first element
            Collections.swap(sch,i,min_idx);
        }
    }
    public Schedule(){
        this.CMD = "00";
        this.ADDRESS = "00";
        this.START_TIME = "00";
        this.END1_TIME = "00";

    };
    public String getCMD() {
        return CMD;
    }
    public String getADDRESS() {
        return ADDRESS;
    }
    public String getSTART_TIME() {
        return START_TIME;
    }
    public String getEND1_TIME() {
        return END1_TIME;
    }
    public String getNAME() {
        return NAME;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(CMD);
        dest.writeString(ADDRESS);
        dest.writeString(START_TIME);
        dest.writeString(END1_TIME);
        dest.writeString(NAME);
    }
    public Schedule(Parcel in) {
        this.CMD = in.readString();
        this.ADDRESS = in.readString();
        this.START_TIME = in.readString();
        this.END1_TIME = in.readString();
    }
}

