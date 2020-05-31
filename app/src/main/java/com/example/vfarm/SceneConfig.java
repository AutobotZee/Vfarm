package com.example.vfarm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.os.Handler;

import com.example.vfarm.ui.main.Schedule;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;


import io.objectbox.Box;

public class SceneConfig extends AppCompatActivity  {

    private final static String TAG = SceneConfig.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String STARTDT = "StartDT";
    public static final String ENDDT = "EndDT";
    public static final String SCH_LIST_NAME = "sch_obj_list";
    public ArrayList<Schedule> SCHEDULE_DFT = new ArrayList<>();



    private TextView mConnectionState;
    private String mDeviceName;
    private String mDeviceAddress;

    private String StartDT;
    private String EndDT;

    private ExpandableListView mGattServicesList;
    public BluetoothLeService mBluetoothLeService;
    public ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    // characteristics description
    public UUID id = UUID.fromString("8ab94001-9bcf-11e8-98d0-529269fb1459");

    public BluetoothGattCharacteristic characteristic1 ;
    public BluetoothGattCharacteristic characteristic2 ;
    public BluetoothGattCharacteristic characteristic3 ;
    public BluetoothGattCharacteristic characteristic_startdt ;
    public BluetoothGattCharacteristic characteristic_enddt ;
    public BluetoothGattCharacteristic characteristic_add1 ;
    public BluetoothGattCharacteristic characteristic_cmd1 ;


    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    final Handler handler = new Handler();

    private Button sc1;
    private Button sc2;
    private Button sc3;
    private Button sc4;
    public Button save_scene;
    public Button ON;
    public Button OFF;
    public Button Send;
    public Button schdl;
    public Button send_recs;

    private CheckedTextView shelf_select_1;
    private CheckedTextView shelf_select_2;
    private CheckedTextView shelf_select_3;
    private CheckedTextView shelf_select_4;
    private TextView text_box1;
    private TextView text_box2;
    private TextView text_box3;
    private EditText cmd;
    private EditText addr;

    public TextView disp_Start_dt;
    public TextView disp_end_dt;

    private Button scheduleButton;
    private SeekBar seek_ch1;
    private SeekBar seek_ch2;
    private SeekBar seek_ch3;
    private int scenecount = 0;

    private String shelf_add = "00";

    public ArrayList<Schedule> SCHEDULE_LIST = new ArrayList<>();


    // Note box variables
    private Box<SceneClass> SceneBox;
    public int year_val;
    public int month_val;
    public int day_val;
    public int hour_val;
    public int min_val;

    public String startdt;
    public String startd;
    public String startt;
    public String endtdt;
    public String endd;
    public String endt;

    public Button set_start_date;
    public Button set_start_time;

    public Button set_end_date;
    public Button set_end_time;

    public Button Save;
    public Button Add_schd;

    public TextView disp_start_date;
    public TextView disp_start_time;

    public TextView disp_end_date;
    public TextView disp_end_time;

    public EditText CMD;
    public EditText ADDRESS;

    public ListView schedule_listview;
    public ArrayList<String> sch_list= new ArrayList<>();
    public ArrayList<Schedule> sch_obj_list = new ArrayList<Schedule>();
    public ArrayAdapter<String> adapter;

    public int counter = 1;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new  BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));

            }
        }
    };

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.

    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic = mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        // String temp = characteristic.getUuid().toString();
                        // saving done

                        characteristic1 = mGattCharacteristics.get(2).get(0);
                        characteristic2 = mGattCharacteristics.get(1).get(0);


                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null)
                            { mBluetoothLeService.setCharacteristicNotification( mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(
                                    characteristic, true);
                        }
                        return true;
                    }
                    return false;
                }
            };



    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
    }
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("Sch_obj_list", sch_obj_list); //I assume you want to mPersonList
    }

    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState) {
        this.sch_obj_list = savedInstanceState.getParcelableArrayList("Sch_obj_list"); //on coming back retrieve all values using key
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_config);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        ObjectBox.init(this);

        // Sets up UI references.
//        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        schdl = findViewById(R.id.schedule_timer);


       // getActionBar().setTitle(mDeviceName);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(getApplicationContext(), BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        // TODO get Box for SceneClass
        SceneBox = ObjectBox.get().boxFor(SceneClass.class);
        // added a shortcut to connect
      //  mBluetoothLeService.connect(mDeviceAddress);

        // Design UI
        sc1 = (Button) findViewById(R.id.Scene1);
        sc2 = (Button) findViewById(R.id.Scene2);
        sc3 = (Button) findViewById(R.id.Scene3);
        sc4 = (Button) findViewById(R.id.Scene4);
        scheduleButton = (Button) findViewById(R.id.schedule);
        save_scene = (Button) findViewById(R.id.save_scene);
        ON = (Button) findViewById(R.id.LED_on);
        OFF = (Button) findViewById(R.id.LED_off);
        Send = (Button) findViewById(R.id.Send);

        send_recs = (Button) findViewById(R.id.Send_recs);


        seek_ch1 = (SeekBar) findViewById(R.id.seekBar1);
        seek_ch2 = (SeekBar) findViewById(R.id.seekBar2);
        seek_ch3 = (SeekBar) findViewById(R.id.seekBar3);

        shelf_select_1 = (CheckedTextView) findViewById(R.id.shelf_1);
        shelf_select_2 = (CheckedTextView) findViewById(R.id.shelf_2);
        shelf_select_3 = (CheckedTextView) findViewById(R.id.shelf_3);
        shelf_select_4 = (CheckedTextView) findViewById(R.id.shelf_4);

        text_box1 = (TextView) findViewById(R.id.channel_1_value);
        text_box2 = (TextView) findViewById(R.id.channel_2_value);
        text_box3 = (TextView) findViewById(R.id.channel_3_value);
        cmd = (EditText) findViewById(R.id.cmd);
        addr = (EditText) findViewById(R.id.Add);

        disp_Start_dt = (TextView) findViewById(R.id.StartDateTime);
        disp_end_dt = (TextView) findViewById(R.id.EndDateTime);

 // Setting up function buttons


        shelf_select_1.setOnClickListener(new View.OnClickListener()
        {   @Override
            public void onClick(View v) {
                check(shelf_select_1);
            }
        });

        shelf_select_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check(shelf_select_2);
            }
        });

        shelf_select_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check(shelf_select_3);
            }
        });

        shelf_select_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check(shelf_select_4);
            }
        });

        // Seekbar functions
        seek_val(seek_ch1, text_box1);
        seek_val(seek_ch2, text_box2);
        seek_val(seek_ch3, text_box3);

        // Save scene Button saves a scene type of object in objectBox
        save_scene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SceneClass sc = new SceneClass();
                sc.id = scenecount; // object box store id saved by clicking scene buttons
                sc.Light_Level_ch1 = seek_ch1.getProgress();
                sc.Light_Level_ch2 = seek_ch2.getProgress();
                sc.Light_Level_ch3 = seek_ch3.getProgress();
                SceneBox.put(sc);
                sc.Attribute2= 0x0000000;


            }
        });


        sc1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scenecount = 1;
                if(SceneBox.get(scenecount)==null){
                    seek_ch1.setProgress(0);
                    seek_ch2.setProgress(0);
                    seek_ch3.setProgress(0);
                    Toast.makeText(getApplicationContext(),"Scene Not Configured", Toast.LENGTH_SHORT).show();

                }
                else {
                    SceneClass sc = SceneBox.get(scenecount);
                    seek_ch1.setProgress(sc.Light_Level_ch1);
                    seek_ch2.setProgress(sc.Light_Level_ch2);
                    seek_ch3.setProgress(sc.Light_Level_ch3);
                    Toast.makeText(getApplicationContext(),"Scene Loaded", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scenecount = 2;
                if(SceneBox.get(scenecount)==null){
                    seek_ch1.setProgress(0);
                    seek_ch2.setProgress(0);
                    seek_ch3.setProgress(0);
                    Toast.makeText(getApplicationContext(),"Scene Not Configured", Toast.LENGTH_SHORT).show();

                }
                else {
                    SceneClass sc = SceneBox.get(scenecount);
                    seek_ch1.setProgress(sc.Light_Level_ch1);
                    seek_ch2.setProgress(sc.Light_Level_ch2);
                    seek_ch3.setProgress(sc.Light_Level_ch3);
                    Toast.makeText(getApplicationContext(),"Scene Loaded", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sc3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scenecount = 3;
                if(SceneBox.get(scenecount)==null){
                    seek_ch1.setProgress(0);
                    seek_ch2.setProgress(0);
                    seek_ch3.setProgress(0);
                    Toast.makeText(getApplicationContext(),"Scene Not Configured", Toast.LENGTH_SHORT).show();

                }
                else {
                    SceneClass sc = SceneBox.get(scenecount);
                    seek_ch1.setProgress(sc.Light_Level_ch1);
                    seek_ch2.setProgress(sc.Light_Level_ch2);
                    seek_ch3.setProgress(sc.Light_Level_ch3);
                    Toast.makeText(getApplicationContext(),"Scene Loaded", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sc4.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scenecount = 4;
                if(SceneBox.get(scenecount)==null){
                    seek_ch1.setProgress(0);
                    seek_ch2.setProgress(0);
                    seek_ch3.setProgress(0);
                    Toast.makeText(getApplicationContext(),"Scene Not Configured", Toast.LENGTH_SHORT).show();

                }
                else {
                    SceneClass sc = SceneBox.get(scenecount);
                    seek_ch1.setProgress(sc.Light_Level_ch1);
                    seek_ch2.setProgress(sc.Light_Level_ch2);
                    seek_ch3.setProgress(sc.Light_Level_ch3);
                    Toast.makeText(getApplicationContext(),"Scene Loaded", Toast.LENGTH_SHORT).show();
                }
            }
        });

        scheduleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SceneConfig.this, Schedule_act.class);
                startActivityForResult(intent, 1);
            }
        });


        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cmd.getText()!=null && addr.getText()!=null){
                    mBluetoothLeService.writeCharacteristic(characteristic1, addr.getText().toString());
                    String com = cmd.getText().toString();
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mBluetoothLeService.writeCharacteristic(characteristic2, com);

                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"CMD or ADD empty", Toast.LENGTH_SHORT).show();
                }
                    ;
            }
        });

        schdl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeService.writeCharacteristic(characteristic_startdt, disp_Start_dt.getText().toString());
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.writeCharacteristic(characteristic_enddt, disp_end_dt.getText().toString());
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.writeCharacteristic(characteristic_add1, addr.getText().toString());
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.writeCharacteristic(characteristic_cmd1, cmd.getText().toString());
                Toast.makeText(getApplicationContext(),"DATA written", Toast.LENGTH_SHORT).show();

            }
        });


        set_start_date = (Button) findViewById(R.id.set_start_date);
        set_start_time = (Button) findViewById(R.id.set_start_time);
        set_end_date = (Button) findViewById(R.id.set_end_date);
        set_end_time = (Button) findViewById(R.id.set_end_time);
        disp_start_date = (TextView) findViewById(R.id.set_start_date_disp);
        disp_end_date = (TextView) findViewById(R.id.set_end_date_disp);
        disp_start_time = (TextView) findViewById(R.id.set_start_time_disp);
        disp_end_time = (TextView) findViewById(R.id.set_end_time_disp);
        Save = (Button) findViewById(R.id.save);
        Add_schd = (Button) findViewById(R.id.add_schedule);
        schedule_listview = (ListView) findViewById(R.id.Schedule_list);

        CMD = (EditText) findViewById(R.id.CMD_TX);
        ADDRESS = (EditText) findViewById(R.id.Address_TX);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, sch_list);
        schedule_listview.setAdapter(adapter);


        Add_schd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(counter==0){
                    for(int i = 0; i<3; i++)
                    {   Schedule schedule =  new Schedule("91","FF","1200","4560");
                        sch_obj_list.add(schedule);
                        schedule.sorter(sch_obj_list);
                        counter = counter+1;
                        sch_list.clear();
                    }}

                for(Schedule s: sch_obj_list)
                { sch_list.add(s.NAME + ": " + s.START_TIME); }
                adapter.notifyDataSetChanged();

                Schedule sch = new Schedule(CMD.getText().toString(), ADDRESS.getText().toString(), disp_start_time.getText().toString(), disp_end_time.getText().toString());
                sch.NAME = sch.NAME + Integer.toString(counter);

                if(counter<6)
                {
                    sch_obj_list.add(sch);
                    sch.sorter(sch_obj_list);
                    counter = counter + 1;
                }
                else{
                    Toast.makeText(getApplicationContext(),"Schedule list full",  Toast.LENGTH_SHORT).show();
                }

                // Displaying the sorted list names
                sch_list.clear();
                for(Schedule s: sch_obj_list)
                { sch_list.add(s.NAME + ": " + s.START_TIME); }
                adapter.notifyDataSetChanged();
            }

        });

        set_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdate(disp_start_date, startd);
            }
        });
        set_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showtime(disp_start_time, startt);

            }
        });

        set_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdate(disp_end_date, endd);
            }
        });

        set_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showtime(disp_end_time, endt);
            }
        });


        send_recs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = false;
                int k ;
                for( k = 0; (k < sch_obj_list.size()-1) && ( sch_obj_list.size() != 0); k++)
               // for( k = 1; k < 2; k++)
                {
                    if(sch_obj_list.get(k) != null){
                    flag = writeSch(sch_obj_list.get(k));
                    // Write the objects to ESP
                }

                };
            }
        });

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK){

            StartDT = data.getStringExtra(STARTDT);
            EndDT = data.getStringExtra(ENDDT);

           // Bundle bun = data.getBundleExtra("result.content");
            Bundle bun = data.getExtras();
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(),"Bundle loaded", Toast.LENGTH_SHORT).show();

            //ArrayList<Schedule> SCHEDULE_DFT_new = bun.getParcelableArrayList("sch_obj_list");
                assert bun != null;
                SCHEDULE_DFT = bun.getParcelableArrayList("sch_obj_list");
           // SCHEDULE_DFT = this.getIntent().getParcelableArrayListExtra("sch_obj_list");

                disp_Start_dt.setText(StartDT);
                disp_end_dt.setText(EndDT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }

        Log.d(TAG, "OnResume done"  );

        // mGattUpdate called here which in turns call Broadcast update which finally calls display gatt
        // mGattCharacteristics can be called here


    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Aditional functions for app
    public void openSchedule()
    {
        Intent intent = new Intent(SceneConfig.this, Schedule_act.class);
        startActivityForResult(intent, 1);
    }

    public void check( CheckedTextView b)
    {
        Intent intent = new Intent(this, Schedule_act.class);
        if(b.isChecked())
        {
            b.setChecked(false);
            b.setTextColor(Color.GRAY);
        }
        else{
            b.setChecked(true);
            b.setTextColor(Color.RED);
        }
    }

    public void seek_val(SeekBar seek, final TextView tbox)
    {
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tbox.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }


        // Usa

        characteristic1 = mGattCharacteristics.get(2).get(0);
        characteristic2 = mGattCharacteristics.get(3).get(0);
        characteristic_startdt = mGattCharacteristics.get(4).get(3);
        characteristic_enddt = mGattCharacteristics.get(4).get(2);
        characteristic_add1 = mGattCharacteristics.get(4).get(1);
        characteristic_cmd1 = mGattCharacteristics.get(4).get(0);


        // button description for On and OFF

        OFF.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mBluetoothLeService.writeCharacteristic(characteristic1, "FF"); // global address
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.writeCharacteristic(characteristic2, "00");
            }
        });
        ON.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mBluetoothLeService.writeCharacteristic(characteristic1, "FF");
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.writeCharacteristic(characteristic2, "A1");
            }
        });

        // code to set voltage on the board.

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mGattServicesList.setAdapter(gattServiceAdapter);
    }

    public boolean writeSch(Schedule sch){
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mBluetoothLeService.writeCharacteristic(characteristic_startdt,sch.getSTART_TIME());
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mBluetoothLeService.writeCharacteristic(characteristic_enddt, sch.getEND1_TIME());
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mBluetoothLeService.writeCharacteristic(characteristic_add1, sch.getADDRESS());
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mBluetoothLeService.writeCharacteristic(characteristic_cmd1, sch.getCMD());
        Toast.makeText(getApplicationContext(),"DATA written", Toast.LENGTH_SHORT).show();
        return true;
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void showdate(final TextView T, String date){

        Calendar calendar = Calendar.getInstance();


        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                year_val = year;
                month_val = month;
                day_val = dayOfMonth;
                String dayval = Integer.toString(day_val);
                String monthval = Integer.toString(month_val);
                if(day_val<10){dayval = "0"+ Integer.toString(day_val); };
                if(month_val<10){monthval = "0"+ Integer.toString(month_val);}

                String s = dayval + "C" + monthval + "C" + year;
                T.setText(s);
            }
        }, YEAR, MONTH, DATE);
        datePickerDialog.show();
    }
    private void showtime(final TextView T, String time){

        Calendar calendar = Calendar.getInstance();


        int HOUR = calendar.get(Calendar.HOUR);
        int MIN = calendar.get(Calendar.MINUTE);


        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                hour_val = hourOfDay;
                min_val = minute;
                String hourval = Integer.toString(hour_val);
                String minval = Integer.toString(min_val);
                if(hourOfDay<10){ hourval = "0"+hour_val;}
                if(min_val<10){ minval = "0" + min_val;}
                String s = hourval+minval;
                T.setText(s);
            }
        }, HOUR, MIN, true);
        timePickerDialog.show();

    }



}
