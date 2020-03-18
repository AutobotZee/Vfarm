package com.example.vfarm;

import java.util.Timer;
import java.util.Date;
import java.util.UUID;

import io.objectbox.annotation.*;

@Entity
public class SceneClass {
    @Id public long id;
    int T_start;
    int T_end;
    String logical_rack_id;
    String rack_GUID;
    String logical_shelf_id;
    String shelf_GUID;
    Date date;

    int time;
    byte Attribute1;
    byte Attribute2;
    int Light_Level_ch1;
    int Light_Level_ch2;
    int Light_Level_ch3;
    byte Rack_mode;


}
