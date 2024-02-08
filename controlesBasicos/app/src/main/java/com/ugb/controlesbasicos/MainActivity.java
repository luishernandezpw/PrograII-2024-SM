package com.ugb.controlesbasicos;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    SensorManager sensorManager;
    Sensor sensor;
    SensorEventListener sensorEventListener;
    TextView tempVal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tempVal = findViewById(R.id.lblSensorAcelerometro);
        acvtivarSensorAcelerometro();
    }
    @Override
    protected void onResume() {
        inicar();
        super.onResume();
    }
    @Override
    protected void onPause() {
        detener();
        super.onPause();
    }

    private void acvtivarSensorAcelerometro(){
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(sensor==null){
            tempVal.setText("Tu dispositivo NO cuenta con el sensor acelerometro.");
            finish();
        }
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                tempVal.setText("Acelerometro: X="+ sensorEvent.values[0]+ "; Y="+ sensorEvent.values[1] +"; Z="+
                        sensorEvent.values[2]);
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }
    private void inicar(){
        sensorManager.registerListener(sensorEventListener, sensor,2000*1000 );
    }
    private void detener(){
        sensorManager.unregisterListener(sensorEventListener);
    }
}