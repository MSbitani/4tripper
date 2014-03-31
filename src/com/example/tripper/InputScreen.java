package com.example.tripper;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class InputScreen extends Fragment implements OnCommunicateWithInputScreen{

	private EditText address;
	private Button gobutton;

	OnCommunicateWithMainActivity activityListener;

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

		View view = inflater.inflate(R.layout.inputlayout, container,
				false);

		address = (EditText)view.findViewById(R.id.editText1);
		gobutton = (Button)view.findViewById(R.id.button1);

		gobutton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(!getAddress().equals("")){
					activityListener.onGoButtonPressed(getAddress());
				}
			}

		});

		return view;
	}

	public String getAddress() {
		return address.getText().toString();
	}
}
