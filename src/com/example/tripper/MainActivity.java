package com.example.tripper;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity implements OnCommunicateWithMainActivity{

	public final static String FRAG1_TAG = "FRAG1";
	
	private InputScreen inputscreen;
	
	OnCommunicateWithInputScreen inputlistener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		inputscreen = new InputScreen();
		
		getFragmentManager().beginTransaction()
		.add(R.id.fragment1, inputscreen, FRAG1_TAG).commit();
		
		inputlistener = (OnCommunicateWithInputScreen) inputscreen;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onGoButtonPressed(String address) {
		//TODO Send this address to google maps
		System.out.println(address);
		
	}

}
