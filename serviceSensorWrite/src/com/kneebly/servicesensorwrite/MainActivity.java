package com.kneebly.servicesensorwrite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	Intent mServiceIntent; //no need to start intent services
//	SensorService mService;//change to sensor service
	boolean mBound=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//1-should this go in toggle or in onCreate?
		//no need to start IntentServices
//		    	mServiceIntent = new Intent(this, SensorService.class);
//		    	startService(mServiceIntent);
		//1- bind service instead of starting it?
		/*		If you want your activity to receive responses even while it 
		is stopped in the background, then you can bind during onCreate() 
		and unbind during onDestroy(). */
		    	//bound service unnecessary
//		Intent intent = new Intent(this, BoundSensorService.class);//or sensor service
//		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}
	@Override
	protected void onDestroy(){
		super.onDestroy();
//		if (mBound) {
//			unbindService(mConnection);
//			mBound = false;
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	public void onToggleClicked(View view) {
		// Is the toggle on?
		boolean on = ((ToggleButton) view).isChecked();
		mServiceIntent= new Intent(this,SensorService.class);

		if (on) {
			//1- could put service start here?
//			mService.startWriting();
			//Send a intent to SensorService to start
			mServiceIntent.putExtra("com.kneebly.servicesensorwrite.ACTION","Start");
			// Start recording
		} else {
			mServiceIntent.putExtra("com.kneebly.servicesensorwrite.ACTION","Stop");

			// Stop recording
		}
		startService(mServiceIntent);
	}
	
	//For bound service- unnecessary.
//	private ServiceConnection mConnection = new ServiceConnection() {
//		// Called when the connection with the service is established
//		public void onServiceConnected(ComponentName className, IBinder service) {
//			// Because we have bound to an explicit
//			// service that is running in our own process, we can
//			// cast its IBinder to a concrete class and directly access it.
//			LocalBBinder binder = (LocalBBinder) service; //LocalBinder for IntentService
//			mService = binder.getService();
//			mBound = true;
//		}
//		// Called when the connection with the service disconnects unexpectedly
//		public void onServiceDisconnected(ComponentName className) {
//			Log.d("kneebler", "onServiceDisconnected");
//			mBound = false;
//		}
//	};
	
	public void startRecording(){

	}

}
