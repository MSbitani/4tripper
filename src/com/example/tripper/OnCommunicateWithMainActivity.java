package com.example.tripper;

import fi.foyt.foursquare.api.entities.CompactVenue;
import android.widget.ArrayAdapter;

public interface OnCommunicateWithMainActivity {

	public void onGoButtonPressed(String address);
	public ArrayAdapter<String> getVenueResults();
	public CompactVenue[] getVenues();
}
