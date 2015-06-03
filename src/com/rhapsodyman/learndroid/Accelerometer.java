package com.rhapsodyman.learndroid;

import java.util.Timer;
import java.util.TimerTask;

import com.rhapsodyman.learndroid.Bluetooth.ConnectedThread;
import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Accelerometer extends Activity {

	private ConnectedThread connection;
	private SensorManager sensorManager;
	private Sensor sensorAccel;
	private Timer timer;
	private float[] valuesGravity = new float[2];
	private int[] PWM = new int[2];
	private int rotation = 0;
	private int speed = 0;
	private SharedPreferences appPreferences;
	private int min_value;
	private int interval;
	private boolean started = false;
	private ImageButton image;
	private TextView text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accelerometer);
		
		//this.getActionBar().hide();
		
		
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		text = (TextView) findViewById(R.id.startorstop);
		image = (ImageButton) findViewById(R.id.button);

		image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (started == true) {
				    stop();
					image.setImageResource(R.drawable.start);
					text.setText("Start Accelerometer");
					started = false;

				} else {
					start();
					image.setImageResource(R.drawable.stop);
					text.setText("Stop Accelerometer");
					started = true;
				}
			}
		});

		appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		min_value = Integer.parseInt(appPreferences.getString("Min_PWM", null));
		interval = Integer.parseInt(appPreferences.getString("interval", null));

	connection = Bluetooth.getConnection();
	if (connection == null)
			toast("Couldn't get connecton");
	}

	@Override
	public void onPause() {
		super.onPause();
		stop();
	}

	public void stop() {
		if (connection != null)
			connection.write("L0R0w");
		sensorManager.unregisterListener(listener);
		if (timer!=null )timer.cancel();
	}

	public void start() {
		sensorManager.registerListener(listener, sensorAccel,
				SensorManager.SENSOR_DELAY_GAME);

		timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				calculatePWM();
				connection.write(prepareMessage());
			}
		};
		timer.schedule(task, 0, interval);
	}

	public String prepareMessage() {

		return String.format("L%dR%dw", PWM[0], PWM[1]);

	}

	SensorEventListener listener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				for (int i = 0; i < 2; i++) {
						valuesGravity[i] = (float) (0.1 * event.values[i] + 0.9 * valuesGravity[i]);

				}

			}
		}
	};

	public void calculatePWM() {
		speed = (int) (255 * (Math.abs(valuesGravity[0]) / 9.8));
		rotation = (int) (255 * (Math.abs(valuesGravity[1]) / 9.8));

		if (rotation > speed)
			rotation = speed;

		if ((valuesGravity[0] < 0) && (valuesGravity[1] < 0)) {
			PWM[0] = speed;
			PWM[1] = speed - rotation;
		} else if ((valuesGravity[0] < 0) && (valuesGravity[1] > 0)) {
			PWM[0] = speed - rotation;
			PWM[1] = speed;

		} else if ((valuesGravity[0] > 0) && (valuesGravity[1] < 0)) {

			PWM[0] = -speed;
			PWM[1] = -(speed - rotation);
		}

		else if ((valuesGravity[0] > 0) && (valuesGravity[1] > 0)) {
			PWM[0] = -(speed - rotation);
			PWM[1] = -speed;

		}

		for (int i = 0; i < 1; i++) {
			if (Math.abs(PWM[i]) < min_value)
				PWM[i] = 0;
		}
	}

	public void toast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
				.show();
	}

}
