package com.example.vfarm;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;


public class Record implements Parcelable {
    public String CMD;
    public String NAME;
    public String ADDRESS;
    public String START_TIME;

    public Record(String cmd, String add, String start_time){
        this.CMD = cmd;
        this.NAME = "NEW RECORD";
        this.ADDRESS = add;
        this.START_TIME = start_time;
    };

    public Record() {
        super();
        this.CMD = "00";
        this.NAME = "NEW RECORD";
        this.ADDRESS = "00";
        this.START_TIME = "00";
    }

    protected Record(Parcel in) {
        CMD = in.readString();
        NAME = in.readString();
        ADDRESS = in.readString();
        START_TIME = in.readString();
    }

    public static final Creator<Record> CREATOR = new Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel in) {
            return new Record(in);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };

    public static void sorter(ArrayList<Record> sch){
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(CMD);
        dest.writeString(NAME);
        dest.writeString(ADDRESS);
        dest.writeString(START_TIME);
    }

    public String getCMD() {
        return CMD;
    }

    public String getADDRESS() {
        return ADDRESS;
    }

    public String getNAME() {
        return NAME;
    }

    public String getSTART_TIME() {
        return START_TIME;
    }
}


