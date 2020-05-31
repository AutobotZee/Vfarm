package com.example.vfarm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.vfarm.ui.main.Schedule;

import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;

public class Schedule_act extends AppCompatActivity {
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
        setContentView(R.layout.activity_schedule_act);

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


    Save.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(),"Schedule saved",  Toast.LENGTH_SHORT).show();

            for(int i = 0; i<3; i++)
            {   Schedule schedule =  new Schedule("91","FF","1200","4560");
                sch_obj_list.add(schedule);
            }

            Intent resultIntent = new Intent();
            startdt= disp_start_date.getText() + " " + disp_start_time.getText();
            endtdt = disp_end_date.getText() + " " + disp_end_time.getText();
            resultIntent.putExtra("StartDT", startdt);
            resultIntent.putExtra("EndDT", endtdt);

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("sch_obj_list", sch_obj_list);
            //resultIntent.putExtra("result.content", bundle);
            resultIntent.putExtras(bundle);

            setResult(RESULT_OK, resultIntent);
            finish();
        }
    });
    }


    // time setter functions

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
