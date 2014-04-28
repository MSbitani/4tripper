package com.example.tripper;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ResultsScreen extends Fragment {

	OnMain activityListener;

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

		View view = inflater.inflate(R.layout.results_layout, container, false);

		ArrayAdapter<String> adapter = activityListener.getVenueResults();

		ListView listView = (ListView) view.findViewById(R.id.list);
		// Assign adapter to ListView
		listView.setAdapter(adapter);

		// ListView Item Click Listener
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Do something
			}

		});

		return view;
	}
}
