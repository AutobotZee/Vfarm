package com.example.vfarm;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import com.example.vfarm.ui.main.MainFragment;

import android.os.Handler;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static int PERIOD = 2000;

    private BluetoothAdapter mBluetoothAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // flash welcome screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(getApplicationContext(), leScan.class);
                startActivity(homeIntent);
                finish(); }
        },PERIOD);
    }
}
