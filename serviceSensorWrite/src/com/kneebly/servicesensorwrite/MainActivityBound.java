package com.kneebly.servicesensorwrite;

import java.io.IOException;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.kneebly.servicesensorwrite.BoundSensorService.LocalBinder;

public class MainActivityBound extends Activity {
	BoundSensorService mService;
	boolean mBound = false;
	Intent startRecordIntent;
	TextView tester;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tester=(TextView) findViewById(R.id.textTester);
		tester.setText("Toggle to start recording accelerometer and GPS data");
		intendToRecord();
	}
	protected void onStart() {
		super.onStart();
	}

	protected void onDestroy() {

		super.onDestroy();

		// Unbind from the service
		if (mBound) { 
			unbindService(mConnection);
			mBound = false;
		}
		//kill the service
		Intent killIntent=new Intent(this, BoundSensorService.class);
		this.stopService(killIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onToggleClicked(View view) throws IOException {
		boolean on = ((ToggleButton) view).isChecked();
		if (on) {
//			tester.setText("totally on");// debugging-if not bound but toggle clicked
			mService.startWriting();
			if (mBound){
//				tester.setText(mService.sentTime());- debugging
			}
			else
			{
				Log.d("kneebler","not bound");
			}
		}
		else {

			mService.stopWriting();
//			tester.setText("No more time :("); //debugging- indicates that the file is closed

		}
	}
//---- BINDING code --------------
	public void intendToRecord() {
		startRecordIntent = new Intent(this, BoundSensorService.class);
		this.bindService(startRecordIntent, mConnection,
				Context.BIND_AUTO_CREATE);

	}

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className,
				IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

}
