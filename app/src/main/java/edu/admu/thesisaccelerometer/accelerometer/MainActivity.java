package edu.admu.thesisaccelerometer.accelerometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        System.out.print("hello world");

    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_FASTEST);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor mySensor = sensorEvent.sensor;
        float hispeed = -100;

        if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();


            //checks every 100ms
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed_xy = (float) (Math.abs( Math.pow(x  - last_x,2) + Math.pow(y - last_y,2))/ diffTime * 10000);

                TextView txv_x = (TextView) findViewById(R.id.txv_x);
                TextView txv_y = (TextView) findViewById(R.id.txv_y);
                TextView txv_z = (TextView) findViewById(R.id.txv_z);
                TextView txv_speed_xy = (TextView) findViewById(R.id.txv_speed_xy);
                TextView txv_hispeed_xy = (TextView) findViewById(R.id.txv_hispeed_xy);

                txv_x.setText(String.valueOf(x));
                txv_y.setText(String.valueOf(y));
                txv_z.setText(String.valueOf(z));
                txv_speed_xy.setText(String.valueOf(speed_xy));

                    if(hispeed<speed_xy)
                {
                    hispeed = speed_xy;
                        txv_hispeed_xy.setText(String.valueOf(hispeed));
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {


    }
}
