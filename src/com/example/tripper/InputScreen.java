package com.example.tripper;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

public class InputScreen extends Fragment {

	EditText address;
	NumberPicker timepicker;
	NumberPicker distancepicker;
	Button submitbutton;

	OnMain blistener;

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		setRetainInstance(true);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		blistener = (OnMain) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.input_layout, container, false);

		LinearLayout content = (LinearLayout) view.findViewById(R.id.content);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			content.setOrientation(LinearLayout.VERTICAL);
			content.setDividerDrawable(getResources().getDrawable(
					R.drawable.vdivider));
		} else {
			content.setOrientation(LinearLayout.HORIZONTAL);
			content.setDividerDrawable(getResources().getDrawable(
					R.drawable.hdivider));
		}

		address = (EditText) view.findViewById(R.id.destination);
		timepicker = (NumberPicker) view.findViewById(R.id.timepicker);
		distancepicker = (NumberPicker) view.findViewById(R.id.distancepicker);
		submitbutton = (Button) view.findViewById(R.id.submit);

		timepicker.setMinValue(0);
		timepicker.setMaxValue(12);
		timepicker.setDisplayedValues(new String[] { "0", "5", "10", "15",
				"20", "25", "30", "35", "40", "45", "50", "55", "60" });

		distancepicker.setMinValue(1);
		distancepicker.setMaxValue(5);

		submitbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String search = address.getText().toString();
				int time = timepicker.getValue() * 300;
				int radius = (int) (distancepicker.getValue() * 1609.344);

				if (!search.equals("")) {
					blistener.onSubmitPressed(search, time, radius);
					((InputMethodManager) getActivity().getSystemService(
							Context.INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(address.getWindowToken(),
									0);
				}
			}
		});

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
}
