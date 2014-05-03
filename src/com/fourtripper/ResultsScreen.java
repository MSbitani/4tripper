package com.fourtripper;

import java.io.File;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.fourtripper.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ResultsScreen extends Fragment {

	private OnMain activityListener;
	private MapView mapView;
	private GoogleMap map;
	private LatLngBounds.Builder bounds;
	private ClusterManager<Venue> cm;
	private JSONArray venues;

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		setRetainInstance(true);

		venues = activityListener.getVenues();
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

		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				LatLng loc = marker.getPosition();
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
						Uri.parse(String.format(
								"http://maps.google.com/maps?daddr=%s,%s",
								loc.latitude, loc.longitude)));
				startActivity(intent);
			}
		});

		MapsInitializer.initialize(this.getActivity());

		bounds = new LatLngBounds.Builder();

		for (int i = 0; i < venues.length(); i++) {
			try {
				JSONObject venue = venues.getJSONObject(i);
				String category = venue.getJSONArray("categories")
						.getJSONObject(0).getString("name");

				LatLng position = new LatLng(venue.getJSONObject("location")
						.getDouble("lat"), venue.getJSONObject("location")
						.getDouble("lng"));
				bounds.include(position);

				File file = new File(getActivity().getCacheDir(), venue
						.getJSONArray("categories").getJSONObject(0)
						.getString("id")
						+ ".png");
				BitmapDescriptor image = BitmapDescriptorFactory.fromPath(file
						.getAbsolutePath());

				cm.addItem(new Venue(new MarkerOptions()
						.title(venue.getString("name")).snippet(category)
						.position(position).icon(image)));

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		// Zoom in on result location
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.build()
				.getCenter(), 10));
		map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				map.animateCamera(CameraUpdateFactory.newLatLngBounds(
						bounds.build(), 50));
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
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
			marker.setSnippet(options.getSnippet());
			marker.setIcon(options.getIcon());
		}
	}
}
