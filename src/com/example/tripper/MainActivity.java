package com.example.tripper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

	private LocationClient locationClient;
	private Location location;
	private String key = "AIzaSyAF6wW8hogpzGNl_3qr1VNbMNl3OiT1yJg";

	private final String fsqAPI = "20140426";
	private String clientID = "Y2N2JHAXEUORBDJZ3V31HJ5M03MQQO3FI1LTW2SK0QDWPXXN";
	private String clientSecret = "1L2MNIVGMJ2U4ATAP3EHZQHCJHBVW0HMOWX2ND2OYQIHWPHV";
	private JSONObject venues;

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

		String fsqURL = "https://api.foursquare.com/v2/venues/search?ll="
				+ location.getLatitude() + "," + location.getLongitude()
				+ "&intent=browse&radius=" + radius + "&v=" + fsqAPI
				+ "&client_id=" + clientID + "&client_secret=" + clientSecret;

		String mapURL = "https://maps.googleapis.com/maps/api/directions/json?origin="
				+ location.getLatitude()
				+ ","
				+ location.getLongitude()
				+ "&destination="
				+ address.replace(' ', '+')
				+ "&sensor=true&key=" + key;

		URL fsqsURL = null;
		URL mapsURL = null;

		try {
			fsqsURL = new URL(fsqURL);
			mapsURL = new URL(mapURL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		new OnNetwork().execute(fsqsURL, mapsURL);

	}

	private class OnNetwork extends AsyncTask<URL, Void, JSONObject[]> {

		protected JSONObject[] doInBackground(URL... urls) {

			JSONObject[] json = new JSONObject[urls.length];
			for (int i = 0; i < urls.length; i++)
				try {
					URLConnection uc = urls[i].openConnection();
					BufferedReader in = new BufferedReader(
							new InputStreamReader(uc.getInputStream()));
					String inputLine = "";
					String nextLine = "";
					while ((nextLine = in.readLine()) != null)
						inputLine += nextLine;
					json[i] = (JSONObject) new JSONTokener(inputLine)
							.nextValue();
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			return json;
		}

		protected void onPostExecute(JSONObject[] results) {
			venues = results[0];

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
			if (venues.getJSONObject("meta").getInt("code") == 200) {
				JSONArray v = venues.getJSONObject("response").getJSONArray(
						"venues");
				results = new String[v.length()];
				for (int i = 0; i < v.length(); i++)
					results[i] = v.getJSONObject(i).getString("name");
			} else {
				results[0] = "Error searching";
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, results);
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
