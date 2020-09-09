package com.example.vfarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class Schedule_act extends AppCompatActivity {
    private RecyclerView Record_recycler;
    private RecyclerView.Adapter mRecAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Schedule_Item data1 = null;
    ArrayList<Schedule_Item> All_schedule = new ArrayList<>();
    public int provided_pos;

    Calendar cc = Calendar.getInstance();
    int hour = cc.get(Calendar.HOUR_OF_DAY);
    int min = cc.get(Calendar.MINUTE);

    int dt = hour*100 + min;


    // TODO Add new layout elements and display the data
    // TODO write adaptor class for 2nd layout
    EditText textview;
    EditText sec_cmd;
    EditText sec_Address;
    EditText sec_StartDT;
    TextView sec_ID;
    Button rename;
    Button AddRec;
    Button SaveReturn;
    Button AutoGen;
    TextView R_int_text;
    TextView G_int_text;
    TextView B_int_text;
    TextView W_int_text;

    SeekBar R_seek;
    SeekBar G_seek;
    SeekBar B_seek;
    SeekBar W_seek;

    CheckBox chk_shelf1;
    CheckBox chk_shelf2;
    CheckBox chk_shelf3;
    CheckBox chk_shelf4;

    String sh1 = "0";
    String sh2 = "0";
    String sh3 = "0";
    String sh4 = "0";

    String cmdR = "000";
    String cmdG = "000";
    String cmdB = "000";
    String cmdW = "000";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_act);
        data1 = getData();

        textview = (EditText) findViewById(R.id.Sch_name);
        rename = (Button) findViewById(R.id.rename);
        AddRec = (Button) findViewById(R.id.AddRecord);
        SaveReturn =( Button) findViewById(R.id.SaveReturn);
        AutoGen = (Button) findViewById(R.id.Auto_generate_records);

        R_int_text = (TextView) findViewById(R.id.R_int_text);
        G_int_text = (TextView) findViewById(R.id.G_int_text);
        B_int_text = (TextView) findViewById(R.id.B_int_text);
        W_int_text = (TextView) findViewById(R.id.W_int_text);

        R_seek = (SeekBar) findViewById(R.id.R_int);
        G_seek = (SeekBar) findViewById(R.id.G_int);
        B_seek = (SeekBar) findViewById(R.id.B_int);
        W_seek = (SeekBar) findViewById(R.id.W_int);

        chk_shelf1 = (CheckBox) findViewById(R.id.chk_shelf1);
        chk_shelf2 = (CheckBox) findViewById(R.id.chk_shelf2);
        chk_shelf3 = (CheckBox) findViewById(R.id.chk_shelf3);
        chk_shelf4 = (CheckBox) findViewById(R.id.chk_shelf4);
        Chk_config();
        Record_recycler = findViewById(R.id.Sch_record_view);
        new ItemTouchHelper(rec_touch).attachToRecyclerView(Record_recycler);

        mLayoutManager = new LinearLayoutManager(this);
        mRecAdapter = new RecordAdapter(data1.record_list);

        Record_recycler.setLayoutManager(mLayoutManager);
        Record_recycler.setAdapter(mRecAdapter);

        sec_ID = findViewById(R.id.sec_ID);
        sec_cmd = findViewById(R.id.sec_CMD);
        sec_Address = findViewById(R.id.sec_Address);
        sec_StartDT = findViewById(R.id.sec_StartDT);

        setData(data1);


        Seek_config(); // configure the seekbar
        rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data1.Sch_name = textview.getText().toString();
                Toast.makeText(getApplicationContext(),"DATA written", Toast.LENGTH_SHORT).show();
            }
        });

        AddRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean exception = false;
                if( sec_StartDT.getText().toString().trim().isEmpty() )
                {
                    exception = true;
                }

                if( sec_Address.getText().toString().trim().isEmpty() )
                {
                    exception = true;
                }

                if( sec_cmd.getText().toString().trim().isEmpty()  )
                {
                    exception = true;
                }

                if( sec_cmd.getText().toString().trim().length() != 12 )
                {
                    exception = true;
                }

                for(Record item: data1.record_list)
                {
                    if(sec_StartDT.getText().toString().equals(item.getSTART_TIME()))
                    {
                        exception = true;
                    }

                    if( (Integer.parseInt(item.getSTART_TIME()) >= 2400) || (Integer.parseInt(item.getSTART_TIME()) <= 0) )
                    {
                        exception = true;
                    }

                }

                if(exception){
                    Toast.makeText(getApplicationContext(),"Start Time is colliding with an existing record's start time or is invalid", Toast.LENGTH_SHORT).show();
                }
                else {
                    Record rec = new Record(sec_cmd.getText().toString(), sec_Address.getText().toString(), sec_StartDT.getText().toString());
                    data1.record_list.add(rec);
                    // just accessing sorter through a record object
                    Record.sorter( data1.record_list);
                    mRecAdapter.notifyDataSetChanged();
                }

            }
        });
        SaveReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(Schedule_act.this, MainActivity.class);
                backIntent.putExtra("Schedule",data1);
                backIntent.putExtra("position", provided_pos);
                All_schedule.remove(provided_pos);
                All_schedule.add(provided_pos, data1);
                backIntent.putExtra("All_Schedule", All_schedule);
                setResult(RESULT_OK, backIntent);
                finish();
            }
        });

        AutoGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO make auto generate a record list here and delete all other initializations;

                data1.record_list.add(new Record("010090115189","1100",String.valueOf(dt+3)));
                data1.record_list.add(new Record("254254254254","1100",String.valueOf(dt+4)));
                data1.record_list.add(new Record("010080150211","1100",String.valueOf(dt+5)));
                data1.record_list.add(new Record("254254254254","1100",String.valueOf(dt+6)));
                mRecAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),"Test DATA written", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private Schedule_Item getData(){
        Schedule_Item data1 = null;
        if (getIntent().hasExtra("ScheduleItem")){
            data1 = getIntent().getParcelableExtra("ScheduleItem");
            provided_pos = getIntent().getIntExtra("position", provided_pos);
            All_schedule = getIntent().getParcelableArrayListExtra("All_Schedules");
        }else{
            Toast.makeText(this,"No data", Toast.LENGTH_LONG).show();
        }
        return data1;

    }

    private void setData(Schedule_Item sch){
        // TODO Write more
        textview.setText(sch.getSch_name());
        mRecAdapter.notifyDataSetChanged();
    }

    ItemTouchHelper.SimpleCallback rec_touch = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            data1.record_list.remove(viewHolder.getAdapterPosition());
            mRecAdapter.notifyDataSetChanged();
        }

    };

    private void Seek_config(){ // configure the Seekbar and Text vies accordingly

        R_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                R_int_text.setText("" + progress + "%");
                cmdR = digfix(255*progress/100);
                sec_cmd.setText(cmdR+cmdG+cmdB+cmdW);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        G_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                G_int_text.setText("" + progress + "%");
                cmdG = digfix(255*progress/100);
                sec_cmd.setText(cmdR+cmdG+cmdB+cmdW);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        B_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                B_int_text.setText("" + progress +"%");
                cmdB = digfix(255*progress/100);
                sec_cmd.setText(cmdR+cmdG+cmdB+cmdW);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        W_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                W_int_text.setText("" + progress +"%");
                cmdW = digfix(255*progress/100);
                sec_cmd.setText(cmdR+cmdG+cmdB+cmdW);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void Chk_config(){
        chk_shelf1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chk_shelf1.isChecked() == true)
                {
                    sh1 = "1";
                }
                else {sh1 = "0";
                }
                sec_Address.setText((sh1 +sh2 + sh3 +sh4).trim());
            }
        });

        chk_shelf2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chk_shelf2.isChecked() == true)
                {
                    sh2 = "1";
                }
                else {sh2 = "0";
                }
                sec_Address.setText((sh1 +sh2 + sh3 +sh4).trim());
            }
        });

        chk_shelf3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chk_shelf3.isChecked() == true)
                {
                    sh3 = "1";
                }
                else {sh3 = "0";
                }
                sec_Address.setText((sh1 +sh2 + sh3 +sh4).trim());
            }
        });
        chk_shelf4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chk_shelf4.isChecked() == true)
                {
                    sh4 = "1";
                }
                else {sh4 = "0";
                }
                sec_Address.setText((sh1 +sh2 + sh3 +sh4).trim());
            }
        });


    }
    private String digfix( int x)
    {
        String s = "0";
        if ((x >= 0) && (x < 10))
        {
            s = "00" + Integer.toString(x);
        }
        if ((x >= 10) && (x < 100))
        {
            s = "0" + Integer.toString(x);
        }
        if ((x > 100) && (x < 256))
        {
            s = Integer.toString(x).trim();
        }
        return s;
    }


}

