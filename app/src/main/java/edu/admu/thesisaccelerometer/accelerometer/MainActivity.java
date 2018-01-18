package edu.admu.thesisaccelerometer.accelerometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private List<Float> data_5s;
    private List<Long> time_5s;
    private String url="";
    long startTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        startTime = System.currentTimeMillis();

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

        if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];

            long curTime = System.currentTimeMillis();

            if(curTime-lastUpdate>50000){
                Map<Long, Float> params = new HashMap<>();
                for(int i=0; i<500; i++){
                    params.put(time_5s.get(i), data_5s.get(i));
                }
                JSONObject jsonObject = new JSONObject(params);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
                );
                Volley.newRequestQueue(this).add(jsonObjectRequest);

            }
            //checks every 10ms
            if ((curTime - lastUpdate) > 10) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float dis_x = (float) (last_x*diffTime*diffTime + 0.5*x*diffTime);
                float dis_y = (float) (last_y*diffTime*diffTime + 0.5*y*diffTime);

                float dis_xy = (float) (Math.sqrt(Math.pow(dis_x,2)+Math.pow(dis_y,2)));
                float speed_xy = (float) (Math.abs( Math.pow(x  - last_x,2) + Math.pow(y - last_y,2))* diffTime);

                TextView txv_x = (TextView) findViewById(R.id.txv_x);
                TextView txv_y = (TextView) findViewById(R.id.txv_y);
                TextView txv_speed_xy = (TextView) findViewById(R.id.txv_speed_xy);

                txv_x.setText(String.valueOf(x));
                txv_y.setText(String.valueOf(y));
                txv_speed_xy.setText(String.valueOf(speed_xy));

                data_5s.add(dis_xy);

                if(data_5s.size()>500){
                    data_5s.remove(0);
                }

                time_5s.add(curTime-startTime);
                if(time_5s.size()>500) time_5s.remove(0);


                last_x = x;
                last_y = y;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {


    }
}
