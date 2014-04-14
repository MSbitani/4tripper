package com.example.tripper;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import fi.foyt.foursquare.api.entities.CompactVenue;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ResultsScreen extends Fragment implements OnCommunicateWithResultsScreen{

	OnCommunicateWithMainActivity activityListener;
	MapView mapView;
	GoogleMap map;

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		activityListener = (MainActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.map, container,
				false);
		
		CompactVenue[] results = activityListener.getVenues();
		
		// Gets the MapView from the XML layout and creates it
		mapView = (MapView) view.findViewById(R.id.mapview);
		mapView.onCreate(savedInstanceState);
 
		// Gets to GoogleMap from the MapView and does initialization stuff
		map = mapView.getMap();
		map.getUiSettings().setMyLocationButtonEnabled(false);
		map.setMyLocationEnabled(true);
		
		for (CompactVenue v : results) {
			map.addMarker(new MarkerOptions()
					.position(new LatLng(v.getLocation().getLat(), v.getLocation().getLng()))
					.title(v.getName())
			);
		}
 
		MapsInitializer.initialize(this.getActivity());
		
		//For each...map.addMarker
		
//		Get the latlng of where we'll be searching near
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(37.228376, -80.407246), 10);
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
