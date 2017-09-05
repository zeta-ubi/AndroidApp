package com.example.zeta.myapp1;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import android.view.View;

import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.trees.J48;
import weka.core.converters.Loader;

public class weka extends Activity implements Runnable, SensorEventListener {
    SensorManager sm;
    private TextView tv;
    float gx, gy, gz;
    int walkCount = 0;
    double xyz, Rxyz;
    private Button returnButton;
    J48 j48;
    Instances instances;
    boolean flagOfMadej48 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        tv = (TextView) findViewById(R.id.text_view);
        flagOfMadej48 = false;

        Button returnButton = (Button) findViewById(R.id.return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flagOfMadej48 = false;
                finish();
            }
        });

        CreateJ48();
    }

    public void CreateJ48() {
        try {
            DataSource source = new DataSource("data/data/com.example.zeta.myapp1/files/arfffile.arff");
            instances = source.getDataSet();
            instances.setClassIndex(0);
            j48 = new J48();
            j48.buildClassifier(instances);

            flagOfMadej48 = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gx = event.values[0];
            gy = event.values[1];
            gz = event.values[2];
            xyz = gx * gx + gy * gy + gz * gz;
            Rxyz = Math.sqrt(xyz);


            Evaluation eval = null;

            if (flagOfMadej48) {
                try {
                    //eval = new Evaluation(instances);
                    //eval.evaluateModel(j48, instances);
                    //System.out.println(eval.toSummaryString());

                    /*
                    FastVector out = new FastVector(3);
                    out.addElement("stand");
                    out.addElement("walk");
                    out.addElement("run");
                    Attribute state = new Attribute("state", out, 0);
                    */
                    Attribute acceleration = new Attribute("Acceleration", 1);

                    Instance instance = new DenseInstance(2);
                    instance.setValue(acceleration, Rxyz);
                    instance.setDataset(instances);

                    double result = j48.classifyInstance(instance);

                    if (result == 0.0)
                        tv.setText("Stopping");
                    if (result == 1.0) {
                        tv.setText("Walking");
                        walkCount++;
                    }


                    if (walkCount >= 50) {
                        setScreenSub();
                    }


                    System.out.println(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void setScreenSub() {
        setContentView(R.layout.warning);
        returnButton = (Button) findViewById(R.id.return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                walkCount = 0;
                finish();
            }
        });
    }

    @Override
    public void run() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors =
                sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (0 < sensors.size()) {
            sm.registerListener(this, sensors.get(0),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
