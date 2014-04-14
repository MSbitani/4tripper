package com.example.tripper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.PointF;

public class JSONParser
{

	/**
	 * Takes BufferedReader object and returns resulting JSON object
	 * 
	 * @param br
	 *            BufferedReader to interpret
	 * @return JSON response
	 * @throws IOException
	 * @throws JSONException
	 */
	public static JSONObject readerToJSONObject(BufferedReader br) throws IOException,
			JSONException
	{
		StringBuilder sb = new StringBuilder();
		String inputLine;

		while ((inputLine = br.readLine()) != null)
			sb.append(inputLine + "\n");
		br.close();

		return new JSONObject(sb.toString());
	}

	/**
	 * Will parse through a JSON object and extract all GeoLocation tags that
	 * fit within a given range of minutes away
	 * 
	 * @param json
	 *            JSON object returned by Google Maps to be processed
	 * @param timeGiven
	 *            "x" time away to be median of range
	 * @param range
	 *            amount of minutes to plus/minus from timeGiven
	 * @return ArrayList of PointF objects with y as latitude and x as longitude
	 * @throws JSONException
	 */
	public static ArrayList<PointF> getGeoPointsInTimeRange(JSONObject json, int timeGiven,
			int range) throws JSONException
	{
		ArrayList<PointF> geoTags = new ArrayList<PointF>();

		int MAX_SEC = (timeGiven + range) * 60;
		int MIN_SEC = (timeGiven - range) * 60;

		int secTaken = 0;
		JSONObject routes = json.getJSONArray("routes").getJSONObject(0);
		JSONObject legs = routes.getJSONArray("legs").getJSONObject(0);
		JSONArray steps = legs.getJSONArray("steps");
		for (int i = 0; i < steps.length(); i++)
		{
			JSONObject curStep = steps.getJSONObject(i);
			if (secTaken >= MIN_SEC)
			{
				JSONObject endLoc = curStep.getJSONObject("start_location");
				PointF p = new PointF();
				// converting double to float will lose precision and range...
				// but not much else we can do
				p.x = (float) endLoc.getDouble("lng");
				p.y = (float) endLoc.getDouble("lat");
				geoTags.add(p);
			}
			// adjust counter to see if in range
			secTaken += curStep.getJSONObject("duration").getInt("value");
			if (secTaken >= MAX_SEC)
				break;
		}
		return geoTags;
	}
}
