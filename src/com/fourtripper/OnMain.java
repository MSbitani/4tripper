package com.fourtripper;

import org.json.JSONArray;

import com.google.android.gms.maps.model.LatLng;

import android.widget.ArrayAdapter;

public interface OnMain {

	public void onSubmitPressed(String address, int itime, int iradius);

	public ArrayAdapter<String> getVenueResults();
	public LatLng getResultLocation();
	public JSONArray getVenues();
}
