package com.example.tripper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.ArrayAdapter;

public class MainActivity extends Activity implements OnMain,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	public final static String FRAG1_TAG = "FRAG1";
	public final static String FRAG2_TAG = "FRAG2";

	private InputScreen inputscreen;
	private ResultsScreen resultsscreen;

	private int time;
	private int radius;

	private ProgressDialog loadingDialog;

	private String key = "AIzaSyAF6wW8hogpzGNl_3qr1VNbMNl3OiT1yJg";
	private JSONArray steps;
	private LocationClient locationClient;
	private Location location;
	private LatLng resultLocation;

	private final String fsqAPI = "20140426";
	private String clientID = "Y2N2JHAXEUORBDJZ3V31HJ5M03MQQO3FI1LTW2SK0QDWPXXN";
	private String clientSecret = "1L2MNIVGMJ2U4ATAP3EHZQHCJHBVW0HMOWX2ND2OYQIHWPHV";
	private JSONArray venues;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		locationClient = new LocationClient(this, this, this);

		if (getFragmentManager().findFragmentByTag(FRAG1_TAG) == null) {
			inputscreen = new InputScreen();
			getFragmentManager().beginTransaction()
					.add(R.id.frame, inputscreen, FRAG1_TAG).commit();
		}

		if (savedInstanceState != null) {
			inputscreen = (InputScreen) getFragmentManager().findFragmentByTag(
					FRAG1_TAG);
			resultsscreen = (ResultsScreen) getFragmentManager()
					.findFragmentByTag(FRAG2_TAG);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		locationClient.connect();
	}

	@Override
	protected void onStop() {
		locationClient.disconnect();
		super.onStop();
	}

	public void onSubmitPressed(String address, int itime, int iradius) {
		location = locationClient.getLastLocation();
		time = itime;
		radius = iradius;

		URL mapURL = null;

		try {
			mapURL = new URL(
					"https://maps.googleapis.com/maps/api/directions/json?origin="
							+ location.getLatitude() + ","
							+ location.getLongitude() + "&destination="
							+ address.replace(' ', '+') + "&sensor=true&key="
							+ key);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		new MapLink(this).execute(mapURL);

	}

	private class MapLink extends AsyncTask<URL, Void, JSONObject> {

		public MapLink(Context c) {
			loadingDialog = ProgressDialog.show(c, "Loading...",
					"We're calculating your anticipated location!");
		}

		protected JSONObject doInBackground(URL... urls) {

			JSONObject json = new JSONObject();
			try {
				URLConnection uc = urls[0].openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(
						uc.getInputStream()));
				String inputLine = "";
				String nextLine = "";
				while ((nextLine = in.readLine()) != null)
					inputLine += nextLine;
				json = (JSONObject) new JSONTokener(inputLine).nextValue();
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			return json;
		}

		@SuppressWarnings("unchecked")
		protected void onPostExecute(JSONObject result) {
			try {
				if (!result.getString("status").equals("OK"))
					return;

				steps = result.getJSONArray("routes").getJSONObject(0)
						.getJSONArray("legs").getJSONObject(0)
						.getJSONArray("steps");

				int i, elapsed = 0;
				for (i = 0; i < steps.length(); i++) {
					elapsed += steps.getJSONObject(i).getJSONObject("duration")
							.getInt("value");
					if (elapsed >= time)
						break;
				}

				if (i == steps.length())
					i--;

				List<LatLng> path = PolyUtil.decode(steps.getJSONObject(i)
						.getJSONObject("polyline").getString("points"));

				new SpotLink().execute(path);

			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}
		}
	}

	private class SpotLink extends AsyncTask<List<LatLng>, Void, LatLng> {

		protected LatLng doInBackground(List<LatLng>... paths) {
			int begin = 0;
			int end = paths[0].size() - 1;
			int mid = ((end - begin) / 2);

			URL mapURL = null;

			while (begin < end) {
				mid = ((end - begin) / 2) + begin;
				try {
					mapURL = new URL(
							"https://maps.googleapis.com/maps/api/directions/json?origin="
									+ location.getLatitude() + ","
									+ location.getLongitude() + "&destination="
									+ paths[0].get(mid).latitude + ","
									+ paths[0].get(mid).longitude
									+ "&sensor=true&key=" + key);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}

				JSONObject json = new JSONObject();
				try {
					URLConnection uc = mapURL.openConnection();
					BufferedReader in = new BufferedReader(
							new InputStreamReader(uc.getInputStream()));
					String inputLine = "";
					String nextLine = "";
					while ((nextLine = in.readLine()) != null)
						inputLine += nextLine;
					json = (JSONObject) new JSONTokener(inputLine).nextValue();
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}

				try {
					if (!json.getString("status").equals("OK"))
						return null;

					int duration = json.getJSONArray("routes").getJSONObject(0)
							.getJSONArray("legs").getJSONObject(0)
							.getJSONObject("duration").getInt("value");

					if (Math.abs(end - begin) <= 1)
						break;
					else if (duration > time)
						end = mid;
					else
						begin = mid;
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}
			}

			return paths[0].get(mid);
		}

		protected void onPostExecute(LatLng result) {

			resultLocation = result;

			String url = "https://api.foursquare.com/v2/venues/search?ll="
					+ result.latitude
					+ ","
					+ result.longitude
					+ "&limit=50&intent=browse&radius="
					+ radius
					+ "&categoryId=4d4b7105d754a06374d81259,4bf58dd8d48988d113951735,4bf58dd8d48988d1fa931735"
					+ "&client_id=" + clientID + "&client_secret="
					+ clientSecret + "&v=" + fsqAPI;

			URL fsqURL = null;

			try {
				fsqURL = new URL(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			new FSQLink().execute(fsqURL);
		}
	}

	private class FSQLink extends AsyncTask<URL, Void, JSONObject> {

		protected JSONObject doInBackground(URL... urls) {

			JSONObject json = new JSONObject();
			try {
				URLConnection uc = urls[0].openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(
						uc.getInputStream()));
				json = (JSONObject) new JSONTokener(in.readLine()).nextValue();
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			return json;
		}

		protected void onPostExecute(JSONObject result) {
			try {
				if (result.getJSONObject("meta").getInt("code") != 200)
					return;

				venues = result.getJSONObject("response")
						.getJSONArray("venues");
			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}

			loadingDialog.hide();

			FragmentManager fragMgr = getFragmentManager();
			FragmentTransaction xact = fragMgr.beginTransaction();

			if (fragMgr.findFragmentByTag(FRAG2_TAG) == null)
				resultsscreen = new ResultsScreen();

			xact.replace(R.id.frame, resultsscreen, FRAG2_TAG);
			xact.addToBackStack(null);
			xact.commit();
		}
	}

	public ArrayAdapter<String> getVenueResults() {
		String[] results = new String[] { "No results found" };

		try {
			results = new String[venues.length()];
			for (int i = 0; i < venues.length(); i++)
				results[i] = venues.getJSONObject(i).getString("name");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, results);
	}

	public LatLng getResultLocation() {
		return resultLocation;
	}

	public JSONArray getVenues() {
		return venues;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

}
