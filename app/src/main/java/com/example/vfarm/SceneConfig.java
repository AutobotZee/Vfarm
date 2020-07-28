package com.example.vfarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Handler;

import java.util.List;
import java.util.UUID;

public class SceneConfig extends AppCompatActivity  {

    private final static String TAG = SceneConfig.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String STARTDT = "StartDT";
    public static final String ENDDT = "EndDT";
    public static final String SCH_LIST_NAME = "sch_obj_list";
    public ArrayList<Record> Record_DFT = new ArrayList<>(); // TODO check usage

    public ArrayList<Schedule_Item> sch_list_list = new ArrayList<>();
    public int selected_pos;

    public ListView schedule_listview;
    public ArrayList<String> sch_name_list = new ArrayList<>();
    public ArrayList<Record> record_list = new ArrayList<Record>();
    public ArrayAdapter<String> listAdapter;

    public int counter = 0;

   // public ArrayAdapter listAdapter ;


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
    Schedule_Item running_sch = new Schedule_Item();
    public BluetoothGattCharacteristic characteristic1 ;
    public BluetoothGattCharacteristic characteristic2 ;
    public BluetoothGattCharacteristic characteristic3 ;

    public BluetoothGattCharacteristic characteristic_startdt ;
    public BluetoothGattCharacteristic characteristic_enddt ;
    public BluetoothGattCharacteristic characteristic_add1 ;
    public BluetoothGattCharacteristic characteristic_cmd1 ;

    public BluetoothGattCharacteristic characteristic_flag ;
    public BluetoothGattCharacteristic characteristic_SCH_ID ;
    public BluetoothGattCharacteristic characteristic_SCH_name ;
    public BluetoothGattCharacteristic characteristic_SCH_Active_status ;
    public BluetoothGattCharacteristic characteristic_SCH_rec_list_size ;

    public BluetoothGattCharacteristic characteristic_running_SCH_flag;
    public BluetoothGattCharacteristic characteristic_running_REC_flag;
    public BluetoothGattCharacteristic characteristic_SCH_running_reclist_size ;




    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    final Handler handler = new Handler();
    public Button ON;
    public Button OFF;
    public Button Send;
    public Button schdl;
    public Button send_recs;
    public Button read_recs;

    private EditText cmd;
    private EditText addr;

    public TextView disp_Start_dt;
    public TextView disp_end_dt;

    public ArrayList<Record> Record_LIST = new ArrayList<>();

    public Button Add_schd;

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


                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0)
                        {
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
        outState.putParcelableArrayList("Schedules", sch_list_list);
    }

    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState) {
        this.sch_list_list  = savedInstanceState.getParcelableArrayList("Schedules");
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.list_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId())
        {
            case R.id.send_menu_option:
                Schedule_Item selected_sch = sch_list_list.get(info.position);
                ArrayList<Record> r_list = selected_sch.record_list;
                boolean flag = false;
                mBluetoothLeService.writeCharacteristic(characteristic_SCH_name, selected_sch.Sch_name);
                try { Thread.sleep(400); } catch (InterruptedException e) { e.printStackTrace(); }

                mBluetoothLeService.writeCharacteristic(characteristic_SCH_ID, String.valueOf(selected_sch.ID));
                try { Thread.sleep(400); } catch (InterruptedException e) { e.printStackTrace(); }

                mBluetoothLeService.writeCharacteristic(characteristic_SCH_Active_status,String.valueOf(selected_sch.Active_status));
                try { Thread.sleep(400); } catch (InterruptedException e) { e.printStackTrace(); }

                mBluetoothLeService.writeCharacteristic(characteristic_SCH_rec_list_size,String.valueOf(selected_sch.record_list.size()));
                try { Thread.sleep(400); } catch (InterruptedException e) { e.printStackTrace(); }

                mBluetoothLeService.writeCharacteristic(characteristic_flag,"0");
                try { Thread.sleep(400); } catch (InterruptedException e) { e.printStackTrace(); }

                for(int k = 0; (k < r_list.size()) && ( r_list.size() != 0); k++)
                {
                    if(r_list.get(k) != null)
                    {
                        flag = writeRecord(r_list.get(k));
                    }
                }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_config);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);


        if (savedInstanceState != null) {
            // Then the application is being reloaded
            sch_list_list  = savedInstanceState.getParcelableArrayList("Schedules");
        }
        if(intent != null)
        {
            if(intent.hasExtra("position") & intent.hasExtra("Schedule"))
            {
                selected_pos = intent.getIntExtra("position", selected_pos);
                Schedule_Item sch_back = intent.getParcelableExtra("Schedule");
                sch_list_list.clear();
                Bundle data = getIntent().getExtras();
                sch_list_list = data.getParcelableArrayList("All_Schedule");
            }
        }

        // Sets up UI references.
//        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        schdl = findViewById(R.id.schedule_timer);

        setTitle(mDeviceName);
//      getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(getApplicationContext(), BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        // added a shortcut to connect
      //  mBluetoothLeService.connect(mDeviceAddress);

        // Design UI
        ON = (Button) findViewById(R.id.LED_on);
        OFF = (Button) findViewById(R.id.LED_off);
        Send = (Button) findViewById(R.id.Send);
        send_recs = (Button) findViewById(R.id.Send_recs);
        read_recs = (Button) findViewById(R.id.Read_recs);

        cmd = (EditText) findViewById(R.id.cmd);
        addr = (EditText) findViewById(R.id.Add);


 // Setting up function buttons

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
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.writeCharacteristic(characteristic_enddt, disp_end_dt.getText().toString());
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.writeCharacteristic(characteristic_add1, addr.getText().toString());
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.writeCharacteristic(characteristic_cmd1, cmd.getText().toString());
                Toast.makeText(getApplicationContext(),"DATA written", Toast.LENGTH_SHORT).show();

            }
        });


        Add_schd = (Button) findViewById(R.id.add_schedule);
        schedule_listview = (ListView) findViewById(R.id.Schedule_list);
        listAdapter = new ArrayAdapter<String>(SceneConfig.this,android.R.layout.simple_list_item_1, sch_name_list);
        schedule_listview.setAdapter(listAdapter);
        registerForContextMenu(schedule_listview);


        Add_schd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sch_list_list.add(new Schedule_Item(1, "Sch_name",record_list ));
                sch_name_list.clear();
                for(Schedule_Item item: sch_list_list)
                {
                    sch_name_list.add(item.Sch_name);
                }
                listAdapter.notifyDataSetChanged();

            }

        });


        send_recs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean flag = false;
                mBluetoothLeService.writeCharacteristic(characteristic_flag,"0");
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for(int k = 0; (k < record_list.size()) && ( record_list.size() != 0); k++)
               // for( k = 1; k < 2; k++)
                {
                    if(record_list.get(k) != null){
                    flag = writeRecord(record_list.get(k));
                }

                };
            }
        });


        read_recs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_bool_char(characteristic_running_SCH_flag, true);
                try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
                boolean flag_1 = read_bool_char(characteristic_running_SCH_flag);
                while( flag_1 == true )
                {
                    flag_1 = read_bool_char(characteristic_running_SCH_flag);
                }

                running_sch = readRunningSch();
                sch_list_list.add(running_sch);
            }
        });

        schedule_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(SceneConfig.this, Schedule_act.class);
                intent.putExtra("ScheduleItem", sch_list_list.get(position));
                intent.putExtra("position", position);
                intent.putExtra("All_Schedules",sch_list_list);
                startActivityForResult(intent,1);

                Toast.makeText(getBaseContext() ,"List data sent", Toast.LENGTH_LONG).show();
            }
        });

        // test profile for char
        if (characteristic_SCH_ID != null)
        { mBluetoothLeService.setCharacteristicNotification( characteristic_SCH_ID, false);
           // mNotifyCharacteristic = null;

            Toast.makeText(getBaseContext() ,"DONE CHANGES", Toast.LENGTH_LONG).show();
        }

    };

    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == -1) {
                Schedule_Item sch_back = data.getParcelableExtra("Schedule");
                int pos = data.getIntExtra("position", selected_pos);
                sch_list_list.remove(pos);
                sch_list_list.add(pos,sch_back);
                sch_name_list.clear();
                for(Schedule_Item item: sch_list_list)
                {
                    sch_name_list.add(item.Sch_name);
                }
                listAdapter.notifyDataSetChanged();

            }
            if (resultCode == 0) {
                //Write your code if there's no result
            }
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


        // Static initialization of Charactersitics based on GATT services Table

        characteristic1 = mGattCharacteristics.get(2).get(0); // Global CMD
        characteristic2 = mGattCharacteristics.get(3).get(0); // Global ADDRESS

        characteristic_startdt = mGattCharacteristics.get(4).get(3); // Record's StartDT
        characteristic_enddt = mGattCharacteristics.get(4).get(2); // Record's Énddt
        characteristic_add1 = mGattCharacteristics.get(4).get(1); // Record's ADDRESS
        characteristic_cmd1 = mGattCharacteristics.get(4).get(0); // Record's CMD

        characteristic_SCH_ID = mGattCharacteristics.get(5).get(4);
        characteristic_SCH_name = mGattCharacteristics.get(5).get(3);
        characteristic_SCH_Active_status = mGattCharacteristics.get(5).get(2);
        characteristic_flag = mGattCharacteristics.get(5).get(1); // Flag
        characteristic_SCH_rec_list_size = mGattCharacteristics.get(5).get(0);

        characteristic_running_SCH_flag = mGattCharacteristics.get(6).get(2) ;
        characteristic_running_REC_flag = mGattCharacteristics.get(6).get(1) ;
        characteristic_SCH_rec_list_size = mGattCharacteristics.get(6).get(0) ;

        // button description for On and OFF

        OFF.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mBluetoothLeService.writeCharacteristic(characteristic1, "255"); // global address
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.writeCharacteristic(characteristic2, "0");
            }
        });
        ON.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mBluetoothLeService.writeCharacteristic(characteristic1, "255");
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.writeCharacteristic(characteristic2, "5");
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

    public boolean writeRecord(Record sch){

        try { Thread.sleep(400); } catch (InterruptedException e) { e.printStackTrace(); }
        mBluetoothLeService.writeCharacteristic(characteristic_startdt,sch.getSTART_TIME());
        try { Thread.sleep(400); } catch (InterruptedException e) { e.printStackTrace(); }
        mBluetoothLeService.writeCharacteristic(characteristic_add1, sch.getADDRESS());
        try { Thread.sleep(400); } catch (InterruptedException e) { e.printStackTrace(); }
        mBluetoothLeService.writeCharacteristic(characteristic_cmd1, sch.getCMD());
        Toast.makeText(getApplicationContext(),"DATA written", Toast.LENGTH_SHORT).show();
        try { Thread.sleep(400); } catch (InterruptedException e) { e.printStackTrace(); }
        return true;
    }


    public Schedule_Item readRunningSch()
    {
            Schedule_Item schedule_item = new Schedule_Item();
            Record aux_rec;
            Log.i(TAG, String.format("READING SCHEDULE:"));
        //    mBluetoothLeService.readCharacteristic(characteristic_SCH_ID);
        //    try { Thread.sleep(400); } catch (InterruptedException e) { e.printStackTrace(); }
        //    String s = characteristic_SCH_ID.getStringValue(0);
            String s = "1";
            schedule_item.ID = Integer.parseInt(s.trim());

            mBluetoothLeService.readCharacteristic(characteristic_SCH_name);
            try { Thread.sleep(400); } catch (InterruptedException e) { e.printStackTrace(); }
            schedule_item.Sch_name= characteristic_SCH_name.getStringValue(0) + "(READ FROM DECIVE)";

            mBluetoothLeService.readCharacteristic(characteristic_SCH_Active_status);
            try { Thread.sleep(400); } catch (InterruptedException e) { e.printStackTrace(); }
            if(characteristic_SCH_Active_status.getStringValue(0) == "0")
                schedule_item.Active_status = false;
            if(characteristic_SCH_Active_status.getStringValue(0) == "1")
                schedule_item.Active_status = true;

            int size = 0;
            mBluetoothLeService.readCharacteristic(characteristic_SCH_rec_list_size);
            try { Thread.sleep(400); } catch (InterruptedException e) { e.printStackTrace();}
            if(characteristic_SCH_rec_list_size.getStringValue(0) == null)
            {
                Toast.makeText(getApplicationContext(),"RECORD LIST EMPTY, ESP not initialized", Toast.LENGTH_SHORT).show();
            }
            else{ size = Integer.parseInt(characteristic_SCH_rec_list_size.getStringValue(0).trim());}


            for(int i =0; i < size; i ++)
            {
                write_bool_char(characteristic_running_REC_flag, true);
                try { Thread.sleep(400); } catch (InterruptedException e) { e.printStackTrace(); }
                boolean flag2 = true;
                while (flag2 == true)
                {
                    flag2 = read_bool_char(characteristic_running_REC_flag);
                    try { Thread.sleep(400); } catch (InterruptedException e) { e.printStackTrace(); }
                    Log.i(TAG, String.format("Awaiting Records to be written:"));
                }
                aux_rec = readRunningSch_Record();
                schedule_item.record_list.add(aux_rec);
            }

            //Initialize the read flag for records

            Toast.makeText(getApplicationContext(),"READ Successful", Toast.LENGTH_SHORT).show();

            sch_name_list.add(schedule_item.Sch_name);
            listAdapter.notifyDataSetChanged();

            write_bool_char(characteristic_running_SCH_flag, false);
            return schedule_item;

    }

    public Record readRunningSch_Record()
    {   Log.i(TAG, String.format("READING RECORDS"));
        Record aux_rec = new Record();
        mBluetoothLeService.readCharacteristic(characteristic_startdt);
        aux_rec.START_TIME = characteristic_startdt.getStringValue(0);
        try { Thread.sleep(400); } catch (InterruptedException e) { e.printStackTrace(); }

        mBluetoothLeService.readCharacteristic(characteristic_add1);
        aux_rec.ADDRESS= characteristic_add1.getStringValue(0);
        try { Thread.sleep(400); } catch (InterruptedException e) { e.printStackTrace(); }

        mBluetoothLeService.readCharacteristic(characteristic_cmd1);
        aux_rec.CMD= characteristic_cmd1.getStringValue(0);
        Toast.makeText(getApplicationContext(),"READ Successful", Toast.LENGTH_SHORT).show();

        return aux_rec;

    }

    private boolean read_bool_char(BluetoothGattCharacteristic characteristic)
    {
        boolean flag = true;
        mBluetoothLeService.readCharacteristic(characteristic);
        String s = characteristic.getStringValue(0).trim();
        if( s.compareTo("0") == 0)
        {flag = true;}
        if(s.compareTo("1") == 0)
        {flag = false;}
        return flag;
    }

    private void write_bool_char(BluetoothGattCharacteristic characteristic, boolean flag)
    {
        if(flag == true)
        {
            mBluetoothLeService.writeCharacteristic(characteristic, "0");
        }
        if(flag == false)
        {
            mBluetoothLeService.writeCharacteristic(characteristic, "1");
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    private void updateConnectionState(final int resourceId) { runOnUiThread(new Runnable() {
        @Override
        public void run()
        {
            mConnectionState.setText(resourceId);
        }
    });
    }

}
