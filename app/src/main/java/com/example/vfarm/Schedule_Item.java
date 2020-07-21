package com.example.vfarm;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

public class Schedule_Item implements Parcelable {
    int ID;
    String Sch_name;
    // Date time_of_creation;
    Boolean Active_status;
    ArrayList<Record> record_list;

    public Schedule_Item(int  ID, String Sch_name, ArrayList<Record> record_list){
        this.ID = ID;
        this.Sch_name = Sch_name;
        //   this.time_of_creation = ct;
        this.Active_status = false;
        this.record_list = record_list;

    };

    public Schedule_Item(){
        this.ID = 0;
        this.Sch_name = "Sch_name";
        //   this.time_of_creation = ct;
        this.Active_status = false;
        this.record_list =  new ArrayList<Record>();

    };

    protected Schedule_Item(Parcel in) {
        ID = in.readInt();
        Sch_name = in.readString();
        byte tmpActive_status = in.readByte();
        Active_status = tmpActive_status == 0 ? null : tmpActive_status == 1;
        record_list = in.createTypedArrayList(Record.CREATOR);
    }

    public static final Creator<Schedule_Item> CREATOR = new Creator<Schedule_Item>() {
        @Override
        public Schedule_Item createFromParcel(Parcel in) {
            return new Schedule_Item(in);
        }

        @Override
        public Schedule_Item[] newArray(int size) {
            return new Schedule_Item[size];
        }
    };

    public int getID(){return ID;}
    public String getSch_name(){return Sch_name;}
    public Boolean getActive_status(){return Active_status;}
    public ArrayList<Record> getRecord_list(){return record_list;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(Sch_name);
        dest.writeByte((byte) (Active_status == null ? 0 : Active_status ? 1 : 2));
        dest.writeTypedList(record_list);
    }
}