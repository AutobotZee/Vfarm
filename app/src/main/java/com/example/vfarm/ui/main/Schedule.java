package com.example.vfarm.ui.main;

import java.util.ArrayList;
import java.util.Collections;

public class Schedule {
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
}

