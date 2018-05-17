package pl.zimny0911.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    TextView mdegree;
    ImageView marrow;
    private static SensorManager msesnorManager;
    private Sensor mSensor, lightSensor;
    private float currentDegree;
    private static final int MIN_LUX_PAUSE = 5;
    private MediaPlayer mediaPlayer;
    boolean playing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mdegree=findViewById(R.id.degree);
        marrow=findViewById(R.id.arrow);
        msesnorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor=msesnorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        lightSensor=msesnorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mediaPlayer = MediaPlayer.create(this,R.raw.pirates);

    }



    @Override
    protected void onResume() {
        super.onResume();
        if (lightSensor!=null) {
            msesnorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }else{
            Toast.makeText(MainActivity.this,"Light Sensor Not Supported!", Toast.LENGTH_SHORT).show();
        }

        if (mSensor!=null){
            msesnorManager.registerListener(this,mSensor,SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        }else{
            Toast.makeText(MainActivity.this,"Sensor Not Supported!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        msesnorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int degree = Math.round(event.values[0]);
        mdegree.setText(Integer.toString(degree) + (char) 0x00B0);
        RotateAnimation rotateAnimation = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setFillAfter(true);
        marrow.setAnimation(rotateAnimation);
        currentDegree = -degree;

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            Float lux = event.values[0];


            if (lux < MIN_LUX_PAUSE && !playing) {
                mediaPlayer.start();
                playing = true;
            } else if (lux > MIN_LUX_PAUSE && playing) {
                mediaPlayer.pause();
                playing = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
