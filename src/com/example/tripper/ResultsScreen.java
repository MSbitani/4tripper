package com.example.tripper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ResultsScreen extends Fragment {

	OnMain activityListener;
	MapView mapView;
	GoogleMap map;

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		activityListener = (OnMain) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.map, container,
				false);
		
		JSONArray results = activityListener.getVenues();
		
		// Gets the MapView from the XML layout and creates it
		mapView = (MapView) view.findViewById(R.id.mapview);
		mapView.onCreate(savedInstanceState);
 
		// Gets to GoogleMap from the MapView and does initialization stuff
		map = mapView.getMap();
		map.getUiSettings().setMyLocationButtonEnabled(false);
		map.setMyLocationEnabled(true);
		
		int length = results.length();
		JSONObject venue;
		for (int i = 0; i < length; i++) {
			try {
				venue = results.getJSONObject(i);
				map.addMarker(new MarkerOptions()
					.position(new LatLng(venue.getJSONObject("location").getDouble("lat"), venue.getJSONObject("location").getDouble("lng")))
					.title(venue.getString("name"))
				);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
 
		MapsInitializer.initialize(this.getActivity());
		
		//Zoom in on result location
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(activityListener.getResultLocation(), 10);
		map.animateCamera(cameraUpdate);

		return view;
	}
	
	@Override
	public void onResume() {
		mapView.onResume();
		super.onResume();
	}
 
	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
 
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	}
}
