package com.example.tripper;

import fi.foyt.foursquare.api.entities.CompactVenue;
import android.widget.ArrayAdapter;

public interface OnMain {

	public void onSubmitPressed(String address, int itime, int iradius);

	public ArrayAdapter<String> getVenueResults();
	public CompactVenue[] getVenues();
}
