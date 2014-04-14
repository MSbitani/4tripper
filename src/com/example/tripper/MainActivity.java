package com.example.tripper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.widget.ArrayAdapter;

public class MainActivity extends Activity implements OnCommunicateWithMainActivity, LocationListener{

	public final static String FRAG1_TAG = "FRAG1";
	public final static String FRAG2_TAG = "FRAG2";

	private InputScreen inputscreen;
	private ResultsScreen resultsscreen;
	private LocationManager locationManager;
	private String provider;
	FoursquareApi foursquareApi;
	String latlong;
	Result<VenuesSearchResult> result;

	float lat;
	float lng;

	private URL mapsURL;

	OnCommunicateWithInputScreen inputlistener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		foursquareApi = new FoursquareApi("T5PPRZTOW4IHLNU20F20UTZHNR13KSGW53FPP0IVW5MLELKI", "XFKUY021ADWBG1DZQAMGTZTJXH5IWLVZ1SORHLGEHMBMAWWD", "https://github.com/cjmenzel/4tripper");

		inputscreen = new InputScreen();
		LocationManager service = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean enabled = service
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!enabled) {
			// creates new activity with settings for GPS
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();

		provider = locationManager.getBestProvider(criteria, false);

		Location location = locationManager.getLastKnownLocation(provider);

		lat = (float) (location.getLatitude());
		lng = (float) (location.getLongitude());

		getFragmentManager().beginTransaction()
		.add(R.id.fragment1, inputscreen, FRAG1_TAG).commit();

		inputlistener = (OnCommunicateWithInputScreen) inputscreen;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onGoButtonPressed(String address) {
		mapsURL = null;
		try {
			mapsURL = new URL(formatAddress(address));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		new OnNetwork().execute(mapsURL);
	}

	private String formatAddress(String address) {
		String newAddress = null;
		mapsURL = null;

		newAddress = address.replace(' ', '+');
		System.out.println(newAddress);
		System.out.println(lat);
		System.out.println(lng);

		latlong = Float.toString(lat) + "," + Float.toString(lng);
		System.out.println(latlong);

		return "https://maps.googleapis.com/maps/api/directions/json?origin="+lat+","+lng+"&destination="+newAddress+"&sensor=true&key=AIzaSyAF6wW8hogpzGNl_3qr1VNbMNl3OiT1yJg";
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	private class OnNetwork extends AsyncTask<URL, Void, String> {

		protected void onPostExecute(String result) {

			System.out.println("in onpostexecute");
			//THIS IS WHERE I WOULD PARSE A JSON
			//			JsonParserFactory factory = JsonParserFactory.getInstance();
			//			JSONParser parser = factory.newJsonParser();
			//			@SuppressWarnings("unchecked")
			//			Map<String, String> jsonData = parser.parseJson(result);
			//			String value = (String) jsonData.get("routes");
			//			System.out.println(value);


			FragmentManager fragMgr = getFragmentManager();
			FragmentTransaction xact = fragMgr.beginTransaction();

			if (fragMgr.findFragmentByTag(FRAG2_TAG) == null) {
				xact.remove(inputscreen);
				resultsscreen = new ResultsScreen();
				fragMgr.executePendingTransactions();

				xact = fragMgr.beginTransaction();

				xact.replace(R.id.fragment1, resultsscreen, FRAG2_TAG);
			}
			xact.addToBackStack(null);
			xact.commit();
		}

		protected String doInBackground(URL... params) {

			URL url = params[0];
			String json = "";
			try {
				//FOURSQUARE STUFF
				result = foursquareApi.venuesSearch(latlong, null, null, null, null, null, null, null, null, null, null);
			} catch (FoursquareApiException e) {
				e.printStackTrace();
			}
			try {

				URLConnection uc = url.openConnection();
				BufferedReader in = new BufferedReader(
						new InputStreamReader(
								uc.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					json = json.concat(inputLine);
				}
				in.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(json);
			return json;
		}
	}

	public ArrayAdapter<String> getVenueResults() {
		if (result.getMeta().getCode() == 200) {
			String[] str = new String[result.getResult().getVenues().length];
			for (int i = 0; i < result.getResult().getVenues().length; i++){
				str[i] = result.getResult().getVenues()[i].getName();
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, str);

			return adapter;
		} else {
			return null;
		}
	}
	
	public CompactVenue[] getVenues() {
		return result.getResult().getVenues();
	}
}
