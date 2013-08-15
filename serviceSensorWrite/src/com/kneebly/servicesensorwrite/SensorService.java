package com.kneebly.servicesensorwrite;

import java.io.BufferedWriter;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Timer;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class SensorService extends IntentService implements SensorEventListener {
    private SensorManager mSensorManager = null;
 // accelerometer vector
 public float[] accel = { 0.0f, 0.0f, 0.0f };
 public float[] grav= { 0.0f, 0.0f, 0.0f };
 Timer timeUpdateAc;
 private long delay = 2000;
 // private long period=100;
 DecimalFormat d = new DecimalFormat("#.##");
 private LocationManager locationManager;
 private LocationListener locationListener;
 private double[] nowLoc = { 0.0d, 0.0d };
 private float gpsAcc = 0;
 BufferedWriter buf;

	public SensorService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		//start or stop recording
		String actionString=intent.getDataString();
		if (actionString=="Start"){
			startWriting();
		}
		else if (actionString=="Stop"){
			stopWriting();
		}
		else{
			Log.w("kneebler", "Received weird string: " + actionString );
		}
		
			
			
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    Toast.makeText(getApplicationContext(), "service starting", Toast.LENGTH_SHORT).show();
	    return super.onStartCommand(intent,flags,startId);
	}
	
	 public void startWriting(){
	    	String fileName=Long.toString(System.currentTimeMillis());
	    	 String filePath =Environment.getExternalStoragePublicDirectory(
	 	            Environment.DIRECTORY_DOWNLOADS)+"/kneebly";
	    	File file = new File(filePath,fileName);
	    	file.mkdirs();
	 	    Toast.makeText(getApplicationContext(), file.getAbsolutePath(), Toast.LENGTH_LONG).show();

	    }
	 public void stopWriting(){
	 	    Toast.makeText(getApplicationContext(), "stopped writing", Toast.LENGTH_LONG).show();

	 }
	//***--------------- Sensor functions--------------------------------
	    
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
		}
		public void onSensorChanged(SensorEvent event) {
			// copy new accelerometer data into accel array
			switch(event.sensor.getType()) {
		    case Sensor.TYPE_ACCELEROMETER:
			System.arraycopy(event.values, 0, accel, 0, 3);
		    case Sensor.TYPE_GRAVITY:
			System.arraycopy(event.values, 0, grav, 0, 3);

			}
		}

		public void initListeners() {
			mSensorManager.registerListener(this,
					mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_FASTEST);
			mSensorManager.registerListener(this,
					mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
					SensorManager.SENSOR_DELAY_FASTEST);
		}

		//----------------- GPS Listener functions--------------------
			public void initLocationListener() {
				locationManager = (LocationManager) this
						.getSystemService(Context.LOCATION_SERVICE);
				locationListener = new LocationListener() {

					@Override
					public void onLocationChanged(Location location) {
						nowLoc[0] = location.getLatitude();
						nowLoc[1] = location.getLongitude();
						gpsAcc = location.getAccuracy();
					}

					@Override
					public void onProviderDisabled(String arg0) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onProviderEnabled(String arg0) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
						// TODO Auto-generated method stub
					}
				};
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
						0, locationListener);
			}
			
		//------------------------------------------------------------



}
