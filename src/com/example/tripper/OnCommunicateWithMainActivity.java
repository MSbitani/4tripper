package com.example.tripper;

import android.widget.ArrayAdapter;

public interface OnCommunicateWithMainActivity {

	public void onGoButtonPressed(String address);
	public ArrayAdapter<String> getVenueResults();
}
