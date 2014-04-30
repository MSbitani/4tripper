package com.example.tripper;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ResultsScreen extends Fragment {

	private OnMain activityListener;
	private MapView mapView;
	private GoogleMap map;
	private ClusterManager<Venue> cm;

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

		View view = inflater.inflate(R.layout.map, container, false);

		JSONArray venues = activityListener.getVenues();

		// Gets the MapView from the XML layout and creates it
		mapView = (MapView) view.findViewById(R.id.mapview);
		mapView.onCreate(savedInstanceState);

		// Gets to GoogleMap from the MapView and does initialization stuff
		map = mapView.getMap();
		map.getUiSettings().setMyLocationButtonEnabled(false);
		map.setMyLocationEnabled(true);

		cm = new ClusterManager<Venue>(getActivity(), map);
		cm.setRenderer(new ClusterRenderer(getActivity(), map, cm));
		map.setOnCameraChangeListener(cm);
		map.setOnMarkerClickListener(cm);

		MapsInitializer.initialize(this.getActivity());

		LatLngBounds.Builder bounds = new LatLngBounds.Builder();

		for (int i = 0; i < venues.length(); i++) {
			try {
				JSONObject venue = venues.getJSONObject(i);
				String category = venue.getJSONArray("categories")
						.getJSONObject(0).getString("name");

				HashMap<String, Float> colors = new HashMap<String, Float>();
				if (!colors.containsKey(category))
					colors.put(category, (float) (Math.random() * 360));

				LatLng position = new LatLng(venue.getJSONObject("location")
						.getDouble("lat"), venue.getJSONObject("location")
						.getDouble("lng"));
				bounds.include(position);

				cm.addItem(new Venue(new MarkerOptions()
						.title(venue.getString("name"))
						.position(position)
						.icon(BitmapDescriptorFactory.defaultMarker(colors
								.get(category)))));

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		// Zoom in on result location
		map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(),
				container.getWidth(), container.getHeight(), 50));

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

	public class Venue implements ClusterItem {
		private final MarkerOptions mOptions;

		public Venue(MarkerOptions options) {
			mOptions = options;
		}

		@Override
		public LatLng getPosition() {
			return mOptions.getPosition();
		}

		public MarkerOptions getOptions() {
			return mOptions;
		}
	}

	class ClusterRenderer extends DefaultClusterRenderer<Venue> {

		public ClusterRenderer(Context context, GoogleMap map,
				ClusterManager<Venue> clusterManager) {
			super(context, map, clusterManager);
		}

		@Override
		protected void onClusterItemRendered(Venue item, Marker marker) {
			super.onClusterItemRendered(item, marker);

			MarkerOptions options = item.getOptions();
			marker.setTitle(options.getTitle());
			marker.setIcon(options.getIcon());
		}
	}
}
