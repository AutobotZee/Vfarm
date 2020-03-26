package com.example.vfarm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.time.Month;
import java.util.Calendar;

public class Schedule_act extends AppCompatActivity {

    public Button set_start_date;
    public Button set_start_time;

    public Button set_end_date;
    public Button set_end_time;

    public Button Save;

    public TextView disp_start_date;
    public TextView disp_start_time;

    public TextView disp_end_date;
    public TextView disp_end_time;


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

    set_start_date.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showdate(disp_start_date);
        }
    });
        set_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showtime(disp_start_time);
            }
        });

        set_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdate(disp_end_date);
            }
        });

        set_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showtime(disp_end_time);
            }
        });
    Save.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(),"Schedule saved",  Toast.LENGTH_SHORT).show();
        }
    });
    }




    // time setter functions

    private void showdate(final TextView T){

        Calendar calendar = Calendar.getInstance();


        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String s = dayOfMonth + " " + month + " " + year + " ";
                T.setText(s);
            }
        }, YEAR, MONTH, DATE);
        datePickerDialog.show();
    }
    private void showtime(final TextView T){

        Calendar calendar = Calendar.getInstance();


        int HOUR = calendar.get(Calendar.HOUR);
        int MIN = calendar.get(Calendar.MINUTE);


        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String s = hourOfDay + " " + minute;
                    T.setText(s);
            }
        }, HOUR, MIN, true);
        timePickerDialog.show();

    }
}
