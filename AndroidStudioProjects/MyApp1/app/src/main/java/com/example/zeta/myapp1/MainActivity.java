package com.example.zeta.myapp1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.List;
import java.util.StringTokenizer;


public class MainActivity extends Activity implements Runnable, SensorEventListener {
    SensorManager sm;
    TextView tv;
    Handler h;
    float gx, gy, gz;
    double xyz;
    private Button buttonSave;

    private String WalkFileName = "walkdata.csv";
    private String StandFileName = "standdata.csv";
    private String RunFileName = "rundata.csv";
    private String arffFileName = "arfffile.arff";

    private int walkCount, standCount, runCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textView);

        /*LinearLayout l1 = new LinearLayout(this);
        setContentView(l1);

        tv = new TextView(this);
        l1.addView(tv);

        h = new Handler();
        h.postDelayed(this, 500);*/

        buttonSave = (Button) findViewById(R.id.StandSaveButton);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                standCount = 1;
            }
        });

        buttonSave = (Button) findViewById(R.id.WalkSaveButton);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walkCount = 1;
            }
        });

        buttonSave = (Button) findViewById(R.id.RunSaveButton);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runCount = 1;
            }
        });

        buttonSave = (Button) findViewById(R.id.FinishMeasureButton);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                standCount = 0;
                walkCount = 0;
                runCount = 0;
            }
        });

        buttonSave = (Button) findViewById(R.id.SaveArffButton);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveArffFile(arffFileName);
            }
        });

        buttonSave = (Button) findViewById(R.id.DeleteFiles);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFile(arffFileName);
                deleteFile(WalkFileName);
                deleteFile(StandFileName);
                deleteFile(RunFileName);
            }
        });

        buttonSave = (Button) findViewById(R.id.SendFileToWeka);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), weka.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void run() {

    }

    public void saveArffFile(String filename) {
        try {
            FileOutputStream files = openFileOutput(filename, Context.MODE_PRIVATE);
            String write = ("@relation file\n\n" +
                            "@attribute state {stand, walk, run}\n" +
                            "@attribute Acceleration real\n\n" +
                            "@data\n");
            files.write(write.getBytes());
            String writestand = readFile(StandFileName);
            files.write(writestand.getBytes());
            String writewalk = readFile(WalkFileName);
            files.write(writewalk.getBytes());
            String writerun = readFile(RunFileName);
            files.write(writerun.getBytes());
            files.flush();
            files.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SaveStandFile(String filename) {
        try {
            FileOutputStream files = openFileOutput(filename, Context.MODE_PRIVATE|MODE_APPEND);
            String write = ("stand," +Math.sqrt(xyz) + "\n");
            files.write(write.getBytes());
            files.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SaveWalkFile(String filename) {
        try {
            FileOutputStream files = openFileOutput(filename, Context.MODE_PRIVATE|MODE_APPEND);
            String write = ("walk," +Math.sqrt(xyz) + "\n");
            files.write(write.getBytes());
            files.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SaveRunFile(String filename) {
        try {
            FileOutputStream files = openFileOutput(filename, Context.MODE_PRIVATE|MODE_APPEND);
            String write = ("run," + Math.sqrt(xyz) + "\n");
            files.write(write.getBytes());
            files.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFile(String filename) {
        FileInputStream fileInputStream;
        String text = null;
        try {
            fileInputStream = openFileInput(filename);
            byte[] readBytes = new byte[fileInputStream.available()];
            fileInputStream.read(readBytes);
            text = new String(readBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors =
                sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(0 < sensors.size()) {
            sm.registerListener(this, sensors.get(0),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gx = event.values[0];
            gy = event.values[1];
            gz = event.values[2];
            xyz = gx*gx + gy*gy + gz*gz;
            String Tmp = "Acceleration Sensor\n"
                    + "x-axis:" + gx + "\n"
                    + "y-axis:" + gy + "\n"
                    + "z-axis:" + gz + "\n"
                    + "Synthetic Acceleration:" + Math.sqrt(xyz) + "\n";
            tv.setText(Tmp);

            if (standCount==1)
                SaveStandFile(StandFileName);
            if (walkCount==1)
                SaveWalkFile(WalkFileName);
            if (runCount==1)
                SaveRunFile(RunFileName);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
