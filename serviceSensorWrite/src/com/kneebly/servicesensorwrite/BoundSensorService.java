package com.kneebly.servicesensorwrite;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BoundSensorService extends Service implements SensorEventListener {
	private final IBinder mBinder = new LocalBinder();
	private SensorManager mSensorManager = null;
	// accelerometer vectors
	public float[] accel = { 0.0f, 0.0f, 0.0f };
	public float[] grav= { 0.0f, 0.0f, 0.0f };
	//Timer timeUpdateAc;
	private Handler handler = new Handler();
	private final long DELAY = 2000;
	private final long FREQUENCY=100;
	DecimalFormat d = new DecimalFormat("#.##");
	private LocationManager locationManager;
	private LocationListener locationListener;
	private double[] nowLoc = { 0.0d, 0.0d };
	private float gpsAcc = 0;
	BufferedWriter buf;
	long startTime;//time service started recording

	@Override
	public void onCreate() {
		super.onCreate();
		mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		initListeners();
		initLocationListener();
	}	
	@Override
	public void onDestroy(){
		super.onDestroy();
		mSensorManager.unregisterListener(this);
		locationManager.removeUpdates(locationListener);
		locationManager=null;
		Log.d("kneebler","set locationManager to null, no longer listening");

	}

	public void startWriting(){
		startTime=System.currentTimeMillis();
		String fileName=Long.toString(startTime)+".csv";
		String filePath =Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DOWNLOADS)+"/kneebly";
		File directory= new File(filePath);
		File file = new File(filePath,fileName);
		directory.mkdirs();//make directory if it's not there already
		Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_LONG).show();

		try {
			buf = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
			//	writeTimer.scheduleAtFixedRate(new CountTask(), DELAY,
			//				FREQUENCY); //not using timers anymore
			handler.postDelayed(runnable,DELAY); //set up a repeating task that 
			// writes values to the file
			buf.write("Time (ms),GPS lat,GPS long,GPS Accuracy,Accel(x)," +
					"Accel(y),Accel(z),grav(x),grav(y),grav(z)");


		} catch (IOException e) {
			Log.w("kneebler", "Error writing  to external storage" + filePath, e);

		}

	}
	public String testBind(){
		return "Bound";
	}
	public void stopWriting(){

		try {
			handler.removeCallbacks(runnable);
			buf.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String sentTime(){
		return Long.toString(System.currentTimeMillis());
	}

	//------- Task that writes updates to file -------------------
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			try {
				String nowTime=Long.toString(System.currentTimeMillis()-startTime);
				Log.d("kneebler","Wrote at "+nowTime);
				//string format:"Time (ms),GPS lat,GPS long,GPS Accuracy,Accel(x)," 
				//"Accel(y),Accel(z),grav(x),grav(y),grav(z)"
				String thisLine=nowTime+ ","+ Double.toString(nowLoc[0])+","
				+ Double.toString(nowLoc[1])+","+ Float.toString(gpsAcc)+","
						+ Float.toString(accel[0])+","+ Float.toString(accel[1])
						+","+ Float.toString(accel[2])+","+ Float.toString(grav[0])+","
						+ Float.toString(grav[1])+","+ Float.toString(grav[2]);

				buf.newLine();
				buf.write(thisLine);
				
			}
			catch (IOException e){

				// TODO Auto-generated catch block
				e.printStackTrace();
			}		      handler.postDelayed(this, FREQUENCY);
		}
	};

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
	//----------- Binding classes --------------------------------------
	/**
	 * Class used for the client Binder.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		BoundSensorService getService() {
			// Return this instance of LocalService so clients can call public methods
			return BoundSensorService.this;
		}
	}


	@Override
	public IBinder onBind(Intent intent) {
		Log.d("kneebler","called onBind");
		return mBinder;
	}

	//------------------------------------------------------------

}
